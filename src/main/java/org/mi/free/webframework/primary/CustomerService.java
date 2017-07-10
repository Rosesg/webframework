package org.mi.free.webframework.primary;

import org.mi.free.webframework.utils.PropsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 传统写法：
 *    我们在该类中执行数据库操作，也就是需要编写JDBC的代码，首先使用PropsUtil读取config.properties配置文件，获取JDBC相关的配置项
 *    这种写法的问题：
 *          1. 将来还有好多Service类需要读取config.properties等工作，所以将这些公共性的代码提取出来
 *          2. 执行一条select语句需要编写一大堆代码，而且必须使用try..catch..finally语句结构，开发效率很低
 */
public class CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

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
     * 获取客户列表
     */
    public List<Customer> getCustomerList() {
        Connection conn = null;
        List<Customer> customerList = new ArrayList<Customer>();
        try {
            String sql = "SELECT * FROM customer";
            conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getLong("id"));
                customer.setName(rs.getString("name"));
                customerList.add(customer);
            }
            return customerList;
        }catch (SQLException e) {
            LOGGER.error("execute sql failure",e);
            return null;
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("close connection failure",e);
                }
            }
        }
    }
}
