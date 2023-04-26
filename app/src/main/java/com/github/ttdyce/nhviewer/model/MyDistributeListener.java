package com.github.ttdyce.nhviewer.model;

import android.app.Activity;
import android.content.DialogInterface;
import android.widget.Toast;

import com.github.ttdyce.nhviewer.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.microsoft.appcenter.distribute.Distribute;
import com.microsoft.appcenter.distribute.DistributeListener;
import com.microsoft.appcenter.distribute.ReleaseDetails;
import com.microsoft.appcenter.distribute.UpdateAction;

public class MyDistributeListener implements DistributeListener {

    @Override
    public boolean onReleaseAvailable(Activity activity, ReleaseDetails releaseDetails) {

        // Look at releaseDetails public methods to get version information, release notes text or release notes URL
        String versionName = releaseDetails.getShortVersion();
//        int versionCode = releaseDetails.getVersion();
        String releaseNotes = releaseDetails.getReleaseNotes();
//        Uri releaseNotesUrl = releaseDetails.getReleaseNotesUrl();

        // Build our own dialog title and message
        new MaterialAlertDialogBuilder(activity, R.style.MaterialDialogTheme)
                .setTitle(activity.getString(R.string.new_version_available, versionName))
                .setMessage(releaseNotes)
                .setPositiveButton(activity.getString(com.microsoft.appcenter.distribute.R.string.appcenter_distribute_update_dialog_download),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Distribute.notifyUpdateAction(UpdateAction.UPDATE);
                            }
                        }).setNegativeButton(activity.getString(com.microsoft.appcenter.distribute.R.string.appcenter_distribute_update_dialog_postpone),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Distribute.notifyUpdateAction(UpdateAction.POSTPONE);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Distribute.notifyUpdateAction(UpdateAction.POSTPONE);
                    }
                })
                .create().show();

        // Return true if you're using your own dialog, false otherwise
        return true;



    }

    @Override
    public void onNoReleaseAvailable(Activity activity) {
        Toast.makeText(activity.getApplicationContext(), "no updates available", Toast.LENGTH_LONG).show();
    }
}