package com.xdja.imp.data.persistent;

import android.app.Application;

import com.xdja.imp.ImpDataApplicationTestCase;
import com.xdja.imp.data.entity.SessionParam;
import com.xdja.xutils.DbUtils;
import com.xdja.xutils.db.sqlite.WhereBuilder;
import com.xdja.xutils.exception.DbException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.persistent</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/12/25</p>
 * <p>Time:17:41</p>
 */
public class DbUtilTest extends ImpDataApplicationTestCase{

    DbUtil dbUtil;

    DbUtils xDbUtil;

    final String sesseionFlag = "1234567890";

    @Before
    public void setUp() throws Exception {
        Application application = RuntimeEnvironment.application;
        dbUtil = new DbUtil(application);
        xDbUtil = dbUtil.get();
    }

    @Test
    public void testSaveSession2Db(){
        SessionParam sessionParam = new SessionParam();
        sessionParam.setDraft("this is a draft");
        sessionParam.setIsNoDisturb(SessionParam.ISDISTURB_TRUE);
        sessionParam.setIsTop(SessionParam.ISTOP_FALE);
        sessionParam.setFlag(sesseionFlag);

        try {
            xDbUtil.saveOrUpdate(sessionParam);
            SessionParam param
                    = xDbUtil.findFirst(SessionParam.class, WhereBuilder.b("flag", "=", sesseionFlag));
            Assert.assertNotNull(param);
            Assert.assertEquals(sessionParam.getDraft(),param.getDraft());
            Assert.assertEquals(sessionParam.getFlag(),param.getFlag());
            Assert.assertEquals(sessionParam.getIsNoDisturb(),param.getIsNoDisturb());
            Assert.assertEquals(sessionParam.getIsTop(),param.getIsTop());
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateSessionInDb(){
        testSaveSession2Db();

        SessionParam sessionParam = new SessionParam();
        sessionParam.setDraft("this is a draft1");
        sessionParam.setIsNoDisturb(SessionParam.ISDISTURB_FALE);
        sessionParam.setIsTop(SessionParam.ISTOP_TRUE);
        sessionParam.setFlag(sesseionFlag);

        try {
            xDbUtil.saveOrUpdate(sessionParam);
            SessionParam param
                    = xDbUtil.findFirst(SessionParam.class, WhereBuilder.b("flag", "=", sesseionFlag));
            Assert.assertNotNull(param);
            Assert.assertEquals(sessionParam.getDraft(),param.getDraft());
            Assert.assertEquals(sessionParam.getFlag(),param.getFlag());
            Assert.assertEquals(sessionParam.getIsNoDisturb(),param.getIsNoDisturb());
            Assert.assertEquals(sessionParam.getIsTop(),param.getIsTop());
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {

    }
}