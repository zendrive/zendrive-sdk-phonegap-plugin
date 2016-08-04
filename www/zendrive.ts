declare var cordova: any; //cordova is declared in another location
export module Zendrive {
    /**
     * Initializes the Zendrive library to automatically detect driving and collect data.
     * Client code should call this method before anything else in the Zendrive API.
     *
     * @param  {Zendrive.ZendriveConfiguration} zendriveConfiguration - The configuration object used to setup
     * the SDK. This object contains your credentials along with additional setup parameters that you
     * can use to provide meta-information about the user or to tune the SDK functionality.
     * @param  {Zendrive.ZendriveCallback} zendriveCallback - Callback for Zendrive events
     * @param  {Zendrive.setupSuccessCallback} successCallback - This is called when Zendrive SDK setup succeeds
     * @param  {Zendrive.failureCallback} errorCallback - This is called if Zendrive SDK setup fails for some reason
     */
    export function setup(zendriveConfiguration?: ZendriveConfiguration,
        zendriveCallback?: ZendriveCallback,
        successCallback?: ISetupSuccessCallback,
        errorCallback?: IFailureCallback): void {
        registerForDelegateCallbacks(zendriveCallback);

        /**
         * Setup success callback
         * @callback setupSuccessCallback
         * @memberOf Zendrive
         */
        var setupSuccessCallback = successCallback;

        /**
         * Failure callback
         * @callback failureCallback
         * @param {String} error This will have an error message
         * @memberOf Zendrive
         */
        var setupFailureCallback = errorCallback;
        cordova &&
            cordova.exec(setupSuccessCallback, setupFailureCallback, "Zendrive", "setup", [zendriveConfiguration]);
    }

    /**
     * Stops driving data collection. The application can disable the Zendrive SDK by invoking this method.
     */
    export function teardown(): void {
        cordova && cordova.exec(null, null, "Zendrive", "teardown", []);
    }

    /**
     * Invoking this method forces the start of a drive. If this API is used then it is application’s
     * responsibility to terminate the drive by invoking @{@link Zendrive.stopDrive} method. If an auto-detected drive is
     * in progress, that drive is stopped and a new drive is started.
     *
     * @param  {String} driveTrackingId - Pass a tracking Id to correlate apps internal data with the
     *  drive data. Cannot be null or empty string. Cannot be longer than 64 characters. Sending null or
     *  empty string as tracking id is a no-op.
     */
    export function startDrive(driveTrackingId: string): void {
        cordova && cordova.exec(null, null, "Zendrive", "startDrive", [driveTrackingId]);
    }

    /**
     * This method provides information about current active drive.
     * @param  {Zendrive.activeDriveCallback} callback - Callback containing active drive information.
     */
    export function getActiveDriveInfo(callback: any): void {
        /**
         * active drive callback
         * @callback activeDriveCallback
         * @param {Zendrive.ZendriveActiveDriveInfo} information about the currently active drive.
         * @memberOf Zendrive
         */
        var activeDriveCallback = callback;
        cordova && cordova.exec(activeDriveCallback, null, "Zendrive", "getActiveDriveInfo", []);
    }

    /**
     * This should be called to indicate the end of a drive started by invoking @{@link Zendrive.startDrive}
     *
     * @param  {String} driveTrackingId - This trackingId should match the trackingId sent to
     * @{@link Zendrive.startDrive} while starting the current drive. If the trackingIds do not match, this
     * function is a no-op. Cannot be null or empty string.
     */
    export function stopDrive(driveTrackingId: string): void {
        cordova && cordova.exec(null, null, "Zendrive", "stopDrive", [driveTrackingId]);
    }

    /**
     * All drives, either automatically detected or started using @{@link Zendrive.startDrive}, will be tagged with the
     * sessionId if a session is already in progress. If a drive is already on when this call is made,
     * that drive will not belong to this session.
     *
     * This session id will be made available as a query parameter in the reports and API that Zendrive provides.
     *
     * The application must call @{@link Zendrive.stopSession} when it wants to end the session.
     *
     * Only one session may be active at a time. Calling startSession when a session is already active
     *  with a new sessionId will stop the ongoing session and start a new one.
     * @param  {String} sessionId - an identifier that identifies this session uniquely. Cannot be null
     * or an empty string. Cannot be longer than 64 characters.
     */
    export function startSession(sessionId: string): void {
        cordova && cordova.exec(null, null, "Zendrive", "startSession", [sessionId]);
    }

    /**
     * Stop currently ongoing session. No-op if no session is ongoing. Trips that start after this call
     * do not belong to the session. Ongoing trips at the time of this call will continue to belong to
     * the session that was just stopped.
     */
    export function stopSession(): void {
        cordova && cordova.exec(null, null, "Zendrive", "stopSession", []);
    }

    /**
     * Change the drive detection mode to control how Zendrive SDK detects drives.
     * See @{@link Zendrive.ZendriveDriveDetectionMode} for further details. This will override the mode sent
     * with @{@link Zendrive.ZendriveConfiguration} during setup.
     *
     * @param {Zendrive.ZendriveDriveDetectionMode}
     */
    export function setDriveDetectionMode(driveDetectionMode: ZendriveDriveDetectionMode): void {
        cordova && cordova.exec(null, null, "Zendrive", "setDriveDetectionMode", [driveDetectionMode]);
    }

    /**
     * @class Callback for Zendrive
     * @param {Zendrive.ZendriveCallback.processStartOfDrive} processStartOfDrive - Called on callback
     * when Zendrive SDK detects a potential start of a drive.
     *
     * @param {Zendrive.ZendriveCallback.processEndOfDrive} processEndOfDrive - Called on the callback
     * when Zendrive SDK detects a drive to have been completed.
     *
     * @param {Zendrive.ZendriveCallback.processLocationDenied} processLocationDenied - This is an iOS
     * specific callback. Called on callback when location services are denied for the SDK.
     * @constructor
     */
    export class ZendriveCallback {
        public constructor(processStartOfDrive?: IProcessStartOfDriveCallback,
            processEndOfDrive?: IProcessEndOfDriveCallback,
            processLocationDenied?: IProcessLocationDeniedCallback) {
            this.processStartOfDrive = processStartOfDrive;
            this.processEndOfDrive = processEndOfDrive;
            this.processLocationDenied = processLocationDenied;
        }

        /**
         * Called on callback when Zendrive SDK detects a potential start of a drive.
         * @callback processStartOfDrive
         * @param {Zendrive.ZendriveDriveStartInfo} driveStartInfo Information about start of a drive.
         * @memberOf Zendrive.ZendriveCallback
         */
        public processStartOfDrive: IProcessStartOfDriveCallback;

        /**
         * Called on the callback when Zendrive SDK detects a drive to have been completed.
         * @callback processEndOfDrive
         * @param {Zendrive.ZendriveDriveInfo} driveInfo Meta-information related to a drive.
         * @memberOf Zendrive.ZendriveCallback
         */

        public processEndOfDrive: IProcessEndOfDriveCallback;
        /**
         * This is an iOS specific callback. Called on callback when location services are denied for the SDK.
         * After this callback, drive detection is paused until location services are re-enabled for the SDK.
         * @callback processLocationDenied
         * @memberOf Zendrive.ZendriveCallback
         */
        public processLocationDenied: IProcessLocationDeniedCallback;
    }

    export class ZendriveConfiguration {
        public constructor(applicationKey: string, driverId: string) {
            this.applicationKey = applicationKey;

            this.driverId = driverId;

            this.driverAttributes = null;
            this.driveDetectionMode = ZendriveDriveDetectionMode.ZendriveDriveDetectionModeAutoON;
        }
        /**
         * Your application key
         * @type {String}
         */
        public applicationKey: string;
        /**
         * Unique ID for the current user. This can be any ID used by your app to
         * identify its users. This is the ID which will be used in Zendrive reports.
         * @type {String}
         */
        public driverId: string;
        /**
         * Attributes for the current user. These attributes are stored on the server and are provided
         * in Zendrive’s APIs. Any existing attributes would be overwritten on the server when a
         * non-null value for this param is passed. Passing null is a no-op.
         * @type {Zendrive.ZendriveDriverAttributes}
         */
        public driverAttributes: ZendriveDriverAttributes;
        /**
         * Use this mode to control the SDK’s behaviour for detecting drives automatically.
         * This mode can be changed at a later point using @{@link Zendrive.setDriveDetectionMode} method.
         * @type {Zendrive.ZendriveDriveDetectionMode}
         */
        public driveDetectionMode: ZendriveDriveDetectionMode;
    }

    /**
     * @class Additional attributes of a Zendrive driver.
     * The application can specify both predefined and custom attributes for a driver.
     * These attributes are associated with a SDK driverId at SDK initialization time.
     * In addition to predefined special attributes, up to 4 custom key value attributes can be associated
     * with a driver using the Zendrive SDK.
     * @constructor
     */
    export class ZendriveDriverAttributes {
        public constructor() { }

        /**
         * First name of the user.
         * @type {String}
         */
        public firstName: string;

        /**
         * Last name of the user.
         * @type {String}
         */
        public lastName: string;

        /**
         * Email of the user.
         * @type {String}
         */
        public email: string;

        /**
         * A unique id that associates the current user to a group. This groupId will be made available
         * as a query parameter to filter users in the reports and API that Zendrive provides.
         * @type {String}
         */
        public group: string;

        /**
         * Phone number of the user without '+' character.
         * @type {String}
         */
        public phoneNumber: string;

        /**
         * The date in seconds at which the driver signed up/started using your application.
         * The date would be available in YYYY-MM-DD format on Zendrive dashboard
         * @type {number}
         */
        public driverStartDate: string;

        /**
         * Don't modify this directly, use setCustomAttribute(key, value)
         * @type {Object}
         */
        public customAttributes: any;

        /**
         * Set the custom attribute of the user, maximum 4 custom attributes are allowed.
         * @param {String} key - A key for the custom attribute. The maximum key length is 64 characters.
         * @param {String} value - Value of the custom attribute. The maximum value length is 64 characters.
         */
        public setCustomAttribute(key: string, value: string) {
            // TODO: Add success and error callbacks in case tried to set more than the
            // allowed number of attributes.
            this.customAttributes[key] = value;
        }
    }

    /**
     * @class Wrapper for meta-information related to a drive.
     * @constructor
     */
    export class ZendriveDriveInfo {
        public constructor() {
            this.averageSpeed = 0.0;
            this.distance = 0.0;
            this.endTimestamp = 0;
            this.startTimestamp = 0;
            this.isValid = true;
            this.waypoints = [];
        }

        /**
         * The average speed of trip in metres/second
         * @type {Number}
         */
        public averageSpeed: number;
        /**
         * The distance of the trip in metres
         * @type {Number}
         */
        public distance: number;
        /**
         * The end timestamp of trip in milliseconds since epoch
         * @type {Number}
         */
        public endTimestamp: number;
        /**
         * The start timestamp of trip in milliseconds since epoch.
         * @type {Number}
         */
        public startTimestamp: number;
        /**
         * Sometimes, the SDK detects that a drive is invalid after it has been started.
         * In these cases, the isValid property will be set to false and values for all other properties
         * in this class have default values.
         * @type {Boolean}
         */
        public isValid: boolean;
        /**
         * A list of @{@link Zendrive.ZendriveLocationPoint} objects corresponding to this trip in increasing order of
         * timestamp. The first point corresponds to trip start location and last to trip end location.
         * @type {Array}
         */
        public waypoints: ZendriveLocationPoint[];
    }

    /**
     * @class Wrapper for meta-information related to a active drive.
     * @constructor
     */
    export class ZendriveActiveDriveInfo {
        public constructor() {
            this.startTimeStamp = 0;
        }

        /**
         * The start timestamp of trip in milliseconds since epoch.
         * @type {Number}
         */
        public startTimeStamp: number;
        /**
         * tracking Id correlates apps internal data with the drive data.
         * @type {String}
         */
        public trackingId: string;
        /**
         * Identifier that identifies this session uniquely.
         * @type {String}
         */
        public sessionId: string;
    }

    /**
     * @class Information about start of a drive.
     * @constructor
     */
    export class ZendriveDriveStartInfo {
        public constructor() {
            this.startTimestamp = 0;
            this.startLocation = null;
        }

        /**
         * The start timestamp of trip in milliseconds since epoch.
         * @type {Number}
         */
        public startTimestamp: number;

        /**
         * The start location of the drive.
         *
         * If the drive is automatically detected by Zendrive SDK, then this location
         * is approximate and close to the start location but not exactly the start location.
         * @type {Zendrive.ZendriveLocationPoint}
         */
        public startLocation: ZendriveLocationPoint;
    }

    /**
     * @class Represents a geographical coordinate along with accuracy and timestamp information.
     * @constructor
     */
    export class ZendriveLocationPoint {
        public constructor() {
            this.latitude = 0.0;
            this.longitude = 0.0;
        }

        /**
         * Latitude in degrees
         * @type {Number}
         */
        public latitude: number;
        /**
         * Longitude in degrees
         * @type {Number}
         */
        public longitude: number;
    }


    /**
     * Dictates the functioning of Zendrive’s drive detection
     * @enum {number}
     */
    export enum ZendriveDriveDetectionMode {
        /**
         * Zendrive SDK will automatically track drives in background in this mode once the SDK is setup.
         *  At the same time, the application can invoke @{@link Zendrive.startDrive} to explicitly start
         *  recording a drive. This is the Default mode.
         * @type {Number}
         */
        ZendriveDriveDetectionModeAutoON = 0,

        /**
         * In this mode auto drive-detection is disabled. All other APIs on Zendrive can be invoked
         * independent of this mode. For recording trips in this mode, the application has to explicitly
         *  invoke the @{@link Zendrive.startDrive} method.
         * @type {Number}
         */
        ZendriveDriveDetectionModeAutoOFF = 1
    }

    export interface IProcessStartOfDriveCallback {
        (driveStartInfo: ZendriveDriveStartInfo): any;
    }

    export interface IProcessEndOfDriveCallback {
        (driveEndInfo: ZendriveDriveInfo): any;
    }

    export interface IProcessLocationDeniedCallback {
        (): any;
    }

    export interface ISetupSuccessCallback {
        (message: string): any;
    }

    export interface IFailureCallback {
        (message: string): any;
    }
}

function registerForDelegateCallbacks(zendriveCallback) {
    var callbackNotNull = (null != zendriveCallback);

    // We are allowing to clear out existing individual callbacks by sending null
    // for callback and the bool as false for first argument
    var processStartOfDriveCallback = callbackNotNull ? zendriveCallback.processStartOfDrive : null;
    cordova &&
        cordova.exec(processStartOfDriveCallback,
            null,
            "Zendrive",
            "setProcessStartOfDriveDelegateCallback", [(null != processStartOfDriveCallback)]);

    var processEndOfDriveCallback = callbackNotNull ? zendriveCallback.processEndOfDrive : null;
    cordova &&
        cordova.exec(processEndOfDriveCallback,
            null,
            "Zendrive",
            "setProcessEndOfDriveDelegateCallback", [(null != processEndOfDriveCallback)]);

    var processLocationDeniedCallback = callbackNotNull ? zendriveCallback.processLocationDenied : null;
    cordova &&
        cordova.exec(processLocationDeniedCallback,
            null,
            "Zendrive",
            "setProcessLocationDeniedDelegateCallback", [(null != processLocationDeniedCallback)]);
}