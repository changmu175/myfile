package com.xdja.imp.sample.activity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.sample.activity</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/15</p>
 * <p>Time:14:10</p>
 */
public class ViewMainTest {

    private ViewMain viewMain;

    @Mock
    MainCommand mainCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        viewMain.setCommand(mainCommand);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetRoam() throws Exception {
//        onView(withId(R.id.btn_getRoam)).perform(click()).check(cli)
//        verify(mainCommand).getRoamSetting();
    }

    @Test
    public void testSetRoam() throws Exception {

    }

    @Test
    public void testInitList() throws Exception {

    }
}