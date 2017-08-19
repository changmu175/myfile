package com.xdja.simcui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.imp.R;
import com.xdja.imp.util.BitmapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 聊天动作面板
 * 
 * @author fanjiandong
 * 
 */
public class ChatActionView extends LinearLayout {

	/** 栏目个数 */
	private static final int NUM_COLUMNS = 4;

	private GridView grid;

	private FaceGridView faceGrid;

	private Context mContext;

	private ChatActionOnItemClick instance;

	private List<MenuBean> menus;

	private MenuBean menu;

	private EditText edit;

	private List<String> emojiCol = null;
	
	private final int maxLength = 2048;
	
	private final int emoji_unite_length = 11;

	private List<String> getEmojiCol() {
		return this.emojiCol;
	}

	public ChatActionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData(context);
	}

	public ChatActionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	public ChatActionView(Context context) {
		super(context);
		initData(context);
	}

	public void setAcceptInput(EditText edit) {
		this.edit = edit;
	}

	@SuppressWarnings("deprecation")
	private void initData(Context context) {
		this.mContext = context;
		LayoutParams ll = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		@SuppressLint("InflateParams") View v = LayoutInflater.from(this.mContext).inflate(
				R.layout.sublayout_kakachat_action, null);
		this.addView(v, ll);
		this.grid = (GridView) v.findViewById(R.id.actiongrid);
		this.grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (menus == null || menus.get(arg2) == null) {
					return;
				}
				menu = menus.get(arg2);
				if (menu.getImgRes() == R.drawable.icon_face) {// 说明是表情
					if(instance != null){
						instance.onBeforeFacePanelShowCallBack();
					}
					setFacePanelVisiable(true);
				} else {// 不是表情
					if (instance != null) {
						instance.onItemClickCallBack(arg0, arg1, arg2, arg3);
					}
				}
			}
		});
		this.faceGrid = (FaceGridView) v.findViewById(R.id.facegrid);
		this.faceGrid.setOnFaceGridViewItemClick(new FaceGridView.OnFaceGridViewItemClick() {

			@Override
			public void onItemClick(int facesPos) {
				if (edit == null) {
					return;
				}
				try {
					if(edit.getVisibility() == View.VISIBLE){
						if(edit.getText().length() + emoji_unite_length > maxLength){
							//add by zya ,20170223
							showToast();
							//end by zya
							return;
						}
						Editable editable = edit.getEditableText();
						editable.insert(edit.getSelectionStart(),getSpannableStr(facesPos));//添加至光标处
					}
					// Log.e("fjd", "inputStr: " + edit.getText().toString());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void onClear() {
				if (edit == null) {
					return;
				}
				deleteSpannableStr();
				// Log.e("fjd", "inputStr: " + edit.getText().toString());
			}
		});
		this.faceGrid.setAdapter();
	}

	//add by zya 20170223
	private void showToast() {
		if (mContext != null
				&& !TextUtils.isEmpty(mContext.getResources().getString(
				R.string.input_length_warnning))) {
			Toast toast = Toast.makeText(mContext, mContext.getResources()
							.getString(R.string.input_length_warnning),
					Toast.LENGTH_SHORT);
			toast.show();
		}
	}//end by zya

	/**
	 * 是否显示表情面板
	 * 
	 * @param isShow
	 */
	public void setFacePanelVisiable(boolean isShow) {
		if (isShow) {
			this.faceGrid.setVisibility(View.VISIBLE);
			this.grid.setVisibility(View.GONE);
		} else {
			this.faceGrid.setVisibility(View.GONE);
			this.grid.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获取表情面板当前的显示情况
	 * 
	 * @return
	 */
	public int getFacePanelVisibility() {
		return this.faceGrid.getVisibility();
	}

	private SpannableString getSpannableStr(int facesPos)
			throws NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		// 获取表情图片
		@SuppressLint("DefaultLocale") Bitmap bitmap = BitmapUtils.getBitmapWithName(
				"emoji_" + String.format("%03d", facesPos), getResources());
		// 缩小表情图片
		bitmap = BitmapUtils.small(bitmap, Float.parseFloat(mContext.getString(R.string.faceitem_small)));
		// 用ImageSpan指定图片替代文字
		ImageSpan is = new ImageSpan(mContext, bitmap);
		// 其实写入EditView中的是这个字段“[fac”，表情图片会替代这个字段显示
		@SuppressLint("DefaultLocale") CharSequence cs = "[emoji_" + String.format("%03d", facesPos) + "]";
		SpannableString ss = new SpannableString(cs);
		if (emojiCol == null) {
			emojiCol = new ArrayList<>();
		}
		emojiCol.add(cs.toString());
		// 如果为了区分表情可以写一个集合每个表情对应一段文字
		ss.setSpan(is, 0, cs.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ss;
	}

	private void deleteSpannableStr() {
		// 获取光标的位置
		int cursonIndex = edit.getSelectionStart();
		int leftIndex;
		if (cursonIndex > 0) {
			// 获取输入框整体字符串
			String content = edit.getText().toString();
			if (!TextUtils.isEmpty(content)) {
				// 获取当前光标之前的所有文本
				content = content.substring(0, cursonIndex);
				// 最后一个字符为“]”的索引未知
				int rightIndex = content.lastIndexOf("]");
				if (rightIndex != -1 && rightIndex == content.length() - 1) {
					leftIndex = content.lastIndexOf("[");
					if (leftIndex != -1) {
						String temp = content.substring(leftIndex,
								rightIndex + 1);
						// 如果是表情
						//modify by zya 20170123 ,fix bug 8270
						Pattern pattern = Pattern.compile("(\\[emoji_[0-9]{3}\\])");
						Matcher matcher = pattern.matcher(temp);
						if ((this.getEmojiCol() != null && this.getEmojiCol().contains(temp)) || matcher.matches()) {
							//end by zya ,20170123
							edit.getEditableText().delete(leftIndex,
									cursonIndex);
							//modify by zya 20170203 ,fix bug 8270
							if(this.getEmojiCol() != null){
								this.getEmojiCol().remove(temp);
							}
							//end by zya 20170203 ,fix bug 8270
							return;
						}
					}
				}
				leftIndex = cursonIndex - 1;
				edit.getEditableText().delete(leftIndex, cursonIndex);

			}
		}
	}

	/**
	 * 设置Item项的点击事件（不包括表情选择项）
	 * 
	 * @param instance
	 */
	public void setOnItemClickCallBack(ChatActionOnItemClick instance) {
		this.instance = instance;
	}

	public void renderMenuView(List<MenuBean> myMenus) {
		this.grid.setNumColumns(NUM_COLUMNS);
		this.menus = myMenus;
		ActionMenuAdapter menuAdapter = new ActionMenuAdapter(this.mContext,
				menus);
		grid.setAdapter(menuAdapter);
	}

	public class MenuBean {
		private String menuName;
		private int imgRes;

		public MenuBean(int imgRes, String menuName) {
			this.menuName = menuName;
			this.imgRes = imgRes;
		}

		public String getMenuName() {
			return menuName;
		}

		public void setMenuName(String menuName) {
			this.menuName = menuName;
		}

		public int getImgRes() {
			return imgRes;
		}

		public void setImgRes(int imgRes) {
			this.imgRes = imgRes;
		}
	}

	class ViewHolder {
		public final ImageView button;
		public final TextView menuName;

		public ViewHolder(View v) {
			button = (ImageView) v.findViewById(R.id.imgbtn_chatmenu);
			menuName = (TextView) v.findViewById(R.id.txt_chatmenu);
		}
	}

	class ActionMenuAdapter extends BaseAdapter {

		private final List<MenuBean> menus;

		private LayoutInflater inflater;

		private ViewHolder holder;

		private MenuBean menu;

		public ActionMenuAdapter(Context context, List<MenuBean> menus) {
			this.menus = menus;
			if (context != null) {
				this.inflater = LayoutInflater.from(context);
			}
		}

		@Override
		public int getCount() {
			return menus.size();
		}

		@Override
		public Object getItem(int position) {
			return menus.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (inflater == null) {
				return null;
			}
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_chat_actionmenu,
						null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			menu = menus.get(position);
			if (menu == null) {
				return null;
			}
			holder.button.setBackgroundResource(menu.getImgRes());
			if (!TextUtils.isEmpty(menu.getMenuName())) {
				holder.menuName.setText(menu.getMenuName());
			}

			return convertView;
		}

	}

	public interface ChatActionOnItemClick {
		@SuppressLint("UnusedParameters")
		void onItemClickCallBack(AdapterView<?> arg0, View arg1, int arg2,
								 long arg3);
		/**
		 * 切换到表情面板前进行的动作
		 */
		void onBeforeFacePanelShowCallBack();
	}

}
