package com.eliburgi.zenote.vendors;

import android.content.Context;

/**
 * Created by Elias on 14.01.2017.
 */

public class CompatUtils {
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
