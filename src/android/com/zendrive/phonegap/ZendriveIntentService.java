package com.zendrive.phonegap;

import com.zendrive.sdk.AccidentInfo;
import com.zendrive.sdk.DriveInfo;
import com.zendrive.sdk.DriveResumeInfo;
import com.zendrive.sdk.DriveStartInfo;
import com.zendrive.sdk.ZendriveLocationSettingsResult;

/**
 * Created by yogesh on 7/20/16.
 */
public class ZendriveIntentService extends com.zendrive.sdk.ZendriveIntentService {
    public ZendriveIntentService() {
        super("com.zendrive.phonegap.ZendriveIntentService");
    }

    @Override
    public void onDriveStart(DriveStartInfo driveStartInfo) {
        ZendriveManager.getSharedInstance().onDriveStart(driveStartInfo);
    }

    @Override
    public void onDriveEnd(DriveInfo driveInfo) {
        ZendriveManager.getSharedInstance().onDriveEnd(driveInfo);
    }

    @Override
    public void onDriveResume(DriveResumeInfo driveResumeInfo) {
        ZendriveManager.getSharedInstance().onDriveResume(driveResumeInfo);
    }

    @Override
    public void onAccident(AccidentInfo accidentInfo) {
        ZendriveManager.getSharedInstance().onAccident(accidentInfo);
    }

    @Override
    public void onLocationPermissionsChange(boolean b) {
        ZendriveManager.getSharedInstance().onLocationPermissionsChange(b);
    }

    @Override
    public void onLocationSettingsChange(ZendriveLocationSettingsResult zendriveLocationSettingsResult) {
        ZendriveManager.getSharedInstance().onLocationSettingsChange(zendriveLocationSettingsResult);
    }
}
