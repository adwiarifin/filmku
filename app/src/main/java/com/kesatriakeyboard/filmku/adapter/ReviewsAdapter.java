package com.kesatriakeyboard.filmku.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kesatriakeyboard.filmku.R;
import com.kesatriakeyboard.filmku.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a_a04 on 09/04/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> mReviewList;
    private Context mContext;

    public ReviewsAdapter(@NonNull Context context) {
        this.mContext = context;
        this.mReviewList = new ArrayList<>();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = mReviewList.get(position);

        holder.reviewAuthor.setText(review.getAuthor());
        holder.reviewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewList != null ? mReviewList.size() : 0;
    }

    public void setReviewList(List<Review> reviewList) {
        this.mReviewList.clear();
        this.mReviewList.addAll(reviewList);
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        final TextView reviewAuthor;
        final TextView reviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewAuthor = itemView.findViewById(R.id.movie_detail_review_author);
            reviewContent = itemView.findViewById(R.id.movie_detail_review_content);
        }
    }
}
