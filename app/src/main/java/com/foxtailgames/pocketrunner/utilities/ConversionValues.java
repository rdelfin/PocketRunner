package com.foxtailgames.pocketrunner.utilities;

import android.content.Context;

import com.foxtailgames.pocketrunner.R;

/**
 * Created by rdelfin on 12/31/14.
 */
public class ConversionValues {

    private Context context;

    // NEEDS TO BE CHANGED IF NEW UNITS ARE TO BE ADDED
    // TODO: Migrate to a strings.xml file or similar
    private static double[][] conversionVals = {
            // m       ft     km           mi
            { 1,      3.281, 0.001,     0.0006214 }, // m
            { 0.3048, 1,     0.0003048, 0.0001894 }, //ft
            { 1000,   3281,  1,         0.6214    }, //km
            { 1609,   5280,  1.609,     1         }  //mi
    };


    private String[] getUnits() { return context.getResources().getStringArray(R.array.units_list_vals); }

    public ConversionValues(Context context) {
        this.context = context;
    }

    /**
     * Method used to convert a value between two units. Will use string resources to get the unit
     * names.
     * @param val Value to convert
     * @param from Units to convert from. Units val is expressed in
     * @param to Units to convert val to
     * @return The converted value expressed in the units of to
     * @throws UnitDoesNotExistException Exception thrown when the unit suplied through either from
     * ro to
     */
    public double convert(double val, String from, String to) throws UnitDoesNotExistException {
        String[] units = getUnits();
        int fromIndex = -1, toIndex = -1;

        //Searc for form and to strings. If the units do not exist, throw an exception
        for(int i = 0; i < units.length; i++) {
            if(units[i].equals(from))
                fromIndex = i;
            if(units[i].equals(to))
                toIndex = i;
        }

        if(toIndex == -1 && fromIndex != -1)
            throw new UnitDoesNotExistException(to, null);
        if(fromIndex == -1 && toIndex != -1)
            throw new UnitDoesNotExistException(from, null);
        if(fromIndex == -1 && toIndex == -1)
            throw new UnitDoesNotExistException(from, to);

        return val * conversionVals[fromIndex][toIndex];
    }
}
