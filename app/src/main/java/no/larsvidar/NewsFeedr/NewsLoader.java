package no.larsvidar.NewsFeedr;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    //Query url.
    private String mApiUrl;

    public NewsLoader(Context context, String apiUrl) {
        super(context);
        mApiUrl = apiUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mApiUrl == null) {
            return null;
        }

        //Perform query to server.
        return QueryUtils.fetchNewsStories(mApiUrl);
    }
}
