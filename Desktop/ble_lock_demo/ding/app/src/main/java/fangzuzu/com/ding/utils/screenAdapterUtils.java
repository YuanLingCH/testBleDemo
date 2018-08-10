package fangzuzu.com.ding.utils;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by lingyuan on 2018/7/25.
 */

public class screenAdapterUtils {
    /**
     *华为start
     */
//判断是否是华为刘海屏
    public static boolean hasNotchInScreen(Context context)
    {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);

        } catch (ClassNotFoundException e)
        { Log.e("test", "hasNotchInScreen ClassNotFoundException"); }
        catch (NoSuchMethodException e)
        { Log.e("test", "hasNotchInScreen NoSuchMethodException"); }
        catch (Exception e)
        { Log.e("test", "hasNotchInScreen Exception"); }
        finally
        { return ret; }
    }

    //获取华为刘海的高宽
    public static int[] getNotchSize(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize"); ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("test", "getNotchSize ClassNotFoundException"); }
        catch (NoSuchMethodException e)
        { Log.e("test", "getNotchSize NoSuchMethodException"); }
        catch (Exception e) { Log.e("test", "getNotchSize Exception"); }
        finally { return ret; }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean hasNotchInOppo(Context context){
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }


    public static final int NOTCH_IN_SCREEN_VOIO=0x00000020;//是否有凹槽
    public static final int ROUNDED_IN_SCREEN_VOIO=0x00000008;//是否有圆角
    public static boolean hasNotchInScreenAtVoio(Context context){
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class FtFeature = cl.loadClass("com.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport",int.class);
            ret = (boolean) get.invoke(FtFeature,NOTCH_IN_SCREEN_VOIO);

        } catch (ClassNotFoundException e)
        { Log.e("test", "hasNotchInScreen ClassNotFoundException"); }
        catch (NoSuchMethodException e)
        { Log.e("test", "hasNotchInScreen NoSuchMethodException"); }
        catch (Exception e)
        { Log.e("test", "hasNotchInScreen Exception"); }
        finally
        { return ret; }
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
}
