package is.hi.finnbogi_mobile.entities;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class UserInfo {

    @SerializedName("id")
    private int mUserInfoId;
    @SerializedName("firstName")
    private String mFirstName;
    @SerializedName("surName")
    private String mSurName;
    @SerializedName("address")
    private String mAddress;
    @SerializedName("phoneNumber")
    private String mPhoneNumber;
    @SerializedName("ssn")
    private String mSSN;
    @SerializedName("startDate")
    private LocalDate mStartDate;
    @SerializedName("email")
    private String mEmail;

    /**
     *
     * @param userInfoId int > 0
     * @param firstName String (Non empty)
     * @param surName String
     * @param address String
     * @param phoneNumber String (len >= 7)
     * @param SSN String
     * @param startDate LocalDate
     * @param email String ( of format = *@*.* )
     */
    public UserInfo(int userInfoId, String firstName, String surName, String address, String phoneNumber, String SSN, LocalDate startDate, String email) {
        mUserInfoId = userInfoId;
        mFirstName = firstName;
        mSurName = surName;
        mAddress = address;
        mPhoneNumber = phoneNumber;
        mSSN = SSN;
        mStartDate = startDate;
        mEmail = email;
    }

    public int getUserInfoId() {
        return mUserInfoId;
    }

    public String getSSN() {
        return mSSN;
    }

    public LocalDate getStartDate() {
        return mStartDate;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getSurName() {
        return mSurName;
    }

    public void setSurName(String surName) {
        mSurName = surName;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }
}
