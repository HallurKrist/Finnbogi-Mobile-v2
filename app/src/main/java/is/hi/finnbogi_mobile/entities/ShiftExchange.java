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

    public ShiftExchange(int shiftExchangeId, int employeeId, int shiftForExchangeId, int coworkerShiftId, String status) {
        mShiftExchangeId = shiftExchangeId;
        mEmployeeId = employeeId;
        mShiftForExchangeId = shiftForExchangeId;
        mCoworkerShiftId = coworkerShiftId;
        mStatus = status;
        if (status == null) {
            mStatus = "upforgrabs";
        }
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
