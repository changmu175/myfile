package com.xdja.contact.task.push;

import com.xdja.comm.contacttask.ITask;
import com.xdja.contact.task.account.TaskIncrementAccount;
import com.xdja.contact.task.department.TaskIncrementDepartContact;

/**
 * Created by wanghao on 2015/12/23.
 * 注意这里taskSuccess 并没有调用所以这里需要手动调用
 * 这里只是实现了ITask 其实处理业务的线程还在TaskIncrementAccount.class 和 TaskIncrementDepartContact.class
 */
public class PushDepartmentTaskContact implements ITask {

    @Override
    public void template() {
        TaskIncrementAccount incrementAccount = new TaskIncrementAccount();
        incrementAccount.template();

        TaskIncrementDepartContact departContact = new TaskIncrementDepartContact();
        departContact.template();
    }



    @Override
    public String getTaskId() {
        return PUSH_CONTACT_UPDATE;
    }

    @Override
    public String getReason() {
        return "";
    }

}
