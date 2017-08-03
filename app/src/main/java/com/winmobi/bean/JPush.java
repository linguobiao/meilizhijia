package com.winmobi.bean;

import java.util.Calendar;

/**
 * Created by linguobiao on 16/8/19.
 */
public class JPush {
    private Calendar date;
    /**
     * JPushInterface.EXTRA_NOTIFICATION_TITLE
     * 保存服务器推送下来的通知的标题。
     * 对应 API 通知内容的 title 字段。
     * 对应 Portal 推送通知界面上的“通知标题”字段。
     */
    private String title;
    /**
     * JPushInterface.EXTRA_ALERT
     * 保存服务器推送下来的通知内容。
     * 对应 API 通知内容的 alert 字段。
     * 对应 Portal 推送通知界面上的“通知内容”字段。
     */
    private String content;
    /**
     * JPushInterface.EXTRA_EXTRA
     * SDK 1.2.9 以上版本支持。
     * 保存服务器推送下来的附加字段。这是个 JSON 字符串。
     * 对应 API 通知内容的 extras 字段。
     * 对应 Portal 推送消息界面上的“可选设置”里的附加字段。
     */
    private String extras;
    /**
     * JPushInterface.EXTRA_NOTIFICATION_ID
     * SDK 1.3.5 以上版本支持。
     * 通知栏的Notification ID，可以用于清除Notification
     * 如果服务端内容（alert）字段为空，则notification id 为0
     */
    private int notificationId;
    /**
     * JPushInterface.EXTRA_CONTENT_TYPE
     * 保存服务器推送下来的内容类型。
     * 对应 API 消息内容的 content_type 字段。
     * Portal 上暂时未提供输入字段。
     */
    private String type;
    /**
     * JPushInterface.EXTRA_RICHPUSH_HTML_PATH
     * SDK 1.4.0 以上版本支持。
     * 富媒体通知推送下载的HTML的文件路径,用于展现WebView。
     */
    private String fileHtml;
    /**
     * JPushInterface.EXTRA_RICHPUSH_HTML_RES
     * SDK 1.4.0 以上版本支持。
     * 富媒体通知推送下载的图片资源的文件名,多个文件名用 “，” 分开。 与 “JPushInterface.EXTRA_RICHPUSH_HTML_PATH” 位于同一个路径。
     */
    private String[] fileNames;
    /**
     * JPushInterface.EXTRA_MSG_ID
     * SDK 1.6.1 以上版本支持。
     * 唯一标识通知消息的 ID, 可用于上报统计等。
     */
    private String file;

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getFileHtml() {
        return fileHtml;
    }

    public void setFileHtml(String fileHtml) {
        this.fileHtml = fileHtml;
    }

    public String[] getFileNames() {
        return fileNames;
    }

    public void setFileNames(String[] fileNames) {
        this.fileNames = fileNames;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
