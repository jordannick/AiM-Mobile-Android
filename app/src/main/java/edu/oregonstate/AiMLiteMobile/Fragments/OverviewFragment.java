package edu.oregonstate.AiMLiteMobile.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.oregonstate.AiMLiteMobile.Activities.OverviewListActivity;
import edu.oregonstate.AiMLiteMobile.R;

/**
 * Created by SellersK on 6/1/2015.
 */
public class OverviewFragment extends Fragment {
    private static final String TAG = "OverviewFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.overview_landing, container, false);

        v.findViewById(R.id.button_scrollToDaily).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "pressed the button");
                ((OverviewListActivity)getActivity()).scrollToPosition(8);
            }
        });

        return v;
    }
}
