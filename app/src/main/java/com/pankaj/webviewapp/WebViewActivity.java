package com.pankaj.webviewapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;

import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;


public class WebViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private WebView webView;
    public static final String WEB_URL = "https://www.google.com/";

    private CustomizeDialog mCustomizeDialog;

    public static final int INPUT_FILE_REQUEST_CODE = 1;

    private ValueCallback<Uri[]> mFilePathCallback;

    private String mCameraPhotoPath;
    private ArrayList<String> imagesEncodedList = new ArrayList<>();
    private String imageEncoded;

    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        setupViews();
    }

    private void setupViews() {
        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbar = findViewById(R.id.toolbar);
        webView = findViewById(R.id.webView);


        mCustomizeDialog = new CustomizeDialog(this);
        mCustomizeDialog.setTitle("");
        mCustomizeDialog.setCancelable(false);

        WebViewHelper.getInstance().webViewSetup(webView);
        showProgressBar();
    }

    private void setWebViewView() {
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                askPermission(request);
                super.onPermissionRequest(request);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                toolbarTitle.setText(title);
            }

            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                showFileChooser( filePathCallback);

                return true;
            }

        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                return false;
            }

            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                hideProgressBar();
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                hideProgressBar();
                showErrorIfNetOff();
                super.onReceivedError(view, request, error);
            }

        });
       webView.loadUrl(WEB_URL);
    }

    private void askPermission(PermissionRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Dexter.withActivity(this)
                    .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                // do you work now
                            }

                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permenantly, navigate user to app settings
                            }
                        }


                        @Override
                        public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .onSameThread()
                    .check();
        }

    }

    private void showFileChooser(ValueCallback<Uri[]> filePathCallback) {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
        }
        mFilePathCallback = filePathCallback;

        // For image capture
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent = WebViewHelper.getInstance()
                            .getFileIntent(WebViewActivity.this,
                                            mCameraPhotoPath, takePictureIntent);
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        contentSelectionIntent.setType("*/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        // For upload document.
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose an action");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
    }

    private void showErrorIfNetOff() {
        if (!Utility.isOnline(this)){
            Utility.showAlertMessage(this, "", getString(R.string.app_name));
        }
    }

    private void hideProgressBar(){
        mCustomizeDialog.dismissDialog();
    }

    private void showProgressBar(){
        mCustomizeDialog.showDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Utility.isOnline(this)){
            Utility.showAlertMessage(this, "", getString(R.string.app_name));
            hideProgressBar();
        }else {
            setWebViewView();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;
        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();

                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                } else {
                    results = onReceivedFileData(data, results);
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    private Uri[] onReceivedFileData(Intent data, Uri[] results) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        imagesEncodedList = new ArrayList<String>();
        if (data.getData() != null) {
            results = WebViewHelper.getInstance()
                                .getFilterData(this, imageEncoded, data, results,
                                                filePathColumn);
        } else {
            if (data.getClipData() != null) {
                results = WebViewHelper
                                    .getInstance()
                                    .getFilterClipData(this, imageEncoded,
                                                imagesEncodedList, data, results, filePathColumn);
            }
        }
        return results;
    }


}
