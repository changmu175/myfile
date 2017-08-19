package com.xdja.presenter_mainframe.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.xdja.comm.server.ActomaController;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.util.TextUtil;

/**
 * Created by ldy on 16/4/13.
 * <p>对EditTextview做了一定的设定,使其能比较方便的加入可点击的文本,在android5.0的系统以上支持文本点击变色,
 * 调用{@link #setHighlightColor(int)}方法可以在android5.0的系统以上加入文字点击变色</p>
 * <p>由于继承了EditText,所以字并不占所有的空间,四周会有空隙</p>
 * 继承EditText是因为点击文字时可以使其变色,如果最后需求也要求不变色,可以将其改为继承textview,则可以避免周围的间隙
 */
public class PartClickTextView extends EditText {
    private int mTextColorType = 0;
    public PartClickTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.PartClickTextView);
        if (typedArray.hasValue(R.styleable.PartClickTextView_underlineText)) {
            OnClickListener onClickListener = null;
            //alh@xdja.com<mailto://alh@xdja.com> 2016-09-14 add. fix bug 4037 . review by wangchao1. Strat
            mTextColorType = typedArray.getInt(R.styleable.PartClickTextView_underlineTextColor , 0);
            String content = typedArray.getString(R.styleable.PartClickTextView_underlineText);
            if (!TextUtils.isEmpty(content) && content.contains(ActomaController.getApp().getString(R.string.title_mainFrame))) {
                appendActomaText(TextUtil.getActomaText(getContext(), mTextColorType == 0 ? TextUtil.ActomaImage
                        .IMAGE_VERSION_BIG : TextUtil.ActomaImage.IMAGE_VERSION_BIG_RED, 0, 0, 0, content),
                        onClickListener);
            } else {
                appendClickableText(content, onClickListener);
            }
            //alh@xdja.com<mailto://alh@xdja.com> 2016-09-14 add. fix bug 4037 . review by wangchao1. End
        }

        setFocusable(false);
        setMovementMethod(LinkMovementMethod.getInstance());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            removeEditUnderline();
        } else {
            removeBackground();
        }

        if (!ObjectUtil.objectIsEmpty(typedArray)) {
            typedArray.recycle();
        }

        //2016-4-25 ldy 移除点击效果
        setHighlightColor(Color.TRANSPARENT);
    }

    private void removeBackground() {
        setBackgroundColor(Color.TRANSPARENT);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void removeEditUnderline() {
        setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }

    /**
     * Sets the color used to display the selection highlight.
     *
     * @param color
     * @attr ref android.R.styleable#TextView_textColorHighlight
     */
    @Override
    public void setHighlightColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            super.setHighlightColor(color);
        else
            super.setHighlightColor(Color.TRANSPARENT);
    }

    public void appendClickableText(String text, ClickableSpan clickableSpan) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        append(spannableString);
    }

    @SuppressWarnings("deprecation")
    public void appendActomaText(Spanned text, final OnClickListener onClickListener) {
        setText(text);
        setTextColor(mTextColorType == 0 ? getResources().getColor(R.color.blade_black_95) : Color.RED);
        setOnClickListener(onClickListener);
    }

    public void appendClickableText(String text, final OnClickListener onClickListener) {
        appendClickableText(text, new ClickableSpan() {
            /**
             * Makes the text underlined and in the link color.
             *
             * @param ds
             */
            @SuppressWarnings("EmptyMethod")
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);

            }

            @Override
            public void onClick(View widget) {
                if (onClickListener != null)
                    onClickListener.onClick(widget);
            }
        });
    }
}
