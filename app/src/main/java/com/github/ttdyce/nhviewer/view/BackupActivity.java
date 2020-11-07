package com.github.ttdyce.nhviewer.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.room.AppDatabase;
import com.github.ttdyce.nhviewer.model.room.ComicCachedDao;
import com.github.ttdyce.nhviewer.model.room.ComicCachedEntity;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionDao;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionEntity;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.List;
import java.util.Locale;

public class BackupActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private static final String TAG = "BackupActivity";

    private QRCodeReaderView qrCodeReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        // check/ask for camera permission here
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            // ask for the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }

        qrCodeReaderView = findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);
        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);
        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 1 && permissions[0].equals(Manifest.permission.CAMERA)) {
            if (grantResults[0] == -1) {
                // permission denied
                Toast.makeText(this, "Failed to scan QRCode, no permission!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                // reload so that camera is usable
                recreate();
            }
        }
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Log.i(TAG, "onQRCodeRead: " + text);
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 400 milliseconds
        if (v != null)
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

    // TODO: 2019/11/3 package below's method to a class/presenter
    private static class BackupTask extends AsyncTask<Void, Void, Boolean> {
        QRData qrData;
        WeakReference<AppCompatActivity> activityRef;
        ProgressDialog dialog;

        public BackupTask(AppCompatActivity activity, String json) {
            Gson gson = new Gson();
            qrData = gson.fromJson(json, QRData.class);

            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(activityRef.get(), "Found QR code",
                    String.format(Locale.ENGLISH, "Connecting to %s:%s... for %s", qrData.ip, qrData.port, qrData.action), true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (qrData.action.equals("restore"))
                return tryRestore();
            else //qrData.action.equals("backup")
                return tryBackup();

        }

        @Override
        protected void onPostExecute(Boolean success) {
            dialog.dismiss();
            String actionCapital = qrData.action.substring(0, 1).toUpperCase() + qrData.action.substring(1);
            if (success)
                Toast.makeText(activityRef.get(), actionCapital + " finished! ", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(activityRef.get(), actionCapital + " failed :(", Toast.LENGTH_SHORT).show();
            activityRef.get().finish();
        }


        private boolean tryBackup() {
            AppDatabase appDatabase = MainActivity.getAppDatabase();
            List<ComicCollectionEntity> collectionEntities = appDatabase.comicCollectionDao().getAll();
            List<ComicCachedEntity> comicCachedEntities = appDatabase.comicCachedDao().getAll();

            Socket socket;

            try {
                socket = new Socket(qrData.ip, Integer.parseInt(qrData.port));
                Log.i(TAG, "tryBackup: connected to " + qrData.ip);

                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                /* NHV-Backup-Protocol
                 * send numOfTables
                 * read ACK
                 * for numOfTables
                 *   send table name
                 *   read ACK
                 *   send entities
                 *   read ACK
                 *
                 * send FIN
                 * read ACK
                 * read FIN
                 * send ACK
                 * close()
                 * */
                Log.i(TAG, "Sending table ComicCollection");
                int numOfTables = 2;
                objectOutputStream.writeInt(numOfTables);
                objectOutputStream.flush();
                String response = objectInputStream.readUTF();

                objectOutputStream.writeUTF("ComicCollection");
                objectOutputStream.flush();
                response = objectInputStream.readUTF();

                objectOutputStream.writeObject(collectionEntities);
//                objectOutputStream.flush(); // may not needed
                response = objectInputStream.readUTF();

                Log.i(TAG, "Sending table ComicCached");
                objectOutputStream.writeUTF("ComicCached");
                objectOutputStream.flush();
                response = objectInputStream.readUTF();

                objectOutputStream.writeObject(comicCachedEntities);
//                objectOutputStream.flush(); // may not needed
                response = objectInputStream.readUTF();

                objectOutputStream.writeUTF("FIN");
                objectOutputStream.flush();
                response = objectInputStream.readUTF();

                response = objectInputStream.readUTF();
                objectOutputStream.writeUTF("ACK");
                objectOutputStream.flush();

                objectOutputStream.close();

                Log.i(TAG, "Closing socket and terminating backup.");
                socket.close();

                return true;
            } catch (IOException e) {
                Log.e(TAG, "Backup failed");
                e.printStackTrace();
            }

            return false;
        }

        private boolean tryRestore() {
            AppDatabase appDatabase = MainActivity.getAppDatabase();
            try (
                    Socket socket = new Socket(qrData.ip, Integer.parseInt(qrData.port));

                    OutputStream outputStream = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            ) {
                Log.i(TAG, "tryRestore: connected to " + qrData.ip);

                Log.i(TAG, "Receiving collectionEntities");
                String response;
                final List<ComicCollectionEntity> collectionEntities = (List<ComicCollectionEntity>) objectInputStream.readObject();
                objectOutputStream.writeUTF("ACK");
                objectOutputStream.flush();

                Log.i(TAG, "Receiving comicCachedEntities");
                final List<ComicCachedEntity> comicCachedEntities = (List<ComicCachedEntity>) objectInputStream.readObject();
                objectOutputStream.writeUTF("ACK");
                objectOutputStream.flush();

                Log.i(TAG, "Insert data");
                // insert the backuped data
                // TODO initial version, only providing the least backup function. Something like "At least we've got one that works"
                ComicCollectionDao comicCollectionDao = appDatabase.comicCollectionDao();
                for (ComicCollectionEntity collection :
                        collectionEntities) {
                    if (comicCollectionDao.notExist(collection.getName(), collection.getId()))
                        comicCollectionDao.insert(collection);
                }
                ComicCachedDao comicCachedDao = appDatabase.comicCachedDao();
                for (ComicCachedEntity comic :
                        comicCachedEntities) {
                    if (comicCachedDao.notExist(comic.getId()))
                        comicCachedDao.insert(comic);
                }

                // ending
                objectOutputStream.writeUTF("FIN");
                objectOutputStream.flush();
                response = objectInputStream.readUTF();

                response = objectInputStream.readUTF();
                objectOutputStream.writeUTF("ACK");
                objectOutputStream.flush();

                objectOutputStream.close();

                Log.i(TAG, "Closing socket and terminating backup.");
                return true;
            } catch (IOException | ClassNotFoundException e) {
                Log.e(TAG, "Restore failed");
                e.printStackTrace();
            }

            return false;
        }
    }

    private static class QRData {
        private String action, ip, port;

        public QRData() {
            // required empty
        }
    }
}

