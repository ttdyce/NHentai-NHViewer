package com.github.ttdyce.nhviewer.model.firebase;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.ttdyce.nhviewer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class Updater {
    private static final String TAG = Updater.class.getSimpleName();

    public static final String KEY_UPDATE_REQUIRED = "updateRequired";
    public static final String KEY_CURRENT_VERSION = "currentVersion";
    public static final String KEY_UPDATE_URL = "updateUrl";

    private OnUpdateNeededListener onUpdateNeededListener;
    private Context context;

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateUrl);
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public Updater(@NonNull Context context,
                   OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    public void check() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setDefaultsAsync(R.xml.firebase_default_config);
        remoteConfig.fetch(5) // TODO: 2019/10/10 fetching each 5 seconds for debug
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            remoteConfig.activate();
                            Log.d(TAG, "remote config is fetched.");
                            Log.d(TAG, "Fetched update required: " + remoteConfig.getBoolean(KEY_UPDATE_REQUIRED));
                            Log.d(TAG, "Fetched current version: " + remoteConfig.getString(KEY_CURRENT_VERSION));
                            Log.d(TAG, "Fetched app version: " + getAppVersion(context));

                            if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
                                String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
                                String localVersion = getAppVersion(context);
                                String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);

                                if (onUpdateNeededListener != null) {
                                    if (TextUtils.equals(currentVersion, localVersion)) {
                                        // no need update
                                        Toast.makeText(context, String.format("No update found, latest version is %s", currentVersion), Toast.LENGTH_SHORT).show();
                                    }else{
                                        // update needed
                                        onUpdateNeededListener.onUpdateNeeded(updateUrl);
                                    }

                                }

                            }
                        }
                    }
                });

    }

    private String getAppVersion(Context context) {
        String result = "";

        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public static class Builder {

        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public Updater build() {
            return new Updater(context, onUpdateNeededListener);
        }

        public Updater check() {
            Updater updater = build();
            updater.check();

            return updater;
        }
    }
}
