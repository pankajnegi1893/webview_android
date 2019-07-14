package com.pankaj.webviewapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WebViewHelper {
    private static final WebViewHelper ourInstance = new WebViewHelper();

    public static WebViewHelper getInstance() {
        return ourInstance;
    }

    private WebViewHelper() {
    }

    public void webViewSetup(WebView webView){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().getAllowUniversalAccessFromFileURLs();
    }

    /**
     * More info this method can be found at
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @return
     * @throws IOException
     */
    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    public Uri[] getFilterData(Context context, String imageEncoded, Intent data, Uri[] results, String[] filePathColumn) {
        Uri mImageUri = data.getData();

        // Get the cursor
        Cursor cursor = context.getContentResolver().query(mImageUri,
                filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        imageEncoded = cursor.getString(columnIndex);
        cursor.close();

        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
        mArrayUri.add(mImageUri);
        for (int i=0;i<mArrayUri.size();i++) {
            results = new Uri[]{mArrayUri.get(i)};
        }
        return results;
    }

    public Uri[] getFilterClipData(Context context, String imageEncoded, ArrayList<String> imagesEncodedList,
                                    Intent data, Uri[] results, String[] filePathColumn) {
        ClipData mClipData = data.getClipData();
        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
        for (int i = 0; i < mClipData.getItemCount(); i++) {
            ClipData.Item item = mClipData.getItemAt(i);
            Uri uri = item.getUri();
            mArrayUri.add(uri);
            // Get the cursor
            Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imageEncoded = cursor.getString(columnIndex);
            imagesEncodedList.add(imageEncoded);
            cursor.close();

        }
        for (int m=0;m<mArrayUri.size();m++){
            if (m==0){
                results = new Uri[mArrayUri.size()];
            }
            results[m]=mArrayUri.get(m);
        }
        return results;
    }

    public Intent getFileIntent(Activity activity, String mCameraPhotoPath,
                                    Intent takePictureIntent) {
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = WebViewHelper.getInstance().createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Request", "Unable to create Image File", ex);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }
        return takePictureIntent;
    }

}
