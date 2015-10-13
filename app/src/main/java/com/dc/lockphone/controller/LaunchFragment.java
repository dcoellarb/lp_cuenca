package com.dc.lockphone.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dc.lockphone.R;

/**
 * Created by dcoellar on 9/29/15.
 */
public class LaunchFragment extends Fragment {

    private LayoutInflater inflater;
    private TextView title;
    private String titleText;
    private TextView text;
    private String textText;
    private ImageView image;
    private int imageResourceId;

    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;

        View rootView = inflater.inflate(R.layout.frament_launch,container,false);
        title = (TextView)rootView.findViewById(R.id.launch_title);
        title.setText(titleText);
        text = (TextView)rootView.findViewById(R.id.launch_text);
        text.setText(textText);
        image = (ImageView)rootView.findViewById(R.id.launch_image);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            image.setBackgroundDrawable(getResources().getDrawable(imageResourceId));
        } else {
            image.setBackground(getResources().getDrawable(imageResourceId));
        }
        return rootView;
    }

    public void setContent(String title,String text,int resource){
        this.titleText = title;
        this.textText = text;
        this.imageResourceId = resource;
    }
}
