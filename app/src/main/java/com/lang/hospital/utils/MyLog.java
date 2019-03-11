package com.lang.hospital.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class MyLog {
    private static Context mContext;
    private static boolean logBackDoor = true;
    private static boolean debug = true;

    public static String HEADER = "HOSPITAL";

    public static void init(Context context, boolean save) {
        mContext = context;
        debug = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static void debug(String info) {
        printLog("debug", HEADER, info);
    }

    public static void error(String info) {
        printLog("error", HEADER, info);
    }

    public static void debug(String header, String info) {
        printLog("debug", header, info);
    }

    public static void error(String header, String info) {
        printLog("error", header, info);
    }

    private static void printLog(String type, String header, String info) {
        if (debug || logBackDoor) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            info += (" (" + stackTraceElements[4].getClassName() + "." + stackTraceElements[4].getMethodName()
                    + "()_" + stackTraceElements[4].getFileName() + "@" + stackTraceElements[4].getLineNumber()
                    + "_" + Thread.currentThread() + ")");
            switch (type) {
                case "error": {
                    Log.e(header, info);
                    break;
                }
                default: {
                    Log.d(header, info);
                    break;
                }
            }
        }
    }
}
