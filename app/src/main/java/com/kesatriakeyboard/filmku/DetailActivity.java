package com.kesatriakeyboard.filmku;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kesatriakeyboard.filmku.adapter.ReviewsAdapter;
import com.kesatriakeyboard.filmku.data.MoviesApiService;
import com.kesatriakeyboard.filmku.data.RestClient;
import com.kesatriakeyboard.filmku.model.Movie;
import com.kesatriakeyboard.filmku.model.Review;
import com.kesatriakeyboard.filmku.model.ReviewsResults;
import com.kesatriakeyboard.filmku.model.Video;
import com.kesatriakeyboard.filmku.model.VideosResults;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity  {

    public static final String EXTRA_MOVIE_ID = "movieId";

    ReviewsAdapter mAdapter;

    ImageView backdrop;
    ImageView poster;
    TextView date;
    TextView time;
    TextView star;
    TextView description;
    RecyclerView mRecyclerReview;

    CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (getIntent().hasExtra(EXTRA_MOVIE_ID)) {
            int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, 0);
            callMoviesService(movieId);
        } else {
            throw new IllegalArgumentException("Detail activity must receive a integer identification");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarLayout = findViewById(R.id.toolbar_layout);

        date = findViewById(R.id.movie_date);
        time = findViewById(R.id.movie_time);
        star = findViewById(R.id.movie_star);

        backdrop = findViewById(R.id.backdrop);
        description = findViewById(R.id.movie_description);
        poster = findViewById(R.id.movie_poster);

        mAdapter = new ReviewsAdapter(this);

        mRecyclerReview = findViewById(R.id.recycler_reviews);
        mRecyclerReview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerReview.setAdapter(mAdapter);
        mRecyclerReview.setHasFixedSize(true);

//        title.setText(mMovie.getTitle());
//        description.setText(mMovie.getDescription());
//        Picasso.with(this)
//                .load(mMovie.getPoster())
//                .into(poster);
//        Picasso.with(this)
//                .load(mMovie.getBackdrop())
//                .into(backdrop);


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void callMoviesService(int movieId) {
        callMovieServiceDetail(movieId);
        callMovieServiceVideos(movieId);
        callMovieServiceReviews(movieId);
    }

    private void callMovieServiceDetail(int movieId){
        MoviesApiService movies = RestClient.retrofit.create(MoviesApiService.class);
        Call<Movie> call = movies.getDetails(movieId);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if (response.isSuccessful()) {
                    setValue(response.body());
                } else {
                    Log.e("DETAIL", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, Throwable t) {
                Log.e("DETAIL", t.getMessage());
            }
        });
    }

    private void callMovieServiceVideos(int movieId) {
        MoviesApiService movies = RestClient.retrofit.create(MoviesApiService.class);
        Call<VideosResults> call = movies.getVideos(movieId);

        call.enqueue(new Callback<VideosResults>() {
            @Override
            public void onResponse(Call<VideosResults> call, Response<VideosResults> response) {
                if (response.isSuccessful()) {
                    VideosResults results = response.body();
                    List<Video> videos = results.getVideos();
                    Video firstVideo = videos.get(0);
                    setTrailer(firstVideo.getKey(), firstVideo.getName());
                } else {
                    Log.e("DETAIL", response.message());
                }
            }

            @Override
            public void onFailure(Call<VideosResults> call, Throwable t) {
                Log.e("DETAIL", t.getMessage());
            }
        });
    }

    private void callMovieServiceReviews(int movieId) {
        MoviesApiService movies = RestClient.retrofit.create(MoviesApiService.class);
        Call<ReviewsResults> call = movies.getReviews(movieId);

        call.enqueue(new Callback<ReviewsResults>() {
            @Override
            public void onResponse(Call<ReviewsResults> call, Response<ReviewsResults> response) {
                if (response.isSuccessful()) {
                    ReviewsResults results = response.body();
                    List<Review> reviews = results.getReviews();
                    if (reviews.size() > 0) {
                        mAdapter.setReviewList(reviews);
                    } else {
                        Review r = new Review();
                        r.setContent("There are no reviews yet");
                        reviews.add(r);
                    }
                } else {
                    Log.e("DETAIL", response.message());
                }
            }

            @Override
            public void onFailure(Call<ReviewsResults> call, Throwable t) {
                Log.e("DETAIL", t.getMessage());
            }
        });
    }

    private void setValue(Movie movie) {
        toolbarLayout.setTitle(movie.getTitle());

        date.setText(movie.getReleaseDate().substring(0,4));
        time.setText(movie.getRuntime().toString() + "min");
        star.setText(movie.getVoteAverage().toString() + "/10");

        description.setText(movie.getOverview());
        Picasso.with(this)
                .load(movie.getPosterPath())
                .into(poster);
        Picasso.with(this)
                .load(movie.getBackdropPath())
                .into(backdrop);
    }

    private void setTrailer(final String youtubeKey, String title){
        ImageView movieTrailer = findViewById(R.id.movie_trailer);
        Picasso.with(this)
                .load("https://img.youtube.com/vi/"+youtubeKey+"/hqdefault.jpg")
                .into(movieTrailer);
        ((TextView) findViewById(R.id.movie_trailer_text)).setText(title);

        movieTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeKey));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + youtubeKey));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });
    }

}
