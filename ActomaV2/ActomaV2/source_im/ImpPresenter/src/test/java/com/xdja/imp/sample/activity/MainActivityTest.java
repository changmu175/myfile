//package com.xdja.imp.sample.activity;
//
//import android.os.Bundle;
//
//import com.xdja.imp.ImpApplicationTestCase;
//import com.xdja.imp.domain.interactor.def.GetRoamSetting;
//import com.xdja.imp.domain.interactor.def.SaveRoamSetting;
//import com.xdja.imp.domain.interactor.mx.GetRoamSettingUseCase;
//import com.xdja.imp.domain.interactor.mx.SetRoamSettingUseCase;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import java.lang.reflect.Field;
//
//import dagger.Lazy;
//import rx.Subscriber;
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.anyInt;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
///**
// * <p>Summary:MainActivity测试</p>
// * <p>Description:</p>
// * <p>Package:com.xdja.imp.sample.activity</p>
// * <p>Author:fanjiandong</p>
// * <p>Date:2015/11/15</p>
// * <p>Time:14:16</p>
// */
//public class MainActivityTest extends ImpApplicationTestCase {
//
//    MainActivity mainActivity;
//
//    @Mock
//    private MainVu mainVu;
//
//    @Mock
//    private Lazy<SaveRoamSetting> saveRoamSettingLazy;
//
//    @Mock
//    private Lazy<GetRoamSetting> getRoamSettingLazy;
//
//    @Mock
//    private GetRoamSetting getRoamSetting;
//    @Mock
//    private SaveRoamSetting saveRoamSetting;
//    @Mock
//    private GetRoamSettingUseCase getRoamSettingUseCase;
//    @Mock
//    private SetRoamSettingUseCase setRoamSettingUseCase;
//
//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//        mainActivity = Mockito.spy(new MainActivity());
//
//        when(mainActivity.getVu()).thenReturn(mainVu);
//    }
//
//    @Test
//    public void testOnBindView() {
//        mainActivity.onBindView(new Bundle());
////        verify(mainVu).initList(any(MainAdapter.class));
//    }
//
//    @Test
//    public void testGetRoamSetting() {
//        try {
//            Field getRoamSettingLazyF = mainActivity.getClass().getDeclaredField("getRoamSettingLazy");
//            getRoamSettingLazyF.setAccessible(true);
//            getRoamSettingLazyF.set(mainActivity, getRoamSettingLazy);
//            when(this.getRoamSettingLazy.get()).thenReturn(getRoamSetting);
//            when(getRoamSetting.get()).thenReturn(getRoamSettingUseCase);
//
//            mainActivity.getRoamSetting();
//            verify(getRoamSettingLazy).get();
//            verify(getRoamSetting).get();
//            verify(getRoamSettingUseCase).execute(any(Subscriber.class));
//        } catch (Exception ex) {
//
//        }
//
//    }
//
//    @Test
//    public void testSetRoamSetting() {
//        try {
//            Field saveRoamSettingLazyF = mainActivity.getClass().getDeclaredField("saveRoamSettingLazy");
//            saveRoamSettingLazyF.setAccessible(true);
//            saveRoamSettingLazyF.set(mainActivity, saveRoamSettingLazy);
//            when(this.saveRoamSettingLazy.get()).thenReturn(saveRoamSetting);
//            when(saveRoamSetting.get()).thenReturn(setRoamSettingUseCase);
//            when(setRoamSettingUseCase.save(anyInt(), anyInt())).thenReturn(setRoamSettingUseCase);
//
//            mainActivity.setRoamSetting();
//            verify(saveRoamSettingLazy).get();
//            verify(saveRoamSetting).get();
//            verify(setRoamSettingUseCase).execute(any(Subscriber.class));
//        } catch (Exception ex) {
//
//        }
//    }
//
//    @After
//    public void tearDown() throws Exception {
//
//    }
//}