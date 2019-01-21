package com.smart.cloud.fire.utils;

/**
 * Created by Administrator on 2016/7/29.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.p2p.core.update.UpdateManager;
import com.smart.cloud.fire.global.AppConfig;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.NpcCommon;
import com.smart.cloud.fire.global.TemperatureTime;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class Utils {

    public static String getDeviceTypeNeme(int devicetype){
        String name="";
        switch (devicetype){
            case 1:
                name="烟感";
                break;
            case 2:
                name="燃气";
                break;
            case 5:
                name="电气";
                break;
            case 7:
                name="声光报警器";
                break;
            case 8:
                name="手动报警器";
                break;
            case 9:
                name="三江主机";
                break;
            case 10:
                name="水压传感器";
                break;
            case 11:
                name="红外传感器";
                break;
            case 12:
                name="门磁传感器";
                break;
            case 13:
                name="环境探测器";
                break;
            case 14:
                name="GPS";
                break;
            case 15:
                name="水浸传感器";
                break;
            case 16:
                name="NB燃气传感器";
                break;
            case 17:
                name="电梯";
                break;
            case 18:
                name="喷淋设备";
                break;
            default:
                name="其他";
                break;
        }
        return name;
    }

    /**
     * 验证是否是手机号码
     * @param input 手机号
     * @return
     */
    public static boolean isPhoneNumber(String input){
        String regex="((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";
        //String regex = "(^(\\d{3,4}-)?\\d{7,8})$|(13[0-9]{9}) ";
        Pattern p = Pattern.compile(regex);
        return p.matches(regex, input);
    }

    public static boolean hasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches())
            flag = true;
        return flag;
    }

    // yyyy-MM-dd HH:mm:ss ->>>>>>> yyyy-MM-dd HH:mm
    public static String ConvertTimeByString(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
            sdf.applyPattern("yyyy-MM-dd HH:mm");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sdf.format(date);
    }

    public static String ConvertTimeByLong(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(time);
        return sdf.format(date);
    }


    public static Bitmap montageBitmap(Bitmap frame, Bitmap src, int x, int y) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap sizeFrame = Bitmap.createScaledBitmap(frame, w, h, true);

        Bitmap newBM = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(newBM);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.drawBitmap(sizeFrame, 0, 0, null);
        return newBM;
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public static void upDate(final Context context) {
        new Thread() {
            @Override
            public void run() {

                boolean isUpdate;
                boolean isOk = false;
                try {
                    Timestamp time = new Timestamp(System.currentTimeMillis());
                    String recent_checkTime = SharedPreferencesManager
                            .getInstance()
                            .getData(
                                    context,
                                    SharedPreferencesManager.SP_FILE_GWELL,
                                    SharedPreferencesManager.KEY_UPDATE_CHECKTIME);
                    if (recent_checkTime.equals("")) {
                        isOk = true;
                    } else {
                        Timestamp recentTime = Timestamp
                                .valueOf(recent_checkTime);
                        long now = time.getTime();
                        long last = recentTime.getTime();
                        if ((now - last) > (1000 * 60 * 60 * 24)) {
                            isOk = true;
                        }
                    }
                    if (!isOk) {
                        return;
                    }
                    isUpdate = UpdateManager.getInstance().checkUpdate(NpcCommon.mThreeNum);
                    if (isUpdate && isOk) {
                        SharedPreferencesManager.getInstance().putData(context,
                                SharedPreferencesManager.SP_FILE_GWELL,
                                SharedPreferencesManager.KEY_UPDATE_CHECKTIME,
                                time.toString());
                        Intent i = new Intent(ConstantValues.Action.ACTION_UPDATE);
                        String data = "";
                        if (Utils.isZh(MyApp.app)) {
                            data = UpdateManager.getInstance()
                                    .getUpdateDescription();
                        } else {
                            data = UpdateManager.getInstance()
                                    .getUpdateDescription_en();
                        }
                        i.putExtra("updateDescription", data);
                        context.sendBroadcast(i);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public static String intToIp(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xff) + "."
                + ((ip >> 16) & 0xff) + "." + ((ip >> 24) & 0xff);
    }

    public static HashMap getHash(String string) {
        try {
            HashMap map = new HashMap<String, String>();
            String[] item = string.split(",");
            for (int i = 0, len = item.length; i < len; i++) {
                String[] info = item[i].split(":");
                map.put("" + info[0], info[1]);
            }
            return map;
        } catch (Exception e) {
            return null;
        }

    }

    public static void sleepThread(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String[] getDeleteAlarmIdArray(String[] data, int position) {
        if (data.length == 1) {
            return new String[] { "0" };
        }

        String[] array = new String[data.length - 1];
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            if (position != i) {
                array[count] = data[i];
                count++;
            }
        }

        return array;
    }

    public static String convertDeviceTime(int iYear, int iMonth, int iDay,
                                           int iHour, int iMinute) {
        int year = (2000 + iYear);
        int month = iMonth;
        int day = iDay;
        int hour = iHour;
        int minute = iMinute;

        StringBuilder sb = new StringBuilder();
        sb.append(year + "-");

        if (month < 10) {
            sb.append("0" + month + "-");
        } else {
            sb.append(month + "-");
        }

        if (day < 10) {
            sb.append("0" + day + " ");
        } else {
            sb.append(day + " ");
        }

        if (hour < 10) {
            sb.append("0" + hour + ":");
        } else {
            sb.append(hour + ":");
        }

        if (minute < 10) {
            sb.append("0" + minute);
        } else {
            sb.append("" + minute);
        }
        return sb.toString();
    }

    public static String convertPlanTime(int hour_from, int minute_from,
                                         int hour_to, int minute_to) {

        StringBuilder sb = new StringBuilder();

        if (hour_from < 10) {
            sb.append("0" + hour_from + ":");
        } else {
            sb.append(hour_from + ":");
        }

        if (minute_from < 10) {
            sb.append("0" + minute_from + "-");
        } else {
            sb.append(minute_from + "-");
        }

        if (hour_to < 10) {
            sb.append("0" + hour_to + ":");
        } else {
            sb.append(hour_to + ":");
        }

        if (minute_to < 10) {
            sb.append("0" + minute_to);
        } else {
            sb.append("" + minute_to);
        }

        return sb.toString();
    }

    public static boolean isNumeric(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isNumOrEng(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        boolean hasSymble = str.matches("^[\\da-zA-Z]*$");
        return hasSymble;
    }

    public static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();

        }
    }

    public static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    public static int getColorByResouse(int resouse) {
        return MyApp.app.getResources().getColor(resouse);
    }


    public static String getStringByResouceID(int R) {
        return MyApp.app.getString(R);
    }

    public static boolean isWeakPassword(String passWord) {
        if (getPassWordStatus(passWord) < 2) {
            return true;
        }
        return false;
    }

    /**
     * 获取密码强弱标记
     *
     * @param password
     * @return
     */
    private static String WEAK = "^[1-9]\\d*$|^[A-Z]+$|^[a-z]+$|^(.)\\1+$";
    private static String MID = "^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S+$";
    private static String STRONG = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,100}$";

    public static int getPassWordStatus(String password) {
        if (password.length() == 0) {
            return 0;
        } else if (password.length() < 6 || isRuo(password)) {
            return 1;
        }
        if (password.matches(MID)) {
            if (password.matches(STRONG)) {
                return 3;
            }
            return 2;
        }
        return -1;
    }

    /**
     * 是否弱密码
     *
     * @param password
     * @return
     */
    private static boolean isRuo(String password) {
        if(password.matches(WEAK)){
            return true;
        }
        return false;
    }

    public static InetAddress getIntentAddress(Context mContext)
            throws IOException {
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
    public static String getIntentAddress(Context mContext,String deviceID)
            throws IOException {
        WifiManager wifiManager =(WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int address = wifiInfo.getIpAddress();
        String ip = intToIp(address);
        String ipAddress=ip.substring(0, ip.lastIndexOf(".")+1)+deviceID;
        Log.e("dxsipaddress", "ip="+ip+"--"+"ipAddress="+ipAddress);
        return ipAddress;
    }

    /**
     * 隐藏软键盘()
     */
    public static void hindKeyBoard(View btnKey) {
        InputMethodManager imm = (InputMethodManager) MyApp.app
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        imm.hideSoftInputFromWindow(btnKey.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }

    /**
     * byte[]以16进制字符串输出
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("[");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (i != src.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    /**
     * 检测邮箱格式
     *
     * @param email
     * @return
     */
    public static boolean isEmial(String email) {
        // String str=
        // "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        String str =
                // "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
                "^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})";
        Pattern pattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        // Matcher matcher = Pattern.compile(regEx).matcher(email);
        return matcher.matches();
    }

    /**
     * WiFi是否加密
     *
     * @param result
     * @return
     */
    public static boolean isWifiOpen(ScanResult result) {
        if (result.capabilities.toLowerCase().indexOf("wep") != -1
                || result.capabilities.toLowerCase().indexOf("wpa") != -1) {
            return false;
        }
        return true;
    }

    /**
     * 创建录像根目录 ~/videorecode
     */
    public static File createRecodeFile() {
        if (!isSD()) {
            return null;
        }
        File sd = Environment.getExternalStorageDirectory();
        String path = sd.getPath() + "/videorecode";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        if (file == null) {
            Log.e("Utils", "create Recoding file failed");
        }
        return file;
    }

    /**
     * 判断手机SD卡是否插入
     *
     * @return
     */
    public static boolean isSD() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置录像文件名（~/videorecoder/callId/callId_yyyy_MM_dd_HH_mm_ss.MP4）
     *
     * @param callId
     * @return
     */
    public static String getVideoRecodeName(String callId) {
        if (!isSD()) {
            return "noSD";
        }
        long time = System.currentTimeMillis();
        File sd = Environment.getExternalStorageDirectory();
        String path = sd.getPath() + "/videorecode/" + callId;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");// 制定日期的显示格式
        String name = callId + "_" + sdf.format(new Date(time));
        String filename = file.getPath() + "/" + name + ".MP4";
        return filename;
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt2（）配套使用
     */
    public static int[] intToBytes2(int value) {
        int[] src = new int[4];
        src[3] = value & 0xFF;
        src[2] = (value >> 8) & 0xFF;
        src[1] = (value >> 16) & 0xFF;
        src[0] = (value >> 24) & 0xFF;

        return src;
    }

    /**
     * 字符串资源格式化
     *
     * @param formatSRC
     * @param formats
     * @return
     */
    public static String getFormatString(int formatSRC, Object... formats) {
        return MyApp.app.getResources().getString(formatSRC, formats);
    }

    /**
     * DES加密
     *
     * @param datasource
     * @param password
     * @return
     */
    public static byte[] desCrypto(byte[] datasource, String password) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES解密
     *
     * @param src
     * @param password
     * @return
     * @throws Exception
     */
    public static String decrypt(byte[] src, String password) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return new String(cipher.doFinal(src));
    }

    public static byte[] gainWifiMode() {
        byte[] data = new byte[20];
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
        return data;
    }

    public static byte[] setDeviceApWifiPwd(String pwd) {
        byte[] data = new byte[272];
        for (int i = 0; i < data.length; i++) {
            data[0] = 0;
        }
        data[0] = 2;
        data[8] = 1;
        byte[] password = pwd.getBytes();
        for (int j = 0; j < password.length; j++) {
            data[j + 144] = password[j];
        }
        return data;

    }

    /**
     * 截图存更新到相册
     *
     * @param fileName
     * @return
     */
    public static boolean saveImgToGallery(String fileName) {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (!sdCardExist)
            return false;
        if (fileName == null || fileName.length() <= 0)
            return false;
        try {
            ContentValues values = new ContentValues();
            values.put("datetaken", new Date().toString());
            values.put("mime_type", "image/png");
            values.put("_data", fileName);
            ContentResolver cr = MyApp.app.getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(MyApp.app,
                new String[] { AppConfig.Relese.SCREENSHORT }, null, null);
        return true;

    }

    /**
     * callId查询的Id 当其为“”时查询所有截图,type 1为时间降序 2为时间升序
     */
    public static List<String> getScreenShotImagePath(final String callId,
                                                      int type) {
        File[] data = new File[0];

        List<String> tempPathList = new ArrayList<String>();
        ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();// 将需要的子文件信息存入到FileInfo里面
        File file = new File(AppConfig.Relese.SCREENSHORT);
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                // TODO Auto-generated method stub
                if (null == callId || "".equals(callId)) {
                    if (pathname.getName().endsWith(".jpg")) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    // final String
                    // ipflag=FList.getInstance().getIpFlag(callId);
                    if (pathname.getName().startsWith(callId)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        };
        data = file.listFiles(filter);
        if (null != data) {
            for (int i = 0, count = data.length; i < count; i++) {
                FileInfo info = new FileInfo();
                File f = data[i];
                info.path = f.getPath();
                info.LastModified = f.lastModified();
                fileList.add(info);
            }
            if (type == 1) {
                Collections.sort(fileList, new FileComparatorUp());// 通过重写Comparator的实现类
            } else {
                Collections.sort(fileList, new FileComparatorDown());// 通过重写Comparator的实现类
            }
            for (FileInfo info : fileList) {
                tempPathList.add(info.path);
            }

        }
        return tempPathList;
    }

    public static class FileInfo {
        long LastModified;
        String path;
    }

    public static class FileComparatorDown implements Comparator<FileInfo> {
        @Override
        public int compare(FileInfo file1, FileInfo file2) {
            if (file1.LastModified < file2.LastModified) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static class FileComparatorUp implements Comparator<FileInfo> {
        @Override
        public int compare(FileInfo file1, FileInfo file2) {
            if (file1.LastModified < file2.LastModified) {
                return 1;
            } else {
                return -1;
            }
        }
    }
    public static byte[] btop = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01, 0x00,
            0x08, 0x00, (byte) 0xff, 0x08 };// 上
    public static byte[] bbottom = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
            0x00, 0x10, 0x00, (byte) 0xff, 0x10 };// 下
    public static byte[] bleft = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
            0x00, 0x04, (byte) 0xff, 0x00, 0x04 };// 左
    public static byte[] bright = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
            0x00, 0x02, (byte) 0xff, 0x00, 0x02, };// 右
    public static byte[] zoom_small = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
            0x00, 0x20, 0x00, 0x00, 0x21 };// 变倍短
    public static byte[] zoom_big = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
            0x00, 0x40, 0x00, 0x00, 0x41 };// 变倍长
    public static byte[] focus_small = { (byte) 130, 1, 7, 0, (byte) 0xff,
            0x01, 0x00, (byte) 0x80, 0x00, 0x00, (byte) 0x81 };// 聚焦近
    public static byte[] focus_big = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
            0x01, 0x00, 0x00, 0x00, 0x02 };// 聚焦远
    public static byte[] aperture_smal = { (byte) 130, 1, 7, 0, (byte) 0xff,
            0x01, 0x02, 0x00, 0x00, 0x00, 0x03 };// 光圈小
    public static byte[] aperture_big = { (byte) 130, 1, 7, 0, (byte) 0xff,
            0x01, 0x04, 0x00, 0x00, 0x00, 0x05 };// 光圈大

    public static int getColorByResouce(int R) {
        return MyApp.app.getResources().getColor(R);
    }
    public static boolean checkPassword(String password){
        if(password.length()<10&& Utils.isNumeric(password)&&password.charAt(0) == '0'){
            return false;
        }else{
            return true;
        }
    }
    public static String getWifiName(String wifiname){
        if(wifiname!=null&&wifiname.length()>2){
            int first = wifiname.charAt(0);
            if (first == 34) {
                wifiname = wifiname.substring(1, wifiname.length() - 1);
            }
        }
        return wifiname;
    }
    /**
     * byte按位输出
     *
     * @param info
     * @param isOpposite 是否反向读取
     * @return
     */
    public static int[] getByteBinnery(byte info, boolean isOpposite) {
        int i;
        int j = 0;
        int[] gpiostatus = new int[8];
        if (isOpposite) {
            for (i = 0; i <= 7; ++i) {
                gpiostatus[j] = (byte) ((info >> i) & 0x01);
                j++;
            }
        } else {
            for (i = 7; i >= 0; --i) {
                gpiostatus[j] = (byte) ((info >> i) & 0x01);
                j++;
            }
        }
        return gpiostatus;
    }
    /**指定某位二进制为1
     * @param src
     * @param position
     * @return
     */
    public static byte ChangeBitTrue(byte src,int position){
        return (byte) (src|(1<<position));
    }
    /**指定某位二进制0，其他位保持不变
     * @param src
     * @param position
     * @return
     */
    public static byte ChangeByteFalse(byte src, int position){
        int t= Integer.MAX_VALUE^(1<<position);
        return (byte) (src&t);
    }
    /**
     * 鱼眼可用
     * @param value
     * @return
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) (value & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[3] = (byte) ((value >> 24) & 0xFF);

        return src;
    }
    public static String getStringForId(int StringId) {
        return MyApp.app.getResources().getString(StringId);

    }


    public static String getPhoneIpdress(){
        WifiManager wifiManager = (WifiManager) MyApp.app.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;

    }

    public static double stringToDouble(String doubleStr){
        try {
            double mDouble = Double.parseDouble(doubleStr);
            return mDouble;
        }catch (Exception e){
            return 0;
        }
    }

    /**
     * 判断手机网络状态..
     * @param activity
     * @return
     */
    public static boolean isNetworkAvailable(Activity activity)
    {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
        {
            T.showShort(activity,"手机网络不可用，请连接网络");
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        T.showShort(activity,"网络异常，请确认当前手机网络是否通畅");
        return false;
    }

    public static int getMax(List<TemperatureTime.ElectricBean> electricBeen){
        int max = (int) Float.parseFloat(electricBeen.get(0).getElectricValue());
        for(TemperatureTime.ElectricBean electricBean : electricBeen){
            int value = (int)Float.parseFloat(electricBean.getElectricValue());
            if (max < value) max = value;
        }
        return max;
    }

    //@@2018.01.03同上方法
    public static float getMaxFloat(List<TemperatureTime.ElectricBean> electricBeen){
        float max =  Float.parseFloat(electricBeen.get(0).getElectricValue());
        for(TemperatureTime.ElectricBean electricBean : electricBeen){
            float value =Float.parseFloat(electricBean.getElectricValue());
            if (max < value) max = value;
        }
        return max;
    }

    public static String ByteArrayToHexString(byte [] inarray) { //converts byte arrays to string
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
}

