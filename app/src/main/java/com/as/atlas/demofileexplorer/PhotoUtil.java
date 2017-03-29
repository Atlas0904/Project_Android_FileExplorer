package com.as.atlas.demofileexplorer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


/**
 * Created by atlas_huang on 2017/3/29.
 */

public class PhotoUtil {

    public final static String TAG = PhotoUtil.class.getSimpleName();

    private PhotoUtil() {}

    public static void FetchAll(Context context) {
        // content:// style URI for the "primary" external storage volume
        Uri uriInternalImage = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        Uri uriExternalImage = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        FetchImageByUri(context, uriInternalImage);
        FetchImageByUri(context, uriExternalImage);
    }

    public static void FetchImageByUri(Context context, Uri uri) {

        // which image properties are we querying
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        };

        // Make the query.
        Cursor cur = context.getContentResolver().query(uri,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        Log.i(TAG," query count=" + cur.getCount());

        if (cur.moveToFirst()) {
            String bucket;
            String date;
            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);

            do {
                // Get the field values
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);

                // Do something with the values.
                Log.i(TAG, " bucket=" + bucket
                        + "  date_taken=" + date);
            } while (cur.moveToNext());

        }
    }
}
