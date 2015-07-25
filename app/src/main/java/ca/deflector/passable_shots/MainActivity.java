package ca.deflector.passable_shots;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
