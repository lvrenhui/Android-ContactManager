package com.uc.contactmanager.common.tool;

import java.io.*;
import java.lang.Process;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.animation.AlphaAnimation;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Paint;
import android.provider.MediaStore;
import cn.ninegame.gamemanager.activity.MainActivity;
import cn.ninegame.gamemanager.app.*;
import cn.ninegame.gamemanager.app.state.EnvironmentState;
import cn.ninegame.gamemanager.biz.base.ui.CustomToast;
import cn.ninegame.gamemanager.biz.stat.BusinessStat;
import cn.ninegame.gamemanager.biz.util.PackageUtil;
import cn.ninegame.gamemanager.biz.util.RootPrivilegeManager;
import cn.ninegame.gamemanager.config.SharePrefConstant;
import cn.ninegame.gamemanager.lib.task.BackgroundHandler;
import cn.ninegame.gamemanager.lib.task.TaskExecutor;
import cn.ninegame.gamemanager.model.pojo.AnimationsToastInfo;
import cn.ninegame.gamemanager.module.ipc.BackProcMessenger;
import cn.ninegame.gamemanager.module.ipc.ProcessManager;
import cn.ninegame.gamemanager.module.message.Message;
import cn.ninegame.gamemanager.module.message.MessagePump;
import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import cn.ninegame.gamemanager.R;
import cn.ninegame.gamemanager.biz.account.core.util.StringUtil;
import cn.ninegame.gamemanager.biz.common.helper.InstalledGamesHelper;
import cn.ninegame.gamemanager.biz.common.listener.OnProgressUpdateListener;
import cn.ninegame.gamemanager.biz.fragment.PageSwitcher;
import cn.ninegame.gamemanager.biz.util.BusinessUtil;
import cn.ninegame.gamemanager.biz.util.NativeUtil;
import cn.ninegame.gamemanager.model.database.NineGameDAOFactory;
import cn.ninegame.gamemanager.model.database.dao.DownloadDAO;
import cn.ninegame.gamemanager.model.pojo.DownloadRecord;
import cn.ninegame.gamemanager.model.pojo.InstalledGameInfo;
import cn.ninegame.gamemanager.module.log.L;
import cn.ninegame.gamemanager.module.message.MessageData2;
import cn.uc.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: xiejm
 * Date: 8/17/12
 * Time: 10:02 AM
 */
public class Util {
    public static final int TIME_MINUTE_MILLIS = 60 * 1000;
    public static final int TIME_HOUR_MILLIS = 60 * TIME_MINUTE_MILLIS;
    public static final int TIME_DAY_MILLIS = 24 * TIME_HOUR_MILLIS;
    public static final int TIME_WEEK_MILLIS = 7 * TIME_DAY_MILLIS;
    public static final int TIME_YEAR_MILLIS = 365 * TIME_DAY_MILLIS;
    public final static int IMAGE_CACHE_TIME = 3600 * 24 * 3;      // in seconds, 3 days

    public static DateFormat YYYY_MM_DD_DATE_FORMAT;
    public static DateFormat YYYY_MM_DD_HH_MM_SS_FORMAT;
    public static DateFormat TIME_24_FORMAT;

    public static DateFormat YYYY_MM_DD_HH_MM_SS_FORMAT_FOR_SERVER;

    private static Pattern PATTERN_PLUS;

    public final static DecimalFormat ONE_DECIMAL_POINT_DF = new DecimalFormat("0.0");
    public final static DecimalFormat TWO_DECIMAL_POINT_DF = new DecimalFormat("0.00");
    private static ThreadLocal<StringBuilder> threadSafeStrBuilder;

    private static ThreadLocal<byte[]> threadSafeByteBuf;

    public static DateFormat DATE_FORMAT;

    private static int mScreenWidth = -1;
    private static int mScreenHeight = -1;
    private static float mDensity = -1;

    private static int[] M9_SECRET_KEY = new int[] {'f', '0', '2', 'a', '1', '7', '0', 'b', 'c', '7', 'f', 'c', 'b', '7', '1', '3'};
    private static final int PLATFORM_GAME = 5;

    private static Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public static Animation getBottomSlideInAnim() {
        Animation bottomSlideInAnim = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        bottomSlideInAnim.setDuration(150);
        return bottomSlideInAnim;
    }

    public static Animation getBottomSlideOutAnim() {
        Animation bottomSlideOutAnim = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
//      ANIM_BOTTOM_SLIDE_OUT.setInterpolator(new AnticipateOvershootInterpolator());
        bottomSlideOutAnim.setDuration(150);
        return bottomSlideOutAnim;
    }

    /*public static File getImageFilesDir(Context context) {
        File file = new File(context.getFilesDir().getAbsolutePath() + "/image");
        if (file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }*/

    /**
     * 读取流
     *
     * @param inStream
     * @return 字节数组
     * @throws Exception

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;

        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }

        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }*/

    /**
     * url编码
     *
     * @param s url字符串
     * @return 字节数组
     * @throws Exception
     */
    public static String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "utf-8");

        } catch (Exception e) {
            L.w(e);
            return s;
        }
    }

    /**
     * 从ImputStream里读取数字
     * @param is  InputStream
     * @return 读取的数字
     * **/
    public static int readNumberFromInputStream(InputStream is) {
        try {
            byte[] buffer = new byte[8];
            int len = is.read(buffer);
            StringBuilder sb = new StringBuilder();
            int i = 0;

            while (i < len && buffer[i] != 0x0a && buffer[i] != 0x0d) {
                sb.append((char)buffer[i]);
                ++i;
            }

            return Integer.parseInt(sb.toString());

        } catch (Exception e) {
            L.w(e);
        }

        return 0;
    }

    /**
     * 获取屏幕分辨率
     * @param context   Context
     * @return 宽x高
     * **/
    public static String getScreenResolution(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth() + "x" + display.getHeight();
    }

    /**
     * 获取md5值
     * @param str   需要md5的字符串
     * @return md后的字符串
     * **/
    public static String getMD5String(String str) {
        if (TextUtils.isEmpty(str))
        {
            return str;
        }

        try {
            if (EnvironmentState.getInstance().getVersionCode() > 7) { //因为之前java版md5获取有误，兼容前面版本
                return NativeUtil.getMd5(str);

            } else {
                return getMD5String(str.getBytes("utf-8"));
            }

        } catch (Exception e) {
            L.w(e);
        }

        return null;
    }

    @Deprecated
    /**
     * 此方法获取md5有错误，为了兼容前面版本，不能修改。
     * 小编推荐：NativeUtil.getMd5(str);
     */
    public static String getMD5String(byte[] bytes) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(bytes);
            byte md5Data[] = md.digest();
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < md5Data.length; ++i)
                hexString.append(Integer.toHexString(0xFF & md5Data[i]));

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            L.w(e);
        }

        return null;
    }

    // see http://stackoverflow.com/a/10469121/668963
    /*public static void setButtonBackgroundResource(Button btn, int resourceId) {
        int paddingLeft = btn.getPaddingLeft();
        int paddingTop = btn.getPaddingTop();
        int paddingRight = btn.getPaddingRight();
        int paddingBottom = btn.getPaddingBottom();
        btn.setBackgroundResource(resourceId);
        btn.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public static void setTextColorToBlack(Context context, TextView tv) {
        ColorStateList mColorStateListBlack = context.getResources().getColorStateList(R.color.text_color_black_selector);
        tv.setTextColor(mColorStateListBlack);
    }

    public static void setTextColorToWhite(Context context, TextView tv) {
        ColorStateList mColorStateListWhite = context.getResources().getColorStateList(R.color.text_color_gray_and_white_selector);
        tv.setTextColor(mColorStateListWhite);
    }*/

    /**
     * 获取线程安全的StringBuilder
     * **/
    public static StringBuilder getThreadSafeStringBuilder() {
        if (threadSafeStrBuilder == null) {
            threadSafeStrBuilder = new ThreadLocal<StringBuilder>();
        }

        StringBuilder sb = threadSafeStrBuilder.get();

        if (sb == null) {
            sb = new StringBuilder();
            threadSafeStrBuilder.set(sb);
        }

        sb.delete(0, sb.length());
        return sb;
    }

    public static byte[] getThreadSafeByteBuffer() {
        if (threadSafeByteBuf == null) {
            threadSafeByteBuf = new ThreadLocal<byte[]>();
        }

        byte[] buf = threadSafeByteBuf.get();

        if (buf == null) {
            buf = new byte[1024 * 4];   // 4kb
            threadSafeByteBuf.set(buf);
        }

        return buf;
    }

    public static void showKeyboard(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void showKeyboard(Context ctx, View token) {
        if(token == null){
            return;
        }

        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(token, InputMethodManager.SHOW_IMPLICIT);
    }
    /*public static boolean hideKeyboard(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isActive()) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
            return true;
        }

        return false;
    }*/

    public static boolean hideKeyboard(Context ctx, IBinder binder) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isActive()) {
            return imm.hideSoftInputFromWindow(binder, 0);
        }

        return false;
    }

    private final static Pattern singleQuotePatern = Pattern.compile("'");


    public static String escapeDBSingleQuotes(String s) {
        if (s == null)
            return null;

        return singleQuotePatern.matcher(s).replaceAll("\''");
    }

    /**
     * 检测sd卡是否已经挂载
     * **/
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 检查指定值在Preference里的boolean
     * @param ctx   Context
     * @param key   要检查的key值
     * @param defaultValue  默认值

    public static boolean isPreferenceSet(Context ctx, String key, boolean defaultValue) {
        return EnvironmentState.getInstance().getPreferences().getBoolean(key, defaultValue);
    }* **/

    /**
     * 递归删除文件
     * @param dir   需要删除的文件夹
     * @param includingSelf 自身是否删除
     * @return 删除的文件数
     * **/
    // return disk space revoked
    public static long deleteFilesRecursively(File dir, boolean includingSelf) {
        long length = 0;

        if (dir.exists()) {
            File[] files = dir.listFiles();

            if (files != null) {
                for (int i = 0; i < files.length; ++i) {
                    File f = files[i];

                    if (f.isDirectory()) {
                        deleteFilesRecursively(f, true);

                    } else {
                        length += f.length();
                        f.delete();
                    }
                }
            }
        }

        if (includingSelf)
            dir.delete();

        return length;
    }

    /**
     * 检测app是不是在运行
     * @param context   Context
     * @param appName   app name
     * @return true or false
     * **/
    public static boolean isAppRunning(Context context, String appName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo info : activityManager.getRunningAppProcesses()) {
            if (info.processName.equals(appName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检测service是不是在运行
     * @param context   Context
     * @param serviceName   service name
     * @return true or false
     * **/
    public static boolean isServiceRunning(Context context, String serviceName) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            for (ActivityManager.RunningServiceInfo info : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceName.equals(info.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            L.w(e);
        }


        return false;
    }

    /**
     * 格式化时间，例如20041216
     * @param  timeMillis时间戳
     * @return 格式化后的字符串
     * **/
    public static String formatTimeDate(long timeMillis) {
        if (YYYY_MM_DD_DATE_FORMAT == null) {
            YYYY_MM_DD_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
        }

        synchronized (YYYY_MM_DD_DATE_FORMAT) {
            return YYYY_MM_DD_DATE_FORMAT.format(timeMillis);
        }
    }

    /**
     * 格式化时间，例如20041216093000
     * @param  timeMillis时间戳
     * @return 格式化后的字符串
     * **/
    public static String formatTime_YYYY_MM_DD_HH_MM_SS(long timeMillis) {
        if (YYYY_MM_DD_HH_MM_SS_FORMAT == null) {
            YYYY_MM_DD_HH_MM_SS_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
        }

        synchronized (YYYY_MM_DD_HH_MM_SS_FORMAT) {
            return YYYY_MM_DD_HH_MM_SS_FORMAT.format(timeMillis);
        }
    }

    /**
     * 格式化时间，24小时制
     * @param date  需要格式化的日期
     * @return 格式化后的字符串
     *
    public static String formatTime24(Date date) {
    synchronized (TIME_24_FORMAT) {
    return TIME_24_FORMAT.format(date);
    }
    } **/

    /**
     * 格式化时间，24小时制
     * @param timeMillis时间戳
     * @return 格式化后的字符串
     * **/
    public static String formatTime24(long timeMillis) {
        if (TIME_24_FORMAT == null) {
            TIME_24_FORMAT = new SimpleDateFormat("HH:mm");
        }

        synchronized (TIME_24_FORMAT) {
            return TIME_24_FORMAT.format(timeMillis);
        }
    }

    /**
     * 时间显示的约定：显示的分钟数以服务器返回时间为准，10分钟以内显示xx分钟前，超过10分钟，当天的就写今天，其它时间写mm-dd
     *
     * @param currentTime
     * @param lastTime
     * @return "" if error occurs
     */
    /*public static String formatTimeShort(Context context, long currentTime, long lastTime) {
        long intervalTimeMills = currentTime - lastTime;
        if(intervalTimeMills < 0) return "";

        if(intervalTimeMills <= Util.TIME_MINUTE_MILLIS * 10) {
            int min = (int)(intervalTimeMills/Util.TIME_MINUTE_MILLIS);
            return min+context.getString(R.string.minute);
        } else if(intervalTimeMills <= Util.TIME_DAY_MILLIS) {
            return context.getString(R.string.today);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            return sdf.format(lastTime);
        }
    }*/

    /**
     * 替换url里的空格
     * @param url   url字符串
     * @return 替换后的结果
     * **/
    public static String replacePlusWithPercent20(String url) {
        if (url.contains("+")) {
            if (PATTERN_PLUS == null) {
                PATTERN_PLUS = Pattern.compile("\\+");
            }

            url = PATTERN_PLUS.matcher(url).replaceAll("%20");
        }

        return url;
    }

    private final static int ONE_GIGABYTE = 1024 * 1024 * 1024;
    private final static int ONE_MEGABYTE = 1024 * 1024;
    private final static int ONE_KILOBYTE = 1024;
    /**
     * 格式化容量大小
     * @param sizeInByte    long类型的字节数
     * @return 格式后的大小
     * **/
    public static String formatSizeInByte(long sizeInByte) {
        if (sizeInByte >= ONE_GIGABYTE)
            return ONE_DECIMAL_POINT_DF.format((double) sizeInByte / ONE_GIGABYTE) + "GB";

        else if (sizeInByte >= ONE_MEGABYTE)
            return ONE_DECIMAL_POINT_DF.format((double) sizeInByte / ONE_MEGABYTE) + "MB";

        else if (sizeInByte >= ONE_KILOBYTE)
            return ONE_DECIMAL_POINT_DF.format((double) sizeInByte / ONE_KILOBYTE) + "KB";

        else
            return sizeInByte + "B";
    }

    /**
     * 格式化容量大小 返回单位为MB
     * @param sizeInByte    long类型的字节数
     * @return 格式后的大小
     * **/
    public static String formatSizeInByteToMB(long sizeInByte) {
        return ONE_DECIMAL_POINT_DF.format((double) sizeInByte / ONE_MEGABYTE) + "MB";
    }

    private final static int ONE_HOUR = 3600;
    private final static int ONE_MINUTE = 60;

    /**
     * 格式化时间
     * @param seconds   待格式化的秒钟数
     * @return 格式后的时间
     * **/
    public static String formatTimeInSecond(int seconds) {
        StringBuilder sb = new StringBuilder();

        if (seconds >= ONE_HOUR) {
            sb.append(seconds / ONE_HOUR).append("小时");
            seconds %= ONE_HOUR;
        }

        if (seconds >= ONE_MINUTE) {
            int minutes = seconds / ONE_MINUTE;

            if (minutes >= 10)
                sb.append(minutes);

            else
                sb.append('0').append(minutes);

            sb.append("分");
            seconds %= ONE_MINUTE;
        }

        if (seconds > 0) {
            if (seconds >= 10)
                sb.append(seconds);

            else
                sb.append('0').append(seconds);

            sb.append("秒");
        }

        return sb.toString();
    }

    /**
     * 格式化数字
     * @param number   待格式化的数字
     * @return 格式后的数字
     * **/
    public static String formatNumberInPercent(double number) {
        if (number > 100) {
            number = 100;
        }
        return ONE_DECIMAL_POINT_DF.format(number) + "%";
    }

    public static void closeCloseable(Closeable obj) {
        try {
            // 修复小米MI2的JarFile没有实现Closeable导致崩溃问题
            if (obj instanceof Closeable && obj != null)
                obj.close();

        } catch (IOException e) {
            L.w(e);
        }
    }

    public static void closeHttpEntity(HttpEntity en) {
        if (en != null) {
            try {
                en.consumeContent();

            } catch (IOException e) {
                L.w(e);
            }
        }
    }

    /*public static File filePathPreProc(String pathstr) {
        pathstr = pathstr.replaceAll("\\\\", "/").trim();
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("(^\\.|^/|^[a-zA-Z])?:?/.+(/$)?");
        java.util.regex.Matcher m = p.matcher(pathstr);

        //不符合要求直接返回
        if (!m.matches()) {
            return null;
        }

        //这里开始文件名已经符合要求
        File path = new File(pathstr);
        return path;
    }*/

    /**
     * 判断sdcard是否有足够的空间
     *
     * @param fileSize
     * @return
     */
    public static boolean isEnoughForFile(long fileSize) {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory()
                                   .getAbsolutePath());
//        //sd卡分区数
//        int blockCounts = statFs.getBlockCount();
        //sd卡可用分区数
        int avCounts = statFs.getAvailableBlocks();
        //一个分区数的大小
        long blockSize = statFs.getBlockSize();
        //sd卡可用空间
        long spaceLeft = avCounts * blockSize;

        if (spaceLeft < fileSize) {
            return false;
        }

        return true;
    }

    /*public static boolean unzipFile(String zipFileName, File destDir) {
        return unzipFile(zipFileName, destDir, null);
    }*/

    private final static int USE_FILE_COUNT_FOR_UNZIP_PROGRESS_THRESHOLD = 10;

    /**
     * 解压文件夹
     * @param zipFileName   zip文件名
     * @param destDir   解压目录
     * @param listener  进度更新回调
     * **/
    public static boolean unzipFile(String zipFileName, File destDir, OnProgressUpdateListener listener) {
        final byte[] buffer = new byte[4096];
        BufferedInputStream bis = null;
        ZipInputStream zis = null;

        try {
            if (listener != null) {
                listener.onPrepare();
            }

            // make sure the directory is existent
            destDir.mkdirs();
            bis = new BufferedInputStream(new FileInputStream(zipFileName));
            zis = new ZipInputStream(bis);
            ZipEntry entry = null;
            long totalSize = 0;
            int fileCount = 0;

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory())
                    totalSize += entry.getSize();

                fileCount++;
            }

            if (listener == null || fileCount >= USE_FILE_COUNT_FOR_UNZIP_PROGRESS_THRESHOLD) {
                unzipUsingFileCountForProgressUpdate(zipFileName, destDir, listener, buffer, fileCount);

            } else {
                unzipUsingFileSizeForProgressUpdate(zipFileName, destDir, listener, buffer, totalSize);
            }

            if (listener != null) {
                listener.onComplete();
            }

            return true;

        } catch (IOException e) {
            L.w(e);
        } finally {
            Util.closeCloseable(bis);
            Util.closeCloseable(zis);
        }

        if (listener != null) {
            listener.onError();
        }

        return false;
    }

    /**
     * 获取zip文件中的根目录
     * @param zipFileName   zip文件名
     * @return the root dirname or null if none is found
     * **/
    public static String getRootDirNameInZipFile(String zipFileName) {
        ZipInputStream zis = null;

        try {
            zis = new ZipInputStream(new FileInputStream(zipFileName));
            ZipEntry entry;

            if ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                int slashIndex = fileName.indexOf('/');

                if (slashIndex != -1)
                    return fileName.substring(0, slashIndex);

                if (entry.isDirectory())
                    return fileName;
            }

        } catch (IOException e) {
            L.w(e);

        } finally {
            Util.closeCloseable(zis);
        }

        return null;
    }

    /**
     * 解压文件数目，用于进度更新
     * @param fileCount   zip文件count
     * @param destDir   解压目录
     * @param listener  进度更新回调
     * @param buffer  解压的文件字节流
     * **/
    private static void unzipUsingFileCountForProgressUpdate(String zipFileName, File destDir, OnProgressUpdateListener listener, byte[] buffer, int fileCount) {
        int count = 0;
        ZipInputStream zis = null;

        try {
            // make sure the directory is existent
            destDir.mkdirs();
            zis = new ZipInputStream(new FileInputStream(zipFileName));
            ZipEntry entry;
            int bufSize = 1024 * 32;

            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();

                if (entry.isDirectory()) {
                    new File(destDir, fileName).mkdirs();

                } else {
                    File file = new File(destDir, fileName);
                    File parent = file.getParentFile();

                    if (parent != null && !parent.exists())
                        parent.mkdirs();

                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), bufSize);
                    int lenRead;

                    while ((lenRead = zis.read(buffer)) != -1) {
                        bos.write(buffer, 0, lenRead);
                    }

                    bos.flush();
                    bos.close();
                }

                if (listener != null) {
                    listener.onProgressUpdate((int) (++count * 100f / fileCount));
                }

                zis.closeEntry();
            }

        } catch (IOException e) {
            L.w(e);
        } finally {
            Util.closeCloseable(zis);
        }
    }

    /**
     * 解压文件大小，用于进度更新
     * @param totalSize   zip文件 size
     * @param destDir   解压目录
     * @param listener  进度更新回调
     * @param buffer  解压的文件字节流
     * **/
    private static void unzipUsingFileSizeForProgressUpdate(String zipFileName, File destDir, OnProgressUpdateListener listener, byte[] buffer, long totalSize) {
        ZipInputStream zis = null;

        try {
            // make sure the directory is existent
            destDir.mkdirs();
            zis = new ZipInputStream(new FileInputStream(zipFileName));
            ZipEntry entry;
            int bufSize = 1024 * 32;
            long totalWrite = 0;
            int lastUpdatedProgress = 0;

            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();

                if (entry.isDirectory()) {
                    new File(destDir, fileName).mkdirs();

                } else {
                    File file = new File(destDir, fileName);
                    File parent = file.getParentFile();

                    if (parent != null && !parent.exists())
                        parent.mkdirs();

                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), bufSize);
                    int lenRead;

                    while ((lenRead = zis.read(buffer)) != -1) {
                        bos.write(buffer, 0, lenRead);
                        totalWrite += lenRead;
                        int progress = (int)(totalWrite * 100 / totalSize);

                        if (progress > lastUpdatedProgress) {
                            if (listener != null) {
                                listener.onProgressUpdate(progress);
                            }

                            lastUpdatedProgress = progress;
                        }
                    }

                    bos.flush();
                    bos.close();
                }

                zis.closeEntry();
            }

        } catch (IOException e) {
            L.w(e);
            if(listener != null)
                listener.onError();
        } finally {
            Util.closeCloseable(zis);
        }
    }

    /**
     * 解压文件
     * @param destDir   解压目录
     * @param fis  解压的文件流
     * **/
    public static boolean unzipFile(InputStream fis, File destDir) {
        final byte[] buffer = new byte[4096];
        ZipInputStream zis = null;

        try {
            // make sure the directory is existent
            destDir.mkdirs();
            zis = new ZipInputStream(fis);
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();

                if (entry.isDirectory()) {
                    new File(destDir, fileName).mkdirs();

                } else {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destDir, fileName)));
                    int lenRead;

                    while ((lenRead = zis.read(buffer)) != -1) {
                        bos.write(buffer, 0, lenRead);
                    }

                    bos.close();
                }

                zis.closeEntry();
            }

            return true;

        } catch (IOException e) {
            L.w(e);
        } finally {
            Util.closeCloseable(zis);
        }

        return false;
    }

    /**
     * 删除文件
     * @param path  需要删除的文件路径
     * **/
    public static void deleteFile(String path) {
        if (path != null) {
            File file = new File(path);

            if (file.exists())
                file.delete();
        }
    }

    /**
     * 取得文件名满足所指定的规则表达式的文件列表 ";"隔开
     */
    /*public static FilenameFilter getFileExtensionFilterByExpStr(String exp) {
        final String[] expArr = exp.split(";");
        return new FilenameFilter() {
            public boolean accept(File file, String name) {
                boolean flag = false;

                for (int i = 0; i < expArr.length; i++) {
                    if (name.endsWith(expArr[i]))
                        flag = true;

                    ;
                }

                return flag;
            }
        };
    }*/


    // from stackoverflow.com: http://stackoverflow.com/a/3549021/668963
    /*public static Bitmap decodeFile(File f, int maxWidth, int maxHeight) {
        Bitmap b = null;

        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            int scale = 1;

            if (o.outHeight > maxHeight || o.outWidth > maxWidth) {
                scale = (int)Math.pow(2, (int) Math.round(Math.log(maxWidth /
                                      (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

        } catch (IOException e) {
            L.w(e);
        }

        return b;
    }*/

    /**
     * 获取apk的签名
     * @param apkPath   apk路径
     * @return apk签名
     * **/
    public static String getAPKSignatures(String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";

        try {
            // apk包的文件路径
            // 这是一个Package 解释器, 是隐藏的
            // 构造函数的参数只有一个, apk文件的路径
            // PackageParser packageParser = new PackageParser(apkPath);
            Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
            Class<?>[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            Object pkgParser = pkgParserCt.newInstance(valueArgs);
            // 这个是与显示有关的, 里面涉及到一些像素显示等等, 我们使用默认的情况
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            typeArgs = new Class[4];
            typeArgs[0] = File.class;
            typeArgs[1] = String.class;
            typeArgs[2] = DisplayMetrics.class;
            typeArgs[3] = Integer.TYPE;
            Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", typeArgs);
            valueArgs = new Object[4];
            valueArgs[0] = new File(apkPath);
            valueArgs[1] = apkPath;
            valueArgs[2] = metrics;
            valueArgs[3] = PackageManager.GET_SIGNATURES;
            Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);
            typeArgs = new Class[2];
            typeArgs[0] = pkgParserPkg.getClass();
            typeArgs[1] = Integer.TYPE;
            Method pkgParser_collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates", typeArgs);
            valueArgs = new Object[2];
            valueArgs[0] = pkgParserPkg;
            valueArgs[1] = PackageManager.GET_SIGNATURES;
            pkgParser_collectCertificatesMtd.invoke(pkgParser, valueArgs);
            // 应用程序信息包, 这个公开的, 不过有些函数, 变量没公开
            Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField("mSignatures");
            Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
            return info[0].toCharsString();

        } catch (Exception e) {
            L.w(e);
        }

        return null;
    }


    /**
     * 判断邮箱格式是否正确
     * @param strEmail
     * @return
     *     true:正确
     *     false:错误
     */
    public static boolean isEmail(String strEmail) {
        //final String emailPattern = "^([a-z0-9A-Z])+([a-z0-9A-Z_]{0,})@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        //final String emailPattern2 = "[a-zA-Z0-9]{1,}[a-zA-Z0-9_-]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}";
        //final String emailPattern3 = "^([a-z0-9A-Z])+([a-z0-9A-Z_]?)@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        final String emailPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        final Pattern pattern = Pattern.compile(emailPattern);
        final Matcher matcher = pattern.matcher(strEmail);

        if (matcher.matches()) {
            return true;
        }

        return false;
    }

    /**
     * 获取拓展卡大小
     * **/
    public static long getExternalStorageSize() {
        String status = Environment.getExternalStorageState();

        // 是否只读
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            status = Environment.MEDIA_MOUNTED;
        }

        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                long sdSize = totalBlocks * blockSize;
                return sdSize;

            } catch (IllegalArgumentException e) {
                L.w(e);
                status = Environment.MEDIA_REMOVED;
            }
        }

        return 0;
    }

    /**
     * 获取Android数据目录大小
     * **/
    public static long getInternalStorageSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 是否存在sdcard
     * **/
    public static boolean existSDcard() {
        boolean isExistSDcard = false;
        long lExternalSize = getExternalStorageSize();
        long lInternalSize = getInternalStorageSize();

        if (lExternalSize != 0 && lExternalSize != lInternalSize)
            isExistSDcard = true;

        return isExistSDcard;
    }

    /**
     * 获取sdcard总大小以及可用大小
     * return  MessageData2<已用大小Long, 总大小Long>
     *
     */
    /*private static String maxSdcardDir;//最大的sdcard
    public static MessageData2<Long, Long> getSdcardSize(){
        MessageData2<Long, Long> sdcardSizeMessage = null;
        String status = Environment.getExternalStorageState();

    //        // 是否只读
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            status = Environment.MEDIA_MOUNTED;
        }
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                if(maxSdcardDir==null || "".equals(maxSdcardDir)){
                    String SDCARD_PARENT_DIR = "/mnt";
                    File parentDir = new File(SDCARD_PARENT_DIR);
                    File[] childfiles = parentDir.listFiles();

                    if(childfiles == null || childfiles.length==0)
                        return sdcardSizeMessage;

                    long maxDirSize = 0;
                    long fileLength = 0;
                    StatFs stat;
                    long blockSize;
                    long totalBlocks;
                    for(File file: childfiles){
                        String SDCARD_STRING = "sdcard";
                        if(file.getName().toLowerCase().contains(SDCARD_STRING)){
                            stat = new StatFs(file.getPath());
                            blockSize = stat.getBlockSize();
                            totalBlocks = stat.getBlockCount();
                            fileLength = totalBlocks * blockSize;
                            if(file.isDirectory() && fileLength> maxDirSize){
                                maxDirSize = fileLength;
                                maxSdcardDir = file.getPath();
                            }
                        }
                    }
                }

                StatFs stat = new StatFs(maxSdcardDir);
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                long availableBlocks = stat.getAvailableBlocks();
                Long sdSize = totalBlocks * blockSize;
                Long sdAvail = availableBlocks * blockSize;
                sdcardSizeMessage = new MessageData2<Long, Long>(sdSize-sdAvail, sdSize);
            } catch (IllegalArgumentException e) {
                status = Environment.MEDIA_REMOVED;
            }
        }
        return sdcardSizeMessage;
    }*/
    //又改回用默认的sd卡大小了
    public static MessageData2<Long, Long> getSdcardSize() {
        MessageData2<Long, Long> sdcardSizeMessage = null;
        String status = Environment.getExternalStorageState();

        // 是否只读
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            status = Environment.MEDIA_MOUNTED;
        }

        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                long availableBlocks = stat.getAvailableBlocks();
                Long sdSize = totalBlocks * blockSize;
                Long sdAvail = availableBlocks * blockSize;
                sdcardSizeMessage = new MessageData2<Long, Long>(sdSize - sdAvail, sdSize);

            } catch (IllegalArgumentException e) {
                L.w(e);
                status = Environment.MEDIA_REMOVED;
            }
        }

        return sdcardSizeMessage;
    }
    /**
     * 获取手机内存总大小以及可用大小
     * return  MessageData2<已用大小Long, 总大小Long>
     *
     */
    /*public static MessageData2<Long, Long> getMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long totalBlocks = stat.getBlockCount();
        Long memoryAvail = availableBlocks * blockSize;
        Long memorySize = totalBlocks * blockSize;
        return new MessageData2<Long, Long>(memorySize - memoryAvail, memorySize);
    }*/

    /**
     * 更新手机内存使用情况， 如果有外置sdcard则显示sdcard，没有则只显示手机内存
     * @param list
     *          List<MessageData2<Long, Long>> list.size=2  0:手机内存  1：sdcard
     *          <Long, Long>已用大小，总大小
     * ***/
    public static List<MessageData2<String, Integer>> updateSize(List<MessageData2<Long, Long>> list) {
        List<MessageData2<String, Integer>> returnMessageData = null;

        if (list != null && list.size() > 1) {
            returnMessageData = new ArrayList<MessageData2<String, Integer>>(2);
            MessageData2<Long, Long> memoryMessage = list.get(0);
            MessageData2<Long, Long> sdcardMessage = list.get(1);
            MessageData2<String, Integer> message;

            if (memoryMessage != null) {
                if (sdcardMessage != null && !memoryMessage.o2.equals(sdcardMessage.o2)) {
                    String sdcardString = Util.formatSizeInByte(sdcardMessage.o1) + "已用/" + Util.formatSizeInByte(sdcardMessage.o2);
                    double progressSdcard = sdcardMessage.o1 / (double)sdcardMessage.o2 * 100;
                    message = new MessageData2<String, Integer>(sdcardString, (int)progressSdcard);
                    returnMessageData.add(message);
                }

                String memoryString = Util.formatSizeInByte(memoryMessage.o1) + "已用/" + Util.formatSizeInByte(memoryMessage.o2);
                double progressMemory = memoryMessage.o1 / (double)memoryMessage.o2 * 100;
                message = new MessageData2<String, Integer>(memoryString, (int)progressMemory);
                returnMessageData.add(message);
            }
        }

        return returnMessageData;
    }


    /**
     * 重命名
     * @param oldPath   原文件路径
     * @param newPath   新文件路径
     * **/
    public static void rename(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);

        if (oldFile.exists())
            oldFile.renameTo(newFile);

//        if (!NativeUtil.renameExt(oldPath, newPath)) {
//            File oldFile = new File(oldPath);
//            try {
//                InputStream is = new FileInputStream(oldFile);
//                OutputStream os = new FileOutputStream(oldPath);
//                byte[] buffer = getThreadSafeByteBuffer();
//
//                int lenRead;
//                while ((lenRead = is.read(buffer)) != -1) {
//                    os.write(buffer, 0, lenRead);
//                }
//
//                is.close();
//                os.close();
//
//                if (oldFile.exists())
//                    oldFile.delete();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//            }
//        }
    }

    public static void copyFile(String srcPath, String destPath) {
        File srcFile = new File(srcPath);

        if (srcFile.isFile()) {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            File destFile = new File(destPath);

            try {
                fis = new FileInputStream(srcFile);
                fos = new FileOutputStream(destFile);
                byte[] buffer = new byte[2048];
                int lenRead;

                while ((lenRead = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, lenRead);
                }

            } catch (Exception e) {
                L.w(e);
                if (destFile.exists())
                    destFile.delete();

            } finally {
                closeCloseable(fis);
                closeCloseable(fos);
            }
        }
    }

    public static String keyFromGameIdAndPkgName(int gameId, String pkgName) {
        return gameId + pkgName;
    }

    /**
     * 获取cpu 核心
     * @return cpu核心数
     * **/
    public static int getCpuCores() {
        try {
            File cpuDir = new File("/sys/devices/system/cpu");
            return cpuDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return Pattern.matches("cpu[0-9]", filename);
                }
            }).length;

        } catch (Exception e) {
            L.w(e);
            return 1;
        }
    }

    /**
     * 获取cpu频率
     * @return 使用频率
     * **/
    public static int getCpuFreq() {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")));
            return Integer.parseInt(br.readLine());

        } catch (Exception e) {
            L.w(e);

        } finally {
            closeCloseable(br);
        }

        return 0;
    }

    /**
     * get screen width
     * @return
     */
    public static int getScreenWidth(Context context) {
        if (mScreenWidth == -1) {
            getScreenProperties(context);
        }

        return mScreenWidth;
    }

    /**
     * get screen height
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (mScreenHeight == -1) {
            getScreenProperties(context);
        }

        return mScreenHeight;
    }

    private static void getScreenProperties(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mScreenWidth = display.getWidth();
        mScreenHeight = display.getHeight();
    }

    /**
     * convert px from dp
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = getScreenDensity(context);
        return (int)(dpValue * scale + 0.5f);
    }

    /**
     * convert dp from px
     */
    /*public static int px2dip(Context context, float pxValue) {
        float scale = getScreenDensity(context);
        return (int)(pxValue / scale + 0.5f);
    }*/

    /**
     * get screen density
     * @param context
     * @return
     */
    public static float getScreenDensity(Context context) {
        if (mDensity == -1) {
            mDensity = context.getResources().getDisplayMetrics().density;
        }

        return mDensity;
    }

    /*public static String formateTimeByTimestamp(long timestamp) {
        try {
            if (DATE_FORMAT == null) {
                DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }

            return DATE_FORMAT.format(new Date(timestamp));

        } catch (Exception e) {
            L.w(e);
        }

        return "";
    }*/

    public final static long randomRequestId() {
        long ret = System.currentTimeMillis();
        ret = ret << 10;
        ret |=   0x03FF & new Random().nextInt(1024);
        return ret;
    }


    public static boolean isGoodJson(String json) {
        if (StringUtil.isNullOrEmpty(json)) {
            return false;
        }
        try {
            new JSONObject(json);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
    /**
     * 重新实现JSON的getLong方法
     * @param jsObj
     * @param key
     * @return
     * @throws JSONException
     */
    public static long getJSONLong(JSONObject jsObj, String key) {
        return getJSONLong(jsObj, key, 0L);
    }

    /**
     * 重新实现JSON的getLong方法
     * @param jsObj
     * @param key
     * @return
     * @throws JSONException
     */
    public static long getJSONLong(JSONObject jsObj, String key, long defValue) {
        try {
            Object object = jsObj.get(key);
            return object instanceof Number ? ((Number) object).longValue()
                   : Long.parseLong((String) object);

        } catch (Exception e) {
            L.w(e);
        }

        return defValue;
    }

    /*public static Bundle jsonToBundle(JSONObject jsonObject) {
        Iterator<String> it = jsonObject.keys();
        Bundle bundle = new Bundle();
        while (it.hasNext()) {
            String key = it.next();
            bundle.putString(key, jsonObject.optString(key));
        }
        return bundle;
    }*/

    /**
     * 获取下载文件的剩余大小
     * @param gameId
     * @param pkgName
     * @return
     */
    /*public static long getDownloadFileRemainSize(int gameId, String pkgName) {
        long ret = -1;

        try {
            DownloadRecord record = NineGameDAOFactory.getDAO(DownloadDAO.class).getDownloadRecordByGameIdAndPkgName(gameId, pkgName);

            if (record != null) {
                ret = record.fileLength - record.downloadedBytes;
            }

        } catch (Exception e) {
            L.w(e);
        }

        return ret;
    }*/

    /**
     * @param time 单位是毫秒
     * */
    public static String toTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;

        if (hour > 0) {
            return String.format("%d小时%d分%d秒", hour, minute, second);

        } else {
            if (minute > 0) {
                return String.format("%d分%d秒", minute, second);

            } else {
                return String.format("%d秒", second);
            }
        }
    }

    public static String formatSecond(double second){
        return TWO_DECIMAL_POINT_DF.format(second) + "秒";
    }

    /**
     * 格式化容量大小 返回单位为MB
     * @param sizeInByte    long类型的字节数
     * @return 格式后的大小
     * **/
    public static double formatByteToMB(long sizeInByte) {
        return (double) sizeInByte / ONE_MEGABYTE;
    }

    public static boolean isToday(long when) {
        return isSameDay(when, System.currentTimeMillis());
    }

    public static boolean isSameDay(long firstTime, long secondTime) {
        Time time = new Time();
        time.set(firstTime);
        int firstYear = time.year;
        int firstMonth = time.month;
        int firstMonthDay = time.monthDay;

        time.set(secondTime);
        return (firstYear == time.year)
               && (firstMonth == time.month)
               && (firstMonthDay == time.monthDay);
    }

    /*public static String getMyThreadTime(long myThreadTime) {
        Time time = new Time();
        time.set(myThreadTime);
        if(isSameYear(myThreadTime,System.currentTimeMillis())){
            return time.month+1+"月"+time.monthDay+"日";
        }else{
            return time.year+"年"+(time.month+1)+"月"+time.monthDay+"日";
        }

    }*/


    public static String getNearTime(long createTime){
        try {
            String ret = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long create = createTime;
            Calendar now = Calendar.getInstance();
            long ms  = 1000*(now.get(Calendar.HOUR_OF_DAY)*3600+now.get(Calendar.MINUTE)*60+now.get(Calendar.SECOND));//毫秒数
            long ms_now = now.getTimeInMillis();
            if(ms_now-create<ms){
                ret = "今天";
            }else if(ms_now-create<(ms+24*3600*1000)){
                ret = "昨天";
            }else if(ms_now-create<(ms+24*3600*1000*2)){
                ret = "前天";
            }else {
                int i=0;
                for(i=0;ms_now-create>(ms+24*3600*1000*i) && i<=30;i++){

                }
                if(i<=30){
                    ret = i+"天前";
                }
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isSameYear(long firstTime, long secondTime) {
        Time time = new Time();
        time.set(firstTime);
        int firstYear = time.year;

        time.set(secondTime);
        return (firstYear == time.year);
    }

    /*public static boolean isSameYearAndMonth(long firstTime, long secondTime) {
        Time time = new Time();
        time.set(firstTime);
        int firstYear = time.year;
        int firstMonth = time.month;
        time.set(secondTime);
        return (firstYear == time.year && firstMonth == time.month);
    }*/


    public static int getYear(long currentTime) {
        Time time = new Time();
        time.set(currentTime);
        int year = time.year;
        return year;
    }

    public static int getDay(long currentTime) {
        Time time = new Time();
        time.set(currentTime);
        int year = time.monthDay;
        return year;
    }

    public static int getMonth(long currentTime) {
        Time time = new Time();
        time.set(currentTime);
        int year = time.month;
        return year;
    }

    /**
     * 用于判断距离上一次调用是否进入新的一天
     * @return
     */
    public static boolean isNewDay(long when) {
        return !Util.isToday(when);
    }

    /**
     * 取总的ram大小
     * @return
     */
    public static int getTotalRamInKiloBytes() {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/meminfo")));
            String totalRam = br.readLine().split("\\s+")[1];   // the first line is: MemTotal:        xxxx kB
            return Integer.parseInt(totalRam);

        } catch (Exception e) {
            L.w(e);

        } finally {
            closeCloseable(br);
        }

        return 0;
    }

    /**
     * 获取sdcard可用大小
     */
    public static long getAvailExternalStorageSizeInKiloBytes() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED) || status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                return availableBlocks * blockSize / 1024;  // in kilobytes

            } catch (Exception e) {
                L.w(e);
            }
        }

        return 0;
    }
    /**
     * 获取手机存储可用大小
     */
    public static long getAvailInternalStorageSizeInKiloBytes() {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize / 1024;  // in kilobytes

        } catch (Exception e) {
            L.w(e);
        }

        return 0;
    }

    public static long getTotalAvailStorageSizeInKiloBytes() {
        return getAvailInternalStorageSizeInKiloBytes() + getAvailExternalStorageSizeInKiloBytes();
    }
    /**
     * 是否竖屏
     * */
    /*public boolean isPortrait(Context context) {
        Configuration cf = context.getResources().getConfiguration();
        int ori = cf.orientation ;
        return ori == Configuration.ORIENTATION_PORTRAIT;
    }*/

    /**
     * GetJsonObject的封装
     * @param object
     * @param key
     * @return
     */
    /*public static Object getJsonObject(JSONObject object, String key){
        try {
            return object.has(key) ? object.get(key) : null;
        } catch (JSONException e) {
            L.w(e);
            return null;
        }
    }*/

    public static String getJSONString(JSONObject jsonObj, String key, String defalutValue) {
        if(jsonObj == null) return defalutValue;

        try {
            if(jsonObj.has(key)) {
              return jsonObj.getString(key);
            }
        } catch (JSONException e) {
            L.w(e);
        }

        return defalutValue;
    }

    public static String getJSONString(JSONObject jsonObj, String key) {
         return getJSONString(jsonObj, key, null);
    }


    public static String getJSONString(JSONArray jsonArr, int index) {
        if(jsonArr == null) return null;

        try {
            return jsonArr.getString(index);
        } catch (JSONException e) {
            L.w(e);
        }

        return null;
    }

    public static int getJSONInt(JSONObject jsonObj, String key, int defValue) {
        if(jsonObj == null) return defValue;

        try {
            if (jsonObj.has(key))
                return  jsonObj.getInt(key);

        } catch (Exception e) {
            L.w(e);
        }

        return defValue;
    }


    public static boolean getJSONBoolean(JSONObject jsonObj, String key) {
        if(jsonObj == null) return false;

        try {
            if(jsonObj.has(key)) {
                return jsonObj.getBoolean(key);
            }

        } catch (JSONException e) {
            L.w(e);
        }

        return false;
    }

    public static void putObject(JSONObject jsonObj, String key, Object obj) {
        if(jsonObj == null || key == null) return;

        try {
            jsonObj.put(key, obj);
        } catch (JSONException e) {
        }
    }

    /**
     * get JSONArray object from the json object , or return null if failed
     * @param jsonObj
     * @param key
     * @return
     */
    public static JSONArray getJSONArray(JSONObject jsonObj, String key) {
        if(jsonObj == null) return null;

        try {
            if(jsonObj.has(key)) {
                return jsonObj.getJSONArray(key);
            }
        } catch (Exception e) {
            L.w(e);
        }

        return null;
    }

    /**
     * get json object from json array with an index in the array
     * and return null if failed
     *
     * @param jsonArr
     * @param index
     * @return
     */
    public static JSONObject getJSONObject(JSONArray jsonArr, int index) {
        if(jsonArr == null) return null;

        try {
            return jsonArr.getJSONObject(index);
        } catch (JSONException e) {
            L.w(e);
        }

        return null;
    }


    /*public static int getJSONInt(JSONArray jsonArr, int index) {
        if(jsonArr == null) return -1;

        try {
            return jsonArr.getInt(index);
        } catch (JSONException e) {
            L.w(e);
        }

        return -1;
    }*/


    /*public static String getJSONString(JSONArray jsonArr, int index) {
        if(jsonArr == null) return null;

        try {
            return jsonArr.getString(index);
        } catch (JSONException e) {
            L.w(e);
        }

        return null;
    }*/


    /**
     * convenience method for convert json string to json object
     * @param jsonStr
     * @return: the json object convert from the json string or null if convert failed
     */
    public static JSONObject toJSONObject(String jsonStr) {
        if(jsonStr == null) return null;

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            L.w(e);
        }

        return jsonObject;
    }

    public static JSONArray toJSONArray(String jsonStr) {
        if(jsonStr == null) return null;

        JSONArray jsonArr = null;
        try {
            jsonArr = new JSONArray(jsonStr);
        } catch (JSONException e) {
            L.w(e);
        }

        return jsonArr;
    }

    /**
     * silent method for putting json object
     *
     * @param originJSON
     * @param key
     * @param value
     */
    public static void putJSONObject(JSONObject originJSON, String key, JSONObject value) {
        try {
            //we not judge originJSON here, because we need developer find the problem in there code
            originJSON.put(key, value);
        } catch (JSONException e) {
            L.w(e);
        }
    }

    public static void putJSONInt(JSONObject originJSON, String key, int value) {
        try {
            //we not judge originJSON here, because we need developer find the problem in there code
            originJSON.put(key, value);
        } catch (JSONException e) {
            L.w(e);
        }
    }

    public static void putJSONString(JSONObject originJSON, String key, String value) {
        try {
            //we not judge originJSON here, because we need developer find the problem in there code
            originJSON.put(key, value);
        } catch (JSONException e) {
            L.w(e);
        }
    }

    /**
     * get json object quietly
     * @param originJSON
     * @param key
     * @return
     */
    public static JSONObject getJSONObject(JSONObject originJSON, String key) {
        if(originJSON == null) return null;

        JSONObject jsonObj = null;
        try {
            if(originJSON.has(key)) {
                jsonObj = originJSON.getJSONObject(key);
            }
        } catch (Exception e) {
            L.w(e);
        }

        return jsonObj;
    }

    /**
     * get object quietly
     * @param originJSON
     * @param key
     * @return
     */
    public static Object getObject(JSONObject originJSON, String key) {
        if(originJSON == null) return null;

        Object obj = null;
        try {
            if(originJSON.has(key)) {
                obj = originJSON.get(key);
            }
        } catch (Exception e) {
            L.w(e);
        }

        return obj;
    }

    public static boolean isScreenlandspace(WindowManager windowManager) {
        Display display = windowManager.getDefaultDisplay();
        return display.getWidth() > display.getHeight();
    }

    /**
     * set data source for media player
     * @param resources
     * @param player
     * @param res
     * @throws IOException
     */
    public static void setDataSourceFromResource(Resources resources,
            MediaPlayer player, int res) throws IOException {
        AssetFileDescriptor afd = resources.openRawResourceFd(res);

        if (afd != null) {
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                                 afd.getLength());
            afd.close();
        }
    }

    /*public final static String appendStrings(final String... values) {
        if (values == null || values.length <= 0) {
            return null;
        }

        if (values.length == 1) {
            return values[0];
        }

        StringBuilder sb = new StringBuilder(values.length * 6);

        for (String value : values) {
            sb.append(value);
        }

        String key = sb.toString();
        return key;
    }*/

    /**
     * 根据随机范围获取随机数
     *
     * @param
     *      intValue  随机范围
     * @return
     *      随机所得
     */
    public static Integer getRandom(Integer intValue) {
        Random random = new Random();
        Integer randomValue;

        if (intValue != null) {
            randomValue = Math.abs(random.nextInt(intValue));

        } else {
            randomValue = Math.abs(random.nextInt());
        }

        return randomValue;
    }

    /**
     * m9编码
     * @param data 需要编码的byte[]
     * @return 编码后的byte[]
     * **/
    /*public static byte[] m9Encode(byte[] data) {
        return MessageDigest.m9Encode(PLATFORM_GAME, data, M9_SECRET_KEY);
    }*/

    /**
     * m9解码  this method is not thread-safe
     * @param data  需要解码的byte[]
     * @return 解码后的byte[]
     * **/

    public synchronized static int[] m9Decode(byte[] data) {
        MessageDigest.m9Decode(data, M9_SECRET_KEY);
        return new int[] { MessageDigest.M9_DECODE_DEST_OFFSET, data.length - MessageDigest.M9_DECODE_DEST_OFFSET_LENGTH };
    }

    public static List<MyItemDataWrapper> loadRecordsList() {
        List<MyItemDataWrapper> itemDataWrapperList = new ArrayList<MyItemDataWrapper>();
        NineGameClientApplication app = NineGameClientApplication.getInstance();
        JSONObject gameSort = BusinessUtil.getPlayGameSort();
        InstalledGamesHelper installedGamesHelper = InstalledGamesHelper.getInstance();
        List<InstalledGameInfo> installedGameInfoList = installedGamesHelper.getInstalledGameList();

        JSONObject gameSortObj;
        long lastPlayTime;

        // load the installed apps
        for (InstalledGameInfo installedGameInfo : installedGameInfoList) {
            boolean isGame = InstalledGamesHelper.getInstance().isGame(installedGameInfo.packageName);

            if (!isGame) {
                continue;
            }

            MyItemDataWrapper itemDataWrapper = new MyItemDataWrapper(installedGameInfo);

            if (gameSort.has(installedGameInfo.packageName)) {
                try {
                    gameSortObj = gameSort.getJSONObject(installedGameInfo.packageName);

                    if (gameSortObj.has(BusinessUtil.PLAY_GAME_SORT_LASTOPEN_TIME)) {
                        lastPlayTime = gameSortObj.getLong(BusinessUtil.PLAY_GAME_SORT_LASTOPEN_TIME);
                        itemDataWrapper.lastPlayTime = lastPlayTime;

                        if (lastPlayTime == 0 && BusinessUtil.isWithinSevenDays(getFirstInstallTime(installedGameInfo))) {
                            itemDataWrapper.isNewGame = true;
                        }

                    }

                } catch (JSONException e) {
                    L.w(e);
                }
            }

            itemDataWrapperList.add(itemDataWrapper);
        }

        sortList(itemDataWrapperList, gameSort);
        return itemDataWrapperList;
    }

    /**
     * 列表排序
     *
     * 排序方式：分为3组，第一组是7天内（自然天）新安装的且没玩过的游戏（从非客户端安装的也算）；第二组是已玩过的游戏；第三组是7天后新安装的且没玩过的游戏（从非客户端安装的也算）；
     * 组间排序：第一组＞第二组＞第三组
     * 组内排序--第一组：安装时间距离现在最近的新安装的排在前面。
     * 组内排序--第二组：最近玩的排在最前；
     * 组内排序--第三组：安装时间距现在最近的在前。
     * ***/
    private static void sortList(List<MyItemDataWrapper> itemDataWrapperList, JSONObject gameSort) {
        if (gameSort != null && gameSort.length() > 0) {
            Collections.sort(itemDataWrapperList, new Comparator<MyItemDataWrapper>() {
                @Override
                public int compare(MyItemDataWrapper o1, MyItemDataWrapper o2) {
                    if (o1.isNewGame && !o2.isNewGame) {
                        return -1;

                    } else if (!o1.isNewGame && o2.isNewGame) {
                        return 1;

                    } else {
                        if (o1.lastPlayTime == null && o2.lastPlayTime != null) {
                            return o2.lastPlayTime > 0 ? 1 : -1;

                        } else if (o1.lastPlayTime != null && o2.lastPlayTime == null) {
                            return o1.lastPlayTime > 0 ? -1 : 1;

                        } else if (o1.lastPlayTime != null && o2.lastPlayTime != null) {
                            if (o1.lastPlayTime > 0 && o2.lastPlayTime <= 0) {
                                return -1;

                            } else if (o1.lastPlayTime <= 0 && o2.lastPlayTime > 0) {
                                return 1;

                            } else if (o1.lastPlayTime == 0 && o2.lastPlayTime == 0) {
                                long o1Timestamp = getTimestamp(o1);
                                long o2Timestamp = getTimestamp(o2);
                                return o1Timestamp > o2Timestamp ? -1 : 1;

                            } else {
                                return o1.lastPlayTime > o2.lastPlayTime ? -1 : 1;
                            }

                        } else {
                            long o1Timestamp = getTimestamp(o1);
                            long o2Timestamp = getTimestamp(o2);
                            return o1Timestamp > o2Timestamp ? -1 : 1;
                        }
                    }
                }

                private long getTimestamp(MyItemDataWrapper itemDataWrapper) {
                    if (Build.VERSION.SDK_INT >= 9)
                        return itemDataWrapper.installedGameInfo.lastUpdateTime;

                    else
                        return new File(itemDataWrapper.installedGameInfo.sourceDir).lastModified();
                }
            });

        } else {
            Collections.sort(itemDataWrapperList, new Comparator<MyItemDataWrapper>() {
                @Override
                public int compare(MyItemDataWrapper o1, MyItemDataWrapper o2) {
                    long o1Timestamp = getTimestamp(o1);
                    long o2Timestamp = getTimestamp(o2);
                    return o1Timestamp > o2Timestamp ? -1 : 1;
                }
                private long getTimestamp(MyItemDataWrapper itemDataWrapper) {
                    if (Build.VERSION.SDK_INT >= 9)
                        return itemDataWrapper.installedGameInfo.lastUpdateTime;

                    else
                        return new File(itemDataWrapper.installedGameInfo.sourceDir).lastModified();
                }
            });
        }
    }

    public static long getFirstInstallTime(InstalledGameInfo info) {
        if (Build.VERSION.SDK_INT >= 9)
            return info.firstInstallTime;

        else
            return new File(info.sourceDir).lastModified();
    }

    public static class MyItemDataWrapper {
        public InstalledGameInfo installedGameInfo;
        public boolean isNewGame = false;
        public Long lastPlayTime;

        public MyItemDataWrapper(InstalledGameInfo installedGameInfo) {
            this.installedGameInfo = installedGameInfo;
        }
    }

    /**
     * 计算速度(数字)
     *
     * **/
    public static double getProcess(long downloadedBytes, long fileLength) {
        if (fileLength == 0) {
            return 0;
        } else {
            return (downloadedBytes * 100f / fileLength);
        }
    }

    /***
     * 从字符串时间格式获取long类型毫秒数
     * @param time 字符串时间
     * ***/
    public static long getTimeFromString(String time){
        if(TextUtils.isEmpty(time))
            return 0;

        if (YYYY_MM_DD_HH_MM_SS_FORMAT_FOR_SERVER == null) {
            YYYY_MM_DD_HH_MM_SS_FORMAT_FOR_SERVER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        long returnValue = 0;
        try {
            Date date = YYYY_MM_DD_HH_MM_SS_FORMAT_FOR_SERVER.parse(time);
            returnValue = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public static boolean existSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    public static boolean hasEnoughRamToPlayDemo() {
        ActivityManager am = (ActivityManager) NineGameClientApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);

        final long playDemoMemoryByte = 90L * 1024 * 1024;// 运行试玩需要的内存
        if (memoryInfo.availMem > (playDemoMemoryByte + memoryInfo.threshold)) {
            return true;
        }

        return false;
    }

    /**
     *判断是否有足够内存运行
     * @param memory
     * @return
     */
    public static boolean hasEnoughRamToPlay(long memory) {
        ActivityManager am = (ActivityManager) NineGameClientApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);

        final long playDemoMemoryByte = memory * 1024 * 1024;// 运行试玩需要的内存
        if (memoryInfo.availMem > (playDemoMemoryByte + memoryInfo.threshold)) {
            return true;
        }

        return false;
    }


    /**
     * 比较两个版本号
     * @param version1 版本号1
     * @param version2 版本号2
     * @return @return 0 if version1 = version2, less than 0 if version1 &lt; version2, and greater than 0 if version1 &gt; version2
     * @throws IllegalArgumentException
     * @throws NumberFormatException
     */
    public static int versionCompareTo(String version1, String version2) throws IllegalArgumentException, NumberFormatException {
        if (TextUtils.isEmpty(version1) || TextUtils.isEmpty(version2)) {
            throw new IllegalArgumentException("compare version can not be null.");
        }
        if (!version1.contains(".") || !version2.contains(".")) {
            throw new IllegalArgumentException("version format error, version should contains '.'");
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        if (version1Array.length != version2Array.length) {
            throw new IllegalArgumentException("compare version's length is not the same");
        }
        for (int i = 0; i < version1Array.length; i++) {
            int splitVersion1 = Integer.parseInt(version1Array[i]);
            int splitVersion2 = Integer.parseInt(version2Array[i]);
            int splitResult = splitVersion1 < splitVersion2 ? -1 : (splitVersion1 == splitVersion2 ? 0 : 1);
            if (splitResult != 0) {
                return splitResult;
            }
        }
        return 0;
    }

    /**
     * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。
     * 对于Android 2.3（Api Level 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
     *
     * @param context 上下文
     * @param packageName  应用程序的包名
     */
    public static void showInstalledAppDetails(Context context, String packageName) {
        final String SCHEME = "package";
        /**
         * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
         */
        final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
        /**
         * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
         */
        final String APP_PKG_NAME_22 = "pkg";
        /**
         * InstalledAppDetails所在包名
         */
        final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
        /**
         * InstalledAppDetails类名
         */
        final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        try {
            if (context instanceof NineGameClientApplication) {
                L.d("start pkg activity from application");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            L.e(e);
        }
    }

    public static void showMiuiInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        String rom = getSystemProperty();
        if ("V6".equals(rom)) {
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
        } else {
            showInstalledAppDetails(context,packageName);
            return;
        }
        Activity ativity = PageSwitcher.peekActivityStack();
        if (isIntentAvailable(ativity, intent)) {
            ativity.startActivityForResult(intent, 2);
        } else {
            L.e("Intent is not available!");
        }
    }

    @TargetApi(9)
    /*public static void openAppDetailActivity(Context context, String packageName) {
        Intent intent = null;
        if (Build.VERSION.SDK_INT >= 9) {
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", packageName, null);
            intent.setData(uri);}
//        } else {
//            final String className = Build.VERSION.SDK_INT == 8 ?
//                    SETTINGS_APPDETAILS_CLASS_NAME_22 : SETTINGS_APPDETAILS_CLASS_NAME_B21;
//            intent = new Intent(Intent.ACTION_VIEW);
//            intent.setClassName(SETTINGS_PACKAGE_NAME, SETTINGS_APPDETAILS_CLASS_NAME);
//            intent.putExtra(className, packageName);
//        }
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            L.e("intent is not available!");
        }
    }*/

    public static String getSystemProperty() {
        String line = null;
        BufferedReader reader = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name" );
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = reader.readLine();
            return line;
        } catch (IOException e) {
            L.e(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //IoUtils.close(reader);
        }
        return "UNKNOWN";
    }
    /**
     * 应用程序是否打开了显示浮窗的开关（部分rom试用，如小米）
     * @param context 当前应用程序的上下文
     * @return boolean
     */
    public static boolean hasOpenedFloatingWindow(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        if (applicationInfo == null) {
            return true;
        }
        Class<? extends ApplicationInfo> clazz = applicationInfo.getClass();
        Field[] fields = clazz.getFields();
        for (Field f : fields) {
            if (f.getName().equals("FLAG_SHOW_FLOATING_WINDOW")) {
                try {
                    int i = f.getInt(context.getApplicationInfo());
                    int flags = context.getApplicationInfo().flags;
                    if ((flags & i) == i) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (IllegalArgumentException e) {
                    L.w(e);
                } catch (IllegalAccessException e) {
                    L.w(e);
                } catch (Exception e) {
                    L.w(e);
                }
            }
        }
        return true;
    }

    /**
     * 判断MIUI的悬浮窗权限
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isMiuiFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if(!Build.MANUFACTURER.equals("Xiaomi")) {
            return true;
        }
        if (version >= 19) {
            return checkOp(context, 24);  //自己写就是24 为什么是24?看AppOpsManager
        } else {
            return hasOpenedFloatingWindow(context);
/*            if ((context.getApplicationInfo().flags & 1<<27) == 1<<27) {
                return true;
            } else {
                return false;
            }*/
        }
        //return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                if (AppOpsManager.MODE_ALLOWED == (Integer)invokeMethod(manager, "checkOp", op,
                        Binder.getCallingUid(), context.getPackageName())) {  //这儿反射就自己写吧
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                L.e(e.getMessage());
            }
        } else {
            L.e("Below API 19 cannot invoke!");
        }
        return false;
    }

    private static Integer invokeMethod(AppOpsManager manager, String methodName, int op,
            int callingUid, String packageName) {
        Class<AppOpsManager> c = AppOpsManager.class;
        try {
            Method method = c.getMethod(methodName, int.class, int.class, String.class);
            if (method != null) {
                method.setAccessible(true);
                Object object = method.invoke(manager, op, callingUid, packageName);
                return (Integer) object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (context == null) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

    public static File createTmpSubFolder(Context mContext, String name) {
        File tmp = createTmpFolder(mContext);
        return mkDir(tmp.getAbsolutePath() + File.separator + name);
    }

    public static File createTmpFolder(Context mContext) {
        return createCacheFolder(mContext, "tmp");
    }

    public static File createCacheFolder(Context mContext, String name) {
        String cacheDir;

        if (Util.isSDCardMounted() && mContext.getExternalFilesDir(null)!=null) {
            cacheDir = mContext.getExternalFilesDir(null).getAbsolutePath() + File.separator + name;

        } else {
            cacheDir = mContext.getFilesDir().getAbsolutePath() + File.separator + name;
        }

        return mkDir(cacheDir);
    }

    private static File mkDir(String path) {
        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result = null;
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentURI, proj, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = contentURI.getPath();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    /**
     * 精确计算文字展示宽度
     * ****/
    public static int getTextWidth(Paint paint, String str, float fontSize) {
        if(paint == null) {
            paint = mTextPaint;
            paint.setTextSize(fontSize);
        }
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    /**
     * 根据用户生日计算年龄
     * @param birthday 生日（yyyy-MM-dd格式）
     * @return 年龄
     */
    public static int getAgeByBirthday(String birthday) {
        if (TextUtils.isEmpty(birthday)) {
            return -1;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return getAgeByBirthday(format.parse(birthday));
        } catch (ParseException e) {
            L.w(e);
        }
        return -1;
    }

    /**
     * 获取当前手机的总内存大小（
     * @param context
     * @return
     */
    public static long getTotalMemory(Context context) {
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 16) {
            try {
                ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
                actManager.getMemoryInfo(memInfo);
                return memInfo.totalMem;
            } catch (Exception e) {
                L.w(e);
            }
        } else {
            String str1 = "/proc/meminfo";
            String str2;
            String[] arrayOfString;
            long initial_memory = 0;
            try {
                FileReader localFileReader = new FileReader(str1);
                BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
                str2 = localBufferedReader.readLine();//meminfo
                arrayOfString = str2.split("\\s+");
                //total Memory
                initial_memory = Integer.valueOf(arrayOfString[1]) * 1024;
                localBufferedReader.close();
                return initial_memory;
            } catch (IOException e) {
                L.w(e);
            } catch (Exception e) {
                L.w(e);
            }
        }
        return -1;
    }

    /**
     * 给一个View设置透明度
     * @param view 需要设置透明度的view
     * @param alpha 透明度值
     */
    public static void setAlpha(View view, float alpha) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(alpha, alpha);
        alphaAnimation.setDuration(0);
        alphaAnimation.setFillAfter(true);
        //设置透明度
        view.startAnimation(alphaAnimation);
    }

     /**
     * 根据用户生日计算年龄
     */
    public static int getAgeByBirthday(Date birthday) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthday)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }
        return age;
    }

    /**
     * 根据访问时间计算已访问天数
     * @param visitTime 访问时间（yyyy-MM-dd HH:mm:ss格式）
     * @return 年龄
     */
    public static int getDayFromOldTime(String visitTime) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date dt1 = null;
        try {
            dt1 = format.parse(visitTime);
            long visitDay = dt1.getTime()/(1000*60*60*24);
            long nowDay = System.currentTimeMillis()/(1000*60*60*24);
            return (int)(nowDay - visitDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 传入资源id大于0为正确的id，其它为错误id，此时对应的数据为空
     *
     * @param titleResId
     * @param contentResId
     * @param iconDrawableId
     * @param duration
     */
    public static void showToastMessage(int titleResId, int contentResId, int iconDrawableId, int duration) {
        Resources res = NineGameClientApplication.getInstance().getResources();
        String title = res.getString(titleResId);
        String content = contentResId > 0 ? res.getString(contentResId) : null;
        showToastMessage(title, content, iconDrawableId, duration);
    }

    public static void showToastMessage(String title, String content, int iconDrawableId, int duration) {
        AnimationsToastInfo info = new AnimationsToastInfo(title, content, duration, iconDrawableId, 0);
        if (!ProcessManager.getInstance().isMainProcess()) {
            BackProcMessenger.send(Message.Type.SHOW_ANIMATIONS_TOAST.ordinal(), info);
        } else {
            // 如果非特殊情况，主线程初始化,Toast在MainActivity初始化
            MessagePump.getInstance().broadcastMessage(Message.Type.SHOW_ANIMATIONS_TOAST, info, Message.PRIORITY_NORMAL);
        }
    }

    public static void showToastMessage(String title) {
        if(TextUtils.isEmpty(title)) return;
        Util.showToastMessage(title, null, CustomToast.NO_ICON, CustomToast.LENGTH_SHORT);
    }

    public static void showToastMessage(int titleRes) {
        Util.showToastMessage(NineGameClientApplication.getInstance().getString(titleRes), null, CustomToast.NO_ICON, CustomToast.LENGTH_SHORT);
    }

    public static void showToastMessage(String title, int iconDrawableId) {
        Util.showToastMessage(title, null, iconDrawableId, CustomToast.LENGTH_SHORT);
    }

    /***
     * 判断是否需要去更新
     **/
    public static boolean needCheckNewVersion() {
        boolean needCheck = false;

        try {
            boolean shouldCheckNewVersion = !BusinessUtil.isWmChannel(NineGameClientApplication.getInstance());
            long sevenDays = 3600 * 1000 * 24 * 7;
            long lastRejectUpgradeTimestamp = EnvironmentState.getInstance().getPreferences().getLong(SharePrefConstant.PREFS_KEY_LAST_REJECT_UPGRADE_TIMESTAMP, 0);

            if (lastRejectUpgradeTimestamp == 0 || lastRejectUpgradeTimestamp + sevenDays < System.currentTimeMillis()) {
                if (!shouldCheckNewVersion) {
                    File dir = new File(NineGameClientApplication.getInstance().getPackageManager().getPackageInfo(NineGameClientApplication.getInstance().getPackageName(), 0).applicationInfo.sourceDir);
                    String firstStartTimestampKey = "tmp_first_start_ts";
                    long ts = EnvironmentState.getInstance().getPreferences().getLong(firstStartTimestampKey, 0);

                    if (ts == 0 || ts < dir.lastModified()) {
                        ts = System.currentTimeMillis();
                        EnvironmentState.getInstance().getPreferences().edit().putLong(firstStartTimestampKey, ts).commit();
                    }
                    shouldCheckNewVersion = ts + sevenDays < System.currentTimeMillis();
                }
                // 安装后7天内不提示更新
                if (shouldCheckNewVersion) {
                    needCheck = true;
                }
            }

        } catch (Exception e) {
            L.w(e);
        }

        return needCheck;
    }

    public static synchronized String getUUID() {
        String uuid = EnvironmentState.getInstance().getPreferences().getString(SharePrefConstant.PREFS_KEY_UUID, null);

        if (uuid != null) {
            return uuid;
        }

        uuid = UUID.randomUUID().toString();
        EnvironmentState.getInstance().getPreferences().edit().putString(SharePrefConstant.PREFS_KEY_UUID, uuid).commit();
        return uuid;
    }

    public static void genUUIDAndPostDeviceSpecs() {
        getUUID();
    }

    public static void checkDeviceRootState() {
        if (!EnvironmentState.getInstance().getPreferences().getBoolean(SharePrefConstant.PREFS_KEY_CHECKED_ROOT_STATE, false)) {
            if (RootPrivilegeManager.isThisDeviceRooted()) {
                BusinessStat.getInstance().addStat("root`1``");

            } else {
                BusinessStat.getInstance().addStat("root`0``");
            }

            EnvironmentState.getInstance().getPreferences().edit().putBoolean(SharePrefConstant.PREFS_KEY_CHECKED_ROOT_STATE, true).commit();
        }
    }

    public static void deleteCachedImageFiles() {
        TaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    long curTimeStamp = System.currentTimeMillis() / 1000;
                    File dir = BusinessUtil.getImageFilesDir(NineGameClientApplication.getInstance());
                    String dirStr = dir.getAbsolutePath();
                    String filePathArr[] = dir.list();

                    for (int i = 0; i < filePathArr.length; ++i) {
                        if (NativeUtil.lastAccessTime(dirStr + "/" + filePathArr[i]) + IMAGE_CACHE_TIME < curTimeStamp) {
                            new File(dir, filePathArr[i]).delete();
                        }
                    }

                } catch (Exception e) {
                    L.w(e);
                }
            }
        });

    }


    public static PendingIntent getOpenMainAppPendingIntent() {
            Intent openMainAppIntent = new Intent(NineGameClientApplication.getInstance(), MainActivity.class);
            openMainAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openMainAppIntent.putExtra(MainActivity.INTENT_REQUEST, MainActivity.INTENT_REQUEST_JUMP_TO_MY_GAMES_DOWNLOAD_PAGE);
            openMainAppIntent.setType("helloworld");
            return PendingIntent.getActivity(NineGameClientApplication.getInstance(), 0, openMainAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 客户端启动就清除标识（标识用于从消息推送进入）
     */
    public static void clearNotificationsMark() {
        BackgroundHandler.execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = EnvironmentState.getInstance().getPreferences().edit();
                editor.remove(SharePrefConstant.PREFS_KEY_FROM_NOTIFICATIONS_UPGRADE);
                editor.remove(SharePrefConstant.PREFS_KEY_FROM_NOTIFICATIONS_SPECIAL);
                editor.remove(SharePrefConstant.PREFS_KEY_FROM_NOTIFICATIONS_DETAIL);
                editor.remove(SharePrefConstant.PREFS_KEY_FROM_NOTIFICATIONS_ID);
                editor.remove(SharePrefConstant.PREFS_KEY_FROM_NOTIFICATIONS_ALARM_TYPE_DETAIL);
                editor.commit();
                BusinessUtil.removedMsgId(editor);
            }
        });
    }


    public static boolean hasUserGuide() {
        if (!NineGameClientApplication.getInstance().getResources().getBoolean(R.bool.has_user_guide)) {
            return false;
        }
        String curVersionName = PackageUtil.getVersionName(NineGameClientApplication.getInstance());
        String lastLaunchVerionName = EnvironmentState.getInstance().getSharedPrefenences(SharePrefConstant.SPLASH_SHARED_PREFERENCE_NAME).getString(SharePrefConstant.PREFS_LAST_LAUNCH_VERSION_NAME, null);
        if (lastLaunchVerionName == null) {
            return true;
        } else if (curVersionName!=null && curVersionName.compareTo(lastLaunchVerionName) != 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取NotificationManager
     * @return
     */
    public static NotificationManager getNotificationManager() {
        return (NotificationManager) NineGameClientApplication.getInstance().getSystemService(NineGameClientApplication.NOTIFICATION_SERVICE);

    }

    /**
     * 截取字符串
     * ***/
    /*public static Object[] splitString(String text, String splitExp){
        try {
            return text.split(splitExp);
        }catch (Exception ex){
            return null;
        }
    }*/
}
