package com.tvi.nuoica.dialogs;

import java.util.Random;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvi.nuoica.R;

public class MessageDialog extends BaseDialog {

    public Button mbtSkip;
    public TextView mtvTitle;
    public TextView mtvGuide;
    public ImageView mivGuide;

    public MessageDialog(Context context, int image, String title,
            String message) {
        super(context);
        mivGuide.setImageResource(image);
        int[] colors = { 0, 2, 3, 4, 5, 6, 7, 9 };
        Random random = new Random();
        mivGuide.setBackgroundResource(R.drawable.button_aqua
                + colors[random.nextInt(colors.length)]);
        mtvTitle.setText(title);
        mtvGuide.setText(message);
    }

    @Override
    public int getViewId() {
        return R.layout.dialog_message;
    }

    @Override
    public void bindView() {
        mtvGuide = (TextView) findViewById(R.id.tv_guide);
        mtvTitle = (TextView) findViewById(R.id.tv_title);
        mivGuide = (ImageView) findViewById(R.id.iv_guide);
        mbtSkip = (Button) findViewById(R.id.bt_skip);
    }

}
