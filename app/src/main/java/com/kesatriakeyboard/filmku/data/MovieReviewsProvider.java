package com.kesatriakeyboard.filmku.data;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.compat.BuildConfig;
import android.text.TextUtils;
import android.util.Log;

import com.kesatriakeyboard.filmku.data.MovieReviewsContract.MovieEntities;
import com.kesatriakeyboard.filmku.data.MovieReviewsContract.Movies;
import com.kesatriakeyboard.filmku.data.MovieReviewsContract.Reviews;
import com.kesatriakeyboard.filmku.model.Movie;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a_a04 on 09/04/17.
 */

public class MovieReviewsProvider extends ContentProvider {

    private static final int MOVIE_LIST = 1;
    private static final int MOVIE_ID = 2;
    private static final int REVIEW_LIST = 3;
    private static final int REVIEW_ID = 4;
    private static final int ENTITY_LIST = 5;
    private static final int ENTITY_ID = 6;

    private static final UriMatcher URI_MATCHER;

    private MovieReviewsOpenHelper mHelper = null;
    private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<Boolean>();

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(MovieReviewsContract.AUTHORITY, "movies", MOVIE_LIST);
        URI_MATCHER.addURI(MovieReviewsContract.AUTHORITY, "movies/#", MOVIE_ID);
        URI_MATCHER.addURI(MovieReviewsContract.AUTHORITY, "reviews", REVIEW_LIST);
        URI_MATCHER.addURI(MovieReviewsContract.AUTHORITY, "reviews/#", REVIEW_ID);
        URI_MATCHER.addURI(MovieReviewsContract.AUTHORITY, "entities", ENTITY_LIST);
        URI_MATCHER.addURI(MovieReviewsContract.AUTHORITY, "entities/#", ENTITY_ID);
    }

    @Override
    public boolean onCreate() {
        mHelper = new MovieReviewsOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        doAnalytics(uri, "query");

        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false;
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                builder.setTables(DbSchema.TBL_MOVIES);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Movies.SORT_ORDER_DEFAULT;
                }
                break;
            case MOVIE_ID:
                builder.setTables(DbSchema.TBL_MOVIES);
                builder.appendWhere(Movies._ID + " = " + uri.getLastPathSegment());
                break;
            case REVIEW_LIST:
                builder.setTables(DbSchema.TBL_REVIEWS);
                break;
            case REVIEW_ID:
                builder.setTables(DbSchema.TBL_REVIEWS);
                builder.appendWhere(Reviews._ID + " = " + uri.getLastPathSegment());
                break;
            case ENTITY_LIST:
                builder.setTables(DbSchema.LEFT_OUTER_JOIN_STATEMENT);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = MovieEntities.SORT_ORDER_DEFAULT;
                }
                useAuthorityUri = true;
                break;
            case ENTITY_ID:
                builder.setTables(DbSchema.LEFT_OUTER_JOIN_STATEMENT);
                builder.appendWhere(DbSchema.TBL_MOVIES + "." + Movies._ID + " = " + uri.getLastPathSegment());
                useAuthorityUri = true;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            logQuery(builder, projection, selection, sortOrder);
        } else {
            logQueryDeprecated(builder, projection, selection, sortOrder);
        }

        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        uri = useAuthorityUri ? MovieReviewsContract.CONTENT_URI : uri;
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                return Movies.CONTENT_TYPE;
            case MOVIE_ID:
                return Movies.CONTENT_MOVIE_TYPE;
            case REVIEW_LIST:
                return Reviews.CONTENT_TYPE;
            case REVIEW_ID:
                return Reviews.CONTENT_REVIEW_TYPE;
            case ENTITY_LIST:
                return MovieEntities.CONTENT_TYPE;
            case ENTITY_ID:
                return MovieEntities.CONTENT_ENTITY_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        doAnalytics(uri, "insert");

        if (URI_MATCHER.match(uri) != MOVIE_LIST && URI_MATCHER.match(uri) != REVIEW_LIST) {
            throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }

        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (URI_MATCHER.match(uri) == MOVIE_LIST) {
            long id = db.insert(DbSchema.TBL_MOVIES, null, values);
            return getUriForId(id, uri);
        } else {
            long id = db.insertWithOnConflict(DbSchema.TBL_REVIEWS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            return getUriForId(id, uri);
        }
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                getContext().getContentResolver().notifyChange(itemUri, null);
            }
            return itemUri;
        }

        throw new SQLException("Problem while inserting into uri: " + uri);
    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        doAnalytics(uri, "delete");

        SQLiteDatabase db = mHelper.getWritableDatabase();
        int delCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                delCount = db.delete(DbSchema.TBL_MOVIES, selection, selectionArgs);
                break;
            case MOVIE_ID:
                String idString = uri.getLastPathSegment();
                String where = Movies._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(DbSchema.TBL_MOVIES, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (delCount > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        doAnalytics(uri, "update");

        SQLiteDatabase db = mHelper.getWritableDatabase();
        int updateCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                updateCount = db.update(DbSchema.TBL_MOVIES, values, selection, selectionArgs);
                break;
            case MOVIE_ID:
                String idString = uri.getLastPathSegment();
                String where = Movies._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(DbSchema.TBL_MOVIES, values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (updateCount > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        mIsInBatchMode.set(true);

        db.beginTransaction();
        try {
            final ContentProviderResult[] retResult = super.applyBatch(operations);
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(MovieReviewsContract.CONTENT_URI, null);
            return retResult;
        } finally {
            mIsInBatchMode.remove();
            db.endTransaction();
        }
    }

    private void logQuery(SQLiteQueryBuilder builder, String[] projection, String selection, String sortOrder) {
        if (BuildConfig.DEBUG) {
            Log.v("MovieReviewsProvider", "query: " + builder.buildQuery(projection, selection, null, null, sortOrder, null));
        }
    }

    private void logQueryDeprecated(SQLiteQueryBuilder builder, String[] projection, String selection, String sortOrder) {
        if (BuildConfig.DEBUG) {
            Log.v("MovieReviewsProvider", "query: " + builder.buildQuery(projection, selection, null, null, null, sortOrder, null));
        }
    }

    private void doAnalytics(Uri uri, String event) {
        if (BuildConfig.DEBUG) {
            Log.v("MovieReviewsProvider", event + "->" + uri);
            Log.v("MovieReviewsProvider", "caller: " + detectCaller());
        }
    }

    private String detectCaller() {
        int pid = Binder.getCallingPid();
        return getProcessNameFromPid(pid);
    }

    private String getProcessNameFromPid(int givenPid) {
        ActivityManager am = (ActivityManager) getContext().getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listAppInfo = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo ai : listAppInfo) {
            if (ai.pid == givenPid) {
                return ai.processName;
            }
        }

        List<ActivityManager.RunningServiceInfo> serviceInfo = am.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo si : serviceInfo) {
            if (si.pid == givenPid) {
                return si.process;
            }
        }

        return null;
    }
}
