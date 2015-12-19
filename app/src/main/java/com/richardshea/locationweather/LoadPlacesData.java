package com.richardshea.locationweather;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by richardshea on 11/7/15.
 */
public class LoadPlacesData extends AsyncTask<Void, Void, List<String>> {

    private static final String TAG = LoadPlacesData.class.getSimpleName();

    private String mUrl;

    private String mQStr;

    LocationAdapter mAdapter;

    public LoadPlacesData(LocationAdapter adapter, String cityStr) {

        mAdapter = adapter;
        mQStr = cityStr;
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

        Log.i(TAG, "composeUrl !!!! ");

        StringBuilder urlStr = new StringBuilder();

        urlStr.append("https://maps.googleapis.com/maps/api/geocode/json?address=");
        String addSignBetweenWordsStr = convertStr(str);
        urlStr.append(addSignBetweenWordsStr);

        return urlStr.toString();
    }

    protected void onPreExecute() {

        mUrl = composeUrl(mQStr);
    }


    private InputStream retrieveStream(String requestUrl) {

        Log.i(TAG, "retrieveStream !!!! ");

        URL url = null;

        try
        {
            // create the HttpURLConnection
            url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // give it 15 seconds to respond
            connection.connect();

            // read the output from the server
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
    protected List<String> doInBackground(Void... params) {

        Log.i(TAG, "doInBackground !!!! ");
        InputStream source = retrieveStream(mUrl);

        JsonReader reader = new JsonReader(new InputStreamReader(source));

        try {
            List<String> itemList = readSource(reader);

            return itemList;
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

    protected void onPostExecute(List<String> entries) {

        Log.i(TAG, "onPostExecute !!!! ");
        if(entries != null && entries.size() != 0)
            mAdapter.upDateEntries(entries);
    }

    private List<String> readSource(JsonReader reader) throws IOException {

        Log.i(TAG, "readSource !!!! ");
        return (reader != null) ? getEntry(reader) : null;
    }

    private List<String> getEntry(JsonReader reader) {

        JsonReader jsonReader = getFeed(reader);
        List<String> values = new ArrayList<>();
        if(jsonReader != null) {
            try {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    while(reader.hasNext()) {
                        String key = reader.nextName();
                        if(NameTag.FORMATTED_ADDRESS.equalsIgnoreCase(key)) {
                            String item = reader.nextString();
                            values.add(item);
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                }
                reader.endArray();
                return values;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    private JsonReader getFeed(JsonReader reader) {

        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String key = reader.nextName();
                if(NameTag.RESULTS.equalsIgnoreCase(key)) {
                    return reader;
                } else {
                    reader.skipValue();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
