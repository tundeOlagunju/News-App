package com.example.olagunjutunde.allnews;

import android.graphics.Bitmap;

/**
 * Created by OLAGUNJU TUNDE on 12/8/2017.
 */

public class News {

    private String title;
    private String publishedAt;
    private String description;
    private String url;
    private Bitmap imageNews;

    public News(String title, String publishedAt,String description,  String url, Bitmap imageNews) {
        this.title = title;
        this.publishedAt = publishedAt;
        this.description = description;
        this.url =url;
        this.imageNews = imageNews;

    }



    public News(String title, String publishedAt,String description,  String url) {
        this.title = title;
        this.publishedAt = publishedAt;
        this.description = description;
        this.url =url;
    }


    public String getTitle() {
        return title;
    }


    public String getPublishedAt(){
        return publishedAt;
    }


    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getImageNews() {
        return imageNews;
    }
}
