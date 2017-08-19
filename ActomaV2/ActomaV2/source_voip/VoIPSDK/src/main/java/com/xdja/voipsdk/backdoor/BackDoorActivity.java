package com.xdja.voipsdk.backdoor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.securevoip.utils.CallLogHelper;
import com.securevoip.utils.ToastUtil;
import com.xdja.comm.server.ActomaController;
import com.xdja.voipsdk.R;

@SuppressLint("AndroidLintRegistered")
public class BackDoorActivity extends Activity {

     NotificationManager notificationManager;

     public static final int NOTIFICAION_ID = 1923;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_back_door);
          notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

          generateNotification();

          final EditText peopleCount = (EditText) findViewById(R.id.people_count);

          final EditText countPerPeople = (EditText) findViewById(R.id.count_per_people);

          Button clear = (Button) findViewById(R.id.clear_notification);
          clear.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    clearNotification();
               }
          });

          Button generateCallLog = (Button) findViewById(R.id.generate_call_log);
          generateCallLog.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    if (!TextUtils.isEmpty(peopleCount.getText().toString().trim())) {
                         final int peopleNumCount = Integer.parseInt(peopleCount.getText().toString().trim());
                         if (!TextUtils.isEmpty(countPerPeople.getText().toString())) {
                              final int perNumCount = Integer.parseInt(countPerPeople.getText().toString().trim());
                              new Thread(new Runnable() {
                                   @Override
                                   public void run() {
                                        generateCallLog(peopleNumCount, perNumCount);
                                   }
                              }).start();
                         } else {
                              ToastUtil.showToast(ActomaController.getApp(), "每个人生成的通话记录数不为空");
                         }
                    } else {
                         ToastUtil.showToast(ActomaController.getApp(), "生成的人数不能为空");
                    }


               }
          });
     }

     private void clearNotification() {
          notificationManager.cancel(NOTIFICAION_ID);
     }

     private void generateNotification() {
          Notification.Builder builder = new Notification.Builder(this);
          builder.setAutoCancel(false);
          builder.setOngoing(true);
          builder.setContentTitle("点击进入后门");
          builder.setSmallIcon(R.drawable.ic_launcher);
          builder.setContentText("界面内按按钮清除通知");
          Intent intent = new Intent(this, BackDoorActivity.class);
          PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
          builder.setContentIntent(contentIntent);
          Notification notification = builder.build();
          notificationManager.notify(NOTIFICAION_ID, notification);
     }

     private void generateCallLog(int peopleNumCount, int perNumCount) {
          CallLogHelper.insertRandomCallLog(peopleNumCount, perNumCount);
     }

}
