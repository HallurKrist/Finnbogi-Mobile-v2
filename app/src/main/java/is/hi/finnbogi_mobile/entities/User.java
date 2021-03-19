package is.hi.finnbogi_mobile.entities;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private int mUserId;
    @SerializedName("userName")
    private String mUserName;
    @SerializedName("role")
    private String mRole;

    //private UserInfo mUserInfo;

    //private Shift[] mShifts;
    @SerializedName("password")
    private String mPassword;

    //private Notification[] mNotifications;
    @SerializedName("admin")
    private Boolean mAdmin;

    /**
     *
     * @param userId int > 0
     * @param userName String (Non empty)
     * @param role String (Non empty)
     * @param userInfo UserInfo / null
     * @param shifts Shift[]
     * @param password String
     * @param notifications Notification[]
     */
    public User(int userId,
                String userName,
                String role,
                //UserInfo userInfo,
                //Shift[] shifts,
                String password,
                //Notification[] notifications,
                Boolean admin) {
        mUserId = userId;
        mUserName = userName;
        mRole = role;
        //mUserInfo = userInfo;
        //mShifts = shifts;
        mPassword = password;
        //mNotifications = notifications;
        mAdmin = admin;
    }

    public int getUserId() {
        return mUserId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getRoles() {
        return mRole;
    }

    public void setRoles(String role) {
        mRole = role;
    }

    /*
    public UserInfo getUserInfo() {
        return mUserInfo;
    }

     */

    /*
    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }
     */

    /*
    public Shift[] getShifts() {
        return mShifts;
    }
     */

    /*
    public void setShifts(Shift[] shifts) {
        mShifts = shifts;
    }
     */

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    /*
    public Notification[] getNotifications() {
        return mNotifications;
    }
     */

    /*
    public void setNotifications(Notification[] notifications) {
        mNotifications = notifications;
    }
     */

    public Boolean getAdmin() {
        return mAdmin;
    }
}
