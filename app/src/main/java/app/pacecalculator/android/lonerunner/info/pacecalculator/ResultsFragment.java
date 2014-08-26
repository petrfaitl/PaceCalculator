package app.pacecalculator.android.lonerunner.info.pacecalculator;

/**
 * Created by Petr on 2014-08-02.
 */

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.pacecalculator.android.lonerunner.info.pacecalculator.converters.PaceUtil;
import app.pacecalculator.android.lonerunner.info.pacecalculator.converters.Sorter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ResultsFragment extends Fragment implements TimePickerFragment.TimeSetNotifyListener,
        DistancePickerFragment.DistanceSetNotifyListener, PacePickerFragment.PaceSetNotifyListener
{

    private List<Map<String, String>> distanceData = new ArrayList<Map<String, String>>();
    private SimpleAdapter mResultsAdapter;
    private SharedPreferences prefs;
    private Map<String, String> factorDistanceMap = new HashMap<String, String>();
    private String entryTime = "00:00:00";
    private String entryDistance = "0.0";
    private String units = "km";
    private String entryDistanceUnits = entryDistance + " "+units;
    private Button timeButton;
    private Button distanceButton;
    private Button paceButton;
    private String pace = "00:00/km";


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

        distanceButton = (Button) headerView.findViewById(R.id.distanceButton);
        distanceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showDistanceDialog(view);
            }
        });





        paceButton = (Button) headerView.findViewById(R.id.paceButton);
        paceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showPaceDialog(view);

            }
        });
        setPace();





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




    public void showTimePickerDialog(View view)
    {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.time), entryTime);
        TimePickerFragment timePicker = new TimePickerFragment();
        timePicker.setArguments(bundle);
        timePicker.setTargetFragment(this, 0);
        timePicker.show(getFragmentManager(), "time picker");

    }

    public void showDistanceDialog(View view)
    {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.distance), entryDistanceUnits);
        DistancePickerFragment distancePicker = new DistancePickerFragment();
        distancePicker.setArguments(bundle);
        distancePicker.setTargetFragment(this, 1);
        distancePicker.show(getFragmentManager(), "distance picker");

    }

    public void showPaceDialog(View view)
    {
        Bundle bundle = new Bundle();
        if (pace != null)
        {
            bundle.putString(getString(R.string.pace), pace);
        }

        PacePickerFragment pacePicker = new PacePickerFragment();
        pacePicker.setTargetFragment(this, 1);
        pacePicker.setArguments(bundle);
        pacePicker.show(getFragmentManager(), "pace picker");

    }


    @Override
    public void onPaceSet(String pace)
    {
        paceButton.setText(pace);
        this.pace = pace;
        Pattern pattern = Pattern.compile("[0-9]+|[a-z]+");
        Matcher matcher = pattern.matcher(pace);
        List<String> paceArray = new ArrayList<String>();
        while (matcher.find())
        {
            paceArray.add((matcher.group()));
        }
        if (paceArray.get(2).contains(getString(R.string.km)))
        {
            units = getString(R.string.km);
        } else
        {
            units = getString(R.string.mile);
        }
        entryDistance = "1.0";
        StringBuilder sb = new StringBuilder().append("00:").append(paceArray.get(0)).append(":").append(paceArray.get(1));
        String time = sb.toString();
        setDistanceButtonText(entryDistance, units);
        setTimeButtonText(time);
        updateDistancePreferences();


    }

    private void setTimeButtonText(String time)
    {
        entryTime = time;
        timeButton.setText(entryTime);

    }


    @Override
    public void onDistanceSet(String distance, String units)
    {
        setDistanceButtonText(distance, units);

        this.units = units;
        if (PaceUtil.isSetTime(entryTime))
        {
            updateDistancePreferences();
            setPace();
        }

    }

    private void setDistanceButtonText(String distance, String units)
    {

        entryDistance = distance;
        entryDistanceUnits = distance + " "+units;
        distanceButton.setText(entryDistanceUnits);
    }

    private void setPace()
    {

        if (PaceUtil.isSetTime(entryTime) && Double.parseDouble(entryDistance) > 0)
        {
            PaceUtil paceUtil = new PaceUtil(entryTime, entryDistance, units);
            pace = paceUtil.calculatePace();
            paceButton.setText(pace);
        }
    }


    @Override
    public void onTimeSet(String time)
    {
        setTimeButtonText(time);
        if (Double.parseDouble(entryDistance) > 0)
        {
            updateDistancePreferences();
            setPace();
        }

    }
}
