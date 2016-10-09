# snapremote_detector

[<img src="https://github.com/YusukeIwaki/snapremote_detector/blob/master/example/src/main/res/drawable-xxxhdpi/fig_snapremote.png?raw=true" width="320"/>](http://amzn.to/2dWgud5)

A signal detection library for [SnapRemote](http://amzn.to/2dWgud5).

Unfortunately, [Snap Remote app](https://play.google.com/store/apps/details?id=magcom.snapremote.camera) is designed just for releasing the shutter.
But you can develop any trigger with this library, such as unlocking screen, sending a email...!!


# Usage

## setup

```
repositories {
    maven {
        url 'https://github.com/YusukeIwaki/snapremote_detector/raw/master/repo'
    }
}

dependencies {
    compile 'io.yi01.snapremote_detector:library:0.0.1'
    compile 'io.yi01.snapremote_detector:recording_service:0.0.1'
}
```

## example

### Simplest example

```
class SignalHandlingService extends Service {
  public void onCreate() {
    super.onCreate();

    registerReceiver(new BroadcastReceiver(){
      public void onReceive(Context context, Intent intent) {
        String signal = intent.getAction();

        if ("A".equals(signal)) handleSignalA();
        if ("B".equals(signal)) handleSignalB();
      }

    }, new IntentFilter(RecordingService.ACTION_NAME))

    RecordingService.start(this);
  }

  private void handleSignalA() {
    // You can add your favorite procedure here!
  }

  ...
}
```


### Calibration (experimental)

On several devices with poor accuracy microphone, signal detection is often failed.
If you want to support such devices, consider implementing calibration.

You can tune the thresholds of signal detection like below:

```
class MyOwnSignalHandler extend SnapRemoteSignalHandler {
    @Override
    protected SignalDetector.Builder getSignalDetectorBuilderForA() {
      return super.getSignalDetectorBuilderForA()
                      .min(15509)
                      .max(16645)
                      .torelance(33)

                      ...;
    }

}

class XXXService extend RecordingService {
    @Override
    protected setupSignalHandler() {
      mSignalHandler = new MyOwnSignalHandler(new SnapRemoteSignalHandler.Callback() {
        @Override
        public void onAccept(String name, double[] data) {
          // Your favorite trigger
        }

        @Override
        public void onReject() { }
      });
    }
}

```
