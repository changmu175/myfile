package com.xdja.imp.domain.model;

import android.support.annotation.NonNull;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/14 9:35
 * 修改人：xdjaxa
 * 修改时间：2016/12/14 9:35
 * 修改备注：
 */
public class HistoryFileCategory implements Comparable<HistoryFileCategory>{
    private String categoryTitle;

    private String categoryId;

    private long time;

    public HistoryFileCategory(){
    }

    public HistoryFileCategory(String categoryId,String categoryTitle){
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryTitle(){
        return categoryTitle;
    }

    public String getCategoryId(){
        return categoryId;
    }

    public void setCategoryTitle(String categoryTitle){
        this.categoryTitle = categoryTitle;
    }

    public void setCategoryId(String categoryId){
        this.categoryId = categoryId;
    }

    public void setTime(long time){
        this.time = time;
    }

    public long getTime(){
        return time;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((categoryId == null) ? 0 : categoryId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HistoryFileCategory other = (HistoryFileCategory) obj;
        if (categoryId == null) {
            if (other.categoryId != null)
                return false;
        } else if (!categoryId.equals(other.categoryId))
            return false;
        return true;
    }

    @Override
    public int compareTo(@NonNull HistoryFileCategory another) {
        return Integer.parseInt(categoryId) - Integer.parseInt(another.categoryId) >= 0 ? -1 : 1;
    }
}
