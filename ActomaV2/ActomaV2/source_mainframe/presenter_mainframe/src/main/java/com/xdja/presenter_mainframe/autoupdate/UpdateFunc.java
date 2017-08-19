package com.xdja.presenter_mainframe.autoupdate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Time;
import android.view.View;

import com.xdja.comm.circleimageview.CustomDialog;
import com.xdja.presenter_mainframe.R;

import java.io.FileOutputStream;
import java.io.IOException;

public class UpdateFunc {

	public static void popAlert(Context context, String title, String msg) {
		try {
			final CustomDialog customDialog = new CustomDialog(context);
			customDialog.setTitle(title)
					.setMessage(msg)
					.setNegativeButton(context.getString(R.string.update_dialog_btn_ok),
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									customDialog.dismiss();
								}
							}).setCancelable(false).show();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	public static  void memset(byte[] buff, byte pad, int len) {
		int i = 0;
		for (i = 0; i < len; i++) {
			buff[i] = pad;
		}
	}
	
	/**
     * ��ȡ��ǰʱ��
     * ��ʽ��1970-01-01
     */
	@SuppressLint("DefaultLocale")
	@SuppressWarnings("deprecation")
	public static String GetCurrentTime() {
    	Time time = new Time("GMT+8");
        time.setToNow();
        return String.format("%04d-%02d-%2d", time.year, time.month+1, time.monthDay);
    }
	public static void WriteFile(String filename, String data, boolean append)
	{
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename, append);
			/*if (append == true) {				
				out = openFileOutput(filename, MODE_APPEND);
			} else {
				out = openFileOutput(filename, MODE_PRIVATE);
			}*/
		    out.write(data.getBytes());
		    out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out!=null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

}