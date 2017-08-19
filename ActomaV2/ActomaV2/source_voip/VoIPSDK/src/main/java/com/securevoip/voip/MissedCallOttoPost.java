package com.securevoip.voip;

import android.util.Log;

import com.securevoip.utils.CallLogHelper;
import com.xdja.comm.event.TabTipsEvent;

/**
 * 构建通知主框架的otto对象
 * Created by zjc on 2015/8/23.
 */
public class MissedCallOttoPost {

    public static TabTipsEvent missedCallEvent() {
        TabTipsEvent event = new TabTipsEvent();
        event.setIndex(TabTipsEvent.INDEX_VOIP);
        int missedCallCount = CallLogHelper.getMissedCallCount();
        Log.e("MissedCallOttoPost", missedCallCount + "");
        event.setIsShowPoint(false);
        //未接来电为0，不显示气泡
        if (missedCallCount > 0) {
            event.setIndex(TabTipsEvent.INDEX_VOIP);
            event.setIsShowPoint(true);
            event.setContent(missedCallCount > 99 ? "..." : missedCallCount + "");
        }
        return event;
    }

}
