package com.xdja.presenter_mainframe.util;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xdja.data_mainframe.entities.cache.UserCache;
import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.presenter_mainframe.presenter.activity.setting.OpenGesturePresenter;
import com.xdja.presenter_mainframe.widget.LockPatternView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

/**
 * 图案解锁加密、解密工具类
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class LockPatternUtils {
    private static final String TAG = "LockPatternUtils";
    private static final String LOCK_PATTERN_FILE = "gesture.key";
    public static final int CHECK_GESTURE = 2;
    /**
     * 最少选择的数
     */
    public static final int MIN_LOCK_PATTERN_SIZE = 4;
    /**
     * 允许输入密码错误的次数
     */
    public static final int FAILED_ATTEMPTS_BEFORE_TIMEOUT = 5;

    public static final int MIN_PATTERN_REGISTER_FAIL = MIN_LOCK_PATTERN_SIZE;


    private static File newAccountCacheFile;

    private static final AtomicBoolean sHaveNonZeroPatternFile = new AtomicBoolean(false);

    private static FileObserver sPasswordObserver;

    private static class LockPatternFileObserver extends FileObserver {
        public LockPatternFileObserver(String path, int mask) {
            super(path, mask);
        }

        @Override
        public void onEvent(int event, String path) {
            if (LOCK_PATTERN_FILE.equals(path)) {
                sHaveNonZeroPatternFile.set(newAccountCacheFile.length() > 0);
            }
        }
    }

    @Inject
    public LockPatternUtils(@NonNull
                            @ContextSpe(DiConfig.CONTEXT_SCOPE_APP) Context context, UserCache userCache) {
        //String newAccountCache = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/XdjaIm/" + userCache.getAccount() + "/";
        //String userAccountCacheDir = context.getCacheDir().getAbsolutePath() + "/userAccount/" + userCache.getAccount() + "/";
        //newAccountCacheFile = new File(newAccountCache, LOCK_PATTERN_FILE);

        String account = userCache.getAccount();
        newAccountCacheFile = createAccountFile(context, account);
        if (null == newAccountCacheFile) return;
        sHaveNonZeroPatternFile.set(newAccountCacheFile.length() > 0);
        int fileObserverMask = FileObserver.CLOSE_WRITE
                | FileObserver.DELETE | FileObserver.MOVED_TO
                | FileObserver.CREATE;
        sPasswordObserver = new LockPatternFileObserver(
                newAccountCacheFile.getParentFile().getAbsolutePath(), fileObserverMask);
        sPasswordObserver.startWatching();

        if (imFileExist(account)) {
            copy(getOldAccountFile(account), newAccountCacheFile);
        }
    }

    //[S] add by licong for safeLock
    //从服务器上获取保存密码
    /*public LockPatternUtils(@NonNull
                            @ContextSpe(DiConfig.CONTEXT_SCOPE_APP)Context context,String account) {
        String dataSystemDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/XdjaIm/" + account + "/";
        newAccountCacheFile = new File(dataSystemDirectory
                , LOCK_PATTERN_FILE);
        sHaveNonZeroPatternFile.set(newAccountCacheFile.length() > 0);
        int fileObserverMask = FileObserver.CLOSE_WRITE
                | FileObserver.DELETE | FileObserver.MOVED_TO
                | FileObserver.CREATE;
        sPasswordObserver = new LockPatternFileObserver(
                dataSystemDirectory, fileObserverMask);
        sPasswordObserver.startWatching();
    }*/
    //[E] add by licong for safeLock


    /**
     * 解密,用于保存状态
     *
     * @param s
     * @return List<LockPatternView.Cell>
     */
    public static List<LockPatternView.Cell> stringToPattern(String s) {
        List<LockPatternView.Cell> result = new ArrayList<LockPatternView.Cell>();
        final byte[] bytes = s.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            result.add(LockPatternView.Cell.of(b / 3, b % 3));
        }
        return result;
    }

    /**
     * 加密
     *
     * @param pattern
     * @return String
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static String patternToString(List<LockPatternView.Cell> pattern) {
        if (pattern == null) {
            return "";
        }

        final int patternSize = pattern.size();

        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            LockPatternView.Cell cell = pattern.get(i);
            res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
        }
        return new String(res);
    }


    /**
     * 判断该账号对应的文件是否存在
     *
     * @throws IOException
     */
    public static void createGestureFile() throws IOException {
        if (newAccountCacheFile != null && !newAccountCacheFile.exists()) {
            newAccountCacheFile.getParentFile().mkdirs();
            newAccountCacheFile.createNewFile();
        }
    }

    /**
     * 判断当前目录下文件时候存在
     */
    public boolean isExistsFile() {
        return newAccountCacheFile != null && newAccountCacheFile.exists();
    }

    /**
     * @param account
     * @return
     */
    public boolean imFileExist(String account) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/XdjaIm/" + account + "/gesture.key");
        if (file != null && file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存安全锁密码
     *
     * @param pattern
     */
    public static void saveLockPattern(List<LockPatternView.Cell> pattern) {

        final byte[] hash = LockPatternUtils.patternToHash(pattern);
        try {
            createGestureFile();
            RandomAccessFile raf = new RandomAccessFile(newAccountCacheFile,
                    "rwd");
            if (pattern == null) {
                raf.setLength(0);
            } else {
                raf.write(hash, 0, hash.length);
            }
            raf.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    @SuppressWarnings({"ReturnOfNull", "NumericCastThatLosesPrecision"})
    private static byte[] patternToHash(List<LockPatternView.Cell> pattern) {
        if (pattern == null) {
            return null;
        }

        final int patternSize = pattern.size();
        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            LockPatternView.Cell cell = pattern.get(i);
            res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(res);
            return hash;
        } catch (NoSuchAlgorithmException nsa) {
            nsa.printStackTrace();
            return res;
        }
    }

    /**
     * 检查密码是否符合
     *
     * @param pattern
     * @return
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public boolean checkPattern(List<LockPatternView.Cell> pattern) {
        try {
            RandomAccessFile raf = new RandomAccessFile(newAccountCacheFile,
                    "r");
            final byte[] stored = new byte[(int) raf.length()];
            int got = raf.read(stored, 0, stored.length);
            raf.close();
            if (got <= 0) {
                return true;
            }
            return Arrays.equals(stored,
                    LockPatternUtils.patternToHash(pattern));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return true;
        }
    }

    public static void startConfirmPattern(Context activity) {
        Intent intent = new Intent(activity, OpenGesturePresenter.class);
        intent.setFlags(CHECK_GESTURE);
        activity.startActivity(intent);
    }

    private File getOldAccountFile(String account) {
        if (TextUtils.isEmpty(account)) {
            return null;
        }
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                "XdjaIm" + File.separator + account + File.separator + LOCK_PATTERN_FILE;
        return new File(filePath);
    }

    private File createAccountFile(Context context, String account) {
        try {
            File cacheDir = new File(context.getFilesDir(), "userAccount" + File.separator + account);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            File accountFile = new File(cacheDir, LOCK_PATTERN_FILE);
            if (!accountFile.exists()) {
                accountFile.createNewFile();
            }
            return accountFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void copy(File res, File dest) {
        moveFile(res, dest);
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * @param res
     * @param dest
     */
    private boolean moveFile(File res, File dest) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            createGestureFile();
            fis = new FileInputStream(res);
            fos = new FileOutputStream(dest);
            int readLen = 0;
            byte[] buffer = new byte[1024];
            while ((readLen = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, readLen);
            }
            fos.flush();
            deleteFile(res);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }

                if (fis != null) {
                    fis.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
