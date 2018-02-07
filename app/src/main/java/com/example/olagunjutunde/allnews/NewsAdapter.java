package com.example.olagunjutunde.allnews;

/**
 * Created by OLAGUNJU TUNDE on 12/8/2017.
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by OLAGUNJU TUNDE on 12/6/2017.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    private final static String LOG_TAG = NewsAdapter.class.getName();


    public NewsAdapter(Context context, List<News> news) {

        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            listItemView = layoutInflater.inflate(R.layout.list_item, parent, false);

        }
        News currentNews = getItem(position);

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.news_image);
        View lineView1 = listItemView.findViewById(R.id.line_view);
        View lineView2 = listItemView.findViewById(R.id.line_view2);

        Bitmap imageNews = currentNews.getImageNews();
        if(imageNews != null) {
            imageView.setImageBitmap(imageNews);
        }else {
            imageView.setVisibility(View.GONE);
            lineView1.setVisibility(View.GONE);
            lineView2.setVisibility(View.GONE);
        }



        TextView title = (TextView) listItemView.findViewById(R.id.title);
        title.setText(currentNews.getTitle());

        TextView descriptionText = (TextView) listItemView.findViewById(R.id.description);
        String newsDescription = currentNews.getDescription();
        if(!newsDescription.isEmpty()){
            descriptionText.setText(newsDescription);
        }else{
            descriptionText.setVisibility(View.GONE);
        }

        SimpleDateFormat sdf;
        String  publishedAt = currentNews.getPublishedAt();
        if(!publishedAt.equals("null")) {

            try {


                if (publishedAt.charAt(19) == '.') {
                    sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.UK);
                } else if (publishedAt.charAt(19) == '+') {
                    sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+'ss:ss", Locale.UK);
                } else {
                    sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);

                }

                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date dateObject = sdf.parse(publishedAt);


                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.UK);
                String formattedTime = timeFormat.format(dateObject);


                TextView time = (TextView) listItemView.findViewById(R.id.time);
                time.setText(formattedTime);


                SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy", Locale.UK);
                String formattedDate = dateFormat.format(dateObject);


                TextView date = (TextView) listItemView.findViewById(R.id.date);
                date.setText(formattedDate);


            } catch (ParseException e) {
                Log.e(LOG_TAG, "Parse Exception handled gracefully", e);
            }
        }
return  listItemView;
    }
}

