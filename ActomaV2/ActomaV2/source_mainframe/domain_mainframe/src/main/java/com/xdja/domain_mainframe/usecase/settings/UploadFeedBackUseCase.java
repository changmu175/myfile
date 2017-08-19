package com.xdja.domain_mainframe.usecase.settings;

import android.content.Context;

import com.xdja.domain_mainframe.repository.DownloadRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext2UseCase;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by ALH on 2016/8/12.
 */
public class UploadFeedBackUseCase extends Ext2UseCase<Context ,UploadFeedBackUseCase.FeedBackRequestBean, UploadFeedBackUseCase.UploadFeedBackResponeBean> {

    DownloadRepository downloadRepository;

    @Inject
    public UploadFeedBackUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DownloadRepository repository) {
        super(threadExecutor, postExecutionThread);
        downloadRepository = repository;
    }

    @Override
    public Observable<UploadFeedBackResponeBean> buildUseCaseObservable() {
        return downloadRepository.uploadFeedback(p1);
    }

    public static class UploadFeedBackResponeBean {
        private String result;//结果 0-成功 非0-失败
        private String msg;//错误信息

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "UploadFeedBackResponeBean{" +
                    "result='" + result + '\'' +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }

    public static class FeedBackRequestBean {
        private String appId;//应用标识
        private String contact;//联系方式
        private Data data;//数据对象
        private List<String> attachments;//附件内容

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public List<String> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<String> attachments) {
            this.attachments = attachments;
        }

        @Override
        public String toString() {
            return "FeedBackRequestBean{" +
                    "appId='" + appId + '\'' +
                    ", contact='" + contact + '\'' +
                    ", data=" + data +
                    ", attachments=" + attachments +
                    '}';
        }

        /**
         * 数据对象
         */
        public static class Data {
            private String content;//意见和建议
            private String account;//安通账号
            private int type;//类型 0 or 1

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getAccount() {
                return account;
            }

            public void setAccount(String account) {
                this.account = account;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            @Override
            public String toString() {
                return "Data{" +
                        "content='" + content + '\'' +
                        ", account='" + account + '\'' +
                        ", type=" + type +
                        '}';
            }
        }
    }
}
