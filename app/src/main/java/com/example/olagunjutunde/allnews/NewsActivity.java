package com.example.olagunjutunde.allnews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.app.Activity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.key;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.olagunjutunde.allnews.QueryUtils.LOG_TAG;

public class NewsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TOP_HEADLINES_ENDPOINT_BASE_URL = "https://newsapi.org/v2/top-headlines";
    private static final String EVERYTHING_ENDPOINT_BASE_URL = "https://newsapi.org/v2/everything?q=";

    private static final int TOP_HEADLINES_LOADER_ID = 1;
    private static final int EVERYTHING_LOADER_ID = 2;

    private  static  String query ="";
   private  static final String endpoints="&language=en&sortBy=relevance,popularity&page=2";
    private static final String API_KEY ="&apiKey=e845ab6724384d848f9fa8517dcb4069";


    private NewsAdapter mAdapter;
    private EverythingAdapter everythingAdapter;
    private LoaderManager loaderManager;
    private Toolbar toolbar;
    private static String newsSource;
    private TextView mEmptyStateTextView;
    private  boolean isConnected;
   private  ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_news);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CoordinatorLayout coordinatorLayout =(CoordinatorLayout)findViewById(R.id.coordinator_layout);

        listView = (ListView) findViewById(R.id.list_view);

        mEmptyStateTextView =(TextView)findViewById(R.id.empty_state_text);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");
        mEmptyStateTextView.setTypeface(typeface);




        listView.setEmptyView(mEmptyStateTextView);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        listView.setAdapter(mAdapter);

        isConnected = checkInternetConnectivity();

        if(isConnected) {

            setProgressLoading(true);

            loaderManager = getSupportLoaderManager();

            loaderManager.initLoader(TOP_HEADLINES_LOADER_ID, null, topHeadlinesLoaderListener);
        }else{

            mEmptyStateTextView.setText("No Internet Connection.Please Check your settings and try again later");

            Snackbar.make(coordinatorLayout,"No Connection",Snackbar.LENGTH_LONG)
                    .show();
        }

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_source_key)))
                {
            // Clear the ListView as a new query will be kicked off
            mAdapter.clear();

                    if(isConnected) {
                        setProgressLoading(true);
                        listView.setVisibility(View.GONE);
                        loaderManager = getSupportLoaderManager();
                        loaderManager.destroyLoader(TOP_HEADLINES_LOADER_ID);
                        loaderManager.destroyLoader(EVERYTHING_LOADER_ID);
                        loaderManager.restartLoader(TOP_HEADLINES_LOADER_ID, null, topHeadlinesLoaderListener);
                    }
                    else {
                        setProgressLoading(false);

                        mEmptyStateTextView.setText("There was a problem loading this content. Please" +
                                "check your internet connection and try again later.");
                    }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {


        getMenuInflater().inflate(R.menu.menu,menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
      final SearchView searchView =(SearchView)  MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search topics and articles");




        //Note:
        //MenuItemCompat.OnActionExpandListener interface has a static implementation and
        //is not an instance method so it is called on its class



        MenuItemCompat.setOnActionExpandListener(searchItem,new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(final MenuItem item) {
                NewsActivity.this.setItemsVisibility(menu, searchItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(final MenuItem item) {
                NewsActivity.this.setItemsVisibility(menu, searchItem, true);
                return true;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {

                hideKeyboard(NewsActivity.this);
                listView.setVisibility(View.GONE);
               // mEmptyStateTextView.setVisibility(View.GONE);

                query = !TextUtils.isEmpty(newText) ? newText : null;


                if(isConnected) {

                    setProgressLoading(true);
                    mEmptyStateTextView.setVisibility(View.GONE);
                    loaderManager = getSupportLoaderManager();
                    loaderManager.destroyLoader(TOP_HEADLINES_LOADER_ID);
                    loaderManager.destroyLoader(EVERYTHING_LOADER_ID);
                    loaderManager.restartLoader(EVERYTHING_LOADER_ID, null, EverythingLoaderListener);
                } else{
                    setProgressLoading(false);
                      mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText("There was a problem loading this content. Please" +
                            " check your internet connection and try again later.");


                }
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!isConnected) {
                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
                    Snackbar.make(coordinatorLayout, "No internet found", Snackbar.LENGTH_SHORT).show();
                }
                return true;
            }
        });


        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.preferences){

            Intent intent = new Intent(NewsActivity.this,PreferenceActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.about){

            Intent intent = new Intent(NewsActivity.this,AboutActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStop() {
        super.onStop();

        mAdapter.clear();

        loaderManager = getSupportLoaderManager();
        loaderManager.destroyLoader(TOP_HEADLINES_LOADER_ID);
        loaderManager.destroyLoader(EVERYTHING_LOADER_ID);

    }

    private LoaderManager.LoaderCallbacks<List<News>> topHeadlinesLoaderListener = new LoaderManager.LoaderCallbacks<List<News>>() {
        @Override
        public Loader<List<News>> onCreateLoader(int id, Bundle args) {
              setProgressLoading(true);
            mEmptyStateTextView.setVisibility(View.GONE);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewsActivity.this);

             newsSource = sharedPreferences.getString(getString(R.string.settings_source_key)
                    ,getString(R.string.settings_source_default));



            Uri baseUri = Uri.parse(TOP_HEADLINES_ENDPOINT_BASE_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();

            uriBuilder.appendQueryParameter("sources",newsSource);
            uriBuilder.appendQueryParameter("apiKey","e845ab6724384d848f9fa8517dcb4069");


            return new NewsLoader(NewsActivity.this, uriBuilder.toString());
        }


        @Override
        public void onLoadFinished(Loader<List<News>> loader, List<News> data) {

            setProgressLoading(false);

            mEmptyStateTextView.setText("Problem loading this content");

            listView.setVisibility(View.VISIBLE);

            newsSource = newsSource.replace("-"," ");

            if(newsSource.contains("news")){
              newsSource  =  newsSource.replace("news","");
            }

            newsSource = newsSource.toUpperCase();

            toolbar.setTitle(newsSource + " News");
            mAdapter.clear();

            ListView listView = (ListView) findViewById(R.id.list_view);

            if (data != null) {

                mAdapter = new NewsAdapter(NewsActivity.this, data);
                listView.setAdapter(mAdapter);

            }


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    News currentNews = mAdapter.getItem(position);

                    Intent intent = new Intent(NewsActivity.this,DetailsActivity.class);

                    intent.putExtra("url",currentNews.getUrl());

                    startActivity(intent);
                }
            });
        }

        @Override
        public void onLoaderReset(Loader<List<News>> loader) {

            mAdapter.clear();
        }
    };

    private LoaderManager.LoaderCallbacks<List<News>> EverythingLoaderListener = new LoaderManager.LoaderCallbacks<List<News>>() {
        @Override
        public Loader<List<News>> onCreateLoader(int id, Bundle args) {

            setProgressLoading(true);

            mEmptyStateTextView.setVisibility(View.GONE);
            Log.i(LOG_TAG,"Test:EveryOnCreateLoader called:");
            return new EverythingLoader(NewsActivity.this, EVERYTHING_ENDPOINT_BASE_URL+query+endpoints+API_KEY);

        }

        @Override
        public void onLoadFinished(Loader<List<News>> loader, List<News> data) {


            setProgressLoading(false);

            mEmptyStateTextView.setText("No results found for "+ "\""+query +"\"");

            listView.setVisibility(View.VISIBLE);

            toolbar.setTitle(query);

            ListView listView = (ListView) findViewById(R.id.list_view);

            if (data != null) {

                everythingAdapter = new EverythingAdapter(NewsActivity.this, data);
                listView.setAdapter(everythingAdapter);




                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        News currentNews = everythingAdapter.getItem(position);

                        Intent intent = new Intent(NewsActivity.this,DetailsActivity.class);

                        intent.putExtra("url",currentNews.getUrl());
                        intent.putExtra("query",query);

                        startActivity(intent);
                    }
                });

            }
        }

        @Override
        public void onLoaderReset(Loader<List<News>> loader) {

            if(everythingAdapter != null)
            everythingAdapter.clear();
        }
    };



    /**
     * Helper Method to check the connectivity status
     */
    private Boolean checkInternetConnectivity(){

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }



    /**
     *   Helper Method to hide the Keyboard
     **/
    private static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    /**
     * Helper Method to hide or show the progress bar
     */
    private void setProgressLoading(boolean isLoading){
        View loadingIndicator = findViewById(R.id.loading_indicator);
        if (isLoading)
        {
            // show the loading indicator
            loadingIndicator.setVisibility(View.VISIBLE);
        }
        else{
            //hide the loading indicator
            loadingIndicator.setVisibility(View.GONE);
        }
    }




    private void setItemsVisibility(final Menu menu, final MenuItem exception,
                                    final boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception)
                item.setVisible(visible);
        }
    }

}