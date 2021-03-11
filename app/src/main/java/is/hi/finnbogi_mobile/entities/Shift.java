package is.hi.finnbogi_mobile.entities;

import java.time.LocalDateTime;

public class Shift {
    private int mShiftId;
    private LocalDateTime mStartTime;
    private LocalDateTime mEndTime;
    private User mUser;
    private String mRole;

    /**
     *
     * @param shiftId int > 0
     * @param startTime LocalDateTime
     * @param endTime LocalDateTime
     * @param user User
     * @param role String TODO: maybe enum
     */
    public Shift(int shiftId, LocalDateTime startTime, LocalDateTime endTime, User user, String role) {
        mShiftId = shiftId;
        mStartTime = startTime;
        mEndTime = endTime;
        mUser = user;
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

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }
}
