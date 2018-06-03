package no.larsvidar.NewsFeedr;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Class of helper methods for getting news stories from Guardian API.
 */
public final class QueryUtils {

    //Empty constructor.
    private QueryUtils() {
    }

    //Request list of news stories from Guardian.
    public static List<News> fetchNewsStories(String queryUrl) {
        //Create URL object.
        URL apiUrl = createUrl(queryUrl);

        //Sends query and recives JSON-string.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(apiUrl);
        } catch (IOException ioe) {
            Log.e("ERROR: ", "Problem making HTTP request." + "ERROR MESSAGE: " + ioe);
        }

        //Extract relevant data from JSON response and return result
        return extractDataFromJson(jsonResponse);
    }

    /**
     * Method for creating URL object from String.
     *
     * @param stringUrl Url String to be converted to object.
     * @return apiUrl the URL object.
     */
    private static URL createUrl(String stringUrl) {
        URL apiUrl = null;
        try {
            apiUrl = new URL(stringUrl);
        } catch (MalformedURLException mue) {
            Log.e("ERROR: ", "Problem building URL." + "Error message: " + mue);
        }

        return apiUrl;
    }

    /**
     * Method for sending HTTP request and reciving response.
     *
     * @param apiUrl Url to query for information at.
     * @return jsonRespone JSON response.
     * @throws IOException if there is a problem retrieving data from server.
     */
    private static String makeHttpRequest(URL apiUrl) throws IOException {
        String jsonResponse = "";

        //Check apiUrl.
        if (apiUrl == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) apiUrl.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If connection is successful.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("ERROR: ", "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException ioe) {
            Log.e("ERROR: ", "Problem retrieving JSON response from Guardian API. " + "Error message: " + ioe);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * Method for getting relevant data from JSON response.
     *
     * @param newsJson JSON object to extract relevant data from.
     * @return news ArrayList.
     */
    private static List<News> extractDataFromJson(String newsJson) {
        //If the JSON string is empty, then return null.
        if (TextUtils.isEmpty(newsJson)) {
            return null;
        }

        //Create an empty ArrayList to be populated by News objects.
        List<News> news = new ArrayList<>();

        //Try to parse the JSON object and catch exception if thrown.
        try {
            //Create JSON object.
            JSONObject baseJsonResponse = new JSONObject(newsJson);
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");

            //Create JSONArray.
            JSONArray newsArray = responseObject.getJSONArray("results");

            //Create News object for each object in the JSONArray.
            for (int i = 0; i < newsArray.length(); i++) {
                //Get the next JSONObject in line in the JSONArray.
                JSONObject currentNewsStory = newsArray.getJSONObject(i);

                //Getting sectionName value from JSONObject.
                String section = currentNewsStory.getString("sectionName");

                //Getting article value from JSONObject.
                String article = currentNewsStory.getString("webTitle");

                //Getting author value from JSONObject.
                JSONObject fields = currentNewsStory.getJSONObject("fields");
                String author = fields.getString("byline");

                //Getting date value from JSONObject.
                String date = currentNewsStory.getString("webPublicationDate");

                //Getting article value from JSONObject.
                String url = currentNewsStory.getString("webUrl");

                //Make new News object.
                News newsStory = new News(section, article, author, date, url);

                //Add News object to news array.
                news.add(newsStory);
            }
        } catch (JSONException je) {
            //Print error message if an exception is thrown.
            Log.e("ERROR: ", "Problem parsing JSON result. " + "Error message: " + je);
        }
        return news;
    }

    /**
     * Method for converting InputStream into a String.
     *
     * @param inputStream to read from.
     * @return output.toString() The date from the JSON response.
     * @throws IOException if there is a problem with inputStream.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        //Create stringbuilder to return the string.
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


}
