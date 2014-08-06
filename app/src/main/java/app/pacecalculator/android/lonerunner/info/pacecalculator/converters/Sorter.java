package app.pacecalculator.android.lonerunner.info.pacecalculator.converters;

import java.util.Comparator;

/**
 * Created by Petr on 2014-08-05.
 */
public class Sorter implements Comparator<Object>
{

    @Override
    public int compare(Object o, Object o2)
    {
        Double one = Double.parseDouble(o.toString());
        Double two = Double.parseDouble(o2.toString());
        if (one < two)
        {
            return -1;
        } else if (one > two)
        {
            return 1;
        } else
        {
            return 0;
        }


    }
}
