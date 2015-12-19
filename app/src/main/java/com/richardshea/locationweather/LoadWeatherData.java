package com.richardshea.locationweather;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by richardshea on 11/8/15.
 */
public class LoadWeatherData extends AsyncTask<Void, Void, String> {

    private static final String TAG = LoadWeatherData.class.getSimpleName();
    private static final String PREURL = "https://query.yahooapis.com/v1/public/yql?q=";
    private static final String SQL = "select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22";
    private static final String POSTURL = "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    private String mCity;

    private String mUrl;

    private WeatherCallback mCallback;

    public LoadWeatherData(String city, WeatherCallback weatherCallback) {

        mCity = city;
        mCallback = weatherCallback;
    }

    private String convertStr(String inputStr) {

        char[] strAry = inputStr.toCharArray();
        StringBuilder str = new StringBuilder();
        for (char ch : strAry) {
            if (Character.isSpaceChar(ch))
                str.append("%20");
            else
                str.append(ch);
        }
        return str.toString();
    }

    private String composeUrl(String str) {

        StringBuilder urlStr = new StringBuilder();

        urlStr.append(PREURL);
        urlStr.append(SQL);
        String addSignBetweenWordsStr = convertStr(str);
        urlStr.append(addSignBetweenWordsStr);
        urlStr.append(POSTURL);

        return urlStr.toString();
    }

    protected void onPreExecute() {

        mUrl = composeUrl(mCity);
    }

    private InputStream retrieveStream(String requestUrl) {

        URL url = null;

        try
        {
            // create the HttpURLConnection
            url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            connection.connect();

            return connection.getInputStream();

        }
        catch (Exception e)
        {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected String doInBackground(Void... params) {

        InputStream source = retrieveStream(mUrl);

        JsonReader reader = new JsonReader(new InputStreamReader(source));

        try {

            String weather = readSource(reader);
            return weather;
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    protected void onPostExecute(String weather) {

        mCallback.onResult(weather);
    }

    private String readSource(JsonReader reader) throws IOException {

        Log.i(TAG, "readSource !!!! ");
        return (reader != null) ? parseInformation(reader) : null;
    }

    private String parseInformation(JsonReader reader) {

        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String key = reader.nextName();
                if(NameTag.QUERY.equalsIgnoreCase(key) || NameTag.RESULTS.equalsIgnoreCase(key) ||
                        NameTag.CHANNEL.equalsIgnoreCase(key)) {
                    String information = parseInformation(reader);
                    return information;
                } else if(NameTag.ITEM.equalsIgnoreCase(key)) {
                    String description = parseDescription(reader);
                    return description;
                } else {
                    reader.skipValue();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String parseDescription(JsonReader reader) {

        Log.i(TAG, "parseDescription !!!! ");

        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String key = reader.nextName();
                if(NameTag.DESCRIPTION.equalsIgnoreCase(key)) {
                    String result = new String(reader.nextString());
                    return result;
                } else {
                    reader.skipValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
