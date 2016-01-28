package com.tvi.nuoica;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.tvi.nuoica.engine.GameBitmap;
import com.tvi.nuoica.engine.GamePreference;
import com.tvi.nuoica.engine.GameScreen;
import com.tvi.nuoica.engine.GameSound;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

    private Handler mHandler;
    private FrameLayout mflMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mflMain = (FrameLayout) findViewById(R.id.fl_main);
        getHashkey();
        mHandler = new Handler();
        new Thread(new Runnable() {

            @Override
            public void run() {
                GameActivity.mGameSound = GameSound
                        .getInstance(MainActivity.this);
                GameActivity.mGameBitmap = GameBitmap
                        .getInstance(MainActivity.this);
                GameActivity.mGamePreference = GamePreference
                        .getInstance(MainActivity.this);
                GameActivity.mGameBitmap.load();
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        mflMain.setBackgroundResource(R.drawable.game);
                    }
                });
                GameActivity.mGameSound.load();
                GameActivity.mGameScreen = new GameScreen(
                        GameActivity.mGamePreference.getLevel());
                startActivity(new Intent(MainActivity.this, GameActivity.class));
                finish();
            }
        }).start();
    }

    public String getHashkey() {
        String key = null;
        try {
            PackageInfo packageInfo;
            String packageName = getApplicationContext().getPackageName();
            packageInfo = getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            Log.e("Package Name = ", getApplicationContext().getPackageName());
            for (Signature signature : packageInfo.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                key = new String(Base64.encode(messageDigest.digest(), 0));
                Log.e("Key Hashes = ", key);
            }
        } catch (NameNotFoundException e) {
            Log.e("NameNotFoundException", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("NoSuchAlgorithmException", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return key;
    }
}
