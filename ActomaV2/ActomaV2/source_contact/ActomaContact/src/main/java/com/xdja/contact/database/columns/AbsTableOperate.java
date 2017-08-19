package com.xdja.contact.database.columns;

import com.xdja.dependence.uitls.LogUtil;

import java.lang.reflect.Field;

/**
 * Created by wanghao on 2015/6/26.
 */
public abstract class AbsTableOperate {


    protected abstract Class getCls();

    public String buildTable(){
        StringBuffer sb = new StringBuffer();
        Class cls = getCls();
        try {
            Object obj = cls.newInstance();
            Field[] fields = cls.getDeclaredFields();
            int length = fields.length;
            for(int i = 0 ; i<length ; i ++){
                Field field = fields[i];
                if(field.getName().equalsIgnoreCase("TABLE_NAME")) { //表名
                    appendTableName(sb, String.valueOf(field.get(obj)));
                    appendLeftBracket(sb);
                }else{
                    continue;
                }
            }
            for(int i = 0 ; i<length ; i ++){
                Field field = fields[i];
                if(isPrimaryKey(field.getName(),cls) && !(field.getName().equalsIgnoreCase("ID"))){
                    sb.append(String.valueOf(field.get(obj)));
                    sb.append(" TEXT PRIMARY KEY ");
                    appendCommas(sb);
                }else if(field.getName().equalsIgnoreCase("ID")) { //自增id 特殊处理
                    appendAutoId(sb, String.valueOf(field.get(obj)));
                    appendCommas(sb);
                } else{
                    continue;
                }
            }
            for(int i = 0 ; i<length ; i ++ ){
                Field field = fields[i];
                if(field.getName().equalsIgnoreCase("TABLE_NAME")){ //表名
                    continue;
                }else if(isPrimaryKey(field.getName(),cls)){ //自增id 特殊处理
                    continue;
                } else if (field.get(obj) == null) {
                    android.util.Log.w("AbsTableOperate", "invalid column "+field.getName());
                    continue;
                } else {  //其他通用 String字段
                    appendNormalColumns(sb, String.valueOf(field.get(obj)));
                    appendCommas(sb);
                }
                /*field.setAccessible(true);
                if(field.getName().equalsIgnoreCase("TABLE_NAME")){ //表名
                    appendTableName(sb, String.valueOf(field.get(obj)));
                    appendLeftBracket(sb);
                }else if(field.getName().equalsIgnoreCase("ID")){ //自增id 特殊处理
                    appendAutoId(sb,String.valueOf(field.get(obj)));
                    appendCommas(sb);
                }else{  //其他通用 String字段
                    if(i != (length-1)){
                        appendNormalColumns(sb,String.valueOf(field.get(obj)));
                        appendCommas(sb);
                    }else{
                        appendNormalColumns(sb,String.valueOf(field.get(obj)));
                        appendRightBracket(sb);
                    }
                }*/
            }
            String sql  = sb.substring(0,sb.length()-2);
            sb = appendRightBracket(new StringBuffer(sql));
        } catch (Exception e) {
            LogUtil.getUtils().e("AbsTableOperate  buildTable error:"+e.getMessage());
        }
        return sb.toString();
    }

    private boolean isPrimaryKey(String c,Class<? extends Class> cls){
        if("ID".equalsIgnoreCase(c) ){
            return true;
        }
        if("DEPT_ID".equalsIgnoreCase(c) &&  TableDepartment.class.getName().equals(cls.getName())){
            return true;
        } if("WORKER_ID".equalsIgnoreCase(c) &&  TableDepartmentMember.class.getName().equals(cls.getName())){
            return true;
        }
        return false;
    }

    protected StringBuffer appendTableName(StringBuffer sb,String tableName){
        sb.append("create table ");
        sb.append(tableName);
        return sb;
    }

    protected StringBuffer appendLeftBracket(StringBuffer sb){
        sb.append(" ( ");
        return sb;
    }

    protected StringBuffer appendRightBracket(StringBuffer sb){
        sb.append(" ); ");
        return sb;
    }

    protected StringBuffer appendCommas(StringBuffer sb){
        sb.append(" , ");
        return sb;
    }

    protected StringBuffer appendAutoId(StringBuffer sb,String autoId){
        sb.append(autoId);
        sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1");
        return sb;
    }

    protected StringBuffer appendNormalColumns(StringBuffer sb,String column){
        sb.append(column);
        sb.append(" TEXT ");
        return sb;
    }
}
