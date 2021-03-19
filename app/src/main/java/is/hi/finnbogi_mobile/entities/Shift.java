package is.hi.finnbogi_mobile.entities;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Shift {

    @SerializedName("id")
    private int mShiftId;
    @SerializedName("startTime")
    private LocalDateTime mStartTime;
    @SerializedName("endTime")
    private LocalDateTime mEndTime;
    @SerializedName("userId")
    private int mUserId;
    //private String mRole;

    /**
     *
     * @param shiftId int > 0
     * @param startTime LocalDateTime
     * @param endTime LocalDateTime
     * @param userId UserId
     * //@param role String TODO: maybe enum
     */
    public Shift(int shiftId, LocalDateTime startTime, LocalDateTime endTime, int userId) {
        mShiftId = shiftId;
        mStartTime = startTime;
        mEndTime = endTime;
        mUserId = userId;
        //mRole = role;
    }

    public int getShiftId() {
        return mShiftId;
    }

    public LocalDateTime getStartTime() {
        return mStartTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        mStartTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return mEndTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        mEndTime = endTime;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUser(int userId) {
        mUserId = userId;
    }

    /*
    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }
     */
}
