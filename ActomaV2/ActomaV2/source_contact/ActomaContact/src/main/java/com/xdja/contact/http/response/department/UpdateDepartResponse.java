package com.xdja.contact.http.response.department;

/**
 * Created by hkb.
 * 2015/7/10/0010.
 */
public class UpdateDepartResponse {

    private String hasMore;//是否有更多；1-有；2-无

    private String deptLastUpdateId;//部门最后一次更新标识

    private ServerDepart[] depts;//更新到的部门信息列表


    public String getDeptLastUpdateId() {
        return deptLastUpdateId;
    }

    public void setDeptLastUpdateId(String deptLastUpdateId) {
        this.deptLastUpdateId = deptLastUpdateId;
    }

    public ServerDepart[] getDepts() {
        return depts;
    }

    public void setDepts(ServerDepart[] depts) {
        this.depts = depts;
    }

    public boolean getHasMore() {
        return "1".equals(hasMore);
    }

    public void setHasMore(String hasMore) {
        this.hasMore = hasMore;
    }
}
