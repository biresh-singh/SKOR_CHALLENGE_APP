package utils;

import android.content.Context;
import android.provider.Settings;


public class GetDeviceInfo {Context mContext;
    public GetDeviceInfo(Context context)
    {
        mContext=context;
    }
    public String getDeviceId() {
        String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

}
