package com.xdja.imp.data.error;

import android.support.annotation.Nullable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.error</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/1/21</p>
 * <p>Time:15:53</p>
 */
public class OkIMException extends OkException {

    public OkIMException(){
        super(new OkIMMatcher());
    }

    public OkIMException(@Nullable String okCode,@Nullable String okMessage){
        super(new OkIMMatcher(),okCode,okMessage);
    }

    @Nullable
    public static OkIMException buildException(@Nullable String okCode,@Nullable String okMessage) {
        return new OkIMException(okCode,okMessage);
    }

}
