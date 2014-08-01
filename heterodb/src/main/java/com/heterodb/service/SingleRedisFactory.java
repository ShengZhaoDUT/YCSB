package com.heterodb.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.heterodb.common.DBConfiguration;

public class SingleRedisFactory {

	private static JedisPool pool;
	
	private static final String REDIS_TOTAL = "redis_total";
	private static final String REDIS_IDLE = "redis_idle";
	private static final String REDIS_WAITMILLIS = "redis_waitmillis";
	private static final String REDIS_SYNC = "redis_sync";
	
	/*
	 * <para>redis_sync</para>
	 * <value>localhost:6379</para>
	 */
	static{
		
		int maxTotal = DBConfiguration.getInt(REDIS_TOTAL, 25);
		int maxIdle = DBConfiguration.getInt(REDIS_IDLE, 5);
		int maxMillis = DBConfiguration.getInt(REDIS_WAITMILLIS, 1000 * 60);
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxMillis);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		String machine = DBConfiguration.get(REDIS_SYNC, "localhost:6379");
		String ip = machine.split(":")[0];
		int port = Integer.valueOf(machine.split(":")[1]);
		pool = new JedisPool(config, ip, port);
	}
	
	public static Jedis getInstance() {
		
		 return pool.getResource();
	}
	
	public static void close(Jedis jedis) {
		pool.returnResource(jedis);
	}
}
