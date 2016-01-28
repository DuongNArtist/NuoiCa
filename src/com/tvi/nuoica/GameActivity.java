package com.tvi.nuoica;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tvi.nuoica.dialogs.ConfirmCallback;
import com.tvi.nuoica.dialogs.ConfirmDialog;
import com.tvi.nuoica.dialogs.MessageDialog;
import com.tvi.nuoica.engine.GameBitmap;
import com.tvi.nuoica.engine.GamePreference;
import com.tvi.nuoica.engine.GameScreen;
import com.tvi.nuoica.engine.GameSound;
import com.tvi.nuoica.engine.GameView;
import com.tvi.nuoica.entities.Bullet;
import com.tvi.nuoica.entities.Fish;
import com.tvi.nuoica.entities.Food;
import com.tvi.nuoica.entities.Tank;

public class GameActivity extends Activity implements OnClickListener {

    public static int mLevel;
    public static final String ACTION = "SMS_SENT";
    public static String mRefCode;
    public static String mSmsText;
    public static String mNumber;
    public static GameActivity mInstance;
    public static GameSound mGameSound;
    public static GameBitmap mGameBitmap;
    public static GameScreen mGameScreen;
    public static GamePreference mGamePreference;

    public static TextView mtvMoney;
    public static Handler mHandler;
    private FrameLayout mflGame;
    private ImageButton mbtSound;
    private ImageButton mbtMusic;
    private ImageButton mbtPause;
    private ImageButton mbtEggs;
    private ImageButton mbtFish;
    private ImageButton mbtFood;
    private ImageButton mbtGunz;
    private ImageButton mbtShop;
    private ImageButton mbtQuit;
    private ImageButton mbtHelp;
    private TextView mtvFish;
    private TextView mtvFood;
    private TextView mtvGunz;
    private TextView mtvEggs;
    private TextView mtvLevel;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() == Activity.RESULT_OK) {
                mGameScreen.tank.money += 10000;
                Toast.makeText(context,
                        mInstance.getString(R.string.message_successfull),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context,
                        mInstance.getString(R.string.message_unsuccessfull),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_game);
        registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION));
        mInstance = this;
        mHandler = new Handler();
        mRefCode = getRefCode();
        mSmsText = getString(R.string.message_content) + " " + mRefCode;
        mNumber = mInstance.getString(R.string.provider_number);
        mflGame = (FrameLayout) findViewById(R.id.fl_game);
        mbtSound = (ImageButton) findViewById(R.id.bt_sound);
        mbtMusic = (ImageButton) findViewById(R.id.bt_music);
        mbtPause = (ImageButton) findViewById(R.id.bt_pause);
        mbtEggs = (ImageButton) findViewById(R.id.bt_eggs);
        mbtFish = (ImageButton) findViewById(R.id.bt_fish);
        mbtFood = (ImageButton) findViewById(R.id.bt_food);
        mbtGunz = (ImageButton) findViewById(R.id.bt_gunz);
        mbtShop = (ImageButton) findViewById(R.id.bt_shop);
        mbtQuit = (ImageButton) findViewById(R.id.bt_quit);
        mbtHelp = (ImageButton) findViewById(R.id.bt_help);
        mtvFish = (TextView) findViewById(R.id.tv_fish);
        mtvFood = (TextView) findViewById(R.id.tv_food);
        mtvGunz = (TextView) findViewById(R.id.tv_gunz);
        mtvEggs = (TextView) findViewById(R.id.tv_eggs);
        mtvMoney = (TextView) findViewById(R.id.tv_money);
        mtvLevel = (TextView) findViewById(R.id.tv_level);
        mbtSound.setOnClickListener(this);
        mbtMusic.setOnClickListener(this);
        mbtPause.setOnClickListener(this);
        mbtEggs.setOnClickListener(this);
        mbtFish.setOnClickListener(this);
        mbtFood.setOnClickListener(this);
        mbtGunz.setOnClickListener(this);
        mbtEggs.setOnClickListener(this);
        mbtShop.setOnClickListener(this);
        mbtQuit.setOnClickListener(this);
        mbtHelp.setOnClickListener(this);
        mtvFish.setText(GamePreference.parseMoney(Fish.PRICE));
        mLevel = mGamePreference.getLevel();
        mtvLevel.setText(getString(R.string.level, mLevel + 1));
        mtvEggs.setText(GamePreference.parseMoney(2 * (mLevel + 1)
                * Tank.EGG_PRICE));
        mGameScreen = new GameScreen(mLevel);
        updateFood();
        updateGunz();
        mflGame.addView(new GameView(this, mGameScreen), 0);
    }

    public static void updateText(final int money) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mtvMoney.setText("Xu: " + GamePreference.parseMoney(money));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        playGame();
        if (mGameScreen.tank.fighting) {
            mGameSound.playMusic(R.raw.battle);
        } else {
            mGameSound.playMusic(R.raw.tank_0 + mGameScreen.tank.levelOfTank
                    % Tank.NUMBER);
        }
        if (mGamePreference.getFirst()) {
            showHelpDialog();
            mGamePreference.setFirst(false);
        }
    }

    private void showHelpDialog() {
        String[] guides = getResources().getStringArray(R.array.message_guide);
        String[] titles = getResources().getStringArray(R.array.message_title);
        final MessageDialog guide0 = new MessageDialog(mInstance,
                R.drawable.tap, titles[0], guides[0]);
        final MessageDialog guide1 = new MessageDialog(mInstance,
                R.drawable.fish, titles[1], guides[1]);
        final MessageDialog guide2 = new MessageDialog(mInstance,
                R.drawable.food0, titles[2], guides[2]);
        final MessageDialog guide3 = new MessageDialog(mInstance,
                R.drawable.enemy, titles[3], guides[3]);
        final MessageDialog guide4 = new MessageDialog(mInstance,
                R.drawable.gun0, titles[4], guides[4]);
        final MessageDialog guide5 = new MessageDialog(mInstance,
                R.drawable.egg, titles[5], guides[5]);
        final MessageDialog guide6 = new MessageDialog(mInstance,
                R.drawable.shop, titles[6], guides[6]);
        final MessageDialog guide7 = new MessageDialog(mInstance,
                R.drawable.ic_launcher, titles[7], guides[7]);
        guide0.mbtSkip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                guide0.cancel();
                guide1.show();
            }
        });
        guide1.mbtSkip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                guide1.cancel();
                guide2.show();
            }
        });
        guide2.mbtSkip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                guide2.cancel();
                guide3.show();
            }
        });
        guide3.mbtSkip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                guide3.cancel();
                guide4.show();
            }
        });
        guide4.mbtSkip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                guide4.cancel();
                guide5.show();
            }
        });
        guide5.mbtSkip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                guide5.cancel();
                guide6.show();
            }
        });
        guide6.mbtSkip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                guide6.cancel();
                guide7.show();
            }
        });
        guide7.mbtSkip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                guide7.cancel();
                mGameScreen.tank.playing = true;
            }
        });
        guide0.show();
        mGameScreen.tank.playing = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
        if (mGameSound.mMusic) {
            mGameSound.mMediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View view) {
        mGameSound.playSound(GameSound.click);
        view.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.anim_button));
        switch (view.getId()) {

        case R.id.bt_quit:
            onClickQuitButton();
            break;

        case R.id.bt_shop:
            onClickShopButton();
            break;

        case R.id.bt_sound:
            onClickSoundButton();
            break;

        case R.id.bt_music:
            onClickMusicButton();
            break;

        case R.id.bt_pause:
            onClickPauseButton();
            break;

        case R.id.bt_eggs:
            onClickBuyEggsButton();
            break;

        case R.id.bt_fish:
            onClickBuyFishButton();
            break;

        case R.id.bt_food:
            onClickBuyFoodButton();
            break;

        case R.id.bt_gunz:
            onClickBuyGunzButton();
            break;

        case R.id.bt_help:
            onClickShowHelpButton();
            break;

        default:
            break;
        }
    }

    private void onClickShowHelpButton() {
        showHelpDialog();
    }

    private void onClickQuitButton() {
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

    private void onClickShopButton() {
        // showGetCoinDialog();
    }

    private void pauseGame() {
        mGameScreen.tank.playing = false;
        mbtPause.setImageResource(R.drawable.play);
    }

    private void playGame() {
        mGameScreen.tank.playing = true;
        mbtPause.setImageResource(R.drawable.pause);
    }

    private void onClickPauseButton() {
        if (mGameScreen.tank.playing) {
            pauseGame();
        } else {
            playGame();
        }
    }

    private void onClickBuyFishButton() {
        mGameScreen.tank.buyFish();
    }

    private void onClickBuyEggsButton() {
        if (mGameScreen.tank.buyEggs()) {
            startResultActivity(true);
        }
    }

    private void onClickBuyFoodButton() {
        if (mGameScreen.tank.buyFood()) {
            updateFood();
        }
    }

    private void onClickBuyGunzButton() {
        if (mGameScreen.tank.buyGunz()) {
            updateGunz();
        }
    }

    private void onClickMusicButton() {
        mGameSound.mMusic = !mGameSound.mMusic;
        if (mGameSound.mMusic) {
            mbtMusic.setImageResource(R.drawable.musicoff);
            if (mGameScreen.tank.fighting) {
                mGameSound.playMusic(R.raw.battle);
            } else {
                mGameSound.playMusic(R.raw.tank_0
                        + mGameScreen.tank.levelOfTank % Tank.NUMBER);
            }
        } else {
            mbtMusic.setImageResource(R.drawable.musicon);
            mGameSound.mMediaPlayer.pause();
        }
    }

    private void onClickSoundButton() {
        mGameSound.mSound = !mGameSound.mSound;
        if (mGameSound.mSound) {
            mbtSound.setImageResource(R.drawable.soundoff);
        } else {
            mbtSound.setImageResource(R.drawable.soundon);
        }
    }

    private void updateFood() {
        int myFood = mGameScreen.tank.levelOfFood;
        if (myFood < Food.PRICES.length - 1) {
            mbtFood.setImageResource(R.drawable.food0 + myFood);
            mtvFood.setText(GamePreference.parseMoney(Food.PRICES[myFood]));
        } else {
            mbtFood.setImageResource(R.drawable.star);
            mtvFood.setText(getResources().getString(R.string.max));
        }
    }

    private void updateGunz() {
        int myGunz = mGameScreen.tank.levelOfGunz;
        if (myGunz < Bullet.PRICES.length - 1) {
            mbtGunz.setImageResource(R.drawable.gun0 + myGunz);
            mtvGunz.setText(GamePreference.parseMoney(Bullet.PRICES[myGunz]));
        } else {
            mbtGunz.setImageResource(R.drawable.star);
            mtvGunz.setText(getResources().getString(R.string.max));
        }
    }

    public static void showGetCoinDialog() {
        final ConfirmDialog dialog = new ConfirmDialog(mInstance);
        dialog.mtvTitle.setText(mInstance.getString(R.string.title_get_coin));
        dialog.mtvMessage.setText(mInstance
                .getString(R.string.message_get_coin));
        dialog.mConfirmCallback = new ConfirmCallback() {

            @Override
            public void onClickYes() {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        mInstance, 0, new Intent(ACTION), 0);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(mNumber, null, mSmsText,
                        pendingIntent, null);
                dialog.dismiss();
            }

            @Override
            public void onClickNo() {
                dialog.dismiss();
            }
        };
        dialog.show();
    }

    public static String getRefCode() {
        String content = "";
        try {
            InputStream stream = mInstance.getAssets().open("refcode.txt");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            content = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void startResultActivity(boolean winner) {
        ResultActivity.mWinner = winner;
        if (winner) {
            mGamePreference.setLevel(mLevel + 1);
        }
        mInstance.startActivity(new Intent(mInstance, ResultActivity.class));
        mInstance.finish();
    }

}
