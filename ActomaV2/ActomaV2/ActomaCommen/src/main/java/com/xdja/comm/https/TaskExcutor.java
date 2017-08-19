package com.xdja.comm.https;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by THZ on 2015/7/16.
 * 异步的线程池
 */
public class TaskExcutor {

    /**
     * 初始化大小
     */
    private static final int INITIAL_POOL_SIZE = 3;

    /**
     * 总的线程大小
     */
    private static final int MAX_POOL_SIZE = 5;

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 10;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    /**
     * 线程池
     */
    private static ThreadPoolExecutor executor;

    /**
     *
     */
    private static TaskExcutor taskExcutor;

    /**
     * LinkedBlockingQueue
     */
    private static LinkedBlockingQueue workQueue;
    /**
     * 单例实例化
     * @return 对象
     */
    public synchronized static TaskExcutor getInstance() {
        if (taskExcutor == null) {
            LinkedBlockingQueue workQueue = new LinkedBlockingQueue<>();// modified by ycm for lint 2017/02/13
            executor = new ThreadPoolExecutor(INITIAL_POOL_SIZE, MAX_POOL_SIZE,KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, workQueue);
            taskExcutor = new TaskExcutor();
        }
        return taskExcutor;
    }

    /**
     * 获取线程池
     * @return ThreadPoolExecutor
     */
    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

}
