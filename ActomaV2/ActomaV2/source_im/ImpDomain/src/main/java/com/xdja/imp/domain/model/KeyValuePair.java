package com.xdja.imp.domain.model;

/**
 * <p>Summary:自定义的键值对组合类</p>
 * <p>Description:</p>
 * <p>Package:com.imdo.model</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/2</p>
 * <p>Time:14:35</p>
 */
public class KeyValuePair<K,V> {
    private K key;
    private V value;

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
