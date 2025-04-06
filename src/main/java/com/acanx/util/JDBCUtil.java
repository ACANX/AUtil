package com.acanx.util;

import com.acanx.utils.StringUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
public class JDBCUtil {

    /**
     *
     * @param driverClass   驱动类
     * @param jdbcUrl       url
     * @param user          用户名
     * @param password      密码
     * @return              数据库连接
     * @throws Exception    异常信息
     */
    public static Connection getConnection(String driverClass, String jdbcUrl, String user, String password) throws Exception {
        // 1. 加载数据库驱动程序(对应的 Driver 实现类中有注册驱动的静态代码块.)
        Class.forName(driverClass);
        // 2. 通过 DriverManager 的 getConnection() 方法获取数据库连接.
        return DriverManager.getConnection(jdbcUrl, user, password);
    }


    /**
     *  释放数据库连接
     *
     * @param rs        查询结果集
     * @param statement statement
     * @param conn      连接
     */
    public static void release(ResultSet rs, Statement statement, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }



//    /**
//     * 将结果集转换成实体对象集合
//     *
//     * @param rs    结果集
//     * @param clazz 实体对象映射类
//     * @return
//     * @throws SQLException
//     * @throws IllegalAccessException
//     * @throws InstantiationException
//     */
//    public static List getResultList(ResultSet rs, Class clazz) throws SQLException, InstantiationException {
//        //结果集 中列的名称和类型的信息
//        ResultSetMetaData rsmd = rs.getMetaData();
//        int colNumber = rsmd.getColumnCount();
//        System.out.println("colNumber=" + colNumber);
//        List list = new ArrayList();
//        Field[] fields = clazz.getDeclaredFields();//Bean 对象字段数组
//        System.out.println("字段数=" + fields.length);
//
//        // 匹配Bean字段 与结果集字段
//        Map fieldMap = new HashMap<Integer, Integer>();
//        for (int i = 1; i <= colNumber; i++) {
//            for (int j = 0; j < fields.length; j++) {
//                String underlineFieldName = StringUtil.camelCaseToUnderline(fields[j].getName());
//                if (rsmd.getColumnName(i).equals(underlineFieldName)) {
//                    fieldMap.put(i, j);
//                }
//            }
//        }
//        List resultlist = new ArrayList();
//        while (rs.next()) {
//            //实例化对象
//            Object obj = null;
//            try {
//                obj = clazz.getDeclaredConstructor().newInstance();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } finally {
//            }
//            //为每个结果集（resultList）的元素属性赋值
//            for (int i = 1; i <= colNumber; i++) {
//                Object value = null;
//                String colName = rsmd.getColumnName(i);
//                //处理类型转换Mysql  BigInteger 转换为java long型
//                if (rs.getObject(i) instanceof BigInteger) {
//                    //System.out.println("【】【】【】"+value.getClass().getName());
//                    value = ((BigInteger) rs.getObject(i)).longValue();
//                    // System.out.println("value=" + value);
//                } else {
//                    value = rs.getObject(i);
//                }
//                int fieldIndex = (int) fieldMap.get(i);
//
//                Field field = fields[fieldIndex];
//                // System.out.println(field.toString());
//                System.out.println(field.getType().toString());
//                if (field == null) {
//                    throw new IllegalArgumentException("Could not find field  [" + field.getName() + "] on target [" + obj + "]");
//                }
//                boolean flag = field.canAccess(obj);
//                field.setAccessible(true);
//                if (value != null) {
//                    //System.out.println(field.getType().toString());
//                    try {
//                        field.set(obj, value);//赋值
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//                field.setAccessible(flag);
//            }
//            list.add(obj);//添加到结果集中
//        }
//        return list;
//    }


    /**
     * 链接关闭后无法使用ResultSet
     *
     * @param connection  数据库连接
     * @param sql     查询SQL语句
     * @return        返回的查询结果
     */
    public static ResultSet getResultSet(Connection connection, String sql) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * 链接关闭后无法使用ResultSetMetaData
     *
     * @param connection 数据库连接
     * @param sql      查询SQL
     * @return   返回的查询结果
     */
    public static ResultSetMetaData getResultSetMetaData(Connection connection, String sql) {
        ResultSet resultSet = getResultSet(connection, sql);
        try {
            return resultSet.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }




}
