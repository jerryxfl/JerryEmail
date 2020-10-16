package com.edu.cdp.utils;

import android.content.Context;
import android.content.res.Resources;

public class AndroidUtils {
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}
