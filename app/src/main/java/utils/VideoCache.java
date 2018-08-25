package utils;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by dss-17 on 2/9/18.
 */

public class VideoCache {
    private static HttpProxyCacheServer sInstance;

    public static void initialize(Context context) {
        sInstance = new HttpProxyCacheServer(context);
    }

    public static HttpProxyCacheServer getInstance() {
        return sInstance;
    }
}
