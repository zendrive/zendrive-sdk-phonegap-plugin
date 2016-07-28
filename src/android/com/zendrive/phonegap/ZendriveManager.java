package com.zendrive.phonegap;

import com.zendrive.sdk.AccidentInfo;
import com.zendrive.sdk.ActiveDriveInfo;
import com.zendrive.sdk.DriveInfo;
import com.zendrive.sdk.DriveResumeInfo;
import com.zendrive.sdk.DriveStartInfo;
import com.zendrive.sdk.LocationPoint;
import com.zendrive.sdk.Zendrive;
import com.zendrive.sdk.ZendriveLocationSettingsResult;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yogesh on 7/20/16.
 */

public class ZendriveManager {

    // String Constants
    // ZendriveLocationPoint dictionary keys
    private static final String kLatitudeKey = "latitude";
    private static final String kLongitudeKey = "longitude";

    // ZendriveDriveStartInfo dictionary keys
    private static final String kStartTimestampKey = "startTimestamp";
    private static final String kStartLocationKey = "startLocation";

    // ZendriveDriveInfo dictionary keys
    private static final String kIsValidKey = "isValid";
    private static final String kEndTimestampKey = "endTimestamp";
    private static final String kAverageSpeedKey = "averageSpeed";
    private static final String kDistanceKey = "distance";
    private static final String kWaypointsKey = "waypoints";
    private static final String kTrackingIdKey = "trackingId";
    private static final String kSessionIdKey = "sessionId";

    // Callbacks
    private CallbackContext processStartOfDriveCallback;
    private CallbackContext processEndOfDriveCallback;

    private static ZendriveManager sharedInstance;
    public static synchronized ZendriveManager getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new ZendriveManager();
        }
        return sharedInstance;
    }

    public static synchronized void teardown() {
        sharedInstance = null;
    }

    public void setProcessStartOfDriveDelegateCallback(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        if (null != this.processStartOfDriveCallback) {
            // Delete old callback
            // Sending NO_RESULT doesn't call any js callback method
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);

            // Setting keepCallback to false would make sure that the callback is deleted from
            // memory after this call
            result.setKeepCallback(false);
            processStartOfDriveCallback.sendPluginResult(result);
        }
        Boolean hasCallback = args.getBoolean(0);
        if (hasCallback) {
            this.processStartOfDriveCallback = callbackContext;
        }
        else {
            this.processStartOfDriveCallback = null;
        }
    }

    public void setProcessEndOfDriveDelegateCallback(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        if (null != this.processEndOfDriveCallback) {
            // Delete old callback
            // Sending NO_RESULT doesn't call any js callback method
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);

            // Setting keepCallback to false would make sure that the callback is deleted from
            // memory after this call
            result.setKeepCallback(false);
            processEndOfDriveCallback.sendPluginResult(result);
        }
        Boolean hasCallback = args.getBoolean(0);
        if (hasCallback) {
            this.processEndOfDriveCallback = callbackContext;
        }
        else {
            this.processEndOfDriveCallback = null;
        }
    }

    public void onDriveStart(DriveStartInfo driveStartInfo) {
        if (processStartOfDriveCallback == null || processStartOfDriveCallback.isFinished()) {
            return;
        }
        try {
            JSONObject driveStartInfoObject = new JSONObject();
            driveStartInfoObject.put(kStartTimestampKey, driveStartInfo.startTimeMillis);

            if (null != driveStartInfo.startLocation) {
                JSONObject driveStartLocationObject = new JSONObject();
                driveStartLocationObject.put(kLatitudeKey, driveStartInfo.startLocation.latitude);
                driveStartLocationObject.put(kLongitudeKey, driveStartInfo.startLocation.longitude);
                driveStartInfoObject.put(kStartLocationKey, driveStartLocationObject);
            }
            else {
                driveStartInfoObject.put(kStartLocationKey, JSONObject.NULL);
            }

            PluginResult result = new PluginResult(PluginResult.Status.OK,
                    driveStartInfoObject);
            result.setKeepCallback(true);
            processStartOfDriveCallback.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getActiveDriveInfo() {
        try {
            JSONObject activeDriveInfoObject = new JSONObject();
            ActiveDriveInfo activeDriveInfo = Zendrive.getActiveDriveInfo();
            activeDriveInfoObject.put(kStartTimestampKey, activeDriveInfo.startTimeMillis);
            activeDriveInfoObject.put(kTrackingIdKey,
                    (activeDriveInfo.trackingId != null) ? activeDriveInfo.trackingId:JSONObject.NULL);
            activeDriveInfoObject.put(kSessionIdKey,
                    (activeDriveInfo.sessionId != null) ? activeDriveInfo.sessionId:JSONObject.NULL);
            return activeDriveInfoObject;
        }  catch (Exception e) {}
        return null;
    }

    public void onDriveEnd(DriveInfo driveInfo) {
        if (processEndOfDriveCallback == null || processEndOfDriveCallback.isFinished()) {
            return;
        }
        try {
            JSONObject driveInfoObject = new JSONObject();
            driveInfoObject.put(kStartTimestampKey, driveInfo.startTimeMillis);
            driveInfoObject.put(kEndTimestampKey, driveInfo.endTimeMillis);
            driveInfoObject.put(kIsValidKey, driveInfo.isValid);
            driveInfoObject.put(kAverageSpeedKey, driveInfo.averageSpeed);
            driveInfoObject.put(kDistanceKey, driveInfo.distanceMeters);

            int waypointsCount = 0;
            if (null != driveInfo.waypoints) {
                waypointsCount = driveInfo.waypoints.size();
            }
            JSONArray waypointsArray = new JSONArray();
            for (int i = 0; i<waypointsCount; i++) {
                LocationPoint locationPoint = driveInfo.waypoints.get(i);

                JSONObject driveLocationObject = new JSONObject();
                driveLocationObject.put(kLatitudeKey, locationPoint.latitude);
                driveLocationObject.put(kLongitudeKey, locationPoint.longitude);
                waypointsArray.put(driveLocationObject);
            }
            driveInfoObject.put(kWaypointsKey, waypointsArray);

            PluginResult result = new PluginResult(PluginResult.Status.OK,
                    driveInfoObject);
            result.setKeepCallback(true);
            processEndOfDriveCallback.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onDriveResume(DriveResumeInfo driveResumeInfo) {

    }

    public void onAccident(AccidentInfo accidentInfo) {

    }

    public void onLocationPermissionsChange(boolean b) {

    }

    public void onLocationSettingsChange(ZendriveLocationSettingsResult zendriveLocationSettingsResult) {

    }
}
