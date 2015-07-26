package ca.deflector.passable_shots;

import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback encryptCallback;
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        try {
            EncryptionSystem.initKey();
        } catch (Exception e) {
            //TODO show an error message and disable the button
            Log.wtf("ps", "?", e);
        }

        encryptCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // call encryption here
                try {
                    byte[] encrypted = EncryptionSystem.encrypt(data);
                    FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/passable." + System.currentTimeMillis());
                    fos.write(encrypted);
                    fos.close();
                } catch (Exception e) {
                    Log.wtf("init", e);
                }
                refreshCamera();
            }
        };

    }

    public void captureImage(View v) throws IOException {
        //take the picture
        camera.takePicture(null, null, null, encryptCallback);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            Log.wtf("create", e);
            return;
        }
        Camera.Parameters param = camera.getParameters();

        param.setPreviewSize(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            Log.wtf("create2", e);
        }
    }

    private void refreshCamera() {
        if (surfaceHolder.getSurface() == null)
            return;

        try {
            camera.stopPreview();
        } catch (Exception e) {
            Log.wtf("refresh", e);
        }
        Camera.Parameters param = camera.getParameters();
        param.setPreviewSize(surfaceHolder.getSurfaceFrame().width(), surfaceHolder.getSurfaceFrame().height());
        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            Log.wtf("refresh2", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int fmt, int w, int h) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}
