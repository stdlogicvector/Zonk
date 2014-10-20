package in.konstant.zonk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity
        extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "Zonk";
    private static final boolean DBG = false;

    private AudioManager audio;
    private MediaPlayer player;
    private Button play;
    private TextView debug;

    private boolean fixedVolume;
    private int volume, oldVolume;

    private String effect;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (DBG) Log.d(TAG, "onCreate");

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        player = MediaPlayer.create(this, R.raw.zonk);
        player.setOnCompletionListener(playEnd);

        debug = (TextView) findViewById(R.id.debug);

        setFixedVolume();
        setVolume();
        setEffect();

        play = (Button) findViewById(R.id.play);
        play.setOnClickListener(playStart);
    }

    private View.OnClickListener playStart = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (player.isPlaying()) {
                player.pause();
                player.seekTo(0);
            } else {
                if (fixedVolume) {
                    oldVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                }
            }

            player.start();
        }
    };

    private MediaPlayer.OnCompletionListener playEnd = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (fixedVolume) {
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, oldVolume, 0);
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();

        if (DBG) Log.d(TAG, "onPause");
        if (DBG) debug.append("unRegister" + System.getProperty("line.separator"));

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (DBG) Log.d(TAG, "onResume");
        if (DBG) debug.append("register" + System.getProperty("line.separator"));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        if (player != null)
            player.release();

        if (DBG) Log.d(TAG, "onDestroy");

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (DBG) Log.d(TAG, "onSharedPreferencesChanged " + key);
        if (DBG) debug.append("onSharedPreferencesChanged " + key + System.getProperty("line.separator"));

        if (key.compareTo("key_fixed_volume") == 0) {
            setFixedVolume();
        }

        if (key.compareTo("key_volume") == 0) {
            setVolume();
        }

        if (key.compareTo("key_effect") == 0) {
            setEffect();
        }
    }

    private void setFixedVolume() {
        fixedVolume = sharedPreferences.getBoolean("key_fixed_volume", false);
    }

    private void setVolume() {
        int percent = sharedPreferences.getInt("key_volume", 50);
        volume = (int) FloatMath.floor(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 100.0f * percent);
    }

    private void setEffect() {
        String effect = sharedPreferences.getString("key_effect", "");

        if (DBG) Log.d(TAG, "setEffect: " + effect);
        if (DBG) debug.append("setEffect " + effect + System.getProperty("line.separator"));

        if (effect.compareTo("Dö düm") == 0) {
            loadEffect(R.raw.zonk);
            return;
        } else
        if (effect.compareTo("Ba Dum Tss") == 0) {
            loadEffect(R.raw.badumtss);
            return;
        } else
        if (effect.compareTo("Wah wah waaah") == 0) {
            loadEffect(R.raw.wahwahwah);
            return;
        } else
        if (effect.compareTo("Bam bam baaaam") == 0) {
            loadEffect(R.raw.drama);
            return;
        } else {
            loadEffect(R.raw.zonk);
        }

    }

    private void loadEffect(int resId) {
        if (DBG) Log.d(TAG, "loadEffect: " + resId);
        if (DBG) debug.append("loadEffect " + resId + System.getProperty("line.separator"));

        AssetFileDescriptor afd = getResources().openRawResourceFd(resId);

        try {
            player.reset();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            player.prepare();
            afd.close();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Unable to load sound effect due to exception : " + e.getMessage());
        }  catch (IllegalStateException e) {
            Log.e(TAG, "Unable to load sound effect due to exception : " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Unable to load sound effect due to exception : " + e.getMessage());
        }
    }
}
