package com.xdja.simcui.recordingControl.manager;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.imp.R;
import com.xdja.dependence.uitls.LogUtil;


/**
 * 用于管理Dialog
 * 
 * @author cxp
 * 
 */
public class DialogManager {

	private ImageView mIcon;
	private ImageView mVoice;
	public TextView mLable;
	public Chronometer mVoiceChronometer;

	private final Context mContext;

	private Dialog dialog;// 替换AlertDialog解决背景阴影的问题

	/**
	 * 构造方法 传入上下文
	 */
	public  DialogManager(Context context) {
		this.mContext = context;
	}

	// 显示录音的对话框
	public void showRecordingDialog() {
		//start fix bug 4455 by licong, reView by zya, 2016/9/28
		if (dialog == null) {
			dialog = new Dialog(mContext, R.style.AudioDialog);
		}//end fix bug 4455 by licong, 2016/9/28

		LogUtil.getUtils().d("DialogManager showRecordingDialog :" + dialog.toString());
		dialog.setCancelable(false);//设置点击其他区域不消失
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.recording_control_recorder_dialog, null);

		mIcon = (ImageView) view.findViewById(R.id.id_recorder_dialog_icon);
		mVoice = (ImageView) view.findViewById(R.id.id_recorder_dialog_voice);
		mLable = (TextView) view.findViewById(R.id.id_recorder_dialog_label);
		mVoiceChronometer = (Chronometer)view.findViewById(R.id.id_recorder_Chronometer);
		
		dialog.setContentView(view);
		dialog.show();
	}

	public void recording() {
		if (dialog != null && dialog.isShowing()) { // 显示状态
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.VISIBLE);
			mLable.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.ic_message_vioce);
			mLable.setText(R.string.recording_control_str_recorder_slide_up_cancel);
		}
	}

	// 显示想取消的对话框
	public void wantToCancel() {
		if (dialog != null && dialog.isShowing()) { // 显示状态
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLable.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.ic_message_cancel);
			mLable.setText(R.string.recording_control_str_recorder_want_cancel);
		}
	}

	//最后十秒显示
	public void lastTenSeconds(String str) {
		if (dialog != null && dialog.isShowing()) { // 显示状态
			mIcon.setVisibility(View.GONE);
			mVoice.setVisibility(View.GONE);
			mLable.setVisibility(View.VISIBLE);

			mLable.setText(str);
		}
	}
	
	// 显示时间过短的对话框
	public void tooShort() {
		if (dialog != null && dialog.isShowing()) { // 显示状态
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLable.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.recording_voice_too_short);
			mLable.setText(R.string.recording_control_str_recorder_voice_too_short);
		}
	}

	// 显示时间过短的Toast提示
	public void tooShort(Context context) {
		if (dialog != null) {
			Toast.makeText(context, R.string.recording_control_str_recorder_voice_too_short, Toast.LENGTH_SHORT).show();
		}
	}

    public void tooShortToShow(Context context){
        Toast.makeText(context, R.string.recording_control_str_recorder_voice_too_short, Toast.LENGTH_SHORT).show();
    }

	public void getAudioDeviceErrToShow(Context context) {
		Toast.makeText(context, R.string.recording_control_str_get_audioDevice_err, Toast.LENGTH_SHORT).show();
	}
	
	//add by ycm 2016/09/30
	public void getAudioPermissionErrToShow(Context context) {
		Toast.makeText(context, R.string.recording_control_str_get_audioPermission_err, Toast.LENGTH_SHORT).show();
	}

	// 显示取消的对话框
	public void dimissDialog() {
		if (dialog != null && dialog.isShowing()) { // 显示状态
			dialog.dismiss();
			dialog = null;
		}
	}

	// 显示更新音量级别的对话框
	public void updateVoiceLevel(int level) {
		if (dialog != null && dialog.isShowing()) { // 显示状态

			// 设置图片的id
			int resId = mContext.getResources().getIdentifier("v" + level,
					"drawable", mContext.getPackageName());
			mVoice.setImageResource(resId);
		}
	}

	//add by zya ,fix bug 8256 ,20170204
	public boolean isShowing(){
		return dialog != null && dialog.isShowing();
	}//end by zya
}
