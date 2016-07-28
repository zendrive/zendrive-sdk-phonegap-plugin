package com.zendrive.phonegap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.zendrive.sdk.ActiveDriveInfo;
import com.zendrive.sdk.DriveInfo;
import com.zendrive.sdk.DriveStartInfo;
import com.zendrive.sdk.LocationPoint;
import com.zendrive.sdk.Zendrive;
import com.zendrive.sdk.ZendriveConfiguration;
import com.zendrive.sdk.ZendriveDriveDetectionMode;
import com.zendrive.sdk.ZendriveDriverAttributes;
import com.zendrive.sdk.ZendriveOperationCallback;
import com.zendrive.sdk.ZendriveOperationResult;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by chandan on 11/3/14.
 */
public class ZendriveCordovaPlugin extends CordovaPlugin {
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
        } else if (action.equals("getActiveDriveInfo")) {
            getActiveDriveInfo(callbackContext);
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
            ZendriveManager.getSharedInstance().setProcessStartOfDriveDelegateCallback(args, callbackContext);
            return true;
        } else if (action.equals("setProcessEndOfDriveDelegateCallback")) {
            ZendriveManager.getSharedInstance().setProcessEndOfDriveDelegateCallback(args, callbackContext);
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
                configuration, ZendriveIntentService.class,
                new ZendriveOperationCallback() {
                    @Override
                    public void onCompletion(ZendriveOperationResult zendriveOperationResult) {
                        if (zendriveOperationResult.isSuccess()) {
                            callbackContext.success();
                        } else {
                            callbackContext.error("Zendrive setup failed");
                        }
                    }
                });
    }

    private void teardown(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.teardown(null);
        ZendriveManager.getSharedInstance().teardown();
        callbackContext.success();
    }

    private void startDrive(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        Zendrive.startDrive(args.getString(0));
        callbackContext.success();
    }

    private void getActiveDriveInfo(final CallbackContext callbackContext)
            throws JSONException {
        JSONObject activeDriveInfoObject = ZendriveManager.getSharedInstance().getActiveDriveInfo();
        PluginResult result;
        if(activeDriveInfoObject!= null) {
            result = new PluginResult(PluginResult.Status.OK, activeDriveInfoObject);
        } else {
            String resultStr = null;
            result = new PluginResult(PluginResult.Status.OK, resultStr);
        }
        result.setKeepCallback(false);
        callbackContext.sendPluginResult(result);
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

    // ZendriveDriverAttributes dictionary keys
    private static final String kCustomAttributesKey = "customAttributes";
    private static final String kDriverAttributesKey = "driverAttributes";

    private static final String kDriveDetectionModeKey = "driveDetectionMode";

}