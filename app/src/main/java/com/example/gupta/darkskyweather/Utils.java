package com.example.gupta.darkskyweather;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableResource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Gupta on 10/10/2017.
 */

public class Utils {

    static String api;

    static JSONObject getApiData(String api){
        try {
            URL url = new URL(api);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String response = convertStreamToString(in);
            return new JSONObject(response);
        } catch (MalformedURLException e) {
            Log.e("error", e.getMessage());
        } catch (IOException e) {
            Log.e("error", e.getMessage());
        } catch (JSONException e) {
            Log.e("error", e.getMessage());
        }
        return null;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    } //ty stackOverflow


    static class JsonHandler extends AsyncTask<Double, Void, List<String>> {

        private Context context;

        private JsonHandler() {}

        JsonHandler(Context context){
            this.context = context;
        }

        @Override
        protected List<String> doInBackground(Double...doubles) {
            api = "https://api.darksky.net/forecast/877ef0fd9c624caa9bf053247ec1b7dd/" + doubles[0] + "," + doubles[1] +"/";
            List<String> toReturn = new ArrayList<String>();
                JSONObject fullData = getApiData(api);
                try {
                    toReturn.add(Integer.toString(Math.round(Float.parseFloat(fullData.getJSONObject("currently").getString("temperature")))) + "°");
                    JSONArray minutelyData = fullData.getJSONObject("minutely").getJSONArray("data");
                    boolean rain = false;
                    int i = 0;
                    while (i < minutelyData.length() && rain==false) {
                        if (minutelyData.getJSONObject(i).getString("precipProbability") == "1") {
                            rain = true;
                            toReturn.add(minutelyData.getJSONObject(i).getString("time"));
                        }
                        i++;
                        if (i == minutelyData.length()) {
                            toReturn.add("No rain in the next hour");
                        }
                    }
                    toReturn.add(fullData.getJSONObject("daily").getString("summary"));

                    if (toReturn.get(1) != "No rain in the next hour") {
                        Date d = new Date(Long.getLong(toReturn.get(1)));
                        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy,HH:mm");
                        f.setTimeZone(TimeZone.getDefault());
                        String s = f.format(d);
                        String originalString = s;
                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(originalString);
                        String newString = new SimpleDateFormat("H:mm").format(date);
                        toReturn.add(newString);
                    }
                    else{
                        toReturn.add(toReturn.get(1));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    toReturn.add(fullData.getJSONObject("currently").getString("temperature"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    toReturn.add(fullData.getJSONObject("currently").getString("temperature"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    toReturn.add(fullData.getJSONObject("currently").getString("summary"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            try {
                toReturn.add(fullData.getJSONObject("minutely").getString("icon"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return toReturn;
        }

        @Override
        protected void onPostExecute(List<String> s) {
            super.onPostExecute(s);
            TextView temperature = (TextView) ((Activity) context).findViewById(R.id.temperature);
            TextView summary = (TextView) ((Activity) context).findViewById(R.id.summary);
            TextView rain = (TextView) ((Activity) context).findViewById(R.id.rain);
            ImageView icon = (ImageView) ((Activity) context).findViewById(R.id.icon);
            temperature.setText(s.get(0));
            summary.setText(s.get(2));
            if (s.get(3) != "No rain in the next hour"){
                rain.setText("Rain at: " + s.get(3));
            }
            else {rain.setText(s.get(3));}
            int reference = 0;
           switch(s.get(7)){
                case "clear-day":
                    reference = R.drawable.sunny;
                    break;
                case "clear-night":
                    reference = R.drawable.nightcloudy;
                    break;
                case "rain":
                    reference = R.drawable.rain;
                    break;
                case "snow":
                    reference = R.drawable.snowy;
                    break;
                case "sleet":
                    reference = R.drawable.rain;
                    break;
                case "wind":
                    reference = R.drawable.windy;
                    break;
                case "fog":
                    reference = R.drawable.cloudy;
                    break;
                case "cloudy":
                    reference = R.drawable.nightcloudy;
                    break;
                case "partly-cloudy-day":
                    reference = R.drawable.partlycloudy;
                    break;
                case "partly-cloudy-night":
                    reference = R.drawable.nightcloudy;
                    break;
            }
            Glide.with((Activity) context).load(reference).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into((ImageView) ((Activity) context).findViewById(R.id.icon));
        }
    }
}
