package com.xdja.presenter_mainframe.ui;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.frame.widget.XDialog;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.EmpowerDeviceLoginCommand;
import com.xdja.presenter_mainframe.presenter.activity.login.helper.LoginHelper;
import com.xdja.presenter_mainframe.ui.uiInterface.VuEmpowerDeviceLogin;
import com.xdja.presenter_mainframe.widget.PartClickTextView;
import com.xdja.presenter_mainframe.widget.QRImageView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by ldy on 16/4/15.
 */
@ContentView(R.layout.activity_empower_device_login)
public class EmpowerDeviceLoginView extends ActivityView<EmpowerDeviceLoginCommand> implements VuEmpowerDeviceLogin {
    @Bind(R.id.pctv_empower_generate_again)
    PartClickTextView pctvEmpowerGenerateAgain;
    @Bind(R.id.iv_empower_device_login_qr)
    QRImageView ivEmpowerDeviceLoginQr;
    @Bind(R.id.tv_empower_device_login_auth_code)
    TextView tvEmpowerDeviceLoginAuthCode;

    @Override
    public void onCreated() {
        super.onCreated();
        pctvEmpowerGenerateAgain.appendClickableText(getStringRes(R.string.empower_device_login_generate_again),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCommand().generateAgainEmpower();
                    }
                });
    }

    @OnClick(R.id.pctv_empower_cannot_empower)
    void cannotEmpoewer() {
        final XDialog xDialog = new XDialog(getActivity());
        xDialog.setTitle(getStringRes(R.string.confirm_device_loss))
                .setMessage(getStringRes(R.string.confirm_device_loss_content1) +
                        getStringRes(R.string.confirm_device_loss_content2))
                .setPositiveButton(getStringRes(R.string.certain), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //modify by alh@xdja.com to fix bug: 1134 2016-07-05 start (rummager : self)
                        if (xDialog != null && xDialog.isShowing()) xDialog.dismiss();
                        //modify by alh@xdja.com to fix bug: 1134 2016-07-05 end (rummager : self)
                        getCommand().cannotEmpower();
                    }
                })
                .setNegativeButton(getStringRes(R.string.cancel), null)
                .show();
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public void setAuthorizeId(String authorizeId) {
        ivEmpowerDeviceLoginQr.setQRCode(authorizeId);
        tvEmpowerDeviceLoginAuthCode.setText(authorizeId);
    }

    @Override
    public void maxLoginCount(int maxLoginCount) {
        LoginHelper.maxLoginDialog(getActivity(), maxLoginCount);
    }

    //modify yangshaopeng,recycle bitmap, review by lixiaolong
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ivEmpowerDeviceLoginQr != null) {
            ivEmpowerDeviceLoginQr.setImageBitmap(null);
            Bitmap bmp = ivEmpowerDeviceLoginQr.getImageBitmap();
            if (bmp != null) {
                bmp.recycle();
            }
            ivEmpowerDeviceLoginQr = null;
        }
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_empower_device_login);
    }
    /*[E]modify by tangsha@20161011 for multi language*/
}
