package io.yi01.snapremote_detector;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import io.yi01.snapremote_detector.util.FFT4G;
import io.yi01.snapremote_detector.util.Pipe;

public class RecordingService extends Service {
    private static final String TAG = RecordingService.class.getName();
    public static final String ACTION_NAME = RecordingService.class.getName()+".TRIGGER";
    private static final long KEEP_ALIVE_INTERVAL_MS = 180000;//3 minutes.

    private static final int AUDIO_SAMPLE_FREQ = 44100;
    private static final int FFT_SIZE = 4096;
    private static final FFT4G FFT = new FFT4G(FFT_SIZE);

    private static final int AUDIO_BUFFER_SIZE = AudioRecord.getMinBufferSize(
            AUDIO_SAMPLE_FREQ, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
    private static final int FRAME_BUFFER_SIZE = AUDIO_BUFFER_SIZE / 2;

    private SnapRemoteSignalHandler mSignalHandler;

    private AudioRecord mRecord;
    private Pipe mPipe;
    private Pipe.Callback mPipeCallback = new Pipe.Callback() {
        @Override
        public void onDataPrepared(short[] data) {
            double[] data2 = new double[data.length];
            for(int i=0;i<data.length;i++) data2[i]=data[i];
            FFT.rdft(1, data2);


            double maxAmp=0;
            double maxHz=0;
            for(int i=0; i<data2.length/2;i++) {
                double hz=(i*AUDIO_SAMPLE_FREQ/FFT_SIZE);
                if (mSignalHandler.shouldCutOff(hz)) continue;
                double amp = Math.log10(Math.sqrt(data2[2*i]*data2[2*i]+data2[2*i+1]*data2[2*i+1]));
                if (amp>maxAmp) {
                    maxAmp=amp;
                    maxHz=hz;
                }
            }
            if (BuildConfig.DEBUG) Log.d(TAG, "> max Hz="+maxHz);

            mSignalHandler.putResult(maxHz);
        }
    };

    public static boolean start(Context context) {
        Intent intent = new Intent(context, RecordingService.class);
        return (context.startService(intent)!=null);
    }

    public static void stop(Context context) {
        Intent recServiceIntent = new Intent(context.getApplicationContext(), RecordingService.class);
        PendingIntent resume = PendingIntent.getService(context.getApplicationContext(), 3, recServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(resume);
        context.stopService(recServiceIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setupSignalHandler();
        setupKeepAlive();
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }
    }

    private void setupRecording(){
        mPipe = new Pipe(FFT_SIZE, mPipeCallback);

        mRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                AUDIO_SAMPLE_FREQ, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, AUDIO_BUFFER_SIZE);

        mRecord.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
            @Override
            public void onPeriodicNotification(AudioRecord recorder) {
                short[] data = new short[FRAME_BUFFER_SIZE];
                recorder.read(data, 0, FRAME_BUFFER_SIZE);
                mPipe.write(data);
            }

            @Override
            public void onMarkerReached(AudioRecord recorder) {
            }
        });

        mRecord.setPositionNotificationPeriod(FRAME_BUFFER_SIZE);

        mRecord.startRecording();
        short[] data = new short[FRAME_BUFFER_SIZE];
        mRecord.read(data, 0, FRAME_BUFFER_SIZE);
        mPipe.write(data);
    }

    protected String getActionName() {
        return ACTION_NAME;
    }

    protected void setupSignalHandler() {
        mSignalHandler = new SnapRemoteSignalHandler(new SnapRemoteSignalHandler.Callback() {
            @Override
            public void onAccept(String name, double[] data) {
                Intent intent = new Intent(getActionName());
                intent.putExtra("name", name);
                StringBuilder sb = new StringBuilder();
                for(double d : data) sb.append(d).append(",");
                intent.putExtra("data", sb.toString());
                if (BuildConfig.DEBUG) Log.d(TAG, "> TRIIGER["+name+"] : "+sb.toString());
                getBaseContext().sendOrderedBroadcast(intent, null);
            }

            @Override
            public void onReject() {
            }
        });
    }

    private void setupKeepAlive() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent recServiceIntent = new Intent(getApplicationContext(), RecordingService.class);
        PendingIntent resume = PendingIntent.getService(getApplicationContext(), 3, recServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(resume);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.uptimeMillis()+KEEP_ALIVE_INTERVAL_MS, KEEP_ALIVE_INTERVAL_MS, resume);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);

        if (mRecord!=null) {
            mRecord.release();
            mRecord = null;
        }

        setupRecording();

        return ret;
    }

    @Override
    public void onDestroy() {
        if (mRecord!=null) {
            mRecord.release();
            mRecord = null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
