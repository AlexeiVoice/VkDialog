package com.pd.a2.vkdialog.model;

public class MailItem {
    private String userPicURL;
    private String userName;
    private String messageBody;
    private long dateTime;

    public MailItem(String userPicURL, String userName, String messageBody, long dateTime) {
        this.userPicURL = userPicURL;
        this.userName = userName;
        this.messageBody = messageBody;
        this.dateTime = dateTime;
    }
    public MailItem() {
        this.userPicURL = "";
        this.userName = "";
        this.messageBody = "";
        this.dateTime = 0;
    }

    //region setters and getters
    public String getUserPicURL() {
        return userPicURL;
    }

    public void setUserPicURL(String userPicURL) {
        this.userPicURL = userPicURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
    //endregion
}
