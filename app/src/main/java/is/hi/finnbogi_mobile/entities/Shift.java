package is.hi.finnbogi_mobile.entities;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Shift {

    @SerializedName("id")
    private int mShiftId;
    @SerializedName("starttime")
    private LocalDateTime mStartTime;
    @SerializedName("endtime")
    private LocalDateTime mEndTime;
    @SerializedName("userid")
    private int mUserId;
    @SerializedName("role")
    private String mRole;

    /**
     *
     * @param shiftId int > 0
     * @param startTime LocalDateTime
     * @param endTime LocalDateTime
     * @param userId UserId
     * @param role String
     */
    public Shift(int shiftId, LocalDateTime startTime, LocalDateTime endTime, int userId, String role) {
        mShiftId = shiftId;
        mStartTime = startTime;
        mEndTime = endTime;
        mUserId = userId;
        mRole = role;
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

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }
}
