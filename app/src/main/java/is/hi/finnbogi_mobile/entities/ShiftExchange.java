package is.hi.finnbogi_mobile.entities;

import java.io.StringBufferInputStream;

public class ShiftExchange {

    private int mShiftExchangeId;
    private User mEmployee;
    private Shift mShiftForExchange;
    private Shift mCoworkerShift;
    private String mStatus;

    // All Status'
    private static String UFG = "UpForGrabs";
    private static String CSO = "CoworkerShiftOffered";
    private static String EAO = "EmployeeAcceptsOffer";

    /**
     *
     * @param shiftExchangeId int > 0
     * @param employee User
     * @param shiftForExchange Shift
     * @param coworkerShift User / null
     * @param status null / UpForGrabs / CoworkerShiftOffered / EmployeeAcceptsOffer
     */
    public ShiftExchange(int shiftExchangeId, User employee, Shift shiftForExchange, Shift coworkerShift, String status) {
        mShiftExchangeId = shiftExchangeId;
        mEmployee = employee;
        mShiftForExchange = shiftForExchange;
        mCoworkerShift = coworkerShift;
        mStatus = status;
        if (status == null) { mStatus = UFG; }
    }

    /**
     * updates status if accepted status given
     * @param status, UFG = UpForGrabs, CSO = CoworkerShiftOffered OR EAO = EmployeeAcceptsOffer.
     * @return true if status was updated, false else.
     */
    public Boolean updateStatus(String status) {
        switch(status) {
            case "UFG":
                mStatus = UFG;
                return true;
            case "CSO":
                mStatus = CSO;
                return true;
            case "EAO":
                mStatus = EAO;
                return true;
        }
        return false;
    }

    public int getShiftExchangeId() {
        return mShiftExchangeId;
    }

    public User getEmployee() {
        return mEmployee;
    }

    public String getStatus() {
        return mStatus;
    }

    public Shift getShiftForExchange() {
        return mShiftForExchange;
    }

    public void setShiftForExchange(Shift shiftForExchange) {
        mShiftForExchange = shiftForExchange;
    }

    public Shift getCoworkerShift() {
        return mCoworkerShift;
    }

    public void setCoworkerShift(Shift coworkerShift) {
        mCoworkerShift = coworkerShift;
    }
}
