package com.zendrive.phonegap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.zendrive.sdk.DriveInfo;
import com.zendrive.sdk.DriveStartInfo;
import com.zendrive.sdk.LocationPoint;
import com.zendrive.sdk.Zendrive;
import com.zendrive.sdk.ZendriveConfiguration;
import com.zendrive.sdk.ZendriveDriveDetectionMode;
import com.zendrive.sdk.ZendriveDriverAttributes;
import com.zendrive.sdk.ZendriveListener;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by chandan on 11/3/14.
 */
public class ZendriveCordovaPlugin extends CordovaPlugin implements ZendriveListener {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {
        if (action.equals("setup")) {
            setup(args, callbackContext);
            return true;
        } else if (action.equals("teardown")) {
            teardown(args, callbackContext);
            return true;
        } else if (action.equals("startDrive")) {
            startDrive(args, callbackContext);
            return true;
        } else if (action.equals("stopDrive")) {
            stopDrive(args, callbackContext);
            return true;
        } else if (action.equals("startSession")) {
            startSession(args, callbackContext);
            return true;
        } else if (action.equals("stopSession")) {
            stopSession(args, callbackContext);
            return true;
        } else if (action.equals("setDriveDetectionMode")) {
            setDriveDetectionMode(args, callbackContext);
            return true;
        } else if (action.equals("setProcessStartOfDriveDelegateCallback")) {
            setProcessStartOfDriveDelegateCallback(args, callbackContext);
            return true;
        } else if (action.equals("setProcessEndOfDriveDelegateCallback")) {
            setProcessEndOfDriveDelegateCallback(args, callbackContext);
            return true;
        }
        return false;
    }

    private void setup(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        JSONObject configJsonObj = args.getJSONObject(0);
        if (configJsonObj == null) {
            callbackContext.error("Wrong configuration supplied");
            return;
        }

        String applicationKey = getApplicationKey(configJsonObj);
        String driverId = getDriverId(configJsonObj);

        Integer driveDetectionModeInt = null;
        if (hasValidValueForKey(configJsonObj, kDriveDetectionModeKey)) {
            driveDetectionModeInt = configJsonObj.getInt(kDriveDetectionModeKey);
        }
        else {
            callbackContext.error("Wrong drive detection mode supplied");
            return;
        }

        ZendriveDriveDetectionMode mode = this.getDriveDetectionModeFromInt(driveDetectionModeInt);
        ZendriveConfiguration configuration = new ZendriveConfiguration(applicationKey, driverId,
                mode);

        ZendriveDriverAttributes driverAttributes = this.getDriverAttrsFromJsonObject(configJsonObj);
        if (driverAttributes != null) {
            configuration.setDriverAttributes(driverAttributes);
        }

        // setup Zendrive SDK
        Zendrive.setup(
                this.cordova.getActivity().getApplicationContext(),
                configuration, this,
                new Zendrive.SetupCallback() {
                    @Override
                    public void onSetup(boolean success) {
                        if (success) {
                            callbackContext.success();
                        } else {
                            callbackContext.error("Zendrive setup failed");
                        }
                    }
                });
    }

    private void teardown(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.teardown();
        callbackContext.success();
    }

    private void startDrive(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.startDrive(args.getString(0));
        callbackContext.success();
    }

    private void stopDrive(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.stopDrive(args.getString(0));
        callbackContext.success();
    }

    private void startSession(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.startSession(args.getString(0));
        callbackContext.success();
    }

    private void stopSession(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.stopSession();
        callbackContext.success();
    }

    private void setDriveDetectionMode(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Integer driveDetectionModeInt = args.getInt(0);
        if (driveDetectionModeInt == null) {
            callbackContext.error("Invalid Zendrive drive detection mode");
            return;
        }

        ZendriveDriveDetectionMode mode = this.getDriveDetectionModeFromInt(driveDetectionModeInt);
        Zendrive.setZendriveDriveDetectionMode(mode);
        callbackContext.success();
    }

    private void setProcessStartOfDriveDelegateCallback(JSONArray args, final CallbackContext callbackContext)
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

    private void setProcessEndOfDriveDelegateCallback(JSONArray args, final CallbackContext callbackContext)
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

    @Override
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

            PluginResult result = new PluginResult(PluginResult.Status.OK,
                    driveStartInfoObject);
            result.setKeepCallback(true);
            processStartOfDriveCallback.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDriveEnd(DriveInfo driveInfo) {
        if (processEndOfDriveCallback == null || processEndOfDriveCallback.isFinished()) {
            return;
        }
        try {
            JSONObject driveInfoObject = new JSONObject();
            driveInfoObject.put(kStartTimestampKey, driveInfo.startTimeMillis);
            driveInfoObject.put(kEndTimestampKey, driveInfo.endTimeMillis);
            driveInfoObject.put(kIsValidKey, true);
            driveInfoObject.put(kAverageSpeedKey, driveInfo.averageSpeed);
            driveInfoObject.put(kDistanceKey, driveInfo.distanceMeters);

            if (null != driveInfo.waypoints) {
                JSONArray waypointsArray = new JSONArray();

                int waypointsCount = driveInfo.waypoints.size();
                for (int i = 0; i<waypointsCount; i++) {
                    LocationPoint locationPoint = driveInfo.waypoints.get(i);

                    JSONObject driveLocationObject = new JSONObject();
                    driveLocationObject.put(kLatitudeKey, locationPoint.latitude);
                    driveLocationObject.put(kLongitudeKey, locationPoint.longitude);
                    waypointsArray.put(driveLocationObject);
                }
                driveInfoObject.put(kWaypointsKey, waypointsArray);
            }

            PluginResult result = new PluginResult(PluginResult.Status.OK,
                    driveInfoObject);
            result.setKeepCallback(true);
            processEndOfDriveCallback.sendPluginResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ZendriveDriveDetectionMode getDriveDetectionModeFromInt(Integer driveDetectionModeInt) {
        ZendriveDriveDetectionMode mode = driveDetectionModeInt == 1 ?
                ZendriveDriveDetectionMode.AUTO_OFF : ZendriveDriveDetectionMode.AUTO_ON;
        return mode;
    }

    private ZendriveDriverAttributes getDriverAttrsFromJsonObject(JSONObject configJsonObj)
            throws JSONException {
        Object driverAttributesObj = getObjectFromJSONObject(configJsonObj, kDriverAttributesKey);
        ZendriveDriverAttributes driverAttributes = null;
        if (null != driverAttributesObj && !JSONObject.NULL.equals(driverAttributesObj)) {
            JSONObject driverAttrJsonObj = (JSONObject) driverAttributesObj;
            driverAttributes = new ZendriveDriverAttributes();

            Object firstName = getObjectFromJSONObject(driverAttrJsonObj, "firstName");
            if (!isNull(firstName)) {
                try {
                    driverAttributes.setFirstName(firstName.toString());
                }
                catch (Exception e) {}
            }

            Object lastName = getObjectFromJSONObject(driverAttrJsonObj, "lastName");
            if (!isNull(lastName)) {
                try {
                    driverAttributes.setLastName(lastName.toString());
                }
                catch (Exception e) {}
            }

            Object email = getObjectFromJSONObject(driverAttrJsonObj, "email");
            if (!isNull(email)) {
                try {
                    driverAttributes.setEmail(email.toString());
                }
                catch (Exception e) {}
            }

            Object group = getObjectFromJSONObject(driverAttrJsonObj, "group");
            if (!isNull(group)) {
                try {
                    driverAttributes.setGroup(group.toString());
                }
                catch (Exception e) {}
            }

            Object phoneNumber = getObjectFromJSONObject(driverAttrJsonObj, "phoneNumber");
            if (!isNull(phoneNumber)) {
                try {
                    driverAttributes.setPhoneNumber(phoneNumber.toString());
                }
                catch (Exception e) {}
            }

            Object driverStartDateStr = getObjectFromJSONObject(driverAttrJsonObj, "driverStartDate");
            if (!isNull(driverStartDateStr)) {
                try {
                    Long driverStartDateTimestampInMillis = Long.parseLong(driverStartDateStr.toString())*1000;
                    Date driverStartDate = new Date(driverStartDateTimestampInMillis);
                    driverAttributes.setDriverStartDate(driverStartDate);
                }
                catch (Exception e) {}

            }

            if (hasValidValueForKey(driverAttrJsonObj, kCustomAttributesKey)) {
                JSONObject customAttrs = driverAttrJsonObj.getJSONObject(kCustomAttributesKey);
                Iterator<?> keys = customAttrs.keys();
                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    Object value = getObjectFromJSONObject(customAttrs, key);
                    if (value instanceof String) {
                        try {
                            driverAttributes.setCustomAttribute(key, (String)value);
                        }
                        catch (Exception e) {}
                    }
                }
            }
        }

        return driverAttributes;
    }

    // UTILITY METHODS
    private Boolean isNull(Object object) {
        return ((object == null) || JSONObject.NULL.equals(object));
    }

    private Object getObjectFromJSONObject(JSONObject jsonObject, String key) throws JSONException {
        if (hasValidValueForKey(jsonObject, key)) {
            return jsonObject.get(key);
        }
        return null;
    }

    private Boolean hasValidValueForKey(JSONObject jsonObject, String key) {
        return (jsonObject.has(key) && !jsonObject.isNull(key));
    }

    private String getDriverId(JSONObject configJsonObj) throws JSONException {
        Object driverIdObj = getObjectFromJSONObject(configJsonObj, "driverId");
        String driverId = null;
        if (!isNull(driverIdObj)) {
            driverId = driverIdObj.toString();
        }
        return driverId;
    }

    private String getApplicationKey(JSONObject configJsonObj) throws JSONException {
        Object applicationKeyObj = getObjectFromJSONObject(configJsonObj, "applicationKey");
        String applicationKey = null;
        if (!isNull(applicationKeyObj)) {
            applicationKey = applicationKeyObj.toString();
        }
        return applicationKey;
    }

    private CallbackContext processStartOfDriveCallback;
    private CallbackContext processEndOfDriveCallback;

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

    // ZendriveDriverAttributes dictionary keys
    private static final String kCustomAttributesKey = "customAttributes";
    private static final String kDriverAttributesKey = "driverAttributes";

    private static final String kDriveDetectionModeKey = "driveDetectionMode";

}