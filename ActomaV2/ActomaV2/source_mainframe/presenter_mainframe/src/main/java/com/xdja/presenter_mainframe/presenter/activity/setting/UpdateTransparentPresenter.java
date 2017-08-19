package com.xdja.presenter_mainframe.presenter.activity.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.event.BusProvider;
import com.xdja.comm.event.FreshUpdateNewEvent;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.presenter.mvp.Command;
import com.xdja.presenter_mainframe.ActomaApplication;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.autoupdate.AutoUpdate;
import com.xdja.presenter_mainframe.autoupdate.UpdateListener;
import com.xdja.presenter_mainframe.presenter.base.PresenterActivity;
import com.xdja.presenter_mainframe.ui.ViewUpdate;
import com.xdja.presenter_mainframe.ui.uiInterface.UpdateVu;
import com.xdja.presenter_mainframe.util.SharePreferceUtil;

/**
 *
 * Created by chenbing on 2015/8/5.
 *
 * Modify by LiXiaolong on 2016/08/11.
 */
public class UpdateTransparentPresenter extends PresenterActivity<Command, UpdateVu> implements Command,
        UpdateListener {

    /**
     * 是否是版本不一致的强制升级提示
     */
    public static final String IS_FORCE_UPDATE_VERSION_DIFFRENT = "isForceUpdateVersionDiffrent";

    /**
     * 无更新时是否提示
     */
    public boolean isShowWithoutNewDialog = false;
    public static final String ISSHOWWITHOUTNEWDIALOG = "isShowWithoutNewDialog";

    @NonNull
    @Override
    protected Class<? extends UpdateVu> getVuClass() {
        return ViewUpdate.class;
    }

    @NonNull
    @Override
    protected Command getCommand() {
        return this;
    }

    @Override
    protected void preBindView(Bundle savedInstanceState) {
        super.preBindView(savedInstanceState);
    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);

        //获取传递的值
        if (getIntent() != null) {
            isShowWithoutNewDialog = getIntent().getBooleanExtra(ISSHOWWITHOUTNEWDIALOG, false);
        }

        getVu().showProgress();

        //是否是版本不同的强制升级
        boolean isVersionForceUpdate = getIntent().getBooleanExtra(IS_FORCE_UPDATE_VERSION_DIFFRENT, false);

        //开始检测更新
        if (isVersionForceUpdate) {
            new AutoUpdate(this, this).updateStartWithForceMessage(getResources().getString(R.string.update_force_version_diffrent));
        } else {
            new AutoUpdate(this, this).updateStartWithForceMessage(getResources().getString(R.string.update_force_message));
        }
    }

    /**
     * 处理更新各种结果
     *
     * @param actionTag 版本检测结果标识
     * @param version 版本检测返回的版本号
     */
    @Override
    public void handerResult(int actionTag, String version, AutoUpdate autoUpdate) {
        getVu().hideProgress();
        switch (actionTag) {
            case AutoUpdate.UPDATE_CODE_NO_NEW: // 无版本更新
                if (isShowWithoutNewDialog) {
                    finish();
                    XToast.showOK(this, getString(R.string.update_already_a_new_version));
                }
                break;
            case AutoUpdate.UPDATE_CODE_ERROR: // 检测版本信息失败
                if (isShowWithoutNewDialog) {
                    finish();
                    //[S]modify by lixiaolong on 20160908. fix bug 3797. review by self.
                    XToast.showOK(this, getString(R.string.update_connect_failured));
                    //[S]modify by lixiaolong on 20160908. fix bug 3797. review by self.
                }
                break;
            case AutoUpdate.UPDATE_FORCE_DOWNLOAD_FAIL://强制更新下载失败,退出程序
//                ActivityContoller.getInstanse().exit();
                ActivityStack.getInstanse().exitApp();
                break;
            case AutoUpdate.UPDATE_FORCDE_CANCEL://强制更行 暂不更新
                SharePreferceUtil.getPreferceUtil(ActomaApplication.getInstance()).setNewVersion("");
                sendUpdateEvent(false);
//                ActivityContoller.getInstanse().exit();
                ActivityStack.getInstanse().exitApp();
                break;
            case AutoUpdate.UPDATE_CANCEL: // 正常更新 暂不更新
                SharePreferceUtil.getPreferceUtil(ActomaApplication.getInstance()).setNewVersion("");
                sendUpdateEvent(false);
            case AutoUpdate.UPDATE_DOWNLOAD_FAIL: // 正常更新升级文件下载失败
            case AutoUpdate.UPDATE_INSTALL: // 安装升级文件
                finish();
                break;
            case AutoUpdate.UPDATE_CODE_NEW: // 检测到有升级版本
            case AutoUpdate.UPDATE_FORCE_CODE_NEW:
                SharePreferceUtil.getPreferceUtil(this).setNewVersion(version);
                autoUpdate.showUpdateMessage();
                break;
            default:
                break;
        }
    }

    /**
     * 发送显/隐NEW红点的事件
     */
    private void sendUpdateEvent(boolean isHaveUpdate) {
        FreshUpdateNewEvent event = new FreshUpdateNewEvent();
        event.setIsHaveUpdate(isHaveUpdate);
        BusProvider.getMainProvider().post(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
