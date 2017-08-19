package com.xdja.presenter_mainframe.ui;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdja.comm.uitl.TextUtil;
import com.xdja.frame.presenter.ActivityStack;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.frame.presenter.mvp.view.ActivitySuperView;
import com.xdja.frame.widget.XDialog;
import com.xdja.frame.widget.XToast;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.LauncherCommand;
import com.xdja.presenter_mainframe.presenter.activity.SplashPresenter;
import com.xdja.presenter_mainframe.ui.uiInterface.VuLauncher;
import com.xdja.presenter_mainframe.util.DensityUtil;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by xdja-fanjiandong on 2016/3/23.
 */
@ContentView(R.layout.activity_launcher)
public class LauncherView extends ActivitySuperView<LauncherCommand> implements VuLauncher {

    @Bind(R.id.iv_launcher_logo)
    ImageView ivLauncherLogo;
    @Bind(R.id.iv_launcher_logo_title)
    ImageView ivLauncherLogoTitle;
    @Bind(R.id.layout_launcher)
    LinearLayout layout_launcher;
    @Bind(R.id.btn_launcher_register)
    Button btnLauncherRegister;
    @Bind(R.id.btn_launcher_login)
    Button btnLauncherLogin;
    @Bind(R.id.btn_launcher_again)
    TextView btnLauncherAgain;

    @OnClick(R.id.btn_launcher_register)
    void register() {
        getCommand().register();
    }

    @Override
    public void setNavigateBtnVisibilety(int visibilety, int type) {
        getCommand().setNavBtnType(type);
        layout_launcher.setVisibility(visibilety);
        if (visibilety == View.VISIBLE) {
            btnLauncherLogin.setVisibility(View.GONE);
            btnLauncherRegister.setVisibility(View.GONE);
            switch (type) {
                case SplashPresenter.NAVIGATE_BTN_TYPE_ONE:
                    btnLauncherLogin.setVisibility(View.VISIBLE);
                    break;
                case SplashPresenter.NAVIGATE_BTN_TYPE_TWO:
                    btnLauncherLogin.setVisibility(View.VISIBLE);
                    btnLauncherRegister.setVisibility(View.VISIBLE);
                    break;
            }
            return;
        }
        btnLauncherLogin.setVisibility(visibilety);
        btnLauncherRegister.setVisibility(visibilety);
    }

    @Override
    public void setAgainBtnVisible(boolean isVisible) {
        if (isVisible){
            btnLauncherAgain.setVisibility(View.VISIBLE);
        }else {
            btnLauncherAgain.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_launcher_again)
    void again(){
        getCommand().again();
    }

    @OnClick(R.id.btn_launcher_login)
    void login() {
        if (getCommand().getNavBtnType() == SplashPresenter.NAVIGATE_BTN_TYPE_ONE){
            getCommand().oldLogin();
            return;
        }
        float buttonMarginBottom = getActivity().getResources().getDimension(R.dimen.launcher_button_margin_bottom);
        float buttonHeight = getActivity().getResources().getDimension(R.dimen.launcher_button_height);
        float buttonOffset = buttonMarginBottom + buttonHeight;
        float titltOffset = DensityUtil.dip2px(getContext(), 640) - getContext().getResources().getDimension(R.dimen.launcher_title_margin_top);
        btnLauncherRegister.animate()
                .translationY(buttonOffset)
                .setDuration(500L);
        btnLauncherLogin.animate()
                .translationY(buttonOffset)
                .setStartDelay(100L)
                .setDuration(500L);
        ivLauncherLogoTitle.animate()
                .translationY(titltOffset)
                .setStartDelay(200L)
                .setDuration(500L);
        ivLauncherLogo.animate()
                .translationY(titltOffset)
                .setStartDelay(300L)
                .setDuration(500L);

        getCommand().login();
    }

    @Override
    public void onResume() {
        super.onResume();
        btnLauncherRegister.animate().translationY(0);
        btnLauncherLogin.animate().translationY(0);
        ivLauncherLogoTitle.animate().translationY(0);
        ivLauncherLogo.animate().translationY(0);
    }



    //[S]modify by xienana for ckms download @2016/10/08 [reviewed by tangsha]
    XDialog notInstallDlg;
    @Override
    public void showDownloadDialog() {
        notInstallDlg = new XDialog(getActivity());
        notInstallDlg.setCancelable(false);
        notInstallDlg.setTitle(R.string.prompt)
                .setMessage(TextUtil.getActomaText(getContext(), TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                        0, 0, 0, getStringRes(R.string.ckms_download_prompt)))
                .setPositiveButton(getStringRes(R.string.ckms_install_btn_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityStack.getInstanse().exitApp();
                        notInstallDlg.dismiss();
                        getCommand().downloadCkms();
                    }
                })
                .show();
    }

    @Override
    public void hideDownloadDialog() {
        if (notInstallDlg != null) {
            notInstallDlg.dismiss();
        }
    }

    @Override
    public void showCkmsInstallFailToast() {
        XToast.show(getContext(),R.string.ckms_install_fail);
    }
    //[E]modify by xienana for ckms download @2016/10/08 [reviewed by tangsha]
}
