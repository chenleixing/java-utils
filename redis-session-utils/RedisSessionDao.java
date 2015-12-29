package com.vcg.usercenter.login.shiro.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisSessionDao extends AbstractSessionDAO {

	@Autowired
	RedisTemplate<Serializable, Session> redisTemplate;

	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = this.generateSessionId(session);
		assignSessionId(session, sessionId);
		redisTemplate.opsForValue().set(sessionId, session);
		return sessionId;
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		return redisTemplate.opsForValue().get(sessionId);
	}

	@Override
	public void update(Session session) throws UnknownSessionException {
		redisTemplate.opsForValue().set(session.getId(), session);
	}

	@Override
	public void delete(Session session) {
		redisTemplate.delete(session.getId());
	}

	@Override
	public Collection<Session> getActiveSessions() {
		Set<Serializable> keys = redisTemplate.keys("*");
		return redisTemplate.opsForValue().multiGet(keys);
	}

}
