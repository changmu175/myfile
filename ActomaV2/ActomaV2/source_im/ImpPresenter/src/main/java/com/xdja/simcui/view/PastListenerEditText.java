package com.xdja.simcui.view;

import android.content.Context;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.util.BitmapUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PastListenerEditText extends EditText {

	/**
	 * 粘贴
	 */
	private static final int PASTCODE = 16908322;
	private final int maxLength = 2048;
	private final float FACE_SIZE = 1.1f;

	public PastListenerEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PastListenerEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTextContextMenuItem(int id){

	switch(id)

	{
		case PASTCODE:
			try {
				ClipboardManager cmb = (ClipboardManager) this.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                /*start modify by geyao 2016-1-11 添加非空判断 防止剪切板无数据时导致粘贴崩溃的问题*/
				if (cmb.getText() == null) {
					break;
				}
                /*end modify by geyao 2016-1-11 添加非空判断 防止剪切板无数据时导致粘贴崩溃的问题*/
				String tempstr = cmb.getText().toString();
				SpannableString ss;
				String preTxtStr = this.getText().toString();
				//当前文本内容长度+剪切板中文本长度 > 输入框最大长度，不进行粘贴
				if (preTxtStr.length() + tempstr.length() > maxLength) {
					tempstr = tempstr.substring(0, maxLength - preTxtStr.length());
				}
				if (!TextUtils.isEmpty(tempstr)) {
					try {
						//输入框表情大小在这里设置
						float FACE_SIZE = 1.1f;
						ss = BitmapUtils.formatSpanContent(tempstr, this.getContext(), FACE_SIZE);

						if (!TextUtils.isEmpty(ss)) {
							Editable edit = getEditableText();
							edit.insert(getSelectionStart(),ss);//必须用insert方法表情才会显示
							this.setFocusable(true);
							return false;
						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				LogUtil.getUtils().e(e.getMessage());
			}
			break;
		default:
			break;
	}

	return super.onTextContextMenuItem(id);
}
	//fix bug 6170,5797 by zya,20161130
	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		return new ZanyInputConnection(super.onCreateInputConnection(outAttrs),true);
	}
	//end by zya

	private class ZanyInputConnection extends InputConnectionWrapper {

		public ZanyInputConnection(InputConnection target, boolean mutable) {
			super(target, mutable);
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			// magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
			//fix bug 6170,5797 by zya,20161130
			/*if (beforeLength == 1 && afterLength == 0) {
				return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
						&& sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
			}*/
			//end by zya
			return super.deleteSurroundingText(beforeLength, afterLength);
		}

		//fix bug 6170,5797 by zya,20161130
		@Override
		public boolean commitText(CharSequence text, int newCursorPosition) {
			//add by zya 20170223
			int length = PastListenerEditText.this.getText().length();

			if(length >= PastListenerEditText.this.maxLength){
				if(mListener != null) {
					mListener.showToast();
				}
				return false;
			}

			Pattern pattern = Pattern.compile("(\\[emoji_[0-9]{3}\\])");
			Matcher matcher = pattern.matcher(text);
			if(matcher.find()){
				Editable edit = getEditableText();
				SpannableString ss = BitmapUtils.formatSpanContent(text, PastListenerEditText.this.getContext(), FACE_SIZE);
				edit.insert(getSelectionStart(),ss);
				return false;
			}//end by zya
			return super.commitText(text, newCursorPosition);
		}//end by zya
	}
	//fix bug 5797 by zya,20161130
	private MaxLengthListener mListener;

	public void setMaxLengthListener(MaxLengthListener listener){
		mListener = listener;
	}

	interface MaxLengthListener{
		void showToast();
	}//end by zya
}
