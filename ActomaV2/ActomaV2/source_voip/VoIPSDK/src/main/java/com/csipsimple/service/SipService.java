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
 */

package com.csipsimple.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.csipsimple.api.ISipConfiguration;
import com.csipsimple.api.ISipService;
import com.csipsimple.api.MediaState;
import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipConfigManager;
import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipManager.PresenceStatus;
import com.csipsimple.api.SipProfile;
import com.csipsimple.api.SipProfileState;
import com.csipsimple.db.DBProvider;
import com.csipsimple.models.Filter;
import com.csipsimple.pjsip.PjSipCalls;
import com.csipsimple.pjsip.PjSipService;
import com.csipsimple.pjsip.UAStateReceiver;
import com.csipsimple.service.receiver.DynamicReceiver4;
import com.csipsimple.service.receiver.DynamicReceiver5;
import com.csipsimple.utils.Compatibility;
import com.csipsimple.utils.CustomDistribution;
import com.csipsimple.utils.ExtraPlugins;
import com.csipsimple.utils.ExtraPlugins.DynActivityPlugin;
import com.csipsimple.utils.PreferencesProviderWrapper;
import com.csipsimple.utils.PreferencesWrapper;
import com.securevoip.ui.incall.InCallMediaControl;
import com.securevoip.voip.PhoneManager;
import com.securevoip.voip.bean.IncomingInfo;
import com.xdja.comm.server.AccountServer;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import webrelay.VOIPManager;



public class SipService extends Service {


    // static boolean creating = false;
    private static final String THIS_FILE = "SIP SRV";

    private SipWakeLock sipWakeLock;
    /**
     * 自动应答
     */
    private boolean autoAcceptCurrent = false;
    /**
     * 是否支持多个呼叫
     */
    public boolean supportMultipleCalls = false;

    private static SipService singleton = null;

    @SuppressLint("AndroidLintSdCardPath")
    public static final String APPLICATION_FILES_PATH = "/data/data/com.xdja.securevoip/files";


    // Implement public interface for the service

    // xjq
    public ISipService.Stub getBinder() {
        return binder;
    }
    private final ISipService.Stub binder = new ISipService.Stub() {


        @Override
        public void setMediaState(final MediaState state) {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                public void doRun() throws SameThreadException {
                    pjService.setMediaState(state);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void sipStart() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            LogUtil.getUtils(THIS_FILE).d("Start required from third party app/serv");
            getExecutor().execute(new StartRunnable());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void sipStop() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new StopRunnable());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void forceStopService() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            LogUtil.getUtils(THIS_FILE).d("Try to force service stop");
            cleanStop();
            //stopSelf();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void askThreadedRestart() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            LogUtil.getUtils(THIS_FILE).d("Restart required from third part app/serv");
            getExecutor().execute(new RestartRunnable());
        }

        public void addCustAccount(final String addr) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                public void doRun() throws SameThreadException {
                    pjService.addCustAccount(addr);
                }
            });
        }

        public void custAccountOnline() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                public void doRun() throws SameThreadException {
                    pjService.setCustAccountOnline();
                }
            });
        }

        @Override
        public void custAccountOffline() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                public void doRun() throws SameThreadException {
                    pjService.setCustAccountOffline();
                }
            });
        }

        @Override
        public void delCustAccount() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                public void doRun() throws SameThreadException {
                    pjService.delCustAccount();
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAllAccounts() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                public void doRun() throws SameThreadException {
                    SipService.this.addAllAccounts();
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void removeAllAccounts() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                public void doRun() throws SameThreadException {
                    SipService.this.unregisterAllAccounts(true);
                }
            });
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void reAddAllAccounts() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                public void doRun() throws SameThreadException {
                    SipService.this.reAddAllAccounts();

                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setAccountRegistration(int accountId, int renew) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);

            final SipProfile acc = getAccount(accountId);
            if(acc != null) {
                final int ren = renew;
                getExecutor().execute(new SipRunnable() {
                    @Override
                    public void doRun() throws SameThreadException {
                        SipService.this.setAccountRegistration(acc, ren, false);
                    }
                });
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SipProfileState getSipProfileState(int accountId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            return SipService.this.getSipProfileState(accountId);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void switchToAutoAnswer() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            LogUtil.getUtils(THIS_FILE).d("Switch to auto answer");
            setAutoAnswerNext(true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void makeCall(final String callee, final int accountId) throws RemoteException {

//            SipCallSession callInfo= new SipCallSession();
//            SipCallSession toSendInfo = new SipCallSession(callInfo);
//            Intent intent = new Intent(SipManager.ACTION_SIP_CALL_UI);
//            intent.putExtra(SipManager.EXTRA_CALL_INFO, toSendInfo);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);

            makeCallWithOptions(callee, accountId, null);
        }


        @Override
        public void makeCallWithOptions(final String callee, final int accountId, final Bundle options)
                throws RemoteException {
            LogUtil.getUtils(THIS_FILE).e("makeCallWithOptions callee=" + callee + " accountId=" + Integer.toString(accountId));
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            //We have to ensure service is properly started and not just binded
            SipService.this.startService(new Intent(SipService.this, SipService.class));

            if(pjService == null) {
                LogUtil.getUtils(THIS_FILE).e("Can't place call if service not started");
                // TODO - we should return a failing status here
                return;
            }

            if(!supportMultipleCalls) {
                // Check if there is no ongoing calls if so drop this request by alerting user
                SipCallSession activeCall = pjService.getActiveCallInProgress();
                if(activeCall != null) {
                    if(!CustomDistribution.forceNoMultipleCalls()) {
                        notifyUserOfMessage(R.string.not_configured_multiple_calls);
                    }
                    return;
                }
            }

            /* BEGIN:如果系统电话正在通话中，不允许新的呼叫。 Add by xjq, 2015/3/26 */
            if(getGSMCallState() != TelephonyManager.CALL_STATE_IDLE) {
                notifyUserOfMessage(R.string.not_configured_multiple_calls);
                return;
            }
            /* END:如果系统电话正在通话中，不允许新的呼叫。 Add by xjq, 2015/3/26 */

            getExecutor().execute(new SipRunnable() {
                                      @Override
                                      protected void doRun() throws SameThreadException {
                                          // TODO Auto-generated method stub
                                          LogUtil.getUtils(THIS_FILE).e("走到了SipService的makeCall");
                                          pjService.makeCall(callee, accountId, options);
                                      }
                                  }
            );
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void sendMessage(final String message, final String callee, final long accountId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            //We have to ensure service is properly started and not just binded
            SipService.this.startService(new Intent(SipService.this, SipService.class));

//			getExecutor().execute(new SipRunnable() {
//				@Override
//				protected void doRun() throws SameThreadException {
//					Log.d(THIS_FILE, "will sms " + callee);
//					if(pjService != null) {
//    					ToCall called = pjService.sendMessage(callee, message, accountId);
//    					if(called!=null) {
//    						SipMessage msg = new SipMessage(SipMessage.SELF, 
//    								SipUri.getCanonicalSipContact(callee), SipUri.getCanonicalSipContact(called.getCallee()), 
//    								message, "text/plain", System.currentTimeMillis(), 
//    								SipMessage.MESSAGE_TYPE_QUEUED, called.getCallee());
//    						msg.setRead(true);
//    						getContentResolver().insert(SipMessage.MESSAGE_URI, msg.getContentValues());
//    						Log.d(THIS_FILE, "Inserted "+msg.getTo());
//    					}else {
//    						SipService.this.notifyUserOfMessage( getString(R.string.invalid_sip_uri)+ " : "+callee );
//    					}
//					}else {
//					    SipService.this.notifyUserOfMessage( getString(R.string.connection_not_valid) );
//					}
//				}
//			});
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public int answer(final int callId, final int status) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            ReturnRunnable action = new ReturnRunnable() {
                @Override
                protected Object runWithReturn() throws SameThreadException {
                    return pjService.callAnswer(callId, status);
                }
            };
            getExecutor().execute(action);
            //return (Integer) action.getResult();
            return SipManager.SUCCESS;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public int hangup(final int callId, final int status) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            ReturnRunnable action = new ReturnRunnable() {
                @Override
                protected Object runWithReturn() throws SameThreadException {
                    return pjService.callHangup(callId, status);
                }
            };
            getExecutor().execute(action);
            //return (Integer) action.getResult();

            return SipManager.SUCCESS;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int xfer(final int callId, final String callee) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            LogUtil.getUtils(THIS_FILE).d("XFER");
            ReturnRunnable action = new ReturnRunnable() {
                @Override
                protected Object runWithReturn() throws SameThreadException {
                    return pjService.callXfer(callId, callee);
                }
            };
            getExecutor().execute(action);
            return (Integer) action.getResult();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int xferReplace(final int callId, final int otherCallId, final int options) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            LogUtil.getUtils(THIS_FILE).d("XFER-replace");
            ReturnRunnable action = new ReturnRunnable() {
                @Override
                protected Object runWithReturn() throws SameThreadException {
                    return pjService.callXferReplace(callId, otherCallId, options);
                }
            };
            getExecutor().execute(action);
            return (Integer) action.getResult();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int sendDtmf(final int callId, final int keyCode) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);

            ReturnRunnable action = new ReturnRunnable() {
                @Override
                protected Object runWithReturn() throws SameThreadException {
                    return pjService.sendDtmf(callId, keyCode);
                }
            };
            getExecutor().execute(action);
            return (Integer) action.getResult();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hold(final int callId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            LogUtil.getUtils(THIS_FILE).d("HOLDING");
            ReturnRunnable action = new ReturnRunnable() {
                @Override
                protected Object runWithReturn() throws SameThreadException {
                    return pjService.callHold(callId);
                }
            };
            getExecutor().execute(action);
            return (Integer) action.getResult();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int reinvite(final int callId, final boolean unhold) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            LogUtil.getUtils(THIS_FILE).d("REINVITING");
            ReturnRunnable action = new ReturnRunnable() {
                @Override
                protected Object runWithReturn() throws SameThreadException {
                    return pjService.callReinvite(callId, unhold);
                }
            };
            getExecutor().execute(action);
            return (Integer) action.getResult();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SipCallSession getCallInfo(final int callId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            return new SipCallSession(pjService.getCallInfo(callId));
        }

		/* BEGIN: Set call hanging state. add by xjq, 2015/1/28 */
        /**
         * {@inheritDoc}
         */
        @Override
        public void setCallHangingState(int callId, boolean isHanging)
                throws RemoteException {
            // TODO Auto-generated method stub
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            SipCallSession session = pjService.getCallInfo(callId);
            if(session != null) // 非空判断
                session.setIsHanging(isHanging);
        }
		/* END: Set call hanging state. add by xjq, 2015/1/28 */

        // Set call state over. Add by xjq, 2015/3/3
        @Override
        public void setCallOverState(int callId, boolean isOver)
                throws RemoteException {
            // TODO Auto-generated method stub
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            //pjService.stopRingback(callId); // Stop ring back. Add by xjq, 2015/3/11
            SipCallSession session = pjService.getCallInfo(callId);
            if(session != null) // 非空判断
                session.setOver(isOver);
        }

        // Set incoming state. Add by xjq, 2015/3/3
        @Override
        public void setIncoming(int callId, boolean isIncoming)
                throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            SipCallSession session = pjService.getCallInfo(callId);
            if(session != null) // 非空判断
                session.setIncoming(isIncoming);
        }

        // 关闭媒体。Add by xjq, 2015-08-17
        // 使用线程完成 xjq 2015-08-31
        @Override
        public void setNoSnd() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.setNoSnd();
                }
            });
        }

        // 打开媒体。Add by xjq, 2015-08-17
        // 使用线程完成 xjq 2015-08-31
        @Override
        public void setSnd() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.setSnd();
                }
            });
        }

        /**
         * restore audio mode xjq
         * @throws RemoteException
         */
        @Override
        public void unsetAudioInCall() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.unsetAudioInCall();

                    if (pjService.mediaManager != null) {
                        pjService.mediaManager.stopRingAndUnfocus();
                        pjService.mediaManager.resetSettings();
                    }
                }
            });
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void setBluetoothOn(final boolean on) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.setBluetoothOn(on);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setMicrophoneMute(final boolean on) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.setMicrophoneMute(on);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setSpeakerphoneOn(final boolean on) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.setSpeakerphoneOn(on);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setHeadsetOn(final boolean on) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.setHeadsetOn(on);
                }
            });
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public SipCallSession[] getCalls() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            if(pjService != null) {
                SipCallSession[] listOfCallsImpl = pjService.getCalls();
                SipCallSession[] result = new SipCallSession[listOfCallsImpl.length];
                for(int sessIdx = 0; sessIdx < listOfCallsImpl.length; sessIdx++) {
                    result[sessIdx] = new SipCallSession(listOfCallsImpl[sessIdx]);
                }
                return result;
            }
            return new SipCallSession[0];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void confAdjustTxLevel(final int port, final float value) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    if(pjService == null) {
                        return;
                    }
                    pjService.confAdjustTxLevel(port, value);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void confAdjustRxLevel(final int port, final float value) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    if(pjService == null) {
                        return;
                    }
                    pjService.confAdjustRxLevel(port, value);
                }
            });

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void adjustVolume(SipCallSession callInfo, int direction, int flags) throws RemoteException {

            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);

            if(pjService == null) {
                return;
            }

            boolean ringing = callInfo.isIncoming() && callInfo.isBeforeConfirmed();
            // Mode ringing
            if(ringing) {
                // What is expected here is to silence ringer
                //pjService.adjustStreamVolume(AudioManager.STREAM_RING, direction, AudioManager.FLAG_SHOW_UI);
                pjService.silenceRinger();
            }else {
                // Mode in call
                if(prefsWrapper.getPreferenceBooleanValue(SipConfigManager.USE_SOFT_VOLUME)) {
                    Intent adjustVolumeIntent = new Intent(SipService.this, InCallMediaControl.class);
                    adjustVolumeIntent.putExtra(Intent.EXTRA_KEY_EVENT, direction);
                    adjustVolumeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(adjustVolumeIntent);
                }else {

                    // 修改此处的判断逻辑 xjq 2015-11-16
                    int stream = AudioManager.STREAM_VOICE_CALL;
                    MediaState ms = pjService.mediaManager.getMediaState();

                    if(pjService.mediaManager.doesUserWantBluetooth() && ms.canBluetoothSco)
                        stream = 6;

                    pjService.adjustStreamVolume(stream, direction, flags);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setEchoCancellation(final boolean on) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            if(pjService == null) {
                return;
            }
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.setEchoCancellation(on);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void startRecording(final int callId, final int way) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            if (pjService == null) {
                return;
            }
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.startRecording(callId, way);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void stopRecording(final int callId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            if (pjService == null) {
                return;
            }
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.stopRecording(callId);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressLint("SimplifiableIfStatement")
        public boolean isRecording(int callId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            if(pjService == null) {
                return false;
            }

            SipCallSession info = pjService.getCallInfo(callId);
            if(info != null) {
                return info.isRecording();
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressLint("SimplifiableIfStatement")
        public boolean canRecord(int callId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            if(pjService == null) {
                return false;
            }
            return pjService.canRecord(callId);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void playWaveFile(final String filePath, final int callId, final int way) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            if(pjService == null) {
                return;
            }
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.playWaveFile(filePath, callId, way);
                }
            });
        }

        @Override
        public void stopPlaying(final int callId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            if (pjService == null) {
                return;
            }
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.stopPlaying(callId);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setPresence(final int presenceInt, final String statusText, final long accountId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            if(pjService == null) {
                return;
            }
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    presence = PresenceStatus.values()[presenceInt];
                    pjService.setPresence(presence, statusText, accountId);
                }
            });
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public int getPresence(long accountId) throws RemoteException {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getPresenceStatus(long accountId) throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void zrtpSASVerified(final int callId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.zrtpSASVerified(callId);
                    pjService.zrtpReceiver.updateZrtpInfos(callId);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void zrtpSASRevoke(final int callId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.zrtpSASRevoke(callId);
                    pjService.zrtpReceiver.updateZrtpInfos(callId);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MediaState getCurrentMediaState() throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            MediaState ms = new MediaState();
            if(pjService != null && pjService.mediaManager != null) {
                ms = pjService.mediaManager.getMediaState();
            }
            return ms;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public int getVersion() throws RemoteException {
            return SipManager.CURRENT_API;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String showCallInfosDialog(final int callId) throws RemoteException {
            ReturnRunnable action = new ReturnRunnable() {
                @Override
                protected Object runWithReturn() throws SameThreadException {
                    String infos = PjSipCalls.dumpCallInfo(callId);
                    LogUtil.getUtils(THIS_FILE).d(infos);
                    return infos;
                }
            };

            getExecutor().execute(action);
            return (String) action.getResult();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int startLoopbackTest() throws RemoteException {
            if(pjService == null) {
                return SipManager.ERROR_CURRENT_NETWORK;
            }
            SipRunnable action = new SipRunnable() {

                @Override
                protected void doRun() throws SameThreadException {
                    pjService.startLoopbackTest();
                }
            };

            getExecutor().execute(action);
            return SipManager.SUCCESS;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int stopLoopbackTest() throws RemoteException {
            if(pjService == null) {
                return SipManager.ERROR_CURRENT_NETWORK;
            }
            SipRunnable action = new SipRunnable() {

                @Override
                protected void doRun() throws SameThreadException {
                    pjService.stopLoopbackTest();
                }
            };

            getExecutor().execute(action);
            return SipManager.SUCCESS;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long confGetRxTxLevel(final int port) throws RemoteException {
            ReturnRunnable action = new ReturnRunnable() {
                @Override
                protected Object runWithReturn() throws SameThreadException {
                    return pjService.getRxTxLevel(port);
                }
            };
            getExecutor().execute(action);
            return (Long) action.getResult();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void ignoreNextOutgoingCallFor(String number) throws RemoteException {
//            OutgoingCall.ignoreNext = number;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void updateCallOptions(final int callId, final Bundle options) throws RemoteException {
            getExecutor().execute(new SipRunnable() {
                @Override
                protected void doRun() throws SameThreadException {
                    pjService.updateCallOptions(callId, options);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getLocalNatType() throws RemoteException {
            ReturnRunnable action = new ReturnRunnable() {
                @Override
                protected Object runWithReturn() throws SameThreadException {
                    return pjService.getDetectedNatType();
                }
            };
            getExecutor().execute(action);
            return (String) action.getResult();
        }

        @Override
        @SuppressLint("ForCanBeForeach")
        public SipCallSession getPNCall(String callId) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_USE_SIP, null);
            if (pjService != null) {
                SipCallSession[] listOfCallsImpl = pjService.getCalls();
                SipCallSession result = null;
                for (int sessIdx = 0; sessIdx < listOfCallsImpl.length; sessIdx++) {
                    String sipCallId = listOfCallsImpl[sessIdx].getCallIdString();
                    if (sipCallId != null && sipCallId.equals(callId)) {
                        result = new SipCallSession(listOfCallsImpl[sessIdx]);
                        return result;
                    }
                }
            }
            return null;
        }

        @Override
        public void startRing(String contact) throws RemoteException {
            if (pjService.mediaManager != null) {
                if (pjService.service.getGSMCallState() == TelephonyManager.CALL_STATE_IDLE) {
                    pjService.mediaManager.startRing(contact);
                } else {
                    pjService.mediaManager.playInCallTone(MediaManager.TONE_CALL_WAITING);
                }
            }
            UAStateReceiver uaStateReceiver = getUAStateReceiver();
            if(uaStateReceiver != null) {
                uaStateReceiver.broadCastAndroidCallState("RINGRING", contact);
            }
        }

        @Override
        public void stopRing(String contact) throws RemoteException {
            UAStateReceiver uaStateReceiver=getUAStateReceiver();
            if (uaStateReceiver!=null){
                uaStateReceiver.broadCastAndroidCallState("OFFHOOK", contact);
            }
            if (pjService.mediaManager != null) {
                pjService.mediaManager.stopRing();
            }
        }

    };

    private final ISipConfiguration.Stub binderConfiguration = new ISipConfiguration.Stub() {

        @Override
        public void setPreferenceBoolean(String key, boolean value) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_CONFIGURE_SIP, null);
            prefsWrapper.setPreferenceBooleanValue(key, value);
        }

        @Override
        public void setPreferenceFloat(String key, float value) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_CONFIGURE_SIP, null);
            prefsWrapper.setPreferenceFloatValue(key, value);

        }

        @Override
        public void setPreferenceString(String key, String value) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_CONFIGURE_SIP, null);
            prefsWrapper.setPreferenceStringValue(key, value);

        }

        @Override
        public String getPreferenceString(String key) throws RemoteException {
            return prefsWrapper.getPreferenceStringValue(key);

        }

        @Override
        public boolean getPreferenceBoolean(String key) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_CONFIGURE_SIP, null);
            return prefsWrapper.getPreferenceBooleanValue(key);

        }

        @Override
        public float getPreferenceFloat(String key) throws RemoteException {
            SipService.this.enforceCallingOrSelfPermission(SipManager.PERMISSION_CONFIGURE_SIP, null);
            return prefsWrapper.getPreferenceFloatValue(key);
        }

    };

    private WakeLock wakeLock;
    private WifiLock wifiLock;
    private DynamicReceiver4 deviceStateReceiver;
    private PreferencesProviderWrapper prefsWrapper;
    private ServicePhoneStateReceiver phoneConnectivityReceiver;
    private TelephonyManager telephonyManager;
//	private ConnectivityManager connectivityManager;

    public SipNotifications notificationManager;

    // xjq 2015-09-12
    private final SipServiceExecutor mExecutor = new SipServiceExecutor(this);

    private static PjSipService pjService;
    private static HandlerThread executorThread;

    private AccountStatusContentObserver statusObserver = null;
    private BroadcastReceiver serviceReceiver;

    class AccountStatusContentObserver extends ContentObserver {
        public AccountStatusContentObserver(Handler h) {
            super(h);
        }

        public void onChange(boolean selfChange) {
            LogUtil.getUtils(THIS_FILE).d("Accounts status.onChange( " + selfChange + ")");
            updateRegistrationsState();
        }
    }


    public SipServiceExecutor getExecutor() {
        // create mExecutor lazily
        return mExecutor;
    }

    private class ServicePhoneStateReceiver extends PhoneStateListener {

        //private boolean ignoreFirstConnectionState = true;
        private boolean ignoreFirstCallState = true;
		/*
		@Override
		public void onDataConnectionStateChanged(final int state) {
			if(!ignoreFirstConnectionState) {
				Log.d(THIS_FILE, "Data connection state changed : " + state);
				Thread t = new Thread("DataConnectionDetach") {
					@Override
					public void run() {
						if(deviceStateReceiver != null) {
							deviceStateReceiver.onChanged("MOBILE", state == TelephonyManager.DATA_CONNECTED);
						}
					}
				};
				t.start();
			}else {
				ignoreFirstConnectionState = false;
			}
			super.onDataConnectionStateChanged(state);
		}
		*/

        @Override
        public void onCallStateChanged(final int state, final String incomingNumber) {
            if(!ignoreFirstCallState) {
                LogUtil.getUtils(THIS_FILE).d("Call state has changed !" + state + " : " + incomingNumber);
                getExecutor().execute(new SipRunnable() {

                    @Override
                    protected void doRun() throws SameThreadException {
                        if(pjService != null) {
                            pjService.onGSMStateChanged(state, incomingNumber);
                        }
                    }
                });
            }else {
                ignoreFirstCallState = false;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        LogUtil.getUtils(THIS_FILE).i("Create SIP Service");

        prefsWrapper = new PreferencesProviderWrapper(this);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        notificationManager = new SipNotifications();
        sipWakeLock = new SipWakeLock((PowerManager) getSystemService(Context.POWER_SERVICE));

        boolean hasSetup = prefsWrapper.getPreferenceBooleanValue(PreferencesProviderWrapper.HAS_ALREADY_SETUP_SERVICE, false);
        LogUtil.getUtils(THIS_FILE).d("Service has been setup ? " + hasSetup);

        registerServiceBroadcasts();

        if(!hasSetup) {
            LogUtil.getUtils(THIS_FILE).e("RESET SETTINGS !!!!");
            prefsWrapper.resetAllDefaultValues();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.getUtils(THIS_FILE).i("Destroying SIP Service");
        unregisterBroadcasts();
        unregisterServiceBroadcasts();
        notificationManager.onServiceDestroy();
        VOIPManager.getInstance().onServiceStateChanged(false);
        getExecutor().execute(new FinalizeDestroyRunnable());

        /** 20160911-mengbo-start: Service结束后注销广播，避免crash **/
        if (pjService != null && pjService.mediaManager != null) {
            pjService.mediaManager.unregisterReceiver();
            LogUtil.getUtils(THIS_FILE).d("Destroying SIP Service mediaManager unregisterReceiver");
        }
        /** 20160911-mengbo-end **/
    }

    public void cleanStop () {
        getExecutor().execute(new DestroyRunnable());
    }

    private void applyComponentEnablingState(boolean active) {
        int enableState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        if(active && prefsWrapper.getPreferenceBooleanValue(SipConfigManager.INTEGRATE_TEL_PRIVILEGED) ) {
            // Check whether we should register for stock tel: intents
            // Useful for devices without gsm
            enableState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        }
        PackageManager pm = getPackageManager();

        ComponentName cmp = new ComponentName(this, "com.csipsimple.ui.PrivilegedOutgoingCallBroadcaster");
        try {
            if (pm.getComponentEnabledSetting(cmp) != enableState) {
                pm.setComponentEnabledSetting(cmp, enableState, PackageManager.DONT_KILL_APP);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            LogUtil.getUtils(THIS_FILE).d("Current manifest has no PrivilegedOutgoingCallBroadcaster -- you can ignore this if voluntary");
        }
    }

    private void registerServiceBroadcasts() {
        if(serviceReceiver == null) {
            IntentFilter intentfilter = new IntentFilter();
            intentfilter.addAction(SipManager.ACTION_DEFER_OUTGOING_UNREGISTER);
            intentfilter.addAction(SipManager.ACTION_OUTGOING_UNREGISTER);
            intentfilter.addAction(SipManager.ACTION_SIP_CALLING2);//拨打电话广播
            //intentfilter.addAction(SipManager.ACTION_ACTOMA_ALARM);//全局心跳广播 xjq

            serviceReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    switch (action) {
                        case SipManager.ACTION_OUTGOING_UNREGISTER :{
                            unregisterForOutgoing((ComponentName) intent.getParcelableExtra(SipManager.EXTRA_OUTGOING_ACTIVITY));
                            break;
                        }

                        case SipManager.ACTION_DEFER_OUTGOING_UNREGISTER :{
                            deferUnregisterForOutgoing((ComponentName) intent.getParcelableExtra(SipManager.EXTRA_OUTGOING_ACTIVITY));
                            break;
                        }

                        case SipManager.ACTION_SIP_CALLING2 :{

                            String num = intent.getStringExtra(SipManager.CALL_NUM);
                            String user= AccountServer.getAccount().getAccount();
                            if (user==null || user.equals("") || num==null || num.equals(""))
                                return;
                            VOIPManager.getInstance().makeCall(num, user);
                            break;
                        }

//                        case SipManager.ACTION_ACTOMA_ALARM : {
//                            break;
//                        }

                    }
                }

            };
            registerReceiver(serviceReceiver, intentfilter);
        }
    }

    private void buildAndCall(String num, Context context) {
        IncomingInfo ii = new IncomingInfo();
        ii.setPhoneNum(num);
        ii.setUserId("1");
        if (PhoneManager.getInstance().getLineState(ii, context)) {
            LogUtil.getUtils(THIS_FILE).e("ACTION_SIP_CALLING  num:" + num);
            if (num != null && !num.equals("")) {  //判断电话号码和账号ID 是否合法
                try {
                    binder.makeCall(num, 1);  //调用sip的协议
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void unregisterServiceBroadcasts() {
        if(serviceReceiver != null) {
            unregisterReceiver(serviceReceiver);
            serviceReceiver = null;
        }
    }

    /**
     * Register broadcast receivers.
     */
    private void registerBroadcasts() {
        // Register own broadcast receiver
        if (deviceStateReceiver == null) {
            IntentFilter intentfilter = new IntentFilter();
            intentfilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            intentfilter.addAction(SipManager.ACTION_SIP_ACCOUNT_CHANGED);

            // 重新添加默认账户 xjq 2015-09-12
            intentfilter.addAction(SipManager.ACTION_SIP_READD_DEFAULT_ACCOUNT);

            intentfilter.addAction(SipManager.ACTION_SIP_ACCOUNT_DELETED);
            intentfilter.addAction(SipManager.ACTION_SIP_CAN_BE_STOPPED);
            intentfilter.addAction(SipManager.ACTION_SIP_REQUEST_RESTART);
            intentfilter.addAction(DynamicReceiver4.ACTION_VPN_CONNECTIVITY);

            // screen lock xjq 2015-08-27
            intentfilter.addAction(Intent.ACTION_SCREEN_OFF);
            intentfilter.addAction(Intent.ACTION_SCREEN_ON);

            //20151014 zjc 清除未接来电的广播
            intentfilter.addAction(DynamicReceiver4.CANCEL_MISSED_CALL_NOTIFICATION);
            intentfilter.addAction(DynamicReceiver4.CANCEL_ONE_MISSED_CALL_NOTIFICATION);

            // 网络连接的广播 xjq
            intentfilter.addAction(SipManager.ACTION_NETWORK_CONNECT);

            // 停止sipservice
            intentfilter.addAction(SipManager.ACTION_STOP_SIPSERVICE);

            if(Compatibility.isCompatible(5)) {
                deviceStateReceiver = new DynamicReceiver5(this);
            }else {
                deviceStateReceiver = new DynamicReceiver4(this);
            }
            registerReceiver(deviceStateReceiver, intentfilter);
            deviceStateReceiver.startMonitoring();
        }
        // Telephony
        if (phoneConnectivityReceiver == null) {
            LogUtil.getUtils(THIS_FILE).d("Listen for phone state ");
            phoneConnectivityReceiver = new ServicePhoneStateReceiver();

            telephonyManager.listen(phoneConnectivityReceiver, /*PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
					| */PhoneStateListener.LISTEN_CALL_STATE );
        }
        // Content observer
        if(statusObserver == null) {
            statusObserver = new AccountStatusContentObserver(serviceHandler);

            //20170224-mengbo : 动态获取URI
            //getContentResolver().registerContentObserver(SipProfile.ACCOUNT_STATUS_URI, true, statusObserver);
            getContentResolver().registerContentObserver(SipProfile.getAccountStatusUri(SipService.this), true, statusObserver);
        }

    }

    /**
     * Remove registration of broadcasts receiver.
     */
    private void unregisterBroadcasts() {
        if(deviceStateReceiver != null) {
            try {
                LogUtil.getUtils(THIS_FILE).d("Stop and unregister device receiver");
                deviceStateReceiver.stopMonitoring();
                unregisterReceiver(deviceStateReceiver);
                deviceStateReceiver = null;
            } catch (IllegalArgumentException e) {
                // This is the case if already unregistered itself
                // Python style usage of try ;) : nothing to do here since it could
                // be a standard case
                // And in this case nothing has to be done
                LogUtil.getUtils(THIS_FILE).d("Has not to unregister telephony receiver");
            }
        }
        if (phoneConnectivityReceiver != null) {
            LogUtil.getUtils(THIS_FILE).d("Unregister telephony receiver");
            telephonyManager.listen(phoneConnectivityReceiver, PhoneStateListener.LISTEN_NONE);
            phoneConnectivityReceiver = null;
        }
        if(statusObserver != null) {
            getContentResolver().unregisterContentObserver(statusObserver);
            statusObserver = null;
        }

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if(intent != null) {
            Parcelable p = intent.getParcelableExtra(SipManager.EXTRA_OUTGOING_ACTIVITY);
            if(p != null) {
                ComponentName outActivity = (ComponentName) p;
                registerForOutgoing(outActivity);
            }
        }

        // Check connectivity, else just finish itself
        if (!isConnectivityValid()) {
            //notifyUserOfMessage(R.string.connection_not_valid);
            LogUtil.getUtils(THIS_FILE).d("Harakiri... we are not needed since no way to use self");
            cleanStop();
            return;
        }

        // Autostart the stack - make sure started and that receivers are there
        // NOTE : the stack may also be autostarted cause of phoneConnectivityReceiver
        if (!loadStack()) {
            return;
        }


        //if(directConnect) {
        getExecutor().execute(new StartRunnable());
			/*
		}else {
			Log.d(THIS_FILE, "Defered SIP start !!");
			NetworkInfo netInfo = (NetworkInfo) connectivityManager.getActiveNetworkInfo();
			if(netInfo != null) {
				String type = netInfo.getTypeName();
				NetworkInfo.State state = netInfo.getState();
				if(state == NetworkInfo.State.CONNECTED) {
					Log.d(THIS_FILE, ">> on changed connected");
					deviceStateReceiver.onChanged(type, true);
				}else if(state == NetworkInfo.State.DISCONNECTED) {
					Log.d(THIS_FILE, ">> on changed disconnected");
					deviceStateReceiver.onChanged(type, false);
				}
			}else {
				deviceStateReceiver.onChanged(null, false);
				Log.d(THIS_FILE, ">> on changed disconnected");
			}
		}
		*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags =  START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private List<ComponentName> activitiesForOutgoing = new ArrayList<>();
    private List<ComponentName> deferedUnregisterForOutgoing = new ArrayList<>();

    public void registerForOutgoing(ComponentName activityKey) {
        if (!activitiesForOutgoing.contains(activityKey)) {
            activitiesForOutgoing.add(activityKey);
        }
    }

    public void unregisterForOutgoing(ComponentName activityKey) {
        activitiesForOutgoing.remove(activityKey);

        if (!isConnectivityValid()) {
            cleanStop();
        }
    }

    public void deferUnregisterForOutgoing(ComponentName activityKey) {
        if (!deferedUnregisterForOutgoing.contains(activityKey)) {
            deferedUnregisterForOutgoing.add(activityKey);
        }
    }

    public void treatDeferUnregistersForOutgoing() {
        for (ComponentName cmp : deferedUnregisterForOutgoing) {
            activitiesForOutgoing.remove(cmp);
        }
        deferedUnregisterForOutgoing.clear();
        if (!isConnectivityValid()) {
            cleanStop();
        }
    }

    public boolean isConnectivityValid() {
        if (prefsWrapper.getPreferenceBooleanValue(PreferencesWrapper.HAS_BEEN_QUIT, false)) {
            return false;
        }
        boolean valid = prefsWrapper.isValidConnectionForIncoming();
        if (activitiesForOutgoing.size() > 0) {
            valid |= prefsWrapper.isValidConnectionForOutgoing();
        }
        return valid;
    }

    @SuppressLint("BooleanMethodIsAlwaysInverted")
    private boolean loadStack() {
        //Ensure pjService exists
        if (pjService == null) {
            pjService = new PjSipService();
        }
        pjService.setService(this);

        if (pjService.tryToLoadStack()) {
            return true;
        }
        return false;
    }


    @Override
    public IBinder onBind(Intent intent) {

        String serviceName = intent.getAction();
        LogUtil.getUtils(THIS_FILE).d("Action is " + serviceName);
        if (serviceName == null || serviceName.equalsIgnoreCase(SipManager.INTENT_SIP_SERVICE)) {
            LogUtil.getUtils(THIS_FILE).d("Service returned");
            return binder;
        } else if (serviceName.equalsIgnoreCase(SipManager.INTENT_SIP_CONFIGURATION)) {
            LogUtil.getUtils(THIS_FILE).d("Conf returned");
            return binderConfiguration;
        }
        LogUtil.getUtils(THIS_FILE).d("Default service (SipService) returned");
        return binder;
    }


//    /**
//     * 复制语音资源文件到app用户目录
//     * @param context
//     * @param lastStatuesCode
//     */
//    public void copyToSD(Context context, int lastStatuesCode) {
//        InputStream is = null;
//        FileOutputStream fos = null;
//
//        try {
//            String path = getApplicationContext().getFilesDir().getAbsolutePath();
//
//            int resourceId = 0;
//            String dbPathAndName = "";
//
//            switch (lastStatuesCode) {
//                case 404:
//                    resourceId = R.raw.not_fount_404;
//                    dbPathAndName = path + "/" + "not_found_404.wav";
//                    break;
//                case 486:
//                    resourceId = R.raw.busy_here_486;
//                    dbPathAndName = path + "/" + "busy_here_486.wav";
//                    break;
//
//                case 408:
//                    resourceId = R.raw.request_timeout_408;
//                    dbPathAndName = path + "/" + "request_timeout_408.wav";
//                    break;
//
//                default:
//                    break;
//            }
//
//            if (resourceId == 0) {
//                Log.e("", "参数有无，不存在文件");
//                return;
//            }
//
//            File file = new File(path);
//
//            if (!file.exists()) {
//                file.mkdir();
//            }
//
//            File dbFile = new File(dbPathAndName);
//            if (!dbFile.exists()) {
//                is = context.getResources().openRawResource(
//                        resourceId);
//                fos = new FileOutputStream(dbFile);
//
//                byte[] buffer = new byte[8 * 1024];// 8K
//                while (is.read(buffer) > 0)// >
//                {
//                    fos.write(buffer);
//                }
//            } else {
//                Log.e("", "文件已存在");
//            }
//
//        } catch (Exception e) {
//
//        } finally {
//            try {
//                if (is != null) {
//                    is.close();
//                }
//
//                if (fos != null) {
//                    fos.close();
//                }
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//
//            }
//        }
//
//    }
    //private KeepAliveTimer kaAlarm;
    // This is always done in SipExecutor thread
    private void startSipStack() throws SameThreadException {


        //Cache some prefs
        supportMultipleCalls = prefsWrapper.getPreferenceBooleanValue(SipConfigManager.SUPPORT_MULTIPLE_CALLS);

        // Make sure we start service. Modify by xjq, 2015/5/11
//        if(!isConnectivityValid()) {
//            // BEGIN: 不提示“选取的网络不允许路由设置" Add by xjq, 2015/4/2
//            //notifyUserOfMessage(R.string.connection_not_valid);
//            // END: 不提示“选取的网络不允许路由设置" Add by xjq, 2015/4/2
//            Log.e(THIS_FILE, "No need to start sip");
//            return;
//        }


        LogUtil.getUtils(THIS_FILE).d("Start was asked and we should actually start now");
        if (pjService == null) {
            LogUtil.getUtils(THIS_FILE).d("Start was asked and pjService in not there");
            if (!loadStack()) {
                LogUtil.getUtils(THIS_FILE).e("Unable to load SIP stack !! ");
                return;
            }
        }
        LogUtil.getUtils(THIS_FILE).d("Ask pjservice to start itself");


        //presenceMgr.startMonitoring(this);
        if (pjService.sipStart()) {
            // This should be done after in acquire resource
            // But due to http://code.google.com/p/android/issues/detail?id=21635
            // not a good idea
//            applyComponentEnablingState(true);

            registerBroadcasts();

            //20170224-mengbo : 动态获取URI
            //getContentResolver().delete(SipProfile.ACCOUNT_STATUS_URI, null, null);
            // 清除Content Provider中的在线记录 xjq 2015-10-16
            getContentResolver().delete(SipProfile.getAccountStatusUri(SipService.this), null, null);

            // 设置sip service binder xjq
            VOIPManager.getInstance().setSipService(this);

            VOIPManager.getInstance().onServiceStateChanged(true);

        }
    }

    /**
     * Safe stop the sip stack
     * @return true if can be stopped, false if there is a pending call and the sip service should not be stopped
     */
    public boolean stopSipStack() throws SameThreadException {

        Log.d("voip_disconnect", "SipService stop sip stack start");

        LogUtil.getUtils(THIS_FILE).d("Stop sip stack");
        //20150811 Modify By：xjq 老版本ACE调用下线会有问题，先经测试没有问题。
		boolean canStop = true;
		if(pjService != null) {
			canStop &= pjService.sipStop();
			/*
			if(canStop) {
				pjService = null;
			}
			*/
		}

        //mengbo 换打印
        Log.d("voip_disconnect", "Can stop sip stack?:" + canStop);

		if(canStop) {
		    //if(presenceMgr != null) {
		    //    presenceMgr.stopMonitoring();
		    //}

		    // Due to http://code.google.com/p/android/issues/detail?id=21635
            // exclude 14 and upper from auto disabling on stop.
//            if(!Compatibility.isCompatible(14)) {
//                applyComponentEnablingState(false);
//            }

            unregisterBroadcasts();
			releaseResources();
		}

		return canStop;

    }



    public void restartSipStack() throws SameThreadException {

        Log.d("voip_disconnect", "SipService restart sip stack");

        if(stopSipStack()) {
            startSipStack();
        }else {
            LogUtil.getUtils(THIS_FILE).e("Can't stop ... so do not restart ! ");
        }
    }

    /**
     * Notify user from a message the sip stack wants to transmit.
     * For now it shows a toaster
     * @param msg String message to display
     */
    public void notifyUserOfMessage(String msg) {
        serviceHandler.sendMessage(serviceHandler.obtainMessage(TOAST_MESSAGE, msg));
    }
    /**
     * Delay notify user from a message the sip stack wants to transmit.
     * For now it shows a toaster. Add by xjq, 2015/4/18
     * @param msg String message to display
     */
    public void notifyUserOfMessage(String msg, long delayMillis) {
        serviceHandler.sendMessageDelayed(serviceHandler.obtainMessage(TOAST_MESSAGE, msg), delayMillis);
    }

    /**
     * Notify user from a message the sip stack wants to transmit.
     * For now it shows a toaster
     * @param resStringId The id of the string resource to display
     */
    public void notifyUserOfMessage(int resStringId) {
        serviceHandler.sendMessage(serviceHandler.obtainMessage(TOAST_MESSAGE, resStringId, 0));
    }

    private boolean hasSomeActiveAccount = false;
    /**
     * Add accounts from database
     */
    private void addAllAccounts() throws SameThreadException {
        LogUtil.getUtils(THIS_FILE).d("We are adding all accounts right now....");

        boolean hasSomeSuccess = false;

        //20170224-mengbo : 动态获取URI
        //Cursor c = getContentResolver().query(SipProfile.ACCOUNT_URI, DBProvider.ACCOUNT_FULL_PROJECTION,
        //        SipProfile.FIELD_ACTIVE + "=?", new String[] {"1"}, null);
        Cursor c = getContentResolver().query(SipProfile.getAccountUri(SipService.this), DBProvider.ACCOUNT_FULL_PROJECTION,
                SipProfile.FIELD_ACTIVE + "=?", new String[] {"1"}, null);

        if (c != null) {
            try {
                int index = 0;
                if(c.getCount() > 0) {
                    c.moveToFirst();
                    do {
                        SipProfile account = new SipProfile(c);
                        if (pjService != null && pjService.addAccount(account) ) {
                            hasSomeSuccess = true;
                        }
                        index ++;
                    } while (c.moveToNext() && index < 10);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.getUtils(THIS_FILE).e("Error on looping over sip profiles");
            } finally {
                c.close();
            }
        }

        hasSomeActiveAccount = hasSomeSuccess;

        if (hasSomeSuccess) {
            acquireResources();

        } else {
            releaseResources();
            if (notificationManager != null) {
                notificationManager.cancelRegisters();
            }
        }
    }


    // xjq
    public int setCustAccountStatus(int online) throws SameThreadException{
        if (pjService != null) {
            if (online == 1)
                pjService.setCustAccountOnline();
            else
                pjService.setCustAccountOffline();

        }
        return 0;
    }

    public boolean setAccountRegistration(SipProfile account, int renew, boolean forceReAdd) throws SameThreadException {
        boolean status = false;
        if(pjService != null) {
            status = pjService.setAccountRegistration(account, renew, forceReAdd);
        }

        return status;
    }

	/**
	 * Remove accounts from database
	 */
	private void unregisterAllAccounts(boolean cancelNotification) throws SameThreadException {

		releaseResources();

        LogUtil.getUtils(THIS_FILE).d("Remove all accounts");

        //20170224-mengbo : 动态获取URI
        //Cursor c = getContentResolver().query(SipProfile.ACCOUNT_URI, DBProvider.ACCOUNT_FULL_PROJECTION, null, null, null);
        Cursor c = getContentResolver().query(SipProfile.getAccountUri(SipService.this), DBProvider.ACCOUNT_FULL_PROJECTION, null, null, null);
		if (c != null) {
			try {
				c.moveToFirst();
				do {
					SipProfile account = new SipProfile(c);
					setAccountRegistration(account, 0, false);
				} while (c.moveToNext() );
			} catch (Exception e) {
                e.printStackTrace();
                LogUtil.getUtils(THIS_FILE).e("Error on looping over sip profiles");
			} finally {
				c.close();
			}
		}


        if (notificationManager != null && cancelNotification) {
            notificationManager.cancelRegisters();
        }
    }

    private void reAddAllAccounts() throws SameThreadException {
        LogUtil.getUtils(THIS_FILE).d("RE REGISTER ALL ACCOUNTS");
        unregisterAllAccounts(false);
        addAllAccounts();
    }




    public SipProfileState getSipProfileState(int accountDbId) {
        final SipProfile acc = getAccount(accountDbId);
        if(pjService != null && acc != null) {
            return pjService.getProfileState(acc);
        }
        return null;
    }

    public void updateRegistrationsState() {
        LogUtil.getUtils(THIS_FILE).d("Update registration state");
        ArrayList<SipProfileState> activeProfilesState = new ArrayList<>();

        //20170224-mengbo : 动态获取URI
        //Cursor c = getContentResolver().query(SipProfile.ACCOUNT_STATUS_URI, null, null, null, null);
        Cursor c = getContentResolver().query(SipProfile.getAccountStatusUri(SipService.this), null, null, null, null);
        if (c != null) {
            try {
                if(c.getCount() > 0) {
                    c.moveToFirst();
                    do {
                        SipProfileState ps = new SipProfileState(c);
                        if(ps.isValidForCall()) {
                            activeProfilesState.add(ps);
                        }
                    } while ( c.moveToNext() );
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.getUtils(THIS_FILE).e("Error on looping over sip profiles");
            } finally {
                c.close();
            }
        }

        Collections.sort(activeProfilesState, SipProfileState.getComparator());



        // Handle status bar notification
        if (activeProfilesState.size() > 0 &&
                prefsWrapper.getPreferenceBooleanValue(SipConfigManager.ICON_IN_STATUS_BAR)) {
            // Testing memory / CPU leak as per issue 676
            //	for(int i=0; i < 10; i++) {
            //		Log.d(THIS_FILE, "Notify ...");
            //		try {
            //			Thread.sleep(6000);
            //		} catch (InterruptedException e) {
            //			e.printStackTrace();
            //		}
            //	}
        } else {
            notificationManager.cancelRegisters();
        }

        if(hasSomeActiveAccount) {
            acquireResources();
        }else {
            releaseResources();
        }
    }

    /**
     * Get the currently instanciated prefsWrapper (to be used by
     * UAStateReceiver)
     *
     * @return the preferenceWrapper instanciated
     */
    public PreferencesProviderWrapper getPrefs() {
        // Is never null when call so ok, just not check...
        return prefsWrapper;
    }

    //Binders for media manager to sip stack
    /**
     * Adjust tx software sound level
     * @param speakVolume volume 0.0 - 1.0
     */
    public void confAdjustTxLevel(float speakVolume) throws SameThreadException {
        if(pjService != null) {
            pjService.confAdjustTxLevel(0, speakVolume);
        }
    }
    /**
     * Adjust rx software sound level
     * @param speakVolume volume 0.0 - 1.0
     */
    public void confAdjustRxLevel(float speakVolume) throws SameThreadException {
        if(pjService != null) {
            pjService.confAdjustRxLevel(0, speakVolume);
        }
    }


    /**
     * Add a buddy to list 
     * @param buddyUri the sip uri of the buddy to add
     */
    public int addBuddy(String buddyUri) throws SameThreadException {
        int retVal = -1;
        if(pjService != null) {
            LogUtil.getUtils(THIS_FILE).d("Trying to add buddy " + buddyUri);
            retVal = pjService.addBuddy(buddyUri);
        }
        return retVal;
    }

    /**
     * Remove a buddy from buddies
     * @param buddyUri the sip uri of the buddy to remove
     */
    public void removeBuddy(String buddyUri) throws SameThreadException  {
        if(pjService != null) {
            pjService.removeBuddy(buddyUri);
        }
    }

    private boolean holdResources = false;
    /**
     * Ask to take the control of the wifi and the partial wake lock if
     * configured
     */
    private synchronized void  acquireResources() {
        if(holdResources) {
            return;
        }

        // Add a wake lock for CPU if necessary
        if (prefsWrapper.getPreferenceBooleanValue(SipConfigManager.USE_PARTIAL_WAKE_LOCK)) {
            PowerManager pman = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (wakeLock == null) {
                wakeLock = pman.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.csipsimple.SipService");
                wakeLock.setReferenceCounted(false);
            }
            // Extra check if set reference counted is false ???
            if (!wakeLock.isHeld()) {
                wakeLock.acquire();
            }
        }

        // Add a lock for WIFI if necessary
        WifiManager wman = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiLock == null) {
            int mode = WifiManager.WIFI_MODE_FULL;
            if(Compatibility.isCompatible(9) && prefsWrapper.getPreferenceBooleanValue(SipConfigManager.LOCK_WIFI_PERFS)) {
                mode = 0x3; // WIFI_MODE_FULL_HIGH_PERF
            }
            wifiLock = wman.createWifiLock(mode, "com.csipsimple.SipService");
            wifiLock.setReferenceCounted(false);
        }
        if (prefsWrapper.getPreferenceBooleanValue(SipConfigManager.LOCK_WIFI) && !wifiLock.isHeld()) {
            WifiInfo winfo = wman.getConnectionInfo();
            if (winfo != null) {
                DetailedState dstate = WifiInfo.getDetailedStateOf(winfo.getSupplicantState());
                // We assume that if obtaining ip addr, we are almost connected
                // so can keep wifi lock
                if (dstate == DetailedState.OBTAINING_IPADDR || dstate == DetailedState.CONNECTED) {
                    if (!wifiLock.isHeld()) {
                        wifiLock.acquire();
                    }
                }
            }
        }
        holdResources = true;
    }

    private synchronized void releaseResources() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
        holdResources = false;
    }




    private static final int TOAST_MESSAGE = 0;

    private Handler serviceHandler = new ServiceHandler(this);

    private static class ServiceHandler extends Handler {
        WeakReference<SipService> s;
        public ServiceHandler(SipService sipService) {
            s = new WeakReference<>(sipService);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SipService sipService = s.get();
            if(sipService == null) {
                return;
            }
            if (msg.what == TOAST_MESSAGE) {
                if (msg.arg1 != 0) {
                    Toast.makeText(sipService, msg.arg1, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(sipService, (String) msg.obj, Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public UAStateReceiver getUAStateReceiver() {
        return pjService.userAgentReceiver;
    }

    /** 20170103-mengbo-start add. 判断是否存在挂断中的会话**/
    @SuppressLint("SimplifiableIfStatement")
    public boolean hasHangingCallInProgress() {
        if(pjService != null){
            return pjService.hasHangingCallInProgress();
        }else{
            return false;
        }
    }
    /** 20170103-mengbo-end **/

    /** 20160908-mengbo-start add. 判断是否存在会话**/
    @SuppressLint("SimplifiableIfStatement")
    public boolean hasActiveSipCallSession() {
        if(pjService != null){
            return pjService.hasActiveCallInProgress();
        }else{
            return false;
        }
    }
    /** 20160908-mengbo-end **/

    /** 20170102-mengbo-start add. 获取存在的会话**/
    public SipCallSession getActiveCallInProgress() {
        if(pjService != null){
            return pjService.getActiveCallInProgress();
        }else{
            return null;
        }
    }
    /** 20170102-mengbo-end **/

    public int getGSMCallState() {
        return telephonyManager.getCallState();
    }

    public static final class ToCall {
        private Integer pjsipAccountId;
        private String callee;
        private String dtmf;

        public ToCall(Integer acc, String uri) {
            pjsipAccountId = acc;
            callee = uri;
        }
        public ToCall(Integer acc, String uri, String dtmfChars) {
            pjsipAccountId = acc;
            callee = uri;
            dtmf = dtmfChars;
        }
        /**
         * @return the pjsipAccountId
         */
        public Integer getPjsipAccountId() {
            return pjsipAccountId;
        }
        /**
         * @return the callee
         */
        public String getCallee() {
            return callee;
        }
        /**
         * @return the dtmf sequence to automatically dial for this call
         */
        public String getDtmf() {
            return dtmf;
        }
    }

    public SipProfile getAccount(long accountId) {
        // TODO : create cache at this point to not requery each time as far as it's a service query
        return SipProfile.getProfileFromDbId(this, accountId, DBProvider.ACCOUNT_FULL_PROJECTION);
    }

    // 查询数据库，获得当前唯一账号。Modify by xjq, 2015/4/6
    // 密信项目只有一个账号。Add by lixin.
    public SipProfile getDefaultAccount() {
        // TODO : create cache at this point to not requery each time as far as it's a service query
        //ArrayList<SipProfile> profileList = SipProfile.getAllProfiles(this, false);
        //return profileList.get(0);
        SipProfile acc = null;
        Cursor c = null;
        try{
            //20170224-mengbo : 动态获取URI
            //c = getContentResolver().query(SipProfile.ACCOUNT_URI, DBProvider.ACCOUNT_FULL_PROJECTION,
            //        null, null, null);
            c = getContentResolver().query(SipProfile.getAccountUri(SipService.this), DBProvider.ACCOUNT_FULL_PROJECTION,
                    null, null, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                acc = new SipProfile(c);
                LogUtil.getUtils(THIS_FILE).d("pn: register error.Default account id = " + acc.id +
                        " transport = " + acc.transport +
                        " data = " + acc.data);
            }
        }catch (Exception e){
            LogUtil.getUtils().e("sipservice getdfaultaccount error="+e.getMessage());
        }finally {
            if(c != null){
                c.close();
            }
        }

        return acc;
    }


    // Auto answer feature

    public void setAutoAnswerNext(boolean auto_response) {
        autoAcceptCurrent = auto_response;
    }

    /**
     * Should a current incoming call be answered.
     * A call to this method will reset internal state
     * @param remContact The remote contact to test
     * @param acc The incoming guessed account
     * @return the sip code to auto-answer with. If > 0 it means that an auto answer must be fired
     */
    public int shouldAutoAnswer(String remContact, SipProfile acc, Bundle extraHdr) {

        LogUtil.getUtils(THIS_FILE).d("Search if should I auto answer for " + remContact);
        int shouldAutoAnswer = 0;

        if(autoAcceptCurrent) {
            LogUtil.getUtils(THIS_FILE).d("I should auto answer this one !!! ");
            autoAcceptCurrent = false;
            return 200;
        }

        if(acc != null) {
            Pattern p = Pattern.compile("^(?:\")?([^<\"]*)(?:\")?[ ]*(?:<)?sip(?:s)?:([^@]*@[^>]*)(?:>)?", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(remContact);
            String number = remContact;
            if (m.matches()) {
                number = m.group(2);
            }
            shouldAutoAnswer = Filter.isAutoAnswerNumber(this, acc.id, number, extraHdr);

        }else {
            LogUtil.getUtils(THIS_FILE).e("Oupps... that come from an unknown account...");
            // If some user need to auto hangup if comes from unknown account, just needed to add local account and filter on it.
        }
        return shouldAutoAnswer;
    }

    // Media direct binders
    public void setNoSnd() throws SameThreadException {
        if (pjService != null) {
            pjService.setNoSnd();
        }
    }

    public void setSnd() throws SameThreadException {
        if (pjService != null) {
            pjService.setSnd();
        }
    }


    private static Looper createLooper() {
        //	synchronized (executorThread) {
        if(executorThread == null) {
            LogUtil.getUtils(THIS_FILE).d("Creating new handler thread");
            // ADT gives a fake warning due to bad parse rule.
            executorThread = new HandlerThread("SipService.Executor");
            executorThread.start();
        }
        //	}
        return executorThread.getLooper();
    }



    // Executes immediate tasks in a single executorThread.
    // Hold/release wake lock for running tasks
    public static class SipServiceExecutor extends Handler {
        WeakReference<SipService> handlerService;

        SipServiceExecutor(SipService s) {
            //super(createLooper());  xjq 2015-09-12
            super();
            handlerService = new WeakReference<>(s);
        }

        public void execute(Runnable task) {
            SipService s = handlerService.get();
            if(s != null) {
                s.sipWakeLock.acquire(task);
            }

            // xjq 2015-09-12
            Message.obtain(this, 0/* don't care */, task).sendToTarget();

        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof Runnable) {
                executeInternal((Runnable) msg.obj);
            } else {
                LogUtil.getUtils(THIS_FILE).w("can't handle msg: " + msg);
            }
        }

        private void executeInternal(Runnable task) {
            try {
                task.run();
            } catch (Throwable t) {
                t.printStackTrace();
                LogUtil.getUtils(THIS_FILE).e("run task: " + task);
            } finally {

                SipService s = handlerService.get();
                if(s != null) {
                    s.sipWakeLock.release(task);
                }
            }
        }
    }


    class StartRunnable extends SipRunnable {
        @Override
        protected void doRun() throws SameThreadException {
            startSipStack();
        }
    }


    class SyncStartRunnable extends ReturnRunnable {
        @Override
        protected Object runWithReturn() throws SameThreadException {
            startSipStack();
            return null;
        }
    }

    class StopRunnable extends SipRunnable {
        @Override
        protected void doRun() throws SameThreadException {

            Log.d("voip_disconnect", "SipService stop runnable");

            stopSipStack();
        }
    }

    class SyncStopRunnable extends ReturnRunnable {
        @Override
        protected Object runWithReturn() throws SameThreadException {

            Log.d("voip_disconnect", "SipService sync stop runnable");

            stopSipStack();
            return null;
        }
    }

    class RestartRunnable extends SipRunnable {
        @Override
        protected void doRun() throws SameThreadException {

            Log.d("voip_disconnect", "SipService restart runnable");

            if(stopSipStack()) {
                startSipStack();
            }else {
                LogUtil.getUtils(THIS_FILE).e("Can't stop ... so do not restart ! ");
            }
        }
    }

    class SyncRestartRunnable extends ReturnRunnable {
        @Override
        protected Object runWithReturn() throws SameThreadException {

            Log.d("voip_disconnect", "SipService sync restart runnable");

            if(stopSipStack()) {
                startSipStack();
            }else {
                LogUtil.getUtils(THIS_FILE).e("Can't stop ... so do not restart ! ");
            }
            return null;
        }
    }

    class DestroyRunnable extends SipRunnable {
        @Override
        protected void doRun() throws SameThreadException {

            Log.d("voip_disconnect", "SipService destroy runnable");

            if(stopSipStack()) {
                stopSelf();
            }
        }
    }

    class FinalizeDestroyRunnable extends SipRunnable {
        @Override
        protected void doRun() throws SameThreadException {

            //mExecutor = null; xjq 2015-09-12

            LogUtil.getUtils(THIS_FILE).d("Destroy sip stack");

            sipWakeLock.reset();

            Log.d("voip_disconnect", "SipService finalize destroy runnable");

            if(stopSipStack()) {
                notificationManager.cancelAll();
                notificationManager.cancelCalls();
            }else {
                LogUtil.getUtils(THIS_FILE).e("Somebody has stopped the service while there is an ongoing call !!!");
            }
			/* If we activate that we can get two concurrent executorThread 
			synchronized (executorThread) {
				HandlerThread currentHandlerThread = executorThread;
				executorThread = null;
				System.gc();
				// This is a little bit crappy, we are cutting were we sit.
				Threading.stopHandlerThread(currentHandlerThread, false);
			}
			*/

            // We will not go longer
            LogUtil.getUtils(THIS_FILE).i("--- SIP SERVICE DESTROYED ---");
        }
    }

    // Enforce same thread contract to ensure we do not call from somewhere else
    public class SameThreadException extends Exception {
        private static final long serialVersionUID = -905639124232613768L;

        public SameThreadException() {
            super("Should be launched from a single worker thread");
        }
    }

    public abstract static class SipRunnable  implements Runnable {
        protected abstract void doRun() throws SameThreadException;

        public void run() {
            try {
                doRun();
            }catch(SameThreadException e) {
                LogUtil.getUtils(THIS_FILE).e("Not done from same thread");
            }
        }
    }


    public abstract class ReturnRunnable extends SipRunnable {
        private Semaphore runSemaphore;
        private Object resultObject;

        public ReturnRunnable() {
            super();
            runSemaphore = new Semaphore(0);
        }

        public Object getResult() {
            try {
                runSemaphore.acquire();
            } catch (InterruptedException e) {
                LogUtil.getUtils(THIS_FILE).e("Can't acquire run semaphore... problem...");
            }
            return resultObject;
        }

        protected abstract Object runWithReturn() throws SameThreadException;

        @Override
        public void doRun() throws SameThreadException {
            setResult(runWithReturn());
        }

        private void setResult(Object obj) {
            resultObject = obj;
            runSemaphore.release();
        }
    }

    private static String UI_CALL_PACKAGE = null;
    public static Intent buildCallUiIntent(Context ctxt, SipCallSession callInfo) {
        // Resolve the package to handle call.
        if(UI_CALL_PACKAGE == null) {
            UI_CALL_PACKAGE = ctxt.getPackageName();
            try {
                Map<String, DynActivityPlugin> callsUis = ExtraPlugins.getDynActivityPlugins(ctxt, SipManager.ACTION_SIP_CALL_UI);
                String preferredPackage  = SipConfigManager.getPreferenceStringValue(ctxt, SipConfigManager.CALL_UI_PACKAGE, UI_CALL_PACKAGE);
                String packageName = null;
                boolean foundPref = false;
                for(String activity : callsUis.keySet()) {
                    packageName = activity.split("/")[0];
                    if(preferredPackage.equalsIgnoreCase(packageName)) {
                        UI_CALL_PACKAGE = packageName;
                        foundPref = true;
                        break;
                    }
                }
                if(!foundPref && !TextUtils.isEmpty(packageName)) {
                    UI_CALL_PACKAGE = packageName;
                }
            }catch(Exception e) {
                e.printStackTrace();
                LogUtil.getUtils(THIS_FILE).e("Error while resolving package");
            }
        }
        SipCallSession toSendInfo = new SipCallSession(callInfo);
        Intent intent = new Intent(SipManager.ACTION_SIP_CALL_UI);
        intent.putExtra(SipManager.EXTRA_CALL_INFO, toSendInfo);
        intent.setPackage(UI_CALL_PACKAGE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }


    public static void setVideoWindow(int callId, SurfaceView window, boolean local) {
        if(singleton != null) {
            if(local) {
                singleton.setCaptureVideoWindow(window);
            }else {
                singleton.setRenderVideoWindow(callId, window);
            }
        }
    }

    private void setRenderVideoWindow(final int callId, final SurfaceView window) {
        getExecutor().execute(new SipRunnable() {
            @Override
            protected void doRun() throws SameThreadException {
                pjService.setVideoAndroidRenderer(callId, window);
            }
        });
    }
    private void setCaptureVideoWindow(final SurfaceView window) {
        getExecutor().execute(new SipRunnable() {
            @Override
            protected void doRun() throws SameThreadException {
                pjService.setVideoAndroidCapturer(window);
            }
        });
    }

    private PresenceStatus presence = SipManager.PresenceStatus.ONLINE;
    /**
     * Get current last status for the user
     * @return
     */
    public PresenceStatus getPresence() {
        return presence;
    }


    // 停止响铃接口 xjq 2015-09-03
    public void stopRing() {
        getExecutor().execute(new SipRunnable() {
            @Override
            protected void doRun() {
                try {
                    pjService.mediaManager.stopRing();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
