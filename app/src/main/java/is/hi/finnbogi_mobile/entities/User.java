package is.hi.finnbogi_mobile.entities;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private int mUserId;
    @SerializedName("username")
    private String mUserName;
    @SerializedName("role")
    private String mRole;
    @SerializedName("admin")
    private Boolean mAdmin;
    @SerializedName("password")
    private String mPassword;

    private int mUserInfoId = -1;

    public User(int userId, String userName, String role, Boolean admin, String password) {
        mUserId = userId;
        mUserName = userName;
        mRole = role;
        mAdmin = admin;
        mPassword = password;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public int getUserInfoId() {
        return mUserInfoId;
    }

    public void setUserInfoId(int userInfoId) {
        mUserInfoId = userInfoId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }

    public Boolean getAdmin() {
        return mAdmin;
    }

    public void setAdmin(Boolean admin) {
        mAdmin = admin;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
}
