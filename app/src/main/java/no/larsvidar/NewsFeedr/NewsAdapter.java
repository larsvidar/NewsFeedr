package no.larsvidar.NewsFeedr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    //Constructor
    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Check if there already is an view we can reuse.
        View newsListView = convertView;
        if (newsListView == null) {
            newsListView = LayoutInflater.from(getContext()).inflate(R.layout.news_list, parent, false);
        }

        //Get news item at the current position.
        News currentNews = getItem(position);

        //Find "news_section" view, and set text.
        TextView sectionView = newsListView.findViewById(R.id.news_section);
        sectionView.setText(currentNews.getSection());

        //Find "news_article" view, and set text.
        TextView articleView = newsListView.findViewById(R.id.news_article);
        articleView.setText(currentNews.getArticle());

        //Find "news_author" view, and set text.
        TextView authorView = newsListView.findViewById(R.id.news_author);
        String authorText = getContext().getText(R.string.byline) + " " + currentNews.getAuthor();
        authorView.setText(authorText);

        //Find "news_title" view, and set text.
        TextView dateView = newsListView.findViewById(R.id.news_date);
        //Stripping away unnecessary date info.
        String[] splitDate = currentNews.getDate().split("T");
        String dateText = getContext().getText(R.string.published) + " " + splitDate[0];
        dateView.setText(dateText);

        return newsListView;
    }
}
