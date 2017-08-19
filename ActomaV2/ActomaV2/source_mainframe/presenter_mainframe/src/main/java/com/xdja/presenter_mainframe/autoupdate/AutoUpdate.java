package com.xdja.presenter_mainframe.autoupdate;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.FreshUpdateNewEvent;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.comm.uitl.NotifiParamUtil;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.util.Function;
import com.xdja.presenter_mainframe.util.SharePreferceUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 用于版本升级的检测、解析、下载
 * Modify by LiXiaolong on 2016/08/10.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class AutoUpdate {

    /**
     * ------------更新提示码----------------
     */
    //正常更新 暂不更新
    public static final int UPDATE_CANCEL = -1;
    //强制更新 暂不更新
    public static final int UPDATE_FORCDE_CANCEL = -9;
    //检测更新失败
    public static final int UPDATE_CODE_ERROR = -10;
    //暂无更新或检测更新失败的提示码
    public static final int UPDATE_CODE_NO_NEW = -2;
    //更新下载失败
    public static final int UPDATE_DOWNLOAD_FAIL = -3;
    //强制更新下载失败
    public static final int UPDATE_FORCE_DOWNLOAD_FAIL = -8;
    //开始升级
    public static final int UPDATE_INSTALL = -4;
    //有更新的提示码
    public static final int UPDATE_CODE_NEW = -5;
    //有强制更新的提示码
    public static final int UPDATE_FORCE_CODE_NEW = -7;

    public static final String ACTION_CHECK_UPDATE = "com.xdja.actoma.service.versionupdate";
    public static final String ACTION_CHECK_UPDATE_TIMER = "com.xdja.actoma.update";

    public static final int ONE_K = 1024;
    /**
     * 升级通知id
     */
    //[S]remove by xienana for notification id move to NotifiParamUtil@2016/10/11 [review by tangsha]
    //   public static final int NOTIFICATION_ID = 0x10010;
    //[S]remove by xienana for notification id move to NotifiParamUtil@2016/10/11 [review by tangsha]

    public static final int REQCODE = 0x10010;

    private final String TAG = "AutoUpdate >>> ";
    // 本地版本信息
    private String serverIP = ""; // 升级服务器IP
    private String serverPort = "";// 升级服务端端口
    private String factory = ""; // 手机厂商
    private String mod = ""; // 手机型号
    private String os = ""; // 手机操作系统
    private String soft = ""; // 升级软件名称
    private String username = "";// 用户
    private ArrayList<VersionInfo> versionList = new ArrayList<>();// 版本历史升级列表

    // 升级配置信息
    private String version = ""; // 最新版本号
    private String date = ""; // 版本发布时间
    private String comment = ""; // 注释信息
    private String updatetag = "";// 更新类型
    private String deleteDb = "";// 是否删除数据库checkCode
    private String checkCode = "";// 版本内容校验码

    private String localFileDir = null;
    private ArrayList<FileInfo> fileList = new ArrayList<>();

    private UpdateSocket socket = null;// 网络监听
    private Context context = null;
    private UpdateListener updateListener = null;
    /**
     * 强制更新提示语
     */
    private String dialogMessage = "";

    private NotificationManager mNotificationManager;

    private boolean isCancel = false;

    private final String TIP_UPDATE = "0";

    private static final String SERVER_IP_ERROR = "serverIpError";
    private static final String SERVER_PORT_ERROR = "serverPortError";
    private static final String CONNECT_FAILURED = "connectFailured";
    private static final String REQUEST_FAILURED = "requestFailured";
    private static final String RECEIVE_CFG_TIMEOUT = "receiveCfgTimeout";
    private static final String NOT_HAVE_UPDATE = "notHaveUpdate";
    private static final String CANCELED = "canceled";
    private static final String UNKNOWN_ERROR = "unKnown";
    private static final String SOCKET_CLOSED = "socketClosed";
    private static final String RECEIVED_TIMEOUT = "receivedTimeout";
    private static final String FILE_INCOMPLETED = "fileIncompleted";
    private static final String CONFIRM_INFO_ERROR = "confirmInfoError";

    public AutoUpdate(Context context, UpdateListener uplisten) {
        this.context = context;
        //[S]add by lixiaolong on 20160921. fix bug 4290. review by myself.
        SharePreferceUtil.getPreferceUtil(context).setNewVersion("");
        //[E]add by lixiaolong on 20160921. fix bug 4290. review by myself.
        this.localFileDir = context.getFilesDir().getAbsolutePath();
        this.updateListener = uplisten;
        mNotificationManager = (NotificationManager) ActomaApplication.getInstance()
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 清除升级信息提示栏
     */
    public void clearNotify() {
        try {
            //[S]modify by xienana for notification id @2016/10/11 [review by tangsha]
            mNotificationManager.cancel(NotifiParamUtil.ANTONG_UPDATE_NOTIFI_ID);
            //[E]modify by xienana for notification id @2016/10/11 [review by tangsha]
        } catch (Exception e) {
            LogUtil.getUtils().e(TAG + "清除提示栏升级信息失败 -->" + e.getMessage());
        }
    }

    public void clearNewRed(){
        SharePreferceUtil.getPreferceUtil(ActomaApplication.getInstance()).setNewVersion("");
        FreshUpdateNewEvent event = new FreshUpdateNewEvent();
        event.setIsHaveUpdate(false);
        BusProvider.getMainProvider().post(event);
    }

    /**
     * 开始进行升级检测
     *
     */
    public void updateStart() {
        new CheckVersionTask().execute();
    }

    /**
     * 调用强制升级程序（自定义提示信息）入口
     *
     */
    public void updateStartWithForceMessage(String dialogmessage) {
        dialogMessage = dialogmessage;
        updateStart();
    }

    /**
     * 展示更新提示信息
     * （适用场景 不知后台还是前台更新的提示 ）
     */
    public void showUpdateMessage() {
        if (TIP_UPDATE.equals(updatetag)) {// 提示版本升级
            doNewVersionUpdate();
        } else {
            doNewVersionForceUpdate(); // 提示强制版本升级
        }
    }

    /**
     * 创建本地版本信息
     */
    private void createVersionInfo(Context mContext, String filename) {

        if (!new File(filename).exists()) {

            InputStream in;
            FileOutputStream out;

            try {
                in = mContext.getResources().getAssets().open("ClientVer.xml");
                out = mContext.openFileOutput("ClientVer.xml", Context.MODE_PRIVATE);
                byte[] buffer = new byte[ONE_K*8];
                int count;
                // 开始复制文件
                while ((count = in.read(buffer)) > 0) {
                    out.write(buffer, 0, count);
                    out.flush();
                }
                in.close();
                out.close();
            } catch (Exception ioe) {
                ioe.printStackTrace();
                UpdateFunc.popAlert(mContext,
                        mContext.getString(R.string.update_dialog_title_tip),
                        mContext.getString(R.string.update_dialog_msg_init_fail));
                updateListener.handerResult(UPDATE_CODE_NO_NEW, "", AutoUpdate.this);
            }
        }
    }

    /*
     * 解析版本信息
     */
    private void parseVersionInfo(String filename) {
        versionList.clear(); // 在未配置升级信息强进入升级程序会有数据，故清空数据。
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            FileInputStream in = new FileInputStream(filename);
            Document doc = docBuilder.parse(in);
            Element root = doc.getDocumentElement();

            NodeList nodeList = root.getElementsByTagName("ServerIP");
            if (nodeList.item(0).getFirstChild() != null) {
                serverIP = nodeList.item(0).getFirstChild().getNodeValue();// 取升级服务器IP
            }

            nodeList = root.getElementsByTagName("ServerPort");
            if (nodeList.item(0).getFirstChild() != null) {
                serverPort = nodeList.item(0).getFirstChild().getNodeValue();// 取升级服务器Port
            }

            nodeList = root.getElementsByTagName("Factory");
            if (nodeList.item(0).getFirstChild() != null) {
                factory = nodeList.item(0).getFirstChild().getNodeValue();// 取手机厂商
            }

            nodeList = root.getElementsByTagName("Mod");
            if (nodeList.item(0).getFirstChild() != null) {
                mod = nodeList.item(0).getFirstChild().getNodeValue();// 取手机型号
            }

            nodeList = root.getElementsByTagName("OS");
            if (nodeList.item(0).getFirstChild() != null) {
                os = nodeList.item(0).getFirstChild().getNodeValue();// 取手机系统
            }

            nodeList = root.getElementsByTagName("Soft");
            if (nodeList.item(0).getFirstChild() != null) {
                soft = nodeList.item(0).getFirstChild().getNodeValue();// 取升级软件名称
            }

            nodeList = root.getElementsByTagName("UserName");
            if (nodeList.item(0).getFirstChild() != null) {
                username = nodeList.item(0).getFirstChild().getNodeValue();// 取用户名
            }

            nodeList = root.getElementsByTagName("Ver");// 取升级版本历史信息
            int nodeNum = nodeList.getLength();

            Element childElement;
            NodeList childList;

            for (int i = 0; i < nodeNum; i++) {
                VersionInfo vi = new VersionInfo();
                childElement = (Element) nodeList.item(i);
                childList = childElement.getElementsByTagName("Version");
                vi.version = childList.item(0).getFirstChild().getNodeValue();// 取版本信息

                childList = childElement.getElementsByTagName("Date");
                vi.date = childList.item(0).getFirstChild().getNodeValue();// 取版本信息

                childList = childElement.getElementsByTagName("Note");
                if (childList.item(0).getFirstChild() != null) {
                    vi.note = childList.item(0).getFirstChild().getNodeValue();// 取版本信息
                }
                versionList.add(vi);// 添加版本信息到链表中
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     * 解析版本升级确认信息
     */
    private int parseConfirmInfo(String confirmInfo) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            ByteArrayInputStream stream = new ByteArrayInputStream(confirmInfo.getBytes());
            Document doc = docBuilder.parse(stream);
            Element root = doc.getDocumentElement();

            NodeList nodeList = root.getElementsByTagName("Result");
            if (nodeList.item(0).getFirstChild() != null) {
                String result = nodeList.item(0).getFirstChild().getNodeValue();// 取版本升级确认信息
                return Integer.parseInt(result);
            } else {
                return -1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    /*
     * 解析版本升级配置信息
     */
    private boolean parseConfigInfo(String filename) {
        boolean res;
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            FileInputStream in = new FileInputStream(localFileDir + "/" + filename);
            Document doc = docBuilder.parse(in);
            Element root = doc.getDocumentElement();

            NodeList nodeList = root.getElementsByTagName("Version");
            if (nodeList.item(0).getFirstChild() != null) {
                version = nodeList.item(0).getFirstChild().getNodeValue();// 取升级版本号
            }

            nodeList = root.getElementsByTagName("DeleteDb");
            if (nodeList.item(0).getFirstChild() != null) {
                deleteDb = nodeList.item(0).getFirstChild().getNodeValue();// 是否删除数据库
                // 0
                // 不删除，1删除
            }

            nodeList = root.getElementsByTagName("CheckCode");
            if (nodeList.item(0).getFirstChild() != null) {
                checkCode = nodeList.item(0).getFirstChild().getNodeValue();// 版本内容校验码
            }

            nodeList = root.getElementsByTagName("Date");
            if (nodeList.item(0).getFirstChild() != null) {
                date = nodeList.item(0).getFirstChild().getNodeValue();// 取升级版本发布时间
            }

            nodeList = root.getElementsByTagName("Comment");
            if (nodeList.item(0).getFirstChild() != null) {
                comment = nodeList.item(0).getFirstChild().getNodeValue();// 取升级版本注释信息
            }

            nodeList = root.getElementsByTagName("UpdateTag");
            if (nodeList.item(0).getFirstChild() != null) {
                updatetag = nodeList.item(0).getFirstChild().getNodeValue();// 取升级版本更新类型
            }

            nodeList = root.getElementsByTagName("File");// 取升级版本文件列表
            int nodeNum = nodeList.getLength();

            Element childElement;
            NodeList childList;
            for (int i = 0; i < nodeNum; i++) {
                FileInfo fi = new FileInfo();
                childElement = (Element) nodeList.item(i);
                childList = childElement.getElementsByTagName("RPath");
                fi.rpath = childList.item(0).getFirstChild().getNodeValue();// 远程文件所在路径

                childList = childElement.getElementsByTagName("LPath");
                fi.lpath = childList.item(0).getFirstChild().getNodeValue();// 本地文件所在路径

                childList = childElement.getElementsByTagName("FName");
                if (childList.item(0).getFirstChild() != null) {
                    fi.filename = childList.item(0).getFirstChild()
                            .getNodeValue();// 文件名
                }

                childList = childElement.getElementsByTagName("FSize");
                if (childList.item(0).getFirstChild() != null) {
                    fi.filesize = Integer.parseInt(childList.item(0)
                            .getFirstChild().getNodeValue());// 文件大小
                }

                childList = childElement.getElementsByTagName("Action");
                if (childList.item(0).getFirstChild() != null) {
                    fi.action = Integer.parseInt(childList.item(0)
                            .getFirstChild().getNodeValue());// 动作类型
                }

                childList = childElement.getElementsByTagName("State");
                if (childList.item(0).getFirstChild() != null) {
                    fi.state = Integer.parseInt(childList.item(0)
                            .getFirstChild().getNodeValue());// 当前状态
                }

                childList = childElement.getElementsByTagName("CSize");
                if (childList.item(0).getFirstChild() != null) {
                    fi.csize = Integer.parseInt(childList.item(0)
                            .getFirstChild().getNodeValue());// 当前已经下载大小
                }
                fileList.clear();
                fileList.add(fi);// 添加文件信息到链表中
            }
            res = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            res = false;
        }
        return res;
    }

    /**
     * 重写版本信息
     */
    private void rewriteVersionInfo(String filename) {
        StringBuilder verstr = new StringBuilder();
        verstr.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        verstr.append("<Root>");

        verstr.append("<ServerIP>").append(serverIP).append("</ServerIP>");
        verstr.append("<ServerPort>").append(serverPort).append("</ServerPort>");
        verstr.append("<Factory>").append(factory).append("</Factory>");
        verstr.append("<Mod>").append(mod).append("</Mod>");
        verstr.append("<OS>").append(os).append("</OS>");
        verstr.append("<Soft>").append(soft).append("</Soft>");
        verstr.append("<UserName>").append(username).append("</UserName>");
        int versionListNum = versionList.size();
        VersionInfo vi;
        for (int i = 0; i < versionListNum; i++) {// 版本升级历史信息
            vi = versionList.get(i);
            verstr.append("<Ver>");
            verstr.append("<Version>").append(vi.version).append("</Version>");
            verstr.append("<Date>").append(vi.date).append("</Date>");
            verstr.append("<Note>").append(vi.note).append("</Note>");
            verstr.append("</Ver>");
        }
        verstr.append("</Root>");
        LogUtil.getUtils().i(TAG + "检测升级的信息 --> " + verstr);
        UpdateFunc.WriteFile(filename, verstr.toString(), false);// 初始化客户端版本信息
    }

//    /**
//     * 重写版本升级配置信息
//     */
//    private void ReWriteConfigInfo(String filename) {
//        StringBuilder getfile = new StringBuilder();
//        getfile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//        getfile.append("<Root>");
//
//        getfile.append("<Version>").append(version).append("</Version>");
//        getfile.append("<Date>").append(date).append("</Date>");
//        getfile.append("<Comment>").append(comment).append("</Comment>");
//        getfile.append("<UpdateTag>").append(updatetag).append("</UpdateTag>");
//        getfile.append("<DeleteDb>").append(deleteDb).append("</DeleteDb>");
//        getfile.append("<CheckCode>").append(checkCode).append("</CheckCode>");
//
//        getfile.append("<Files>");
//
//        int fileListNum = fileList.size();
//        FileInfo fi;
//        for (int i = 0; i < fileListNum; i++) {// 文件列表
//            fi = fileList.get(i);
//            getfile.append("<File>");
//            getfile.append("<RPath>").append(fi.rpath).append("</RPath>");
//            getfile.append("<LPath>").append(fi.lpath).append("</LPath>");
//            getfile.append("<FName>").append(fi.filename).append("</FName>");
//            getfile.append("<FSize>").append(fi.filesize).append("</FSize>");
//            getfile.append("<Action>").append(fi.action).append("</Action>");
//            getfile.append("<State>").append(fi.state).append("</State>");
//            getfile.append("<CSize>").append(fi.csize).append("</CSize>");
//            getfile.append("</File>");
//        }
//
//        getfile.append("</Files>");
//        getfile.append("</Root>");
//
//        UpdateFunc.WriteFile(filename, getfile.toString(), false);// 重写版本升级配置信息
//    }


    /**
     * 强制更新的提示信息
     */
    private void doNewVersionForceUpdate() {
        //取得提示的message信息
        String message = context.getResources().getString(R.string.update_force_message);
        if (!TextUtils.isEmpty(dialogMessage)) {
            message = dialogMessage;
        }
        final CustomDialog dialog = new CustomDialog(context);
        dialog.setTitle(R.string.update_dialog_title)
                .setMessage(message)
                        // 设置内容
                .setPositiveButton(context.getString(R.string.update_dialog_btn_update),// 设置确定按钮
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();// 关闭进度对话框
                                clearNotify();
                                clearNewRed();
                                checkWifiDialog(true);
                            }
                        })
                .setNegativeButton(context.getString(R.string.update_dialog_btn_cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();// 关闭进度对话框
                                //一旦选择升级，就清除通知栏升级通知信息
                                clearNotify();
                                clearNewRed();
                                closeSocket();// 关闭本次连接
                                updateListener.handerResult(UPDATE_FORCDE_CANCEL, "", AutoUpdate.this);// 暂不更新
                            }
                        })
                .setCancelable(false)
                .show();
    }

    /**
     * 新版本正常下载提示信息
     */
    private void doNewVersionUpdate() {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.update_dialog_msg_start));
        sb.append(version);
        sb.append(context.getString(R.string.update_dialog_msg_end));
        LogUtil.getUtils().i(TAG + sb + "\n" + comment);
        final CustomDialog dialog = new CustomDialog(context);
        dialog.setTitle(R.string.update_dialog_title)
                .setMessage(sb + "\n" + comment)
                .setPositiveButton(context.getString(R.string.update_dialog_btn_update),// 设置确定按钮
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();// 关闭进度对话框
                                clearNotify();
                                clearNewRed();
                                checkWifiDialog(false);
                            }
                        })
                .setNegativeButton(context.getString(R.string.update_dialog_btn_cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();// 关闭进度对话框
                                //一旦选择升级，就清除通知栏升级通知信息
                                clearNotify();
                                clearNewRed();
                                closeSocket();// 关闭本次连接
                                updateListener.handerResult(UPDATE_CANCEL, "", AutoUpdate.this);// 暂不更新
                            }
                        })
                .setCancelable(false)
                .show();
    }

    /**
     * 根据网络信息判断是否进行费流量的提示
     *
     * @param isForceUpdate 是否为强制更新
     */
    private void checkWifiDialog(final boolean isForceUpdate) {
        boolean isMobCon = Function.isMobConnect(context);
        if (!isMobCon) {
            startDownload();
        } else {
            //检测是否为2G/3G/4G网络
            final CustomDialog dialog = new CustomDialog(context);
            dialog.setTitle(R.string.update_dialog_title)
                    .setMessage(R.string.update_dialog_msg_net_type)
                            // 设置内容
                    .setPositiveButton(context.getString(R.string.update_dialog_btn_continue),// 设置确定按钮
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();// 关闭进度对话框
                                    //一旦选择升级，就清除通知栏升级通知信息
                                    clearNotify();
                                    clearNewRed();
                                    startDownload();
                                }
                            })
                    .setNegativeButton(context.getString(R.string.update_dialog_btn_cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();// 关闭进度对话框
                                    clearNotify();
                                    clearNewRed();
                                    closeSocket();// 关闭本次连接
                                    if (isForceUpdate) {
                                        updateListener.handerResult(UPDATE_FORCDE_CANCEL, "", AutoUpdate.this);// 暂不更新
                                    } else {
                                        updateListener.handerResult(UPDATE_CANCEL, "", AutoUpdate.this);// 暂不更新
                                    }
                                }
                            })
                    .setCancelable(false)
                    .show();
        }
    }

    /**
     * 下载升级信息任务
     */
    private class CheckVersionTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String error) {
            if (error != null) {// 申请验证或下载升级信息失败
                if (NOT_HAVE_UPDATE.equals(error)) { // 没有版本更新
                    LogUtil.getUtils().e(TAG + "申请验证或下载升级信息成功 - 无升级版本");
                    updateListener.handerResult(UPDATE_CODE_NO_NEW, "", AutoUpdate.this);
                } else {
                    LogUtil.getUtils().e(TAG + "申请验证或下载升级信息失败 - error=" + error);
                    updateListener.handerResult(UPDATE_CODE_ERROR, error, AutoUpdate.this);
                }
            } else { // 申请验证和下载升级信息成功
                LogUtil.getUtils().i(TAG + "正在解析update_s.xml文件......");
                if (!parseConfigInfo("update_s.xml")) {
                    // 删除update_s.xml文件
                    deleteFile(new File(localFileDir + "/update_s.xml"));
                    updateListener.handerResult(UPDATE_CODE_NO_NEW, "", AutoUpdate.this);// 解析update_s.xml失败
                    LogUtil.getUtils().e(TAG + "解析update_s.xml文件失败");
                    return;
                }
                LogUtil.getUtils().i(TAG + "解析update_s.xml文件成功");
                if (TIP_UPDATE.equals(updatetag)) {// 提示版本升级
                    updateListener.handerResult(UPDATE_CODE_NEW, version, AutoUpdate.this);
                } else {
                    updateListener.handerResult(UPDATE_FORCE_CODE_NEW, version, AutoUpdate.this);
                }
            }
        }

        @SuppressWarnings("ReturnOfNull")
        @Override
        protected String doInBackground(String... params) {
            // 生成客户端文件
            createVersionInfo(context, localFileDir + "/ClientVer.xml");
            // 解析文件
            parseVersionInfo(localFileDir + "/ClientVer.xml");
            // 修正记录的版本升级列表，如果版本相等不修正，若不等就删除最后一个节点
            String verName = "";
            try {
                verName = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0).versionName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            if (versionList.size() > 0) {
                VersionInfo vi = versionList.get(versionList.size() - 1);
                if (!verName.equals(vi.version) && versionList.size() > 1) {
                    versionList.remove(versionList.size() - 1);
                    rewriteVersionInfo(localFileDir + "/ClientVer.xml");
                }
            }
            String cardId = TFCardManager.getTfCardId();
            LogUtil.getUtils().i(TAG + "获取到的卡号 --> " + cardId);
            StringBuilder checkVer = new StringBuilder();
            checkVer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            checkVer.append("<Root>");
            checkVer.append("<Req>checkver</Req>");
            checkVer.append("<Factory>").append(factory).append("</Factory>");
            checkVer.append("<Mod>").append(mod).append("</Mod>");
            checkVer.append("<OS>").append(os).append("</OS>");
            checkVer.append("<Soft>").append(soft).append("</Soft>");
            checkVer.append("<Version>").append(verName).append("</Version>");
            checkVer.append("<UserName>").append(username).append("</UserName>");
            checkVer.append("<CardNo>").append(cardId).append("</CardNo>");
            checkVer.append("</Root>");
            if (serverIP.equals("")) {
                return SERVER_IP_ERROR;
            }
            if (serverPort.equals("")) {
                return SERVER_PORT_ERROR;
            }
            LogUtil.getUtils().i(TAG + "检测升级的信息 --> " + checkVer);
            socket = new UpdateSocket(serverIP, Integer.parseInt(serverPort));
            int nres = socket.connect();
            if (nres != 0) {
                socket = null;
                return CONNECT_FAILURED;
            }
            nres = socket.sendData(checkVer.toString());
            if (nres != 0) {
                return REQUEST_FAILURED;
            }
            String result = socket.recvData();
            if (result == null) {
                return RECEIVE_CFG_TIMEOUT;
            } else {
                nres = parseConfirmInfo(result);
                LogUtil.getUtils().i(TAG + "解析升级信息回传结果 --> " + nres);
                switch (nres) {
                    case 0:
                        result = socket.recvData();
                        LogUtil.getUtils().i(TAG + "升级配置信息 --> " + result);
                        if (result == null) {
                            return RECEIVED_TIMEOUT;
                        } else {
                            UpdateFunc.WriteFile(localFileDir + "/update_s.xml", result, false);// 把升级配置信息写入本地文件update_s.xml
                        }
                        break;
                    case 1: // 无版本更新
                        return NOT_HAVE_UPDATE;
                    case 2:// 当前终端无配置信息
                    case 3:// 查询升级配置失败
                    case 4:// 升级配置文件不存在
                    case -1:// 无法解析升级确认信息
                        return UNKNOWN_ERROR;
                }
            }
            return null;
        }
    }

    private void startDownload(){
        if (fileList != null && fileList.size() > 0) {
            for (FileInfo fi : fileList) {
                File file = new File(localFileDir + "/" + fi.filename);
                if (file.exists()) {
                    try {
                        String code = getHash(localFileDir + "/" + fi.filename, "MD5");
                        LogUtil.getUtils().i(TAG + "exists file - code=" + code + " - checkcode=" + checkCode);
                        if (!checkCode.equals(code)) {
                            // 删除已下载的不完整文件
                            file.delete();
                        } else {
                            //[S]modify by lixiaolong on 20160906. fix error parsing package. review by myself.
                            installApk(file.getAbsolutePath());
                            //[E]modify by lixiaolong on 20160906. fix error parsing package. review by myself.
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        file.delete();
                    }
                }
            }
        }
        new DownloadTask().execute();
    }

    /**
     * 下载升级文件任务
     */
    private class DownloadTask extends AsyncTask<String, Integer, String> {
        private CustomDialog pDialog = null;
        private ProgressBar progressBar = null;

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public void setProgressBar(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        private void showErrorDialog(int msgId){
            if (msgId <= 0) {
                msgId = R.string.update_dialog_msg_download_fail;
            }
            try {
                final CustomDialog dialog = new CustomDialog(context);
                dialog.setTitle(R.string.update_dialog_title_tip)
                        .setMessage(msgId)
                        .setNegativeButton(context.getString(R.string.update_dialog_btn_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                // 文件下载失败
                                if (TIP_UPDATE.equals(updatetag)) {// 提示版本升级
                                    updateListener.handerResult(UPDATE_DOWNLOAD_FAIL, "", AutoUpdate.this);
                                } else {
                                    updateListener.handerResult(UPDATE_FORCE_DOWNLOAD_FAIL, "", AutoUpdate.this);
                                }
                            }
                        })
                        .setCancelable(false)
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            pDialog = new CustomDialog(context);
            View customView = LayoutInflater.from(context).inflate(R.layout.dialog_progress_dialog, null);
            progressBar = (ProgressBar) customView.findViewById(R.id.progress);
            setProgressBar(progressBar);
            pDialog.setMessage(R.string.update_dialog_downloading)
                    .setNegativeButton(context.getString(R.string.update_dialog_btn_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pDialog.dismiss();
                            isCancel = true;
                        }
                    })
                    .setCancelable(false)
                    .setCustomContentView(customView)
                    .show();
        }

        @SuppressWarnings("CallToRuntimeExecWithNonConstantString")
        @Override
        protected void onPostExecute(String error) {
            // 关闭进度对话框
            if (pDialog != null) {
                pDialog.dismiss();
            }
            // 关闭本次连接
            closeSocket();
            if (error != null) {// 下载升级文件失败
                LogUtil.getUtils().i(TAG + "download failured - error=" + error);
                if (CANCELED.equals(error) && isCancel) {
                    isCancel = false;
                    if (TIP_UPDATE.equals(updatetag)) {// 提示版本升级
                        updateListener.handerResult(UPDATE_CANCEL, "", AutoUpdate.this);
                    } else {
                        updateListener.handerResult(UPDATE_FORCDE_CANCEL, "", AutoUpdate.this);
                    }
                } else {
                    showErrorDialog(R.string.update_dialog_msg_download_fail);
                }
            } else {// 下载升级文件成功
                LogUtil.getUtils().i(TAG + "download success");
                for (FileInfo fi : fileList) {
                    // 修改文件给与其他包读的权限
                    String apkPath = localFileDir + "/" + fi.filename;
                    try {
                        Runtime.getRuntime().exec("chmod 644 " + apkPath);
                        LogUtil.getUtils().i(TAG + "apk文件权限修改成功");
                    } catch (Exception e) {
                        LogUtil.getUtils().i(TAG + "apk文件权限修改失败");
                        e.printStackTrace();
                    }
                    // 执行升级文件
                    if (fi.action == 0) {
                        installApk(apkPath);
                    }
                }
                // 删除update_s.xml文件
                deleteFile(new File(localFileDir + "/update_s.xml"));
                // 更新版本升级配置文件
                VersionInfo ver = new VersionInfo();
                ver.version = version;
                ver.date = date;
                ver.note = comment;
                versionList.add(ver);
                rewriteVersionInfo(localFileDir + "/ClientVer.xml");
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 更新进度
            if (getProgressBar() != null) {
                getProgressBar().setProgress(values[0]);
            }
        }

        @SuppressWarnings({"ReturnOfNull", "NumericCastThatLosesPrecision"})
        @Override
        protected String doInBackground(String... params) {
            File updateXml = new File(localFileDir + "/update_s.xml");
            int filesCount = fileList.size();
            // 如果update_s.xml文件信息为空则无效，删掉（写update_s.xml文件没有写完全会出现该情况）。
            if (filesCount <= 0) {
                deleteFile(updateXml);
                return NOT_HAVE_UPDATE;
            }
            // 保存是否删除数据库和当前升级到的版本号
            SharePreferceUtil.getPreferceUtil(context).setIsDeleteDb(deleteDb.equals("1"));
            SharePreferceUtil.getPreferceUtil(context).setDeleteVersion(version);
            // 开始下载
            for (FileInfo fi : fileList) {
                String apkPath = localFileDir + "/" + fi.filename;
                File apkFile = new File(apkPath);
                if (fi.csize < fi.filesize) {
                    String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                            "<Root>" +
                            "<Req>getfile</Req>" +
                            "<RPath>" + fi.rpath + "</RPath>" +
                            "<LPath>" + fi.lpath + "</LPath>" +
                            "<FName>" + fi.filename + "</FName>" +
                            "<FSize>" + fi.filesize + "</FSize>" +
                            "<FPos>" + fi.csize + "</FPos>" +
                            "</Root>";
                    LogUtil.getUtils().i(TAG + "getfile - request=" + request);
                    if (socket == null) {// 未连接网络
                        if (TextUtils.isEmpty(serverIP)) {
                            return SERVER_IP_ERROR;
                        }
                        if (TextUtils.isEmpty(serverPort)) {
                            return SERVER_PORT_ERROR;
                        }
                        socket = new UpdateSocket(serverIP, Integer.parseInt(serverPort));
                        int connState = socket.connect();
                        if (connState != 0) {
                            socket = null;
                            deleteFile(updateXml);
                            return CONNECT_FAILURED;
                        }
                    }
                    int sendState = socket.sendData(request);
                    if (sendState != 0) {
                        deleteFile(updateXml);
                        return REQUEST_FAILURED;
                    }
                    String result = socket.recvData();
                    if (TextUtils.isEmpty(result)) {
                        deleteFile(updateXml);
                        return RECEIVE_CFG_TIMEOUT;
                    } else {
                        LogUtil.getUtils().i(TAG + "getfile - response=" + result);
//[S]modify by LiXiaolong on 20160824. fix bug 3329. review by wangchao1.
                        int res = parseConfirmInfo(result);
                        if (res == 0) {
                            FileOutputStream out = null;
                            byte[] buffer = new byte[ONE_K*10];// 10KB缓冲区
                            try {
                                out = new FileOutputStream(apkPath, true);
                                while (fi.csize < fi.filesize) {
                                    if (isCancel) {
                                        closeSocket();
                                        return CANCELED;
                                    }
                                    if (socket == null) {
                                        return SOCKET_CLOSED;
                                    }
                                    int len = socket.sin.read(buffer, 0, ONE_K*10);
                                    if (len <= 0) {
                                        deleteFile(updateXml);
                                        return RECEIVED_TIMEOUT;
                                    } else {
                                        out.write(buffer, 0, len);
                                        fi.csize += len;
                                        int percent = (int) (fi.csize / (float) fi.filesize * 100);
                                        publishProgress(percent);
                                        LogUtil.getUtils().i(TAG + "download progress - " + fi.csize + " - " + fi.filesize + " - " + percent + "%");
                                        // 判断是否下载完成
                                        if (fi.csize >= fi.filesize) {
                                            deleteFile(updateXml);
                                            // 利用校验码进行验证
                                            try {
                                                String code = getHash(apkPath, "MD5");
                                                if (!checkCode.equals(code)) {
                                                    deleteFile(apkFile);
                                                    fi.state = 0;
                                                    fi.csize = 0;
                                                    return FILE_INCOMPLETED;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                deleteFile(apkFile);
                                                return FILE_INCOMPLETED;
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                deleteFile(apkFile);
                                deleteFile(updateXml);
                                return CONFIRM_INFO_ERROR;
                            } finally {
                                try {
                                    if (out != null) {
                                        out.flush();
                                        out.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {// 1:文件不存在；2:读取文件失败；3:文件大小不附；4:指定偏移量无效；5:参数不完整
                            // 删除update_s.xml文件,否则一次不成功就保留该文件无法更新。
                            deleteFile(updateXml);
                            return CONFIRM_INFO_ERROR;
                        }

//                        switch (res) {
//                            case 0:
//                                while (fi.csize < fi.filesize) {
//                                    byte[] data = new byte[fi.filesize - fi.csize];
//                                    int currentLen;
//                                    try {
//                                        if (sl == null) {
//                                            return "socket关闭";
//                                        }
//                                        currentLen = sl.sin.read(data, 0, fi.filesize - fi.csize);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                        return "接收升级文件数据超时";
//                                    }
//                                    if (currentLen == 0) {
//                                        ReWriteConfigInfo(localFileDir + "/update_s.xml");// 重写版本升级配置信息
//                                        return "接收升级文件数据超时";
//                                    } else {
//                                        fi.csize += currentLen;
//                                        publishProgress((int) ((fi.csize / (float) fi.filesize) * 100));
//                                        LogUtil.getUtils().i("fi.csize - fi.filesize" + fi.csize + " - " + fi.filesize+ " - " + (int) ((fi.csize / (float) fi.filesize) * 100) + "%");
//                                        FileOutputStream out;
//                                        try {
//                                            String tempStr = localFileDir + "/" + fi.filename;
//                                            out = new FileOutputStream(tempStr, true);
//                                            out.write(data, 0, currentLen);
//                                            out.flush();
//                                            out.close();
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                            LogUtil.getUtils().i("file download fail info" + "文件写入错误");
//                                            apkFile.delete();
//                                            fi.state = 0;
//                                            fi.csize = 0;
//                                            ReWriteConfigInfo(localFileDir + "/update_s.xml");// 重写版本升级配置信息
//                                            return "升级文件写入错误";
//                                        }
//
//                                        // 修改文件给与其他包读的权限
//                                        if (flag && apkFile.exists()) {
//                                            try {
//                                                Runtime.getRuntime().exec("chmod 644 " + localFileDir + "/" + fi.filename);
//                                                flag = false;
//                                                LogUtil.getUtils().i("change " + "权限修改成功");
//                                            } catch (Exception e) {
//                                                flag = true;
//                                                LogUtil.getUtils().i("change " + "权限修改失败");
//                                                e.printStackTrace();
//                                            }
//                                        }
//
//                                        if (fi.csize >= fi.filesize) {
//                                            fi.state = 2; // 文件处理完毕
//                                            ReWriteConfigInfo(localFileDir + "/update_s.xml");// 重写版本升级配置信息
//                                            // 利用校验码进行校验证
//                                            try {
//                                                String Code = getHash(localFileDir + "/" + fi.filename, "MD5");
//                                                if (!Code.equals(checkCode)) {
//                                                    apkFile.delete();
//                                                    fi.state = 0;
//                                                    fi.csize = 0;
//                                                    ReWriteConfigInfo(localFileDir + "/update_s.xml");// 重写版本升级配置信息
//                                                    return "文件内容校验失败，请重新下载！";
//                                                }
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                                return "文件内容校验失败，请重新下载！";
//                                            }
//                                            break;
//                                        } else {
//                                            fi.state = 1; // 文件正在处理
//                                            ReWriteConfigInfo(localFileDir + "/update_s.xml");// 重写版本升级配置信息
//                                        }
//                                    }
//                                }
//                                LogUtil.getUtils().i(TAG + "apk file size - " + apkFile.length());
//                                break;
//                            case 1:// 文件不存在
//                            case 2:// 读取文件失败
//                            case 3:// 文件大小不附
//                            case 4:// 指定偏移量无效
//                            case 5:// 参数不完整
//                            // 删除update_s.xml文件,否则一次不成功就保留该文件无法更新。
//                            deleteFile(updateXml);
//                            return CONFIRM_INFO_ERROR;
//                        }
// [S]modify by LiXiaolong on 20160824. fix bug 3329. review by wangchao1.
                    }
                }
            }
            return null;
        }
    }

    private void closeSocket(){
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    private void installApk(String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        context.startActivity(intent);
        updateListener.handerResult(UPDATE_INSTALL, "", AutoUpdate.this);
    }

    private void deleteFile(File file){
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    private String getHash(String fileName, String hashType) throws Exception {

        InputStream fis;
        fis = new FileInputStream(fileName);// 读取文件
        byte[] buffer = new byte[ONE_K];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        int numRead;
        while ((numRead = fis.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }
        fis.close();
        return toHexString(md5.digest());
    }

    private String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            sb.append(hexChar[(aB & 0xf0) >>> 4]);
            sb.append(hexChar[aB & 0x0f]);
        }
        return sb.toString();
    }

    private char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};
}