package util;

import android.content.ContentValues;
import android.provider.CallLog;

import com.csipsimple.api.SipManager;
import com.securevoip.contacts.CustContacts;
import com.securevoip.utils.RandomName;
import com.xdja.comm.server.ActomaController;

import java.util.List;
import java.util.Random;

/**
 * Created by zjc on 2016/5/13.
 */
public class CallLogUtil {

     private static final String[] callTypes = new String[3];

     static {
          callTypes[0] = "1";
          callTypes[1] = "2";
          callTypes[2] = "3";
     }

     public static ContentValues randomCallLog(int actomaAccount) {

          ContentValues cv = new ContentValues();

          Random random = new Random();

          //name - 就是安通账号
          cv.put(CallLog.Calls.CACHED_NAME, actomaAccount);
          //nickname - 昵称
          cv.put(SipManager.CALLLOG_NICKNAME, RandomName.getChineseName());

          //type - 来电类型
          int typeIndex = random.nextInt(3);
          cv.put(CallLog.Calls.TYPE, callTypes[typeIndex]);

          //numbertype - 意义不明，统一为1
          cv.put(CallLog.Calls.CACHED_NUMBER_TYPE, 1);

          //date - 时间戳
          cv.put(CallLog.Calls.DATE, DateUtil.randomTimeStamp());

          //duration - 通话时长，以秒计
          int duration = random.nextInt(500);
          cv.put(CallLog.Calls.DURATION, typeIndex != 2 ? duration : 0);

          //new - 是否是未接来电
          cv.put(CallLog.Calls.NEW, typeIndex != 2 ? 0 : 1);

          //number - 完整的Url
          cv.put(CallLog.Calls.NUMBER, RandomName.getChineseName());

          //account_id - 账户id，非重要
          cv.put(SipManager.CALLLOG_PROFILE_ID_FIELD, random.nextInt(6) + 1);

          //status_code 状态码

          //status_text - 状态中文信息


          //nickname_pyfull - 昵称全拼 暂不关心

          //nickname_py - 昵称简拼 暂不关心

          //photo_uri - 图像地址 暂不关心
          //模拟数据 从我的好友里面随机读取头像
          List<String> allAvatarUrls = CustContacts.getAllThumbNailUrls(ActomaController.getApp());
          if (allAvatarUrls != null && allAvatarUrls.size() > 0) {
               cv.put(SipManager.CALLLOG_AVATAR_URL, allAvatarUrls.get(random.nextInt(allAvatarUrls.size())));
          } else {
               cv.put(SipManager.CALLLOG_AVATAR_URL, "");
          }

          return cv;
     }


}
