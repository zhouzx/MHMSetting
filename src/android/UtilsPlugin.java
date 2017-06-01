package com.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.Gravity;
import android.widget.Toast;

import com.newrelic.agent.android.NewRelic;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by harri on 9/06/16.
 */
public class UtilsPlugin extends CordovaPlugin {
    /** Application context. */
    private Context mContext = null;
    /** Current callback context. */
    private CallbackContext mCbCtx = null;
    /** The Constant IBM_SOFTPHONE_PACKAGE_NAME. */
    private static final String IBM_SOFTPHONE_PACKAGE_NAME = "com.ibm.cio.softphone";
    /** Call method (device/IBM Softphone). */
    private String callMethod = "";
    
    /** The phone number. */
    private String phoneNubmer = "";
    
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        
        this.mContext = cordova.getActivity();
        this.mCbCtx = callbackContext;
        
        //        if(action.equals("newRelicRecordMetric")) {
        //            String name = args.getJSONObject(0).getString("name");
        //            String category = args.getJSONObject(0).getString("category");
        //            int value = args.getJSONObject(0).getInt("value");
        //            this.newRelicRecordMetric(name, category, value, callbackContext);
        //            return true;
        //        }
        
        if(action.equals("newRelicRecordEvent")) {
            String event = args.getJSONObject(0).getString("event");
            String method = args.getJSONObject(0).getString("method");
            String userID = args.getJSONObject(0).getString("userID");
            int state = args.getJSONObject(0).getInt("state");
            this.newRelicRecordEvent(event, method,userID,state, callbackContext);
            return true;
        }
        
        //        if(action.equals("newRelicRecordAccessTimes")) {
        //            String event = args.getJSONObject(0).getString("event");
        //            this.newRelicRecordAccessTimes(event, callbackContext);
        //            return true;
        //        }
        
        if(action.equals("newRelicRecordAccessTimesFacetUserID")) {
            String event = args.getJSONObject(0).getString("event");
            String userID = args.getJSONObject(0).getString("userID");
            this.newRelicRecordAccessTimesFacetUserID(userID, event, callbackContext);
            return true;
        }
        
        if(action.equals("newRelicSendFeedback")) {
            String userID = args.getJSONObject(0).getString("userID");
            String category = args.getJSONObject(0).getString("category");
            String content = args.getJSONObject(0).getString("content");
            
            this.newRelicSendFeedback(userID, category, content, callbackContext);
            return true;
        }
        
        /**
         * Handle call action based on call method.
         */
        if (action.equals("handleCall")) {
            this.phoneNubmer = args.getString(0);
            this.callMethod = args.getString(1);
            // Not run on UI thread, not block WebCore thread
            cordova.getThreadPool().execute(new Runnable() {
                
                @Override
                public void run() {
                    handleCall();
                    // callbackContext.success();
                }
            });
            
            return true;
        }
        
        if (action.equals("launchAppByURL")) {
            String ret = "false";
            String packageName = args.getString(0);
            String extraKey = args.getString(1);
            String extraValue = args.getString(2);
            
            if (this.launchAppByURL(packageName, extraKey, extraValue)) {
                ret = "true";
            }
            String responseText = "{'success': " + ret + "}";
            callbackContext.success(responseText);
            return true;
        }
        
        if (action.equals("openMap")){
            String packageName = args.getString(0);
            System.out.println(args.getString(0));
            String address = args.getString(1).replace("<br>", " ");
            System.out.println(packageName + ", " + address);
            openMap(packageName, address);
        }
        
        return false;
    }
    
    //    private void newRelicRecordMetric(String name, String category, int value, CallbackContext callbackContext) {
    //        NewRelic.recordMetric(name, category,value);
    //        callbackContext.success("record metric success!");
    //    }
    
    private void newRelicRecordEvent(String event, String method,String userID,int state,CallbackContext callbackContext) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("method", method);
        attributes.put("state", state);
        attributes.put("userID",userID);
        boolean eventRecorded = NewRelic.recordEvent(event, attributes);
        
        if (eventRecorded) {
            callbackContext.success("record event success!");
        }else {
            callbackContext.error("error");
        }
    }
    
    //    private void newRelicRecordAccessTimes(String event, CallbackContext callbackContext) {
    //
    //        Map<String, Object> attributes = new HashMap<String, Object>();
    //        attributes.put("event", event);
    //        boolean eventRecorded = NewRelic.recordEvent("AccessTimes", attributes);
    //        if (eventRecorded) {
    //            callbackContext.success("success!");
    //        }else {
    //            callbackContext.error("error");
    //        }
    //    }
    
    private void newRelicRecordAccessTimesFacetUserID(String userID,String event, CallbackContext callbackContext) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("UserID", userID);
        attributes.put("event", event);
        boolean eventRecorded = NewRelic.recordEvent("AccessTimes", attributes);
        if (eventRecorded) {
            callbackContext.success("success!");
        }else {
            callbackContext.error("error");
        }
    }
    
    private void newRelicSendFeedback(String userID, String category,String content, CallbackContext callbackContext) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("UserID", userID);
        attributes.put("feedbackCategory", category);
        attributes.put("feedbackContent", content);
        boolean eventRecorded = NewRelic.recordEvent("feedback", attributes);
        if (eventRecorded) {
            callbackContext.success("success!");
        }else {
            callbackContext.error("error");
        }
    }
    
    /**
     * Handle call action.
     */
    protected void handleCall() {
        PackageManager pm = mContext.getPackageManager();
        Intent appStartIntent = null;
        if ("Device Phone".equalsIgnoreCase(callMethod)) {//"Device Phone" = 1
            // Intent.ACTION_DIAL: shows a UI with the number being dialed
            // E.x HTC Dialer:
            // appStartIntent.setComponent(ComponentName.unflattenFromString("com.android.htcdialer/.Dialer"));
            // Intent.ACTION_CALL: Perform immediately a call, need
            // android.permission.CALL_PHONE
            // defect fix by yahuil@cn.ibm.com
            // I'm not sure but when I tap the number with simcard it would call
            // directly,not like that on iphone
            appStartIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                                                                      + phoneNubmer));
            List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(
                                                                         appStartIntent, 0);
            if (resolveInfoList.size() > 0) {
                // Logger.i(TAG, "pkName: " +
                // resolveInfoList.get(0).activityInfo.packageName + "|" +
                // resolveInfoList.get(0).activityInfo.applicationInfo.className);
                // Call the 1st phone app (default phone app)
                appStartIntent
                .setPackage(resolveInfoList.get(0).activityInfo.packageName);
            } else { // There aren't any phone app on device!!!
                appStartIntent = null;
            }
        } else if ("IBM Softphone".equalsIgnoreCase(callMethod)) {//"IBM Softphone" is 2
            try {
                appStartIntent = pm
                .getLaunchIntentForPackage(IBM_SOFTPHONE_PACKAGE_NAME);
                appStartIntent.setAction(Intent.ACTION_CALL);
                // appStartIntent.setAction(Intent.ACTION_DIAL); // phone number
                // not be able showed
                appStartIntent.setData(Uri.parse("stel:" + phoneNubmer));
            } catch (Exception e) {
                System.out.println("call method= ex"+e.getMessage());
                // IBM Softphone has been uninstalled before execute this action
                // Need alert user and update "Call Method Option" on Settings
                // view
            }
        }
        
        if (appStartIntent != null) {
            // appStartIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            appStartIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            mContext.startActivity(appStartIntent);
            mCbCtx.success();
        } else
            mCbCtx.error("handleCall error: phone application not found");
    }
    
    /**
     * Launch app by indicating the package name and URI
     *
     * @param packageName
     * @param extraKey
     * @param extraValue
     * @return boolean
     */
    protected boolean launchAppByURL(String packageName, String extraKey,
                                     String extraValue) {
        Context context = cordova.getActivity();
        try {
            PackageManager manager = cordova.getActivity().getPackageManager();
            Intent i;
            if (packageName.contains("://")) {
                i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setData(Uri.parse(packageName));
                context.startActivity(i);
            } else {
                i = manager.getLaunchIntentForPackage(packageName);
                if (i == null)
                    throw new PackageManager.NameNotFoundException();
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.putExtra(extraKey, extraValue);
                context.startActivity(i);
            }
        } catch (PackageManager.NameNotFoundException e) {
            String message = "App not found.  Unable to launch";
            
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
        return true;
    }
    
    public void openMap(String packageName, String address){
        System.out.println("OpenMap");
        try{
            if(packageName.equals("com.baidu.BaiduMap")) {
                System.out.println("OpenMap:----------");
                String urlMap = "intent://map/geocoder?address="+address+"#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                //String urlMap = "intent://map/place/search?query="+address+"#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                //Uri gmmIntentUri = Uri.parse(urlMap);
                Intent mapIntent = Intent.getIntent(urlMap);
                this.cordova.getActivity().startActivity(mapIntent);
            }else{
                String urlMap = "geo:0,0?q=" + address; // done google map
                Uri gmmIntentUri = Uri.parse(urlMap);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage(packageName);
                this.cordova.getActivity().startActivity(mapIntent);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
