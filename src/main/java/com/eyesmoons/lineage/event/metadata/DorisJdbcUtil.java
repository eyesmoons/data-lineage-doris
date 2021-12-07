package com.eyesmoons.lineage.event.metadata;

import com.alibaba.fastjson.JSONObject;
import com.eyesmoons.lineage.common.exception.CommonException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
public class DorisJdbcUtil {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final String CONNECTION_URL = "jdbc:mysql://%s/%s";

    /**
     * 执行sql 语句（单次执行）
     * @param hostUrl: 10.220.146.11:8040
     * @param db: ods
     * @param user: xxx
     * @param password: xxx
     * @param sql:  select 1
     */
    public static List<JSONObject> executeQuery(String hostUrl, String db, String user, String password, String sql){
        List<JSONObject> beJson = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection(hostUrl, db, user, password);
            ps = con.prepareStatement(sql);
            long start = System.currentTimeMillis();
            rs = ps.executeQuery();
            long end = System.currentTimeMillis();
            log.info("SQL执行耗时:{}ms",(end-start));
            beJson = resultSetToJson(rs);
        } catch (SQLException e) {
            log.error("SQL执行异常",e);
        } catch (Exception e) {
            log.error("SQL执行错误",e);
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(con);
        }
        return beJson;
    }


    /**
     * resultSet转List
     */
    public static List<JSONObject> resultSetToJson(ResultSet rs) throws SQLException
    {
        //定义接收的集合
        List<JSONObject> list = new ArrayList<JSONObject>();
        // 获取列数
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        // 遍历ResultSet中的每条数据
        while (rs.next()) {
            JSONObject jsonObj = new JSONObject();
            // 遍历每一列
            for (int i = 1; i <= columnCount; i++) {
                String columnName =metaData.getColumnLabel(i);
                String value = rs.getString(columnName);
                jsonObj.put(columnName, value);
            }
            list.add(jsonObj);
        }
        return list;
    }

    public static ExecutionResult execute(Connection conn, String sql, Long startTime, Boolean closeConnection) {
        ExecutionResult result = new ExecutionResult();
        doExecute(sql, result, conn, startTime, closeConnection);
        return result;
    }

    /**
     * 执行SQL
     * @param hostUrl hostUrl
     * @param db db
     * @param user user
     * @param password password
     * @param sql sql
     */
    public static ExecutionResult execute(String hostUrl, String db, String user, String password, String sql, Long startTime, Boolean closeConnection) {
        ExecutionResult result = new ExecutionResult();
        Connection conn = getConnection(hostUrl, db, user, password);
        doExecute(sql, result, conn, startTime, closeConnection);
        return result;
    }

    private static void doExecute(String sql, ExecutionResult result, Connection conn, Long startTime, Boolean closeConnection) {
        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.setFetchSize(Integer.MIN_VALUE);
            boolean execute = stmt.execute(sql);
            if (execute) {
                rs = stmt.getResultSet();
                ResultSetMetaData metaData = rs.getMetaData();
                int colNum = metaData.getColumnCount();
                // 1. metadata
                List<Map<String, String>> metaFields = Lists.newArrayList();
                // index start from 1
                for (int i = 1; i <= colNum; ++i) {
                    Map<String, String> field = Maps.newHashMap();
                    field.put("name", metaData.getColumnName(i));
                    field.put("type", metaData.getColumnTypeName(i));
                    metaFields.add(field);
                }
                // 2. data
                List<List<Object>> rows = Lists.newArrayList();
                long rowCount = 0;
                while (rs.next()) {
                    List<Object> row = new ArrayList<>(colNum);
                    // index start from 1
                    for (int i = 1; i <= colNum; ++i) {
                        row.add(rs.getObject(i));
                    }
                    rows.add(row);
                    rowCount++;
                }
                result.setData(rows);
                result.setMeta(metaFields);

                ExecutionResult.ExecutionInfo info = new ExecutionResult.ExecutionInfo();
                info.setReturnRows(rowCount);
                info.setMessage("success");
                info.setStatusCode(1);
                info.setDuration(convertNumber2DateString(System.currentTimeMillis() - startTime));
                result.setInfo(info);
            } else {
                setResultExceptQuery(result, Long.parseLong(stmt.getUpdateCount() + ""), startTime);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new CommonException(e.getMessage());
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(stmt);
            if (closeConnection) {
                DbUtils.closeQuietly(conn);
            }
        }
    }

    /**
     * 取消正在执行的sql
     * @param hostUrl hostUrl
     * @param db db
     * @param user user
     * @param password password
     * @param threadId threadId
     */
    public static Boolean cancel(String hostUrl, String db, String user, String password, Long threadId) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection(hostUrl, db, user, password);
            stmt = conn.createStatement();
            stmt.execute("kill " + threadId);
        } catch (SQLException e) {
            log.error(e.getMessage());
            return false;
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(conn);
        }
        return true;
    }

    public static void cancel(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new CommonException(e.getMessage());
        }
    }

    public static Connection getConnection(String hostUrl, String db, String user, String password) {
        Connection conn;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(String.format(CONNECTION_URL, hostUrl, db), user, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new CommonException(e.getMessage());
        }

        return conn;
    }

    private static void setResultExceptQuery(ExecutionResult result, Long returnRows, Long startTime) {
        result.setMeta(new ArrayList<>());
        result.setData(new ArrayList<>());
        ExecutionResult.ExecutionInfo info = new ExecutionResult.ExecutionInfo();
        info.setMessage("success");
        info.setStatusCode(1);
        info.setDuration(convertNumber2DateString(System.currentTimeMillis() - startTime));
        info.setReturnRows(returnRows);
        result.setInfo(info);
    }

    public static void setResultError(ExecutionResult result, String errMsg, Long startTime) {
        result.setMeta(new ArrayList<>());
        result.setData(new ArrayList<>());
        ExecutionResult.ExecutionInfo info = new ExecutionResult.ExecutionInfo();
        info.setMessage(errMsg);
        info.setStatusCode(0);
        info.setDuration(convertNumber2DateString(System.currentTimeMillis() - startTime));
        info.setReturnRows(0L);
        result.setInfo(info);
    }

    public static String convertNumber2DateString(Long n) {
        String result = "";
        long hh = n / 3600000;
        long mm = (n - hh * 3600000) / 60000;
        long ss = (n - hh * 3600000 - mm * 60000) / 1000;
        long ms = (n - hh * 3600000 - mm * 60000 - ss * 1000);

        if (n > 0) {
            if (hh > 0) {
                result += hh + "小时";
            }
            if (mm > 0) {
                result += mm + "分";
            }
            if (ss > 0) {
                result += ss + "秒";
            }
            if (ms > 0) {
                result += ms + "毫秒";
            }
        } else {
            result = "NaN";
        }
        return result;
    }
}
