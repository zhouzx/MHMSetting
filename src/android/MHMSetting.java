package com.zhouzx;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.Gravity;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;



/**
 * Created by harri on 9/06/16.
 */
public class MHMSetting extends CordovaPlugin {
    /** Application context. */
    private Context mContext = null;
    /** Current callback context. */
    private CallbackContext mCbCtx = null;
    /** The Constant IBM_SOFTPHONE_PACKAGE_NAME. */
    
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        
        this.mContext = cordova.getActivity();
        this.mCbCtx = callbackContext;

        if(action.equals("openPreferencesScreen"))
        {
            
            // save for later response
            managerhub mnhub= ((managerhub) this.cordova.getActivity());
            mnhub.setCallCtx(callbackContext);
            
            Context context = mnhub.getApplicationContext();
            
            Intent intent=new Intent( context, NativeSettings.class);
            intent.addCategory(Intent.CATEGORY_PREFERENCE);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            
            mnhub.startActivityForResult(intent,mnhub.CHANGE_PUSH_SETTINGS);
            return true;
            
        }

        return false;
    }
}
