package com.xdja.presenter_mainframe.ui.uiInterface;

import android.view.View;
import android.widget.TextView;

import com.xdja.comm.circleimageview.CircleImageView;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.OpenGestureCommand;
import com.xdja.presenter_mainframe.ui.ActivityView;
import com.xdja.presenter_mainframe.util.LockPatternUtils;
import com.xdja.presenter_mainframe.widget.LockPatternView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by licong on 2016/11/25.
 */
@ContentView(R.layout.open_gesture_password)
public class ViewOpenGesture extends ActivityView<OpenGestureCommand> implements OpenGestureVu {
    @Bind(R.id.iv_login_face)
    CircleImageView ivLoginFace;

    @Bind(R.id.input_gesturepwd_unlock)
    TextView inputGestureText;

    @Bind(R.id.error_gesturepwd_unlock)
    TextView errorGestureText;

    @Bind(R.id.error_text)
    TextView errorText;

    @Bind(R.id.original_password)
    TextView originalPassword;

    @Bind(R.id.gesturepwd_unlock_lockview)
    LockPatternView gestureUnlockView;

    @Bind(R.id.forget_password)
    TextView forgetPassword;

    public static final String MODIFIED_GESTURE = "openGesture";

    public static final String CHECK_GESTURE = "checkGesture";

    @Override
    public void setImage(String avatarId, String thumbnailId) {
        if (ivLoginFace != null)
            ivLoginFace.loadImage(avatarId,true);
    }

    @Override
    public void setText(String type,int retry) {
        if ( 0 < retry &&  retry < LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
                errorGestureText.setVisibility(View.VISIBLE);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(getActivity().getResources().getString(R.string.safe_pwd_error)).append(retry).append(getActivity().getResources().getString(R.string.safe_number));
                errorGestureText.setText(stringBuffer.toString());
                errorText.setVisibility(View.VISIBLE);
                originalPassword.setVisibility(View.GONE);
                inputGestureText.setVisibility(View.GONE);

        } else {
            if (type.equals(MODIFIED_GESTURE)) {
                originalPassword.setVisibility(View.VISIBLE);
                inputGestureText.setVisibility(View.GONE);

            } else {
                originalPassword.setVisibility(View.GONE);
                inputGestureText.setVisibility(View.VISIBLE);
            }
            errorGestureText.setVisibility(View.GONE);
            errorText.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public LockPatternView getLockPatternView() {
        return gestureUnlockView;
    }

    @OnClick(R.id.forget_password)
    public void clickForgetButton() {
        getCommand().showDialog();

    }
}
