package com.plus3.privilege.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.plus3.privilege.common.Utils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Created by admin on 2017/8/7.
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.plus3.privilege.dao.mapper", sqlSessionFactoryRef = "privilegeSqlSessionFactory")
public class DataSourceConfiguration {

    static final String MAPPER_LOCATION = "classpath:/mapper/*.xml";

    @Value("${privilege.datasource.url}")
    private String url;
    @Value("${privilege.datasource.username}")
    private String userName;
    @Value("${privilege.datasource.password}")
    private String password;
    @Value("${privilege.datasource.driverClassName}")
    private String driverClassName;

    @Bean(name = "privilegeDataSource")
    public DataSource getDataSource() {
        DruidDataSource dataSource =
                Utils.getDruidDataSource(driverClassName, url, userName, password);
        return dataSource;
    }

    @Bean(name = "privilegeTransactionManager")
    public DataSourceTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(getDataSource());
    }

    @Bean(name = "privilegeSqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(@Qualifier("privilegeDataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));

        return sessionFactory.getObject();
    }
}
