package com.plus3.privilege.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * Created by admin on 2017/12/19.
 */
// FIXME try to rename it
@Component
public class SimpleDataReader {
    @Autowired private DataSource dataSource;

    private static final Map<Integer, Class> TypeMap;
    static {
        {
            TypeMap = new HashMap<>();
            TypeMap.put(Types.INTEGER, Integer.class);
            TypeMap.put(Types.VARCHAR, String.class);
            TypeMap.put(Types.TIMESTAMP, Long.class);
        }
    }

    //===================================================================================
    public <T> List<T> simpleQuery(String table, Class<T> clazz,
                                   Integer from, Integer limit) {
        String sql = "select * from " + table;
        if (from != null && limit != null)
            sql += " limit " + from + ", " + limit;

        return doSql(sql, clazz);
    }

    public <T> List<T> simpleQuery(String table, Class<T> clazz,
                                   int adminId,
                                   Integer from, Integer limit) {
        String sql = "select * from " + table + "where creator_id = " + adminId;
        if (from != null && limit != null)
            sql += " limit " + from + ", " + limit;

        return doSql(sql, clazz);
    }

    public <T> List<T> simpleQuery(String table, Class<T> clazz,
                                   Collection<String> inGroupList,
                                   Integer from, Integer limit) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ").append(table);
        sb.append(" where group in (");

        for (String groupId : inGroupList)
            sb.append(groupId).append(",");
        sb.replace(sb.length() - 1, sb.length(), ") ");

        if (from != null && limit != null)
            sb.append(" limit ").append(from).append(", ").append(limit);

        return doSql(sb.toString(), clazz);
    }

    public <T> List<T> simpleQuery(String table, Class<T> clazz,
                                   String groupToken, boolean withChildrenGroup,
                                   Integer from, Integer limit) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ").append(table);

        if (withChildrenGroup)
            sb.append(" where group_token like ('").append(groupToken).append("%' ");
        else
            sb.append(" where group_token = ").append(groupToken);

        if (from != null && limit != null)
            sb.append(" limit ").append(from).append(", ").append(limit);

        return doSql(sb.toString(), clazz);
    }

    //===================================================================================
    public <T> List<T> doSql(String sql, Class<T> clazz) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            return getObjects(resultSet, clazz);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T> List<T> getObjects(ResultSet resultSet, Class<T> clazz) throws Exception {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        int columnCount = resultSetMetaData.getColumnCount();

        int[] columnTypes = new int[columnCount];
        String[] columnNames = new String[columnCount];
        String[] setters = new String[columnCount];

        for (int n = 0; n < columnCount; n++) {
            columnNames[n] = resultSetMetaData.getColumnName(n + 1);
            setters[n] = getSetterName(columnNames[n]);
            columnTypes[n] = resultSetMetaData.getColumnType(n + 1);
        }

        List<T> objectList = new ArrayList<>();
        while (resultSet.next()) {
            String[] values = new String[columnCount];
            for (int n = 0; n < columnCount; n++)
                values[n] = resultSet.getString(n + 1);

            T object = getObject(setters, columnTypes, values, clazz);
            objectList.add(object);
        }

        return objectList;
    }

    //===================================================================================
    private <T> T getObject(String[] setters,
                            int[] columnTypes,
                            String[] values,
                            Class<T> clazz) throws Exception {
        T object = clazz.newInstance();

        for (int n = 0; n < values.length; n++) {
            if (values[n] == null)
                continue;

            Class parameterType = TypeMap.get(columnTypes[n]);
            if (parameterType == null)
                continue;

            Method method = clazz.getMethod(setters[n], parameterType);
            if (method == null)
                continue;

            Object value = getParameter(values[n], parameterType);
            method.invoke(object, value);
        }

        return object;
    }

    private String getSetterName(String columnName) {
        String[] parts = columnName.split("_");
        StringBuilder sb = new StringBuilder("set");
        for (String part : parts)
            sb.append(getCaptiveName(part));

        return sb.toString();
    }

    private String getCaptiveName(String name) {
        char[] chars = name.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

    private Object getParameter(String value, Class type) {
        if (type == Integer.class)
            return Integer.valueOf(value);
        else if (type == Long.class)
            return Long.parseLong(value);

        return value;
    }
}
