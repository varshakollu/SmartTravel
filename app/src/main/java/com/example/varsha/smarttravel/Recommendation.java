package com.example.varsha.smarttravel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */

public class Recommendation extends Fragment{

    public Recommendation() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_recommendation, container, false);

        Intent intent=new Intent(getActivity(),s3file.class);
        view.getContext().startActivity(intent);

        return view;
    }
}

