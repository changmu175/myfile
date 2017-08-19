package com.xdja.imsdk.db.helper;

import java.util.Collection;
import java.util.List;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：sql语句构造                       <br>
 * 创建时间：2016/11/28 20:26                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class SqlBuilder {
    public static String insertSql(String insertInto, String tableName, String[] columns) {
        StringBuilder builder = new StringBuilder(insertInto);
        builder.append('"').append(tableName).append('"').append(" (");
        appendColumns(builder, columns);
        builder.append(") VALUES (");
        appendPlaceholders(builder, columns.length);
        builder.append(')');
        return builder.toString();
    }

    public static String deleteSql(String tableName, String columns, List<?> list) {
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        String quotedTableName = '"' + tableName + '"';
        builder.append(quotedTableName);
        if (list != null && list.size() > 0) {

            builder.append(" WHERE ");
            builder.append(columns);
            appendColumn(builder, list);
        }
        return builder.toString();
    }

    public static String deleteSql(String tableName, String columns, String value) {
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        String quotedTableName = '"' + tableName + '"';
        builder.append(quotedTableName);
        builder.append(" WHERE ");
        builder.append(columns);
        builder.append(" = ");
        builder.append("\'").append(value).append("\'").append(";");
        return builder.toString();
    }

    public static String deleteSql(String tableName, String[] columns) {
        String quotedTableName = '"' + tableName + '"';
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append(quotedTableName);
        if (columns != null && columns.length > 0) {
            builder.append(" WHERE ");
            appendColumnsEqValue(builder, quotedTableName, columns);
        }
        return builder.toString();
    }

    public static String updateSql(String tableName, String[] updateColumns, String[] whereColumns) {
        String quotedTableName = '"' + tableName + '"';
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append(quotedTableName).append(" SET ");
        appendColumnsEqualPlaceholders(builder, updateColumns);
        builder.append(" WHERE ");
        appendColumnsEqValue(builder, quotedTableName, whereColumns);
        return builder.toString();
    }

    public static String selectAll(String tableName) {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(tableName).append(".*").append(" FROM ");
        builder.append('"').append(tableName).append('"');
        return builder.toString();
    }

    private static StringBuilder appendColumnsEqualPlaceholders(StringBuilder builder, String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            appendColumn(builder, columns[i]).append("=?");
            if (i < columns.length - 1) {
                builder.append(',');
            }
        }
        return builder;
    }

    private static StringBuilder appendColumnsEqValue(StringBuilder builder, String tableAlias, String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            appendColumn(builder, tableAlias, columns[i]).append("=?");
            if (i < columns.length - 1) {
                builder.append(',');
            }
        }
        return builder;
    }

    private static StringBuilder appendColumn(StringBuilder builder, String column) {
        builder.append('"').append(column).append('"');
        return builder;
    }

    private static StringBuilder appendColumn(StringBuilder builder, String tableAlias, String column) {
        builder.append(tableAlias).append(".\"").append(column).append('"');
        return builder;
    }

    public static StringBuilder appendColumn(StringBuilder builder, Collection<?> columns) {
        builder.append(" IN(");

        if (!columns.isEmpty()) {
            for (Object tag : columns) {
                builder.append("\'").append(tag).append("\',");
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append(") ");
        } else {
            builder.append(") ");
        }
        return builder;
    }

    public static StringBuilder appendColumns(StringBuilder builder, String[] columns) {
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            builder.append('"').append(columns[i]).append('"');
            if (i < length - 1) {
                builder.append(',');
            }
        }
        return builder;
    }

    public static StringBuilder appendPlaceholders(StringBuilder builder, int count) {
        for (int i = 0; i < count; i++) {
            if (i < count - 1) {
                builder.append("?,");
            } else {
                builder.append('?');
            }
        }
        return builder;
    }

    public static String getLimit(int size) {
        return " LIMIT " + size;
    }

    public static String getOrder(String columns) {
        return " ORDER BY " + columns + " DESC ";
    }

    public static String getOrderDefault(String columns) {
        return " ORDER BY " + columns + " ASC ";
    }

}
