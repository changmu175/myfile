package com.xdja.imp.data.utils;

import android.text.util.Linkify;
import android.util.Patterns;
import com.xdja.imp.domain.model.HyperLinkBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称：ActomaV2
 * 类描述：分享信息封装
 * 创建人：yuchangmu
 * 创建时间：2017/3/5.
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class HyperLinkUtil {
    private static final int LINK_WEB = 0x01;
    private static final int LINK_EMAIL = 0x02;
    private static final int LINK_PHONE = 0x04;
    private static final String[] webPrefix = new String[]{"http://", "https://", "rtsp://"};
    private static final String[] emailPrefix = new String[]{"mailto:"};
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

    public static List<HyperLinkBean> parseContent(String content, int type) {
        List<HyperLinkBean> links = new ArrayList<>();
        if ((type & LINK_WEB) != 0) {
            getWebOrEmailLink(links, content, WEB_URL, webPrefix, Linkify.sUrlMatchFilter);
        }

        if ((type & LINK_EMAIL) != 0) {
            getWebOrEmailLink(links, content, Patterns.EMAIL_ADDRESS, emailPrefix, null);
        }

        if ((type & LINK_PHONE) != 0) {
            getPhoneLink(links, content, Patterns.PHONE, Linkify.sPhoneNumberMatchFilter);
        }
        return hyperlinkFilter(links);
    }

    // add by ycm for phone number hyperlink click 2016/11/04 [start]
    private static void getWebOrEmailLink(List<HyperLinkBean> links,
                                   String s, Pattern pattern, String[] schemes, Linkify.MatchFilter matchFilter) {
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (matchFilter == null || matchFilter.acceptMatch(s, start, end)) {
                HyperLinkBean hyperLink = new HyperLinkBean();
                String link = createLink(matcher.group(0), schemes);
                hyperLink.setHyperlink(link);
                hyperLink.setStartPosition(start);
                hyperLink.setEndPosition(end);
                if (pattern == WEB_URL) {
                    hyperLink.setLinkType(LINK_WEB);
                } else if (pattern == Patterns.EMAIL_ADDRESS) {
                    hyperLink.setLinkType(LINK_EMAIL);
                }
                links.add(hyperLink);
            }
        }
    }

    private static String createLink(String group, String[] prefixes) {
        boolean isHasPrefix = false;
        for (String prefixe : prefixes) {
            if (group.regionMatches(true, 0, prefixe, 0, prefixe.length())) {
                isHasPrefix = true;
                break;
            }
        }

        if (!isHasPrefix) {
            group = prefixes[0] + group;
        }
        return group;
    }

    private static void getPhoneLink(List<HyperLinkBean> links, String s, Pattern pattern, Linkify.MatchFilter matchFilter) {
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (matchFilter == null || matchFilter.acceptMatch(s, start, end)) {
                HyperLinkBean hyperLink = new HyperLinkBean();
                String link = matcher.group(0); // modified by ycm for bug 6542
                hyperLink.setHyperlink(link);
                hyperLink.setStartPosition(start);
                hyperLink.setEndPosition(end);
                hyperLink.setLinkType(LINK_PHONE);
                links.add(hyperLink);
            }
        }
    }

    /**
     * 根据起始位置和结束位置和长度过滤规则，过滤出更符合的超链接
     * @param linkList
     * @return
     */
    private static List<HyperLinkBean> hyperlinkFilter(List<HyperLinkBean> linkList) {
        int len = linkList.size();
        int i = 0;
        linkList = sortAllLink(linkList);
        while (i < len - 1) {
            HyperLinkBean a = linkList.get(i);
            HyperLinkBean b = linkList.get(i + 1);
            int index = -1;
            if ((a.getStartPosition() <= b.getStartPosition()) && (a.getEndPosition() >= b.getStartPosition())) {
                if (a.getEndPosition() >= b.getEndPosition()) {
                    index = i + 1;
                } else if ((a.getEndPosition() - a.getStartPosition()) > (b.getEndPosition() - b.getStartPosition())) {
                    index = i + 1;
                } else if ((a.getEndPosition() - a.getStartPosition()) < (b.getEndPosition() - b.getStartPosition())) {
                    index = i;
                }
                if (index != -1) {
                    linkList.remove(index);
                    len--;
                    continue;
                }

            }

            i++;
        }
        return linkList;
    }

    /**
     * 根据起始位置或结束位置对超链接进行排序
     * @param linkList
     * @return
     */
    private static List<HyperLinkBean> sortAllLink(List<HyperLinkBean> linkList) {
        Comparator<HyperLinkBean> comparator = new Comparator<HyperLinkBean>() {
            @Override
            public int compare(HyperLinkBean lhs, HyperLinkBean rhs) {
                if (lhs.getStartPosition() < rhs.getStartPosition()) {
                    return -1;
                }

                if (lhs.getStartPosition() > rhs.getStartPosition()) {
                    return 1;
                }

                if (lhs.getEndPosition() < rhs.getEndPosition()) {
                    return -1;
                }

                if (lhs.getEndPosition() > rhs.getEndPosition()) {
                    return 1;
                }
                return 0;
            }
        };
        Collections.sort(linkList, comparator);
        return linkList;
    }
}
