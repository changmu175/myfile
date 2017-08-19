package com.xdja.imp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.imp.data.utils.HyperLinkUtil;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.HyperLinkBean;
import com.xdja.imp.ui.ViewChatDetailBaseItem;
import com.xdja.imp.ui.ViewRecTextItem;
import com.xdja.imp.widget.HyperLinkClickPop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称：ActomaV2
 * 类描述：识别消息中的超链接 并可点击
 * 创建人：yuchangmu
 * 创建时间：2016/11/25.
 * 修改人：yuchangmu
 * 修改时间：20161129
 * 修改备注：
 * 1)Task 2632, modify for recognize hyperlink function by ycm at 20161129.
 * 2)BUG 6542, modify for recognize hyperlink function by ycm at 20161201.
 * 3)BUG 6542, modify for recognize hyperlink function by ycm at 20161205.
 * 4)Task 2632, optimized for recognize web_url function by ycm at 20161207.
 */
public class RecognizeHyperlink {
    String at = "@";
    private final int LINK_WEB = 0x01;
    private final int LINK_EMAIL = 0x02;
    private final int LINK_PHONE = 0x04;
    public final int ALL = LINK_WEB | LINK_EMAIL | LINK_PHONE;
    private final String[] webPrefix = new String[]{"http://", "https://", "rtsp://"};
    private final String[] emailPrefix = new String[]{"mailto:"};
    private Activity activity;

    private static final String GOOD_IRI_CHAR = "a-zA-Z0-9\uF900-\uFDCF\uFDF0-\uFFEF";

    private static final Pattern IP_ADDRESS = Pattern.compile(
                      "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");

    private static final String GOOD_GTLD_CHAR = "a-zA-Z\uF900-\uFDCF\uFDF0-\uFFEF";

    private static final String GTLD = "[" + GOOD_GTLD_CHAR + "]{2,63}";

    private static final String IRI = "[" + GOOD_IRI_CHAR + "]([" + GOOD_IRI_CHAR + "\\-]{0,61}[" + GOOD_IRI_CHAR + "]){0,1}";

    private static final String HOST_NAME = "(" + IRI + "\\.)+" + GTLD;

    private static final Pattern DOMAIN_NAME = Pattern.compile("(" + HOST_NAME + "|" + IP_ADDRESS + ")");

    private static final Pattern WEB_URL = Pattern.compile(// 修改了url的识别规则，避免前后受中文的干扰。
            "((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                    + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                    + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
                    + "(?:" + DOMAIN_NAME + ")"
                    + "(?:\\:\\d{1,5})?)" // plus option port number
                    + "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
                    + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
                    + "(?:\\b|$)");

    public void recognizeHyperlinks(final ViewChatDetailBaseItem.LongClickCbk cbk,
                                    final TextView contentView,
                                    final Activity activity,
                                    int type) {
        this.activity = activity;
        if (type == 0) {
            return;
        }

        String content = contentView.getText().toString();
        List<HyperLinkBean> links = HyperLinkUtil.parseContent(content, type);
        int size = links.size();
        if (size > 0) {
            setLinkSpan(links, content, contentView, cbk);
        }
    }

    private void setLinkSpan(List<HyperLinkBean> links, String content, TextView contentView, final ViewRecTextItem.LongClickCbk cbk) {
            final SpannableString spannableString = BitmapUtils.formatSpanContent(content, activity, 1.1f);// modified by ycm 2017/02/03 for bug 8301
            for (final HyperLinkBean link : links) {
                ForegroundColorSpan linkColor = new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.link_color));
                spannableString.setSpan(linkColor, link.getStartPosition(), link.getEndPosition(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(
                        new SpanClickListenser() {
                            @Override
                            public void onClick(View widget) {
                                if (link.getLinkType() == LINK_WEB) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    Uri uri = Uri.parse(link.getHyperlink());
                                    intent.setData(uri);
                                    activity.startActivity(intent);
                                } else if (link.getLinkType() == LINK_EMAIL) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SENDTO);
                                    Uri uri = Uri.parse(link.getHyperlink());// modified by ycm for deleting emailPrefix 20161207
                                    intent.setData(uri);
                                    activity.startActivity(intent);
                                } else if (link.getLinkType() == LINK_PHONE) {
                                    clickPhoneLink(link.getHyperlink(), widget);
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                ds.setUnderlineText(false);
                            }
                        }
                        , link.getStartPosition(), link.getEndPosition(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            HyperlinkMovementMethod method = new HyperlinkMovementMethod();
            contentView.setText(spannableString);
            contentView.setMovementMethod(method);
            contentView.setClickable(false);
            contentView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    cbk.onLongClick();
                    return false;
                }
            });
    }

    private void clickPhoneLink(String group, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        new HyperLinkClickPop(activity, null,
                ConstDef.hyperlink_click_normal, group).showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    class SpanClickListenser extends ClickableSpan implements View.OnLongClickListener {

        @Override
        public void onClick(View v) {
        }


        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

}
