package is.hi.finnbogi_mobile.entities;

import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("id")
    private int mNotificationId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("text")
    private String mText;
    @SerializedName("read")
    private Boolean mRead;

    /**
     *
     * @param notificationId int > 0
     * @param title String
     * @param text String
     * @param read Boolean
     */
    public Notification(int notificationId, String title, String text, Boolean read) {
        mNotificationId = notificationId;
        mTitle = title;
        mText = text;
        mRead = read;
    }

    public int getNotificationId() {
        return mNotificationId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Boolean getRead() {
        return mRead;
    }

    public void setRead(Boolean read) {
        mRead = read;
    }
}
