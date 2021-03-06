package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.content.Context;

import org.secuso.privacyfriendlyweather.http.HttpRequestType;
import org.secuso.privacyfriendlyweather.http.IHttpRequest;
import org.secuso.privacyfriendlyweather.http.VolleyHttpRequest;
import org.secuso.privacyfriendlyweather.orm.CityToWatch;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForCityList;

import java.util.List;

/**
 * This class provides the functionality for making and processing HTTP requests to the
 * OpenWeatherMap to retrieve the latest weather data for a given city and then process the
 * response.
 */
public class OwmHttpRequestAddCity extends OwmHttpRequest implements IHttpRequestForCityList {

    private Context context;
    private DatabaseHelper dbHelper;
    private boolean storePersistently;

    /**
     * @param context           The application context.
     * @param dbHelper          The database helper to use.
     * @param storePersistently Indicates whether to store the requested city permanently.
     */
    public OwmHttpRequestAddCity(Context context, DatabaseHelper dbHelper, boolean storePersistently) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.storePersistently = storePersistently;
    }

    /**
     * @see IHttpRequestForCityList#perform(List)
     */
    @Override
    public void perform(List<CityToWatch> cities) {
        IHttpRequest httpRequest = new VolleyHttpRequest(context);
        final String URL = getUrlForQueryingSingleCity(cities.get(0).getCity().getCityId(), true);
        httpRequest.make(URL, HttpRequestType.GET, new ProcessOwmAddCityRequest(context, dbHelper, storePersistently));
    }

}
