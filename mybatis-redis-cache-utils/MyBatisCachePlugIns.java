package com.vcg.usercenter.common.interceptor.cache;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.vcg.usercenter.common.log.LogApt;
import com.vcg.usercenter.common.utils.JsonUtil;
import com.vcg.usercenter.common.utils.StringUtils;
import com.vcg.usercenter.users.model.User;

@Intercepts({
		/*
		 * @Signature(type = Executor.class, method = "query", args = {
		 * MappedStatement.class, Object.class, RowBounds.class,
		 * ResultHandler.class }),
		 */
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class MyBatisCachePlugIns implements Interceptor {

	private List<String> methods = Arrays.asList("com.vcg.usercenter.users.dao.UserDao.update",
			"com.vcg.usercenter.users.dao.UserDao.updateByPrimaryKeySelective");
	private String emailKey = "email_userId";
	private String mobileKey = "mobile_userId";
	private String userNameKey = "userName_userId";
	private String userIdKey = "users";
	@Autowired
	StringRedisTemplate redisClient;


	public Object intercept(Invocation invocation) throws Throwable {

		Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0];
		String msId = ms.getId();
		// 不需要拦截的方法直接返回
		if (isMapperMethod(msId)) {
			// 第一次经过处理后，就不会是ProviderSqlSource了，一开始高并发时可能会执行多次，但不影响。以后就不会在执行了
			try {
				LogApt.info("update intercept method query cache start!");
				Object updateInfo = args[1];
				if (updateInfo == null) {
					return null;
				}
				updateCache(updateInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return invocation.proceed();
	}

	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	public boolean isMapperMethod(String methodName) {
		return methods.contains(methodName);
	}

	public void setProperties(Properties properties) {

	}

	public void updateCache(Object obj) {

		User user = (User) obj;
		String userId = user.getUserId();
		Object object = redisClient.opsForHash().get(userIdKey, userId);
		if (object != null) {
			user = JsonUtil.parseObject(object.toString(), User.class);
		}

		// 删除缓存
		redisClient.opsForHash().delete(userIdKey, user.getUserId());

		// 删除email对应的UserId缓存
		if (!StringUtils.isBlank(user.getEmail())) {
			redisClient.opsForHash().delete(emailKey, user.getEmail());
		}

		// 删除mobile对应的uesrId缓存
		if (!StringUtils.isBlank(user.getMobile())) {
			redisClient.opsForHash().delete(mobileKey, user.getMobile());
		}

		// 删除userName对应的userId缓存
		if (!StringUtils.isBlank(user.getUserName())) {
			redisClient.opsForHash().delete(userNameKey, user.getUserName());
		}
		LogApt.info("Cache  has " + user.getUserId() + " id,update cache!");
	}

}
