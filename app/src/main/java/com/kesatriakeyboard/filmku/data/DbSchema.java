package com.kesatriakeyboard.filmku.data;

import android.provider.BaseColumns;

/**
 * Created by a_a04 on 09/04/17.
 */

interface DbSchema {
    String DB_NAME = "movieitems.db";

    String TBL_MOVIES = "movies";
    String TBL_REVIEWS = "reviews";

    String COL_ID = BaseColumns._ID;
    String COL_MOVIE_ID = "movie_id";
    String COL_TITLE = "title";
    String RELEASE_DATE = "release_date";
    String RUNTIME = "runtime";
    String VOTE_AVERAGE = "vote_average";
    String OVERVIEW = "overview";
    String TRAILER = "trailer";

    String DDL_CREATE_TBL_MOVIES =
            "CREATE TABLE movies(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "movie_id INTEGER UNIQUE NOT NULL" +
                    "title TEXT, " +
                    "release_date DATE, " +
                    "runtime INTEGER, " +
                    "vote_average DOUBLE, " +
                    "overview TEXT," +
                    "trailer TEXT" +
                    ")";

    String DDL_CREATE_TBL_REVIEWS =
            "CREATE TABLE reviews(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "author TEXT, " +
                    "content TEXT, " +
                    "movie_id INTEGER NOT NULL" +
                    ")";

    String DDL_CREATE_TRIGGER_DEL_MOVIES =
            "CREATE TRIGGER delete_movies DELETE ON movies " +
                    "begin " +
                    "  delete from reviews where movie_id = old._id; " +
                    "end";

    String DDL_DROP_TBL_MOVIES = "DROP TABLE IF EXISTS movies";

    String DDL_DROP_TBL_REVIEWS = "DROP TABLE IF EXISTS reviews";

    String DDL_DROP_TRIGGER_DEL_MOVIES = "DROP TRIGGER IF EXISTS delete_movies";

    String DML_WHERE_ID_CLAUSE = "_id = ?";

    String DEFAULT_TBL_ITEMS_SORT_ORDER = "title ASC";

    String LEFT_OUTER_JOIN_STATEMENT = TBL_MOVIES + " LEFT OUTER JOIN " + TBL_REVIEWS + " ON(movies._id = reviews.movie_id) ";
}
