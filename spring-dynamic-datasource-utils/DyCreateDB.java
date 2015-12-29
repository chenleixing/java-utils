package com.vcg.usercenter.common.interceptor.route;

import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 数据库动态切换
 * 
 * @author 19128_000
 * 
 */
@Component
public class DyCreateDB {

	@Autowired
	private SqlSessionFactoryBean sessionFactoryBean;

	@Autowired
	private DruidDataSource druidDataSource;

	@Autowired
	private DataSourceTransactionManager tx;

	private Logger logger = Logger.getLogger(this.getClass());

	// 此操作会直接覆盖原有数据源.
	public boolean switchDB(DruidDataSource dataSource) {

		logger.debug("正在切换数据源:" + druidDataSource.getUrl() + "切换为"
				+ dataSource.getUrl());
		try {
			this.druidDataSource = dataSource;
		} catch (Exception e) {
			logger.debug("切换失败:" + dataSource.getUrl());
			return false;
		}
		return true;
	}

	// 切换mybatis数据源,事务.此操作不会停用上一数据源,
	public boolean switchFactoryDBAndTx(DruidDataSource dataSource) {
		// 切换数据源
		try {
			// 切换数据库
			sessionFactoryBean.setDataSource(dataSource);
			// 切换事务
			tx.setDataSource(dataSource);
		} catch (Exception e) {
			logger.error("切换数据源失败:" + dataSource.getUrl());
			return false;
		}
		return true;

	}

	// 创建数据库连接池
	public static DruidDataSource createDBPool(
			@SuppressWarnings("rawtypes") Map conInfo) throws SQLException {
		DruidDataSource db = new DruidDataSource();
		db.setUrl(conInfo.get("url").toString());
		db.setUsername(conInfo.get("username").toString());
		db.setPassword(conInfo.get("password").toString());

		if (conInfo.get("driverClassName") != null) {
			db.setDriverClassName(conInfo.get("driverClassName").toString());
		}

		if (conInfo.get("maxActive") != null) {
			db.setMaxActive(Integer.parseInt(conInfo.get("maxActive")
					.toString()));
			db.setMaxOpenPreparedStatements(Integer.parseInt(conInfo.get(
					"maxActive").toString()) - 5);
		}

		if (conInfo.get("initialSize") != null) {
			db.setInitialSize(Integer.parseInt(conInfo.get("initialSize")
					.toString()));
		}

		if (conInfo.get("minIdle") != null) {
			db.setMinIdle(Integer.parseInt(conInfo.get("minIdle").toString()));
		}
		db.setMaxWait(60000);
		db.setFilters("stat");
		db.setTimeBetweenEvictionRunsMillis(300000);
		db.setValidationQuery("SELECT 'x'");
		db.setTestWhileIdle(true);
		db.setTestOnBorrow(false);
		db.setTestOnReturn(false);
		db.setRemoveAbandoned(true);
		db.setRemoveAbandonedTimeout(1800);
		db.setLogAbandoned(true);

		try {
			db.init();
		} catch (Exception e) {
			return null;
		}
		return db;

	}
}
