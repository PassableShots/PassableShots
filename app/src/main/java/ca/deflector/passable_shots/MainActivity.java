package ca.deflector.passable_shots;

import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

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

        encryptCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // call encryption here
            }
        };
        
        /* try {
            File sample = new File(Environment.getExternalStorageDirectory() + "/sample.jpg");
            byte[] bytes = new byte[(int) sample.length()];
            FileInputStream fis = new FileInputStream(sample);
            int count = 0;
            while (count < bytes.length) {
                int read = fis.read(bytes, count, bytes.length - count);
                count += read;
            }
            fis.close();
            EncryptionSystem.initKey();
            byte[] encrypted = EncryptionSystem.encrypt(bytes);
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/sample.encrypted");
            fos.write(encrypted);
            fos.close();
        } catch (Exception e) {
            Log.wtf("ps", "?", e);
        } */
    }

    public void captureImage(View v) throws IOException {
        //take the picture
        camera.takePicture(null, null, null, encryptCallback);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int fmt, int w, int h) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
