package com.foxtailgames.pocketrunner.utilities;

/**
 * Created by rdelfin on 12/31/14.
 */
public class UnitDoesNotExistException extends Exception {
    public UnitDoesNotExistException(String unita, String unitb) {
        // WHY CAN YOU NOT CALL SUPER CONSTRUCTOR AFTER DOING SOMETHING, I'M ALREADY TECHNICALLY
        // DOING THAT GOD DAMMIT!!!! (note on why this is ugly as fuck)
        super("Unit" + (unitb == null ? "" : "s") + " " +
                "\'" + unita + "\'" +
                (unitb == null ? " and \'" + unitb + "\'" : "") +
                " do" + (unitb == null ? "es" : "") +
                " not exist. Throwing exception.");
    }

    public UnitDoesNotExistException() {
        super("Unit used does not exist. Throwing exception.");
    }
}
