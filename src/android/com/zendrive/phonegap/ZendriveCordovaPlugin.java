package com.zendrive.phonegap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.zendrive.sdk.Zendrive;
import com.zendrive.sdk.ZendriveConfiguration;
import com.zendrive.sdk.ZendriveDriveDetectionMode;

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
        }
        return false;
    }

    private void setup(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        String applicationKey = args.getString(0);
        String driverId = args.getString(1);

        ZendriveDriveDetectionMode mode = ZendriveDriveDetectionMode.AUTO_ON;
        String driveDetectionModeString = args.optString(2);
        if (driveDetectionModeString.equals("")) {
            mode = ZendriveDriveDetectionMode.AUTO_ON;
        } else {
            mode = getZendriveDriveDetectionModeFromString(driveDetectionModeString);
        }
        if (mode == null) {
            callbackContext.error("Invalid Zendrive drive detection mode: " +
                driveDetectionModeString);
            return;
        }

        // Zendrive configuration
        ZendriveConfiguration configuration =
        		new ZendriveConfiguration(applicationKey, driverId, mode);

        // setup zendrive sdk
        Zendrive.setup(
                this.cordova.getActivity().getApplicationContext(),
                configuration, null,
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
        Zendrive.stopDrive();
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
            String modeString = args.getString(0);
            ZendriveDriveDetectionMode mode =
                getZendriveDriveDetectionModeFromString(modeString);
            if (mode != null) {
                Zendrive.setZendriveDriveDetectionMode(mode);
                callbackContext.success();
            } else {
                callbackContext.error("Invalid Zendrive drive detection mode: " + modeString);
            }
    }

    private ZendriveDriveDetectionMode getZendriveDriveDetectionModeFromString(
        String modeString) {
        if (modeString.equals(kAutoOn)) {
            return ZendriveDriveDetectionMode.AUTO_ON;
        } else if (modeString.equals(kAutoOff)) {
            return ZendriveDriveDetectionMode.AUTO_OFF;
        } else {
            return null;
        }
    }
    private final String kAutoOff = "AUTO_OFF";
    private final String kAutoOn = "AUTO_ON";

}
