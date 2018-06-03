package no.larsvidar.NewsFeedr;

public class News {
    private String mSection;
    private String mArticle;
    private String mAuthor;
    private String mDate;
    private String mUrl;

    public News(String section, String article, String author, String date, String url) {
        mSection = section;
        mArticle = article;
        mAuthor = author;
        mDate = date;
        mUrl = url;
    }

    public String getSection() {
        return mSection;
    }

    public String getArticle() {
        return mArticle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }
}
