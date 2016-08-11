package org.secuso.privacyfriendlyweather;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.orm.City;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.ui.AutoCompleteCityTextViewGenerator;

public class RadiusSearchActivity extends BaseActivity {

    /**
     * Visual components
     */
    private AutoCompleteTextView edtLocation;
    private SeekBar sbEdgeLength;
    private TextView tvEdgeLengthValue;
    private SeekBar sbNumReturns;
    private TextView tvNumReturnsValue;
    private Button btnSearch;

    /**
     * Other components
     */
    private DatabaseHelper dbHelper;
    private City dropdownSelectedCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radius_search);
        overridePendingTransition(0, 0);

        dbHelper = new DatabaseHelper(getApplicationContext());
        initialize();
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_radius;
    }

    /**
     * Initializes the visual components / the view.
     */
    private void initialize() {
        final int MAX_EDGE_LENGTH = 50;
        final int MAX_NUMBER_OF_RETURNS = 5;
        final String FORMAT_EDGE_LENGTH_VALUE = "%s km";

        AutoCompleteCityTextViewGenerator generator = new AutoCompleteCityTextViewGenerator(this, dbHelper);
        edtLocation = (AutoCompleteTextView) findViewById(R.id.radius_search_edt_location);
        generator.getInstance(edtLocation, 8, dropdownSelectedCity);
        sbEdgeLength = (SeekBar) findViewById(R.id.radius_search_sb_edge_length);
        tvEdgeLengthValue = (TextView) findViewById(R.id.radius_search_tv_edge_length_value);
        sbNumReturns = (SeekBar) findViewById(R.id.radius_search_sb_num_returns);
        tvNumReturnsValue = (TextView) findViewById(R.id.radius_search_tv_num_returns_value);
        btnSearch = (Button) findViewById(R.id.radius_search_btn_search);

        // Set properties of seek bars and the text of the corresponding text views
        sbEdgeLength.setMax(MAX_EDGE_LENGTH);
        sbEdgeLength.setProgress(MAX_EDGE_LENGTH >> 1);
        tvEdgeLengthValue.setText(String.format(FORMAT_EDGE_LENGTH_VALUE, sbEdgeLength.getProgress()));

        sbNumReturns.setMax(MAX_NUMBER_OF_RETURNS);
        sbNumReturns.setProgress(MAX_NUMBER_OF_RETURNS);
        tvNumReturnsValue.setText(String.valueOf(sbNumReturns.getProgress()));

        // On change of the seek bars set the text of the corresponding text views
        sbEdgeLength.setOnSeekBarChangeListener(new OnSeekBarEdgeLengthChange());
        sbNumReturns.setOnSeekBarChangeListener(new OnSeekBarNumberOfReturnsChange());

        // Set the click event on the button
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnButtonSearchClick();
            }
        });
    }

    /**
     * This method handles the click event on the 'Search' button.
     */
    private void handleOnButtonSearchClick() {
        // Retrieve all necessary inputs
        int edgeLength = sbEdgeLength.getProgress();
        int numberOfReturnCities = sbNumReturns.getProgress();
    }

    /**
     * Implements the logic for the SeekBar to set the edge length.
     */
    private class OnSeekBarEdgeLengthChange implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            String text = String.format("%s km", progress);
            tvEdgeLengthValue.setText(text);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

    /**
     * Implements the logic for the SeekBar to set the number of returned cities.
     */
    private class OnSeekBarNumberOfReturnsChange implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            tvNumReturnsValue.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

}