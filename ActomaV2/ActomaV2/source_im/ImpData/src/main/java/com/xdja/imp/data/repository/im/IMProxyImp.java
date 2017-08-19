package com.xdja.imp.data.repository.im;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xdja.comm.server.ActomaController;
import com.xdja.comm.server.PreferencesServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.data.cache.CardCache;
import com.xdja.imp.data.cache.ConfigCache;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.di.DiConfig;
import com.xdja.imp.data.di.annotation.Scoped;
import com.xdja.imp.data.entity.mapper.DataMapper;
import com.xdja.imp.data.entity.mapper.ModelGenerator;
import com.xdja.imp.data.entity.mapper.ValueConverter;
import com.xdja.imp.data.repository.datasource.DiskDataStore;
import com.xdja.imp.data.utils.AppVersionHelper;
import com.xdja.imp.data.utils.IMFileUtils;
import com.xdja.imp.data.utils.ToolUtil;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileExtraInfo;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.HistoryFileCategory;
import com.xdja.imp.domain.model.ImageFileInfo;
import com.xdja.imp.domain.model.LocalFileInfo;
import com.xdja.imp.domain.model.LocalPictureInfo;
import com.xdja.imp.domain.model.TalkListBean;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.domain.model.WebPageInfo;
import com.xdja.imp.domain.repository.IMProxyRepository;
import com.xdja.imp_data.R;
import com.xdja.imsdk.ImClient;
import com.xdja.imsdk.callback.CallbackFunction;
import com.xdja.imsdk.callback.IMFileInfoCallback;
import com.xdja.imsdk.callback.IMMessageCallback;
import com.xdja.imsdk.callback.IMSecurityCallback;
import com.xdja.imsdk.callback.IMSessionCallback;
import com.xdja.imsdk.constant.ImSdkConfig;
import com.xdja.imsdk.constant.ImSdkConstant;
import com.xdja.imsdk.constant.MsgPackType;
import com.xdja.imsdk.exception.ImSdkException;
import com.xdja.imsdk.model.IMFileInfo;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;
import com.xdja.imsdk.model.InitParam;
import com.xdja.imsdk.model.body.IMTextBody;
import com.xdja.imsdk.util.ToolUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * <p>Summary:IM业务代理实现</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.repository.datasource</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:17:37</p>
 * 修改备注：
 * 1)Task for 2632, modify for share and forward function by ycm at 20161103.
 */
public class IMProxyImp implements IMProxyRepository {
    private ImClient imClient;
    private Context context;
    private UserCache userCache;
    private CardCache cardCache;
    private ConfigCache configCache;
    private DataMapper mapper;
    private CallbackFunction callbackFunction;
    private IMFileInfoCallback imFileInfoCallback;
    private IMMessageCallback imMessageCallback;
    private IMSessionCallback imSessionCallback;
    private IMSecurityCallback imSecurityCallback;
    private DiskDataStore diskDataStore; // TODO: 2017/2/16 定义了未使用

    private Gson gson;
    /**
     * 默认的初始化结果
     */
    private static final int DEFAULT_REGIST_RESULT = 0;

    @Inject
    public IMProxyImp(ImClient imClient,
                      @Scoped(DiConfig.CONTEXT_SCOPE_APP)
                      Context context,
                      UserCache userCache,
                      CardCache cardCache,
                      ConfigCache configCache,
                      DataMapper mapper,
                      CallbackFunction callbackFunction,
                      IMFileInfoCallback imFileInfoCallback,
                      IMMessageCallback imMessageCallback,
                      IMSessionCallback imSessionCallback,
                      IMSecurityCallback imSecurityCallback,
                      DiskDataStore diskDataStore,
                      Gson gson) {

        this.imClient = imClient;
        this.context = context;
        this.userCache = userCache;
        this.cardCache = cardCache;
        this.configCache = configCache;
        this.mapper = mapper;
        this.callbackFunction = callbackFunction;
        this.imFileInfoCallback = imFileInfoCallback;
        this.imMessageCallback = imMessageCallback;
        this.imSessionCallback = imSessionCallback;
        this.imSecurityCallback = imSecurityCallback;
        this.diskDataStore = diskDataStore;
        this.gson = gson;

    }

    @Override
    public Observable<Integer> registSessionCallBack() {
        this.imClient.RegisterIMSessionChangeListener(this.context, this.imSessionCallback);
        return Observable.just(DEFAULT_REGIST_RESULT);
    }

    @Override
    public Observable<Integer> registMessageCallBack() {
        this.imClient.RegisterIMMessageChangeListener(this.context, this.imMessageCallback);
        return Observable.just(DEFAULT_REGIST_RESULT);
    }

    @Override
    public Observable<Integer> registFileCallBack() {
        this.imClient.RegisterIMFileInfoChangeListener(this.context, this.imFileInfoCallback);
        return Observable.just(DEFAULT_REGIST_RESULT);
    }

    @Override
    public Observable<Integer> unRegistSessionCallBack() {
        this.imClient.UnregisterIMSessionChangeListener(this.context);
        return Observable.just(DEFAULT_REGIST_RESULT);
    }

    @Override
    public Observable<Integer> unRegistMessageCallBack() {
        this.imClient.UnregisterIMMessageChangeListener(this.context);
        return Observable.just(DEFAULT_REGIST_RESULT);
    }

    @Override
    public Observable<Integer> unRegistFileCallBack() {
        this.imClient.UnregisterIMFileInfoChangeListener(this.context);
        return Observable.just(DEFAULT_REGIST_RESULT);
    }

    @Override
    public Observable<Integer> unRegistAllCallBack() {
        this.imClient.UnregisterIMSessionChangeListener(this.context);
        this.imClient.UnregisterIMMessageChangeListener(this.context);
        this.imClient.UnregisterIMFileInfoChangeListener(this.context);

        return Observable.just(DEFAULT_REGIST_RESULT);
    }

    @Override
    public Observable<Integer> downloadFile(final List<FileInfo> FileInfos) {
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(ImClient imClient) {
                        if (FileInfos == null || FileInfos.isEmpty()) {
                            return Observable.just(0);
                        }

                        List<IMFileInfo> files = new ArrayList<>();
                        for (int i = 0; i < FileInfos.size(); i++) {
                            files.add(mapper.mapIMFileInfo(FileInfos.get(i)));
                        }

                        int i = imClient.ReceiveFileStart(files);
                        return Observable.just(i);
                    }
                });
    }

    @Override
    public Observable<Integer> registAllCallBack() {
        this.imClient.RegisterIMSessionChangeListener(this.context, this.imSessionCallback);
        this.imClient.RegisterIMMessageChangeListener(this.context, this.imMessageCallback);
        this.imClient.RegisterIMFileInfoChangeListener(this.context, this.imFileInfoCallback);

        return Observable.just(DEFAULT_REGIST_RESULT);
    }

    @Override
    public Observable<Integer> initIMProxy() {
        return Observable.create(new Observable.OnSubscribe<InitParam>() {
            @Override
            public void call(Subscriber<? super InitParam> subscriber) {
                InitParam initParam = new InitParam();
                initParam.setAccount(userCache.get().getAccount());
                initParam.setTfcardId(cardCache.get().getCardId().toLowerCase());
                initParam.setTicket(userCache.get().getTicket());
                initParam.setdType(configCache.get().getDeviceType());
                initParam.setCallback(callbackFunction);
                initParam.setSecurityCallback(imSecurityCallback);

                HashMap<String, String> imProperty = new HashMap<>();
                String url = PreferencesServer.getWrapper(ActomaController.getApp())
                        .gPrefStringValue("imUrl");
                imProperty.put(ImSdkConfig.K_SERVER, url);

                String fdfs = PreferencesServer.getWrapper(ActomaController.getApp())
                        .gPrefStringValue("fastDfs");
                if (!TextUtils.isEmpty(fdfs)) {
                    if (fdfs.contains("http://") || fdfs.contains("https://")) {
                        String[] infos = fdfs.split("/");
                        String host_port = infos[2];
                        if (null != host_port) {
                            int pos = host_port.lastIndexOf(":");
                            String address = host_port.substring(0, pos == -1 ? 0 : pos);
                            String port = pos == -1 ? "" : host_port.substring(pos + 1);
                            imProperty.put(ImSdkConfig.K_FILE_ADDR, address);
                            imProperty.put(ImSdkConfig.K_FILE_PORT, port);
                        }
                    }
                }

                /*设置为Https请求*/
                //imProperty.put(ImSdkConfig.HTTPS, "true");
                imProperty.put(ImSdkConfig.K_STORE, String.valueOf(R.raw.truststore));
                //imProperty.put(ImSdkConfig.KEY_PWD, DEFAULT_PASSWORD);

                imProperty.put(ImSdkConfig.K_PATH, ToolUtil.getAppParent());
                imProperty.put(ImSdkConfig.K_SIZE, ImSdkConfig.V_SIZE);

                initParam.setProperties(imProperty);

                subscriber.onNext(initParam);

            }
        })
                .map(new Func1<InitParam, Integer>() {
                    @Override
                    public Integer call(InitParam param) {

                        return imClient.Init(param);
                    }
                });
    }

    @Override
    public Observable<Integer> releaseIMProxy() {
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(ImClient imClient) {

                        return Observable.just(imClient.Release(1));
                    }
                });
    }

    @Override
    public Observable<TalkMessageBean> sendMessage(final TalkMessageBean talkMessageBean) {
        //SendMessageUseCase存在子类,这个方法不会被调用，使用它子类的方法。buildUseCaseObservable
        return null;
    }

    @Override
    public Observable<TalkMessageBean> sendTextMessage(@NonNull final String content,
                                                       @NonNull final String to,
                                                       final boolean isShan,
                                                       final boolean isGroup) {
        return Observable.create(new Observable.OnSubscribe<IMMessage>() {
            @Override
            public void call(Subscriber<? super IMMessage> subscriber) {
                IMMessage imMessage = new IMMessage();
                imMessage.setCardId(cardCache.get().getCardId().toLowerCase());
                imMessage.setTimeToLive(isShan ? 9000 : 0);
                imMessage.setIMMessageTime(System.currentTimeMillis());
                imMessage.setMessageBody(new IMTextBody(content));
                imMessage.setFrom(userCache.get().getAccount());
                imMessage.setTo(to);
                imMessage.setType(1 + (isGroup ? 4 : 0) + (isShan ? 8 : 0));
                subscriber.onNext(imMessage);
            }
        }).flatMap(new Func1<IMMessage, Observable<TalkMessageBean>>() {
            @Override
            public Observable<TalkMessageBean> call(IMMessage imMessage) {
                try {
                    imMessage = imClient.SendIMMessage(imMessage);
                    TalkMessageBean talkMessageBean
                            = mapper.mapMessage(imMessage);

                    return Observable.just(talkMessageBean);
                } catch (ImSdkException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    @Override
    public Observable<TalkMessageBean> sendCustomTextMessage(@NonNull final String content,
                                                              @NonNull final String to,
                                                              final boolean isGroup) {
        return Observable.create(new Observable.OnSubscribe<IMMessage>() {
            @Override
            public void call(Subscriber<? super IMMessage> subscriber) {
                IMMessage imMessage = new IMMessage();
                imMessage.setCardId(cardCache.get().getCardId().toLowerCase());
                imMessage.setIMMessageTime(System.currentTimeMillis());
                imMessage.setMessageBody(new IMTextBody(content));
                imMessage.setFrom(userCache.get().getAccount());
                imMessage.setTo(to);
                imMessage.setType(isGroup ? MsgPackType.NOTICE_PG_TEXT : MsgPackType.NOTICE_PP_TEXT);
                subscriber.onNext(imMessage);
            }
        }).flatMap(new Func1<IMMessage, Observable<TalkMessageBean>>() {
            @Override
            public Observable<TalkMessageBean> call(IMMessage imMessage) {
                try {
                    imMessage = imClient.SendIMMessage(imMessage);
                    TalkMessageBean talkMessageBean
                            = mapper.mapMessage(imMessage);

                    return Observable.just(talkMessageBean);
                } catch (ImSdkException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    @Override
    public Observable<TalkMessageBean> sendFileMessage(@NonNull final String to,
                                                       final boolean isShan,
                                                       final boolean isGroup,
                                                       final List<FileInfo> fileInfoList){
        return Observable.from(fileInfoList).flatMap(new Func1<FileInfo, Observable<IMMessage>>() {
            @Override
            public Observable<IMMessage> call(FileInfo fileInfo) {
                //数据类型转换
                IMMessage imMessage = new IMMessage();
                imMessage.setCardId(cardCache.get().getCardId().toLowerCase());
                imMessage.setTimeToLive(isShan ? 9000 : 0);
                imMessage.setIMMessageTime(System.currentTimeMillis());
                imMessage.setFrom(userCache.get().getAccount());
                imMessage.setTo(to);
                imMessage.setMessageBody(mapper.mapFileBody(fileInfo));
                imMessage.setType(2 + (isGroup ? 4 : 0) + (isShan ? 8 : 0));
                return Observable.just(imMessage);
            }
        }).flatMap(new Func1<IMMessage, Observable<TalkMessageBean>>() {
            @Override
            public Observable<TalkMessageBean> call(IMMessage imMessage) {
                try {
                    imMessage = imClient.SendIMMessage(imMessage);
                    TalkMessageBean talkMessageBean
                            = mapper.mapMessage(imMessage);
                    return Observable.just(talkMessageBean);
                } catch (ImSdkException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    @Override
    public Observable<TalkMessageBean> sendWebMessage(@NonNull final String to,
                                                      final boolean isShan,
                                                      final boolean isGroup,
                                                      final WebPageInfo fileInfo) {

        return Observable.create(new Observable.OnSubscribe<IMMessage>() {
            @Override
            public void call(Subscriber<? super IMMessage> subscriber) {
                IMMessage imMessage = new IMMessage();
                imMessage.setCardId(cardCache.get().getCardId().toLowerCase());
                imMessage.setTimeToLive(isShan ? 9000 : 0);
                imMessage.setIMMessageTime(System.currentTimeMillis());
                imMessage.setFrom(userCache.get().getAccount());
                imMessage.setTo(to);
                imMessage.setMessageBody(mapper.mapWebBody(fileInfo));
                imMessage.setType(64 + (isGroup ? 4 : 0) + (isShan ? 8 : 0));
                subscriber.onNext(imMessage);
            }
        }).flatMap(new Func1<IMMessage, Observable<TalkMessageBean>>() {
            @Override
            public Observable<TalkMessageBean> call(IMMessage imMessage) {
                try {
                    imMessage = imClient.SendIMMessage(imMessage);
                    TalkMessageBean talkMessageBean
                            = mapper.mapMessage(imMessage);
                    return Observable.just(talkMessageBean);
                } catch (ImSdkException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    @Override
    public Observable<TalkMessageBean> sendFileMessage(@NonNull final String to, final boolean isShan,
                                                       final boolean isGroup, final FileInfo fileInfo){
        return Observable.create(new Observable.OnSubscribe<IMMessage>() {
            @Override
            public void call(Subscriber<? super IMMessage> subscriber) {
                IMMessage imMessage = new IMMessage();
                imMessage.setCardId(cardCache.get().getCardId().toLowerCase());
                imMessage.setTimeToLive(isShan ? 9000 : 0);
                imMessage.setIMMessageTime(System.currentTimeMillis());
                imMessage.setFrom(userCache.get().getAccount());
                imMessage.setTo(to);
                imMessage.setMessageBody(mapper.mapFileBody(fileInfo));
                imMessage.setType(2 + (isGroup ? 4 : 0) + (isShan ? 8 : 0));
                subscriber.onNext(imMessage);
            }
        }).flatMap(new Func1<IMMessage, Observable<TalkMessageBean>>() {
            @Override
            public Observable<TalkMessageBean> call(IMMessage imMessage) {

                try {
                    imMessage = imClient.SendIMMessage(imMessage);
                    TalkMessageBean talkMessageBean
                            = mapper.mapMessage(imMessage);
                    return Observable.just(talkMessageBean);
                } catch (ImSdkException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    @Override
    @Deprecated
    public Observable<TalkMessageBean> sendVoiceMessage(@NonNull final String to, final boolean isShan,
                                                        final boolean isGroup, final FileInfo fileInfo) {
        return Observable.create(new Observable.OnSubscribe<IMMessage>() {
            @Override
            public void call(Subscriber<? super IMMessage> subscriber) {
                IMMessage imMessage = new IMMessage();
                imMessage.setCardId(cardCache.get().getCardId().toLowerCase());
                imMessage.setTimeToLive(isShan ? 9000 : 0);
                imMessage.setIMMessageTime(System.currentTimeMillis());
                imMessage.setFrom(userCache.get().getAccount());
                imMessage.setTo(to);
                imMessage.setMessageBody(mapper.mapFileBody(fileInfo));
                imMessage.setType(2 + (isGroup ? 4 : 0) + (isShan ? 8 : 0));
                subscriber.onNext(imMessage);
            }
        }).flatMap(new Func1<IMMessage, Observable<TalkMessageBean>>() {
            @Override
            public Observable<TalkMessageBean> call(IMMessage imMessage) {
                try {
                    imMessage = imClient.SendIMMessage(imMessage);
                    TalkMessageBean talkMessageBean
                            = mapper.mapMessage(imMessage);
                    return Observable.just(talkMessageBean);
                } catch (ImSdkException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    @Override
    public Observable<List<TalkListBean>> getTalkListBeans(final String begin, final int size) {
        return Observable
                .just(imClient)
                .flatMap(new Func1<ImClient, Observable<IMSession>>() {
                    @Override
                    public Observable<IMSession> call(ImClient imClient) {
                        try {
                            List<IMSession> sessions = imClient.GetIMSessionList(begin, size);
                            return Observable.from(sessions);
                        } catch (ImSdkException e) {
                            return Observable.error(e);
                        }
                    }
                })
                .filter(new Func1<IMSession, Boolean>() {
                    @Override
                    public Boolean call(IMSession imSession) {

                        return imSession != null;
                    }
                })
                .map(new Func1<IMSession, TalkListBean>() {
                    @Override
                    public TalkListBean call(final IMSession imSession) {

                        if(imSession == null){
                            return null;
                        }

                        return mapper.mapTalkBean(imSession);
                    }
                })
                .toList();
    }


    @Override
    public Observable<Boolean> setProxyConfig(final Map<String, String> param) {
        return Observable.just(imClient)
                .map(new Func1<ImClient, Boolean>() {
                    @Override
                    public Boolean call(ImClient imClient) {
                        imClient.SetConfig(param);
                        return true;
                    }
                });
    }

    @Override
    public Observable<String> getProxyConfig(final String key) {
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<String>>() {
                    @Override
                    public Observable<String> call(ImClient imClient) {
                        try {
                            String config = imClient.GetConfig(key);
                            return Observable.just(config);
                        } catch (ImSdkException e) {
                            return Observable.error(e);
                        }
                    }
                });
    }

    @Override
    public Observable<TalkListBean> addCustomTalk(final TalkListBean talkListBean) {
        return Observable.just(talkListBean)
                .map(new Func1<TalkListBean, IMSession>() {
                    @Override
                    public IMSession call(TalkListBean talkListBean) {
                        if(talkListBean == null){
                            return null;
                        }
                        return mapper.mapSession(talkListBean);
                    }
                })
                .flatMap(new Func1<IMSession, Observable<TalkListBean>>() {
                    @Override
                    public Observable<TalkListBean> call(IMSession imSession) {
                        try {
                            imSession = imClient.IMSessionListAddCust(imSession);
                            talkListBean.setTalkFlag(imSession.getSessionTag());
                            return Observable.just(talkListBean);
                        } catch (ImSdkException e) {
                            return Observable.error(e);
                        }
                    }
                });
    }

    @Override
    public Observable<Integer> deleteTalks(final List<String> talkIds) {
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(ImClient imClient) {
                        int i = imClient.DeleteIMSession(talkIds);
                        return Observable.just(i);
                    }
                });
    }

    @Override
    public Observable<TalkMessageBean> addCustomMessage(final TalkMessageBean msg) {
        return Observable.just(msg)
                .map(new Func1<TalkMessageBean, IMMessage>() {
                    @Override
                    public IMMessage call(TalkMessageBean talkMessageBean) {
                        if(talkMessageBean == null){
                            return null;
                        }
                        return mapper.mapMessage(talkMessageBean);
                    }
                })
                .flatMap(new Func1<IMMessage, Observable<TalkMessageBean>>() {
                    @Override
                    public Observable<TalkMessageBean> call(IMMessage message) {
                        try {
                            IMMessage imMessage = imClient.IMMessageListAddCust("", message);
                            TalkMessageBean talkMessageBean
                                    = mapper.mapMessage(imMessage);

                            return Observable.just(talkMessageBean);
                        } catch (ImSdkException e) {
                            return Observable.error(e);
                        }
                    }
                });
    }

    @Override
    public Observable<Integer> deleteMessages(final List<Long> msgids) {
        return Observable.just(msgids)

                .flatMap(new Func1<List<Long>, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(List<Long> msgIds) {
                        int i = imClient.DeleteIMMessage(msgIds);
                        return Observable.just(i);
                    }
                });
    }

    @Override
    public Observable<List<LocalPictureInfo>> queryLocalPictures() {
        return Observable.just("").flatMap(new Func1<String, Observable<LocalPictureInfo>>(){

            @Override
            public Observable<LocalPictureInfo> call(String s) {

                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath())));
                context.sendBroadcast(scanIntent);

                //查询本地图片文件
                List<LocalPictureInfo> localPicInfoList = new ArrayList<>();
                localPicInfoList.addAll(IMFileUtils.queryLocalPictures());

                return Observable.from(localPicInfoList);
            }
        }).filter(new Func1<LocalPictureInfo, Boolean>() {
            @Override
            public Boolean call(LocalPictureInfo pictureInfo) {
                String suffix = ToolUtils.getLastString(pictureInfo.getLocalPath(), ".");
                return ToolUtils.isImageSuffix(suffix);
            }
        }).toList();
    }

    @Override
    public Observable<Map<String, List<LocalFileInfo>>> queryLocalFiles(int fileType) {
        return  Observable.just(fileType).map(new Func1<Integer, Map<String, List<LocalFileInfo>>>() {
            @Override
            public Map<String, List<LocalFileInfo>> call(Integer type) {
                //获取文件类型，根据不同的类别进行区分
                Map<String, List<LocalFileInfo>>  localFileMap = new HashMap<>();
                switch (type) {
                    case ConstDef.TYPE_VOICE: //音视频
                        localFileMap.putAll(IMFileUtils.queryLocalAudios());
                        localFileMap.putAll(IMFileUtils.queryLocalVideos());
                        break;
                    case ConstDef.TYPE_PHOTO: //图片
                        localFileMap.putAll(IMFileUtils.queryLocalImages());
                        break;
                    case ConstDef.TYPE_TXT:   //文档
                        localFileMap.putAll(IMFileUtils.queryLocalDocuments());
                        break;
                    case ConstDef.TYPE_APK:   //应用
                        localFileMap.putAll(IMFileUtils.queryLocalApplication());
                        break;
                    case ConstDef.TYPE_OTHER: //其他
                        localFileMap.putAll(IMFileUtils.queryOtherFiles());
                        break;
                }
                return localFileMap;
            }
        });
    }

    @Override
    public Observable<Map<String, List<LocalFileInfo>>> queryLastFiles() {
        return Observable.just("").map(new Func1<String, Map<String, List<LocalFileInfo>>>() {
            @Override
            public Map<String, List<LocalFileInfo>> call(String s) {
                try {
                    return IMFileUtils.getLastFileList(imClient.GetFileList(""));
                } catch (ImSdkException e) {
                    e.printStackTrace();
                }
                return Collections.emptyMap();
            }
        });
    }

    @Override
    public Observable<List<FileInfo>> getImageFileList(List<LocalPictureInfo> pictureList) {
        LogUtil.getUtils().d("pictureList:" + pictureList.toString());
        return Observable.from(pictureList)
                .concatMap(new Func1<LocalPictureInfo, Observable<FileInfo>>() {
                    @Override
                    public Observable<FileInfo> call(LocalPictureInfo pictureInfo) {
                        //生成图片缩略图，图片等相关信息
                        String account = userCache.get().getAccount();
                        if (TextUtils.isEmpty(account)){
                            return Observable.error(new NullPointerException("user account is NULL."));
                        }
                        Observable<FileInfo> fileInfo = ModelGenerator.createImageFileInfo(pictureInfo, account);
                        if (fileInfo == null){
                            return Observable.error(new Exception("create thumbnail failed."));
                        }
                        return fileInfo;
                    }
                }).toList();
    }

    // add by ycm for task 2632 实现转发时生成缩略图等资源信息 [start]

    @Override
    public Observable<List<WebPageInfo>> getCompressFileList(List<WebPageInfo> fileInfos) {
       return Observable.from(fileInfos).concatMap(new Func1<WebPageInfo, Observable<WebPageInfo>>() {
            @Override
            public Observable<WebPageInfo> call(final WebPageInfo fileInfo) {
                String fileName = UUID.randomUUID().toString();
                File compFile = new File(ToolUtil.getWebPath(), fileName);
                Observable<String> observable = ModelGenerator.createHDThumbnail(fileInfo.getFilePath(), compFile);
                return observable.map(new Func1<String, WebPageInfo>() {
                    @Override
                    public WebPageInfo call(String s) {
                        fileInfo.setFilePath(s);
                        return fileInfo;
                    }
                });
            }
        }).toList();
    }

    @Override
    public Observable<List<FileInfo>> getImageFileListForForward(final List<FileInfo> pictureInfos, final boolean isOriginal) {
        return Observable.from(pictureInfos)
                .concatMap(new Func1<FileInfo, Observable<FileInfo>>() {
            @Override
            public Observable<FileInfo> call(FileInfo fileInfo) {
                String account = userCache.get().getAccount();
                if (TextUtils.isEmpty(account)) {
                    return Observable.error(new NullPointerException("user account is NULL."));
                }
                ImageFileInfo imageFileInfo = (ImageFileInfo) fileInfo;
                FileExtraInfo fileExtraInfo = imageFileInfo.getExtraInfo();
                LocalPictureInfo localPictureInfo1 = new LocalPictureInfo();
                if (!TextUtils.isEmpty(fileExtraInfo.getRawFileUrl()) && new File(fileExtraInfo.getRawFileUrl()).exists()) {
                    localPictureInfo1 = getLocalPictureInfo(
                            fileExtraInfo.getRawFileName(),
                            fileExtraInfo.getRawFileUrl(),
                            fileExtraInfo.getRawFileSize(), true);
                } else if (!TextUtils.isEmpty(fileExtraInfo.getThumbFileUrl())
                        && new File(fileExtraInfo.getThumbFileUrl()).exists()) {
                    localPictureInfo1 = getLocalPictureInfo(
                            fileExtraInfo.getThumbFileName(),
                            fileExtraInfo.getThumbFileUrl(),
                            fileExtraInfo.getThumbFileSize(), false);
                } else if (!TextUtils.isEmpty(fileInfo.getFilePath()) && new File(fileInfo.getFilePath()).exists()) {
                    localPictureInfo1 = getLocalPictureInfo(
                            fileInfo.getFileName(),
                            fileInfo.getFilePath(),
                            fileInfo.getFileSize(), false);
                }
                Observable<FileInfo> localPictureInfo = ModelGenerator.createImageFileInfo(localPictureInfo1, account);
                if (localPictureInfo == null) {
                    return Observable.error(new Exception("create thumbnail failed."));
                }
                return localPictureInfo;
            }
        }).toList();
    }

    /**
     * 设置LocalPictureInfo的信息
     * @param name
     * @param path
     * @param size
     * @param isOriginal
     * @return
     */
    private LocalPictureInfo getLocalPictureInfo(String name, String path, long size, boolean isOriginal) {
        LocalPictureInfo localPictureInfo = new LocalPictureInfo();
        localPictureInfo.setPicName(name);
        localPictureInfo.setFileSize(size);
        localPictureInfo.setLocalPath(path);
        localPictureInfo.setOriginalPic(isOriginal);
        return localPictureInfo;
    }
	// add by ycm for task 2632 实现转发时生成缩略图等资源信息 [end]
	
	// modified by ycm 2017/03/13 [start]
    @Override
    public Observable<TalkMessageBean> getMessageById(final String msgId) {

        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<TalkMessageBean>>() {
                    @Override
                    public Observable<TalkMessageBean> call(ImClient imClient) {
                        try {
                            IMMessage imMessage = imClient.getIMMessageById(Long.valueOf(msgId));
                            TalkMessageBean talkMessageBean = mapper.mapMessage(imMessage);
                            return Observable.just(talkMessageBean);
                        } catch (ImSdkException e) {
                            return Observable.error(e);
                        }
                    }
                });

    }
	// modified by ycm 2017/03/13 [end]

    @Override
    public Observable<List<TalkMessageBean>> getMessageList(final String talkId,
                                                            final long begin,
                                                            final int size) {
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<IMMessage>>() {
                    @Override
                    public Observable<IMMessage> call(ImClient imClient) {
                        try {
                            List<IMMessage> messages = imClient.GetIMMessageList(talkId, begin, size);
                            return Observable.from(messages);
                        } catch (ImSdkException e) {
                            return Observable.error(e);
                        }
                    }
                })
                .filter(new Func1<IMMessage, Boolean>() {
                    @Override
                    public Boolean call(IMMessage msgBean) {
                        return msgBean != null;
                    }
                })
                .map(new Func1<IMMessage, TalkMessageBean>() {
                    @Override
                    public TalkMessageBean call(IMMessage message) {
                        if(message == null){
                            return null;
                        }
//                        TalkMessageBean talkMessageBean =
//                                mapper.mapMessage(message);

                        return mapper.mapMessage(message);// modified by ycm for lint 2017/02/16
                    }
                })
                .toSortedList(new Func2<TalkMessageBean, TalkMessageBean, Integer>() {
                    @Override
                    public Integer call(TalkMessageBean talkMessageBean, TalkMessageBean talkMessageBean2) {
                        return talkMessageBean.getSortTime() > talkMessageBean2.getSortTime() ? 1 : -1;
                    }
                });
    }

    @Override
    public Observable<Integer> getUnReadMsgCount(final String talkId) {
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(ImClient imClient) {
                        return Observable.just(imClient.GetRemindIMMessageCount(talkId));
                    }
                });

    }

    @Override
    public Observable<Integer> getAllUnReadMsgCount() {
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(ImClient imClient) {

                        return Observable.just(imClient.GetAllRemindIMMessageCount());
                    }
                });
    }

    @Override
    public Observable<List<TalkMessageBean>> getImageList(final String talkId, final int begin, final int size) {
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<IMMessage>>() {
                    @Override
                    public Observable<IMMessage> call(ImClient imClient) {
                        try {
                            List<IMMessage> images = imClient.GetImageList(talkId, begin, size);
                            return Observable.from(images);
                        } catch (ImSdkException e) {
                            return Observable.error(e);
                        }
                    }
                })
                .filter(new Func1<IMMessage, Boolean>() {
                    @Override
                    public Boolean call(IMMessage message) {
                        return message != null;
                    }
                })
                .map(new Func1<IMMessage, TalkMessageBean>() {

                    @Override
                    public TalkMessageBean call(IMMessage message) {
//                        return mapper.mapFileInfo(message, talkId);// TODO: 2016/12/24 liming 和亚安确认
                        return mapper.mapMessage(message);
                    }
                }).toList();
    }

    @Override
    public Observable<Integer> pauseFileSending(final FileInfo fileInfo) {
        return Observable.just(fileInfo)
                .map(new Func1<FileInfo, IMFileInfo>() {
                    @Override
                    public IMFileInfo call(FileInfo fileInfo) {
                        if (fileInfo == null) {
                            return null;
                        }

                        return mapper.mapIMFileInfo(fileInfo);
                    }
                })
                .flatMap(new Func1<IMFileInfo, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(IMFileInfo file) {
                        return Observable.just(imClient.SendFilePause(file));
                    }
                });
    }

    @Override
    public Observable<Integer> resumeFileSend(final FileInfo fileInfo) {
        return Observable.just(fileInfo)
                .map(new Func1<FileInfo, IMFileInfo>() {
                    @Override
                    public IMFileInfo call(FileInfo fileInfo) {

                        if (fileInfo == null) {
                            return null;
                        }

                        return mapper.mapIMFileInfo(fileInfo);
                    }
                })
                .flatMap(new Func1<IMFileInfo, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(IMFileInfo file) {
                        return Observable.just(imClient.SendFileResume(file));
                    }
                });
    }

    @Override
    public Observable<Integer> pauseFileReceiving(final FileInfo fileInfo) {
        return Observable.just(fileInfo)
                .map(new Func1<FileInfo, IMFileInfo>() {
                    @Override
                    public IMFileInfo call(FileInfo fileInfo) {

                        if (fileInfo == null) {
                            return null;
                        }

                        return mapper.mapIMFileInfo(fileInfo);
                    }
                })
                .flatMap(new Func1<IMFileInfo, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(IMFileInfo file) {
                        return Observable.just(imClient.ReceiveFilePause(file));
                    }
                });
    }

    @Override
    public Observable<Integer> resumeFileReceive(final FileInfo fileInfo) {
        return Observable.just(fileInfo)
                .map(new Func1<FileInfo, IMFileInfo>() {
                    @Override
                    public IMFileInfo call(FileInfo fileInfo) {
                        if (fileInfo == null) {
                            return null;
                        }
                        return mapper.mapIMFileInfo(fileInfo);
                    }
                })
                .flatMap(new Func1<IMFileInfo, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(IMFileInfo file) {
                        return Observable.just(imClient.ReceiveFileResume(file));
                    }
                });
    }

    @Override
    public Observable<Integer> changeMessageState(final TalkMessageBean talkMessageBean,
                                                  @ConstDef.MsgState int mState) {

        return Observable.just(mState)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        return ValueConverter.talkMsgStateConvert(integer);
                    }
                })

                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer msgState) {

                        if(talkMessageBean == null){
                            return null;
                        }

                        IMMessage message = mapper.mapMessage(talkMessageBean);

                        return Observable.just(imClient.IMMessageStateChange(message, msgState));
                    }
                });
    }

    @Override
    public Observable<Integer> resendMsg(final TalkMessageBean msg) {
        return Observable.just(msg)
                .map(new Func1<TalkMessageBean, IMMessage>() {
                    @Override
                    public IMMessage call(TalkMessageBean talkMessageBean) {

                        if(talkMessageBean == null){
                            return null;
                        }
//                        IMMessage message = mapper.mapMessage(talkMessageBean);

                        return mapper.mapMessage(talkMessageBean);// modified by ycm for lint 2017/02/16
                    }
                })
                .flatMap(new Func1<IMMessage, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(IMMessage message) {

                        if(message == null){
                            return Observable.just(-1);
                        }
                        return Observable.just(imClient.ResendIMMessage(message.getIMMessageId()));
                    }
                });
    }

    @Override
    public Observable<Integer> clearAllMsgByTalkId(final String talkId) {
        return Observable.just(talkId)
                .flatMap(
                        new Func1<String, Observable<Integer>>() {
                            @Override
                            public Observable<Integer> call(String account) {
                                return Observable.just(imClient.ClearIMSessionAllIMMessage(talkId));
                            }
                        }
                );
    }

    @Override
    public Observable<Integer> clearUnReadMsgCount(final String account) {
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(ImClient imClient) {
                        int i = imClient.SetRemind(account, ImSdkConstant.REMIND_CLEAR);
                        return Observable.just(i);
                    }
                });
    }

    @Override
    public Observable<Integer> clearAllData() {
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(ImClient imClient) {
                        int i = imClient.ClearAllLocalData();
                        return Observable.just(i);
                    }
                });
    }

    //add by zya
    @Override
    public Observable<Map<HistoryFileCategory, List<TalkMessageBean>>> getAllHistoryFileInfoWithTalkId(final String talkId) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());//modified by ycm for lint
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<Map<HistoryFileCategory, List<TalkMessageBean>>>>() {
                    @Override
                    public Observable<Map<HistoryFileCategory, List<TalkMessageBean>>> call(ImClient imClient) {

                        Map<HistoryFileCategory,List<TalkMessageBean>> resultMaps = new LinkedHashMap<>();

                        try {
                            List<IMMessage> messages = imClient.GetFileList(talkId);
                            for (IMMessage message : messages) {
                                TalkMessageBean messageBean = mapper.mapMessage(message);
                                messageBean.setDownloadState(ConstDef.DOWNLOAD_BEFORE);

                                //add by zya 20170103 ,download progress cache
                                if(message.isFileIMMessage()){
                                    FileInfo fInfo = messageBean.getFileInfo();
                                    if(fInfo.getPercent() == 0 && userCache.containKey(messageBean.get_id())){
                                        fInfo.setPercent(userCache.getProgress(messageBean.get_id()));
                                    }
                                }//end by zya

                                //key值获取
                                String categoryId = dateFormat.format(message.getIMMessageTime());
                                HistoryFileCategory category = new HistoryFileCategory();
                                category.setCategoryId(categoryId);
                                messageBean.setCategoryId(categoryId);
                                //value添加
                                List<TalkMessageBean> valueLists ;
                                if(resultMaps.containsKey(category)){
                                    valueLists = resultMaps.get(category);
                                } else {
                                    valueLists = new ArrayList<>();
                                    category.setTime(messageBean.getShowTime());
                                }
                                valueLists.add(messageBean);
                                resultMaps.put(category,valueLists);
                            }
                        } catch (ImSdkException e) {
                            return Observable.error(e);
                        }
                        return Observable.just(resultMaps);
                    }
                });
    }

    @Override
    public Observable<Integer> getVersion(final String account, final String ticket) {
        return Observable.just(account).map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                return  AppVersionHelper.getHelper().requestAppVersion(account, ticket);
            }
        });
    }
	
	//add by licong 网络模块的添加

    @Override
    public Observable<Integer> synService() {
        return Observable.just(imClient)
                .flatMap(new Func1<ImClient, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(ImClient imClient) {

                        return Observable.just(imClient.SyncMessage());
                    }
                });
    }
}
