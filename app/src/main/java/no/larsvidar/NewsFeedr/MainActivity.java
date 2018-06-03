package no.larsvidar.NewsFeedr;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {

    //URL for fetching news from Guardian API.
    private static final String GUARDIAN_API_URL = "https://content.guardianapis.com/search?";

    //Adapter for news.
    private NewsAdapter mAdapter;

    //TextView for empty News-items.
    private TextView mEmptyTextView;

    /**
     * OnCreate method
     *
     * @param savedInstanceState Saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the ListView in the layout.
        final ListView newsListView = findViewById(R.id.news_list_view);

        //Find "empty_view" view.
        mEmptyTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyTextView);

        //Create an empty list of news.
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        //Set the adapter
        newsListView.setAdapter(mAdapter);

        //Make a reference to SharedPreference file and set a listener on preference changes.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);


        //Create an item click listener to open news story in browser when selected.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long row) {
                //Find the news item that was clicked.
                News currentNews = mAdapter.getItem(position);

                //Convert String URL to URI-object.
                Uri newsUri = Uri.parse(currentNews.getUrl());

                //Create intent to open URI in browser and launch it.
                Intent webIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(webIntent);
            }
        });

        //Create click listener for update button.
        Button updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Display Loading message.
                mEmptyTextView.setText(getText(R.string.loading));
                //Check for network connection
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    //Restart loader to fetch data from server again.
                    getLoaderManager().restartLoader(1, null, MainActivity.this);
                } else {
                    //Show error if there is no network connection.
                    mEmptyTextView.setText(getText(R.string.no_connection));
                }

                //Clear out old data and populate the ListView again.
                mAdapter.clear();
                newsListView.setAdapter(mAdapter);
            }
        });

        //Create a reference to ConnectivityManager to check network status.
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Display Loading message.
        mEmptyTextView.setText(getText(R.string.loading));

        //Get data if there is a network connection.
        if (networkInfo != null && networkInfo.isConnected()) {
            //Create reference to LoaderManager and initialize the loader.
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, this);
        } else {
            //Show error if there is no network connection.
            mEmptyTextView.setText(getText(R.string.no_connection));
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String newsCategory = sharedPrefs.getString(getString(R.string.news_category_key), getString(R.string.default_search));

        String sortOrder = sharedPrefs.getString(getString(R.string.sort_order_key), getString(R.string.default_sort_order));

        //Parse API url and pass it to Uri.Builder.
        Uri apiUri = Uri.parse(GUARDIAN_API_URL);
        Uri.Builder uriBuilder = apiUri.buildUpon();
        //Set query options.
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("api-key", "test");
        uriBuilder.appendQueryParameter("q", newsCategory);
        uriBuilder.appendQueryParameter("from-date", "2018-01-01");
        uriBuilder.appendQueryParameter("order-by", sortOrder);
        uriBuilder.appendQueryParameter("show-fields", "byline");

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Set empty state text.
        mEmptyTextView.setText(getText(R.string.no_stories));

        //If there are valid News objects in news array, add them to mAdapter.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

        if (key.equals(getString(R.string.news_category_key)) || key.equals(getString(R.string.sort_order_key))) {
            //Clear the view.
            mAdapter.clear();

            //Restart loader to get new query from the server.
            getLoaderManager().restartLoader(1, null, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
