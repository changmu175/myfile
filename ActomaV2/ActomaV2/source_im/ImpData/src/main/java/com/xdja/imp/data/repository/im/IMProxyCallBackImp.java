package com.xdja.imp.data.repository.im;

import android.content.Context;

import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.Scoped;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.repository.IMProxyCallBack;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * <p>Summary:向业务层回调的接口</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/19</p>
 * <p>Time:16:37</p>
 * 修改备注：
 * 1)Task for 2632, modify for share and forward function by ycm at 20161103.
 */
public class IMProxyCallBackImp implements IMProxyCallBack {

    private BusProvider busProvider;

    private Context context;


    private UserCache userCache;

    private int result;
    @Inject
    public IMProxyCallBackImp(@Scoped(value = DiConfig.CONTEXT_SCOPE_APP)
                              Context context,
                              BusProvider provider,
                              UserCache userCache) {

        this.busProvider = provider;
        this.context = context;
        this.userCache = userCache;
    }

    @Override
    public int onSendFileFaild(String talkId, long msgId, FileInfo fileInfo, int code) {

        try {
            //构建事件对象
            IMProxyEvent.SendFileFailedEvent fileEvent = new IMProxyEvent.SendFileFailedEvent();
            fileEvent.setFileInfo(fileInfo);
            fileEvent.setFileId(fileInfo.get_id());
            fileEvent.setAttachedMsgId(msgId);
            fileEvent.setAttachedTalkId(talkId);

            //打印事件对象
            //LogUtil.getUtils().d(fileEvent.toString());
            //发送事件
            this.busProvider.post(fileEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }

    }

    @Override
    public int onReceiveFileFaild(String talkId, long msgId, FileInfo fileInfo, int code) {
        try {
            //构建事件对象
            IMProxyEvent.ReceiveFileFailedEvent fileEvent = new IMProxyEvent.ReceiveFileFailedEvent();
            fileEvent.setFileInfo(fileInfo);
            fileEvent.setFileId(fileInfo.get_id());
            fileEvent.setAttachedMsgId(msgId);
            fileEvent.setAttachedTalkId(talkId);

            //打印事件对象
            //LogUtil.getUtils().d(fileEvent.toString());
            //发送事件
            this.busProvider.post(fileEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int onReceiveFilePaused(String talkId, long msgId, FileInfo fileInfo) {
        try{
            IMProxyEvent.ReceiveFilePaused fileEvent =
                    new IMProxyEvent.ReceiveFilePaused();
            fileEvent.setFileInfo(fileInfo);
            fileEvent.setFileId(fileInfo.get_id());
            fileEvent.setAttachedMsgId(msgId);
            fileEvent.setAttachedTalkId(talkId);
            this.busProvider.post(fileEvent);
            return ConstDef.CALLBACK_HANDLED;
        }catch (Exception ex){
            return  ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int onSendFileProgressUpdate(String talkId, long msgId,
                                        FileInfo fileInfo, int progress) {
        try {
            //构建事件对象
            IMProxyEvent.SendFileProgressUpdateEvent fileEvent
                    = new IMProxyEvent.SendFileProgressUpdateEvent();
            fileEvent.setFileInfo(fileInfo);
            fileEvent.setFileId(fileInfo.get_id());
            fileEvent.setAttachedMsgId(msgId);
            fileEvent.setAttachedTalkId(talkId);
            fileEvent.setPercent(progress);

            //打印事件对象
            //LogUtil.getUtils().d(fileEvent.toString());
            //发送事件
            this.busProvider.post(fileEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int onSendFileFinished(String talkId, long msgId, FileInfo fileInfo) {
        try {
            //构建事件对象
            IMProxyEvent.SendFileFinishedEvent fileEvent
                    = new IMProxyEvent.SendFileFinishedEvent();
            fileEvent.setFileInfo(fileInfo);
            fileEvent.setFileId(fileInfo.get_id());
            fileEvent.setAttachedMsgId(msgId);
            fileEvent.setAttachedTalkId(talkId);

            //打印事件对象
            //LogUtil.getUtils().d(fileEvent.toString());
            //发送事件
            this.busProvider.post(fileEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int onReceiveFileProgressUpdate(String talkId, long msgId,
                                          FileInfo fileInfo, int progress) {
        try {
            //构建事件对象
            IMProxyEvent.ReceiveFileProgressUpdateEvent fileEvent
                    = new IMProxyEvent.ReceiveFileProgressUpdateEvent();
            fileEvent.setFileInfo(fileInfo);
            fileEvent.setFileId(fileInfo.get_id());
            fileEvent.setAttachedMsgId(msgId);
            fileEvent.setAttachedTalkId(talkId);
            fileEvent.setPercent(progress);

            //打印事件对象
            //LogUtil.getUtils().d(fileEvent.toString());
            //发送事件
            this.busProvider.post(fileEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int onReceiveFileFinished(final String talkId,final long msgId,final  FileInfo fileInfo) {
        Observable.just("").observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                IMProxyEvent.ReceiveFileFinishedEvent fileEvent
                        = new IMProxyEvent.ReceiveFileFinishedEvent();
                fileEvent.setFileInfo(fileInfo);
                fileEvent.setFileId(fileInfo.get_id());
                fileEvent.setAttachedMsgId(msgId);
                fileEvent.setAttachedTalkId(talkId);
                busProvider.post(fileEvent);
            }
        });
        return ConstDef.CALLBACK_NOT_HANDLED;
    }

    @Override
    public int onCreateNewTalk(final TalkListBean talkListBean) {
        try {
            //构建事件对象
            IMProxyEvent.CreateNewTalkEvent talkEvent
                    = new IMProxyEvent.CreateNewTalkEvent();
            talkEvent.setTalkId(talkListBean.getTalkerAccount());
            talkEvent.setTalkListBean(talkListBean);
            //打印事件对象
            //LogUtil.getUtils().d(talkEvent.toString());
            //发送事件
            this.busProvider.post(talkEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int onDeleteTalk(TalkListBean talkListBean) {
        try {
            //构建事件对象
            IMProxyEvent.DeleteTalkEvent talkEvent
                    = new IMProxyEvent.DeleteTalkEvent();
            talkEvent.setTalkId(talkListBean.getTalkerAccount());
            talkEvent.setTalkListBean(talkListBean);

            //打印事件对象
            //LogUtil.getUtils().d(talkEvent.toString());
            //发送事件
            this.busProvider.post(talkEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int refreshSingleTalk(TalkListBean talkListBean) {
        try {
            //构建事件对象
            IMProxyEvent.RefreshSingleTalkEvent talkEvent
                    = new IMProxyEvent.RefreshSingleTalkEvent();
            talkEvent.setTalkId(talkListBean.getTalkerAccount());
            talkEvent.setTalkListBean(talkListBean);

            //打印事件对象
            //LogUtil.getUtils().d(talkEvent.toString());
            //发送事件
            this.busProvider.post(talkEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int refreshTalkList() {
        try {
            //构建事件对象
            IMProxyEvent.RefreshTalkListEvent talkEvent
                    = new IMProxyEvent.RefreshTalkListEvent();

            //打印事件对象
            //LogUtil.getUtils().d(talkEvent.toString());
            //发送事件
            this.busProvider.post(talkEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int refreshMessageList() {
        try {
            //构建事件对象
            IMProxyEvent.RefreshMessageListEvent messageEvent
                    = new IMProxyEvent.RefreshMessageListEvent();

            //打印事件对象
            //LogUtil.getUtils().d(messageEvent.toString());
            //发送事件
            this.busProvider.post(messageEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int onRefreshSingleMessage(String account, TalkMessageBean talkMessageBean) {
        try {
            //构建事件对象
            IMProxyEvent.RefreshSingleMessageEvent messageEvent
                    = new IMProxyEvent.RefreshSingleMessageEvent();
            messageEvent.setMsgAccount(account);
            messageEvent.setTalkMessageBean(talkMessageBean);

            //打印事件对象
            //LogUtil.getUtils().d(messageEvent.toString());
            //发送事件
            this.busProvider.post(messageEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int onReceiveNewMessage(String account, List<TalkMessageBean> talkMessageBeans) {
        try {

            IMProxyEvent.ReceiveNewMessageEvent messageEvent
                    = new IMProxyEvent.ReceiveNewMessageEvent();
            messageEvent.setMsgAccount(account);
            messageEvent.setTalkMessageBeansList(talkMessageBeans);

            //打印事件对象
            //LogUtil.getUtils().d(messageEvent.toString());
            //发送事件
            this.busProvider.post(messageEvent);


            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }


    @Override
    public int onRemainNewMessage(TalkListBean listBean, List<TalkMessageBean> talkMessageBean) {
        try {

            IMProxyEvent.GetSingleListBeanDisturb messageEvent
                    = new IMProxyEvent.GetSingleListBeanDisturb();
            messageEvent.setListBean(listBean);
            messageEvent.setTalkMessageBeansList(talkMessageBean);
            //打印事件对象
            //LogUtil.getUtils().d(messageEvent.toString());
            //发送事件
            this.busProvider.post(messageEvent);

            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }

    @Override
    public int onDeleteMessage(String account, TalkMessageBean talkMessageBean) {
        try {
            //构建事件对象
            IMProxyEvent.DeleteMessageEvent messageEvent
                    = new IMProxyEvent.DeleteMessageEvent();
            messageEvent.setMsgAccount(account);
            messageEvent.setTalkMessageBean(talkMessageBean);

            //打印事件对象
            //LogUtil.getUtils().d(messageEvent.toString());
            //发送事件
            this.busProvider.post(messageEvent);
            //返回正确处理结果
            return ConstDef.CALLBACK_HANDLED;
        } catch (Exception ex) {
            //处理异常信息
            //LogUtil.getUtils().e(ex.getMessage());
            return ConstDef.CALLBACK_NOT_HANDLED;
        }
    }


    @Override
    public int onInitFinished() {

        try {
            //构建事件对象
            IMProxyEvent.OnInitFinishedEvent onInitFinishedEvent =
                    new IMProxyEvent.OnInitFinishedEvent();

            //打印日志
            //LogUtil.getUtils().i(onInitFinishedEvent.toString());
            //发送事件
            this.busProvider.post(onInitFinishedEvent);
        } catch (Exception e) {
            //处理异常信息
            //LogUtil.getUtils().e(e.getMessage());
        }

        return ConstDef.CALLBACK_HANDLED;
    }

    @Override
    public int onInitFailed() {
        try {
            //构建事件对象
            IMProxyEvent.OnInitFailedEvent onInitFailedEvent =
                    new IMProxyEvent.OnInitFailedEvent();

            //打印日志
            //LogUtil.getUtils().i(onInitFailedEvent.toString());
            //发送事件
            this.busProvider.post(onInitFailedEvent);
        } catch (Exception e) {
            //处理异常信息
            //LogUtil.getUtils().e(e.getMessage());
        }
        return ConstDef.CALLBACK_HANDLED;
    }
}
