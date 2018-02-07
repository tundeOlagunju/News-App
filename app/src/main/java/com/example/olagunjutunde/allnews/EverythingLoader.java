package com.example.olagunjutunde.allnews;

/**
 * Created by OLAGUNJU TUNDE on 12/10/2017.
 */

import android.support.v4.content.AsyncTaskLoader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;


import com.example.olagunjutunde.allnews.News;
import com.example.olagunjutunde.allnews.QueryUtils;

import java.util.List;



/**
 * Created by OLAGUNJU TUNDE on 12/6/2017.
 */

public class EverythingLoader extends AsyncTaskLoader<List<News>> {

    private String mUrl;
    private static  String LOG_TAG = EverythingLoader.class.getName();

    public  EverythingLoader(Context context,String url){

        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        Log.i(LOG_TAG,"Test:EveryLoadInBack() called:");
        if (mUrl == null){
            return null;
        }

        List<News> news =  Utils.fetchNewsData(mUrl);
        return news;
    }
}
