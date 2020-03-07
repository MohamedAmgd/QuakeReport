package com.example.android.quakereport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.graphics.drawable.GradientDrawable;



public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    public EarthquakeAdapter(@NonNull Context context, @NonNull ArrayList<Earthquake> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       View listItemView = convertView;
       if (listItemView == null){
           listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item , parent , false);
       }

       final Earthquake currentEarthquake = getItem(position);

       DecimalFormat decimalFormat = new DecimalFormat("0.0");
       SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy\n h:mm a");


       String primaryLocation , offsetLocation;
       if(currentEarthquake.getLocation().contains("of")){
           offsetLocation = currentEarthquake.getLocation().substring(0 , currentEarthquake.getLocation().indexOf("of" )+2);
           primaryLocation = currentEarthquake.getLocation().substring(currentEarthquake.getLocation().indexOf("of")+2).trim();

           //orv .split("(?<=of)")
       }
       else{
           offsetLocation = getContext().getString(R.string.near_the);
           primaryLocation = currentEarthquake.getLocation();
       }


       TextView mag = (TextView) listItemView.findViewById(R.id.mag);
       mag.setText(decimalFormat.format(currentEarthquake.getMag()));

       TextView Olocation = (TextView) listItemView.findViewById(R.id.Olocation);
       Olocation.setText(offsetLocation);

       TextView Plocation = (TextView) listItemView.findViewById(R.id.Plocation);
       Plocation.setText(primaryLocation);

       TextView time = (TextView) listItemView.findViewById(R.id.quake_time);
       time.setText(dateFormat.format(new Date(currentEarthquake.getTime())));


      // Date date = new Date(currentEarthquake.getTime()); // for debug
      // String s = time.getText().toString();  // for debug


        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) mag.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMag());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);


        return listItemView;
    }

    private int getMagnitudeColor(double mag) {
        int magnitude1Color;

        switch ((int) Math.floor(mag)) {
            case 0:
            case 1:
                magnitude1Color = ContextCompat.getColor(getContext(), R.color.magnitude1);
                break;
            case 2:
                magnitude1Color = ContextCompat.getColor(getContext(), R.color.magnitude2);
                break;
            case 3:
                magnitude1Color = ContextCompat.getColor(getContext(), R.color.magnitude3);
                break;
            case 4:
                magnitude1Color = ContextCompat.getColor(getContext(), R.color.magnitude4);
                break;
            case 5:
                magnitude1Color = ContextCompat.getColor(getContext(), R.color.magnitude5);
                break;
            case 6:
                magnitude1Color = ContextCompat.getColor(getContext(), R.color.magnitude6);
                break;
            case 7:
                magnitude1Color = ContextCompat.getColor(getContext(), R.color.magnitude7);
                break;
            case 8:
                magnitude1Color = ContextCompat.getColor(getContext(), R.color.magnitude8);
                break;
            case 9:
                magnitude1Color = ContextCompat.getColor(getContext(), R.color.magnitude9);
                break;
            default:
                magnitude1Color = ContextCompat.getColor(getContext(), R.color.magnitude10plus);


        }
        return magnitude1Color;
    }
}
