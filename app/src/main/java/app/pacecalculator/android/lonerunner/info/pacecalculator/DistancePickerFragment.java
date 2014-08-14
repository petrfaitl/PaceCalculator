package app.pacecalculator.android.lonerunner.info.pacecalculator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by Petr on 2014-08-13.
 */
public class DistancePickerFragment extends DialogFragment
{

    private NumberPicker kms;
    private NumberPicker meters;
    private NumberPicker units;
    private static final String[] meterUnits = {".1", ".2", ".3",".4",".5",".6",".7",".8",".9"};
    private static final String[] unitsValues = {"km", "mile"};

    public interface DistanceSetNotifyListener
    {
        public void onDistanceSet(String distance, String units);
    }

    private DistanceSetNotifyListener distanceSetNotifyListener;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try
        {
            distanceSetNotifyListener = (DistanceSetNotifyListener)getTargetFragment();
        }catch (ClassCastException e)
        {
            throw new ClassCastException("Calling Fragment must implement DistanceSetNotifyListener");
        }


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View pickersLayout = inflater.inflate(R.layout.fragment_distance,null);
        kms = (NumberPicker)pickersLayout.findViewById(R.id.numberPickerKilometers);
        kms.setMinValue(0);
        kms.setMaxValue(100);
        kms.setWrapSelectorWheel(false);
        if(savedInstanceState != null)
        {
            kms.setValue(savedInstanceState.getInt("kmsValue"));
            Log.v("Restoring Instance", "Now");
        }

        meters = (NumberPicker)pickersLayout.findViewById(R.id.numberPickerMeters);
        meters.setMaxValue(meterUnits.length-1);
        meters.setMinValue(0);
        meters.setDisplayedValues(meterUnits);
        meters.computeScroll();

        units = (NumberPicker)pickersLayout.findViewById(R.id.units);
        units.setMaxValue(unitsValues.length-1);
        units.setMinValue(0);
        units.setDisplayedValues(unitsValues);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Distance");
        builder.setView(pickersLayout);

        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                StringBuilder distance = new StringBuilder().append(kms.getValue()).append(meterUnits[meters.getValue()]);

                distanceSetNotifyListener.onDistanceSet(distance.toString(), unitsValues[units.getValue()]);


            }
        });


        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {

        outState.putInt("kmsValue", kms.getValue());
        outState.putString("meterValue",meterUnits[meters.getValue()] );
        outState.putString("unitsValue", unitsValues[units.getValue()]);
        //Log.v("SavedInstanceState", outState.toString());
        super.onSaveInstanceState(outState);

    }


}
