package de.fachstudie.stressapp;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.fachstudie.stressapp.db.DatabaseService;
import de.fachstudie.stressapp.networking.HttpWrapper;
import de.fachstudie.stressapp.tetris.TetrisView;

public class MainActivity extends AppCompatActivity {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private NotificationReceiver notificationReceiver;
    private LockScreenReceiver lockScreenReceiver;
    private IntentFilter filter;
    private IntentFilter filterLock;
    private TetrisView tetrisView;
    private AlertDialog gameOverDialog;
    private AlertDialog permissionDialog;
    private boolean receiversCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createGameOverDialog();
        Handler handler = createGameOverHandler();

        tetrisView = (TetrisView) findViewById(R.id.tetrisview);
        tetrisView.setHandler(handler);
        tetrisView.pauseGame();

        if (!isNLServiceRunning()) {
            createUserInfoDialog();
        } else {
            createReceivers();
        }
    }

    private void createReceivers() {
        notificationReceiver = new NotificationReceiver();
        filter = new IntentFilter();
        filter.addAction("de.fachstudie.stressapp.notification");
        registerReceiver(notificationReceiver, filter);

        lockScreenReceiver = new LockScreenReceiver();
        filterLock = new IntentFilter();
        filterLock.addAction(Intent.ACTION_SCREEN_ON);
        filterLock.addAction(Intent.ACTION_SCREEN_OFF);
        filterLock.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(lockScreenReceiver, filterLock);

        receiversCreated = true;
    }

    private boolean isNLServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer
                .MAX_VALUE)) {
            if (NotificationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void createUserInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("User Information");

        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent settingsIntent = new Intent("android.settings" +
                        ".ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(settingsIntent);
                permissionDialog.dismiss();
            }
        });

        builder.setCancelable(false);

        permissionDialog = builder.create();
        permissionDialog.show();
    }

    @NonNull
    private Handler createGameOverHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                gameOverDialog.setMessage("HIGHSCORE: " + data.getInt("highscore"));
                gameOverDialog.show();
            }
        };
    }

    private void createGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("GAME OVER");

        builder.setPositiveButton("Start new game", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                tetrisView.startNewGame();
            }
        });

        gameOverDialog = builder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", " ");
        if (!isNLServiceRunning()) {
            permissionDialog.show();
            tetrisView.pauseGame();
        } else if (!receiversCreated) {
            createReceivers();
        } else {
            tetrisView.resumeGame();
        }
    }

    @Override
    protected void onDestroy() {
        super.onPause();
        if (notificationReceiver != null) {
            unregisterReceiver(notificationReceiver);
        }

        if (lockScreenReceiver != null) {
            unregisterReceiver(lockScreenReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // no inspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case R.id.action_survey:
                Intent intent = new Intent(MainActivity.this, SurveyActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class NotificationReceiver extends BroadcastReceiver {
        DatabaseService dbService = null;

        public NotificationReceiver() {
            dbService = DatabaseService.getInstance(MainActivity.this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            dbService.saveNotification(intent);
            JSONObject event = new JSONObject();
            try {
                event.put("event", intent.getStringExtra("event"));
                event.put("application", intent.getStringExtra("application"));
                event.put("title", intent.getStringExtra("title"));
                event.put("content_length", intent.getStringExtra("content").length());
                String timestamp = dateFormat.format(new Date());
                event.put("timestamp", timestamp);
                event.put("emoticons", EmojiFrequency.getCommaSeparatedEmoticons(intent
                        .getStringExtra("content")));
            } catch (JSONException e) {
            }
            new SendTask(context).execute(event.toString());
        }
    }

    private class LockScreenReceiver extends BroadcastReceiver {
        DatabaseService dbService = null;

        public LockScreenReceiver() {
            dbService = DatabaseService.getInstance(MainActivity.this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    // Screen is on but not unlocked (if any locking mechanism present)
                    Log.i("LockScreenReceiver", "Screen is on but not unlocked");
                    dbService.saveScreenEvent("SCREEN_ON");
                    JSONObject event = new JSONObject();
                    try {
                        event.put("event", "SCREEN_ON");
                        String timestamp = dateFormat.format(new Date());
                        event.put("timestamp", timestamp);
                    } catch (JSONException e) {
                    }
                    new SendTask(context).execute(event.toString());
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    // Screen is locked
                    Log.i("LockScreenReceiver", "Screen is locked");
                    dbService.saveScreenEvent("SCREEN_LOCK");
                    JSONObject event = new JSONObject();
                    try {
                        event.put("event", "SCREEN_LOCK");
                        String timestamp = dateFormat.format(new Date());
                        event.put("timestamp", timestamp);
                    } catch (JSONException e) {
                    }
                    new SendTask(context).execute(event.toString());
                } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    // Screen is unlocked
                    Log.i("LockScreenReceiver", "Screen is unlocked");
                    dbService.saveScreenEvent("SCREEN_UNLOCKED");
                    JSONObject event = new JSONObject();
                    try {
                        event.put("event", "SCREEN_UNLOCK");
                        String timestamp = dateFormat.format(new Date());
                        event.put("timestamp", timestamp);
                    } catch (JSONException e) {
                    }
                    new SendTask(context).execute(event.toString());
                }
            }
        }
    }

    public class SendTask extends AsyncTask<String, Void, Void> {

        private final Context context;

        public SendTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... text) {
            HttpWrapper.doPost(this.context, text[0]);
            return null;
        }
    }
}
