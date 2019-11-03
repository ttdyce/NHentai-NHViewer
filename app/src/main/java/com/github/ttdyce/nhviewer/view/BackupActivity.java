package com.github.ttdyce.nhviewer.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.room.AppDatabase;
import com.github.ttdyce.nhviewer.model.room.ComicCachedEntity;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionEntity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Locale;

public class BackupActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private static final String TAG = "BackupActivity";

    private TextView resultTextView;
    private QRCodeReaderView qrCodeReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        qrCodeReaderView = findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);
        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);
        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String ip = getIPAddress();
//                if (ip == null) {
//                    // TODO: 2019/11/3 cannot toast in a thread
//                    Toast.makeText(BackupActivity.this, "Error: cannot find local ip address, backup has stopped", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                int port = 3333;
//                Socket socket;
//                ArrayList<String> data = new ArrayList<>();
//                ip = ip.substring(0, ip.length() - 3);
//
//                //only support subnet mask 255.255.255.0, e.g. 192.168.128.1 ~ 192.168.128.254
//                for (int i = 1; i < 255; i++) {
//                    String testHost = ip + i;
//                    Log.i(TAG, "Testing host: "+testHost);
//                    try (Socket s = new Socket(testHost, port)) {
//                        data.add(ip + i);
//
//                    } catch (IOException ex) {
//                        /* ignore */
//                    }
//
//                }
//
//            }
//        }).start();
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Log.i(TAG, "onQRCodeRead: "+text);
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 400 milliseconds
        v.vibrate(250);
        qrCodeReaderView.setQRDecodingEnabled(false);
        qrCodeReaderView.stopCamera();

        new BackupTask(this, text).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    // TODO: 2019/11/3 package below's method to a class

    private static class BackupTask extends AsyncTask<Void, Void, Void> {
        String ip;
        int port;
        Context context;
        ProgressDialog dialog;

        public BackupTask(Context context, String data) {
            String[] split = data.split(":");
            ip = split[0];
            port = Integer.parseInt(split[1]);
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(context, "Found QR code",
                    String.format(Locale.ENGLISH, "Connecting to %s:%d...", ip, port), true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            tryBackup();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
        }


        private void tryBackup() {
            AppDatabase appDatabase = MainActivity.getAppDatabase();
            List<ComicCollectionEntity> collectionEntities = appDatabase.comicCollectionDao().getAll();
            List<ComicCachedEntity> comicCachedEntities = appDatabase.comicCachedDao().getAll();

            Socket socket;

            try {
                socket = new Socket(ip, port);
                Log.d(TAG, "tryBackup: connected to " + ip);
//                    Toast.makeText(MainActivity.this, "tryBackup: connected to " + host, Toast.LENGTH_SHORT).show();

                // get the output stream from the socket.
                OutputStream outputStream = socket.getOutputStream();
                // create a data output stream from the output stream so we can send data through it
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                Log.d(TAG, "Sending table ComicCollection");
                dataOutputStream.write("Table name".getBytes());
                dataOutputStream.write("ComicCollection".getBytes());
                socket.getInputStream().read();

                for (ComicCollectionEntity e :collectionEntities){
                    dataOutputStream.write(e.toJson().getBytes());
                    socket.getInputStream().read();
                }
                dataOutputStream.write("EOF".getBytes());
                socket.getInputStream().read();


                Log.d(TAG, "Sending table ComicCached");
                dataOutputStream.write("Table name".getBytes());
                dataOutputStream.write("ComicCached".getBytes());
                socket.getInputStream().read();

                for (ComicCachedEntity e :comicCachedEntities){
                    dataOutputStream.write(e.toJson().getBytes());
                    socket.getInputStream().read();
                }
                dataOutputStream.write("EOF".getBytes());
                socket.getInputStream().read();

                dataOutputStream.write("END".getBytes());
                dataOutputStream.flush();
                dataOutputStream.close();

                Log.d(TAG, "Closing socket and terminating program.");
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Backup failed");
//                    Toast.makeText(this, "Backup failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
