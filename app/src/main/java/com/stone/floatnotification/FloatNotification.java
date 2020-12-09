package com.stone.floatnotification;

import cn.bmob.v3.BmobObject;

public class FloatNotification extends BmobObject {
    private String title;
    private String imageUrl;
    private String appName;
    private String jumpUrl;
    private String downloadUrl;
    private long delayTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    @Override
    public String toString() {
        return "NotificationBean{" +
                "title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", appName='" + appName + '\'' +
                ", jumpUrl='" + jumpUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", delayTime=" + delayTime +
                '}';
    }
}
