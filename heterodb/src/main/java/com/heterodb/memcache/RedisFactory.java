/*
 * Create RedisPool, it can return jedis instance from a pool. 
 */

package com.heterodb.memcache;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heterodb.common.DBConfiguration;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 
 * @author zs
 * @version 1.0
 * Description: create Factory, set parameters of RedisPool.
 * This Pool use consistent hash algorithm. Return ShardedJedis
 * We need give a List represent machine list to construct RedisFactory.
 * 
 */
public class RedisFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisFactory.class);
	private static final String REDIS_TOTAL = "redis_total";
	private static final String REDIS_IDLE = "redis_idle";
	private static final String REDIS_WAITMILLIS = "redis_waitmillis";
	private static final String REDIS_SHARD = "redis_shard";
	
	private static ShardedJedisPool shardedJedisPool;
	//private List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
	
	static{
		
		int maxTotal = DBConfiguration.getInt(REDIS_TOTAL, 25);
		int maxIdle = DBConfiguration.getInt(REDIS_IDLE, 5);
		int maxMillis = DBConfiguration.getInt(REDIS_WAITMILLIS, 1000 * 60);
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxMillis);
		config.setTestOnBorrow(true);
		shardedJedisPool = new ShardedJedisPool(config, getMachineList());
	}
	
	private static List<JedisShardInfo> getMachineList() {
		List<JedisShardInfo> machineList = new ArrayList<JedisShardInfo>();
		String value = DBConfiguration.get(REDIS_SHARD, "localhost:6379");
		System.out.println("DEBUG: " + value);
		String[] machines = value.split(";");
		for(String machine : machines) {
			String ip = machine.split(":")[0];
			int port = Integer.valueOf(machine.split(":")[1]);
			System.out.println(ip + " " + String.valueOf(port));
			logger.debug(ip + " " + String.valueOf(port));
			JedisShardInfo jsInfo = new JedisShardInfo(ip, port);
			machineList.add(jsInfo);
		}
		return machineList;
	}
	
	public static ShardedJedis getClient() {
		return shardedJedisPool.getResource();
	}
	
	public static void close(ShardedJedis jedis) {
		shardedJedisPool.returnResource(jedis);
	}
}
