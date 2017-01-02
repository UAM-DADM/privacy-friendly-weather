package org.secuso.privacyfriendlyweather;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.secuso.privacyfriendlyweather.orm.CurrentWeatherData;
import org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter;

import java.util.List;

import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.DAY;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.DETAILS;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.OVERVIEW;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.SUN;
import static org.secuso.privacyfriendlyweather.ui.RecycleList.ForecastAdapter.WEEK;

public class ForecastCityActivity extends BaseActivity {

    private static CurrentWeatherData currentWeatherData = null;

    private RecyclerView mRecyclerView;
    private ForecastAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //TODO Change to DB Values
    private String[] mDataset = {"29°C", "Seahawks 24 - 27 Bengals",
            "Flash missing, vanishes in crisis", "Half Life 3 announced"};
    private List<CurrentWeatherData> currentWeatherDataList;

    private int mDataSetTypes[] = {OVERVIEW, DETAILS, WEEK, DAY, SUN}; //TODO Make dynamic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_city);
        overridePendingTransition(0, 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewActivity);
        mLayoutManager = new LinearLayoutManager(ForecastCityActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //Adapter is created in the last step
        //TODO Get dataset from DB
        mAdapter = new ForecastAdapter(mDataset, mDataSetTypes);
        mRecyclerView.setAdapter(mAdapter);

        //TODO Change to city name from DB
        setTitle("Darmstadt");

    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_weather;
    }

}

