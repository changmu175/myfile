package com.xdja.imp.util;

import com.xdja.comm.server.ActomaController;
import com.xdja.imp.R;

/**
 * Created by xrj on 2015/8/11.
 */
public enum VoicePlayState {
    DEFAULT(0, ActomaController.getApp().getString(R.string.default_type)),

    PLAYING(1, ActomaController.getApp().getString(R.string.playing)),

    SUSPAUSE(2, ActomaController.getApp().getString(R.string.pause_play)),

    STOP(3, ActomaController.getApp().getString(R.string.stop_play)),

    COMPLETION(4, ActomaController.getApp().getString(R.string.play_complete)),

    ERROR(5, ActomaController.getApp().getString(R.string.play_complete));


    private final int key;

    private final String description;

    VoicePlayState(int key, String description) {
        this.key = key;
        this.description = description;
    }

    public int getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }


    public static VoicePlayState getMessageType(int key){

        VoicePlayState voicePlayState = DEFAULT;

        for(VoicePlayState type : VoicePlayState.values()){

            if(type.getKey() == key){

                voicePlayState = type;

                break;
            }
        }
        return voicePlayState;
    }
}
