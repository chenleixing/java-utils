package com.vcg.usercenter.common.interceptor.route;

import java.lang.annotation.Annotation;

import org.aspectj.lang.JoinPoint;

/**
 * AOP拦截切换数据源
 */
public class DataSourceChange {
	public void change(JoinPoint join){
		Annotation[] annotations = join.getThis().getClass().getAnnotations();
		for (Annotation annotation : annotations) {
			if(annotation instanceof DataSource){
				DataSource data=(DataSource) annotation;
				DataSourceHolder.setDataSource(data.name());
				break;
			}
		}
	}
}
