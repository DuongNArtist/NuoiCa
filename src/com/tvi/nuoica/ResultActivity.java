package com.tvi.nuoica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvi.nuoica.dialogs.ConfirmCallback;
import com.tvi.nuoica.dialogs.ConfirmDialog;
import com.tvi.nuoica.engine.GameSound;
import com.tvi.nuoica.entities.Ally;

public class ResultActivity extends Activity implements OnClickListener {

    public static boolean mWinner;
    private TextView mtvMessage;
    private ImageView mivAvatar;
    private Button mbtPlay;
    private Button mbtQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mtvMessage = (TextView) findViewById(R.id.tv_message);
        mivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mbtQuit = (Button) findViewById(R.id.bt_quit);
        mbtPlay = (Button) findViewById(R.id.bt_play);
        mbtQuit.setOnClickListener(this);
        mbtPlay.setOnClickListener(this);
        if (mWinner) {
            mbtPlay.setText(getString(R.string.button_play));
            final int level = GameActivity.mGamePreference.getLevel();
            mtvMessage.setText(getString(R.string.message_game_win, level));
            ((AnimationDrawable) mivAvatar.getDrawable()).start();
            GameActivity.mGameSound.playMusic(R.raw.egg);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    GameActivity.mGameSound.playMusic(R.raw.win);
                    mivAvatar.setImageResource(R.drawable.ally00 + (level - 1)
                            % Ally.NUMBER);
                }
            }, 4000);
        } else {
            mivAvatar.setImageResource(R.drawable.over);
            mbtPlay.setText(getString(R.string.button_replay));
            mtvMessage.setText(getString(R.string.message_game_over));
        }
        mivAvatar.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.anim_image));
        mtvMessage.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.anim_text));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (GameActivity.mGameSound.mMusic) {
            GameActivity.mGameSound.mMediaPlayer.pause();
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View view) {
        GameActivity.mGameSound.playSound(GameSound.click);
        view.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.anim_button));
        switch (view.getId()) {
        case R.id.bt_quit:
            onClickQuit();
            break;

        case R.id.bt_play:
            onClickPlay();
            break;

        default:
            break;
        }
    }

    private void onClickPlay() {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }

    private void onClickQuit() {
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.mtvTitle.setText(getString(R.string.title_quit_game));
        dialog.mtvMessage.setText(getString(R.string.message_quit_game));
        dialog.mConfirmCallback = new ConfirmCallback() {

            @Override
            public void onClickYes() {
                dialog.dismiss();
                System.gc();
                System.exit(0);
            }

            @Override
            public void onClickNo() {
                dialog.dismiss();
            }
        };
        dialog.show();
    }

}
