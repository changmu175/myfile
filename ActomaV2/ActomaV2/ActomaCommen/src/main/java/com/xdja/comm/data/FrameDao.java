package com.xdja.comm.data;

/**
 * <p>Summary:安通+框架数据库操作类</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.comm.data</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/7/13</p>
 * <p>Time:15:11</p>
 */
public class FrameDao {

    protected static FrameDbHelper helper = null;
    //start:add by wangchao
    public FrameDao open() {
        if (helper == null) {
            synchronized (FrameDao.class) {
                if (helper == null) {
                    helper = new FrameDbHelper();
                }
            }
        }
        return this;
    }

    public synchronized void close() {
        //框架数据库，应用退出再释放
//        if (helper != null) {
//            helper.close();
//            helper = null;
//        }
    }
    //end:add by wangchao
}
