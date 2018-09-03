package fangzuzu.com.ding.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by lingyuan on 2018/8/31.
 */

public class delectFile {
    public static File getPathFile(String path){
        String apkName = path.substring(path.lastIndexOf("/"));
        File outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), apkName);
        return outputFile;
    }

    public static void rmoveFile(String path){
        File file = getPathFile(path);
        file.delete();
    }


    //flie：要删除的文件夹的所在位置
    public static void RecursionDeleteFile(File file) {
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
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }
    }
