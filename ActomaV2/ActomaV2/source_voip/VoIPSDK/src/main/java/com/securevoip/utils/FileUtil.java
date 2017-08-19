package com.securevoip.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.xdja.voipsdk.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    private String sdCardPath;

    public FileUtil() {
        sdCardPath = Environment.getExternalStorageDirectory() + "/";
    }


    /**
     * 拷贝文件
     * @param context
     * @param resource
     * @param destination
     * @return
     */
    public static boolean copy(Context context, String resource,String destination) {
        File file = new File(destination +"/"+ resource);
        if (file.exists() && file.length() == 0) {
            file.delete();
        }
        if (!file.exists()) {
            InputStream in = null;
            FileOutputStream out = null;
            try {
                in = context.getResources().getAssets().open(resource);
                out = context.openFileOutput(resource, Context.MODE_PRIVATE);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = in.read(buffer)) > 0) {
                    out.write(buffer, 0, count);
                }
            } catch (Exception ioe) {
                ioe.printStackTrace();
                return false;
            } finally {
                try {
                    in.close();
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return true;
    }


    /**
     * 判断SD卡是否存在
     */
    public static boolean sdCardIsExit() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) || Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_SHARED);
    }

    /**
     * 获取SD卡路径
     *
     * @return /sdcard/
     */
    @SuppressLint("AndroidLintSdCardPath")
    public static String getSDCardPath() {
        if (sdCardIsExit()) {
            return Environment.getExternalStorageDirectory().toString() + "/";
        }
        //return "/sdcard/";
        return R.string.sd_card_path+"";
    }

    /**
     * 删除文件夹
     * @param directory
     * @throws IOException
     */
    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) return;
        if (!isSymlink(directory)) cleanDirectory(directory);
        if (!directory.delete()) {
            String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        File fileInCanonicalDir = null;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }

        if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
            return false;
        } else {
            return true;
        }
    }

    public static void cleanDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles();
        if (files == null) { // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (File file : files) {
            try {
                forceDelete(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    /**
     * 创建文件
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createSdFile(String fileName) throws IOException {
        File file = new File(fileName);
        if(!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 创建文件夹
     * @param dirName
     * @return
     */
    public static File createSdDir(String dirName) {
        File dir = new File(dirName);
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 文件是否存在
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 从输入流读取中写文件到sd
     * @param path
     * @param fileName
     * @param input
     * @return
     */
    public static File write2SDFromInputStream(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            createSdDir(path);
            file = createSdFile(path + fileName);
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            int count = 0;
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer,0,count);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(output!=null){
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    /**
     * 获取文件类型
     */
    public static String geFileMIMEType(File file){
        String type = "";
        String name = file.getName();
        // 文件扩展名
        String end = name.substring(name.lastIndexOf(".") + 1, name.length());
        if (end.equalsIgnoreCase("m4a") || end.equalsIgnoreCase("mp3") || end.equalsIgnoreCase("wav")) {
            type = "audio";
        } else if (end.equalsIgnoreCase("mp4") || end.equalsIgnoreCase("3gp")) {
            type = "video";
        } else if (end.equalsIgnoreCase("jpg") || end.equalsIgnoreCase("png") || end.equalsIgnoreCase("jpeg") || end.equalsIgnoreCase("bmp") || end.equalsIgnoreCase("gif")) {
            type = "image";
        } else {
            // 如果无法直接打开，跳出列表由用户选择
            type = "*";
        }
        type += "/*";
        return type;
    }

}

