package is.hi.finnbogi_mobile.entities;

import com.google.gson.annotations.SerializedName;

public class ShiftExchange {

    @SerializedName("id")
    private int mShiftExchangeId;
    @SerializedName("employeeid")
    private int mEmployeeId;
    @SerializedName("shiftforexchangeid")
    private int mShiftForExchangeId;
    @SerializedName("coworkershiftid")
    private int mCoworkerShiftId;
    @SerializedName("status")
    private String mStatus;

    // All Status'
    private static String UFG = "upforgrabs";
    private static String PENDING = "pending";
    private static String CONFIRMABLE = "confirmable";

    public ShiftExchange(int shiftExchangeId, int employeeId, int shiftForExchangeId, int coworkerShiftId, String status) {
        mShiftExchangeId = shiftExchangeId;
        mEmployeeId = employeeId;
        mShiftForExchangeId = shiftForExchangeId;
        mCoworkerShiftId = coworkerShiftId;
        mStatus = status;
        if (status == null) {
            mStatus = UFG;
        }
    }

    /**
     * Updates status if accepted status is given.
     *
     * @param status UFG = UpForGrabs, CSO = CoworkerShiftOffered OR EAO = EmployeeAcceptsOffer.
     * @return true if status was updated, false else.
     */
    public boolean updateStatus(String status) {
        switch(status) {
            case "UFG":
                mStatus = UFG;
                return true;
            case "PENDING":
                mStatus = PENDING;
                return true;
            case "CONFIRMABLE":
                mStatus = CONFIRMABLE;
                return true;
        }
        return false;
    }

    public int getShiftExchangeId() {
        return mShiftExchangeId;
    }

    public int getEmployeeId() {
        return mEmployeeId;
    }

    public int getShiftForExchangeId() {
        return mShiftForExchangeId;
    }

    public int getCoworkerShiftId() {
        return mCoworkerShiftId;
    }

    public String getStatus() {
        return mStatus;
    }
}
