package com.zendrive.phonegap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.zendrive.sdk.Zendrive;
import com.zendrive.sdk.ZendriveConfiguration;

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
        }
        return false;
    }

    private void setup(JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        String applicationKey = args.getString(0);
        String driverId = args.getString(1);

        // Zendrive configuration
        ZendriveConfiguration configuration = 
        		new ZendriveConfiguration(applicationKey, driverId);

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
}
