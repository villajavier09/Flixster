package com.javiervillalpando.flixster;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.javiervillalpando.flixster.adapters.MovieAdapter;
import com.javiervillalpando.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView thumbnail;
    public static final String TAG = "MovieDetailsActivity";
    public String trailer_id;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);


        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity",String.format("Showing details for '%s'",movie.getTitle()));

        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        Glide.with(this).load(movie.getBackdropPath()).into(thumbnail);


        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0? voteAverage /2.0f : voteAverage);


        String movieId = movie.getId().toString();
        String MOVIE_VIDEO_URL = "https://api.themoviedb.org/3/movie/" +movieId +"/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
        AsyncHttpClient client2 = new AsyncHttpClient();
        client2.get(MOVIE_VIDEO_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG,"onSuccess");

                JSONObject jsonObject2 = json.jsonObject;
                try{
                    JSONArray results = jsonObject2.getJSONArray("results");
                    trailer_id = results.getJSONObject(0).getString("key");

                    thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(MovieDetailsActivity.this,MovieTrailerActivity.class);
                            i.putExtra("trailerid",trailer_id);
                            startActivity(i);
                        }
                    });
                }
                catch (JSONException e) {
                    Log.e(TAG,"Hit json exception",e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG,"onFailure");

            }
        });

    }
}