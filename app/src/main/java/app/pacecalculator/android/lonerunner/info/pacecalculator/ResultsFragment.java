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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.pacecalculator.android.lonerunner.info.pacecalculator.converters.Sorter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ResultsFragment extends Fragment
{
    private Map<String, String> distanceFactorMap = new HashMap<String, String>();
    private ArrayAdapter mResultsAdapter;
    private SharedPreferences prefs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        createDistanceMap();


        mResultsAdapter = new ArrayAdapter(getActivity(), R.layout.result_item, R.id.textView_distance, new ArrayList<String>());


        ListView resultsView = (ListView) rootView.findViewById(R.id.results_view);
        resultsView.setAdapter(mResultsAdapter);


        return rootView;
    }


    private void updateDistancePreferences()
    {
        Set<String> defaultDistances = new HashSet<String>(Arrays.asList(getResources().getStringArray(R.array.pref_distances_list_default_values)));
        Set<String> prefDistances = prefs.getStringSet(getString(R.string.pref_distance_key), defaultDistances);

        List<String> prefDistanceList = new ArrayList<String>(prefDistances);
        List<String> resultDistances = new ArrayList();

        Collections.sort(prefDistanceList, new Sorter());
        for (String prefDistance : prefDistanceList)
        {
            resultDistances.add(distanceFactorMap.get(prefDistance));
        }
        mResultsAdapter.clear();
        mResultsAdapter.addAll(resultDistances);


    }

    private void createDistanceMap()
    {
        String[] factors = getResources().getStringArray(R.array.pref_distances_list_values);
        String[] distances = getResources().getStringArray(R.array.pref_distances_list_titles);

        for (int i = 0; i < factors.length; i++)
        {
            distanceFactorMap.put(factors[i], distances[i]);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        updateDistancePreferences();


    }


}
