package com.ryanmearkle.dev.gathr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class ViewFragment extends Fragment {


    public ViewFragment() {
        // Required empty public constructor
    }

    public abstract String getFriendlyName();

    public abstract String getTagName();
}
