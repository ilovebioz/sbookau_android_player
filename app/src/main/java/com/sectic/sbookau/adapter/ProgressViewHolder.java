package com.sectic.sbookau.adapter;

/**
 * Created by bioz on 7/26/2017.
*/

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.sectic.sbookau.R;

public class ProgressViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar pBar;
    public ProgressViewHolder(View v) {
        super(v);
        pBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }
}