package com.richardshea.locationweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/*
        CitySearchActivity is the main activity to show the possible different places
        named the same name such as London which could be from United Kingdom or Canada
        or US.
 */
public class CitySearchActivity extends Activity {

    private static final String TAG = CitySearchActivity.class.getSimpleName();

    private Context mContext;

    private EditText mCityEdView;

    private Button mSearchBtn;

    private LocationAdapter mLocationAdapter;

    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);

        mContext = getApplicationContext();
        mCityEdView = (EditText) findViewById(R.id.searchView);

        mListView = (ListView) findViewById(R.id.searchViewResult);
        mLocationAdapter = new LocationAdapter(mContext);
        mListView.setAdapter(mLocationAdapter);
        mListView.setOnItemClickListener(mListItemListener);

        mSearchBtn = (Button) findViewById(R.id.searchButton);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchLocations(mCityEdView.getText().toString());
            }
        });
    }

    private void searchLocations(String city) {

        if(city == null || city.isEmpty()) {
            //TODO: No result!!
            return;
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

        LoadPlacesData loadPlacesData = new LoadPlacesData(mLocationAdapter, city);
        loadPlacesData.execute();

    }

    protected ListView.OnItemClickListener mListItemListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Log.i(TAG, "onItemSelected: position = " + position + ", id = " + id);
            Log.i(TAG, "onItemSelected: Text = " + parent.getAdapter().getItem(position));

            String city = (String) parent.getAdapter().getItem(position);
            LoadWeatherData loadWeatherData = new LoadWeatherData(city, mWeatherCallback);
            loadWeatherData.execute();
        }
    };

    private WeatherCallback mWeatherCallback = new WeatherCallback() {

        @Override
        public void onResult(String result) {

            Intent intent = new Intent(mContext, DisplayWeatherActivity.class);
            intent.putExtra(NameTag.WEATHER, result);
            startActivity(intent);
        }
    };
}
