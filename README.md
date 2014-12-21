zendrive-sdk-phonegap-plugin
============================
This is the official plugin for <a href="http://www.zendrive.com">zendrive</a> in Apache Cordova/PhoneGap!

<a href="http://www.zendrive.com">Zendrive</a> is commited to improving driving and transportation for everyone through better data and analytics.
<br/>
You can integrate zendrive-sdk-phonegap-plugin in your application to get Zendrive's driver centric analytics and insights for your fleet.

<h3>Zendrive plugin performs three key functions.</h3>
<ul style="list-style-position: inside">
<li> Automatically collects data from various sensors on mobile phone while minimizing the battery impact.</li>
<li> Provides end points to application to manually start/end drives.</li>
<li> Uploads the data back to Zendrive servers for analysis. </li>
</ul>

<p class="center">
<img src="http://developers.zendrive.com/static/img/dev_intro_1.png" /> 

<h3>How to integrate</h3>

<h4>Integrate zendrive phonegap plugin by running the following cordova command</h4>
<code>cordova plugin add https://github.com/zendrive/zendrive-sdk-phonegap-plugin</code>

<h4>Alternatively if you are using plugman in your application then use the following</h4>
<h5>For Android</h5>
<code>plugman install --platform android --project ./platforms/android --plugin https://github.com/zendrive/zendrive-sdk-phonegap-plugin.git</code>

<h5>For iOS</h5>
<code>plugman install --platform ios --project ./platforms/ios --plugin https://github.com/zendrive/zendrive-sdk-phonegap-plugin.git</code>


<h4>Alternatively if you are using phonegap build then add the following in your config.xml ( plugin only supported for android )</h4>
<code>&lt;gap:plugin name="com.zendrive.phonegap.sdk" version="1.2.1" /&gt;</code>


<h3>Enable Zendrive in the app</h3>
<h4>Initialize the SDK</h4>
<p>Typically this is done after the driver logs into the application and you know the identity of the driver.
After this call, Zendrive SDK will start automatic trip detection and collect driver data. You can also record trips manually from your application.</p>
```cordova.exec(callback, function(err){alert(err)},"Zendrive","setup", [ ZENDRIVE_APPLICATION_KEY, <DRIVER_ID>]);```

<ul style="list-style-position: inside">
<li>If you don't have the ZENDRIVE_APPLICATION_KEY, sign up on the Zendrive Developer Portal and get the key.</li>
<li>The <DRIVER_ID> is an ID for the driver currently using the application. Each driver using the application needs a unique ID</li>
</ul>

<h4>Manual Trip Tagging</h4>
<p>The Zendrive SDK works in the background and automatically detects trips and tracks driving behaviour. However, some applications already have knowledge of point-to-point trips made by the driver using the application. For example - a taxi metering app. If your application has this knowledge, it can indicate that to the Zendrive SDK explicitly.</p>
```cordova.exec(callback, function(err){alert(err)},"Zendrive","startDrive", [<TRACKING_ID>]);```
<p>The <strong>&lt;TRACKING_ID&gt;</strong> can be used to find Zendrive trips with this ID in the Zendrive Analytics API. See the SDK Reference for more details about these calls.</p>

<h4>Driving Sessions</h4>
<p>Some applications want to track multiple point-to-point trips together as a single entity. For example, a car rental app may want to track all trips made by a user between a rental car pickup and dropoff as a single entity. This can be done using sessions in the Zendrive SDK.
Sessions can be used in the Zendrive SDK by making the following calls.</p>
```cordova.exec(callback, function(err){alert(err)},"Zendrive","startSession", [<SESSION_ID>]);```
<p>All trips within a session are tagged with the <strong>&lt;SESSION_ID&gt;</strong>. The session id can then be used to lookup Zendrive trips belonging to this session using the Zendrive REST API.</p>

<h4>Disable the SDK</h4>
<p>To disable the SDK at any point in the application, you can invoke this method. The Zendrive SDK goes completely silent after this call and does not track any driving behaviour again.</p>
```cordova.exec(callback, function(err){alert(err)},"Zendrive","teardown", []);```
<p>The application needs to re-initalize the SDK to start tracking driving behaviour.</p>

<br/>
<br/>
<br/>
