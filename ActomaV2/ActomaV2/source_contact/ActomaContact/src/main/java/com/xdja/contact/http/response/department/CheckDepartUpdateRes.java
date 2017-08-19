package com.xdja.contact.http.response.department;

/**
 * 通讯录更新检测Bean
 * Created by hkb.
 * 2015/7/10/0010.
 */
public class CheckDepartUpdateRes {

    private String deptSubUpdateId;

    private String personSubUpdateId;

    private String checkStatus;

    private String totalSize;

    public String getPersonSubUpdateId() {
        return personSubUpdateId;
    }

    public void setPersonSubUpdateId(String personSubUpdateId) {
        this.personSubUpdateId = personSubUpdateId;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public String getDeptSubUpdateId() {
        return deptSubUpdateId;
    }

    public void setDeptSubUpdateId(String deptSubUpdateId) {
        this.deptSubUpdateId = deptSubUpdateId;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }
}
