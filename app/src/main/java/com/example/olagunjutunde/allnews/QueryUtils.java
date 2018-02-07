package com.example.olagunjutunde.allnews;

/**
 * Created by OLAGUNJU TUNDE on 12/8/2017.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;

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

import static com.example.olagunjutunde.allnews.Utils.extractNewsListFromJSON;


public final class QueryUtils {


    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**Create a private object so that no one ever creates an object of the class */

    private QueryUtils(){

    }


    public static List<News> fetchNewsData(String requestUrl) {


        //call to the method that creates a URL object from the URL address
        URL url = createUrl(requestUrl);

        String jSonResponse = null;

        try {

            //call to the method that performs or makes the HTTP request
            jSonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem Making The HTTP request", e);
        }

        List<News> news = extractNewsListFromJSON(jSonResponse);

        return news;

    }



    /** Helper method to create a URL object from the URL address*/
    private static URL createUrl(String requestUrl) {

        URL url = null;

        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {

            Log.e(LOG_TAG, "Error while creating the URL ", e);
        }
        return url;
    }


    /**
     * Helper Method to make HttpRequest to the given URL*/
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }


        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {

                //receive the response as an input stream
                inputStream = urlConnection.getInputStream();

                //interpret the input stream and receive the response as json String
                jsonResponse = readFromInputStream(inputStream);

            } else {

                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {

            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);

        } finally {

            if (urlConnection != null) {

                // disconnect url connection if it is not null
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }

        return jsonResponse;


    }


    /**
     * Convert the  InputStream into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromInputStream(InputStream inputStream) throws IOException {

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

    public static List<News> extractNewsListFromJSON(String jsonResponse) {

        List<News> newsList = null;
        if (jsonResponse.length() == 0)
            return newsList;

        try {
            Log.e(LOG_TAG, "The response is: " + jsonResponse);
            newsList = new ArrayList<>();
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray articles = response.getJSONArray("articles");
            News newsObj;
            for (int i = 0; i < articles.length(); i++) {
                JSONObject articleJsonObject = articles.getJSONObject(i);



                String author = articleJsonObject.getString("author");
                String title = articleJsonObject.getString("title");
                String description = articleJsonObject.getString("description");
                String url = articleJsonObject.getString("url");

                String urlToImage = articleJsonObject.getString("urlToImage");


                    Bitmap imageNews = makeHttpRequest(urlToImage);

                    String time = articleJsonObject.getString("publishedAt");


                    newsObj = new News(title, time, description, url, imageNews);

                    newsList.add(newsObj);

            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }catch (IOException e){
            Log.e(LOG_TAG,"IOException",e);
        }

        // Return the list of earthquakes
        return newsList;
    }



/**Helper method to make HTTP request to the  image URL*/
    private static Bitmap makeHttpRequest (String  imageUrl) throws IOException  {

        URL url = null;
        Bitmap image = null;

        if (imageUrl == null){

            return image;
        }
        else {
            try {
                url = new URL(imageUrl);
            } catch (MalformedURLException e) {

                Log.e(LOG_TAG, "Error while creating the URL ", e);
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);

                urlConnection.connect();

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    inputStream = urlConnection.getInputStream();
                    image = BitmapFactory.decodeStream(inputStream);

                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while making the URL connections", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return image;

    }
}


