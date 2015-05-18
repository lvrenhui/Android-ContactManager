package com.uc.contactmanager.common.tool;

/**
 * Created by lvrh on 2015/5/10.
 */

import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: xiejm
 * Date: 7/25/13
 * Time: 6:32 PM
 */
public class L {

    public static void d(String message, Object... args) {
        message = formatMessage(message, args);
        Log.d(getTag(), message);
    }

    public static void i(String message, Object... args) {
        message = formatMessage(message, args);

        Log.i(getTag(), message);
    }

    public static void w(String message, Object... args) {
        message = formatMessage(message, args);
        Log.w(getTag(), message);
    }


    public static void e(String message, Object... args) {
        message = formatMessage(message, args);
        Log.e(getTag(), message);
    }

    public static void e(Throwable e) {
        e.printStackTrace();
    }

    public static void v(String message, Object... args) {
        message = formatMessage(message, args);
        Log.v(getTag(), message);
    }

    public static void t(String message, Object... args) {
        v(message, args);
    }


    private static String formatMessage(String message, Object... args) {
        if (message == null) {
            return "";
        }
        if (args != null && args.length > 0) {
            try {
                return String.format(message, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    /**
     * 获取native日志tag
     *
     * @return
     */
    private static String getTag() {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[4];

        String className = stackTrace.getClassName();
        String tag = className.substring(className.lastIndexOf('.') + 1)
                + "." + stackTrace.getMethodName() + "#" + stackTrace.getLineNumber();
        return tag;
    }

    /**
     * 打印到std
     *
     * @param level
     * @param tag
     * @param message
     */
    private static void print(int level, String tag, String message) {
        if (message == null) {
            message = message + "";
        }
        switch (level) {
            case Log.VERBOSE:
                Log.v(tag, message);
                break;

            case Log.DEBUG:
                Log.d(tag, message);
                break;

            case Log.INFO:
                Log.i(tag, message);
                break;

            case Log.WARN:
                Log.w(tag, message);
                break;

            case Log.ERROR:
                Log.e(tag, message);
                break;

        }
    }

    public static void w(Exception e) {
            e.printStackTrace();
    }
}
