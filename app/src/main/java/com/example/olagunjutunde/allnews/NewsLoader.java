package com.example.olagunjutunde.allnews;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;


import com.example.olagunjutunde.allnews.News;
import com.example.olagunjutunde.allnews.QueryUtils;

import java.util.List;



/**
 * Created by OLAGUNJU TUNDE on 12/6/2017.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>>{

    private String mUrl;

    public  NewsLoader(Context context,String url){

        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null){
            return null;
        }

        List<News> news =  QueryUtils.fetchNewsData(mUrl);
        return news;
    }
}
