package org.mi.free.webframework.helper;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.mi.free.webframework.utils.PropsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * 数据库操作助手
 */
public class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
    //利用dbutils简化JDBC操作
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    //static语句块在对象的构造函数前被调用，且仅被调用一次
    static {
        Properties conf = PropsUtil.loadProps("config.properties");
        DRIVER = PropsUtil.getString(conf,"jdbc.driver");
        URL = PropsUtil.getString(conf,"jdbc.url");
        USERNAME = PropsUtil.getString(conf,"jdbc.username");
        PASSWORD = PropsUtil.getString(conf,"jdbc.password");
        try {
            Class.forName(DRIVER);
        }catch(ClassNotFoundException e) {
            LOGGER.error("can not load jdbc driver",e);
        }
    }

    /**
     * 获得数据库连接
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        }catch (SQLException e) {
            LOGGER.error("get connection failure",e);
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure",e);
            }
        }
    }

    /**
     * 查询实体列表
     *  说明：
     *        使用dbutils提供的QueryRunner对象可以面向实体（Entity）进行查询。
     *        dbutils首先执行SQL语句并返回一个ResultSet，随后通过反射去创建并初始化实体对象
     *        我们此方法需要返回的是List，所以使用BeanListHandler
     */
    public static <T> List<T> queryEntityList(Class<T> entityClass,Connection conn,String sql,Object... params) {
        List<T> entityList;
        try {
            entityList = QUERY_RUNNER.query(conn,sql,new BeanListHandler<T>(entityClass),params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure",e);
            throw new RuntimeException(e);
        }finally {
            closeConnection(conn);
        }
        return entityList;
    }
}
