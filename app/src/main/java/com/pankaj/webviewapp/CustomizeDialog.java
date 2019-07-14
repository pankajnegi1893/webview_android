package com.pankaj.webviewapp;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;




public class CustomizeDialog extends Dialog implements OnClickListener {
    Button okButton;
    Context mContext;
    TextView mTitle = null;
    TextView mMessage = null;
    View v = null;
    Button cancelButton;
    ProgressBar progressBar;
    ImageView animationTarget;
    Animation animation;

    public CustomizeDialog(Context context) {
        super(context);
        mContext = context;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the mTitle */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.custom_dialog_box);
        v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        mTitle = (TextView) findViewById(R.id.dialogTitle);
        mMessage = (TextView) findViewById(R.id.dialogMessage);
        /* okButton = (Button) findViewById(R.id.OkButton);
          cancelButton = (Button) findViewById(R.id.cancelButton);*/
        //cancelButton.setOnClickListener(this);
        //okButton.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        animation = AnimationUtils.loadAnimation(context, R.anim.rotate_around_center_point);
        //progressBar.startAnimation(animation);
    }

    public void showDialog(){
        progressBar.startAnimation(animation);
        this.show();
    }

    public void dismissDialog(){
        this.dismiss();
    }


    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        mTitle.setText(mContext.getResources().getString(titleId));
    }

    /**
     * Set the message text for this dialog's window.
     *
     * @param message - The new message to display in the title.
     */
    public void setMessage(CharSequence message) {
        mMessage.setText(message);
        mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    /**
     * Set the message ID
     *
     * @param messageId
     */
    public void setMessage(int messageId) {
        mMessage.setText(mContext.getResources().getString(messageId));
        mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }
}


