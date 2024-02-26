package com.xinkon.wancompose.utils;

import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isFileExist(String filePath) {
        boolean result = false;

        try {
            File file = new File(filePath);
            result = file.exists();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return result;
    }

    public static boolean upZipFile(String zipPath, String folderPath) throws IOException {
        try {
            int BUFF_SIZE = 1048576;
            File mZipFile = new File(zipPath);
            File mDesDir = new File(folderPath);
            if (!mDesDir.exists()) {
                mDesDir.mkdirs();
            }

            ZipFile mZf = new ZipFile(mZipFile);
            Enumeration mEntries = mZf.entries();

            while(mEntries.hasMoreElements()) {
                ZipEntry mEntry = (ZipEntry)mEntries.nextElement();
                InputStream mIn = mZf.getInputStream(mEntry);
                String mStr = folderPath + File.separator + mEntry.getName();
                mStr = new String(mStr.getBytes("8859_1"), "GB2312");
                File mDesFile = new File(mStr);
                if (!mDesFile.exists()) {
                    File mFileParentDir = mDesFile.getParentFile();
                    if (!mFileParentDir.exists()) {
                        mFileParentDir.mkdirs();
                    }

                    if (!mEntry.isDirectory()) {
                        mDesFile.createNewFile();
                    } else {
                        mDesFile.mkdirs();
                    }
                }

                if (!mEntry.isDirectory()) {
                    OutputStream mOut = new FileOutputStream(mDesFile);
                    byte[] buffer = new byte[BUFF_SIZE];

                    int realLength;
                    while((realLength = mIn.read(buffer)) > 0) {
                        mOut.write(buffer, 0, realLength);
                    }

                    mIn.close();
                    mOut.close();
                }
            }
        } catch (Exception var14) {
            var14.printStackTrace();
        }

        return false;
    }
}
