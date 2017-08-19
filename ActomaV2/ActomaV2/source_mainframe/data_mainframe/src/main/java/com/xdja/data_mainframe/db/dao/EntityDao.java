package com.xdja.data_mainframe.db.dao;

import java.util.List;

import rx.Observable;

/**
 * Created by ldy on 16/2/24.
 *
 * @param <T> 关联的bean类
 * @param <K> ID(一般查询使用的索引)
 */
public interface EntityDao<T, K> {

    /**
     * 索引相同则更新,否则创建
     * @param entity 一个或多个bean对象
     */
    Observable<Void> createOrUpdate(final T... entity);

    /**
     * 移除记录（指定ID集）
     *
     * @param ids 可以有多个
     */
    Observable<Void> remove(final K... ids);

    /**
     * 按ID查询对象
     *
     * @param ids   id
     * @return  查询结果
     */
    Observable<List<T>> find(final K... ids);

    /**
     * 获取所有数据
     */
    Observable<List<T>> findAll();

    /**
     * 清除所有数据
     */
    Observable<Void> clear();

}
