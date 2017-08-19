package com.xdja.presenter_mainframe.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.xdja.comm.uitl.QRUtil;

import java.util.Hashtable;

/**
 * Created by ldy on 16/4/25.
 * 可以通过设置字符串来使imageView显示二维码
 */
public class QRImageView extends ImageView{
    Bitmap mBitmap;
    public QRImageView(Context context) {
        super(context);
    }

    public QRImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QRImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Bitmap getImageBitmap(){
        return mBitmap;
    }

    public void setQRCode(final String text){
        //防止在view还没有渲染时调用会获取不到宽高
        post(new Runnable() {
            @Override
            public void run() {
                mBitmap = createBitmap(QRUtil.AuthorizeId2QrString(text),getWidth(),getHeight());
                setImageBitmap(mBitmap);
            }
        });
    }

    /**
     * 生成二维码图片
     */
    private Bitmap createBitmap(String text,int qrWidth,int qrHeight) {
        Bitmap bitmap = null;
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);

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
                    Bitmap.Config.RGB_565);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
