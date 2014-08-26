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
 * Created by Petr on 2014-08-14.
 */
public class PacePickerFragment extends DialogFragment
{
    private NumberPicker units;
    private NumberPicker minutes;
    private NumberPicker seconds;
    private String inputTime;
    private List<String> reinstatedPace;



    public interface PaceSetNotifyListener
    {
        public void onPaceSet(String time);

    }

    private PaceSetNotifyListener paceSetNotifyListener;




    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        processEntryPace();

        try
        {
            paceSetNotifyListener = (PaceSetNotifyListener)getTargetFragment();
        }catch (ClassCastException e)
        {
            throw new ClassCastException("Calling Fragment must implement TimeSetNotifyListener");
        }
    }

    private void processEntryPace()
    {
        Bundle bundle = getArguments();
        inputTime = bundle.getString("pace");
        Pattern pattern = Pattern.compile("[0-9]+|[a-z]+");
        Matcher matcher = pattern.matcher(inputTime);
        reinstatedPace = new ArrayList<String>();
        while(matcher.find())
        {
            reinstatedPace.add((matcher.group()));
        }
        if(reinstatedPace.size()== 4)
        {
            reinstatedPace.remove(0);
        }

       // Log.v("disected pace", reinstatedPace.toString());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View paceLayout = inflater.inflate(R.layout.pace_dialog, null);
        minutes = (NumberPicker)paceLayout.findViewById(R.id.minutePicker);
        minutes.setMinValue(0);
        minutes.setMaxValue(59);
        if(reinstatedPace != null)
        {
            minutes.setValue(Integer.parseInt(reinstatedPace.get(0)));
        }

        seconds = (NumberPicker)paceLayout.findViewById(R.id.secondsPicker);
        seconds.setMinValue(0);
        seconds.setMaxValue(59);
        if(reinstatedPace != null)
        {
            seconds.setValue(Integer.parseInt(reinstatedPace.get(1)));
        }

        units = (NumberPicker)paceLayout.findViewById(R.id.unitsPicker);
        final String[] unitsValues = {"/km","/mile"};
        units.setDisplayedValues(unitsValues);
        units.setMinValue(0);
        units.setMaxValue(unitsValues.length-1);
        if(reinstatedPace.get(2).contains("km"))
        {
            units.setValue(0);
        }else
        {
            units.setValue(1);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(paceLayout);
        builder.setTitle(R.string.set_time_title);
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                StringBuilder time = new StringBuilder().append(String.format("%02d",minutes.getValue())).append(":").append(String.format("%02d",seconds.getValue())).append(unitsValues[units.getValue()]);
                paceSetNotifyListener.onPaceSet(time.toString());

            }
        });
        return builder.create();
    }


}
