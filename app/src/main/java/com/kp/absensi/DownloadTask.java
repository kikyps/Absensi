package com.kp.absensi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Integer, String> {
    private ProgressDialog mPDialog;
    private Context mContext;
    private PowerManager.WakeLock mWakeLock;
    //Constructor parameters :
    // @context (current Activity)
    // @targetFile (File object to write,it will be overwritten if exist)
    // @dialogMessage (message of the ProgresDialog)
    public DownloadTask(Context context) {
        this.mContext = context;
        mPDialog = new ProgressDialog(context);

        mPDialog.setMessage("Sedang Mengunduh Pembaruan....");
        mPDialog.setIndeterminate(true);
        mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mPDialog.setCancelable(false);
        // reference to instance to use inside listener
        final DownloadTask me = this;
        mPDialog.setOnCancelListener(dialog -> me.cancel(true));
        Log.i("DownloadTask","Constructor done");
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            Log.i("DownloadTask","Response " + connection.getResponseCode());

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

            File f = new File(rootPath + "update.apk");
            if (f.exists()) {
                f.delete();
            }

            input = connection.getInputStream();
            output = new FileOutputStream(f,false);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    Log.i("DownloadTask","Cancelled");
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire(10*60*1000L /*10 minutes*/);

        mPDialog.show();

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mPDialog.setIndeterminate(false);
        mPDialog.setMax(100);
        mPDialog.setProgress(progress[0]);

    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("DownloadTask", "Work Done! PostExecute");
        mWakeLock.release();
        mPDialog.dismiss();
        if (result != null)
            Toast.makeText(mContext,"Download error: " + result, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(mContext,"File Downloaded", Toast.LENGTH_SHORT).show();
    }
}
