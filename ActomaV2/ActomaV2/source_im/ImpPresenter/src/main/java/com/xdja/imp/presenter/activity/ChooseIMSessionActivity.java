package com.xdja.imp.presenter.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.contact.bean.Avatar;
import com.xdja.contact.bean.dto.LocalCacheDto;
import com.xdja.contact.presenter.activity.ChooseContactPresenter;
import com.xdja.contact.util.cache.ContactSearchUtils;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.contactopproxy.ContactService;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.utils.AppVersionHelper;
import com.xdja.imp.data.utils.IMFileUtils;
import com.xdja.imp.domain.interactor.def.*;
import com.xdja.imp.domain.interactor.im.GetImageFileListForwardUseCase;
import com.xdja.imp.domain.interactor.im.GetImageFileListUseCase;
import com.xdja.imp.domain.model.*;
import com.xdja.imp.event.ForwardCompletedEvent;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.adapter.ChooseIMSessionAdapterPresenter;
import com.xdja.imp.presenter.adapter.SearchResultAdapter;
import com.xdja.imp.presenter.command.SessionListCommand;
import com.xdja.imp.ui.ViewChooseIMSession;
import com.xdja.imp.ui.vu.ISessionListVu;
import com.xdja.imp.util.XToast;
import com.xdja.imp.widget.SharePopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.functions.Func1;

/**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，分享界面会话选择界面fragment
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)修改转发图片时图片信息获取的方式 by ycm at 20161110.
 * 3)增加转发分享过程中的正在发送中提示 by ycm at 20161110.
 * 4)Activity 被回收之后导致getActivity为null，这里在fragment初始化的时候设置Context的引用  by ycm at 20161201.
 */
public class ChooseIMSessionActivity extends IMActivityPresenter<SessionListCommand, ISessionListVu>
        implements SessionListCommand, SharePopWindow.PopWindowEvent<TalkListBean> {
    //获取会话列表
    @Inject
    GetSessionList getSessionList;

    @Inject
    Lazy<ContactService> contactService;

    @Inject
    Lazy<GetImageFileListUseCase> getImageFileList;

    @Inject
    Lazy<GetImageFileListForwardUseCase> getImageFileListForward; // add by ycm 20161110

    @Inject
    Lazy<SendFileMsgList> sendFileMsgList;

    @Inject
    Lazy<SendTextMsg> sendTextMsg;

    @Inject
    Lazy<ShareTextMsg> shareTextMsg;

    @Inject
    Lazy<ShareFileMsgList> shareFileMsgList;

    //获取会话配置
    @Inject
    Lazy<GetSessionConfigs> getSessionConfigs;

    @Inject
    //匹配会话配置
    Lazy<MatchSessionConfig> matchSessionConfig;

    @Inject
    BusProvider busProvider;

    @Inject
    Lazy<GetVersion> getVersion;

    @Inject
    Lazy<CompressImages> compressImages;
    private SearchResultAdapter searchResultAdapterAdapter;
    private ChooseIMSessionAdapterPresenter adapterPresenter = null;
    //会话集合
    private final List<TalkListBean> dataSource = new ArrayList<>();
    private Intent intent = null;
    private LocalSearchTask searchTask;
    //adapter类型
    private final String[] adapterTypes = new String[]{"CHOOSE_SESSION_ADAPTER",
            "SEARCH_RESULT_ADAPTER"};
    private final String AT_SESSION = "-10000_100";
    private final String TAG = "ChooseIMSessionActivity";
    private String type;
    /**
     * 最大可发送文件大小
     */
    private static final float MAX_FILE_SIZE = 30 * 1024 * 1024;

    @NonNull
    @Override
    protected Class<? extends ISessionListVu> getVuClass() {
        return ViewChooseIMSession.class;
    }

    @NonNull
    @Override
    protected SessionListCommand getCommand() {
        return this;
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        type = IMFileUtils.filterFileType(intent);// add by ycm for bug 8194 20170117
        getVu().setType(TextUtils.equals(type, ConstDef.FILE_SHARE_TYPE));
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);
        if (null == useCaseComponent) {
            LogUtil.getUtils().i("useCaseComponent is null");
            return;
        }
        //初始化注入
        useCaseComponent.inject(this);
        busProvider.register(this);
        //初始化适配器
        adapterPresenter = new ChooseIMSessionAdapterPresenter(dataSource);

        this.useCaseComponent.inject(adapterPresenter);
        //设置适配器绑定的单个项
        adapterPresenter.setListView(getVu().getDisplayList());
        adapterPresenter.setActivity(this);
        //初始化ListView
        getVu().initListView(adapterPresenter);
        setAdapterType(adapterTypes[0]);
        //将好友、群聊加入缓存
        new preSearch().executeOnExecutor(Executors.newScheduledThreadPool(20));
        getVu().loadSelfImage();
        getSessionList
                .setParam("", 0)
                .execute(new OkSubscriber<List<TalkListBean>>(this.okHandler) {
                    @Override
                    public void onNext(List<TalkListBean> list) {
                        super.onNext(list);
                        if (list == null || list.isEmpty()) {
                            dataSource.clear();
                            adapterPresenter.notifyDataSetChanged();
                        } else {
                            List<TalkListBean> listBeen = new ArrayList<>();
                            if (!getVu().getType()) {//不是文件转发
                                for (int i = 0; i < list.size(); i++) {
                                    if (AT_SESSION.equals(list.get(i).getTalkFlag())) {//除去安通加团队会话
                                        list.remove(list.get(i));
                                    }
                                }
                                listBeen = list;
                            } else {//是文件转发
                                for (int i = 0; i < list.size(); i++) {
                                    if (ConstDef.CHAT_TYPE_P2P == list.get(i).getTalkType()) {//过滤掉群聊
                                        listBeen.add(list.get(i));
                                    }
                                }
                            }
                            dataSource.clear();
                            dataSource.addAll(listBeen);
                            adapterPresenter.notifyDataSetChanged();
                            getSessionConfigs.get().execute(new GetSessionConfigsSubscriber());
                        }
                    }
                });

        setTitle(R.string.select_session);
    }

    /**
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.intent = intent;
        type = IMFileUtils.filterFileType(intent);
        getVu().setType(TextUtils.equals(type, ConstDef.FILE_SHARE_TYPE));
    }

    /**
     * 获取会话配置结果监听
     */
    class GetSessionConfigsSubscriber extends OkSubscriber<List<SessionConfig>> {

        public GetSessionConfigsSubscriber() {
            super(okHandler);
        }

        @Override
        public void onNext(List<SessionConfig> configs) {
            super.onNext(configs);
            if (configs != null) {
                matchSessionConfig
                        .get()
                        .setConfigs(configs, dataSource)
                        .execute(
                                new OkSubscriber<List<TalkListBean>>(okHandler) {
                                    @Override
                                    public void onNext(List<TalkListBean> talkListBeen) {
                                        super.onNext(talkListBeen);
                                        dataSource.clear();
                                        if (talkListBeen != null && !talkListBeen.isEmpty()) {
                                            dataSource.addAll(talkListBeen);
                                        }
                                        adapterPresenter.notifyDataSetChanged();
                                    }
                                }
                        );
            } else {
                LogUtil.getUtils().d("Session config is null !!");
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            LogUtil.getUtils().e("Get session config error：" + e.getMessage());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ContactSearchUtils.endSearch();//释放联系人缓存
        if (busProvider != null) {
            busProvider.unregister(this);
        }
    }

    @Override
    public void onListItemClick(int position) {
        itemClickEventWithAdapterType(getAdapterType(), position);
    }

    /**
     * 根据adapter的不同点击事件不同
     *
     * @param adapterType
     * @param position
     */
    private void itemClickEventWithAdapterType(String adapterType, int position) {
        if (position != 0) {
            if (adapterType.equals(adapterTypes[0])) {
                if (dataSource.size() != 0) {
                    TalkListBean talkListBean = dataSource.get(position - 1);
                    clickSession(talkListBean);
                }
            } else if (adapterType.equals(adapterTypes[1])) {
                if (ObjectUtil.objectIsEmpty(searchResultAdapterAdapter)) {
                    return;
                }
                List<LocalCacheDto> dataSource = searchResultAdapterAdapter.getDataSource();
                if (ObjectUtil.collectionIsEmpty(dataSource)) {
                    return;
                }
                LocalCacheDto localSearchBean = dataSource.get(position);
                clickContact(localSearchBean);
            }
        }

    }

    /**
     * 点击最近会话
     *
     * @param talkListBean
     */
    @SuppressLint("SwitchIntDef")
    private void clickSession(TalkListBean talkListBean) {
        if (talkListBean != null) {
            String account = talkListBean.getTalkerAccount();
            String avaUrl;
            String nickName;
            ContactInfo contactInfo = null;
            Map<String, String> contactInfoMap = new HashMap<>();
            List<TalkListBean> talkListBeanList = new ArrayList<>();
            switch (talkListBean.getTalkType()) {
                case ConstDef.CHAT_TYPE_P2P:
                    contactInfo = getCommand().getContactInfo(account);
                    break;
                case ConstDef.CHAT_TYPE_P2G:
                    contactInfo = getCommand().getGroupInfo(account);
                    break;
                default:
                    break;
            }
            if (contactInfo != null) {
                avaUrl = contactInfo.getAvatarUrl();
                nickName = contactInfo.getName();
                contactInfoMap.put(ConstDef.AVAURL, avaUrl);
                contactInfoMap.put(ConstDef.NICK_NAME, nickName);
                talkListBeanList.add(talkListBean);
                getVu().sharePopuOptionWindow(talkListBeanList, this, contactInfoMap, intent);

            }
        }
    }

    /**
     * 点击搜索出来的联系人
     *
     * @param localSearchBean
     */
    private void clickContact(LocalCacheDto localSearchBean) {
        String account = null;
        String avaUrl = null;
        String nickName = null;
        Map<String, String> contactInfo = new HashMap<>();
        TalkListBean bean = new TalkListBean();
        List<TalkListBean> talkListBeanList = new ArrayList<>();
        if (ObjectUtil.objectIsEmpty(localSearchBean)) {
            return;
        }
        if (localSearchBean.getViewType() == ConstDef.GROUP_ITEM) {//点击的是群聊
            account = localSearchBean.getGroupId();
            avaUrl = localSearchBean.getGroupAvatar();//群头像
            nickName = localSearchBean.getGroupName();//群昵称
            bean.setTalkType(ConstDef.CHAT_TYPE_P2G);
        } else if (localSearchBean.getViewType() == ConstDef.FRIEND_ITEM) {//点击的是联系人
            if (!ObjectUtil.stringIsEmpty(localSearchBean.getAccount())) {
                account = localSearchBean.getAccount();
                Avatar avatar = localSearchBean.getAvatar();
                avaUrl = avatar.getAvatar();
                nickName = localSearchBean.getName();
                if (nickName == null) {
                    nickName = localSearchBean.getNickName();
                    if (nickName == null) {
                        nickName = account;
                    }
                }
                bean.setTalkType(ConstDef.CHAT_TYPE_P2P);
            }
        }
        bean.setTalkerAccount(account);
        talkListBeanList.add(bean);
        contactInfo.put(ConstDef.AVAURL, avaUrl);
        contactInfo.put(ConstDef.NICK_NAME, nickName);
        getVu().sharePopuOptionWindow(talkListBeanList, this, contactInfo, intent);
    }

    /**
     * 获取群信息
     *
     * @param groupId
     * @return
     */
    @Override
    public ContactInfo getGroupInfo(String groupId) {
        return contactService.get().getGroupInfo(groupId);
    }

    /**
     * 获取联系人信息
     *
     * @param account
     * @return
     */
    @Override
    public ContactInfo getContactInfo(String account) {
        return contactService.get().getContactInfo(account);
    }

    private boolean isSearching = false;

    /**
     * 根据keyword搜索好友
     *
     * @param keyWord
     * @return
     */
    @Override
    public List<LocalCacheDto> startSearch(String keyWord) {

        //本地搜索好友
        if (searchTask != null && isSearching) {
            searchTask.cancel(true);
        }
        if (TextUtils.isEmpty(keyWord)) {
            searchResultAdapterAdapter.clear();
        }
        searchTask = new LocalSearchTask(keyWord);
        searchTask.executeOnExecutor(Executors.newScheduledThreadPool(20));
        return null;
    }


    @Override
    public void preSearch(String keyWord) {
        searchResultAdapterAdapter = new SearchResultAdapter(getBaseContext());
        searchResultAdapterAdapter.setKeyWord(keyWord);
    }

    /**
     * 结束搜索
     */
    @Override
    public void endSearch() {
        getVu().setChooseIMSessionAdapter(adapterPresenter);
        setAdapterType(adapterTypes[0]);
    }

    /**
     * 分享文本或转发文本
     *
     * @param dataSource     分享至的会话
     * @param text           分享的文本内容
     * @param messageContent 留言
     */
    @Override
    public void shareText(List<TalkListBean> dataSource, String text, String messageContent) {
        if (text == null) {
            return;
        }
        showProgressDialog();
        shareTextMessage(text, messageContent, dataSource, false);
    }

    @Override
    public void forwardImages(final List<TalkListBean> dataSource,
                              final List<FileInfo> localPictureInfos,
                              final String messageContent, boolean isOriginal) {
        showProgressDialog();
        if (localPictureInfos == null) {
            getImageInfoError("forwardImages");
            return;
        }
        getImageFileListForward //modified by ycm 20161110
                .get()
                .getImageFileList(localPictureInfos, isOriginal)
                .execute(new OkSubscriber<List<FileInfo>>(this.okHandler) {
                            @Override
                            public void onNext(List<FileInfo> imageFileInfoList) {
                                super.onNext(imageFileInfoList);
                                if (imageFileInfoList != null && imageFileInfoList.size() > 0) {
                                        forwardImageMessage(imageFileInfoList, messageContent, dataSource);

                                } else {
                                    //提示信息
                                    getImageInfoError("forwardImages");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                shareOrForwardOnError("forwardImages", e);
                            }

                            @Override
                            public void onCompleted() {
                                super.onCompleted();
                            }
                        }

                );
    }

    private void getImageInfoError(String name) {
        getVu().dismissCommonProgressDialog();
        LogUtil.getUtils().d(TAG + ": " + name + ": create thumbnail failed.");
//        new XToast(getBaseContext()).display(R.string.get_image_error);
    }

    /**
     * 处理错误情况
     */
    private void shareOrForwardOnError(String name, Throwable e) {
        //提示信息
        getVu().dismissCommonProgressDialog();
        LogUtil.getUtils(TAG).e(name + ": shareOrForwardOnError" + e);
//        new XToast(this).display(R.string.send_failed);
    }

    /**
     * 转发文本
     *
     * @param dataSource     转发至的会话
     * @param text           转发的文本内容
     * @param messageContent 留言
     * @return
     */
    @Override
    public void forwardText(List<TalkListBean> dataSource, String text, String messageContent) {
        showProgressDialog();
        if (text == null) {
            return;
        }
        forwardTextMessage(text, messageContent, dataSource);

    }

    /**
     * 文件转发
     *
     * @param dataSource 会话
     * @param localFileInfos 文件信息
     * @param messageContent 留言
     */
    @Override
    public void forwardFile(final List<TalkListBean> dataSource,
                            List<FileInfo> localFileInfos,
                            final String messageContent) {
        showProgressDialog();
        //add by zya fix bug 7718
        List<FileInfo> results = new ArrayList<>();
        for (FileInfo fInfo : localFileInfos) {
            results.add(resetFileInfo(fInfo));
        }//end by zya

        checkParams(dataSource, results, messageContent, true);//校验参数
    }

    /**
     * 校验参数
     * @param dataSource 会话
     * @param localFileInfos 文件信息
     * @param messageContent 留言内容
     * @param isFwd 是否转发
     */
    private void checkParams(final List<TalkListBean> dataSource,
                             List<FileInfo> localFileInfos,
                             final String messageContent,
                             boolean isFwd) {
        if (dataSource == null || dataSource.size() <= 0) {
            return;
        }
        String account = dataSource.get(0).getTalkerAccount();
        ContactInfo contactInfo = getContactInfo(account);
        String nickName = contactInfo == null ? "" : contactInfo.getName();
        String ticket = userCache.get().getTicket();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(ConstDef.NICK_NAME, nickName);
        paramMap.put(ConstDef.TAG_TICKET, ticket);
        paramMap.put(ConstDef.CONTENT, messageContent);
        getVersion.get().setParam(account, ticket).execute(new GetVersionSubscriber(dataSource, localFileInfos, paramMap, isFwd));
    }

    /**
     * 发送文件消息
     *
     * @param dataSource     会话
     * @param results        转发的文件
     * @param messageContent 留言
     */
    private void sendFileMsg(final List<TalkListBean> dataSource,
                             List<FileInfo> results,
                             final String messageContent,
                             final boolean isFwd) {
        shareFileMsgList
                .get()
                .send(dataSource, results)
                .execute(new OkSubscriber<List<TalkListBean>>(this.okHandler) {
                    @Override
                    public void onNext(List<TalkListBean> dataSource) {
                        super.onNext(dataSource);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
//                        completeForward(messageContent, dataSource);
                        completeSendMsg(messageContent, dataSource, isFwd, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        shareOrForwardOnError("forwardImageMessage", e);
                    }
                });
    }

    //add by ycm 2017/02/13 for requestVersion [start]

    /**
     * 获取版本号
     */
    private class GetVersionSubscriber extends OkSubscriber<Integer> {
        List<TalkListBean> dataSource;
        List<FileInfo> localFileInfos;
        boolean isFwd;
        Map<String, String> paramMap;

        public GetVersionSubscriber(final List<TalkListBean> dataSource,
                                    List<FileInfo> localFileInfos,
                                    Map<String, String> paramMap,
                                    boolean isFwd) {
            super(okHandler);
            this.dataSource = dataSource;
            this.localFileInfos = localFileInfos;
            this.paramMap = paramMap;
            this.isFwd = isFwd;
        }

        @Override
        public void onNext(Integer version) {
            super.onNext(version);
            String name = paramMap.get(ConstDef.NICK_NAME);
            String messageContent = paramMap.get(ConstDef.CONTENT);
            if (version == 0) {//对方版本号支持文件
                sendFileMsg(dataSource, localFileInfos, messageContent, isFwd);
            } else {//对方版本号不支持文件
                String textMsg = String.format(getBaseContext().getResources().getString(R.string.file_not_support), name);
                new XToast(getBaseContext()).display(textMsg);
                    dismissCommonProgressDialog();

            }
        }
    }
    //add by ycm 2017/02/13 for requestVersion [end]

    //add by zya fix bug 7718
    private FileInfo resetFileInfo(FileInfo oldFileInfo) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilePath(oldFileInfo.getFilePath());
        fileInfo.setFileName(oldFileInfo.getFileName());
        fileInfo.setFileSize(oldFileInfo.getFileSize());
        fileInfo.setSuffix(oldFileInfo.getSuffix());
        fileInfo.setFileType(oldFileInfo.getFileType());
        return fileInfo;
    }//end by zya

    /**
     * 文件分享
     * @param dataSource 会话
     * @param localFileInfos 文件信息
     * @param messageContent 留言
     */
    @Override
    public void shareFile(List<TalkListBean> dataSource, List<LocalFileInfo> localFileInfos, String messageContent) {
        showProgressDialog();
        List<FileInfo> infos = new ArrayList<>();
        FileInfo fileInfo;
        for(LocalFileInfo info : localFileInfos){
            if (info.getFileSize() > MAX_FILE_SIZE) {// 判断分享文件大小
                new XToast(getBaseContext()).display(R.string.file_is_too_larger);
                getVu().dismissCommonProgressDialog();
                return;
            }
            fileInfo = new FileInfo();
            fileInfo.setFilePath(info.getFilePath());
            fileInfo.setFileName(info.getFileName());
            fileInfo.setFileSize(info.getFileSize());
            String suffix = IMFileUtils.getSuffixFromFilepath(info.getFilePath());
            fileInfo.setSuffix(suffix);
            fileInfo.setFileType(info.getFileType());
            infos.add(fileInfo);
        }
        checkParams(dataSource, infos, messageContent, false);//校验参数
    }

    @Override
    public void shareVideos(List<TalkListBean> dataSource, List<VideoFileInfo> localVideoInfos, String messageContent) {
        showProgressDialog();
        if (localVideoInfos == null) {
            getImageInfoError("shareVideos");
            return;
        }
        List<FileInfo> fileInfoList = new ArrayList<>();
        for(VideoFileInfo videoFileInfo : localVideoInfos){
            if (videoFileInfo.getFileSize() > MAX_FILE_SIZE) {// 判断分享文件大小
                new XToast(getBaseContext()).display(R.string.file_is_too_larger);
                getVu().dismissCommonProgressDialog();
                return;
            } else {
                fileInfoList.add(videoFileInfo);
            }
        }
        checkParams(dataSource, fileInfoList, messageContent, false);//校验参数
    }

    @Override
    public void forwardVideos(List<TalkListBean> dataSource, List<VideoFileInfo> localVideoInfos, String messageContent) {
        showProgressDialog();
        if (localVideoInfos == null) {
            getImageInfoError("shareVideos");
            return;
        }
        List<FileInfo> fileInfoList = new ArrayList<>();
        for(VideoFileInfo videoFileInfo : localVideoInfos){
            if (videoFileInfo.getFileSize() > MAX_FILE_SIZE) {// 判断分享文件大小
                new XToast(getBaseContext()).display(R.string.file_is_too_larger);
                getVu().dismissCommonProgressDialog();
                return;
            } else {
                fileInfoList.add(videoFileInfo);
            }
        }
        checkParams(dataSource, fileInfoList, messageContent, true);//校验参数
    }

    @Override
    public void shareWebs(final List<TalkListBean> dataSource, final List<WebPageInfo> webPageInfos, final String messageContent) {
        showProgressDialog();
        if (webPageInfos == null) {
            return;
        }

        compressImages.get().
                compressFile(webPageInfos).
                execute(new OkSubscriber<List<WebPageInfo>>(null) {
            @Override
            public void onNext(List<WebPageInfo> strings) {
                super.onNext(strings);
                List<FileInfo> fileInfoList = new ArrayList<>();
                fileInfoList.addAll(strings);
                checkParams(dataSource, fileInfoList, messageContent, false);//校验参数
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    public void forwardWebs(List<TalkListBean> dataSource, List<WebPageInfo> webPageInfos, String messageContent) {
        showProgressDialog();
        if (webPageInfos == null) {
            return;
        }
        List<FileInfo> fileInfoList = new ArrayList<>();
        fileInfoList.addAll(webPageInfos);
        checkParams(dataSource, fileInfoList, messageContent, true);//校验参数
    }

    /**
     * 分享图片或转发图片
     *
     * @param dataSource        分享至的会话
     * @param localPictureInfos 分享的图片信息list
     * @param messageContent    留言
     */
    @Override
    public void shareImages(final List<TalkListBean> dataSource,
                            List<LocalPictureInfo> localPictureInfos,
                            final String messageContent) {
        if (localPictureInfos == null) {
            return;
        }
        showProgressDialog();
        getImageFileList
                .get()
                .getImageFileList(localPictureInfos)
                .execute(
                        new OkSubscriber<List<FileInfo>>(this.okHandler) {
                            @Override
                            public void onNext(List<FileInfo> imageFileInfoList) {
                                super.onNext(imageFileInfoList);
                                if (imageFileInfoList != null && imageFileInfoList.size() > 0) {
                                    shareImageMessage(imageFileInfoList, messageContent, dataSource);
                                } else {
                                    getImageInfoError("shareImages");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                //提示信息
                                shareOrForwardOnError("shareImages", e);
                            }

                            @Override
                            public void onCompleted() {
                                super.onCompleted();
                            }
                        }
                );
    }

    /**
     * 分享文本消息
     *c
     * @param message
     * @param content
     * @return
     */
    private boolean forwardTextMessage(final String message, final String content, final List<TalkListBean> dataSource) {
        shareTextMsg
                .get()
                .send(this, message, dataSource)
                .execute(new OkSubscriber<List<TalkListBean>>(this.okHandler) {
                             @Override
                             public void onNext(List<TalkListBean> talkListBaens) {
                                 super.onNext(talkListBaens);
                             }

                             @Override
                             public void onError(Throwable e) {
                                 super.onError(e);
                                 shareOrForwardOnError("forwardTextMessage", e);
                             }

                             @Override
                             public void onCompleted() {
                                 super.onCompleted();
//                                 completeForward(content, dataSource);
                                 completeSendMsg(content, dataSource, true, false);
                             }
                         }
                );
        return true;
    }

    /**
     * 分享文本消息
     *
     * @param message
     * @param content
     * @return
     */
    private boolean shareTextMessage(final String message, final String content,
                                     final List<TalkListBean> talkListBeen,
                                     final boolean isLeaveMeassage) {
        shareTextMsg
                .get()
                .send(this, message, talkListBeen)
                .execute(new OkSubscriber<List<TalkListBean>>(this.okHandler) {
                             @Override
                             public void onNext(List<TalkListBean> talkListBeen) {
                                 super.onNext(talkListBeen);
                             }

                             @Override
                             public void onError(Throwable e) {
                                 super.onError(e);
                                 shareOrForwardOnError("shareTextMessage", e);
                             }

                             @Override
                             public void onCompleted() {
                                 super.onCompleted();
                                 completeSendMsg(content, talkListBeen, false, true);
                             }
                         }
                );
        return true;
    }

    /**
     * 分享图片消息
     *
     * @param fileInfoList 需分享的图片
     * @param message      留言
     */
    private void shareImageMessage(final List<FileInfo> fileInfoList, final String message, final List<TalkListBean> talkListBaens) {
        shareFileMsgList.get().send(talkListBaens, fileInfoList)
                .execute(new OkSubscriber<List<TalkListBean>>(this.okHandler) {
                    @Override
                    public void onNext(List<TalkListBean> talkListBaen) {
                        super.onNext(talkListBaen);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        completeSendMsg(message, talkListBaens, false, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        shareOrForwardOnError("shareImageMessage", e);
                    }
                });
    }

    /**
     * 分享图片消息
     *
     * @param fileInfoList 需分享的图片
     * @param message      留言
     */
    private boolean forwardImageMessage(List<FileInfo> fileInfoList, final String message, final List<TalkListBean> dataSource) {
        shareFileMsgList.get().send(dataSource, fileInfoList)
                .execute(new OkSubscriber<List<TalkListBean>>(this.okHandler) {
                    @Override
                    public void onNext(List<TalkListBean> dataSource) {
                        super.onNext(dataSource);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        completeSendMsg(message, dataSource, true, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        shareOrForwardOnError("forwardImageMessage", e);
                    }
                });
        return true;
    }

    /**
     * 消息转发或分享结束后的处理
     * @param message 留言
     * @param dataSource 会话
     * @param isFwd 是否转发
     * @param isLeaving 是否留言
     */
    private void completeSendMsg(String message, final List<TalkListBean> dataSource, boolean isFwd, boolean isLeaving) {

        if (isFwd) {
            if (message != null && !message.isEmpty()) {
                forwardTextMessage(message, "", dataSource);
                return;
            }
            String talkerAccount = intent.getStringExtra(ConstDef.TAG_TALKERID);
            for (TalkListBean talkListBean : dataSource) {
                if (TextUtils.equals(talkerAccount, talkListBean.getTalkerAccount())) {//转发给当前会话才刷新
                    busProvider.post(new ForwardCompletedEvent());//发送刷新消息事件
                }
            }
            new XToast(getBaseContext()).display(R.string.forward_succeed);
            finish();
            return;
        }

        if (isLeaving) {
            if (message != null && !message.isEmpty()) {
                shareTextMessage(message, "", dataSource, true);
                return;
            }
            showSelectionDialog();
        }

        busProvider.post(new ForwardCompletedEvent());//发送刷新消息事件

    }

    /**
     * 显示选择框，隐藏提示信息
     */
    private void showSelectionDialog() {
        getVu().dismissCommonProgressDialog();
        getVu().showSelectPopWindow(this);
    }

    private void showProgressDialog() {
        String sending = getString(R.string.sending);
        getVu().showCommonProgressDialog(sending, false);
    }

    // add by ycm 20161111 [start]
    public void finish() {
        getVu().dismissCommonProgressDialog();
        super.finish();
    }
    // add by ycm 20161111 [end]

    // add by ycm 2017/02/15
    public void dismissCommonProgressDialog() {
        getVu().dismissCommonProgressDialog();
    }

    /**
     * 创建新会话
     */
    @Override
    public void createNewSession() {
        Intent mIntent = new Intent(this, ChooseContactPresenter.class);
        mIntent.putExtra(ConstDef.SHARE, ConstDef.SHARE_FOR_CREATENEWSESSION);
        mIntent.setType(IMFileUtils.filterFileType(intent));// add by ycm 20170117
        startActivityForResult(mIntent, ConstDef.SELECT_CONTACT);
    }

    @Override
    public void moreContact() {
        Intent mIntent = new Intent(this, ChooseContactPresenter.class);
        mIntent.putExtra(ConstDef.SHARE, ConstDef.SHARE_FOR_MORECONTACT);
        mIntent.putExtra(ConstDef.MULTIPLE, ConstDef.MULTIPLE);
        startActivityForResult(mIntent, ConstDef.MORE_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstDef.SELECT_CONTACT) { //创建新聊天时，选择联系人事件处理
            if (data != null) {
                TalkListBean bean = new TalkListBean();
                String account = data.getStringExtra(ConstDef.ACCOUNT);
                Map<String, String> contactInfoMap = new HashMap<>();
                List<TalkListBean> talkListBeanList = new ArrayList<>();
                ContactInfo contactInfo = null;
                String avaUrl;
                String nickName;
                if (resultCode == ConstDef.SINGLE_SESSION) { //单人聊天
                    if (account != null) {
                        bean.setTalkType(ConstDef.CHAT_TYPE_P2P);
                        bean.setTalkerAccount(account);
                        contactInfo = getCommand().getContactInfo(account);
                    }
                } else if (resultCode == ConstDef.GROUP_SESSION) { //群聊
                    bean.setTalkType(ConstDef.CHAT_TYPE_P2G);
                    bean.setTalkerAccount(account);
                    contactInfo = getCommand().getGroupInfo(account);
                }
                if (contactInfo != null) {
                    avaUrl = contactInfo.getAvatarUrl();
                    nickName = contactInfo.getName();
                    contactInfoMap.put(ConstDef.AVAURL, avaUrl);
                    contactInfoMap.put(ConstDef.NICK_NAME, nickName);
                    talkListBeanList.add(bean);
                    getVu().sharePopuOptionWindow(talkListBeanList, this, contactInfoMap, intent);
                }

            }

        } else if (requestCode == ConstDef.MORE_CONTACT) { //选择更多联系人
            if (data != null) {
                List<String> selectAccount = data.getStringArrayListExtra(ConstDef.SELECT_ACCOUNT_LIST);
                List<TalkListBean> beanData = new ArrayList<>();
                Map<String, List<String>> contactInfoMap = new HashMap<>();
                List<String> avaUrls = new ArrayList<>();
                List<String> nickNames = new ArrayList<>();
                int accounts = selectAccount.size();
                String avaUrl;
                String nickName;
                for (int i = 0; i < accounts; i++) {
                    ContactInfo contactInfo = getCommand().getContactInfo(selectAccount.get(i));
                    ContactInfo groupInfo = getCommand().getGroupInfo(selectAccount.get(i));
                    TalkListBean bean = new TalkListBean();
                    groupInfo.getName();
                    if (groupInfo.getName() != null) {
                        bean.setTalkType(ConstDef.CHAT_TYPE_P2G);
                        avaUrl = groupInfo.getAvatarUrl();
                        nickName = groupInfo.getName();
                    } else {
                        bean.setTalkType(ConstDef.CHAT_TYPE_P2P);
                        avaUrl = contactInfo.getAvatarUrl();
                        nickName = contactInfo.getName();
                    }
                    bean.setTalkerAccount(selectAccount.get(i));
                    avaUrls.add(avaUrl);
                    nickNames.add(nickName);
                    beanData.add(bean);
                    Log.d("selectAccountList", selectAccount.get(i));
                }
                contactInfoMap.put(ConstDef.AVAURL, avaUrls);
                contactInfoMap.put(ConstDef.NICK_NAME, nickNames);
                getVu().handOutSharePopuOptionWindow(beanData, this, contactInfoMap, intent);
            }
        }
    }

    /**
     * 构建搜索结果数据源
     *
     * @param contactSource
     * @return
     */
    private List<LocalCacheDto> buildContactSource(List<LocalCacheDto> contactSource) {
        List<LocalCacheDto> dataSource = new ArrayList<>();
        if (!ObjectUtil.collectionIsEmpty(classifyContacts(contactSource))) {
            dataSource.addAll(classifyContacts(contactSource));
        }
        if (!ObjectUtil.collectionIsEmpty(classifyGroup(contactSource))) {
            dataSource.addAll(classifyGroup(contactSource));
        }
        return dataSource;
    }

    /**
     * 分理处好友和集团与通讯录
     *
     * @param data 搜索到的所有数据
     * @return
     */
    private List<LocalCacheDto> classifyContacts(List<LocalCacheDto> data) {
        List<LocalCacheDto> contactList = new ArrayList<>();
        for (LocalCacheDto contact : data) {
            if (contact.getViewType() == LocalCacheDto.FRIEND_ITEM) {
                contactList.add(contact);
            }
        }
        if (!ObjectUtil.collectionIsEmpty(contactList)) {
            LocalCacheDto friendAlpha = new LocalCacheDto();
            friendAlpha.setViewType(LocalCacheDto.FRIEND_ALPHA);
            contactList.add(0, friendAlpha);
        }
        return contactList;
    }

    /**
     * 分离出群聊
     *
     * @param data 搜索到的所有数据
     * @return
     */
    private List<LocalCacheDto> classifyGroup(List<LocalCacheDto> data) {
        List<LocalCacheDto> groupList = new ArrayList<>();
        for (LocalCacheDto contact : data) {
            if (contact.getViewType() == LocalCacheDto.GROUP_ITEM) {
                groupList.add(contact);
            }
        }
        if (!ObjectUtil.collectionIsEmpty(groupList)) {
            LocalCacheDto groupAlpha = new LocalCacheDto();
            groupAlpha.setViewType(LocalCacheDto.GROUP_ALPHA);
            groupList.add(0, groupAlpha);
        }
        return groupList;
    }

    /**
     * 异步搜索
     */
    private class LocalSearchTask extends AsyncTask<String, Integer, List<LocalCacheDto>> {

        private final String keyword;

        public LocalSearchTask(String keyWord) {
            this.keyword = keyWord;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSearching = true;
        }

        @Override
        protected void onPostExecute(List<LocalCacheDto> searchResult) {
            super.onPostExecute(searchResult);
            isSearching = false;
            searchResultAdapterAdapter.setDataSource(buildContactSource(searchResult));
            getVu().setLocalSearchAdapter(searchResultAdapterAdapter);
            setAdapterType(adapterTypes[1]);
        }

        @Override
        protected List<LocalCacheDto> doInBackground(String... strings) {
            return ContactSearchUtils.startSearch(keyword);
        }
    }

    private String adapterType;

    /**
     * 设置adapter的类型， 本界面的点击事件需要根据adapter的不同而响应不同的事件
     *
     * @param adapterType
     */
    private void setAdapterType(String adapterType) {
        this.adapterType = adapterType;
    }

    /**
     * 获取adapter的类型
     *
     * @return
     */
    private String getAdapterType() {
        return this.adapterType;
    }

    /**
     * 搜索前的准备
     */
    private class preSearch extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
            ContactSearchUtils.preSearch();
            return null;
        }
    }
}
