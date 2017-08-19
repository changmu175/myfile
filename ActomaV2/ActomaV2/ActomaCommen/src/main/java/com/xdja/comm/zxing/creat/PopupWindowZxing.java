package com.xdja.comm.zxing.creat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.xdja.comm.R;

import java.util.Hashtable;

/**
 * 分类popWindow
 */
public class PopupWindowZxing {


    private Context context;
    private PopupWindow upLoadPopWindow = null; // 弹出窗口
    private int QR_WIDTH = 200;
    private int QR_HEIGHT = 200;
    private String addUrl;

    public PopupWindowZxing(Context context, String addFriendUrl) {
        this.context = context;
        this.addUrl = addFriendUrl;
        final float scale = context.getResources().getDisplayMetrics().density;
        QR_WIDTH = (int) (QR_WIDTH * scale);
        QR_HEIGHT = (int) (QR_HEIGHT * scale);

        initPopupWindow();
    }

    @SuppressLint("InflateParams")
    private void initPopupWindow() {
        if (upLoadPopWindow == null) {
            LayoutInflater layoutInflater = ((Activity) context)
                    .getLayoutInflater();
            View sort_view = layoutInflater.inflate(R.layout.view_pop_zxing, null);

            upLoadPopWindow = new PopupWindow(sort_view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            upLoadPopWindow.setFocusable(true);
            upLoadPopWindow.setOutsideTouchable(true);
            sort_view.setOnTouchListener(ontouchListener);
            upLoadPopWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));

            ImageView zxingImage = (ImageView) sort_view.findViewById(R.id.zxing_image);
            Bitmap bitmap = createBitmap(addUrl);
            if (bitmap != null) {
                zxingImage.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 生成二维码图片
     *
     * @return
     */
    private Bitmap createBitmap(String text) {
        Bitmap bitmap = null;
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);

            //去白边
            int[] rec = bitMatrix.getEnclosingRectangle();
            int resWidth = rec[2] + 1;
            int resHeight = rec[3] + 1;
            BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
            resMatrix.clear();
            for (int i = 0; i < resWidth; i++) {
                for (int j = 0; j < resHeight; j++) {
                    if (bitMatrix.get(i + rec[0], j + rec[1])) {
                        resMatrix.set(i, j);
                    }
                }
            }

            //填充内容
            int width = resMatrix.getWidth();
            int height = resMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (resMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void showPopupWindow() {
        View view = new View(context);
        upLoadPopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
    //add by lwl diss window start
    public void hidePopupWindow() {
       if(upLoadPopWindow!=null)
           upLoadPopWindow.dismiss();
    }
    //add by lwl diss window end
    @SuppressLint("ClickableViewAccessibility")
    private final OnTouchListener ontouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            upLoadPopWindow.dismiss();
            return false;
        }
    };
}
