package com.kesatriakeyboard.filmku.data;

import com.kesatriakeyboard.filmku.model.Movie;
import com.kesatriakeyboard.filmku.model.MoviesResults;
import com.kesatriakeyboard.filmku.model.ReviewsResults;
import com.kesatriakeyboard.filmku.model.VideosResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * Created by a_a04 on 08/13/17.
 */

public interface MoviesApiService {

    @GET("movie/popular")
    Call<MoviesResults> getPopular();

    @GET("movie/now_playing")
    Call<MoviesResults> getNowPlaying();

    @GET("movie/top_rated")
    Call<MoviesResults> getTopRated();

    @GET("movie/upcoming")
    Call<MoviesResults> getUpcoming();

    @GET("movie/{movie_id}")
    Call<Movie> getDetails(@Path("movie_id") int movie_id);

    @GET("movie/{movie_id}/videos")
    Call<VideosResults> getVideos(@Path("movie_id") int movie_id);

    @GET("movie/{movie_id}/reviews")
    Call<ReviewsResults> getReviews(@Path("movie_id") int movie_id);
}
