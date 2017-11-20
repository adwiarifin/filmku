package com.kesatriakeyboard.filmku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.kesatriakeyboard.filmku.adapter.MoviesAdapter;
import com.kesatriakeyboard.filmku.data.MoviesApiService;
import com.kesatriakeyboard.filmku.data.RestClient;
import com.kesatriakeyboard.filmku.model.MoviesResults;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private final String TAG = MainActivity.class.getSimpleName();

    private final String TYPE_POPULAR = "popular";
    private final String TYPE_UPCOMING = "upcoming";
    private final String TYPE_TOP_RATED = "top-rated";
    private final String TYPE_NOW_PLAYING = "now-playing";

    private MoviesAdapter mAdapter;
    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name) + ": Popular");
        setSupportActionBar(toolbar);

        mAdapter = new MoviesAdapter(this, this);
        progressBar = findViewById(R.id.pb_loading_indicator);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        callMoviesService("popular");
    }

    private void callMoviesService(String type) {
        MoviesApiService movies = RestClient.retrofit.create(MoviesApiService.class);
        Call<MoviesResults> call = null;
        showLoadingBar();

        switch (type) {
            case TYPE_POPULAR:
                call = movies.getPopular();
                break;
            case TYPE_TOP_RATED:
                call = movies.getTopRated();
                break;
            case TYPE_NOW_PLAYING:
                call = movies.getNowPlaying();
                break;
            case TYPE_UPCOMING:
                call = movies.getUpcoming();
                break;
            default:
                call = null;
                break;
        }

        call.enqueue(new Callback<MoviesResults>() {

            @Override
            public void onResponse(Call<MoviesResults> call, Response<MoviesResults> response) {
                if (response.isSuccessful()) {
                    showMovieList();
                    mAdapter.setMovieList(response.body().getMovies());
                } else {
                    Log.e(TAG, response.message() + " : " + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<MoviesResults> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void showLoadingBar() {
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showMovieList() {
        progressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(int movieId) {
        Intent movieDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        movieDetailIntent.putExtra(DetailActivity.EXTRA_MOVIE_ID, movieId);
        startActivity(movieDetailIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_now_playing:
                callMoviesService(TYPE_NOW_PLAYING);
                toolbar.setTitle(getString(R.string.app_name) + ": Now Playing");
                break;
            case R.id.action_popular:
                callMoviesService(TYPE_POPULAR);
                toolbar.setTitle(getString(R.string.app_name) + ": Popular");
                break;
            case R.id.action_top_rated:
                callMoviesService(TYPE_TOP_RATED);
                toolbar.setTitle(getString(R.string.app_name) + ": Top Rated");
                break;
            case R.id.action_upcoming:
                callMoviesService(TYPE_UPCOMING);
                toolbar.setTitle(getString(R.string.app_name) + ": Upcoming");
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
