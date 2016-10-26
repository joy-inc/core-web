package com.joy.webview.utils;

import android.support.annotation.Nullable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static com.joy.utils.TextUtil.TEXT_EMPTY;

/**
 * Created by Daisw on 2016/10/21.
 */

public class DocumentParser {

    @Nullable
    public static Elements getElementsByTag(Document document, String tagName) {
        if (document != null) {
            return document.getElementsByTag(tagName);
        }
        return null;
    }

    @Nullable
    public static Element getElementByTag(Document document, String tagName, int index) {
        try {
            Elements elements = getElementsByTag(document, tagName);
            if (elements != null && elements.size() > index) {
                return elements.get(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Element getFirstElementByTag(Document document, String tagName) {
        return getElementByTag(document, tagName, 0);
    }

    /**
     * eg: <description>希望每一刻大家都有幸运女神的庇护，但是如果你是那不幸的几分之几，也希望多些勇气理性面对困境。</description>
     * String desc = getTag("description");// 希望每一刻大家都有幸运女神的庇护，但是如果你是那不幸的几分之几，也希望多些勇气理性面对困境。
     *
     * @param document
     * @param tagName
     * @return
     */
    public static String getTag(Document document, String tagName) {
        Element element = getFirstElementByTag(document, tagName);
        if (element != null) {
            return element.text();
        }
        return TEXT_EMPTY;
    }

    /**
     * eg: <meta name="description" content="希望每一刻大家都有幸运女神的庇护，但是如果你是那不幸的几分之几，也希望多些勇气理性面对困境。" />
     * String desc = getAttribute("name", "description").attr("content");// 希望每一刻大家都有幸运女神的庇护，但是如果你是那不幸的几分之几，也希望多些勇气理性面对困境。
     *
     * @param document
     * @param attrName
     * @param attrValue
     * @param attributeKey
     * @return
     */
    public static String getAttribute(Document document, String attrName, String attrValue, String attributeKey) {
        if (document != null) {
            Elements elements = document.getElementsByAttributeValue(attrName, attrValue);
            return elements.attr(attributeKey);
        }
        return TEXT_EMPTY;
    }
}
