package de.fachstudie.stressapp;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.fachstudie.stressapp.db.DatabaseHelper;
import de.fachstudie.stressapp.model.StressNotification;
import de.fachstudie.stressapp.tetris.TetrisView;

public class MainActivity extends AppCompatActivity {
    private NotificationReceiver notificationReceiver;
    private LockScreenReceiver lockScreenReceiver;
    private IntentFilter filter;
    private IntentFilter filterLock;
    private TetrisView tetrisView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        tetrisView = (TetrisView) findViewById(R.id.tetrisview);

        notificationReceiver = new NotificationReceiver();
        filter = new IntentFilter();
        filter.addAction("com.test");
        registerReceiver(notificationReceiver, filter);

        lockScreenReceiver = new LockScreenReceiver();
        filterLock = new IntentFilter();
        filterLock.addAction(Intent.ACTION_SCREEN_ON);
        filterLock.addAction(Intent.ACTION_SCREEN_OFF);
        filterLock.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(lockScreenReceiver, filterLock);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {StressNotification.NotificationEntry._ID, StressNotification
                .NotificationEntry.TITLE, StressNotification.NotificationEntry.APPLICATION,
                StressNotification.NotificationEntry.TIMESTAMP, StressNotification
                .NotificationEntry.CONTENT};
        Cursor c = db.query(StressNotification.NotificationEntry.TABLE_NAME, projection, null,
                null, null, null, null);

        List<StressNotification> notificationList = new ArrayList<>();
        EmojiFrequency frequency = new EmojiFrequency();
        if (c != null && c.moveToFirst()) {
            Log.d("Count: ", c.getCount() + "");

            while (c.moveToNext()) {
                String title = c.getString(c.getColumnIndex(StressNotification.NotificationEntry
                        .TITLE));
                String content = c.getString(c.getColumnIndex(StressNotification.NotificationEntry
                        .CONTENT));
                String application = c.getString(c.getColumnIndex(StressNotification.NotificationEntry
                        .APPLICATION));
                String timeStampText = c.getString(c.getColumnIndex(StressNotification
                        .NotificationEntry
                        .TIMESTAMP));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date timeStampDate = null;
                try {
                    timeStampDate = sdf.parse(timeStampText);
                } catch (ParseException e) {
                }

                StressNotification not = new StressNotification(title, application, content,
                        timeStampDate);
                notificationList.add(not);
            }

            db.close();
            dbHelper.close();
        }

        Log.d("Notifications: ", notificationList.size() + "");
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // will be created of 1x1 pixel
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable
                    .getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onPause();
        unregisterReceiver(notificationReceiver);
        unregisterReceiver(lockScreenReceiver);
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

        switch (item.getItemId()){
            case R.id.action_survey:
                Intent intent = new Intent(MainActivity.this, SurveyActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class NotificationReceiver extends BroadcastReceiver {
        DatabaseHelper dbHelper = null;

        public NotificationReceiver() {
            dbHelper = new DatabaseHelper(MainActivity.this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            String application = intent.getStringExtra("application");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());

            Log.d("Received Notification", application);

            try {
                Drawable applicationIcon = getPackageManager().getApplicationIcon(application);
                Bitmap bitmap = drawableToBitmap(applicationIcon);
                tetrisView.setBitmap(bitmap);
            } catch (PackageManager.NameNotFoundException e) {
            }
            tetrisView.postNotification(title);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(StressNotification.NotificationEntry.TITLE, title);
            values.put(StressNotification.NotificationEntry.CONTENT, content);
            values.put(StressNotification.NotificationEntry.APPLICATION, application);
            db.insert(StressNotification.NotificationEntry.TABLE_NAME, null, values);
        }
    }

    private class LockScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    // Screen is on but not unlocked (if any locking mechanism present)
                    Log.i("LockScreenReceiver", "Screen is on but not unlocked");
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    // Screen is locked
                    Log.i("LockScreenReceiver", "Screen is locked");
                } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    // Screen is unlocked
                    Log.i("LockScreenReceiver", "Screen is unlocked");
                }
            }
        }
    }
}
