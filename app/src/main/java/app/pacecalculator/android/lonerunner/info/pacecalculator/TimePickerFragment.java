package app.pacecalculator.android.lonerunner.info.pacecalculator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Petr on 2014-08-10.
 */
public class TimePickerFragment extends
        DialogFragment
{
    private NumberPicker hours;
    private NumberPicker minutes;
    private NumberPicker seconds;
    private String inputTime;
    private List<Integer> reinstatedTime;


    public interface TimeSetNotifyListener
    {
        public void onTimeSet(String time);

    }

    private TimeSetNotifyListener timeSetNotifyListener;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        processEntryTime();

        try
        {
            timeSetNotifyListener = (TimeSetNotifyListener) getTargetFragment();
        } catch (ClassCastException e)
        {
            throw new ClassCastException("Calling Fragment must implement TimeSetNotifyListener");
        }
    }

    private void processEntryTime()
    {
        Bundle bundle = getArguments();
        inputTime = bundle.getString("time");
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(inputTime);
        reinstatedTime = new ArrayList<Integer>();
        while (matcher.find())
        {
            reinstatedTime.add(Integer.parseInt(matcher.group()));
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View timeLayout = inflater.inflate(R.layout.time_dialog, null);
        hours = (NumberPicker) timeLayout.findViewById(R.id.hourPicker);

        hours.setMinValue(0);
        hours.setMaxValue(23);
        hours.setValue(reinstatedTime.get(0));


        minutes = (NumberPicker) timeLayout.findViewById(R.id.minutePicker);
        minutes.setMinValue(0);
        minutes.setMaxValue(59);
        minutes.setValue(reinstatedTime.get(1));


        seconds = (NumberPicker) timeLayout.findViewById(R.id.secondsPicker);
        seconds.setMinValue(0);
        seconds.setMaxValue(59);
        seconds.setValue(reinstatedTime.get(2));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(timeLayout);
        builder.setTitle(R.string.set_time_title);
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                StringBuilder time = new StringBuilder().append(String.format("%02d", hours.getValue())).append(":").append(String.format("%02d", minutes.getValue())).append(":").append(String.format("%02d", seconds.getValue()));
                timeSetNotifyListener.onTimeSet(time.toString());

            }
        });
        return builder.create();
    }


}