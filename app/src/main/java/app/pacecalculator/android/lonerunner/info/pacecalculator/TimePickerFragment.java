package app.pacecalculator.android.lonerunner.info.pacecalculator;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;

/**
 * Created by Petr on 2014-08-10.
 */
public class TimePickerFragment extends
        DialogFragment implements TimePickerDialog.OnTimeSetListener
{
    private Button timeButton;



    public interface TimeSetNotifyListener
    {
        public void onComplete(String time);

    }

    private TimeSetNotifyListener timeSetNotifyListener;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            timeSetNotifyListener = (TimeSetNotifyListener)getTargetFragment();
        }catch (ClassCastException e)
        {
            throw new ClassCastException("Calling Fragment must implement TimeSetNotifyListener");
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        int hour = 0;
        int minute = 10;
        return new TimePickerDialog(getActivity(), this,hour, minute, true);

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute)
    {
        StringBuilder time = new StringBuilder().append(hour).append(":").append(String.format("%02d",minute)).append(":00");
        this.timeSetNotifyListener.onComplete(time.toString());
    }


}