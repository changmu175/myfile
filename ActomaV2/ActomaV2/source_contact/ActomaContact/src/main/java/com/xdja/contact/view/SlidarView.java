package com.xdja.contact.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.xdja.contact.R;
import com.xdja.contact.callback.OnTouchingLetterChangedListener;


public class SlidarView extends View {
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    public static String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    /* public static String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
             "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
             "W", "X", "Y", "Z"};*/
    private int choose = -1;
    private Paint paint = new Paint();

    private TextView mTextDialog;

    private Context context;

    /**
     * @param mTextDialog
     */
    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }


    public SlidarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public SlidarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SlidarView(Context context) {
        super(context);
        this.context = context;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / b.length;

        for (int i = 0; i < b.length; i++) {
            //paint.setColor(Color.parseColor("#777777"));
            paint.setColor(Color.parseColor("#8A000000"));
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);
            paint.setTextSize(context.getResources().getDimension(R.dimen.slidar_text_size));
            if (i == choose) {
//                paint.setColor(Color.parseColor("#3399ff"));
                paint.setColor(Color.parseColor("#b1965f"));
                paint.setFakeBoldText(true);
            }
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * b.length);

        switch (action) {
            /*=====================modify by geyao 2015-09-03=========================*/
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //setBackgroundResource(R.color.contact_listview_slide);
                if (oldChoose != c) {
                    if (c >= 0 && c < b.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(b[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(b[c]);
                            mTextDialog.setVisibility(View.VISIBLE);

                        }

                        choose = c;
                        invalidate();
                    }
                }
                break;
            default:
                setBackgroundColor(Color.parseColor("#00000000"));
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;
             /*=====================modify by geyao 2015-09-03=========================*/
        }
        return true;
    }


    /**
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

}