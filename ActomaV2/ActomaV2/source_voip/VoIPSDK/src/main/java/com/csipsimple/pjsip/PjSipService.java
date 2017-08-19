/**
 * Copyright (C) 2010-2013 Regis Montoya (aka r3gis - www.r3gis.fr)
 * Copyright (C) 2012-2013 Dennis Guse (http://dennisguse.de)
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

package com.csipsimple.pjsip;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.SurfaceView;

import com.csipsimple.api.MediaState;
import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipConfigManager;
import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipManager.PresenceStatus;
import com.csipsimple.api.SipProfile;
import com.csipsimple.api.SipProfileState;
import com.csipsimple.api.SipUri;
import com.csipsimple.api.SipUri.ParsedSipContactInfos;
import com.csipsimple.pjsip.earlylock.EarlyLockModule;
import com.csipsimple.pjsip.player.IPlayerHandler;
import com.csipsimple.pjsip.player.impl.SimpleWavPlayerHandler;
import com.csipsimple.pjsip.recorder.IRecorderHandler;
import com.csipsimple.pjsip.recorder.impl.SimpleWavRecorderHandler;
import com.csipsimple.pjsip.reghandler.RegHandlerModule;
import com.csipsimple.pjsip.sipclf.SipClfModule;
import com.csipsimple.service.MediaManager;
import com.csipsimple.service.SipService;
import com.csipsimple.service.SipService.SameThreadException;
import com.csipsimple.service.SipService.SipRunnable;
import com.csipsimple.service.SipService.ToCall;
import com.csipsimple.utils.ExtraPlugins;
import com.csipsimple.utils.ExtraPlugins.DynCodecInfos;
import com.csipsimple.utils.PreferencesProviderWrapper;
import com.csipsimple.utils.PreferencesWrapper;
import com.csipsimple.utils.TimerWrapper;
import com.securevoip.pninter.PNMessageManager;
import com.securevoip.voip.PhoneManager;
import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.server.ActomaController;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.voipsdk.R;

import org.pjsip.pjsua.SWIGTYPE_p_pj_stun_auth_cred;
import org.pjsip.pjsua.csipsimple_config;
import org.pjsip.pjsua.dynamic_factory;
import org.pjsip.pjsua.pj_ice_sess_options;
import org.pjsip.pjsua.pj_pool_t;
import org.pjsip.pjsua.pj_qos_params;
import org.pjsip.pjsua.pj_str_t;
import org.pjsip.pjsua.pj_turn_tp_type;
import org.pjsip.pjsua.pjmedia_srtp_use;
import org.pjsip.pjsua.pjsip_ssl_method;
import org.pjsip.pjsua.pjsip_timer_setting;
import org.pjsip.pjsua.pjsip_tls_setting;
import org.pjsip.pjsua.pjsip_transport_type_e;
import org.pjsip.pjsua.pjsua;
import org.pjsip.pjsua.pjsuaConstants;
import org.pjsip.pjsua.pjsua_acc_info;
import org.pjsip.pjsua.pjsua_buddy_config;
import org.pjsip.pjsua.pjsua_call_flag;
import org.pjsip.pjsua.pjsua_call_setting;
import org.pjsip.pjsua.pjsua_call_vid_strm_op;
import org.pjsip.pjsua.pjsua_config;
import org.pjsip.pjsua.pjsua_logging_config;
import org.pjsip.pjsua.pjsua_media_config;
import org.pjsip.pjsua.pjsua_msg_data;
import org.pjsip.pjsua.pjsua_transport_config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import util.SPUtil;
import webrelay.VOIPManager;

@SuppressLint("SynchronizeOnNonFinalField")
public class PjSipService {
    private static final String THIS_FILE = "PjService";
    private static int DTMF_TONE_PAUSE_LENGTH = 300;
    private static int DTMF_TONE_WAIT_LENGTH = 2000;
    public SipService service;

    private boolean created = false;

    private boolean hasSipStack = false;
    private boolean sipStackIsCorrupted = false;
    private Integer localUdpAccPjId, localUdp6AccPjId,
            localTcpAccPjId, localTcp6AccPjId,
            localTlsAccPjId, localTls6AccPjId;
    public PreferencesProviderWrapper prefsWrapper;

    private Integer hasBeenHoldByGSM = null;

    /** 20170208-mengbo-start: 屏蔽不使用的变量 **/
    //private Integer hasBeenChangedRingerMode = null;
    /** 20170208-mengbo-end **/

    public UAStateReceiver userAgentReceiver;
    public ZrtpStateReceiver zrtpReceiver;
    public MediaManager mediaManager;

    private Timer tasksTimer;
    private SparseArray<String> dtmfToAutoSend = new SparseArray<>(5);
    private SparseArray<TimerTask> dtmfTasks = new SparseArray<>(5);
    private SparseArray<PjStreamDialtoneGenerator> dtmfDialtoneGenerators = new SparseArray<>(5);
    private SparseArray<PjStreamDialtoneGenerator> waittoneGenerators = new SparseArray<>(5);
    private String mNatDetected = "";

    // -------
    // Locks
    // -------

    public PjSipService() {

    }

    public void setService(SipService aService) {
        service = aService;
        prefsWrapper = service.getPrefs();
    }

    public boolean isCreated() {
        return created;
    }

    public boolean tryToLoadStack() {
        if (hasSipStack) {
            return true;
        }

        // File stackFile = NativeLibManager.getStackLibFile(service);
        if (!sipStackIsCorrupted) {
            try {
                // Try to load the stack
                // System.load(NativeLibManager.getBundledStackLibFile(service,
                // "libcrypto.so").getAbsolutePath());
                // System.load(NativeLibManager.getBundledStackLibFile(service,
                // "libssl.so").getAbsolutePath());
                // System.loadLibrary("crypto");
                // System.loadLibrary("ssl");
                System.loadLibrary(NativeLibManager.STD_LIB_NAME);
                System.loadLibrary(NativeLibManager.STACK_NAME);
                hasSipStack = true;
                return true;
            } catch (UnsatisfiedLinkError e) {
                // If it fails we probably are running on a special hardware
                LogUtil.getUtils(THIS_FILE).e("We have a problem with the current stack.... NOT YET Implemented", e);
                hasSipStack = false;
                sipStackIsCorrupted = true;

                service.notifyUserOfMessage("Can't load native library. CPU arch invalid for this build");
                return false;
            } catch (Exception e) {
                LogUtil.getUtils(THIS_FILE).e(" We have a problem with the current stack....", e);
            }
        }
        return false;
    }

    // Start the sip stack according to current settings
    /**
     * Start the sip stack Thread safing of this method must be ensured by upper
     * layer Every calls from pjsip that require start/stop/getInfos from the
     * underlying stack must be done on the same thread
     */
    public boolean sipStart() throws SameThreadException {

        if (!hasSipStack) {
            LogUtil.getUtils(THIS_FILE).e("We have no sip stack, we can't start");
            return false;
        }

        // Ensure the stack is not already created or is being created
        if (!created) {
            LogUtil.getUtils(THIS_FILE).d("Starting sip stack");

            // Pj timer
            TimerWrapper.create(service);

            int status;
            status = pjsua.create();

            LogUtil.getUtils(THIS_FILE).i("Created " + status);
            // General config
            {
                pj_str_t[] stunServers = null;
                int stunServersCount = 0;
                pjsua_config cfg = new pjsua_config();
                pjsua_logging_config logCfg = new pjsua_logging_config();
                pjsua_media_config mediaCfg = new pjsua_media_config();
                csipsimple_config cssCfg = new csipsimple_config();

                // SERVICE CONFIG

                if (userAgentReceiver == null) {
                    LogUtil.getUtils(THIS_FILE).d("create ua receiver");
                    userAgentReceiver = new UAStateReceiver();
                    userAgentReceiver.initService(this);
                }
                userAgentReceiver.reconfigure(service);
                if (zrtpReceiver == null) {
                    LogUtil.getUtils(THIS_FILE).d("create zrtp receiver");
                    zrtpReceiver = new ZrtpStateReceiver(this);
                }
                if (mediaManager == null) {
                    mediaManager = new MediaManager(service);
                }
                mediaManager.startService();

                initModules();

                DTMF_TONE_PAUSE_LENGTH = prefsWrapper
                        .getPreferenceIntegerValue(SipConfigManager.DTMF_PAUSE_TIME);
                DTMF_TONE_WAIT_LENGTH = prefsWrapper
                        .getPreferenceIntegerValue(SipConfigManager.DTMF_WAIT_TIME);

                pjsua.setCallbackObject(userAgentReceiver);
                pjsua.setZrtpCallbackObject(zrtpReceiver);

                LogUtil.getUtils(THIS_FILE).d("Attach is done to callback");

                // CSS CONFIG
                pjsua.csipsimple_config_default(cssCfg);
                cssCfg.setUse_compact_form_headers(prefsWrapper
                        .getPreferenceBooleanValue(SipConfigManager.USE_COMPACT_FORM) ? pjsua.PJ_TRUE
                        : pjsua.PJ_FALSE);
                cssCfg.setUse_compact_form_sdp(prefsWrapper
                        .getPreferenceBooleanValue(SipConfigManager.USE_COMPACT_FORM) ? pjsua.PJ_TRUE
                        : pjsua.PJ_FALSE);
                cssCfg.setUse_no_update(prefsWrapper
                        .getPreferenceBooleanValue(SipConfigManager.FORCE_NO_UPDATE) ? pjsua.PJ_TRUE
                        : pjsua.PJ_FALSE);
                cssCfg.setUse_noise_suppressor(prefsWrapper
                        .getPreferenceBooleanValue(SipConfigManager.ENABLE_NOISE_SUPPRESSION) ? pjsua.PJ_TRUE
                        : pjsua.PJ_FALSE);

                cssCfg.setTcp_keep_alive_interval(prefsWrapper.getTcpKeepAliveInterval());
                cssCfg.setTls_keep_alive_interval(prefsWrapper.getTlsKeepAliveInterval());

                cssCfg.setDisable_tcp_switch(prefsWrapper
                        .getPreferenceBooleanValue(SipConfigManager.DISABLE_TCP_SWITCH) ? pjsuaConstants.PJ_TRUE
                        : pjsuaConstants.PJ_FALSE);
                cssCfg.setDisable_rport(prefsWrapper
                        .getPreferenceBooleanValue(SipConfigManager.DISABLE_RPORT) ? pjsuaConstants.PJ_TRUE
                        : pjsuaConstants.PJ_FALSE);
                cssCfg.setAdd_bandwidth_tias_in_sdp(prefsWrapper
                        .getPreferenceBooleanValue(SipConfigManager.ADD_BANDWIDTH_TIAS_IN_SDP) ? pjsuaConstants.PJ_TRUE
                        : pjsuaConstants.PJ_FALSE);

                // Transaction timeouts
                int tsx_to = prefsWrapper
                        .getPreferenceIntegerValue(SipConfigManager.TSX_T1_TIMEOUT);
                if (tsx_to > 0) {
                    cssCfg.setTsx_t1_timeout(tsx_to);
                }
                tsx_to = prefsWrapper.getPreferenceIntegerValue(SipConfigManager.TSX_T2_TIMEOUT);
                if (tsx_to > 0) {
                    cssCfg.setTsx_t2_timeout(tsx_to);
                }
                tsx_to = prefsWrapper.getPreferenceIntegerValue(SipConfigManager.TSX_T4_TIMEOUT);
                if (tsx_to > 0) {
                    cssCfg.setTsx_t4_timeout(tsx_to);
                }
                tsx_to = prefsWrapper.getPreferenceIntegerValue(SipConfigManager.TSX_TD_TIMEOUT);
                if (tsx_to > 0) {
                    cssCfg.setTsx_td_timeout(tsx_to);
                }

                // -- USE_ZRTP 1 is no_zrtp, 2 is create_zrtp
                File zrtpFolder = PreferencesWrapper.getZrtpFolder(service);
                if (zrtpFolder != null) {
                    cssCfg.setUse_zrtp((prefsWrapper
                            .getPreferenceIntegerValue(SipConfigManager.USE_ZRTP) > 1) ? pjsua.PJ_TRUE
                            : pjsua.PJ_FALSE);
                    cssCfg.setStorage_folder(pjsua.pj_str_copy(zrtpFolder.getAbsolutePath()));
                } else {
                    cssCfg.setUse_zrtp(pjsua.PJ_FALSE);
                    cssCfg.setStorage_folder(pjsua.pj_str_copy(""));
                }

                Map<String, DynCodecInfos> availableCodecs = ExtraPlugins.getDynCodecPlugins(
                        service, SipManager.ACTION_GET_EXTRA_CODECS);
                dynamic_factory[] cssCodecs = cssCfg.getExtra_aud_codecs();
                int i = 0;
                for (Entry<String, DynCodecInfos> availableCodec : availableCodecs.entrySet()) {
                    DynCodecInfos dyn = availableCodec.getValue();
                    if (!TextUtils.isEmpty(dyn.libraryPath)) {
                        cssCodecs[i].setShared_lib_path(pjsua.pj_str_copy(dyn.libraryPath));
                        cssCodecs[i++].setInit_factory_name(pjsua
                                .pj_str_copy(dyn.factoryInitFunction));
                    }
                }
                cssCfg.setExtra_aud_codecs_cnt(i);

                // Audio implementation
                int implementation = prefsWrapper
                        .getPreferenceIntegerValue(SipConfigManager.AUDIO_IMPLEMENTATION);
                if (implementation == SipConfigManager.AUDIO_IMPLEMENTATION_OPENSLES) {
                    dynamic_factory audImp = cssCfg.getAudio_implementation();
                    audImp.setInit_factory_name(pjsua.pj_str_copy("pjmedia_opensl_factory"));
                    File openslLib = NativeLibManager.getBundledStackLibFile(service,
                            "libpj_opensl_dev.so");
                    audImp.setShared_lib_path(pjsua.pj_str_copy(openslLib.getAbsolutePath()));
                    cssCfg.setAudio_implementation(audImp);
                    LogUtil.getUtils(THIS_FILE).d("Use OpenSL-ES implementation");
                }

                // Video implementation
                if (prefsWrapper.getPreferenceBooleanValue(SipConfigManager.USE_VIDEO)) {
                    // TODO :: Have plugins per capture / render / video codec /
                    // converter
                    Map<String, DynCodecInfos> videoPlugins = ExtraPlugins.getDynCodecPlugins(
                            service, SipManager.ACTION_GET_VIDEO_PLUGIN);

                    if (videoPlugins.size() > 0) {
                        DynCodecInfos videoPlugin = videoPlugins.values().iterator().next();
                        pj_str_t pjVideoFile = pjsua.pj_str_copy(videoPlugin.libraryPath);
                        LogUtil.getUtils(THIS_FILE).d("Load video plugin at " + videoPlugin.libraryPath);
                        // Render
                        {
                            dynamic_factory vidImpl = cssCfg.getVideo_render_implementation();
                            vidImpl.setInit_factory_name(pjsua
                                    .pj_str_copy("pjmedia_webrtc_vid_render_factory"));
                            vidImpl.setShared_lib_path(pjVideoFile);
                        }
                        // Capture
                        {
                            dynamic_factory vidImpl = cssCfg.getVideo_capture_implementation();
                            vidImpl.setInit_factory_name(pjsua
                                    .pj_str_copy("pjmedia_webrtc_vid_capture_factory"));
                            vidImpl.setShared_lib_path(pjVideoFile);
                            /*
                             * -- For testing video screen -- Not yet released
                             * try { ComponentName cmp = new
                             * ComponentName("com.csipsimple.plugins.video",
                             * "com.csipsimple.plugins.video.CaptureReceiver");
                             * DynCodecInfos screenCapt = new
                             * ExtraPlugins.DynCodecInfos(service, cmp);
                             * vidImpl.setInit_factory_name(pjsua
                             * .pj_str_copy(screenCapt.factoryInitFunction));
                             * vidImpl.setShared_lib_path(pjsua
                             * .pj_str_copy(screenCapt.libraryPath)); } catch
                             * (NameNotFoundException e) { Log.e(THIS_FILE,
                             * "Not found capture plugin"); }
                             */
                        }
                        // Video codecs
                        availableCodecs = ExtraPlugins.getDynCodecPlugins(service,
                                SipManager.ACTION_GET_EXTRA_VIDEO_CODECS);
                        cssCodecs = cssCfg.getExtra_vid_codecs();
                        dynamic_factory[] cssCodecsDestroy = cssCfg.getExtra_vid_codecs_destroy();
                        i = 0;
                        for (Entry<String, DynCodecInfos> availableCodec : availableCodecs
                                .entrySet()) {
                            DynCodecInfos dyn = availableCodec.getValue();
                            if (!TextUtils.isEmpty(dyn.libraryPath)) {
                                // Create
                                cssCodecs[i].setShared_lib_path(pjsua.pj_str_copy(dyn.libraryPath));
                                cssCodecs[i].setInit_factory_name(pjsua
                                        .pj_str_copy(dyn.factoryInitFunction));
                                // Destroy
                                cssCodecsDestroy[i].setShared_lib_path(pjsua
                                        .pj_str_copy(dyn.libraryPath));
                                cssCodecsDestroy[i].setInit_factory_name(pjsua
                                        .pj_str_copy(dyn.factoryDeinitFunction));
                            }
                            i++;
                        }
                        cssCfg.setExtra_vid_codecs_cnt(i);

                        // Converter
                        dynamic_factory convertImpl = cssCfg.getVid_converter();
                        convertImpl.setShared_lib_path(pjVideoFile);
                        convertImpl.setInit_factory_name(pjsua
                                .pj_str_copy("pjmedia_libswscale_converter_init"));
                    }
                }

                // MAIN CONFIG
                pjsua.config_default(cfg);
                cfg.setCb(pjsuaConstants.WRAPPER_CALLBACK_STRUCT);
                cfg.setUser_agent(pjsua.pj_str_copy(prefsWrapper.getUserAgent(service)));
                // We need at least one thread
                int threadCount = prefsWrapper
                        .getPreferenceIntegerValue(SipConfigManager.THREAD_COUNT);
                if (threadCount <= 0) {
                    threadCount = 1;
                }
                cfg.setThread_cnt(threadCount);
                cfg.setUse_srtp(getUseSrtp());
                cfg.setSrtp_secure_signaling(0);
                cfg.setNat_type_in_sdp(0);

                pjsip_timer_setting timerSetting = cfg.getTimer_setting();
                int minSe = prefsWrapper.getPreferenceIntegerValue(SipConfigManager.TIMER_MIN_SE);
                int sessExp = prefsWrapper
                        .getPreferenceIntegerValue(SipConfigManager.TIMER_SESS_EXPIRES);
                if (minSe <= sessExp && minSe >= 90) {
                    timerSetting.setMin_se(minSe);
                    timerSetting.setSess_expires(sessExp);
                    cfg.setTimer_setting(timerSetting);
                }
                // DNS
                if (prefsWrapper.enableDNSSRV() && !prefsWrapper.useIPv6()) {
                    pj_str_t[] nameservers = getNameservers();
                    if (nameservers != null) {
                        cfg.setNameserver_count(nameservers.length);
                        cfg.setNameserver(nameservers);
                    } else {
                        cfg.setNameserver_count(0);
                    }
                }
                // STUN
                boolean isStunEnabled = prefsWrapper.getPreferenceBooleanValue(SipConfigManager.ENABLE_STUN);
                if (isStunEnabled) {
                    String[] servers = prefsWrapper.getPreferenceStringValue(
                            SipConfigManager.STUN_SERVER).split(",");
                    cfg.setStun_srv_cnt(servers.length);
                    stunServers = cfg.getStun_srv();
                    for (String server : servers) {
                        LogUtil.getUtils(THIS_FILE).d("add server " + server.trim());
                        stunServers[stunServersCount] = pjsua.pj_str_copy(server.trim());
                        stunServersCount++;
                    }
                    cfg.setStun_srv(stunServers);
                    cfg.setStun_map_use_stun2(boolToPjsuaConstant(prefsWrapper
                            .getPreferenceBooleanValue(SipConfigManager.ENABLE_STUN2)));
                }

                // LOGGING CONFIG
                pjsua.logging_config_default(logCfg);
                logCfg.setConsole_level(prefsWrapper.getLogLevel());
                logCfg.setLevel(prefsWrapper.getLogLevel());
                logCfg.setMsg_logging(pjsuaConstants.PJ_TRUE);

                if (prefsWrapper.getPreferenceBooleanValue(SipConfigManager.LOG_USE_DIRECT_FILE,
                        false)) {
                    File outFile = PreferencesWrapper.getLogsFile(service, true);
                    if (outFile != null) {
                        logCfg.setLog_filename(pjsua.pj_str_copy(outFile.getAbsolutePath()));
                        logCfg.setLog_file_flags(0x1108 /* PJ_O_APPEND */);
                    }
                }

                // MEDIA CONFIG
                pjsua.media_config_default(mediaCfg);

                // For now only this cfg is supported
                mediaCfg.setChannel_count(1);
                mediaCfg.setSnd_auto_close_time(prefsWrapper.getAutoCloseTime());
                // Echo cancellation
                mediaCfg.setEc_tail_len(prefsWrapper.getEchoCancellationTail());
                int echoMode = prefsWrapper.getPreferenceIntegerValue(SipConfigManager.ECHO_MODE);
                long clockRate = prefsWrapper.getClockRate();
                if (clockRate > 16000 && echoMode == SipConfigManager.ECHO_MODE_WEBRTC_M) {
                    // WebRTC mobile does not allow higher that 16kHz for now
                    // TODO : warn user about this point
                    echoMode = SipConfigManager.ECHO_MODE_SIMPLE;
                }
                mediaCfg.setEc_options(echoMode);
                mediaCfg.setNo_vad(boolToPjsuaConstant(!prefsWrapper
                        .getPreferenceBooleanValue(SipConfigManager.ENABLE_VAD)));
                mediaCfg.setQuality(prefsWrapper.getMediaQuality());
                mediaCfg.setClock_rate(clockRate);
                mediaCfg.setAudio_frame_ptime(prefsWrapper
                        .getPreferenceIntegerValue(SipConfigManager.SND_PTIME));

                // Disabled ? because only one thread enabled now for battery
                // perfs on normal state
                int mediaThreadCount = prefsWrapper
                        .getPreferenceIntegerValue(SipConfigManager.MEDIA_THREAD_COUNT);
                mediaCfg.setThread_cnt(mediaThreadCount);
                boolean hasOwnIoQueue = prefsWrapper
                        .getPreferenceBooleanValue(SipConfigManager.HAS_IO_QUEUE);
                if (threadCount <= 0) {
                    // Global thread count is 0, so don't use sip one anyway
                    hasOwnIoQueue = false;
                }
                mediaCfg.setHas_ioqueue(boolToPjsuaConstant(hasOwnIoQueue));

                // ICE
                boolean iceEnabled = prefsWrapper.getPreferenceBooleanValue(SipConfigManager.ENABLE_ICE);
                mediaCfg.setEnable_ice(boolToPjsuaConstant(iceEnabled));
                if(iceEnabled) {
                    pj_ice_sess_options iceOpts = mediaCfg.getIce_opt();
                    boolean aggressiveIce = prefsWrapper.getPreferenceBooleanValue(SipConfigManager.ICE_AGGRESSIVE);
                    iceOpts.setAggressive(boolToPjsuaConstant(aggressiveIce));
                }

                // TURN
                boolean isTurnEnabled = prefsWrapper.getPreferenceBooleanValue(SipConfigManager.ENABLE_TURN);
                if (isTurnEnabled) {
                    SWIGTYPE_p_pj_stun_auth_cred creds = mediaCfg.getTurn_auth_cred();
                    mediaCfg.setEnable_turn(boolToPjsuaConstant(isTurnEnabled));
                    mediaCfg.setTurn_server(pjsua.pj_str_copy(prefsWrapper.getTurnServer()));
                    pjsua.set_turn_credentials(
                            pjsua.pj_str_copy(prefsWrapper
                                    .getPreferenceStringValue(SipConfigManager.TURN_USERNAME)),
                            pjsua.pj_str_copy(prefsWrapper
                                    .getPreferenceStringValue(SipConfigManager.TURN_PASSWORD)),
                            pjsua.pj_str_copy("*"), creds);
                    // Normally this step is useless as manipulating a pointer in C memory at this point, but in case this changes reassign
                    mediaCfg.setTurn_auth_cred(creds);
                    int turnTransport = prefsWrapper.getPreferenceIntegerValue(SipConfigManager.TURN_TRANSPORT);
                    if(turnTransport != 0) {
                        switch (turnTransport) {
                            case 1:
                                mediaCfg.setTurn_conn_type(pj_turn_tp_type.PJ_TURN_TP_UDP);
                                break;
                            case 2:
                                mediaCfg.setTurn_conn_type(pj_turn_tp_type.PJ_TURN_TP_TCP);
                                break;
                            case 3:
                                mediaCfg.setTurn_conn_type(pj_turn_tp_type.PJ_TURN_TP_TLS);
                                break;
                            default:
                                break;
                        }
                    }
                    //mediaCfg.setTurn_conn_type(value);
                } else {
                    mediaCfg.setEnable_turn(pjsua.PJ_FALSE);
                }

                // INITIALIZE
                status = pjsua.csipsimple_init(cfg, logCfg, mediaCfg, cssCfg, service);
                if (status != pjsuaConstants.PJ_SUCCESS) {
                    String msg = "Fail to init pjsua "
                            + pjStrToString(pjsua.get_error_message(status));
                    LogUtil.getUtils(THIS_FILE).e(msg);
                    service.notifyUserOfMessage(msg);
                    cleanPjsua();
                    return false;
                }
            }

            // Add transports
            {
                // TODO : allow to configure local accounts.

                // We need a local account for each transport
                // to not have the
                // application lost when direct call to the IP

                // UDP
//                if (prefsWrapper.isUDPEnabled()) {
//                    int udpPort = prefsWrapper.getUDPTransportPort();
//                    localUdpAccPjId = createLocalTransportAndAccount(
//                            pjsip_transport_type_e.PJSIP_TRANSPORT_UDP,
//                            udpPort);
//                    if (localUdpAccPjId == null) {
//                        cleanPjsua();
//                        return false;
//                    }
//                    // UDP v6
//                    if (prefsWrapper.useIPv6()) {
//                        localUdp6AccPjId = createLocalTransportAndAccount(
//                                pjsip_transport_type_e.PJSIP_TRANSPORT_UDP6,
//                                udpPort == 0 ? udpPort : udpPort + 10);
//                    }
//                }

                // TCP
                if (prefsWrapper.isTCPEnabled()) {
                    int tcpPort = prefsWrapper.getTCPTransportPort();
                    localTcpAccPjId = createLocalTransportAndAccount(
                            pjsip_transport_type_e.PJSIP_TRANSPORT_TCP,
                            tcpPort);
                    if (localTcpAccPjId == null) {
                        cleanPjsua();
                        return false;
                    }
                }

                    // TCP v6
//                    if (prefsWrapper.useIPv6()) {
//                        localTcp6AccPjId = createLocalTransportAndAccount(
//                                pjsip_transport_type_e.PJSIP_TRANSPORT_TCP6,
//                                tcpPort == 0 ? tcpPort : tcpPort + 10);
//                    }
                }

                // TLS
                if (prefsWrapper.isTLSEnabled()) {
                    int tlsPort = prefsWrapper.getTLSTransportPort();
                    localTlsAccPjId = createLocalTransportAndAccount(
                            pjsip_transport_type_e.PJSIP_TRANSPORT_TLS,
                            tlsPort);
                    if (localTlsAccPjId == null) {
                        cleanPjsua();
                        return false;
                    }

                    // TLS v6
                    if (prefsWrapper.useIPv6()) {
                        localTls6AccPjId = createLocalTransportAndAccount(
                                pjsip_transport_type_e.PJSIP_TRANSPORT_TLS6,
                                tlsPort == 0 ? tlsPort : tlsPort + 10);
                    }
                }


            // Add pjsip modules
            for (PjsipModule mod : pjsipModules.values()) {
                mod.onBeforeStartPjsip();
            }

            // Initialization is done, now start pjsua
            status = pjsua.start();

            if (status != pjsua.PJ_SUCCESS) {
                String msg = "Fail to start pjsip  "
                        + pjStrToString(pjsua.get_error_message(status));
                LogUtil.getUtils(THIS_FILE).e(msg);
                service.notifyUserOfMessage(msg);
                cleanPjsua();
                return false;
            }

            // Init media codecs
            initCodecs();
            setCodecsPriorities();

            created = true;

            return true;
        }

        return false;
    }

    /**
     * Stop sip service
     * 
     * @return true if stop has been performed
     */
    public boolean sipStop() throws SameThreadException {
        LogUtil.getUtils(THIS_FILE).d(">> SIP STOP <<");

        // 是否有活动的推送session xjq

        Log.d("voip_disconnect", "-sipStop- hasActiveCall:" + VOIPManager.getInstance().hasActiveCall());

        if(VOIPManager.getInstance().hasActiveCall()) {
            return false;
        }

        Log.d("voip_disconnect", "-sipStop- getHangingCallInProgress:" + (getHangingCallInProgress() != null));

        // BEGIN:若有挂断中的会话，则不能停止协议栈。Add by xjq, 2015/4/2
        if(getHangingCallInProgress() != null) {
            LogUtil.getUtils(THIS_FILE).e("We have a call in hanging... DO NOT STOP !!!");
            return false;
        }
        // END:若有挂断中的会话，则不能停止协议栈。Add by xjq, 2015/4/2

        Log.d("voip_disconnect", "-sipStop- getActiveCallInProgress:" + (getActiveCallInProgress() != null));

        if (getActiveCallInProgress() != null) {
            LogUtil.getUtils(THIS_FILE).e("We have a call in progress... DO NOT STOP !!!");
            // TODO : queue quit on end call;

            Log.d("voip_disconnect", "-sipStop- callHangup(getActiveCallInProgress().getCallId(), 0)");
            return false;
        }

        if (service.notificationManager != null) {

            Log.d("voip_disconnect", "-sipStop- service.notificationManager cancelRegisters");

            service.notificationManager.cancelRegisters();
        }

        Log.d("voip_disconnect", "-sipStop- pjsipservice is created:"+ created);

        if (created) {

            cleanPjsua();
        }
        if (tasksTimer != null) {

            Log.d("voip_disconnect", "-sipStop- tasksTimer cancel");

            tasksTimer.cancel();
            tasksTimer.purge();
            tasksTimer = null;
        }

        // 20160903-mengbo-start 释放module持有的service
        releaseModules();
        // 20160903-mengbo-end

        return true;
    }

    private void cleanPjsua() throws SameThreadException {

        Log.d("voip_disconnect", "PjsipService start to cleanPjsua");

        LogUtil.getUtils(THIS_FILE).d("Detroying...");
        // This will destroy all accounts so synchronize with accounts
        // management lock
        // long flags = 1; /*< Lazy disconnect : only RX */
        // Try with TX & RX if network is considered as available
        long flags = 0;
        if (!prefsWrapper.isValidConnectionForOutgoing(false)) {
            // If we are current not valid for outgoing,
            // it means that we don't want the network for SIP now
            // so don't use RX | TX to not consume data at all
            flags = 3;
        }
        pjsua.csipsimple_destroy(flags);
        //20170224-mengbo : 动态获取URI
        //service.getContentResolver().delete(SipProfile.ACCOUNT_STATUS_URI, null, null);
        service.getContentResolver().delete(SipProfile.getAccountStatusUri(service), null, null);
        if (userAgentReceiver != null) {
            userAgentReceiver.stopService();
            userAgentReceiver = null;
        }

        if (mediaManager != null) {
            mediaManager.stopService();
            mediaManager = null;
        }

        TimerWrapper.destroy();

        created = false;
    }

    /**
     * Utility to create a transport
     * 
     * @return transport id or -1 if failed
     */
    private Integer createTransport(pjsip_transport_type_e type, int port)
            throws SameThreadException {
        pjsua_transport_config cfg = new pjsua_transport_config();
        int[] tId = new int[1];
        int status;
        pjsua.transport_config_default(cfg);
        cfg.setPort(port);

        if (type.equals(pjsip_transport_type_e.PJSIP_TRANSPORT_TLS)) {
            pjsip_tls_setting tlsSetting = cfg.getTls_setting();

            /*
             * TODO : THIS IS OBSOLETE -- remove from UI String serverName =
             * prefsWrapper
             * .getPreferenceStringValue(SipConfigManager.TLS_SERVER_NAME); if
             * (!TextUtils.isEmpty(serverName)) {
             * tlsSetting.setServer_name(pjsua.pj_str_copy(serverName)); }
             */

            String caListFile = prefsWrapper
                    .getPreferenceStringValue(SipConfigManager.CA_LIST_FILE);
            if (!TextUtils.isEmpty(caListFile)) {
                tlsSetting.setCa_list_file(pjsua.pj_str_copy(caListFile));
            }

            String certFile = prefsWrapper.getPreferenceStringValue(SipConfigManager.CERT_FILE);
            if (!TextUtils.isEmpty(certFile)) {
                tlsSetting.setCert_file(pjsua.pj_str_copy(certFile));
            }

            String privKey = prefsWrapper.getPreferenceStringValue(SipConfigManager.PRIVKEY_FILE);

            if (!TextUtils.isEmpty(privKey)) {
                tlsSetting.setPrivkey_file(pjsua.pj_str_copy(privKey));
            }

            String tlsPwd = prefsWrapper.getPreferenceStringValue(SipConfigManager.TLS_PASSWORD);
            if (!TextUtils.isEmpty(tlsPwd)) {
                tlsSetting.setPassword(pjsua.pj_str_copy(tlsPwd));
            }

            boolean checkClient = prefsWrapper
                    .getPreferenceBooleanValue(SipConfigManager.TLS_VERIFY_CLIENT);
            tlsSetting.setVerify_client(checkClient ? 1 : 0);

            tlsSetting.setMethod(pjsip_ssl_method.swigToEnum(prefsWrapper.getTLSMethod()));
            tlsSetting.setProto(0);
            boolean checkServer = prefsWrapper
                    .getPreferenceBooleanValue(SipConfigManager.TLS_VERIFY_SERVER);
            tlsSetting.setVerify_server(checkServer ? 1 : 0);

            cfg.setTls_setting(tlsSetting);
        }

        if (prefsWrapper.getPreferenceBooleanValue(SipConfigManager.ENABLE_QOS)) {
            LogUtil.getUtils(THIS_FILE).d("Activate qos for this transport");
            pj_qos_params qosParam = cfg.getQos_params();
            qosParam.setDscp_val((short) prefsWrapper
                    .getPreferenceIntegerValue(SipConfigManager.DSCP_VAL));
            qosParam.setFlags((short) 1); // DSCP
            cfg.setQos_params(qosParam);
        }

        status = pjsua.transport_create(type, cfg, tId);
        if (status != pjsuaConstants.PJ_SUCCESS) {
            String errorMsg = pjStrToString(pjsua.get_error_message(status));
            String msg = "Fail to create transport " + errorMsg + " (" + status + ")";
            if (status == 120098) { /* Already binded */
                msg = service.getString(R.string.another_application_use_sip_port);
            }
            service.notifyUserOfMessage(msg);
            return null;
        }
        return tId[0];
    }

    private Integer createLocalAccount(Integer transportId)
            throws SameThreadException {
        if (transportId == null) {
            return null;
        }
        int[] p_acc_id = new int[1];
        pjsua.acc_add_local(transportId, pjsua.PJ_FALSE, p_acc_id);
        return p_acc_id[0];
    }

    private Integer createLocalTransportAndAccount(pjsip_transport_type_e type, int port)
            throws SameThreadException {
        Integer transportId = createTransport(type, port);
        return createLocalAccount(transportId);
    }


    // add by xjq
    public boolean addCustAccount(String addr) throws SameThreadException {
        int status = pjsuaConstants.PJ_FALSE;
        if (!created) {
            LogUtil.getUtils(THIS_FILE).e(" PJSIP is not started here, nothing can be done");
            return status == pjsuaConstants.PJ_SUCCESS;

        }

        delCustAccount();
        SipProfile profile = PhoneManager.getInstance().buildCustAccount(service, addr);
        PjSipAccount account = new PjSipAccount(profile);
        account.applyExtraParams(service);

        account.cfg.setRegister_on_acc_add(pjsuaConstants.PJ_FALSE);
        int[] accId = new int[1];

        status = pjsua.acc_add(account.cfg, pjsuaConstants.PJ_TRUE, accId);// 设置为默认帐号 xjq 2015-12-12

        pjsua.csipsimple_set_acc_user_data(accId[0], account.css_cfg);
//        beforeAccountRegistration(accId[0], profile);
//        pjsua.acc_set_registration(accId[0], 1);


        if (status == pjsuaConstants.PJ_SUCCESS) {
            SipProfileState ps = new SipProfileState(profile);
            ps.setAddedStatus(status);
            ps.setPjsuaId(accId[0]);
            //20170224-mengbo : 动态获取URI
            //service.getContentResolver().insert(
            //        ContentUris.withAppendedId(SipProfile.ACCOUNT_STATUS_ID_URI_BASE,
            //                account.id), ps.getAsContentValue());
            service.getContentResolver().insert(
                    ContentUris.withAppendedId(SipProfile.getBaseAccountStatusIdUri(service),
                            account.id), ps.getAsContentValue());


//                pjsua.acc_set_online_status(accId[0], 1);
        }


        return status == pjsuaConstants.PJ_SUCCESS;
    }



    public boolean setCustAccountOnline( )
            throws SameThreadException {
        int status = -1;
        if (!created) {
            LogUtil.getUtils(THIS_FILE ).e(" PJSIP is not started here, nothing can be done");
            return false;
        }

        int defaultId = pjsua.acc_get_default();
        if(defaultId == SipProfile.INVALID_ID) {
            return false;
        }


        status = pjsua.acc_set_registration(defaultId, 1);
        return status == 0;

    }

    public boolean setCustAccountOffline()
            throws SameThreadException {
        int status = -1;
        if (!created) {
            LogUtil.getUtils(THIS_FILE).e(" PJSIP is not started here, nothing can be done");
            return false;
        }

        int defaultId = pjsua.acc_get_default();
        if(defaultId == SipProfile.INVALID_ID) {
            return false;
        }

        status = pjsua.acc_set_registration(defaultId, 0);
        return status == 0;

    }


    public boolean delCustAccount()
            throws SameThreadException {
        int status = -1;
        if (!created) {
            LogUtil.getUtils(THIS_FILE).e(" PJSIP is not started here, nothing can be done");
            return false;
        }

        int defaultId = pjsua.acc_get_default();
        if(defaultId == SipProfile.INVALID_ID) {
            return false;
        }

        status = pjsua.acc_del(defaultId);
        return status == 0;

    }



    public boolean addAccount(SipProfile profile) throws SameThreadException {
        int status = pjsuaConstants.PJ_FALSE;
        if (!created) {
            LogUtil.getUtils(THIS_FILE).e(" PJSIP is not started here, nothing can be done");
            return status == pjsuaConstants.PJ_SUCCESS;

        }
        PjSipAccount account = new PjSipAccount(profile);
        account.applyExtraParams(service);

        // Force the use of a transport
        /*
         * switch (account.transport) { case SipProfile.TRANSPORT_UDP: if
         * (udpTranportId != null) {
         * //account.cfg.setTransport_id(udpTranportId); } break; case
         * SipProfile.TRANSPORT_TCP: if (tcpTranportId != null) { //
         * account.cfg.setTransport_id(tcpTranportId); } break; case
         * SipProfile.TRANSPORT_TLS: if (tlsTransportId != null) { //
         * account.cfg.setTransport_id(tlsTransportId); } break; default: break;
         * }
         */

        SipProfileState currentAccountStatus = getProfileState(profile);
        account.cfg.setRegister_on_acc_add(pjsuaConstants.PJ_FALSE);

        if (currentAccountStatus.isAddedToStack()) {
            pjsua.csipsimple_set_acc_user_data(currentAccountStatus.getPjsuaId(), account.css_cfg);
            status = pjsua.acc_modify(currentAccountStatus.getPjsuaId(), account.cfg);
            beforeAccountRegistration(currentAccountStatus.getPjsuaId(), profile);
            ContentValues cv = new ContentValues();
            cv.put(SipProfileState.ADDED_STATUS, status);
            //20170224-mengbo : 动态获取URI
            //service.getContentResolver().update(
            //        ContentUris.withAppendedId(SipProfile.ACCOUNT_STATUS_ID_URI_BASE, profile.id),
            //        cv, null, null);
            service.getContentResolver().update(
                    ContentUris.withAppendedId(SipProfile.getBaseAccountStatusIdUri(service), profile.id),
                    cv, null, null);

            if (!account.wizard.equalsIgnoreCase("LOCAL")) {
                // Re register
                if (status == pjsuaConstants.PJ_SUCCESS) {
                    status = pjsua.acc_set_registration(currentAccountStatus.getPjsuaId(), 1);
                    if (status == pjsuaConstants.PJ_SUCCESS) {
//                        pjsua.acc_set_online_status(currentAccountStatus.getPjsuaId(), 1);
                    }
                }
            }
        } else {
            int[] accId = new int[1];
            if (account.wizard.equalsIgnoreCase("LOCAL")) {
                // We already have local account by default
                // For now consider we are talking about UDP one
                // In the future local account should be set per transport
                switch (account.transport) {
                    case SipProfile.TRANSPORT_UDP:
                        accId[0] = prefsWrapper.useIPv6() ? localUdp6AccPjId : localUdpAccPjId;
                        break;
                    case SipProfile.TRANSPORT_TCP:
                        accId[0] = prefsWrapper.useIPv6() ? localTcp6AccPjId : localTcpAccPjId;
                        break;
                    case SipProfile.TRANSPORT_TLS:
                        accId[0] = prefsWrapper.useIPv6() ? localTls6AccPjId : localTlsAccPjId;
                        break;
                    default:
                        // By default use UDP
                        accId[0] = localUdpAccPjId;
                        break;
                }

                pjsua.csipsimple_set_acc_user_data(accId[0], account.css_cfg);
                // TODO : use video cfg here
//                nCfg.setVid_in_auto_show(pjsuaConstants.PJ_TRUE);
//                nCfg.setVid_out_auto_transmit(pjsuaConstants.PJ_TRUE);
//                status = pjsua.acc_modify(accId[0], nCfg);
            } else {
                // Cause of standard account different from local account :)
                status = pjsua.acc_add(account.cfg, pjsuaConstants.PJ_FALSE, accId);
                pjsua.csipsimple_set_acc_user_data(accId[0], account.css_cfg);
                beforeAccountRegistration(accId[0], profile);
                pjsua.acc_set_registration(accId[0], 1);
            }

            if (status == pjsuaConstants.PJ_SUCCESS) {
                SipProfileState ps = new SipProfileState(profile);
                ps.setAddedStatus(status);
                ps.setPjsuaId(accId[0]);
                //20170224-mengbo : 动态获取URI
                //service.getContentResolver().insert(
                //        ContentUris.withAppendedId(SipProfile.ACCOUNT_STATUS_ID_URI_BASE,
                //                account.id), ps.getAsContentValue());
                service.getContentResolver().insert(
                        ContentUris.withAppendedId(SipProfile.getBaseAccountStatusIdUri(service),
                                account.id), ps.getAsContentValue());

//                pjsua.acc_set_online_status(accId[0], 1);
            }
        }

        return status == pjsuaConstants.PJ_SUCCESS;
    }

    void beforeAccountRegistration(int pjId, SipProfile profile) {
        for (PjsipModule mod : pjsipModules.values()) {
            mod.onBeforeAccountStartRegistration(pjId, profile);
        }
    }

    /**
     * Synchronize content provider backend from pjsip stack
     * 
     * @param pjsuaId the pjsua id of the account to synchronize
     * @throws SameThreadException
     */
    public void updateProfileStateFromService(int pjsuaId) throws SameThreadException {
        if (!created) {
            return;
        }
        long accId = getAccountIdForPjsipId(service, pjsuaId);
        LogUtil.getUtils(THIS_FILE).d("Update profile from service for " + pjsuaId + " aka in db " + accId);
        if (accId != SipProfile.INVALID_ID) {
            int success = pjsuaConstants.PJ_FALSE;
            pjsua_acc_info pjAccountInfo;
            pjAccountInfo = new pjsua_acc_info();
            success = pjsua.acc_get_info(pjsuaId, pjAccountInfo);
            if (success == pjsuaConstants.PJ_SUCCESS && pjAccountInfo != null) {
                ContentValues cv = new ContentValues();

                try {
                    boolean regSuccess = pjAccountInfo.getStatus().swigValue() / 100 == 2;
                    boolean isReg = pjAccountInfo.getExpires() > 0;


                    PNMessageManager.getInstance().regStateChanged(regSuccess, pjAccountInfo.getStatus().swigValue());
                    // Should be fine : status code are coherent with RFC
                    // status codes
                    cv.put(SipProfileState.STATUS_CODE, pjAccountInfo.getStatus().swigValue());
                    
                    // 若有回话需要重连，则重连. Add by xjq, 2015/4/16
                    if(regSuccess && isReg) { // 上线成功
                        int callId;
                        SipCallSession callSession = userAgentReceiver.getCallNeedReconnect();
                        if(callSession != null ) {
                            callId = callSession.getCallId();
                            if(callId != SipCallSession.INVALID_CALL_ID) {
                                LogUtil.getUtils(THIS_FILE).d("xjq start to reconnect call ");
                                callReconnect(callId);
                            }
                        }

                    }

                    // 发送广播，告知账户注册状态
//                    Intent intent = new Intent(CallManager.ACC_REG_STATE);
//
//                    intent.putExtra("REG_STATE", pjAccountInfo.getStatus().swigValue() / 100 == 2?CallManager.REG_SUCESS
//                    :CallManager.REG_FAILED);
//                    service.sendBroadcast(intent);




                } catch (IllegalArgumentException e) {
                    cv.put(SipProfileState.STATUS_CODE,
                            SipCallSession.StatusCode.INTERNAL_SERVER_ERROR);
                }

                cv.put(SipProfileState.STATUS_TEXT, pjStrToString(pjAccountInfo.getStatus_text()));
                cv.put(SipProfileState.EXPIRES, pjAccountInfo.getExpires());

                //20170224-mengbo : 动态获取URI
                //service.getContentResolver().update(
                //        ContentUris.withAppendedId(SipProfile.ACCOUNT_STATUS_ID_URI_BASE, accId),
                //        cv, null, null);
                service.getContentResolver().update(
                        ContentUris.withAppendedId(SipProfile.getBaseAccountStatusIdUri(service), accId),
                        cv, null, null);

                LogUtil.getUtils(THIS_FILE).d("Profile state UP : " + cv);
            }
        } else {
            LogUtil.getUtils(THIS_FILE).e("Trying to update not added account " + pjsuaId);
        }
    }

    /**
     * Get the dynamic state of the profile
     * 
     * @param account the sip profile from database. Important field is id.
     * @return the dynamic sip profile state
     */
    public SipProfileState getProfileState(SipProfile account) {
        if (!created || account == null) {
            return null;
        }
        if (account.id == SipProfile.INVALID_ID) {
            return null;
        }
        SipProfileState accountInfo = new SipProfileState(account);
        //20170224-mengbo : 动态获取URI
        //Cursor c = service.getContentResolver().query(
        //        ContentUris.withAppendedId(SipProfile.ACCOUNT_STATUS_ID_URI_BASE, account.id),
        //        null, null, null, null);
        Cursor c = service.getContentResolver().query(
                ContentUris.withAppendedId(SipProfile.getBaseAccountStatusIdUri(service), account.id),
                null, null, null, null);
        if (c != null) {
            try {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    accountInfo.createFromDb(c);
                }
            } catch (Exception e) {
                LogUtil.getUtils(THIS_FILE).e("Error on looping over sip profiles states", e);
            } finally {
                c.close();
            }
        }
        return accountInfo;
    }

    private static ArrayList<String> codecs = new ArrayList<>();
    private static ArrayList<String> video_codecs = new ArrayList<>();
    private static boolean codecs_initialized = false;

    /**
     * Reset the list of codecs stored
     */
    public static void resetCodecs() {
        synchronized (codecs) {
            if (codecs_initialized) {
                codecs.clear();
                video_codecs.clear();
                codecs_initialized = false;
            }
        }
    }

    /**
     * Retrieve codecs from pjsip stack and store it inside preference storage
     * so that it can be retrieved in the interface view
     * 
     * @throws SameThreadException
     */
    private void initCodecs() throws SameThreadException {

        synchronized (codecs) {
            if (!codecs_initialized) {
                int nbrCodecs, i;

                // Audio codecs
                nbrCodecs = pjsua.codecs_get_nbr();
                for (i = 0; i < nbrCodecs; i++) {
                    String codecId = pjStrToString(pjsua.codecs_get_id(i));
                    codecs.add(codecId);
                    // Log.d(THIS_FILE, "Added codec " + codecId);
                }
                // Set it in prefs if not already set correctly
                prefsWrapper.setCodecList(codecs);

                // Video codecs
                nbrCodecs = pjsua.codecs_vid_get_nbr();
                for (i = 0; i < nbrCodecs; i++) {
                    String codecId = pjStrToString(pjsua.codecs_vid_get_id(i));
                    video_codecs.add(codecId);
                    LogUtil.getUtils(THIS_FILE).d("Added video codec " + codecId);
                }
                // Set it in prefs if not already set correctly
                prefsWrapper.setVideoCodecList(video_codecs);

                codecs_initialized = true;
                // We are now always capable of tls and srtp !
                prefsWrapper.setLibCapability(PreferencesProviderWrapper.LIB_CAP_TLS, true);
                prefsWrapper.setLibCapability(PreferencesProviderWrapper.LIB_CAP_SRTP, true);
            }
        }

    }

    /**
     * Append log for the codec in String builder
     * 
     * @param sb the buffer to be appended with the codec info
     * @param codec the codec name
     * @param prio the priority of the codec
     */
    private void buffCodecLog(StringBuilder sb, String codec, short prio) {
        if (prio > 0 ) {
            sb.append(codec);
            sb.append(" (");
            sb.append(prio);
            sb.append(") - ");
        }
    }

    /**
     * Set the codec priority in pjsip stack layer based on preference store
     * 
     * @throws SameThreadException
     */
    private void setCodecsPriorities() throws SameThreadException {
        ConnectivityManager cm = ((ConnectivityManager) service
                .getSystemService(Context.CONNECTIVITY_SERVICE));

        synchronized (codecs) {
            if (codecs_initialized) {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null) {

                    StringBuilder audioSb = new StringBuilder();
                    StringBuilder videoSb = new StringBuilder();
                    audioSb.append("Audio codecs : ");
                    videoSb.append("Video codecs : ");

                    //getType区分是wifi还是手机网络，getSubType判断具体手机网络类型（2/3/4g）
                    String currentBandType = prefsWrapper.getPreferenceStringValue(
                            SipConfigManager.getBandTypeKey(ni.getType(), ni.getSubtype()),
                            SipConfigManager.CODEC_WB);

                    synchronized (codecs) {

                        for (String codec : codecs) {
                            short aPrio = prefsWrapper.getCodecPriority(codec, currentBandType,
                                    "-1");
                            buffCodecLog(audioSb, codec, aPrio);
                            pj_str_t codecStr = pjsua.pj_str_copy(codec);
                            if (aPrio >= 0) {
                                pjsua.codec_set_priority(codecStr, aPrio);
                            }

                            String codecKey = SipConfigManager.getCodecKey(codec,
                                    SipConfigManager.FRAMES_PER_PACKET_SUFFIX);
                            Integer frmPerPacket = SipConfigManager.getPreferenceIntegerValue(
                                    service, codecKey);
                            if (frmPerPacket != null && frmPerPacket > 0) {
                                LogUtil.getUtils(THIS_FILE).v("Set codec " + codec + " fpp : " + frmPerPacket);
                                pjsua.codec_set_frames_per_packet(codecStr, frmPerPacket);
                            }
                        }

                    }

                    LogUtil.getUtils(THIS_FILE).d(audioSb.toString());
                    LogUtil.getUtils(THIS_FILE).d(videoSb.toString());
                }

            }
        }
    }



    public String getBandType(NetworkInfo ni) {
        return prefsWrapper.getPreferenceStringValue(
                SipConfigManager.getBandTypeKey(ni.getType(), ni.getSubtype()),
                SipConfigManager.CODEC_WB);
    }

    public void setCodecsPrioritiesManual(int band) throws SameThreadException {

        synchronized (codecs) {
            if (codecs_initialized) {
                StringBuilder audioSb = new StringBuilder();
                StringBuilder videoSb = new StringBuilder();
                audioSb.append("Audio codecs : ");
                videoSb.append("Video codecs : ");

                //getType区分是wifi还是手机网络，getSubType判断具体手机网络类型（2/3/4g）
                String currentBandType = band == 0 ? SipConfigManager.CODEC_NB : SipConfigManager.CODEC_WB;

                synchronized (codecs) {

                    for (String codec : codecs) {
                        short aPrio = prefsWrapper.getCodecPriority(codec, currentBandType,
                                "-1");
                        buffCodecLog(audioSb, codec, aPrio);
                        pj_str_t codecStr = pjsua.pj_str_copy(codec);
                        if (aPrio >= 0) {
                            pjsua.codec_set_priority(codecStr, aPrio);
                        }

                        String codecKey = SipConfigManager.getCodecKey(codec,
                                SipConfigManager.FRAMES_PER_PACKET_SUFFIX);
                        Integer frmPerPacket = SipConfigManager.getPreferenceIntegerValue(
                                service, codecKey);
                        if (frmPerPacket != null && frmPerPacket > 0) {
                            pjsua.codec_set_frames_per_packet(codecStr, frmPerPacket);
                        }
                    }

                }

                LogUtil.getUtils(THIS_FILE).d(audioSb.toString());
                LogUtil.getUtils(THIS_FILE).d(videoSb.toString());
            }

        }

    }
    // Call related

    /**
     * Answer a call
     * 
     * @param callId the id of the call to answer to
     * @param code the status code to send in the response
     * @return
     */
    public int callAnswer(int callId, int code) throws SameThreadException {

        if (created) {
            pjsua_call_setting cs = new pjsua_call_setting();
            pjsua.call_setting_default(cs);
            cs.setAud_cnt(1);
            cs.setVid_cnt(prefsWrapper.getPreferenceBooleanValue(SipConfigManager.USE_VIDEO) ? 1
                    : 0);
            cs.setFlag(0);
            return pjsua.call_answer2(callId, cs, code, null, null);
            // return pjsua.call_answer(callId, code, null, null);
        }
        return -1;
    }

    /**
     * Hangup a call
     * 
     * @param callId the id of the call to hangup
     * @param code the status code to send in the response
     * @return
     */
    public int callHangup(int callId, int code) throws SameThreadException {
        if (created) {
            return pjsua.call_hangup(callId, code, null, null);
        }
        return -1;
    }

    public int callXfer(int callId, String callee) throws SameThreadException {
        if (created) {
            return pjsua.call_xfer(callId, pjsua.pj_str_copy(callee), null);
        }
        return -1;
    }

    public int callXferReplace(int callId, int otherCallId, int options) throws SameThreadException {
        if (created) {
            return pjsua.call_xfer_replaces(callId, otherCallId, options, null);
        }
        return -1;
    }

    /**
     * Make a call
     * 
     * @param callee remote contact ot call If not well formated we try to add
     *            domain name of the default account
     */
    public int makeCall(String callee, int accountId, Bundle b) throws SameThreadException {
        LogUtil.getUtils(THIS_FILE).d("0               makeCall  created=" + created);
        if (!created) {
            return -1;
        }
        LogUtil.getUtils().e(THIS_FILE+ "makeCall()的callee" + callee);
        final ToCall toCall = sanitizeSipUri(callee, accountId);
        LogUtil.getUtils(THIS_FILE).d("1               makeCall=");

        String extraCallid = null;
        if (toCall != null) {
            pj_str_t uri = pjsua.pj_str_copy(toCall.getCallee());  

            // Nothing to do with this values
            byte[] userData = new byte[1];
            int[] callId = new int[1];
            pjsua_call_setting cs = new pjsua_call_setting();
            pjsua_msg_data msgData = new pjsua_msg_data();
            int pjsuaAccId = toCall.getPjsipAccountId();

            // Call settings to add video
            pjsua.call_setting_default(cs);
            cs.setAud_cnt(1);
            cs.setVid_cnt(0);
            if (b != null && b.getBoolean(SipCallSession.OPT_CALL_VIDEO, false)) {
                cs.setVid_cnt(1);
            }
            cs.setFlag(0);

            pj_pool_t pool = pjsua.pool_create("call_tmp", 512, 512);

            // Msg data to add headers
            pjsua.msg_data_init(msgData);
            pjsua.csipsimple_init_acc_msg_data(pool, pjsuaAccId, msgData);
            if (b != null) {
                Bundle extraHeaders = b.getBundle(SipCallSession.OPT_CALL_EXTRA_HEADERS);
                if (extraHeaders != null) {
                    for (String key : extraHeaders.keySet()) {
                        try {
                            String value = extraHeaders.getString(key);
                            if (!TextUtils.isEmpty(value)) {
                                // add by xjq 记录推送session的callid
                                if(key.equals(SipManager.HDR_EXTRA_CALL_ID)) {
                                    extraCallid = value;
                                }

                                int res = pjsua.csipsimple_msg_data_add_string_hdr(pool, msgData,
                                        pjsua.pj_str_copy(key), pjsua.pj_str_copy(value));
                                if (res == pjsuaConstants.PJ_SUCCESS) {
                                    LogUtil.getUtils(THIS_FILE).e("Failed to add Xtra hdr (" + key + " : "
                                            + value + ") probably not X- header");
                                }
                            }
                        } catch (Exception e) {
                            LogUtil.getUtils(THIS_FILE).e("Invalid header value for key : " + key);
                        }
                    }
                }
            }

            int status = pjsua.call_make_call(pjsuaAccId, uri, cs, userData, msgData, callId);
            LogUtil.getUtils(THIS_FILE).d("1               status=" + status);

            //正在通话中但不是本次呼叫的目标 xjq 2015-09-08
            if(status==70025){
                XToast.show(service, ActomaController.getApp().getString(R.string.DONOT_MORE_CALL));

                //正在通话中和本次呼叫的目标一致，直接拉起通话界面 xjq 2015-09-08
            }else if(status==70026){
                SipCallSession callInfo=userAgentReceiver.getActiveCallInProgress();
                Intent intent = new Intent(SipManager.ACTION_SIP_CALL_UI);
                if(callInfo != null) {
                    SipCallSession toSendInfo = new SipCallSession(callInfo);
                    intent.putExtra(SipManager.EXTRA_CALL_INFO, toSendInfo);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                LogUtil.getUtils().e(THIS_FILE + "走到了makeCall");
                service.startActivity(intent);
            } else if(status == 70013) { // 重新添加默认的账号 xjq 2015-10-12
                LogUtil.getUtils(THIS_FILE).d("协议栈当前账号不在线！！");
                XToast.show(service, ActomaController.getApp().getString(R.string.CANNOT_CONNECT_SERVER));
                final SipProfile account = service.getDefaultAccount();
                if (account != null) {
                    LogUtil.getUtils(THIS_FILE).d("xjq 70013 READD DEFAULT ACCOUNT");
                    service.setAccountRegistration(account, account.active ? 1 : 0, true);
                } else {
                    LogUtil.getUtils(THIS_FILE).e(" xjq 70013 ERROR!! Default account NULL");
                }
            }


            if (status == pjsuaConstants.PJ_SUCCESS) {
                dtmfToAutoSend.put(callId[0], toCall.getDtmf());
                LogUtil.getUtils(THIS_FILE).d("DTMF - Store for " + callId[0] + " - " + toCall.getDtmf());
            }


            // 初始化sipcallsession中的extra callid字段 xjq 2016-01-25
            if(extraCallid != null) {
                SipCallSession session = getCallInfo(callId[0]);
                if(session != null)
                    session.setExtraCallId(extraCallid);
            }

            pjsua.pj_pool_release(pool);
            return status;
        } else {
            //zjc 20150831 toCall不是null，但是依旧可以走到这里。原因不明，先注释掉。
            /*service.notifyUserOfMessage(service.getString(R.string.invalid_sip_uri) + " : "
                    + callee);
            */
        }
        return -1;
    }

    public int updateCallOptions(int callId, Bundle options) {
        // TODO : if more options we should redesign this part.
        if (options.containsKey(SipCallSession.OPT_CALL_VIDEO)) {
            boolean add = options.getBoolean(SipCallSession.OPT_CALL_VIDEO);
            SipCallSession ci = getCallInfo(callId);
            if (add && ci.mediaHasVideo()) {
                // We already have one video running -- refuse to send another
                return -1;
            } else if (!add && !ci.mediaHasVideo()) {
                // We have no current video, no way to remove.
                return -1;
            }
            pjsua_call_vid_strm_op op = add ? pjsua_call_vid_strm_op.PJSUA_CALL_VID_STRM_ADD
                    : pjsua_call_vid_strm_op.PJSUA_CALL_VID_STRM_REMOVE;
            if (!add) {
                // TODO : manage remove case
            }
            return pjsua.call_set_vid_strm(callId, op, null);
        }

        return -1;
    }

    /**
     * Send a dtmf signal to a call
     * 
     * @param callId the call to send the signal
     * @param keyCode the keyCode to send (android style)
     * @return
     */
    public int sendDtmf(int callId, int keyCode) throws SameThreadException {
        if (!created) {
            return -1;
        }
        String keyPressed = "";
        // Since some device (xoom...) are apparently buggy with key character
        // map loading...
        // we have to do crappy thing here
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            keyPressed = Integer.toString(keyCode - KeyEvent.KEYCODE_0);
        } else if (keyCode == KeyEvent.KEYCODE_POUND) {
            keyPressed = "#";
        } else if (keyCode == KeyEvent.KEYCODE_STAR) {
            keyPressed = "*";
        } else {
            // Fallback... should never be there if using visible dialpad, but
            // possible using keyboard
            KeyCharacterMap km = KeyCharacterMap.load(KeyCharacterMap.NUMERIC);
            keyPressed = Integer.toString(km.getNumber(keyCode));
        }
        return sendDtmf(callId, keyPressed);
    }

    private int sendDtmf(final int callId, String keyPressed) throws SameThreadException {
        if (TextUtils.isEmpty(keyPressed)) {
            return pjsua.PJ_SUCCESS;
        }

        if (pjsua.call_is_active(callId) != pjsuaConstants.PJ_TRUE) {
            return -1;
        }
        if(pjsua.call_has_media(callId) != pjsuaConstants.PJ_TRUE) {
            return -1;
        }

        String dtmfToDial = keyPressed;
        String remainingDtmf = "";
        int pauseBeforeRemaining = 0;
        boolean foundSeparator = false;
        if (keyPressed.contains(",") || keyPressed.contains(";")) {
            dtmfToDial = "";
            for (int i = 0; i < keyPressed.length(); i++) {
                char c = keyPressed.charAt(i);
                if (!foundSeparator) {
                    if (c == ',' || c == ';') {
                        pauseBeforeRemaining += (c == ',') ? DTMF_TONE_PAUSE_LENGTH
                                : DTMF_TONE_WAIT_LENGTH;
                        foundSeparator = true;
                    } else {
                        dtmfToDial += c;
                    }
                } else {
                    if ((c == ',' || c == ';') && TextUtils.isEmpty(remainingDtmf)) {
                        pauseBeforeRemaining += (c == ',') ? DTMF_TONE_PAUSE_LENGTH
                                : DTMF_TONE_WAIT_LENGTH;
                    } else {
                        remainingDtmf += c;
                    }
                }
            }

        }

        int res = 0;
        if (!TextUtils.isEmpty(dtmfToDial)) {
            pj_str_t pjKeyPressed = pjsua.pj_str_copy(dtmfToDial);
            res = -1;
            if (prefsWrapper.useSipInfoDtmf()) {
                res = pjsua.send_dtmf_info(callId, pjKeyPressed);
                LogUtil.getUtils(THIS_FILE).d("Has been sent DTMF INFO : " + res);
            } else {
                if (!prefsWrapper.forceDtmfInBand()) {
                    // Generate using RTP
                    res = pjsua.call_dial_dtmf(callId, pjKeyPressed);
                    LogUtil.getUtils(THIS_FILE).d("Has been sent in RTP DTMF : " + res);
                }

                if (res != pjsua.PJ_SUCCESS && !prefsWrapper.forceDtmfRTP()) {
                    // Generate using analogic inband
                    if (dtmfDialtoneGenerators.get(callId) == null) {
                        dtmfDialtoneGenerators.put(callId, new PjStreamDialtoneGenerator(callId));
                    }
                    res = dtmfDialtoneGenerators.get(callId).sendPjMediaDialTone(dtmfToDial);
                    LogUtil.getUtils(THIS_FILE).d("Has been sent DTMF analogic : " + res);
                }
            }
        }

        // Finally, push remaining DTMF in the future
        if (!TextUtils.isEmpty(remainingDtmf)) {
            dtmfToAutoSend.put(callId, remainingDtmf);

            if (tasksTimer == null) {
                tasksTimer = new Timer("com.csipsimple.PjSipServiceTasks");
            }
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    service.getExecutor().execute(new SipRunnable() {
                        @Override
                        protected void doRun() throws SameThreadException {
                            LogUtil.getUtils(THIS_FILE).d("Running pending DTMF send");
                            sendPendingDtmf(callId);
                        }
                    });
                }
            };
            dtmfTasks.put(callId, tt);
            LogUtil.getUtils(THIS_FILE).d("Schedule DTMF " + remainingDtmf + " in " + pauseBeforeRemaining);
            tasksTimer.schedule(tt, pauseBeforeRemaining);
        } else {
            if (dtmfToAutoSend.get(callId) != null) {
                dtmfToAutoSend.put(callId, null);
            }
            if (dtmfTasks.get(callId) != null) {
                dtmfTasks.put(callId, null);
            }
        }

        return res;
    }

    /**
     * Send sms/message using SIP server
     */
    public ToCall sendMessage(String callee, String message, long accountId)
            throws SameThreadException {
        if (!created) {
            return null;
        }

        ToCall toCall = sanitizeSipUri(callee, accountId);
        if (toCall != null) {

            pj_str_t uri = pjsua.pj_str_copy(toCall.getCallee());
            pj_str_t text = pjsua.pj_str_copy(message);
            /*
             * Log.d(THIS_FILE, "get for outgoing"); int finalAccountId =
             * accountId; if (accountId == -1) { finalAccountId =
             * pjsua.acc_find_for_outgoing(uri); }
             */
            // Nothing to do with this values
            byte[] userData = new byte[1];

            int status = pjsua.im_send(toCall.getPjsipAccountId(), uri, null, text, null, userData);
            return (status == pjsuaConstants.PJ_SUCCESS) ? toCall : null;
        }
        return toCall;
    }

    /**
     * Add a buddy to buddies list
     * 
     * @param buddyUri the uri to register to
     * @throws SameThreadException
     */
    public int addBuddy(String buddyUri) throws SameThreadException {
        if (!created) {
            return -1;
        }
        int[] p_buddy_id = new int[1];

        pjsua_buddy_config buddy_cfg = new pjsua_buddy_config();
        pjsua.buddy_config_default(buddy_cfg);
        buddy_cfg.setSubscribe(1);
        buddy_cfg.setUri(pjsua.pj_str_copy(buddyUri));

        pjsua.buddy_add(buddy_cfg, p_buddy_id);

        return p_buddy_id[0];
    }

    /**
     * Remove one buddy from the buddy list managed by pjsip
     * 
     * @param buddyUri he uri to unregister
     * @throws SameThreadException
     */
    public void removeBuddy(String buddyUri) throws SameThreadException {
        if (!created) {
            return;
        }
        int buddyId = pjsua.buddy_find(pjsua.pj_str_copy(buddyUri));
        if (buddyId >= 0) {
            pjsua.buddy_del(buddyId);
        }
    }

    public void sendPendingDtmf(int callId) throws SameThreadException {
        if (dtmfToAutoSend.get(callId) != null) {
            LogUtil.getUtils(THIS_FILE).d("DTMF - Send pending dtmf " + dtmfToAutoSend.get(callId) + " for "
                    + callId);
            sendDtmf(callId, dtmfToAutoSend.get(callId));
        }
    }

    public void stopDialtoneGenerator(int callId) {
        if (dtmfDialtoneGenerators.get(callId) != null) {
            dtmfDialtoneGenerators.get(callId).stopDialtoneGenerator();
            dtmfDialtoneGenerators.put(callId, null);
        }
        if (dtmfToAutoSend.get(callId) != null) {
            dtmfToAutoSend.put(callId, null);
        }
        if (dtmfTasks.get(callId) != null) {
            dtmfTasks.get(callId).cancel();
            dtmfTasks.put(callId, null);
        }
    }

    public void startWaittoneGenerator(int callId) {
        if (waittoneGenerators.get(callId) == null) {
            waittoneGenerators.put(callId, new PjStreamDialtoneGenerator(callId, false));
        }
        waittoneGenerators.get(callId).startPjMediaWaitingTone();
    }
    
    public void stopWaittoneGenerator(int callId) {
        if (waittoneGenerators.get(callId) != null) {
            waittoneGenerators.get(callId).stopDialtoneGenerator();
            waittoneGenerators.put(callId, null);
        }
    }

    public int callHold(int callId) throws SameThreadException {
        if (created) {
            return pjsua.call_set_hold(callId, null);
        }
        return -1;
    }

    public int callReinvite(int callId, boolean unhold) throws SameThreadException {
        if (created) {
            return pjsua.call_reinvite(callId,
                    unhold ? pjsua_call_flag.PJSUA_CALL_UNHOLD.swigValue() : 0, null);

        }
        return -1;
    }

    public SipCallSession getCallInfo(int callId) {
        if (created/* && !creating */&& userAgentReceiver != null) {
            SipCallSession callInfo = userAgentReceiver.getCallInfo(callId);
            return callInfo;
        }
        return null;
    }
    
    public SipCallSession getPublicCallInfo(int callId) {
        SipCallSession internalCallSession = getCallInfo(callId);
        if( internalCallSession == null) {
            return null;
        }
        return new SipCallSession(internalCallSession);
    }

    public void setBluetoothOn(boolean on) throws SameThreadException {
        if (created && mediaManager != null) {
            mediaManager.setBluetoothOn(on);
        }
    }

    /**
     * Mute microphone
     * 
     * @param on true if microphone has to be muted
     * @throws SameThreadException
     */
    public void setMicrophoneMute(boolean on) throws SameThreadException {
        if (created && mediaManager != null) {
            mediaManager.setMicrophoneMute(on);
        }
    }


    public void setMediaState(MediaState state) {
        if(created && mediaManager != null) {
            mediaManager.setMediaState(state);
        }
    }
    /**
     * Change speaker phone mode
     * 
     * @param on true if the speaker mode has to be on.
     * @throws SameThreadException
     */
    public void setSpeakerphoneOn(boolean on) throws SameThreadException {
        if (created && mediaManager != null) {
            mediaManager.setSpeakerphoneOn(on);
        }
    }

    public void setHeadsetOn(boolean on) throws SameThreadException {
        if (created && mediaManager != null) {
            mediaManager.setHeadsetOn(on);
        }
    }

    public SipCallSession[] getCalls() {
        if (created && userAgentReceiver != null) {
            SipCallSession[] callsInfo = userAgentReceiver.getCalls();
            return callsInfo;
        }
        return new SipCallSession[0];
    }

    public void confAdjustTxLevel(int port, float value) throws SameThreadException {
        if (created && userAgentReceiver != null) {
            pjsua.conf_adjust_tx_level(port, value);
        }
    }

    public void confAdjustRxLevel(int port, float value) throws SameThreadException {
        if (created && userAgentReceiver != null) {
            pjsua.conf_adjust_rx_level(port, value);
        }
    }

    public void setEchoCancellation(boolean on) throws SameThreadException {
        if (created && userAgentReceiver != null) {
            LogUtil.getUtils(THIS_FILE).d("set echo cancelation " + on);
            pjsua.set_ec(on ? prefsWrapper.getEchoCancellationTail() : 0,
                    prefsWrapper.getPreferenceIntegerValue(SipConfigManager.ECHO_MODE));
        }
    }

    public void adjustStreamVolume(int stream, int direction, int flags) {
        if (mediaManager != null) {
            mediaManager.adjustStreamVolume(stream, direction, AudioManager.FLAG_SHOW_UI);
        }
    }

    public void silenceRinger() {
        if (mediaManager != null) {
            mediaManager.stopRingAndUnfocus();
        }
    }

    /**
     * Change account registration / adding state
     * 
     * @param account The account to modify registration
     * @param renew if 0 we ask for deletion of this account; if 1 we ask for
     *            registration of this account (and add if necessary)
     * @param forceReAdd if true, we will first remove the account and then
     *            re-add it
     * @return true if the operation get completed without problem
     * @throws SameThreadException
     */
    public boolean setAccountRegistration(SipProfile account, int renew, boolean forceReAdd)
            throws SameThreadException {
        int status = -1;
        if (!created || account == null) {
            LogUtil.getUtils(THIS_FILE).e("PJSIP is not started here, nothing can be done");
            return false;
        }
        if (account.id == SipProfile.INVALID_ID) {
            LogUtil.getUtils(THIS_FILE).w("Trying to set registration on a deleted account");
            return false;
        }


        SipProfileState profileState = getProfileState(account);
        
        // If local account -- Ensure we are not deleting, because this would be
        // invalid
        if (profileState.getWizard().equalsIgnoreCase("LOCAL")) {
            if (renew == 0) {
                return false;
            }
        }

        // In case of already added, we have to act finely
        // If it's local we can just consider that we have to re-add account
        // since it will actually just touch the account with a modify
        if (profileState != null && profileState.isAddedToStack()
                && !profileState.getWizard().equalsIgnoreCase("LOCAL")) {
            // The account is already there in accounts list
            //20170224-mengbo : 动态获取URI
            //service.getContentResolver().delete(
            //        ContentUris.withAppendedId(SipProfile.ACCOUNT_STATUS_URI, account.id), null,
            //        null);
            service.getContentResolver().delete(
                    ContentUris.withAppendedId(SipProfile.getAccountStatusUri(service), account.id), null,
                    null);
            LogUtil.getUtils(THIS_FILE).d("Account already added to stack, remove and re-load or delete");
            if (renew == 1) {
                if (forceReAdd) {
                    status = pjsua.acc_del(profileState.getPjsuaId());
                    addAccount(account);
                } else {
//                    pjsua.acc_set_online_status(profileState.getPjsuaId(),
//                            getOnlineForStatus(service.getPresence()));
                    status = pjsua.acc_set_registration(profileState.getPjsuaId(), renew);
                }
            } else {
                // if(status == pjsuaConstants.PJ_SUCCESS && renew == 0) {
                LogUtil.getUtils(THIS_FILE).d("Delete account !!");
                status = pjsua.acc_del(profileState.getPjsuaId());
            }
        } else {
            if (renew == 1) {
                addAccount(account);
            } else {
                LogUtil.getUtils(THIS_FILE).w("Ask to unregister an unexisting account !!" + account.id);
            }

        }
        // PJ_SUCCESS = 0
        return status == 0;
    }

    /**
     * Set self presence
     * 
     * @param presence the SipManager.SipPresence
     * @param statusText the text of the presence
     * @throws SameThreadException
     */
    public void setPresence(PresenceStatus presence, String statusText, long accountId)
            throws SameThreadException {
        if (!created) {
            LogUtil.getUtils(THIS_FILE).e("PJSIP is not started here, nothing can be done");
            return;
        }
        SipProfile account = new SipProfile();
        account.id = accountId;
        SipProfileState profileState = getProfileState(account);

        // In case of already added, we have to act finely
        // If it's local we can just consider that we have to re-add account
        // since it will actually just touch the account with a modify
        if (profileState != null && profileState.isAddedToStack()) {
            // The account is already there in accounts list
//            pjsua.acc_set_online_status(profileState.getPjsuaId(), getOnlineForStatus(presence));
        }

    }

    private int getOnlineForStatus(PresenceStatus presence) {
        return presence == PresenceStatus.ONLINE ? 1 : 0;
    }

    public static long getAccountIdForPjsipId(Context context, int pjId) {
        long accId = SipProfile.INVALID_ID;

        //20170224-mengbo : 动态获取URI
        //Cursor c = ctxt.getContentResolver().query(SipProfile.ACCOUNT_STATUS_URI, null, null,
        //        null, null);
        Cursor c = context.getContentResolver().query(SipProfile.getAccountStatusUri(context), null, null,
                null, null);
        if (c != null) {
            try {
                c.moveToFirst();
                do {
                	/**Begin:sunyunlei 添加对索引有效性判断，防止游标越界 20140813**/
                	int index=c.getColumnIndex(SipProfileState.PJSUA_ID);
                	if(index==-1){
                		break;
                	}
                    int pjsuaId = c.getInt(index);
                    /**End:sunyunlei 添加对索引有效性判断，防止游标越界 20140813**/
                    LogUtil.getUtils(THIS_FILE).d("Found pjsua " + pjsuaId + " searching " + pjId);
                    if (pjsuaId == pjId) {
                        accId = c.getInt(c.getColumnIndex(SipProfileState.ACCOUNT_ID));
                        break;
                    }
                } while (c.moveToNext());
            } catch (Exception e) {
               e.printStackTrace();
            } finally {
                c.close();
            }
        }
        return accId;
    }

    public SipProfile getAccountForPjsipId(int pjId) {
        long accId = getAccountIdForPjsipId(service, pjId);
        if (accId == SipProfile.INVALID_ID) {
            return null;
        } else {
            return service.getAccount(accId);
        }
    }

    public int validateAudioClockRate(int aClockRate) {
        if (mediaManager != null) {
            return mediaManager.validateAudioClockRate(aClockRate);
        }
        return -1;
    }

    public void setAudioInCall(int beforeInit) {
        if (mediaManager != null) {
            mediaManager.setAudioInCall(beforeInit == pjsuaConstants.PJ_TRUE);
        }
    }

    public void unsetAudioInCall() {

        if (mediaManager != null) {
            mediaManager.unsetAudioInCall();
        }
    }

    public SipCallSession getActiveCallInProgress() {
        if (created && userAgentReceiver != null) {
            return userAgentReceiver.getActiveCallInProgress();
        }
        return null;
    }

    /** 20160908-mengbo-start add. 判断是否存在活跃的会话**/
    public boolean hasActiveCallInProgress() {
        if (created && userAgentReceiver != null) {
            SipCallSession sipCallSession = userAgentReceiver.getActiveCallInProgress();
            if(sipCallSession != null){
                return true;
            }
        }
        return false;
    }
    /** 20160908-mengbo-end **/

    /** 20170103-mengbo-start add. 判断是否存在正在挂断的会话**/
    public boolean hasHangingCallInProgress() {
        if (created && userAgentReceiver != null) {
            SipCallSession sipCallSession = userAgentReceiver.getHangingCallInProgress();
            if(sipCallSession != null){
                return true;
            }
        }
        return false;
    }
    /** 20170103-mengbo-end **/

    /**
     * Get hanging call in progress. Add by xjq, 2015/4.2
     * @return callinfo
     */
    public SipCallSession getHangingCallInProgress() {
        if (created && userAgentReceiver != null) {
            return userAgentReceiver.getHangingCallInProgress();
        }
        return null;
    }

    /**
     * 获取当前正在通话中的回话。xjq 2015-09-03
     * @return
     */
    public SipCallSession getCallConfirmed() {
        if (created && userAgentReceiver != null) {
            return userAgentReceiver.getCallConfirmed();
        }
        return null;
    }

    public void refreshCallMediaState(final int callId) {
        service.getExecutor().execute(new SipRunnable() {
            @Override
            public void doRun() throws SameThreadException {
                if (created && userAgentReceiver != null) {
                    userAgentReceiver.updateCallMediaState(callId);
                }
            }
        });
    }

    /**
     * Transform a string callee into a valid sip uri in the context of an
     * account
     * 
     * @param callee the callee string to call
     * @param accountId the context account
     * @return ToCall object representing what to call and using which account
     */
    private ToCall sanitizeSipUri(String callee, long accountId) throws SameThreadException {
//        LogUtil.e(THIS_FILE, "sanitizeSipUri()中callee的值" + callee);
        // accountId is the id in term of csipsimple database
        // pjsipAccountId is the account id in term of pjsip adding
        int pjsipAccountId = (int) SipProfile.INVALID_ID;

        // Fake a sip profile empty to get it's profile state
        // Real get from db will be done later
        SipProfile account = new SipProfile();
        account.id = accountId;
        SipProfileState profileState = getProfileState(account);
        long finalAccountId = accountId;

        // If this is an invalid account id
        if (accountId == SipProfile.INVALID_ID || !profileState.isAddedToStack()) {
            int defaultPjsipAccount = pjsua.acc_get_default();

            boolean valid = false;
            account = getAccountForPjsipId(defaultPjsipAccount);
            if (account != null) {
                profileState = getProfileState(account);
                valid = profileState.isAddedToStack();
            }
            // If default account is not active
            if (!valid) {
                //20170224-mengbo : 动态获取URI
                //Cursor c = service.getContentResolver().query(SipProfile.ACCOUNT_STATUS_URI, null,
                //        null, null, null);
                Cursor c = service.getContentResolver().query(SipProfile.getAccountStatusUri(service), null,
                        null, null, null);
                if (c != null) {
                    try {
                        if (c.getCount() > 0) {
                            c.moveToFirst();
                            do {
                                SipProfileState ps = new SipProfileState(c);
                                if (ps.isValidForCall()) {
                                    finalAccountId = ps.getAccountId();
                                    pjsipAccountId = ps.getPjsuaId();
                                    break;
                                }
                            } while (c.moveToNext());
                        }
                    } catch (Exception e) {
                        LogUtil.getUtils(THIS_FILE).e("Error on looping over sip profiles state", e);
                    } finally {
                        c.close();
                    }
                }
            } else {
                // Use the default account
                finalAccountId = profileState.getAccountId();
                pjsipAccountId = profileState.getPjsuaId();
            }
        } else {
            // If the account is valid
            pjsipAccountId = profileState.getPjsuaId();
        }

        if (pjsipAccountId == SipProfile.INVALID_ID) {
            LogUtil.getUtils(THIS_FILE).e("Unable to find a valid account for this call");
            return null;
        }

        // Check integrity of callee field

        ParsedSipContactInfos finalCallee = SipUri.parseSipContact(callee);
//        LogUtil.e(THIS_FILE, "sanitizeSipUri()中finalCallee的值" + finalCallee);
        if(!TextUtils.isEmpty(callee)){
        	/**Begin:sunyunlei 根据密信号获取联系人姓名 20150226**/
            /*XdjaContact xdjaContact = new XdjaContact(service,callee);
            if(xdjaContact!=null && !TextUtils.isEmpty(xdjaContact.getContactName())) {
                finalCallee.displayName = xdjaContact.getContactName();
            }*/
            /**End:sunyunlei 根据密信号获取联系人姓名 20150226**/
        }

        if (TextUtils.isEmpty(finalCallee.domain) ||
                TextUtils.isEmpty(finalCallee.scheme)) {
            account = PhoneManager.getInstance().buildCustAccount(service, SPUtil.getVoIPIncomingAddr(service)); // xjq
        }

        if (TextUtils.isEmpty(finalCallee.domain)) {
            String defaultDomain = account.getDefaultDomain();
            finalCallee.domain = defaultDomain;
//            LogUtil.e(THIS_FILE, "account.getDefaultDomain():" + finalCallee.domain);
        }
        if (TextUtils.isEmpty(finalCallee.scheme)) {
            if (!TextUtils.isEmpty(account.default_uri_scheme)) {
                finalCallee.scheme = account.default_uri_scheme;
//                LogUtil.e(THIS_FILE, "account.default_uri_scheme():" + finalCallee.scheme);
            } else {
                finalCallee.scheme = SipManager.PROTOCOL_SIP;
            }
        }
        String digitsToAdd = null;
        if (!TextUtils.isEmpty(finalCallee.userName) &&
                (finalCallee.userName.contains(",") || finalCallee.userName.contains(";"))) {
            int commaIndex = finalCallee.userName.indexOf(",");
            int semiColumnIndex = finalCallee.userName.indexOf(";");
            if (semiColumnIndex > 0 && semiColumnIndex < commaIndex) {
                commaIndex = semiColumnIndex;
            }
            digitsToAdd = finalCallee.userName.substring(commaIndex);
            finalCallee.userName = finalCallee.userName.substring(0, commaIndex);
//            LogUtil.e(THIS_FILE, "sanitizeSipUri()中finalCallee的userName值" + finalCallee.userName);
        }

//        LogUtil.e(THIS_FILE, "will call " + finalCallee);

        //设置成true，用于显示来电人的displayname-Mod-Lixin-0415
        if (pjsua.verify_sip_url(finalCallee.toString(true)) == 0) {
            // In worse worse case, find back the account id for uri.. but
            // probably useless case
            if (pjsipAccountId == SipProfile.INVALID_ID) {
                pjsipAccountId = pjsua.acc_find_for_outgoing(pjsua.pj_str_copy(finalCallee
                        .toString(true)));
            }
            return new ToCall(pjsipAccountId, finalCallee.toString(true), digitsToAdd);
        }

        return null;
    }

    public void onGSMStateChanged(int state, String incomingNumber) throws SameThreadException {
        // Avoid ringing if new GSM state is not idle
        if (state != TelephonyManager.CALL_STATE_IDLE && mediaManager != null) {
            mediaManager.stopRingAndUnfocus();
        }

        // If new call state is not idle
        if (state != TelephonyManager.CALL_STATE_IDLE && userAgentReceiver != null) {
            SipCallSession currentActiveCall = userAgentReceiver.getActiveCallOngoing();
            // If we have a sip call on our side
            if (currentActiveCall != null) {
                AudioManager am = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    // GSM is now off hook => hold current sip call
                    hasBeenHoldByGSM = currentActiveCall.getCallId();

                    /* BEGIN:ACE手机上暂时不做通话保持，直接挂断加密电话。Add by xjq, 2015/3/26 */

//                    ToastUtil.showToast(service, "当前加密电话正在保持");
//                    userAgentReceiver.pauseCallStatics(hasBeenHoldByGSM);
//
//                    callHold(hasBeenHoldByGSM);
//                    Log.e(THIS_FILE, "xjq hold on call id = " + hasBeenHoldByGSM);
//                    pjsua.set_no_snd_dev();

                    pjsua.call_hangup(hasBeenHoldByGSM, 0, null,null);

                    Log.d("voip_disconnect", "UAStateReceiver onGSMStateChanged -> (pjsua.call_hangup)");

                    XToast.showErrorTop(service, ActomaController.getApp().getString(R.string.ENCRYPT_CALL_STOP));
                    /* END:ACE手机上暂时不做通话保持，直接挂断加密电话。Add by xjq, 2015/3/26 */

                    am.setMode(AudioManager.MODE_IN_CALL);
                } else {

                    /** 20170208-mengbo-start: 通话时，系统来电静音去除 **/
                    //// We have a ringing incoming call.
                    //// Avoid ringing
                    //hasBeenChangedRingerMode = am.getRingerMode();
                    //am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    /** 20170208-mengbo-end **/

                    // And try to notify with tone
                    if (mediaManager != null) {
                        mediaManager.playInCallTone(MediaManager.TONE_CALL_WAITING);
                    }
                }
            }
        } else {
            // GSM is now back to an IDLE state, resume previously stopped SIP
            // calls
            if (hasBeenHoldByGSM != null && isCreated()) {
                /* BEGIN: ACE手机上不能调用此接口，否则会崩溃。 Add by xjq, 2015/3/26 */
//                pjsua.set_snd_dev(0, 0);
//                ToastUtil.showToast(service, "当前加密电话已恢复");
//
//                userAgentReceiver.resumeCallStatics(hasBeenHoldByGSM);
//                callReinvite(hasBeenHoldByGSM, true);
                /* END: ACE手机上不能调用此接口，否则会崩溃。 Add by xjq, 2015/3/26 */

                hasBeenHoldByGSM = null;


            }

            if(state == TelephonyManager.CALL_STATE_IDLE && userAgentReceiver != null) {
                SipCallSession currentActiveCall = userAgentReceiver.getActiveCallOngoing();
                if(currentActiveCall != null) {
                    // 此处因该重新打开音频设备 xjq 2016-05-17 14:46
                    setNoSnd();
                    setSnd();
                }
            }

            /** 20170208-mengbo-start: 通话时，系统来电静音去除,不会执行此处代码 **/
            //// GSM is now back to an IDLE state, reset ringerMode if was
            //// changed.
            //if (hasBeenChangedRingerMode != null) {
            //    AudioManager am = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
            //    am.setRingerMode(hasBeenChangedRingerMode);
            //    hasBeenChangedRingerMode = null;
            //}
            /** 20170208-mengbo-end **/
        }
    }

    /*
     * public void sendKeepAlivePackets() throws SameThreadException {
     * ArrayList<SipProfileState> accounts = getActiveProfilesState(); for
     * (SipProfileState acc : accounts) {
     * pjsua.send_keep_alive(acc.getPjsuaId()); } }
     */

    public void zrtpSASVerified(int callId) throws SameThreadException {
        if (!created) {
            return;
        }
        pjsua.jzrtp_SASVerified(callId);
    }

    public void zrtpSASRevoke(int callId) throws SameThreadException {
        if (!created) {
            return;
        }
        pjsua.jzrtp_SASRevoked(callId);
    }
    
    protected void setDetectedNatType(String natName, int status) {
        // Maybe we will need to treat status to eliminate some set (depending of unknown string fine for 3rd part dev) 
        mNatDetected = natName;
    }

    /**
     * @return nat type name detected by pjsip. Empty string if nothing detected
     */
    public String getDetectedNatType() {
        return mNatDetected;
    }

    // Config subwrapper
    private pj_str_t[] getNameservers() {
        pj_str_t[] nameservers = null;

        if (prefsWrapper.enableDNSSRV()) {
            String prefsDNS = prefsWrapper
                    .getPreferenceStringValue(SipConfigManager.OVERRIDE_NAMESERVER);
            if (TextUtils.isEmpty(prefsDNS)) {
                String ipv6Escape = "[ \\[\\]]";
                String ipv4Matcher = "^\\d+(\\.\\d+){3}$";
                String ipv6Matcher = "^[0-9a-f]+(:[0-9a-f]*)+:[0-9a-f]+$";
                List<String> dnsServers;
                List<String> dnsServersAll = new ArrayList<>();
                List<String> dnsServersIpv4 = new ArrayList<>();
                for (int i = 1; i <= 2; i++) {
                    String dnsName = prefsWrapper.getSystemProp("net.dns" + i);
                    if (!TextUtils.isEmpty(dnsName)) {
                        dnsName = dnsName.replaceAll(ipv6Escape, "");
                        if (!TextUtils.isEmpty(dnsName) && !dnsServersAll.contains(dnsName)) {
                            if (dnsName.matches(ipv4Matcher) || dnsName.matches(ipv6Matcher)) {
                                dnsServersAll.add(dnsName);
                            }
                            if (dnsName.matches(ipv4Matcher)) {
                                dnsServersIpv4.add(dnsName);
                            }
                        }
                    }
                }

                if (dnsServersIpv4.size() > 0) {
                    // Prefer pure ipv4 list since pjsua doesn't manage ipv6
                    // resolution yet
                    dnsServers = dnsServersIpv4;
                } else {
                    dnsServers = dnsServersAll;
                }

                if (dnsServers.size() == 0) {
                    // This is the ultimate fallback... we should never be there
                    // !
                    nameservers = new pj_str_t[] {
                            pjsua.pj_str_copy("127.0.0.1")
                    };
                } else if (dnsServers.size() == 1) {
                    nameservers = new pj_str_t[] {
                            pjsua.pj_str_copy(dnsServers.get(0))
                    };
                } else {
                    nameservers = new pj_str_t[] {
                            pjsua.pj_str_copy(dnsServers.get(0)),
                            pjsua.pj_str_copy(dnsServers.get(1))
                    };
                }
            } else {
                nameservers = new pj_str_t[] {
                        pjsua.pj_str_copy(prefsDNS)
                };
            }
        }
        return nameservers;
    }

    private pjmedia_srtp_use getUseSrtp() {
        try {
            int use_srtp = Integer.parseInt(prefsWrapper
                    .getPreferenceStringValue(SipConfigManager.USE_SRTP));
            if (use_srtp >= 0) {
                return pjmedia_srtp_use.swigToEnum(use_srtp);
            }
        } catch (NumberFormatException e) {
            LogUtil.getUtils(THIS_FILE).e("Transport port not well formated");
        }
        return pjmedia_srtp_use.PJMEDIA_SRTP_DISABLED;
    }

    public void setNoSnd() throws SameThreadException {
        if (!created) {
            return;
        }
        pjsua.set_no_snd_dev();
    }

    public void setSnd() throws SameThreadException {
        if (!created) {
            return;
        }
        pjsua.set_snd_dev(0, 0);
    }

    // Recorder
    private SparseArray<List<IRecorderHandler>> callRecorders = new SparseArray<>();

    /**
     * Start recording of a call.
     * 
     * @param callId the call id of the call to record
     * @throws SameThreadException virtual exception to be sure we are calling
     *             this from correct thread
     */
    public void startRecording(int callId, int way) throws SameThreadException {
        // Make sure we are in a valid state for recording
        if (!canRecord(callId)) {
            return;
        }
        // Sanitize call way : if 0 assume all
        if (way == 0) {
            way = SipManager.BITMASK_ALL;
        }

        try {
            File recFolder = PreferencesProviderWrapper.getRecordsFolder(service);
            IRecorderHandler inRecoder = new SimpleWavRecorderHandler(getCallInfo(callId), recFolder,
                    SipManager.BITMASK_IN);
            IRecorderHandler outRecoder = new SimpleWavRecorderHandler(getCallInfo(callId), recFolder,
                    SipManager.BITMASK_OUT);

            List<IRecorderHandler> recordersList = callRecorders.get(callId,
                    new ArrayList<IRecorderHandler>());
            recordersList.add(inRecoder);
            recordersList.add(outRecoder);
            callRecorders.put(callId, recordersList);
            inRecoder.startRecording();
            outRecoder.startRecording();
            userAgentReceiver.updateRecordingStatus(callId, false, true);
        } catch (IOException e) {
            e.printStackTrace();
//            service.notifyUserOfMessage("不能写入录音文件");
        } catch (RuntimeException e) {
            e.printStackTrace();
            LogUtil.getUtils(THIS_FILE).e("Impossible to record ", e);
        }
    }

    /**
     * Stop recording of a call.
     * 
     * @param callId the call to stop record for.
     * @throws SameThreadException virtual exception to be sure we are calling
     *             this from correct thread
     */
    public void stopRecording(int callId) throws SameThreadException {
        if (!created) {
            return;
        }
        List<IRecorderHandler> recoders = callRecorders.get(callId, null);
        if (recoders != null) {
            for (IRecorderHandler recoder : recoders) {
                recoder.stopRecording();
                // Broadcast to other apps the a new sip record has been done
                SipCallSession callInfo = getPublicCallInfo(callId);
                Intent it = new Intent(SipManager.ACTION_SIP_CALL_RECORDED);
                it.putExtra(SipManager.EXTRA_CALL_INFO, callInfo);
                recoder.fillBroadcastWithInfo(it);
                service.sendBroadcast(it, SipManager.PERMISSION_USE_SIP);
            }
            // In first case we drop everything
            callRecorders.delete(callId);
            userAgentReceiver.updateRecordingStatus(callId, true, false);
        }
    }

    /**
     * Can we record for this call id ?
     * 
     * @param callId The call id to record to a file
     * @return true if seems to be possible to record this call.
     */
    public boolean canRecord(int callId) {
        if (!created) {
            // Not possible to record if service not here
            return false;
        }
        SipCallSession callInfo = getCallInfo(callId);
        if (callInfo == null) {
            // Not possible to record if no call info for given call id
            return false;
        }
        int ms = callInfo.getMediaStatus();
        if (ms != SipCallSession.MediaState.ACTIVE &&
                ms != SipCallSession.MediaState.REMOTE_HOLD) {
            // We can't record if media state not running on our side
            return false;
        }
        return true;
    }

    /**
     * Are we currently recording the call?
     * 
     * @param callId The call id to test for a recorder presence
     * @return true if recording this call
     */
    @SuppressLint("SimplifiableIfStatement")
    public boolean isRecording(int callId) throws SameThreadException {
        List<IRecorderHandler> recorders = callRecorders.get(callId, null);
        if (recorders == null) {
            return false;
        }
        return recorders.size() > 0;
    }

    // Stream players
    // We use a list for future possible extensions. For now api only manages
    // one
    private SparseArray<List<IPlayerHandler>> callPlayers = new SparseArray<>();

    /**
     * Play one wave file in call stream.
     * 
     * @param filePath The path to the file we'd like to play
     * @param callId The call id we want to play to. Even if we only use
     *            {@link SipManager#BITMASK_IN} this must correspond to some
     *            call since it's used to identify internally created player.
     * @param way The way we want to play this file to. Bitmasked value that
     *            could be compounded of {@link SipManager#BITMASK_IN} (read
     *            local) and {@link SipManager#BITMASK_OUT} (read to remote
     *            party of the call)
     * @throws SameThreadException virtual exception to be sure we are calling
     *             this from correct thread
     */
    public void playWaveFile(String filePath, int callId, int way) throws SameThreadException {
        if (!created) {
            return;
        }
        // Stop any current player
        stopPlaying(callId);
        if (TextUtils.isEmpty(filePath)) {
            // Nothing to do if we have not file path
            return;
        }
        if (way == 0) {
            way = SipManager.BITMASK_ALL;
        }

        // We create a new player conf port.
        try {
            IPlayerHandler player = new SimpleWavPlayerHandler(getCallInfo(callId), filePath, way);
            List<IPlayerHandler> playersList = callPlayers.get(callId,
                    new ArrayList<IPlayerHandler>());
            playersList.add(player);
            callPlayers.put(callId, playersList);

            player.startPlaying();
        } catch (IOException e) {
            // TODO : add a can't read file txt
//            service.notifyUserOfMessage("不能写入文件");
            LogUtil.getUtils(THIS_FILE).e("CALL_DBG 播放语音提示文件失败，文件路径 " + filePath);
            XToast.show(service, ActomaController.getApp().getString(R.string.CANNOT_SEND_TIPS));

        } catch (RuntimeException e) {
            LogUtil.getUtils(THIS_FILE).e("Impossible to play file", e);
        }
    }

    /**
     * Stop eventual player for a given call.
     * 
     * @param callId the call id corresponding to player previously created with
     *            {@link #playWaveFile(String, int, int)}
     * @throws SameThreadException virtual exception to be sure we are calling
     *             this from correct thread
     */
    public void stopPlaying(int callId) throws SameThreadException {
        List<IPlayerHandler> players = callPlayers.get(callId, null);
        if (players != null) {
            for (IPlayerHandler player : players) {
                player.stopPlaying();
            }
            callPlayers.delete(callId);
        }
    }

    public void updateTransportIp(String oldIPAddress) throws SameThreadException {
        if (!created) {
            return;
        }
        LogUtil.getUtils(THIS_FILE).d("Trying to update my address in the current call to " + oldIPAddress);
        pjsua.update_transport(pjsua.pj_str_copy(oldIPAddress));
    }

//    public static String pjStrToString(pj_str_t pjStr) {
//        try {
//            if (pjStr != null) {
//                // If there's utf-8 ptr length is possibly lower than slen
//                int len = pjStr.getSlen();
//                if (len > 0 && pjStr.getPtr() != null) {
//                    // Be robust to smaller length detected
//                    if (pjStr.getPtr().length() < len) {
//                        len = pjStr.getPtr().length();
//                    }
//
//                    if (len > 0) {
//                        String str = pjStr.getPtr().substring(0, len);
//
//                        Log.e("mb","******PjSipService**** pjStr.getPtr().substring(0, len):"+str);
//
//                        return str;
//                    }
//                }
//            }
//        } catch (StringIndexOutOfBoundsException e) {
//            Log.e(THIS_FILE, "Impossible to retrieve string from pjsip ", e);
//        }
//
//        Log.e("mb","******PjSipService**** return:"+"");
//
//
//        return "";
//    }

    // mengbo 2016-08-13 start 修复 JNI NewStringUTF input is not valid Modified UTF-8 问题，调用pjStr.getPtr()存在此问题
    public static String pjStrToString(pj_str_t pjStr) {
        try {
            if (pjStr != null) {
                // If there's utf-8 ptr length is possibly lower than slen
                int pj_str_t_SLen = pjStr.getSlen();
                int pjStrRealLen = pjsua.pj_get_str_len(pjStr);

                short[] pjShort = new short[pjStrRealLen];
                byte[] pjbyte = new byte[pjStrRealLen];

                pjsua.pj_get_str(pjStr, pjShort);
                for(int i=0; i<pjStrRealLen; i++) {
                    pjbyte[i] = (byte)(pjShort[i]&0xff);
                }

                String transPjStr = "";
                if(pjbyte != null){
                    transPjStr = new String(pjbyte, Charset.forName("UTF-8"));
                }

                if (pj_str_t_SLen > 0 && transPjStr != null) {
                    // Be robust to smaller length detected
                    if (transPjStr.length() < pj_str_t_SLen) {
                        pj_str_t_SLen = transPjStr.length();
                    }

                    if (pj_str_t_SLen > 0) {
                        String str = transPjStr.substring(0, pj_str_t_SLen);
                        return str;
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException e) {
            LogUtil.getUtils(THIS_FILE).e("Impossible to retrieve string from pjsip ", e);
        }
        return "";
    }
    // mengbo 2016-08-13 end.

    /**
     * Get the signal level
     * @param port The pjsip port to get signal from
     * @return an encoded long with rx level on higher byte and tx level on lower byte
     */
    public long getRxTxLevel(int port) {
        long[] rx_level = new long[1];
        long[] tx_level = new long[1];
        pjsua.conf_get_signal_level(port, tx_level, rx_level);
        return (rx_level[0] << 8 | tx_level[0]);
    }

    /**
     * Connect mic source to speaker output.
     * Usefull for tests.
     */
    public void startLoopbackTest() {
        pjsua.conf_connect(0, 0);
    }
    
    /**
     * Stop connection between mic source to speaker output.
     */
    public void stopLoopbackTest() {
        pjsua.conf_disconnect(0, 0);
    }
    
    
    private Map<String, PjsipModule> pjsipModules = new HashMap<>();

    private void initModules() {
        // TODO : this should be more modular and done from outside
        PjsipModule rModule = new RegHandlerModule();
        pjsipModules.put(RegHandlerModule.class.getCanonicalName(), rModule);

        rModule = new SipClfModule();
        pjsipModules.put(SipClfModule.class.getCanonicalName(), rModule);
        
        rModule = new EarlyLockModule();
        pjsipModules.put(EarlyLockModule.class.getCanonicalName(), rModule);

        for (PjsipModule mod : pjsipModules.values()) {
            mod.setContext(service);
        }
    }

    // 20160903-mengbo-start 释放module持有的service
    private void releaseModules() {
        for (PjsipModule mod : pjsipModules.values()) {
            mod.release();
        }
    }
    // 20160903-mengbo-end

    public interface PjsipModule {
        /**
         * Set the android context for the module. Could be usefull to get
         * preferences for examples.
         * 
         * @param ctxt android context
         */
        void setContext(Context ctxt);

        /**
         * Here pjsip endpoint should have this module added.
         */
        void onBeforeStartPjsip();

        /**
         * This is fired just after account was added to pjsip and before will
         * be registered. Modules does not necessarily implement something here.
         * 
         * @param pjId the pjsip id of the added account.
         * @param acc the profile account.
         */
        void onBeforeAccountStartRegistration(int pjId, SipProfile acc);

        // mengbo 2016-09-03 start 释放持有context，避免内存泄露
        void release();
        // mengbo 2016-09-03 end
    }

    /**
     * Provide video render surface to native code.  
     * @param callId The call id for this video surface
     * @param window The video surface object
     */
    public void setVideoAndroidRenderer(int callId, SurfaceView window) {
        pjsua.vid_set_android_renderer(callId, window);
    }

    /**
     * Provide video capturer surface view (the one binded to camera).
     * @param window The surface view object
     */
    public void setVideoAndroidCapturer(SurfaceView window) {
        pjsua.vid_set_android_capturer(window);
    }

    private static int boolToPjsuaConstant(boolean v) {
        return v ? pjsuaConstants.PJ_TRUE : pjsuaConstants.PJ_FALSE;
    }


    /**
     * 重连回话 xjq 2015-09-03
     * @param callId
     * @throws SameThreadException
     */
    public void callReconnect(int callId) throws SameThreadException {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if(created) {
    		pjsua.call_reinvite(callId, 2, null);//2表示update contact，联系人地址信息需要更新
    	}
    }

}
