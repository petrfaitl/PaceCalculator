package app.pacecalculator.android.lonerunner.info.pacecalculator.converters;

import android.content.Context;

import app.pacecalculator.android.lonerunner.info.pacecalculator.R;

/**
 * Created by Petr on 2014-08-01.
 */


public class PaceUtil
{

    private long timeInMilisec;
    private long distanceInMetres;
    private long calculatedTime;
    private static final long HOUR_IN_MILS = 3600000l;
    private static final long MINUTE_IN_MILS = 60000l;
    private static final long SECOND_IN_MILS = 1000l;
    private static final long MILES_TO_METRES_FACTOR = 1609l;
    private static final long KM_TO_METRES_FACTOR = 1000l;
    private String units;


    public PaceUtil(Context context, String time, String distance, String distanceUnits)
    {
        this.timeInMilisec = getTimeInMiliseconds(time);
        this.units = distanceUnits;
        if(distanceUnits.contains(context.getString(R.string.mile)))
        {
            this.distanceInMetres = convertToLongFormat(distance,MILES_TO_METRES_FACTOR) ;
        }else
        {
            this.distanceInMetres = convertToLongFormat(distance,KM_TO_METRES_FACTOR) ;
        }

    }

    private long convertToLongFormat(String number, long multiplier)
    {
        double decimalNumber = Double.parseDouble(number);
        long longNumber = (long)(decimalNumber * multiplier);
        return longNumber;
    }

    private static long getTimeInMiliseconds(String time)
    {
        String[] timeArray = time.split(":");
        return Long.valueOf(timeArray[0]) * HOUR_IN_MILS
                + Long.valueOf(timeArray[1]) * MINUTE_IN_MILS
                + Long.valueOf(timeArray[2]) * SECOND_IN_MILS;

    }

    public static boolean isSetTime(String time)
    {
        if(getTimeInMiliseconds(time)>0)
        {
            return true;
        }
        return false;
    }

    public long getCalculatedTime()
    {
        return calculatedTime;
    }


    public String paceToString(long inputTime)
    {
        int hours = ((int) (inputTime / HOUR_IN_MILS));
        long remainder = (inputTime % HOUR_IN_MILS);
        int minutes = (int) (remainder / MINUTE_IN_MILS);
        remainder = remainder % MINUTE_IN_MILS;
        int seconds = (Math.round(remainder / SECOND_IN_MILS));
        if (hours > 0)
        {
            return String.format("%02d:", hours) + String.format("%02d:", minutes) + String.format("%02d", seconds) + "/" + units;
        }
        return String.format("%02d:", minutes) + String.format("%02d", seconds) + "/" + units;
    }

    public String toString(long inputTime)
    {
        int hours = ((int) (inputTime / HOUR_IN_MILS));
        long remainder = (inputTime % HOUR_IN_MILS);
        int minutes = (int) (remainder / MINUTE_IN_MILS);
        remainder = remainder % MINUTE_IN_MILS;
        int seconds = (Math.round(remainder / SECOND_IN_MILS));

        return String.format("%02d:", hours) + String.format("%02d:", minutes) + String.format("%02d", seconds);
    }

    public String estimateTime(String factor)
    {
        if(distanceInMetres > 0)
        {
            this.calculatedTime = timeInMilisec / distanceInMetres * Long.parseLong(factor);
        }else
        {
            calculatedTime = 0;
        }

        return toString(calculatedTime);

    }

    public String calculatePace()
    {
        if(units.equals("km"))
        {
            this.calculatedTime = timeInMilisec / distanceInMetres * KM_TO_METRES_FACTOR;
        }else
        {
            this.calculatedTime = timeInMilisec / distanceInMetres * MILES_TO_METRES_FACTOR;
        }

        return paceToString(calculatedTime);

    }


}
