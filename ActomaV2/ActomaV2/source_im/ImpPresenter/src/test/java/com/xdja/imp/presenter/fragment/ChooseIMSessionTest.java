package com.xdja.imp.presenter.fragment;

import com.xdja.contact.bean.dto.LocalCacheDto;
import com.xdja.imp.presenter.activity.ChooseIMSessionActivity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：yuchangmu
 * 创建时间：2016/12/8.
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ChooseIMSessionTest {
    ChooseIMSessionActivity fragment;

    @Before
    public void setUp() {
        fragment = new ChooseIMSessionActivity();
    }

    @Test
    public void testClassifyGroup() throws Exception {
        List<LocalCacheDto> dtoList = setDataSource();;
        List<LocalCacheDto> result = (List<LocalCacheDto>)
                getPrivateMethod("classifyGroup", List.class).invoke(fragment, dtoList);
        Assert.assertEquals(LocalCacheDto.GROUP_ALPHA, result.get(0).getViewType());
        Assert.assertEquals(dtoList.get(0).getViewType(), result.get(1).getViewType());
        Assert.assertEquals(dtoList.get(3).getViewType(), result.get(2).getViewType());
        Assert.assertEquals(dtoList.get(5).getViewType(), result.get(3).getViewType());
    }

    @Test
    public void testClassifyContacts() throws Exception {
        List<LocalCacheDto> dtoList = setDataSource();
        List<LocalCacheDto> result = (List<LocalCacheDto>)
                getPrivateMethod("classifyContacts", List.class).invoke(fragment, dtoList);
        Assert.assertEquals(LocalCacheDto.FRIEND_ALPHA, result.get(0).getViewType());
        Assert.assertEquals(dtoList.get(1).getViewType(), result.get(1).getViewType());
        Assert.assertEquals(dtoList.get(2).getViewType(), result.get(2).getViewType());
        Assert.assertEquals(dtoList.get(4).getViewType(), result.get(3).getViewType());
    }

    @Test
    public void testBuildContactSource() throws Exception {
        List<LocalCacheDto> dtoList = setDataSource();
        List<LocalCacheDto> result = (List<LocalCacheDto>)
                getPrivateMethod("buildContactSource", List.class).invoke(fragment, dtoList);
        List<LocalCacheDto> expect = setExpectData();
        Assert.assertEquals(LocalCacheDto.FRIEND_ALPHA, result.get(0).getViewType());
        Assert.assertEquals(expect.get(0).getViewType(), result.get(1).getViewType());
        Assert.assertEquals(expect.get(1).getViewType(), result.get(2).getViewType());
        Assert.assertEquals(expect.get(2).getViewType(), result.get(3).getViewType());
        Assert.assertEquals(LocalCacheDto.GROUP_ALPHA, result.get(4).getViewType());
        Assert.assertEquals(expect.get(3).getViewType(), result.get(5).getViewType());
        Assert.assertEquals(expect.get(4).getViewType(), result.get(6).getViewType());
        Assert.assertEquals(expect.get(5).getViewType(), result.get(7).getViewType());
    }

    /**
     * 设置数据
     * @return
     */
    private List<LocalCacheDto> setDataSource() {
        LocalCacheDto dto1 = new LocalCacheDto();
        LocalCacheDto dto2 = new LocalCacheDto();
        LocalCacheDto dto3 = new LocalCacheDto();
        LocalCacheDto dto4 = new LocalCacheDto();
        LocalCacheDto dto5 = new LocalCacheDto();
        LocalCacheDto dto6 = new LocalCacheDto();
        dto1.setViewType(LocalCacheDto.GROUP_ITEM);
        dto2.setViewType(LocalCacheDto.FRIEND_ITEM);
        dto3.setViewType(LocalCacheDto.FRIEND_ITEM);
        dto4.setViewType(LocalCacheDto.GROUP_ITEM);
        dto5.setViewType(LocalCacheDto.FRIEND_ITEM);
        dto6.setViewType(LocalCacheDto.GROUP_ITEM);
        List<LocalCacheDto> dtoList = new ArrayList<>();
        dtoList.add(dto1);
        dtoList.add(dto2);
        dtoList.add(dto3);
        dtoList.add(dto4);
        dtoList.add(dto5);
        dtoList.add(dto6);
        return dtoList;
    }

    /**
     * 设置期望数据
     * @return
     */
    private List<LocalCacheDto> setExpectData() {
        LocalCacheDto dto1 = new LocalCacheDto();
        LocalCacheDto dto2 = new LocalCacheDto();
        LocalCacheDto dto3 = new LocalCacheDto();
        LocalCacheDto dto4 = new LocalCacheDto();
        LocalCacheDto dto5 = new LocalCacheDto();
        LocalCacheDto dto6 = new LocalCacheDto();
        dto1.setViewType(LocalCacheDto.FRIEND_ITEM);
        dto2.setViewType(LocalCacheDto.FRIEND_ITEM);
        dto3.setViewType(LocalCacheDto.FRIEND_ITEM);
        dto4.setViewType(LocalCacheDto.GROUP_ITEM);
        dto5.setViewType(LocalCacheDto.GROUP_ITEM);
        dto6.setViewType(LocalCacheDto.GROUP_ITEM);
        List<LocalCacheDto> dtoList = new ArrayList<>();
        dtoList.add(dto1);
        dtoList.add(dto2);
        dtoList.add(dto3);
        dtoList.add(dto4);
        dtoList.add(dto5);
        dtoList.add(dto6);
        return dtoList;
    }

    /**
     * 通过映射得到私有方法
     * @param methodName
     * @param params
     * @return
     * @throws NoSuchMethodException
     */
    private Method getPrivateMethod(String methodName, Class<?>...params) throws NoSuchMethodException {
        Class cls = fragment.getClass();
        Method method = cls.getDeclaredMethod(methodName, params);
        method.setAccessible(true);
        return method;
    }
    @Test
    public void testCompleteShared() throws Exception {
//        Method method = getPrivateMethod("completeShared", String.class, boolean.class, List.class);
//        ChooseIMSessionActivity fragment = Mockito.mock(ChooseIMSessionActivity.class);
//        Class cls = fragment.getClass();
    }
}
