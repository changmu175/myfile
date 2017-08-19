package com.xdja.imp.data.utils;

import android.content.*;
import android.net.Uri;
import android.text.TextUtils;
import com.xdja.imp.domain.model.*;

import java.util.List;

/**
 * 项目名称：ActomaV2
 * 类描述：网页分享工具类，提取网页分享时的各个数据
 * 创建人：yuchangmu
 * 创建时间：2017/1/5.
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ShareUtils {

    /**
     * 获取分享信息封装
     * */
    public static ShareInfo getShareInfo(Intent intent) {
        String mText = getText(intent);
        String mUrl = getLegalWebUrl(intent);
        String mUri = getLegalFileUri(intent);
        String mTitle = getTitle(intent);
        String mSource = getSource(intent);
        return new ShareInfo(mTitle, mText, mUrl, mUri, mSource);
    }

    /**
     * 获取来源
     * */
    private static String getSource(Intent intent) {
        String isUCM = intent.getStringExtra("isUCM");
        if (TextUtils.equals(isUCM, "true")) {
            return transformSource("isUCM");
        }
        return null;
    }

    /**
     * 获取内容
     * */
    private static String getText(Intent intent) {
        if (intent == null) {
            return null;
        }

        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (!TextUtils.isEmpty(text)) {
            return text;
        }

        String sms_body = intent.getStringExtra(ConstDef.SMS_BODY);
        if (!TextUtils.isEmpty(sms_body)) {
            return sms_body;
        }

        String weixin_text = intent.getStringExtra(ConstDef.WEIXIN_TEXT);
        if (!TextUtils.isEmpty(weixin_text)) {
            return weixin_text;
        }
        return null;
    }

    /**
     * 获取标题
     * */
    private static String getTitle(Intent intent) {
        if (intent == null) {
            return null;
        }
        String weixin_title = intent.getStringExtra(ConstDef.WEIXIN_TITLE);
        if (!TextUtils.isEmpty(weixin_title)) {
            return weixin_title;
        }

        String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        if (!TextUtils.isEmpty(subject)) {
            return subject;
        }

        String title = getText(intent);
        if (!TextUtils.isEmpty(title)) {
            return title;
        }
        return null;
    }

    /**
     * 获取合法的url
     * @param intent 含有url的intent
     * @return 合法的url
     */
    private static String getLegalWebUrl(Intent intent) {
        return checkWebUrl(getWebUrl(intent));
    }

    /**
     * 检查Url是否合法
     * @param url url
     * @return 1、null为不合法 2、url合法
     */
    private static String checkWebUrl(String url) {
        if (url == null) {
            return null;
        }
        List<HyperLinkBean> hyperLinkBeans  = HyperLinkUtil.parseContent(url, ConstDef.WEB_LINK);
        if (hyperLinkBeans.isEmpty()) {
            return null;
        }
        return url;
    }

    /**
     * 获取网页Url
     * @param intent 含有分享数据
     * @return url字符串
     */
    private static String getWebUrl(Intent intent) {
        String url = intent.getStringExtra(ConstDef.URL);
        if (url != null) {
            return url;
        }

        String web_url = intent.getStringExtra(ConstDef.WEB_URL);
        if (web_url != null) {
            return web_url;
        }

        String pageUrl = intent.getStringExtra("pageUrl");
        if (pageUrl != null) {
            return pageUrl;
        }

        String text = getText(intent);
        if (text != null) {
            if (isUri(text)) {
                return null;
            } else {
                //从分享的文本内容中获取url
                List<HyperLinkBean> linkBeanList = HyperLinkUtil.parseContent(text, ConstDef.WEB_LINK);
                if (!linkBeanList.isEmpty()) {
                    return linkBeanList.get(0).getHyperlink();
                }
            }
        }

        return null;
    }

    /**
     * 判断是否是Uri
     * @param content 文本内容
     * @return true 是Uri
     *          false 不是Uri
     */
    private static boolean isUri(String content) {
        Uri uri = Uri.parse(content);
        if (uri == null) {
            return false;
        }
        String scheme = uri.getScheme();
        if (TextUtils.isEmpty(scheme)) {
            return false;
        }

        return TextUtils.equals(scheme, ContentResolver.SCHEME_CONTENT) || TextUtils.equals(scheme, ContentResolver.SCHEME_FILE);
    }

    private static String getLegalFileUri(Intent intent) {
        return checkUri(getFileUri(intent));
    }

    /**
     * 获取文件Uri
     * @param intent 含有分享数据的intent
     * @return fileUri 字符串
     */
    private static String getFileUri(Intent intent) {
        Uri extraUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (extraUri != null) {
            return extraUri.toString();
        }

        Uri dataUri = intent.getData();
        if (dataUri != null) {
            return dataUri.toString();
        }

        String ucFileUri = intent.getStringExtra("file");
        if (ucFileUri != null) {
            return ucFileUri;
        }

        ClipData shareClip = intent.getClipData();
        if (shareClip == null) {
            return null;
        }

        ClipData.Item item = shareClip.getItemAt(0);
        if (item == null) {
            return null;
        }

        Uri itemUri = item.getUri();
        if (itemUri != null) {
            return itemUri.toString();
        }

        return null;
    }

    /**
     * 检测是否是uri
     * @param uri uri
     * @return 合法的uri
     */
    private static String checkUri(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }
        if (!uri.startsWith(ContentResolver.SCHEME_FILE) && !uri.startsWith(ContentResolver.SCHEME_CONTENT)) {
            uri = "file://" + uri;
        }

        return uri;
    }

    /**
     * 获取网页分享信息
     * @param context 上下文
     * @param shareInfo 分享信息
     * @return 网页
     */
    public static WebPageInfo getShareWebInfo(Context context, ShareInfo shareInfo) {
        WebPageInfo webPageInfo = new WebPageInfo();
        webPageInfo.setTitle(shareInfo.getTitle());
        webPageInfo.setDescription(shareInfo.getContent());
        webPageInfo.setWebUri(shareInfo.getWebUrl());
        if (shareInfo.getFileUri() == null) {
            return webPageInfo;
        }

        LocalFileInfo localFileInfo = IMFileUtils.queryLocalFiles(context, Uri.parse(Uri.decode(shareInfo.getFileUri())));

        if (localFileInfo == null) {
            return webPageInfo;
        }

        webPageInfo.setFileName(localFileInfo.getFileName());
        webPageInfo.setFilePath(localFileInfo.getFilePath());
        webPageInfo.setFileSize(localFileInfo.getFileSize());
        webPageInfo.setFileType(localFileInfo.getFileType());
        String suffix = IMFileUtils.getSuffixFromFilepath(localFileInfo.getFilePath());
        webPageInfo.setSuffix(suffix);
        webPageInfo.setFileType(localFileInfo.getFileType());
        return webPageInfo;
    }

    /**
     * 从两个数据中选择一个合适的数据
     * @param str1 字符1
     * @param str2 字符2
     * @return 字符
     */
    private static String dealText(String str1, String str2) {
        String text = null;
        if (!TextUtils.isEmpty(str1)) {
            text = str1;
        } else if (!TextUtils.isEmpty(str2)) {
            text = str2;
        }
        return text;
    }

    private static String transformSource(String source) {
        if (TextUtils.equals(source, "isUCM")) {
            return "UC";
        }
        return null;
    }

}
