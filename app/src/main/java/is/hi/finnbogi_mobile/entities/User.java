package is.hi.finnbogi_mobile.entities;

public class User {

    private int mUserId;
    private String mUserName;
    private String[] mRoles;
    private UserInfo mUserInfo;
    private Shift[] mShifts;
    private String mPassword;
    private Notification[] mNotifications;
    private Boolean mAdmin;

    /**
     *
     * @param userId int > 0
     * @param userName String (Non empty)
     * @param roles String (Non empty)
     * @param userInfo UserInfo / null
     * @param shifts Shift[]
     * @param password String
     * @param notifications Notification[]
     */
    public User(int userId,
                String userName,
                String[] roles,
                UserInfo userInfo,
                Shift[] shifts,
                String password,
                Notification[] notifications,
                Boolean admin) {
        mUserId = userId;
        mUserName = userName;
        mRoles = roles;
        mUserInfo = userInfo;
        mShifts = shifts;
        mPassword = password;
        mNotifications = notifications;
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

    public String[] getRoles() {
        return mRoles;
    }

    public void setRoles(String[] roles) {
        mRoles = roles;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public Shift[] getShifts() {
        return mShifts;
    }

    public void setShifts(Shift[] shifts) {
        mShifts = shifts;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public Notification[] getNotifications() {
        return mNotifications;
    }

    public void setNotifications(Notification[] notifications) {
        mNotifications = notifications;
    }

    public Boolean getAdmin() {
        return mAdmin;
    }
}
