package app.pacecalculator.android.lonerunner.info.pacecalculator.converters;

/**
 * Created by Petr on 2014-08-01.
 */


public class TimeUtil
{

    private long timeInMilisec;
    private long calculatedTime;
    private static final long HOUR_IN_MILS = 3600000L;
    private static final long MINUTE_IN_MILS = 60000L;
    private static final long SECOND_IN_MILS = 1000L;


    public TimeUtil(String time)
    {
        this.timeInMilisec = getTimeInMiliseconds(time);
    }

    private long getTimeInMiliseconds(String time)
    {
        String[] timeArray = time.split(":");
        return Long.valueOf(timeArray[0]) * HOUR_IN_MILS
                + Long.valueOf(timeArray[1]) * MINUTE_IN_MILS
                + Long.valueOf(timeArray[2]) * SECOND_IN_MILS;

    }

    public long getCalculatedTime()
    {
        return calculatedTime;
    }

    @Override
    public String toString()
    {
        int hours = (int) (timeInMilisec / HOUR_IN_MILS);
        long remainder = (timeInMilisec % HOUR_IN_MILS);
        int minutes = (int) (remainder / MINUTE_IN_MILS);
        remainder = remainder % MINUTE_IN_MILS;
        int seconds = (int) (remainder / SECOND_IN_MILS);
        remainder = remainder % SECOND_IN_MILS;
        return String.format("%02d:", hours) + String.format("%02d:", minutes) + String.format("%02d", seconds);
    }

    public String toString(long inputTime)
    {
        int hours = ((int) (inputTime / HOUR_IN_MILS));
        long remainder = (inputTime % HOUR_IN_MILS);
        int minutes = (int) (remainder / MINUTE_IN_MILS);
        remainder = remainder % MINUTE_IN_MILS;
        int seconds = (int) (remainder / SECOND_IN_MILS);
        remainder = remainder % SECOND_IN_MILS;
        return String.format("%02d:", hours) + String.format("%02d:", minutes) + String.format("%02d", seconds);
    }

    public long multiplyTime(Double factor)
    {
        long longFactor = (long) (factor * 1000);
        this.calculatedTime = (timeInMilisec * longFactor) / 1000;
        return calculatedTime;
    }

    public long addTime(String addedTime)
    {
        long addedTimeInMiliseconds = getTimeInMiliseconds(addedTime);
        this.calculatedTime = timeInMilisec + addedTimeInMiliseconds;
        return calculatedTime;
    }


}
