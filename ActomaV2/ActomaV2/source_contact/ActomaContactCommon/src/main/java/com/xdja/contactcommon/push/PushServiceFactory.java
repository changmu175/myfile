package com.xdja.contactcommon.push;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.xdja.contact.bean.PushMessage;
import com.xdja.contact.http.response.group.PushTypeMessage;
import com.xdja.contact.service.GroupInternalService;
import com.xdja.comm.contacttask.ITask;
import com.xdja.contact.task.configuration.TaskContactConfiguration;
import com.xdja.contact.task.friend.TaskIncrementalRequest;
import com.xdja.contact.task.group.TaskIncrementGroup;
import com.xdja.contact.task.account.TaskIncrementAccount;
import com.xdja.contact.task.department.TaskIncrementDepartContact;
import com.xdja.contact.task.friend.TaskIncrementFriend;
import com.xdja.contact.task.push.PushDepartmentTaskContact;
import com.xdja.comm.uitl.ObjectUtil;
import com.xdja.dependence.uitls.LogUtil;


/**
 * Created by wanghao on 2015/8/7.
 *
 */
public final class PushServiceFactory {

    private static PushServiceFactory instance;

    private PushServiceFactory(){}

    public static PushServiceFactory getInstance(){
        if(ObjectUtil.objectIsEmpty(instance)){
            synchronized (PushServiceFactory.class){
                instance = new PushServiceFactory();
            }
        }
        return instance;
    }
    /**
     * 推送以下业务发出提醒通知:
     * <ol>
     * <li>账户信息增量</li>
     * <li>好友请求增量</li>
     * <li>好友接受增量</li>
     * <li>好友删除增量</li>
     * <li>集团通讯录更新增量</li>
     * <li>创建群增量</li>
     * <li>解散群增量</li>
     * <li>更新群名称增量</li>
     * <li>更新群头像增量</li>
     * <li>群成员添加增量</li>
     * <li>群成员删除增量</li>
     * <li>退出群增量</li>
     * <li>群昵称增量</li>
     * </ol>
     * @param pushMessage
     * @return
     */
    public ITask getStrategy(PushMessage pushMessage) {
        String type = pushMessage.getPushServiceType();
        if (ITask.INCREMENT_ACCOUNT_TASK.equals(type)) {
            //增量账户数据
            return new TaskIncrementAccount();
        } else if (ITask.PUSH_ACCOUNT_UPDATE.equals(type)) {
            //增量账户数据
            return new TaskIncrementAccount();
        } else if (ITask.PUSH_REQUEST.equals(type)) {
            //增量更新---->好友请求列表
            return new TaskIncrementalRequest(true);
        } else if (ITask.PUSH_ACCEPT.equals(type)) {
            //增量更新好友列表
            return new TaskIncrementFriend(true);
        } else if (ITask.PUSH_DELETE.equals(type)) {
            //查询好友列表
            return new TaskIncrementFriend(true);
        } else if (ITask.PUSH_MODIFY_REMARK.equals(type)) {
            return new TaskIncrementFriend(true);
        } else {
            //[S]modify by tangsha@20170111 for 7783
            PushTypeMessage typeMessage = null;
            try{
                typeMessage = JSON.parseObject(type, PushTypeMessage.class);
            }catch (JSONException e){
                 LogUtil.getUtils().w("PushServiceFactory getStrategy JSONException "+e.toString());
            }
            if (typeMessage != null) {
                String flag = typeMessage.getFlag();
                LogUtil.getUtils().d("PushServiceFactory getStrategy flag is "+flag);
                if ((flag != null) && (flag.compareTo(PushTypeMessage.REMOVE_MEMBER_TAG) == 0)) {
                    String groupId = typeMessage.getGroupId();
                    GroupInternalService groupInternalService = GroupInternalService.getInstance();
                    groupInternalService.processGroupRemovePushMsg(groupId);
                    return null;
                }
            }
            //[E]modify by tangsha@20170111 for 7783
            return new TaskIncrementGroup(type);
        }
    }
    /**
     * 网络恢复包括以下业务(不执行)：
     * <ol>
     * <li>账户增量</li>
     * <li>好友增量</li>
     * <li>群组增量</li>
     * <li>集团部门增量</li>
     * <li>集团部门人员增量</li>
     * <li>好友请求增量</li>
     * <li>好友删除增量</li>
     * <li>好友接受增量</li>
     * </ol>
     * @param serviceType
     * @return
     */
    public ITask recovery(String serviceType){
        if(ITask.INCREMENT_ACCOUNT_TASK.equals(serviceType)){
            return new TaskIncrementAccount();
        }else if(ITask.CONFIGURATION_TASK.equals(serviceType)){
            return new TaskContactConfiguration();
        }else if(ITask.INCREMENT_DEPART_TASK.equals(serviceType)){
            return new TaskIncrementDepartContact();
        }else if(ITask.PUSH_CONTACT_UPDATE.equals(serviceType)){
            return new PushDepartmentTaskContact();
        }else if(ITask.PUSH_REQUEST.equals(serviceType)){
            return new TaskIncrementalRequest();
        }else if(ITask.PUSH_ACCEPT.equals(serviceType)
                 || ITask.PUSH_DELETE.equals(serviceType)
                 || ITask.PUSH_MODIFY_REMARK.equals(serviceType)
                 || ITask.INCREMENT_FRIEND_TASK.equals(serviceType)){
            return new TaskIncrementFriend();
        }else if(ITask.INCREMENT_GROUP_TASK.equals(serviceType)){
            return new TaskIncrementGroup();//群增量恢复---------------
        }else{
            return new TaskIncrementGroup(serviceType);//群增量恢复---------------
        }
    }
}
