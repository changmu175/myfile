/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This file contains relicensed code from Apache copyright of 
 * Copyright (C) 2008-2009 The Android Open Source Project
 */

package com.securevoip.ui.incall.locker.slidingtab;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.securevoip.ui.incall.locker.IOnLeftRightChoice;
import com.securevoip.ui.incall.locker.IOnLeftRightChoice.IOnLeftRightProvider;
import com.securevoip.ui.incall.locker.IOnLeftRightChoice.TypeOfLock;
import com.securevoip.ui.incall.locker.LeftRightChooserUtils;
import com.xdja.voipsdk.R;

import java.util.ArrayList;

/**
 * A special widget containing two Sliders and a threshold for each. Moving
 * either slider beyond the threshold will cause the registered
 * OnTriggerListener.onTrigger() to be called with
 * Deeply inspired from android SlidingTab internal widget but simplified for our use
 * 
 */
public class SlidingTab extends ViewGroup implements IOnLeftRightProvider {

	//这个数值越大，可向两边滑动的距离越大；数值是2.2可以让最右边无论如何都无法划出界，但是有可能出现不到边界就接听的状况
	private static final float TARGET_ZONE = 2.2f / 3.0f;
	private static final long VIBRATE_SHORT = 30;
	private static final long VIBRATE_LONG = 40;

	private IOnLeftRightChoice onTriggerListener;
	private boolean triggered = false;
	private Vibrator mVibrator;
	// used to scale dimensions for bitmaps.
	private static float density;

	private Slider leftSlider, rightSlider, currentSlider;
	private boolean tracking;
	private float targetZone;
    private float downPoint = 0;
    private float lastPoint = 0;
	private static final String THIS_FILE = "SlidingTab";



	/**
	 * Simple container class for all things pertinent to a slider. A slider
	 * consists of 3 Views:
	 * 
	 * {@link #tab} is the tab shown on the screen in the default state.
	 * {@link #text} is the view revealed as the user slides the tab out.
	 * {@link #target} is the target the user must drag the slider past to
	 * trigger the slider.
	 * 
	 */
	private class Slider {
		/**
		 * Tab alignment - determines which side the tab should be drawn on
		 */
		public static final int ALIGN_LEFT = 0;
		public static final int ALIGN_RIGHT = 1;

		/**
		 * States for the view.
		 */
		private static final int STATE_NORMAL = 0;
		private static final int STATE_PRESSED = 1;
		private static final int STATE_ACTIVE = 2;

		private int PADDING_RL = 60;
		private int TARGET_RL = 52;
		private int TARGET_WID = 256;

		private final ImageView tab;//接听或挂断按钮
		private final TextView text;
		private final ImageView target;//背景条
        private final ImageView arrow;

        private final LinearLayout anSection;

		/**
		 * Constructor
		 * 
		 * @param parent
		 *            the container view of this one
		 * @param strId
		 *            indicate string
		 * @param arrowId
		 *            indicate arrow
		 * @param targetId
		 *            drawable for the target
         * @param LorR
         *            tab director
		 */
		Slider(ViewGroup parent, int iconId, int targetId, int strId, int arrowId, int LorR) {

			/** 20161130-mengbo-start: 锁屏接听、挂断相关参数适配不同机型 **/
			TARGET_RL = (int)parent.getContext().getResources().getDimension(R.dimen.answer_controls_target_padding);
			TARGET_WID = (int)parent.getContext().getResources().getDimension(R.dimen.answer_controls_target_width);
			PADDING_RL = (int)parent.getContext().getResources().getDimension(R.dimen.answer_controls_padding_rl);
			/** 20161130-mengbo-end **/

			// Create tab
			tab = new ImageView(parent.getContext());
			tab.setImageResource(iconId);
			tab.setScaleType(ScaleType.CENTER);
			tab.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            final LayoutInflater inflater = (LayoutInflater)parent.getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (LorR == ALIGN_LEFT) {
                anSection = (LinearLayout) inflater.inflate(R.layout.indicate_view_left, null);
            } else {
                anSection = (LinearLayout) inflater.inflate(R.layout.indicate_view_right, null);
            }

			// Create hint TextView
			text = (TextView)anSection.findViewById(R.id.indicate_text);
            text.setText(strId);
            arrow = (ImageView)anSection.findViewById(R.id.indicate_image);
            arrow.setImageResource(arrowId);
			anSection.setVisibility(INVISIBLE);

			// Create target
			target = new ImageView(parent.getContext());
			target.setBackgroundResource(targetId);
			target.setScaleType(ScaleType.FIT_XY);

			/** 20161129-mengbo-start: 来电接听、挂断滑动控件长度适配不同机型 **/
			int screenWidth = parent.getContext().getResources().getDisplayMetrics().widthPixels; //得到屏幕宽度
			int targetWidth = screenWidth - (int)(TARGET_RL * density *2);
			target.setLayoutParams(new LayoutParams(targetWidth, LayoutParams.WRAP_CONTENT));
			//target.setLayoutParams(new LayoutParams((int) (TARGET_WID * density), LayoutParams.WRAP_CONTENT));
			/** 20161129-mengbo-end **/
			target.setVisibility(View.INVISIBLE);

			// this needs to be first - relies on painter's algorithm
			parent.addView(target);
            parent.addView(anSection);
			parent.addView(tab);
            parent.setPadding((int) (TARGET_RL * density), 0, (int) (TARGET_RL * density), 0);
		}

		private void setResources(int iconId, int targetId, int barId, int tabId) {
			tab.setImageResource(iconId);
			tab.setBackgroundResource(tabId);
			target.setBackgroundResource(targetId);
		}

		private void setDrawables(Drawable iconD, Drawable targetD, Drawable barD, Drawable tabD) {
			if(iconD != null) {
				tab.setImageDrawable(iconD);
			}
			if(tabD != null) {
//			    UtilityWrapper.getInstance().setBackgroundDrawable(tab, tabD);
				tab.setBackgroundDrawable(tabD);
			}
			if(barD != null) {
//			    UtilityWrapper.getInstance().setBackgroundDrawable(text, barD);
				text.setBackgroundDrawable(barD);
			}
			if(tabD != null) {
				target.setImageDrawable(targetD);
			}
		}


		private void setHintText(int resId) {
			text.setText(resId);
		}

        private void setHintText(String str) {
            text.setText(str);
        }

		private void hide() {
			tab.setVisibility(View.INVISIBLE);
			target.setVisibility(View.INVISIBLE);
            anSection.setVisibility(View.INVISIBLE);
		}

		private void setState(int state) {
			text.setPressed(state == STATE_PRESSED);
			tab.setPressed(state == STATE_PRESSED);
			if (state == STATE_ACTIVE) {
				final int[] activeState = new int[] { android.R.attr.state_active };
				if (text.getBackground().isStateful()) {
					text.getBackground().setState(activeState);
				}
				if (tab.getBackground().isStateful()) {
					tab.getBackground().setState(activeState);
				}
//				text.setTextAppearance(text.getContext(), R.style.TextAppearance_SlidingTabActive);
			} else {
//				text.setTextAppearance(text.getContext(), R.style.TextAppearance_SlidingTabNormal);
			}
		}

		private void showTarget() {
			target.setVisibility(View.VISIBLE);
            anSection.setVisibility(View.VISIBLE);
			Log.d(THIS_FILE, "showTarget  targetLeft:" + target.getLeft() + " targetRight:" + target.getRight() +
					"  targetWid:" + target.getWidth() + "  targetHeight:" + target.getHeight());
            anSectionStart();
        }

		private void reset() {
			setState(STATE_NORMAL);
			tab.setVisibility(View.VISIBLE);
			target.setVisibility(View.INVISIBLE);
            anSectionStop();
		}


        private void anSectionStart() {
            anSection.setVisibility(View.VISIBLE);
            AlphaAnimation alphaAnimation1 = new AlphaAnimation(1.0f, 0.3f);
            alphaAnimation1.setDuration(500);
            alphaAnimation1.setRepeatCount(Animation.INFINITE);
            alphaAnimation1.setRepeatMode(Animation.REVERSE);
            anSection.setAnimation(alphaAnimation1);
            alphaAnimation1.start();

            /*TranslateAnimation alphaAnimation2 = new TranslateAnimation(150f, 350f, 50, 50);
            alphaAnimation2.setDuration(3000);
            alphaAnimation2.setRepeatCount(Animation.INFINITE);
            alphaAnimation2.setRepeatMode(Animation.REVERSE);
            anSection.setAnimation(alphaAnimation2);
            alphaAnimation2.start();*/
        }

        private void anSectionStop() {
            anSection.clearAnimation();
            anSection.setVisibility(View.INVISIBLE);
        }
		/**
		 * Layout the given widgets within the parent.
		 *
		 * @param l
		 *            the parent's left border
		 * @param t
		 *            the parent's top border
		 * @param r
		 *            the parent's right border
		 * @param b
		 *            the parent's bottom border
		 * @param alignment
		 *            which side to align the widget to
		 */
		private void layout(int l, int t, int r, int b, int alignment) {

			final int handleWidth = tab.getDrawable().getIntrinsicWidth();
			final int handleHeight = tab.getDrawable().getIntrinsicHeight();
			final int targetWidth = target.getLayoutParams().width; //target.getDrawable().getIntrinsicWidth();
			final int targetHeight = target.getDrawable().getIntrinsicHeight();
			final int parentWidth = r - l;
			final int parentHeight = b - t;

			/*Log.d(THIS_FILE, "handleWidth:"+handleWidth+" handleHeight:"+handleHeight+
					"  targetWidth:"+targetWidth+"  targetHeight:"+targetHeight);
			Log.d(THIS_FILE, "l:"+l+" t:"+t+
					"  r:"+r+"  b:"+b);
            */
			/*Log.d(THIS_FILE, "targetLeft:"+target.getLeft()+" targetRight:"+target.getRight()+
					"  targetWid:"+targetWidth+"  targetHeight:"+targetHeight +"     alignment:"+alignment);
			Log.d(THIS_FILE, "textLeft:"+text.getLeft()+" textRight:"+text.getRight()+
					"  textWid:"+text.getWidth()+"  textHeight:"+text.getHeight());
			Log.d(THIS_FILE, "arrowLeft:"+arrow.getLeft()+" arrowRight:"+arrow.getRight()+
					"  arrowWid:"+arrow.getWidth()+"  arrowHeight:"+arrow.getHeight());*/

			final int leftTarget = TARGET_RL;
			final int rightTarget = TARGET_RL;

			final int targetTop = (parentHeight - targetHeight) / 2;
			final int targetBottom = targetTop + targetHeight;
			final int top = (parentHeight - handleHeight) / 2;
			final int bottom = (parentHeight + handleHeight) / 2;
			if (alignment == ALIGN_LEFT) {
                tab.layout((int) (PADDING_RL * density), top,
                        handleWidth + (int) (PADDING_RL * density), bottom);

                final int textwid = text.getRight() - text.getLeft();
                final int arrowwid = arrow.getRight() - arrow.getLeft();
                final int anSectionWid = (int) (textwid + arrowwid + 8*density);
                final int sectionLeft = (int) (target.getRight() - anSectionWid - 26*density);
                anSection.layout(sectionLeft, targetTop,
                        sectionLeft + anSectionWid, targetBottom);

				target.layout((int)(leftTarget*density), targetTop,
                        (int)(leftTarget*density)+targetWidth, targetBottom);
			} else {
				tab.layout(parentWidth - (int)(PADDING_RL*density) - handleWidth,
						top, parentWidth-(int)(PADDING_RL*density), bottom);
                final int textwid = text.getRight() - text.getLeft();
                final int arrowwid = arrow.getRight() - arrow.getLeft();
                final int anSectionWid = (int) (textwid + arrowwid + 8*density);
                final int sectionLeft = (int) (target.getLeft() + 26*density);
                anSection.layout(sectionLeft, targetTop,
                        sectionLeft + anSectionWid, targetBottom);
				target.layout((int)(rightTarget*density), targetTop,
                        (int)(rightTarget*density)+targetWidth, targetBottom);
			}

		}

		public int getTabHeight() {
			return tab.getBackground().getIntrinsicHeight();
		}

		public int getTargetHeight() {
			return target.getDrawable().getIntrinsicHeight();
		}

	}

	public SlidingTab(Context context) {
		this(context, null);
	}

	/**
	 * Constructor used when this widget is created from a layout file.
	 */
	public SlidingTab(Context context, AttributeSet attrs) {
		super(context, attrs);

		density = getResources().getDisplayMetrics().density;
		leftSlider = new Slider(this,
				R.drawable.ic_jog_dial_answer_new,
				R.drawable.voip_phone_bg,
				R.string.slidtoright,
				R.drawable.pic_voip_right,
                Slider.ALIGN_LEFT);
		rightSlider = new Slider(this,
				R.drawable.ic_jog_dial_decline_new,
				R.drawable.voip_phone_bg,
				R.string.slidtoleft,
				R.drawable.pic_voip_left,
                Slider.ALIGN_RIGHT);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		final int leftTabHeight = (int) (density * leftSlider.getTabHeight() + 0.5f);
		final int rightTabHeight = (int) (density * rightSlider.getTabHeight() + 0.5f);
		int height = Math.max(leftTabHeight, rightTabHeight);

		/** 20161130-mengbo-start: 锁屏接听、挂断点击阴影高度适配不同机型 **/
		int targetHeight = Math.max(leftSlider.getTargetHeight(), rightSlider.getTargetHeight());
		if(targetHeight > height){
			height = targetHeight;
		}
		/** 20161130-mengbo-end **/

		setMeasuredDimension(widthSpecSize, height);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			view.measure(widthSpecSize, height);
			if (view instanceof LinearLayout) {
				LinearLayout group = (LinearLayout)view;
				for (int j=0; j<group.getChildCount(); j++) {
					group.getChildAt(j).measure(group.getWidth(), group.getHeight());
				}
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();
		final Rect frame = new Rect();

		View leftHandle = leftSlider.tab;
		leftHandle.getHitRect(frame);
		boolean leftHit = frame.contains((int) x, (int) y);
		View rightHandle = rightSlider.tab;
		rightHandle.getHitRect(frame);
		boolean rightHit = frame.contains((int) x, (int) y);
		if (!tracking && !(leftHit || rightHit)) {
			return false;
		}

		if (action == MotionEvent.ACTION_DOWN) {
			tracking = true;
			triggered = false;
			vibrate(VIBRATE_SHORT);
			if (leftHit) {
				currentSlider = leftSlider;
				targetZone = TARGET_ZONE;
				rightSlider.hide();
			} else {
				currentSlider = rightSlider;
				targetZone = 1.0f - TARGET_ZONE;
				leftSlider.hide();
			}
            downPoint = x;
            lastPoint = x;
			currentSlider.setState(Slider.STATE_PRESSED);
			currentSlider.showTarget();
		}
		recomputeLayout(getLeft(), getTop(), getRight(), getBottom());
		return true;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (tracking) {
			final int action = event.getAction();
			final float x = event.getX();
			final float y = event.getY();
			final View handle = currentSlider.tab;
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				float position = x;
				float target = targetZone * getWidth();
				boolean targetZoneReached = (currentSlider.equals(leftSlider) ?
                        position > target : position < target);
				if (!triggered && targetZoneReached) {
					triggered = true;
					tracking = false;
//					currentSlider.setState(Slider.STATE_ACTIVE);
					resetView();
					dispatchTriggerEvent(currentSlider.equals(leftSlider) ? IOnLeftRightChoice.LEFT_HANDLE : IOnLeftRightChoice.RIGHT_HANDLE);
					break;
				}
                if (currentSlider.equals(leftSlider) && downPoint >= position) {
                    break;
                }
                if (currentSlider.equals(rightSlider) && downPoint <= position) {
                    break;
                }
                if (Math.abs(downPoint - position) > 100) {
                    currentSlider.anSectionStop();
                } else {
                    currentSlider.anSectionStart();
                }

                moveHandle(x-lastPoint, y);
                lastPoint = x;
				if (y <= handle.getBottom() && y >= handle.getTop()) {
					break;
				}

				// Intentionally fall through - we're outside tracking rectangle
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
                downPoint = 0;
                lastPoint = 0;
				tracking = false;
                if(!triggered){
                    triggered = false;
                    resetView();
                }
				break;
			default:
				break;
			}
		}

		return tracking || super.onTouchEvent(event);
	}


	public void resetView() {
		leftSlider.reset();
		rightSlider.reset();
		recomputeLayout(getLeft(), getTop(), getRight(), getBottom());
	}

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(THIS_FILE, "onLayout    changed:" + changed);
        if (!changed) {
            return;
        }
		recomputeLayout(l, t, r, b);
	}

    public void recomputeLayout(int l, int t, int r, int b) {
		// Center the widgets in the view
		leftSlider.layout(l, t, r, b, Slider.ALIGN_LEFT);
		rightSlider.layout(l, t, r, b, Slider.ALIGN_RIGHT);
		invalidate();
    }

	private void moveHandle(float x, float y) {
		final View handle = currentSlider.tab;
		final View content = currentSlider.text;

		handle.offsetLeftAndRight((int) x);
		//content.offsetLeftAndRight(deltaX);
		invalidate();
	}

	/**
	 * Sets the left handle icon to a given resource.
	 * 
	 * The resource should refer to a Drawable object, or use 0 to remove the
	 * icon.
	 * 
	 * @param iconId
	 *            the resource ID of the icon drawable
	 * @param targetId
	 *            the resource of the target drawable
	 * @param barId
	 *            the resource of the bar drawable (stateful)
	 * @param tabId
	 *            the resource of the
	 */
	public void setLeftTabResources(int iconId, int targetId, int barId, int tabId) {
		leftSlider.setResources(iconId, targetId, barId, tabId);
	}
	
	public void setLeftTabDrawables(Drawable iconD, Drawable targetD, Drawable barD, Drawable tabD) {
		leftSlider.setDrawables(iconD, targetD, barD, tabD);
	}

	/**
	 * Sets the left handle hint text to a given resource string.
	 * 
	 * @param resId
	 */
	public void setLeftHintText(int resId) {
		leftSlider.setHintText(resId);
	}
	
    /**
     * Sets the left handle hint text to a given string.
     * 
     * @param str
     */
    public void setLeftHintText(String str) {
        leftSlider.setHintText(str);
    }

	/**
	 * Sets the right handle icon to a given resource.
	 * 
	 * The resource should refer to a Drawable object, or use 0 to remove the
	 * icon.
	 * 
	 * @param iconId
	 *            the resource ID of the icon drawable
	 * @param targetId
	 *            the resource of the target drawable
	 * @param barId
	 *            the resource of the bar drawable (stateful)
	 * @param tabId
	 *            the resource of the
	 */
	public void setRightTabResources(int iconId, int targetId, int barId, int tabId) {
		rightSlider.setResources(iconId, targetId, barId, tabId);
	}

	public void setRightTabDrawables(Drawable iconD, Drawable targetD, Drawable barD, Drawable tabD) {
		rightSlider.setDrawables(iconD, targetD, barD, tabD);
	}
	
	/**
	 * Sets the left handle hint text to a given resource string.
	 * @param resId
	 */
	public void setRightHintText(int resId) {
		rightSlider.setHintText(resId);
	}
	
    /**
     * Sets the left handle hint text to a given string.
     * @param str
     */
    public void setRightHintText(String str) {
        rightSlider.setHintText(str);
    }

	/**
	 * Triggers haptic feedback.
	 */
	private synchronized void vibrate(long duration) {
		if (mVibrator == null) {
			mVibrator = (android.os.Vibrator) getContext().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		}
		mVibrator.vibrate(duration);
	}

	

	/**
	 * Dispatches a trigger event to listener. Ignored if a listener is not set.
	 * 
	 * @param whichHandle
	 *            the handle that triggered the event.
	 */
	private void dispatchTriggerEvent(int whichHandle) {
		vibrate(VIBRATE_LONG);
		Log.d(THIS_FILE, "We take the call....");
		if (onTriggerListener != null) {
			Log.d(THIS_FILE, "We transmit to the parent....");
			onTriggerListener.onLeftRightChoice(whichHandle);
		}
	}

	/**
     * Registers a callback to be invoked when the user triggers an event.
     * 
     *            the OnDialTriggerListener to attach to this view
     */
    @Override
    public void setOnLeftRightListener(IOnLeftRightChoice l) {
        onTriggerListener = l;
    }

    /* (non-Javadoc)
     * @see com.csipsimple.ui.incall.locker.IOnLeftRightChoice.IOnLeftRightProvider#applyTargetTitles(int)
     */
    @Override
    public void applyTargetTitles(int resArrayTitles) {
        ArrayList<String> strings = LeftRightChooserUtils.loadTargetsDescriptions(getContext(),
                resArrayTitles);
        // setRightHintText(R.string.ignore_call);
        // setLeftHintText(R.string.take_call);
        setRightHintText(strings.get(0));
        setLeftHintText(strings.get(1));
    }

    /* (non-Javadoc)
     * @see com.csipsimple.ui.incall.locker.IOnLeftRightChoice.IOnLeftRightProvider#setTypeOfLock(com.csipsimple.ui.incall.locker.IOnLeftRightChoice.TypeOfLock)
     */
    @Override
    public void setTypeOfLock(TypeOfLock lock) {
        // TODO Theme
        if(lock == TypeOfLock.CALL) {

            // To sliding tab
            setLeftTabDrawables(getContext().getResources().getDrawable(R.drawable.ic_jog_dial_answer_new),
                    getContext().getResources().getDrawable(R.drawable.voip_phone_bg),
                    getContext().getResources().getDrawable(R.drawable.jog_tab_bar_left_end_normal),
                    getContext().getResources().getDrawable(R.drawable.jog_tab_bar_left_end_normal));
            
            setRightTabDrawables(
                    getContext().getResources().getDrawable(R.drawable.ic_jog_dial_decline_new),
                    getContext().getResources().getDrawable(R.drawable.voip_phone_bg),
                    getContext().getResources().getDrawable(R.drawable.jog_tab_bar_left_end_normal),
                    getContext().getResources().getDrawable(R.drawable.jog_tab_bar_left_end_normal));
        }
    }

    /* (non-Javadoc)
     * @see com.csipsimple.ui.incall.locker.IOnLeftRightChoice.IOnLeftRightProvider#getLayoutingHeight()
     */
    @Override
    public int getLayoutingHeight() {
        return LayoutParams.WRAP_CONTENT;
    }

    /* (non-Javadoc)
     * @see com.csipsimple.ui.incall.locker.IOnLeftRightChoice.IOnLeftRightProvider#getLayoutingWidth()
     */
    @Override
    public int getLayoutingWidth() {
        return LayoutParams.WRAP_CONTENT;
    }

}
