package org.mi.free.webframework.primary;

import org.mi.free.webframework.helper.DatabaseHelper;
import org.mi.free.webframework.helper.DatabaseHelperOptimize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 利用DatabaseHelper优化
 *       和CustomerService比较一下，就可以感觉到优化的好处了！
 *       现在的代码简单多了，我们不再面对PreparedStatement与ResultSet了，只需要使用DatabaseHelper就能执行数据库操作。
 *       分三步：首先需要创建一个Connection，然后进行数据库操作，最后关闭Connection。
 */
public class CustomerServiceOptimize {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceOptimize.class);

    /**
     * 获取客户列表
     */
    public List<Customer> getCustomerList() {
        Connection conn = DatabaseHelper.getConnection();
        List<Customer> customerList = new ArrayList<Customer>();
        try {
            String sql = "SELECT * FROM customer";
            return DatabaseHelper.queryEntityList(Customer.class,conn,sql);//optimize
        }finally {
            DatabaseHelper.closeConnection(conn);// optimize
        }
    }

    /**
     * 获取客户列表优化
     */
    public List<Customer> getCustomerListOptimize() {
        String sql = "SELECT * FROM customer";
        return DatabaseHelperOptimize.queryEntityList(Customer.class,sql);//optimize optimize

    }
}
