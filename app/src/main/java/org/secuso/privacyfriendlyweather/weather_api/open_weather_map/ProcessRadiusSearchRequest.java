package org.secuso.privacyfriendlyweather.weather_api.open_weather_map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.RadiusSearchResultActivity;
import org.secuso.privacyfriendlyweather.radius_search.RadiusSearchItem;
import org.secuso.privacyfriendlyweather.radius_search.RadiusSearchItemComparator;
import org.secuso.privacyfriendlyweather.weather_api.IDataExtractor;
import org.secuso.privacyfriendlyweather.weather_api.IProcessHttpRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Processes the radius search request to extract location data as well as the request to retrieve
 * the locations with the best weather.
 */
public class ProcessRadiusSearchRequest implements IProcessHttpRequest {

    /**
     * Member variables
     */
    private Context context;
    private int edgeLength;
    private int resultCount;

    /**
     * Constructor.
     *
     * @param edgeLength  The edge length of the search square.
     * @param resultCount Determines the number of cities to display.
     */
    public ProcessRadiusSearchRequest(Context context, int edgeLength, int resultCount) {
        this.context = context;
        this.edgeLength = edgeLength;
        this.resultCount = resultCount;
    }

    /**
     * @see IProcessHttpRequest#processSuccessScenario(String)
     */
    @Override
    public void processSuccessScenario(String response) {
        // The following value is the fifth parameter for the bounding box and represents the
        // map zoom, as explained here as of 2016-08-12:
        // https://github.com/renkun-ken/rlist-tutorial/blob/master/Examples/Weather-API.Rmd
        // It is set to 10 because this seems to be a good granularity for this project
        final int MAP_ZOOM = 10;

        IDataExtractor extractor = new OwmDataExtractor();
        double[] latitudeLongitude = extractor.extractLatitudeLongitude(response);
        if (latitudeLongitude.length > 0) {
            double[] boundingBox = getBoundingBox(latitudeLongitude, edgeLength);
            IHttpRequestForRadiusSearchResults owmHttpRequestForResults =
                    new OwmHttpRequestForRadiusSearch(null).new OwmHttpRequestForResults(context, resultCount, boundingBox, MAP_ZOOM);
            owmHttpRequestForResults.perform();
        }
    }

    /**
     * @see IProcessHttpRequest#processFailScenario(VolleyError)
     */
    @Override
    public void processFailScenario(VolleyError error) {
        // TODO: Fill with life
    }

    /**
     * @param latLon     The center of the square.
     * @param edgeLength The edge length of the square.
     * @return Returns the square with the given edge length where the given latLon argument is the center of the square.
     * The first value in the return array is the left longitude, second bottom latitude, third right longitude, fourth
     * top latitude.
     */
    private static double[] getBoundingBox(double[] latLon, int edgeLength) {
        if (latLon.length != 2) {
            throw new IllegalArgumentException("Expected an array with two elements where the first is the longitude and the second the latitude");
        }

        // Compute the longitude difference
        // The formulas have been taken from the answer by Jim Lewis as of 2016-08-12
        // http://stackoverflow.com/questions/1253499/simple-calculations-for-working-with-lat-lon-km-distance
        double distance = edgeLength >> 1;
        double latDif = (distance / 110.574);
        // Need to do a degree to radian conversion here
        double lonDif = (distance / (111.32 * Math.cos((latLon[0] * Math.PI) / 180)));

        // Compute and return the square
        return new double[]{
                latLon[1] - lonDif,
                latLon[0] - latDif,
                latLon[1] + lonDif,
                latLon[0] + latDif
        };
    }

    public class ProcessRadiusSearchResultRequest implements IProcessHttpRequest {

        /**
         * Member variables
         */
        private Context context;
        private int resultCount;

        /**
         * Constructor.
         *
         * @param context The context to use.
         */
        public ProcessRadiusSearchResultRequest(Context context, int resultCount) {
            this.context = context;
            this.resultCount = resultCount;
        }

        /**
         * @see IProcessHttpRequest#processSuccessScenario(String)
         */
        @Override
        public void processSuccessScenario(String response) {
            // Retrieve all weather information
            List<RadiusSearchItem> radiusItems = new ArrayList<>();
            IDataExtractor extractor = new OwmDataExtractor();
            try {
                JSONObject json = new JSONObject(response);
                JSONArray list = json.getJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    String currentItem = list.get(i).toString();
                    RadiusSearchItem searchItem = extractor.extractRadiusSearchItemData(currentItem);
                    // Data were not well-formed, abort
                    if (searchItem == null) {
                        final String ERROR_MSG = context.getResources().getString(R.string.convert_to_json_error);
                        Toast.makeText(context, ERROR_MSG, Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        radiusItems.add(searchItem);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Sort the weather and get the items to display
            Collections.sort(radiusItems, new RadiusSearchItemComparator());
            int endIndex = radiusItems.size() > resultCount ? resultCount : radiusItems.size();
            ArrayList<RadiusSearchItem> resultList = new ArrayList<>();
            for (int i = 0; i < endIndex; i++) {
                resultList.add(radiusItems.get(i));
            }

            // Finally, load the activity to show the result
            Intent intent = new Intent(context, RadiusSearchResultActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("resultList", resultList);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }

        /**
         * @see IProcessHttpRequest#processFailScenario(VolleyError)
         */
        @Override
        public void processFailScenario(VolleyError error) {
            // TODO: Fill with life
        }
    }

}
