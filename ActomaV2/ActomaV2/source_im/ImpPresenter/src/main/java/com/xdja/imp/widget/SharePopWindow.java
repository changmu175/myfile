package com.xdja.imp.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.server.ActomaController;
import com.xdja.contactopproxy.ContactService;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.imp.ImApplication;
import com.xdja.imp.R;
import com.xdja.imp.data.utils.HyperLinkUtil;
import com.xdja.imp.data.utils.IMFileUtils;
import com.xdja.imp.data.utils.ShareUtils;
import com.xdja.imp.domain.model.*;
import com.xdja.imp.util.BitmapUtils;
import com.xdja.imp.util.LocalPictureInfoUtil;
import com.xdja.imp.util.XToast;
import com.xdja.simcui.view.PastListenerEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * 项目名称：ActomaV2
 * 类描述：分享转发中，确认分享转发弹窗
 * 创建人：ycm
 * 创建时间：2016/11/1 17:11
 * 修改人：ycm
 * 修改时间：2016/11/1 17:11
 * 修改备注：
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Bug 5619、5619、5620、5654, modify for share and forward function by ycm at 20161104.
 * 3)适配浏览器分享来的数据 by ycm at 20161201.
 */
public class SharePopWindow {
    @Inject
    Lazy<ContactService> contactService;
    private CircleImageView circleImageView;//头像
    private TextView userName;//昵称
    private TextView canleBtn;//取消
    private TextView sendBtn;//发送
    private TextView textContent;//分享文本内容
    private FrameLayout content_fl;
    private PastListenerEditText messageContent;//留言
    private ImageView imageContent;//分享图片内容
    private CustomDialog shareDialog;//确认分享弹框
    private TextView title_tv;
    private Activity context;

    private List<FileInfo> localFileInfoList = null;
    private LinearLayout topLayout;
    private LinearLayout avatTop;
    private CircleImageView[] circleImageViews;
    private final int compressRatio = 2;
    private final String[] actionTypeArray = new String[]{"SEND", "SEND_MULTIPLE", "VIEW"};
    private View divider_iv;
    private ImageView divider_iv2;
    private boolean isExistText = false;
    private List<FileInfo> localPictureInfos = null;
    private List<VideoFileInfo> localVideoInfos = null;
    private List<LocalPictureInfo> shareImageList = null;
    private List<LocalFileInfo> shareFileList = null;
    private List<VideoFileInfo> shareVidoeList = null;
    private List<WebPageInfo> webPageInfoList = null;
    private List<WebPageInfo> forwardWebInfoList = null;
    private SpannableString shareText = null;
    private CustomDialog selectActionDialog;
    private final static String CONTACT_FILES_AUTHORITY = "com.android.contacts.files";//用于判断是否是联系人文件
    private final static String DOWNLOAD_FILES_AUTHORITY = "com.android.providers.downloads.documents";//用于判断是否是系统下载中的文件
    private final static String CONTACT_FILES_PATH = "/Android/data/com.android.contacts/cache/";//联系人文件路径
    /**
     * 显示单人分享dialog
     *
     * @param context 上下文
     * @param dataSource 会话
     * @param contactInfo 联系人信息
     * @param intent 含分享转发内容
     */
    public <T> void showSingleSharePopWindow(final Activity context, final PopWindowEvent<T> sharePopWindow,
                                             final List<T> dataSource, Map<String, String> contactInfo, Intent intent) {
        String avaUrl = contactInfo.get(ConstDef.AVAURL);
        String nickName = contactInfo.get(ConstDef.NICK_NAME);
        List<String> avaUrls = new ArrayList<>();
        List<String> nickNames = new ArrayList<>();
        avaUrls.add(avaUrl);
        nickNames.add(nickName);
        Map<String, List<String>> contactInfos = new HashMap<>();
        contactInfos.put(ConstDef.AVAURL, avaUrls);
        contactInfos.put(ConstDef.NICK_NAME, nickNames);
        showSingleSharePopWindow(context, sharePopWindow, dataSource, contactInfos, intent, false);
    }

    /**
     * 显示分享dialog
     *
     * @param context 上下文
     * @param dataSource 会话
     * @param contactInfo 联系人信息
     * @param intent 含分享转发内容
     */
    private <T> void showSingleSharePopWindow(final Activity context, final PopWindowEvent<T> sharePopWindow,
                                              final List<T> dataSource, Map<String, List<String>> contactInfo,
                                              Intent intent, boolean isHandOut) {
        if (context == null) {
            return;
        } else {
            this.context = context;
        }
        initShareDialog(context); //初始化
        initView(shareDialog);
        final String action = intent.getAction();
        String type = IMFileUtils.unifyFileType(intent);
        if (!TextUtils.isEmpty(type)) {
            if (type.startsWith(ConstDef.IMAGE_SHARE_TYPE)) {
                type = ConstDef.IMAGE_SHARE_TYPE;
            } else if (type.startsWith(ConstDef.TEXT_SHARE_TYPE)) {
                type = ConstDef.TEXT_SHARE_TYPE;
            } else if (type.startsWith(ConstDef.VIDEO_SHARE_TYPE)) {
                type = ConstDef.VIDEO_SHARE_TYPE;
            }
        }

        final boolean isOriginal = intent.getBooleanExtra(ConstDef.IS_ORIGINAL, false); //add by ycm for 5655
        List<String> avaUrls = contactInfo.get(ConstDef.AVAURL);
        List<String> nickNames = contactInfo.get(ConstDef.NICK_NAME);
        setPopWindowMessage(action, isHandOut, nickNames, avaUrls);

        switch (action) {
            case Intent.ACTION_SEND:

                type = actionForSend(type, intent, actionTypeArray[0]);
                break;
            case Intent.ACTION_SEND_MULTIPLE :
                actionForSendMulti(type, intent, actionTypeArray[1]);
                break;
            case ConstDef.FORWARD:
                type = actionForForward(type, intent);
                break;
        }

        final String finalType = type;
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareType;
                if (isExistText) {
                    shareType = ConstDef.TEXT_SHARE_TYPE;
                } else {
                    shareType = finalType;
                }
                preToSend(action, shareType, sharePopWindow, dataSource, isOriginal);
                messageContent.setEnabled(false);
            }
        });

        canleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoft(shareDialog.getView());
                shareDialog.dismiss();
            }
        });

        try {
            showDialog();
        } catch (Exception e) {
            LogUtil.getUtils("showSingleSharePopWindow").e(e);
        }

    }

    /**
     * send 类型分享
     * @param type 类型
     * @param intent 分享内容
     * @param actionType 动作类型
     * @return type类型
     */
    private String actionForSend(String type, Intent intent, String actionType) {
        switch (type) {
            case ConstDef.IMAGE_SHARE_TYPE:
                return setSharePreviewBitmap(intent, actionType);
            case ConstDef.TEXT_SHARE_TYPE:
                return dealTextShare(intent, actionType);
            case ConstDef.VIDEO_SHARE_TYPE:
                setSharePreviewVideo(intent, actionType);
                return type;
            case ConstDef.FILE_SHARE_TYPE:
                setSharePreviewFile(intent, actionType);
                return type;
            default:
                return type;
        }
    }

    /**
     * send_multiple分享类型
     * @param type 类型
     * @param intent 分享 内容
     * @param actionType 动作类型
     */
    private void actionForSendMulti(String type, Intent intent, String actionType) {
        switch (type) {
            case ConstDef.IMAGE_SHARE_TYPE:
                setSharePreviewBitmap(intent, actionType);
                break;
            case ConstDef.TEXT_SHARE_TYPE:
            case ConstDef.FILE_SHARE_TYPE:
                setSharePreviewFile(intent, actionType);
                break;
        }
    }

    /**
     * 转发
     * @param type 类型
     * @param intent 转发内容
     * @return type类型
     */
    private String actionForForward(String type, Intent intent) {
        switch (type) {
            case ConstDef.IMAGE_SHARE_TYPE:
                localPictureInfos = intent.getParcelableArrayListExtra(ConstDef.TAG_SELECTPIC);
                setForwardPreviewBitmap(localPictureInfos);
                return type;
            case ConstDef.TEXT_SHARE_TYPE:
                return dealTextShare(intent, null);
            case ConstDef.FILE_SHARE_TYPE:
                localFileInfoList = intent.getParcelableArrayListExtra(ConstDef.TAG_SELECTFILE);
                setForwardPreviewFile(localFileInfoList);
                return type;
            case ConstDef.VIDEO_SHARE_TYPE:
                localVideoInfos = intent.getParcelableArrayListExtra(ConstDef.TAG_SELECTVIDEO);
                setForwardPreviewVideo(localVideoInfos);
                return type;
            case ConstDef.WEB_SHARE_TYPE:
                forwardWebInfoList = intent.getParcelableArrayListExtra(ConstDef.TAG_SELECTWEB);
                setForwardPreviewWeb(forwardWebInfoList);
            default:
                return type;
        }
    }

    private void setForwardPreviewWeb(List<WebPageInfo> forwardWebInfoList) {
        content_fl.setVisibility(View.VISIBLE);
        textContent.setVisibility(View.VISIBLE);
        if (forwardWebInfoList != null && !forwardWebInfoList.isEmpty()) {
            if (forwardWebInfoList.size() == 1) {
                String txtContent = context.getString(R.string.web_name, forwardWebInfoList.get(0).getDescription());
                textContent.setText(new SpannableString(txtContent));
            }
        } else {
            sendBtn.setEnabled(false);
            Toast.makeText(context, R.string.content_null_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置预览文件名
     * @param fileInfo 文件信息
     */
    private void setPreviewFileName(LocalFileInfo fileInfo) {
        content_fl.setVisibility(View.VISIBLE);
        textContent.setVisibility(View.VISIBLE);
        String fileName = context.getString(R.string.file_name, fileInfo.getFileName());
        textContent.setText(fileName);
    }

    /**
     * 根据类型设置视图
     * @param isFwd 是否转发
     */
    private void setViewByType(boolean isFwd) {
        if (isFwd) {
            divider_iv2.setVisibility(View.GONE);
            divider_iv.setVisibility(View.VISIBLE);
        } else {
            divider_iv2.setVisibility(View.VISIBLE);
            divider_iv.setVisibility(View.GONE);
        }
    }

    /**
     * 给多人转发或分享的dialog
     *
     * @param context 上下文
     * @param dataSource 会话
     * @param contactInfo 联系人信息
     * @param intent 含分享转发内容
     */
    public <T> void showHandOutSharePopWindow(final Activity context, final PopWindowEvent<T> sharePopWindow,
                                              final List<T> dataSource, Map<String,
                                              List<String>> contactInfo, Intent intent) {
        showSingleSharePopWindow(context, sharePopWindow, dataSource, contactInfo, intent, true);
    }

    /**
     * 设置dialog的信息
     *
     * @param action 分享动作类型
     * @param isHandOut 是否多人
     * @param nickNames 昵称
     * @param avaUrls 头像
     */
    private void setPopWindowMessage(String action, boolean isHandOut,
                                     List<String> nickNames, List<String> avaUrls) {
        if (isHandOut) {
            if (action.equals(ConstDef.FORWARD)) {
                title_tv.setText(R.string.multi_forward_to);
                setViewByType(true);
            } else {
                title_tv.setText(R.string.multi_share_to);
                setViewByType(false);
            }
        } else {
            if (action.equals(ConstDef.FORWARD)) {
                title_tv.setText(R.string.forward_to);
                setViewByType(true);
            } else {
                title_tv.setText(R.string.share_to);
                setViewByType(false);
            }
        }
        setSudokuAvater(nickNames, avaUrls);
    }

    private void hideSoft(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 准备发送
     * @param action         分享动作
     * @param type           分享类型
     * @param dataSource     分享的会话
     */
    private <T> void preToSend(String action, String type, final PopWindowEvent<T> sharePopWindow, final List<T> dataSource, boolean isOriginal) {
        setViewStatus(true);
        String content = messageContent.getText().toString();
        switch (action) {
            case Intent.ACTION_SEND:
            case Intent.ACTION_VIEW:
            case Intent.ACTION_SEND_MULTIPLE:
                preToShare(type, sharePopWindow, dataSource, content);
                break;
            case ConstDef.FORWARD:
                preToForward(type, sharePopWindow, dataSource, content, isOriginal);
                break;
        }
        isExistText = false;
        setViewStatus(false);
        dismissDialog();
    }

    /**
     * 准备分享
     * @param type 内容类型
     * @param dataSource 会话
     * @param content 留言内容
     */
    private <T> void preToShare(String type,
                                final PopWindowEvent<T> sharePopWindow,
                                final List<T> dataSource,
                                String content) {
        switch (type) {
            case ConstDef.IMAGE_SHARE_TYPE:
                shareImages(shareImageList, content, dataSource, sharePopWindow);
                shareImageList = null;
                break;
            case ConstDef.TEXT_SHARE_TYPE:
                shareText(shareText, content, dataSource, sharePopWindow);
                shareText = null;
                break;
            case ConstDef.VIDEO_SHARE_TYPE:
                shareVideos(shareVidoeList, content, dataSource, sharePopWindow);
                break;
            case ConstDef.FILE_SHARE_TYPE:
                shareFiles(shareFileList, content, dataSource, sharePopWindow);
                shareFileList = null;
                break;
            case  ConstDef.WEB_SHARE_TYPE:
                shareWebs(webPageInfoList, content, dataSource, sharePopWindow);
                break;
            default:
                new XToast(context).display(R.string.not_support_type);
                setViewStatus(false);
        }
    }

    private <T> void shareWebs(List<WebPageInfo> webPageInfoList, String content, List<T> dataSource, PopWindowEvent<T> sharePopWindow) {
        if (webPageInfoList == null) {
            getSourceError();
            return;
        }

        if (webPageInfoList.isEmpty()) {
            getSourceError();
            return;
        }

        if (webPageInfoList.size() > 9) {
            new XToast(context).display(R.string.share_limit);
            setViewStatus(false);
        } else {
            sharePopWindow.shareWebs(dataSource, webPageInfoList, content);
        }
    }

    /**
     * 分享图片
     * @param shareImageList 图片信息list
     * @param content 留言内容
     * @param dataSource 会话
     */
    private <T> void shareImages(List<LocalPictureInfo> shareImageList, String content,
                                 List<T> dataSource, final PopWindowEvent<T> sharePopWindow) {
        if (shareImageList == null) {
            getSourceError();
            return;
        }

        if (shareImageList.isEmpty()) {
            getSourceError();
            return;
        }

        if (shareImageList.size() > 9) {
            new XToast(context).display(R.string.share_limit);
            setViewStatus(false);
        } else {
            sharePopWindow.shareImages(dataSource, shareImageList, content);
        }
    }

    /**
     * 分享文本
     * @param shareText 文本
     * @param content 留言内容
     * @param dataSource 会话
     */
    private <T> void shareText(SpannableString shareText,
                               String content,
                               List<T> dataSource,
                               final PopWindowEvent<T> sharePopWindow) {
        if (shareText == null) {
            getSourceError();
            return;
        }
        sharePopWindow.shareText(dataSource, shareText.toString(), content);
    }

    /**
     * 分享文件
     * @param shareFileList 文件信息list
     * @param content 留言内容
     * @param dataSource 会话
     */
    private <T> void shareFiles(List<LocalFileInfo> shareFileList,
                                String content,
                                List<T> dataSource,
                                final PopWindowEvent<T> sharePopWindow) {
        if (shareFileList == null) {
            getSourceError();
            return;
        }

        if (shareFileList.isEmpty()) {
            getSourceError();
            return;
        }

        if (shareFileList.size() > 9) {
            new XToast(context).display(R.string.share_file_limit);
            setViewStatus(false);
        } else {
            sharePopWindow.shareFile(dataSource, shareFileList, content);
        }
    }

    /**
     * 分享视频
     * @param shareVidoeList 视频信息list
     * @param content 留言内容
     * @param dataSource 会话
     */
    private <T> void shareVideos(List<VideoFileInfo> shareVidoeList,
                                String content,
                                List<T> dataSource,
                                final PopWindowEvent<T> sharePopWindow) {
        if (shareVidoeList == null) {
            getSourceError();
            return;
        }

        if (shareVidoeList.isEmpty()) {
            getSourceError();
            return;
        }

        if (shareVidoeList.size() > 9) {
            new XToast(context).display(R.string.share_file_limit);
            setViewStatus(false);
        } else {
            sharePopWindow.shareVideos(dataSource, shareVidoeList, content);
        }
    }

    /**
     * 准备转发
     * @param type 内容类型
     * @param dataSource 会话
     * @param content 留言内容
     */
    private <T> void preToForward(String type,
                                  final PopWindowEvent<T> sharePopWindow,
                                  final List<T> dataSource,
                                  String content,
                                  boolean isOriginal) {

        switch (type) {
            case ConstDef.IMAGE_SHARE_TYPE:
                forwardImages(localPictureInfos, content, dataSource, sharePopWindow, isOriginal);
                localPictureInfos = null;
                break;
            case ConstDef.TEXT_SHARE_TYPE:
                forwardText(shareText, content, dataSource, sharePopWindow);
                shareText = null;
                break;
            case ConstDef.FILE_SHARE_TYPE:
                forwardFiles(localFileInfoList, content, dataSource, sharePopWindow);
                localFileInfoList = null;
                break;
            case ConstDef.VIDEO_SHARE_TYPE:
                forwardVideo(localVideoInfos, content, dataSource, sharePopWindow);
                localVideoInfos = null;
                break;
            case ConstDef.WEB_SHARE_TYPE:
                forwardWebs(forwardWebInfoList, content, dataSource, sharePopWindow);
                forwardWebInfoList = null;
                break;
            default:
                new XToast(context).display(R.string.not_support_type);
                setViewStatus(false);
        }
    }

    /**
     * 转发图片
     * @param localPictureInfos 图片信息
     * @param content 留言内容
     * @param dataSource 会话
     * @param sharePopWindow popWindow
     * @param isOriginal 是否原图
     */
    private <T> void forwardImages(List<FileInfo> localPictureInfos,
                                   String content,
                                   List<T> dataSource,
                                   final PopWindowEvent<T> sharePopWindow,
                                   boolean isOriginal) {
        if (localPictureInfos == null) {
            getSourceError();
            return;
        }

        if (localPictureInfos.isEmpty()) {
            getSourceError();
            return;
        }

        if (localPictureInfos.size() > 9) {
            new XToast(context).display(R.string.share_limit);
            setViewStatus(false);
        } else {
            sharePopWindow.forwardImages(dataSource, localPictureInfos, content, isOriginal);
        }
    }

    /**
     * 转发文本
     * @param shareText 转发的文本
     * @param content 留言内容
     * @param dataSource 会话
     */
    private <T> void forwardText(SpannableString shareText,
                                 String content,
                                 List<T> dataSource,
                                 final PopWindowEvent<T> sharePopWindow) {
        if (shareText == null) {
            getSourceError();
            return;
        }
        sharePopWindow.forwardText(dataSource, shareText.toString(), content); // modified by ycm for 6501
    }

    /**
     * 转发文件
     * @param localFileInfoList 文件信息
     * @param content 留言内容
     * @param dataSource 会话
     * @param sharePopWindow popWindow
     */
    private <T> void forwardFiles(List<FileInfo> localFileInfoList,
                                  String content,
                                  List<T> dataSource,
                                  final PopWindowEvent<T> sharePopWindow) {
        if (localFileInfoList == null) {
            getSourceError();
            return;
        }

        if (localFileInfoList.isEmpty()) {
            getSourceError();
            return;
        }

        if (localFileInfoList.size() > 9) {
            new XToast(context).display(R.string.share_file_limit);
            setViewStatus(false);
        } else {
            sharePopWindow.forwardFile(dataSource, localFileInfoList, content);
        }
    }

    /**
     * 转发小视频
     * @param localVideoInfos 小视频信息
     * @param content 留言内容
     * @param dataSource 会话
     * @param sharePopWindow popWindow
     */
    private <T> void forwardVideo(List<VideoFileInfo> localVideoInfos,
                              String content,
                              List<T> dataSource,
                              final PopWindowEvent<T> sharePopWindow) {
        if (localVideoInfos == null) {
            getSourceError();
            return;
        }

        if (localVideoInfos.isEmpty()) {
            getSourceError();
            return;
        }

        if (localVideoInfos.size() > 9) {
            new XToast(context).display(R.string.share_limit);
            setViewStatus(false);
        } else {
            sharePopWindow.forwardVideos(dataSource, localVideoInfos, content);
        }
    }

    private <T> void forwardWebs(List<WebPageInfo> webPageInfoList,
                                 String content,
                                 List<T> dataSource,
                                 PopWindowEvent<T> sharePopWindow) {
        if (webPageInfoList == null) {
            getSourceError();
            return;
        }

        if (webPageInfoList.isEmpty()) {
            getSourceError();
            return;
        }

        if (webPageInfoList.size() > 9) {
            new XToast(context).display(R.string.share_limit);
            setViewStatus(false);
        } else {
            sharePopWindow.forwardWebs(dataSource, webPageInfoList, content);
        }
    }

    /**
     * 处理获取资源错误
     */
    private void getSourceError(){
        new XToast(context).display(R.string.get_source_error);
        setViewStatus(false);
    }

    /**
     * 设置发送按钮等状态避免多次点击多次发送
     *
     * @param isSending 是否在发送
     */
    private void setViewStatus(boolean isSending) {
        if (isSending) {
            sendBtn.setClickable(false);
            canleBtn.setClickable(false);
        } else {
            sendBtn.setClickable(true);
            canleBtn.setClickable(true);
        }
    }

    //add by ycm :适配各种浏览器分享来的数据 [start]
    /**
     * 从两个数据中选择一个合适的数据
     * @param str1 字符1
     * @param str2 字符2
     * @return 字符
     */
    private String dealText(String str1, String str2) {
        String text = null;
        if (!TextUtils.isEmpty(str1)) {
            text = str1;
        } else if (!TextUtils.isEmpty(str2)) {
            text = str2;
        }
        return text;
    }

    /**
     * 解析INTENT
     * @param intent 含转发分享内容
     */
    private Map<String, String> parseIntent(Intent intent) {
        Map<String, String> typeMap = new HashMap<>();
        String content ;
        String mText;
        String mUrl;
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        String weixin_text = intent.getStringExtra(ConstDef.WEIXIN_TEXT);
        mText = dealText(text, weixin_text);

        String sms_body = intent.getStringExtra(ConstDef.SMS_BODY);
        mText = dealText(sms_body, mText);

        String url = intent.getStringExtra(ConstDef.URL);
        String web_url = intent.getStringExtra(ConstDef.WEB_URL);
        mUrl = dealText(web_url, url);
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Uri uri = intent.getData();
        imageUri = imageUri == null ? uri : imageUri;
        boolean isText = false;
        if (TextUtils.isEmpty(mUrl) && imageUri != null){ //图片分享
            if (mText != null) {
                List<HyperLinkBean> linkBeanList= HyperLinkUtil.parseContent(mText, ConstDef.WEB_LINK);
                int size = linkBeanList.size();
                if (size != 0) {
                    for (HyperLinkBean hyperLinkBean : linkBeanList) {
                        if (hyperLinkBean.getHyperlink().contains(ConstDef.HTTP)) {
                            isText = true;
                            break;
                        }
                    }
                    if (isText) {
                        content = mText;
                        typeMap = setType(ConstDef.TEXT, typeMap, content);
                    } else {
                        typeMap = setType(ConstDef.IMAGE, typeMap, imageUri.toString());
                    }
                }else {
                    typeMap = setType(ConstDef.IMAGE, typeMap, imageUri.toString());
                }
            } else {
               typeMap = setType(ConstDef.IMAGE, typeMap, imageUri.toString());
            }
        } else { //网页分享
//            checkWebShare()
            if (mText != null) {
                content = mText;
            } else {
                content = mUrl;
            }
            //add by ycm for bug 8939 [start]
            if (imageUri != null) {
                List<HyperLinkBean> linkBean = HyperLinkUtil.parseContent(content, ConstDef.WEB_LINK);
                if (linkBean.isEmpty()) {
                    return setType(ConstDef.IMAGE, typeMap, imageUri.toString());
                }
            }
            //add by ycm for bug 8939 [end]
           typeMap = setType(ConstDef.TEXT, typeMap, content);
        }
        return typeMap;
    }

    private Map<String, String> setType(String type, Map<String, String> typeMap, String content) {
        if (ConstDef.TEXT.equals(type)) {
            typeMap.clear();
            typeMap.put(ConstDef.CONTENT, content);
            typeMap.put(ConstDef.TYPE, ConstDef.TEXT);
        } else if (ConstDef.IMAGE.equals(type)) {
            typeMap.clear();
            typeMap.put(ConstDef.CONTENT, content);
            typeMap.put(ConstDef.TYPE, ConstDef.IMAGE);
        }
        return typeMap;
    }
    //add by ycm :适配各种浏览器分享来的数据 [end]




    /**
     * 设置分享图片预览
     *
     * @param intent 含分享转发内容
     * @param actionType 分享动作类型
     */
    private String setSharePreviewBitmap(Intent intent, String actionType) {
        ArrayList<String> paths = new ArrayList<>();
        Bitmap bitmap = null;
        String type = ConstDef.IMAGE_SHARE_TYPE;
//        Map<String, String> typeMap;
        if (actionType.equals(actionTypeArray[0])) {
            // 不存在文本再设置图片预览
            ShareInfo shareInfo = ShareUtils.getShareInfo(intent);
            switch (shareInfo.getShareType()) {
                case 0x01:
                    isExistText = false;
                    String path = IMFileUtils.getImagePathFormUri(context, decodeUri(shareInfo.getFileUri()));
                    if (!TextUtils.isEmpty(path)) {
                        paths.add(path);
                        bitmap = BitmapUtils.getZoomedDrawable(path, compressRatio);
                    }
                    break;
                case 0x02:
                    isExistText = true;
                    String content = shareInfo.getContent() + shareInfo.getWebUrl();
                    dealTextContent(content);
                    type = ConstDef.TEXT_SHARE_TYPE;
                    break;
                case 0x03:
                    isExistText = false;
                    WebPageInfo webPageInfo = ShareUtils.getShareWebInfo(context, shareInfo);
                    webPageInfoList = new ArrayList<>();
                    webPageInfoList.add(webPageInfo);
                    setPreviewShareWeb(webPageInfo);
                    type = ConstDef.WEB_SHARE_TYPE;
                    break;
            }
//            typeMap = parseIntent(intent);
//            String type = typeMap.get(ConstDef.TYPE);
//            String content = typeMap.get(ConstDef.CONTENT);
//            if (TextUtils.equals(type, ConstDef.TEXT)) {
//                isExistText = true;
//                dealTextContent(content);
//            } else if (TextUtils.equals(type, ConstDef.IMAGE)){
//                isExistText = false;
//                String path = getImagePathFormUri(context, decodeUri(content));
//                if (!TextUtils.isEmpty(path)) {
//                    paths.add(path);
//                    bitmap = BitmapUtils.getZoomedDrawable(path, compressRatio);
//                }
//            }
        } else if (actionType.equals(actionTypeArray[1])) {
            ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
			//for bug 6544 [start]
            for (Uri uri : uris) {
                if (uri != null) {
                    String content = uri.toString();
                    String path = IMFileUtils.getImagePathFormUri(context, decodeUri(content));
                    paths.add(path);
                }
            }
            shareImageList = LocalPictureInfoUtil.getLocalPictureInfoList(paths);
        } /*else if (actionType.equals(actionTypeArray[2])) {
            // for bug 6442 Intent中Uri中文乱码问题 [start]
            Uri imageUri = intent.getData();
            String uriStr = Uri.decode(imageUri.toString());
            imageUri = Uri.parse(uriStr);
            // for bug 6442 Intent中Uri中文乱码问题 [end]
            String path = getImagePathFormUri(context, imageUri);
            if (!TextUtils.isEmpty(path)) {
                paths.add(path);
                bitmap = BitmapUtils.getZoomedDrawable(path, compressRatio);
            }
        }*/

        if (TextUtils.equals(type, ConstDef.IMAGE_SHARE_TYPE)) {
            content_fl.setVisibility(View.VISIBLE);
            imageContent.setVisibility(View.VISIBLE);
            if (bitmap == null) { // add by ycm 20161111: 如果预览图片获取为空，则显示失败图片
                imageContent.setImageResource(R.drawable.pic_failed);
            } else {
                shareImageList = LocalPictureInfoUtil.getLocalPictureInfoList(paths);
                imageContent.setImageBitmap(bitmap);
            }
        }
        return type;

    }

    /**
     * 设置文件分享预览
     * @param intent 含分享转发内容
     * @param actionType 分享动作类型
     */
    private void setSharePreviewFile(Intent intent, String actionType) {
        if (actionType.equals(actionTypeArray[0]) || actionType.equals(actionTypeArray[2])) {
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            Uri uri = intent.getData();
            imageUri = imageUri == null ? uri : imageUri;
            if (imageUri == null) {
                return;
            }
            String content = imageUri.toString();
            LocalFileInfo localFileInfo = IMFileUtils.queryLocalFiles(context, decodeUri(content));
            if (localFileInfo != null) {
                shareFileList = new ArrayList<>();
                shareFileList.add(localFileInfo);
                setPreviewFileName(localFileInfo);
            }
        } else {
            shareFileList = new ArrayList<>();
            ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            for (Uri uri : uris) {
                if (uri != null) {
                    String content = uri.toString();
                    LocalFileInfo localFileInfo = IMFileUtils.queryLocalFiles(context, decodeUri(content));
                    if (localFileInfo != null) {
                        shareFileList.add(localFileInfo);
                    }
                }
            }
        }
    }

    private void setSharePreviewFile(ShareInfo shareInfo) {
        String fileUriStr = shareInfo.getFileUri();
        if (TextUtils.isEmpty(fileUriStr)) {
            return;
        }
        LocalFileInfo localFileInfo = IMFileUtils.queryLocalFiles(context, decodeUri(fileUriStr));
        if (localFileInfo != null) {
            shareFileList = new ArrayList<>();
            shareFileList.add(localFileInfo);
            setPreviewFileName(localFileInfo);
        }
    }

    /**
     * 设置转发文件预览
     * @param fileInfoList 文件信息
     */
    private void setForwardPreviewFile(List<FileInfo> fileInfoList) {
        content_fl.setVisibility(View.VISIBLE);
        textContent.setVisibility(View.VISIBLE);
        if (fileInfoList != null && !fileInfoList.isEmpty()) {
            if (fileInfoList.size() == 1) {
                String fileName = context.getString(R.string.file_name, fileInfoList.get(0).getFileName());
                textContent.setText(fileName);
            }
        }
    }

    /**
     * Intent 中Uri中文乱码问题
     * @param content Uri字符
     * @return uri
     */
    private Uri decodeUri(String content) {
        String uriStr = Uri.decode(content);
        return Uri.parse(uriStr);
    }


    /**
     * 设置分享文本内容预览
     *
     * @param intent 含分享内容
     */
    private boolean getSharePreviewText(Intent intent, String action) {
        String content = null;
        //modified by ycm for 6336 [start]
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (text != null) {
            content = text;
        } else {
            ClipData shareClip = intent.getClipData();
            Uri textUri = intent.getData();
            if (shareClip != null) {
                ClipData.Item item = shareClip.getItemAt(0);
                Uri fileuri = item.getUri();
                CharSequence chars = item.getText();
                if (fileuri != null) {
				 // add by ycm : 文本分享中判断是否是vacard格式 2017/02/09
                    LocalFileInfo fileInfo = IMFileUtils.queryLocalFiles(context, fileuri);
                    if (fileInfo != null) {
                        setPreviewFileName(fileInfo);
                        shareFileList = new ArrayList<>();
                        shareFileList.add(fileInfo);
                    }
                    return false;
                } else if (chars != null){
                    content = chars.toString();
                }
            } else if (textUri != null && action != null) {
                setSharePreviewFile(intent, action);
            } else {
                return false;
            }
            //modified by ycm for 6336 [end]
        }
        if (content != null && !content.isEmpty()) {
            dealTextContent(content);
            return true;
        } else {
            return false;
        }
    }

    //modified by ycm  [start]

    /**
     * 处理分享文字
     * @param intent 含分享信息
     */
    private String dealTextShare(Intent intent, String action) {
        ShareInfo shareInfo = ShareUtils.getShareInfo(intent);
        switch (shareInfo.getShareType()) {
            case ShareInfo.SHARE_FILE:
                setSharePreviewFile(shareInfo);
                return ConstDef.FILE_SHARE_TYPE;
            case ShareInfo.SHARE_TEXT:
                dealTextContent(shareInfo.getContent());
                return ConstDef.TEXT_SHARE_TYPE;
            case ShareInfo.SHARE_WEB:
                WebPageInfo webPageInfo = ShareUtils.getShareWebInfo(context, shareInfo);
                webPageInfoList = new ArrayList<>();
                webPageInfoList.add(webPageInfo);
                setPreviewShareWeb(webPageInfo);
                return ConstDef.WEB_SHARE_TYPE;
        }

        return ConstDef.TEXT_SHARE_TYPE;
    }

    /**
     * 处理分享文字内容
     * @param content 文本内容
     */
    private void dealTextContent(String content) {
        if (content != null && !content.isEmpty()) {
            shareText = BitmapUtils.formatSpanContent(content, context, ImApplication.FACE_ITEM_SMALL_VALUE);
            if (!shareText.toString().isEmpty()) {
                content_fl.setVisibility(View.VISIBLE);
                textContent.setVisibility(View.VISIBLE);
                textContent.setText(new SpannableString(shareText));// for bug 5699 by ycm
            } else {
                sendBtn.setEnabled(false);
                Toast.makeText(context, R.string.get_source_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            sendBtn.setEnabled(false);
            Toast.makeText(context, R.string.content_null_error, Toast.LENGTH_SHORT).show();
        }
    }
    //modified by ycm  [end]
	
	//网页预览
    private void setPreviewShareWeb(WebPageInfo shareWeb) {
        if (shareWeb != null) {
            String text = ActomaController.getApp().getString(R.string.web_message) + shareWeb.getTitle();
            if (!TextUtils.isEmpty(text)) {
                content_fl.setVisibility(View.VISIBLE);
                textContent.setVisibility(View.VISIBLE);
                textContent.setText(new SpannableString(text));// for bug 5699 by ycm
            } else {
                sendBtn.setEnabled(false);
                Toast.makeText(context, R.string.get_source_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            sendBtn.setEnabled(false);
            Toast.makeText(context, R.string.content_null_error, Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 设置图片转发预览
     *
     * @param localPictureInfos 要转发的图片List
     */
    private void setForwardPreviewBitmap(List<FileInfo> localPictureInfos) {
        if (localPictureInfos != null && !localPictureInfos.isEmpty()) {
            Bitmap bitmap;
            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            int pictureInfosSize = localPictureInfos.size();
            for (int i = 0; i < pictureInfosSize; i++) {
                ImageFileInfo imageFileInfo = (ImageFileInfo) localPictureInfos.get(i);
                String localPath = imageFileInfo.getFilePath();
                bitmap = BitmapUtils.getZoomedDrawable(localPath, compressRatio);//获取压缩图片
                bitmaps.add(bitmap);
            }
            content_fl.setVisibility(View.VISIBLE);
            imageContent.setVisibility(View.VISIBLE);
            if (bitmaps.get(0) != null) {
                imageContent.setImageBitmap(bitmaps.get(0));
            }
            return;
        }
        imageContent.setImageResource(R.drawable.pic_failed);

    }

    /**
     * 设置短视频转发预览
     * @param videoFileInfoList 要转发的短视频List
     */
    private void setForwardPreviewVideo(List<VideoFileInfo> videoFileInfoList) {
        if (videoFileInfoList != null && !videoFileInfoList.isEmpty()) {
            String localPath = videoFileInfoList.get(0).getFilePath();
            Bitmap bitmap = BitmapUtils.getZoomedDrawable(localPath, compressRatio);
            content_fl.setVisibility(View.VISIBLE);
            imageContent.setVisibility(View.VISIBLE);
            if (bitmap != null) {
                imageContent.setImageBitmap(bitmap);
            }
            return;
        }
        imageContent.setImageResource(R.drawable.pic_failed);
    }

    /**
     * 设置小视频预览
     * @param intent 分享内容
     * @param actionType 动作类型
     */
    private void setSharePreviewVideo(Intent intent, String actionType) {
        if (actionType.equals(actionTypeArray[0]) || actionType.equals(actionTypeArray[2])) {
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            Uri uri = intent.getData();
            imageUri = imageUri == null ? uri : imageUri;
            VideoFileInfo localVideoInfo = IMFileUtils.getVideoInfo(context, imageUri);
            shareVidoeList = new ArrayList<>();
            shareVidoeList.add(localVideoInfo);
            setPreviewVideo(localVideoInfo);
        } else {
            shareVidoeList = new ArrayList<>();
            ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            for (Uri uri : uris) {
                if (uri != null) {
                    String content = uri.toString();
                    VideoFileInfo localVideoInfo = IMFileUtils.getVideoInfo(context, decodeUri(content));
                    if (localVideoInfo != null) {
                        shareVidoeList.add(localVideoInfo);
                    }
                }
            }
        }
    }

    /**
     * 设置小视频预览
     * @param videoFileInfo 文件信息
     */
    private void setPreviewVideo(VideoFileInfo videoFileInfo) {
        if (videoFileInfo != null) {
            String localPath = videoFileInfo.getFilePath();
            Bitmap bitmap = BitmapUtils.getZoomedDrawable(localPath, compressRatio);
            content_fl.setVisibility(View.VISIBLE);
            imageContent.setVisibility(View.VISIBLE);
            if (bitmap != null) {
                imageContent.setImageBitmap(bitmap);
            }
            return;
        }
        imageContent.setImageResource(R.drawable.pic_failed);
    }

    /**
     * 初始化Dialog
     *
     * @param context 上下文
     */
    @SuppressLint("InflateParams")
    private void initShareDialog(Context context) {
        LayoutInflater factory = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = factory.inflate(R.layout.share_pop_dialog, null);
        shareDialog = new CustomDialog(context).setView(view);
        shareDialog.setCanceledOnTouchOutside(false);
    }


    /**
     * 初始化各类组件
     *
     * @param shareDialog 弹窗
     */
    private void initView(CustomDialog shareDialog) {
        circleImageView = (CircleImageView) shareDialog.getView().findViewById(R.id.avatar_image);
        userName = (TextView) shareDialog.getView().findViewById(R.id.nick_txt);
        imageContent = (ImageView) shareDialog.getView().findViewById(R.id.imageContent);
        messageContent = (PastListenerEditText) shareDialog.getView().findViewById(R.id.messageContent);
        canleBtn = (TextView) shareDialog.getView().findViewById(R.id.cancel_btn);
        sendBtn = (TextView) shareDialog.getView().findViewById(R.id.send_btn);
        textContent = (TextView) shareDialog.getView().findViewById(R.id.textContent);
        content_fl = (FrameLayout) shareDialog.getView().findViewById(R.id.contentLayout);
        title_tv = (TextView) shareDialog.getView().findViewById(R.id.titleContent);
        topLayout = (LinearLayout) shareDialog.getView().findViewById(R.id.topLayout);
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        avatTop = (LinearLayout) shareDialog.getView().findViewById(R.id.avatar_ll);
        circleImageViews = new CircleImageView[9];
        circleImageViews[0] = (CircleImageView) shareDialog.getView().findViewById(R.id.a0);
        circleImageViews[1] = (CircleImageView) shareDialog.getView().findViewById(R.id.a1);
        circleImageViews[2] = (CircleImageView) shareDialog.getView().findViewById(R.id.a2);
        circleImageViews[3] = (CircleImageView) shareDialog.getView().findViewById(R.id.a3);
        circleImageViews[4] = (CircleImageView) shareDialog.getView().findViewById(R.id.a4);
        circleImageViews[5] = (CircleImageView) shareDialog.getView().findViewById(R.id.a5);
        circleImageViews[6] = (CircleImageView) shareDialog.getView().findViewById(R.id.a6);
        circleImageViews[7] = (CircleImageView) shareDialog.getView().findViewById(R.id.a7);
        circleImageViews[8] = (CircleImageView) shareDialog.getView().findViewById(R.id.a8);
        divider_iv = shareDialog.getView().findViewById(R.id.divider_iv);
        divider_iv2 = (ImageView) shareDialog.getView().findViewById(R.id.divider_iv2);
    }

    /**
     * 设置头像
     *
     * @param url            头像url
     * @param defaultImageId 默认头像
     */
    private void setCircleImageUrl(String url, int defaultImageId) {
        circleImageView.loadImage(url, true, defaultImageId);
    }

    /**
     * 显示九宫格头像
     *
     * @param nickNames 昵称
     * @param avaUrls 头像
     */
    private void setSudokuAvater(List<String> nickNames, List<String> avaUrls) {
        int avaCount = avaUrls.size();
        if (avaCount > 1) {
            avatTop.setVisibility(View.VISIBLE);
            for (int i = 0; i < avaCount; i++) {
                circleImageViews[i].setVisibility(View.VISIBLE);
                circleImageViews[i].loadImage(avaUrls.get(i), true, getDefaultImageId());
            }
        } else {
            topLayout.setVisibility(View.VISIBLE);
            userName.setText(nickNames.get(0));
            setCircleImageUrl(avaUrls.get(0), getDefaultImageId());
        }


    }

    /**
     * 获取默认的头像
     *
     * @return 默认头像ID
     */
    private int getDefaultImageId() {
        return R.drawable.corp_user_40dp;
    }

    /**
     * 获取华为手机联系人分享二维码的路径
     * @param displayName 二维码图名称
     * @return 路径
     */
    private String getContactFilesPath(String displayName) {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + CONTACT_FILES_PATH + displayName;
    }

    /**
     * 获取download apk中的图片分享路径
     * @param displayName 图片名称
     * @return 图片路径
     */
    private String getDownLoadImagePath(String displayName) {
        if (TextUtils.isEmpty(displayName)) {
            return null;
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + Environment.DIRECTORY_DOWNLOADS + File.separator + displayName;
    }

    /**
     * 显示弹窗
     */
    private void showDialog() {
        if (!shareDialog.isShowing()) {
            shareDialog.show();
        }
        if (shareDialog.isShowing()) {
            InputMethodManager imm = (InputMethodManager) messageContent.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(messageContent.getWindowToken(), 0);
            }
        }
    }

    /**
     * 隐藏窗口
     */
    public void dismissDialog() {

        if (messageContent != null) {
            InputMethodManager imm = (InputMethodManager) messageContent.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(messageContent.getWindowToken(), 0);
            }
        }
        if (shareDialog != null) {
            shareDialog.dismiss();
        }

    }



    /**
     * 显示分享完成后选择离开还是留下的dialog
     *
     * @param activity 相关的activity
     */
    @SuppressLint("InflateParams")
    public void selectActionPopWindow(Activity activity) {
        if (activity == null) {
            return;
        }
        LayoutInflater factory = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = factory.inflate(R.layout.select_action_dialog, null);
        selectActionDialog = new CustomDialog(activity).setView(view);
        selectActionDialog.setCanceledOnTouchOutside(false);
        selectActionDialog.setCancelable(false); //add by ycm 防止按返回键关闭了弹窗 20161205
        initSelectActionPop(activity);
        try {
            selectActionDialog.show();
        } catch (Exception e) {
            LogUtil.getUtils().e(e.getMessage());
        }
    }

    /**
     * 初始化选择去留dialog
     *
     * @param activity 相关的activity
     */
    private void initSelectActionPop(final Activity activity) {
        TextView leave_btn = (TextView) selectActionDialog.getView().findViewById(R.id.leave);
        TextView stay_btn = (TextView) selectActionDialog.getView().findViewById(R.id.stay);
        String staySource = activity.getString(R.string.stay)+"<sup>+</sup>";    //ConstDef.SHARE_STAY; //设置安通的“+”为上标
        String leaveSource = activity.getString(R.string.go_back)+"<sup> </sup>"; // 设置安通的空上标，为了和另个按钮保持一致
        Spanned staySpan = Html.fromHtml(staySource);
        Spanned leaveSpan = Html.fromHtml(leaveSource);
        stay_btn.setText(staySpan);
        leave_btn.setText(leaveSpan);
        leave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActionDialog.dismiss();// add by ycm for memory leak
                selectActionDialog = null;
                ActivityStack.getInstanse().goBackApp();//modified by ycm 20170204 for bug 8629 
            }
        });
        stay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(activity, ConstDef.MAINFREAME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // add by ycm for bug 5616、5654
                selectActionDialog.dismiss(); // add by ycm for memory leak
                selectActionDialog = null;
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    public interface PopWindowEvent<T> {

        /**
         * 图片分享
         *
         * @param dataSource        分享至的会话
         * @param localPictureInfos 分享的图片信息list
         * @param messageContent    留言
         */
        void shareImages(List<T> dataSource, List<LocalPictureInfo> localPictureInfos, String messageContent);

        /**
         * 图片分享
         *
         * @param dataSource     分享至的会话
         * @param text           分享的文本内容
         * @param messageContent 留言
         */
        void shareText(List<T> dataSource, String text, String messageContent);

        /**
         * 图片分享
         *
         * @param dataSource        转发至的会话
         * @param localPictureInfos 转发的图片信息list
         * @param messageContent    留言
         */
        void forwardImages(List<T> dataSource, List<FileInfo> localPictureInfos, String messageContent, boolean isOriginal);

        /**
         * 图片分享
         *
         * @param dataSource     转发至的会话
         * @param text           转发的文本内容
         * @param messageContent 留言
         */
        void forwardText(List<T> dataSource, String text, String messageContent);

        /**
         * 文件转发
         * @param dataSource 会话
         * @param localPictureInfos 本地图片信息
         * @param messageContent 留言
         */
        void forwardFile(List<T> dataSource, List<FileInfo> localPictureInfos, String messageContent);

        /**
         * 文件分享
         * @param dataSource 会话
         * @param localPictureInfos 本地图片信息
         * @param messageContent 留言
         */
        void shareFile(List<T> dataSource, List<LocalFileInfo> localPictureInfos, String messageContent);

        /**
         * 短视频分享
         *
         * @param dataSource        分享至的会话
         * @param localVideoInfos 分享的图片信息list
         * @param messageContent    留言
         */
        void shareVideos(List<T> dataSource, List<VideoFileInfo> localVideoInfos, String messageContent);

        /**
         * 短视频转发
         *
         * @param dataSource        转发至的会话
         * @param localVideoInfos 转发的图片信息list
         * @param messageContent    留言
         */
        void forwardVideos(List<T> dataSource, List<VideoFileInfo> localVideoInfos, String messageContent);
		
 		/**
         * 网页分享
         *
         * @param dataSource        分享至的会话
         * @param webPageInfos 		分享的图片信息list
         * @param messageContent    留言
         */
        void shareWebs(List<T> dataSource, List<WebPageInfo> webPageInfos, String messageContent);

 		/**
         * 网页转发
         *
         * @param dataSource        转发至的会话
         * @param webPageInfos 		转发的网页信息list
         * @param messageContent    留言
         */
        void forwardWebs(List<T> dataSource, List<WebPageInfo> webPageInfos, String messageContent);
    }

}
