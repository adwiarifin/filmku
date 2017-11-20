package com.kesatriakeyboard.filmku.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by a_a04 on 09/04/17.
 */

public final class MovieReviewsContract {

    public static final String AUTHORITY = "com.kesatriakeyboard.filmku";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Movies implements CommonColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(MovieReviewsContract.CONTENT_URI, "movies");

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.kesatriakeyboard.filmku.movies";

        public static final String CONTENT_MOVIE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.kesatriakeyboard.filmku.movies";

        public static final String[] PROJECTION_ALL = {
                _ID,
                MOVIE_ID,
                TITLE,
                RELEASE_DATE,
                RUNTIME,
                VOTE_AVERAGE,
                OVERVIEW,
                TRAILER
        };

        public static final String SORT_ORDER_DEFAULT = TITLE + " ASC";
    }

    public static final class Reviews implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(MovieReviewsContract.CONTENT_URI, "reviews");

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.kesatriakeyboard.filmku.reviews";

        public static final String CONTENT_REVIEW_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.kesatriakeyboard.filmku.reviews";

        public static final String AUTHOR = "author";

        public static final String CONTENT = "content";

        public static final String MOVIE_ID = "movie_id";

        public static final String[] PROJECTION_ALL = {
                _ID,
                AUTHOR,
                CONTENT,
                MOVIE_ID
        };
    }

    public static final class MovieEntities implements CommonColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(MovieReviewsContract.CONTENT_URI, "entities");

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/movieitems_entities";

        public static final String CONTENT_ENTITY_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/movieitems_entities";

        public static final String _DATA = "_data";

        public static final String[] PROJECTION_ALL = {
                DbSchema.TBL_MOVIES + "." + _ID,
                MOVIE_ID,
                TITLE,
                RELEASE_DATE,
                RUNTIME,
                VOTE_AVERAGE,
                OVERVIEW,
                TRAILER,
                _DATA
        };

        public static final String SORT_ORDER_DEFAULT = TITLE + " ASC";
    }

    public static interface CommonColumns extends BaseColumns {
        public static final String MOVIE_ID = "movie_id";
        public static final String TITLE = "title";
        public static final String RELEASE_DATE = "release_date";
        public static final String RUNTIME = "runtime";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String OVERVIEW = "overview";
        public static final String TRAILER = "trailer";
    }
}
