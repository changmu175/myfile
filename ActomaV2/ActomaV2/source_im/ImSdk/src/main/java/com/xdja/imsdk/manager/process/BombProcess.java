package com.xdja.imsdk.manager.process;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;

import com.xdja.imsdk.manager.callback.BombCallback;
import com.xdja.imsdk.model.IMMessage;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 项目名称：ImSdk                                        <br>
 * 类描述：                                               <br>
 * 创建时间：2016/11/27 下午6:50                              <br>
 * 修改记录：                                             <br>
 *
 * @author liming@xdja.com                               <br>
 * @version V1.1.7                                       <br>
 */

public class BombProcess {
    private BombCallback bombCallback;
    private HandlerThread bombThread;
    private Looper bombLooper;
    private Handler handler;
    private final LinkedBlockingQueue<BombNode> bombQueue = new LinkedBlockingQueue<>();// 闪信处理队列

    /**
     * 构造
     * @param bombCallback 闪信回调处理
     */
    public BombProcess(BombCallback bombCallback) {
        this.bombCallback = bombCallback;
        this.bombThread = new HandlerThread("BombProcess", Process.THREAD_PRIORITY_BACKGROUND);
        this.bombThread.start();
        this.bombLooper = bombThread.getLooper();
        this.handler = new Handler(bombLooper);
    }

    /**
     * 添加闪信到待处理队列
     * @param message 闪信
     */
    public void add(IMMessage message) {
        BombNode node = new BombNode(message, SystemClock.elapsedRealtime(), message.getTimeToLive());
        synchronized (bombQueue) {
            bombQueue.offer(node);
            if (bombQueue.size() == 1) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, node.getDelayTime());
            }
        }
    }

    /**
     * 移除闪信队列中所有元素
     */
    public void removeAll() {
        synchronized (bombQueue) {
            bombQueue.clear();
        }
    }

    /**
     * 移除闪信队列中指定的闪信 TODO:可能存在效率问题，需要写测试用例
     * @param ids 需要移除的闪信
     */
    public void remove(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }


        synchronized (bombQueue) {
            BombNode[] nodes = new BombNode[bombQueue.size()];
            nodes = bombQueue.toArray(nodes);
            if (nodes.length > 0) {
                for (Long id : ids) {
                    for (BombNode node : nodes) {
                        if (id == node.getMessage().getIMMessageId()) {
                            bombQueue.remove(node);
                        }
                    }
                }
            }
        }
    }


    /**
     * 停止闪信处理
     */
    public void stop() {
        synchronized (bombQueue) {
            bombQueue.clear();
        }

        if (handler == null || bombThread == null) {
            return;
        }

        bombLooper.quit(); // TODO: 2016/12/9 liming 
        handler = null;
        bombThread.quit();
        bombThread.interrupt();
        bombThread = null;
    }

    /**
     * 闪信任务线程
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            BombNode node;
            synchronized (bombQueue) {
                node = bombQueue.poll();
            }

            if (node == null || node.getMessage() == null) {
                return;
            }

            bombCallback.BombDestroy(node.getMessage());// 删除闪信即消息状态变为已销毁。执行完成后需要回调
            refreshQueueWhenRemove();
        }
    };

    /**
     * 处理完成后刷新队列
     */
    private void refreshQueueWhenRemove() {
        synchronized (bombQueue) {
            long now = SystemClock.elapsedRealtime();
            BombNode[] nodes = new BombNode[bombQueue.size()];
            nodes = bombQueue.toArray(nodes);
            if (nodes.length > 0) {
                for (BombNode node : nodes) {
                    int delayTime = (int) (node.getWholeDelayTime() - (now - node.getInsertQueueTime()));
                    if(delayTime < 0) {
                        node.setDelayTime(0);
                    }
                }
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, nodes[0].getDelayTime());
            }
        }
    }
}
