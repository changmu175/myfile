/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 * <p/>
 * CSipSimple is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * If you own a pjsip commercial license you can also redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License
 * as an android library.
 * <p/>
 * CSipSimple is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * This file contains relicensed code from Apache copyright of
 * Copyright (C) 2008 The Android Open Source Project
 */
/**
 * This file contains relicensed code from Apache copyright of 
 * Copyright (C) 2008 The Android Open Source Project
 */

package com.securevoip.ui.incall.locker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.csipsimple.api.SipCallSession;
import com.xdja.dependence.uitls.LogUtil;
import com.securevoip.ui.incall.IOnCallActionTrigger;
import com.securevoip.ui.incall.locker.slidingtab.SlidingTab;

public class InCallAnswerControls extends RelativeLayout implements IOnLeftRightChoice {

     private static final String THIS_FILE = "InCallAnswerControls";

     private IOnLeftRightProvider lockerWidget;

     private static final int MODE_LOCKER = 0;
     private static final int MODE_NO_ACTION = 1;
     private int controlMode;
     private SipCallSession currentCall;
     private IOnCallActionTrigger onTriggerListener;

     private boolean needUpdateVisibility = true;

     public InCallAnswerControls(Context context) {
          this(context, null, 0);
     }

     public InCallAnswerControls(Context context, AttributeSet attrs) {
          this(context, attrs, 0);
     }

     public InCallAnswerControls(Context context, AttributeSet attrs, int style) {
          super(context, attrs, style);
          setGravity(Gravity.CENTER_VERTICAL);
          setCallLockerVisibility(VISIBLE);
     }

     @Override
     protected void onFinishInflate() {
          super.onFinishInflate();
     }


     private void setCallLockerVisibility(int visibility) {
          controlMode = visibility == View.VISIBLE ? MODE_LOCKER : MODE_NO_ACTION;
          if (needUpdateVisibility) {
               setVisibility(visibility);
          }
          if (visibility == View.VISIBLE) {
               if (lockerWidget == null) {
                    lockerWidget = new SlidingTab(getContext());
               }
               lockerWidget.setOnLeftRightListener(this);
               lockerWidget.setTypeOfLock(TypeOfLock.CALL);
               LayoutParams lp = new LayoutParams(lockerWidget.getLayoutingWidth(), lockerWidget.getLayoutingHeight());
               if (lockerWidget.getLayoutingHeight() == LayoutParams.WRAP_CONTENT ||
                       lockerWidget.getLayoutingWidth() == LayoutParams.WRAP_CONTENT) {
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
               }
               this.addView((View) lockerWidget, lp);

          }
          if (lockerWidget != null) {
               lockerWidget.setVisibility(visibility);
               lockerWidget.resetView();
          }
     }


     /**
      * Registers a callback to be invoked when the user triggers an event.
      *
      * @param listener
      *            the OnTriggerListener to attach to this view
      */
     public void setOnTriggerListener(IOnCallActionTrigger listener) {
          onTriggerListener = listener;
     }

     public void updateCallSession(SipCallSession callInfo) {
          currentCall = callInfo;
     }

     public void setCallState(SipCallSession callInfo) {
          currentCall = callInfo;

          if (currentCall == null) {
               setCallLockerVisibility(GONE);
               return;
          }

          if (!needUpdateVisibility) {
               return;
          }

          int state = currentCall.getCallState();
          switch (state) {
               case SipCallSession.InvState.INCOMING:
                    setCallLockerVisibility(VISIBLE);
                    break;
               case SipCallSession.InvState.CALLING:
               case SipCallSession.InvState.CONNECTING:
               case SipCallSession.InvState.CONFIRMED:
               case SipCallSession.InvState.NULL:
               case SipCallSession.InvState.DISCONNECTED:
                    setCallLockerVisibility(GONE);
                    break;
               case SipCallSession.InvState.EARLY:
               default:
                    if (currentCall.isIncoming()) {
                         setCallLockerVisibility(VISIBLE);
                    } else {
                         setCallLockerVisibility(GONE);
                    }
                    break;
          }

     }

     private void dispatchTriggerEvent(int whichHandle) {
          if (onTriggerListener != null) {
               onTriggerListener.onTrigger(whichHandle, currentCall);
          }
     }


     @Override
     public void onLeftRightChoice(int whichHandle) {
          LogUtil.getUtils(THIS_FILE).d("Call controls receive info from slider " + whichHandle);
          if (controlMode != MODE_LOCKER) {
               // Oups we are not in locker mode and we get a trigger from
               // locker...
               // Should not happen... but... to be sure
               return;
          }
          switch (whichHandle) {
               case LEFT_HANDLE:
                    LogUtil.getUtils(THIS_FILE).d("We take the call");
                    dispatchTriggerEvent(IOnCallActionTrigger.TAKE_CALL);
                    break;
               case RIGHT_HANDLE:
                    LogUtil.getUtils(THIS_FILE).d("We clear the call");
                    dispatchTriggerEvent(IOnCallActionTrigger.DONT_TAKE_CALL);
               default:
                    break;
          }
     }

     @Override
     public boolean onKeyDown(int keyCode, KeyEvent event) {
          LogUtil.getUtils(THIS_FILE).d("Hey you hit the key : " + keyCode);
          if (controlMode == MODE_LOCKER) {
               switch (keyCode) {
                    case KeyEvent.KEYCODE_CALL:
                         dispatchTriggerEvent(IOnCallActionTrigger.TAKE_CALL);
                         return true;
                    case KeyEvent.KEYCODE_ENDCALL:
                         //case KeyEvent.KEYCODE_POWER:
                         dispatchTriggerEvent(IOnCallActionTrigger.REJECT_CALL);
                         return true;
                    default:
                         break;
               }
          }
          return super.onKeyDown(keyCode, event);
     }

}
