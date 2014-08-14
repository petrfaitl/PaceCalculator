package app.pacecalculator.android.lonerunner.info.pacecalculator;

/**
 * Created by Petr on 2014-08-02.
 */

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.pacecalculator.android.lonerunner.info.pacecalculator.converters.PaceUtil;
import app.pacecalculator.android.lonerunner.info.pacecalculator.converters.Sorter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ResultsFragment extends Fragment implements TimePickerFragment.TimeSetNotifyListener,
        DistancePickerFragment.DistanceSetNotifyListener
{

    private List<Map<String, String>> distanceData = new ArrayList<Map<String, String>>();
    private SimpleAdapter mResultsAdapter;
    private SharedPreferences prefs;
    private Map<String, String> factorDistanceMap = new HashMap<String, String>();
    private String entryTime = "00:00:00";
    private String entryDistance = "0.0";
    private String units = "km";
    private Button timeButton;
    private Button distanceButton;
    private Button paceButton;
    private String TAG = "Fragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        View headerView = inflater.inflate(R.layout.header_buttons, null);
        timeButton = (Button) headerView.findViewById(R.id.timeButton);

        timeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showTimePickerDialog(view);
            }
        });

        distanceButton = (Button)headerView.findViewById(R.id.distanceButton);
        distanceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showDistanceDialog(view);
            }
        });

        //Log.v("onCreateView", "Now");

        if ((savedInstanceState != null))
        {
            entryTime = savedInstanceState.getString("entryTime");
            timeButton.setText(entryTime);
            //Log.v("Retrieving SavedInstance", savedInstanceState.getString("entryTime"));
        }

        paceButton = (Button)headerView.findViewById(R.id.paceButton);
        paceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
        setPace();


        //Log.v("RestoringInstanceState", savedInstanceState.toString());





        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        createDefaultResultsList();
        mResultsAdapter = new SimpleAdapter(getActivity(), distanceData, R.layout.result_item,
                                            new String[]{getResources().getString(R.string.distance), getResources().getString(R.string.estimated_time)},
                                            new int[]{R.id.textView_distance, R.id.textView_time});
        ListView resultsView = (ListView) rootView.findViewById(R.id.results_view);
        resultsView.addHeaderView(headerView);
        resultsView.setAdapter(mResultsAdapter);
        return rootView;
    }




    private void updateDistancePreferences()
    {
        Set<String> defaultDistances = new HashSet<String>(Arrays.asList(getResources().getStringArray(R.array.pref_distances_list_default_values)));
        Set<String> prefDistances = prefs.getStringSet(getString(R.string.pref_distance_key), defaultDistances);
        List<String> prefDistanceList = new ArrayList<String>(prefDistances);
        Map<String, String> prefDistanceFactorMap;
        distanceData.clear();

        Collections.sort(prefDistanceList, new Sorter());
        for (int i = 0; i < prefDistanceList.size(); i++)
        {
            prefDistanceFactorMap = new HashMap<String, String>();
            prefDistanceFactorMap.put(getResources().getString(R.string.distance), factorDistanceMap.get(prefDistanceList.get(i)));
            prefDistanceFactorMap.put(getResources().getString(R.string.estimated_time), estimateTime(prefDistanceList.get(i)));

            distanceData.add(prefDistanceFactorMap);
        }
        mResultsAdapter.notifyDataSetChanged();


    }

    private void createDefaultResultsList()
    {
        String[] factors = getResources().getStringArray(R.array.pref_distances_list_values);
        String[] distances = getResources().getStringArray(R.array.pref_distances_list_titles);
        Map<String, String> defaultDistFactorMap;


        for (int i = 0; i < factors.length; i++)
        {
            defaultDistFactorMap = new HashMap<String, String>();
            defaultDistFactorMap.put(getResources().getString(R.string.distance), distances[i]);
            defaultDistFactorMap.put(getResources().getString(R.string.estimated_time), estimateTime(factors[i]));
            distanceData.add(defaultDistFactorMap);
            factorDistanceMap.put(factors[i], distances[i]);
        }

    }

    private String estimateTime(String factor)
    {
        PaceUtil paceUtil = new PaceUtil(entryTime, entryDistance, units);
        return paceUtil.estimateTime(factor);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        updateDistancePreferences();


    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {

        outState.putString("entryTime", entryTime);
        outState.putString("distance", entryDistance);
        super.onSaveInstanceState(outState);
        //Log.v("SavedInstanceState", outState.toString());

    }


    public void showTimePickerDialog(View view)
    {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.commit();
        TimePickerFragment timePicker = new TimePickerFragment();
        timePicker.setTargetFragment(this, 0);
        timePicker.show(getFragmentManager(), "time picker");

    }

    public void showDistanceDialog(View view)
    {
        DistancePickerFragment distancePicker = new DistancePickerFragment();
        distancePicker.setTargetFragment(this,1);
        distancePicker.show(getFragmentManager(), "distance picker");

    }


    @Override
    public void onComplete(String time)
    {

        timeButton.setText(time);
        entryTime = time;
        if(Double.parseDouble(entryDistance)>0)
        {
            updateDistancePreferences();
            setPace();
        }
    }


    @Override
    public void onDistanceSet(String distance, String units)
    {
        distanceButton.setText(distance + " " + units);
        entryDistance = distance;
        this.units = units;
        if(PaceUtil.isSetTime(entryTime))
        {
            updateDistancePreferences();
            setPace();
        }

    }

    private void setPace()
    {
        Log.v("Set Pace vars", ""+ PaceUtil.isSetTime(entryTime) + Double.parseDouble(entryDistance));
        if(PaceUtil.isSetTime(entryTime) && Double.parseDouble(entryDistance)>0)
        {
            PaceUtil paceUtil = new PaceUtil(entryTime, entryDistance, units);
            paceButton.setText(paceUtil.calculatePace());
        }
    }


}
