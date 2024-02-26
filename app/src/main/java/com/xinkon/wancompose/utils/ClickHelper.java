package com.xinkon.wancompose.utils;


public class ClickHelper {
    private long mLastClickTime;
    private static ClickHelper sManager;


    private ClickHelper() {
    }

    public static synchronized ClickHelper getHelper() {
        if (sManager == null) {
            sManager = new ClickHelper();
        }

        return sManager;
    }

    public boolean isCanClick() {
        long nowTime = System.currentTimeMillis();
        if (nowTime - this.mLastClickTime >= 500L) {
            this.mLastClickTime = nowTime;
            return true;
        } else {
            this.mLastClickTime = nowTime;
            return false;
        }
    }
}

