package com.plus3.privilege.common;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * Created by admin on 2017/9/15.
 */
public class Utils {
    public static void print(String text) {
        System.out.println(text);
    }

    public static DruidDataSource getDruidDataSource(String driverClassName,
                                                     String dataSourceUrl,
                                                     String userName,
                                                     String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dataSourceUrl);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);

        //
        dataSource.setInitialSize(10);
        dataSource.setMinIdle(10);
        dataSource.setMaxActive(100);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(30000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(100);

        return dataSource;
    }
}
