package com.xdja.comm.uitl;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xdja.comm.R;
import com.xdja.comm.circleimageview.XToast;

/**网络操作控制类，用于判断当前网络是否可用
 * Created by yangpeng on 2015/10/24.
 */
public class HttpUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            XToast.show(context, context.getResources().getString(R.string.net_exception));
            return false;
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {// modified by ycm for lint 2017/02/13
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        XToast.show(context, context.getResources().getString(R.string.net_exception));
        return false;
    }

    public static boolean isNetworkValid(Context context) {
        if (!TextUtils.isEmpty(GetNetworkType(context))) {
            return true;
        } else {
            showNetworkError(context);
            return false;
        }
    }

    public static String GetNetworkType(Context context) {
        String strNetworkType = "";

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }
            }
        }
        return strNetworkType;
    }

    /**
     * 无可用网络连接时弹出错误提示框
     *
     * @param context 必须是Activity的context
     */
    @SuppressLint("InflateParams")
    public static void showNetworkError(Context context) {
        /*AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage("请检查网络连接").setPositiveButton("确定", null).create();
        alertDialog.show();*/

        final AlertDialog dialog = new AlertDialog.Builder(context).create();

        View view = LayoutInflater.from(context).inflate(R.layout.view_normal_dialog, null);
        dialog.setView(view);

        TextView message = (TextView) view.findViewById(R.id.dialog_message);
        message.setText(context.getResources().getString(R.string.net_error_and_retry));

        TextView title = (TextView) view.findViewById(R.id.dialog_title);
        title.setText(context.getResources().getString(R.string.hint_text));

        TextView positiveButton = (TextView) view.findViewById(R.id.dialog_ok);
        positiveButton.setVisibility(View.VISIBLE);
        positiveButton.setText(context.getResources().getString(R.string.know));

        TextView negativeButton = (TextView) view.findViewById(R.id.dialog_cancel);
        negativeButton.setVisibility(View.INVISIBLE);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.setCancelable(true);

        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        dialog.show();


    }


    public static void showError(Context context, String content) {
        /*AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage("请检查网络连接").setPositiveButton("确定", null).create();
        alertDialog.show();*/

        final AlertDialog dialog = new AlertDialog.Builder(context).create();

        View view = LayoutInflater.from(context).inflate(R.layout.view_normal_dialog, null);
        dialog.setView(view);

        TextView message = (TextView) view.findViewById(R.id.dialog_message);
        message.setText(content);

        TextView title = (TextView) view.findViewById(R.id.dialog_title);
        title.setText(context.getResources().getString(R.string.hint_text));

        TextView positiveButton = (TextView) view.findViewById(R.id.dialog_ok);
        positiveButton.setVisibility(View.VISIBLE);
        positiveButton.setText(context.getResources().getString(R.string.know));

        TextView negativeButton = (TextView) view.findViewById(R.id.dialog_cancel);
        negativeButton.setVisibility(View.INVISIBLE);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.setCancelable(true);

        /** 20161128-mengbo-start: 使用 TYPE_TOAST可以不用开启悬浮窗权限，首云手机使用 TYPE_SYSTEM_ALERT会报错 **/
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        /** 20161128-mengbo-end **/

        dialog.show();


    }


}
