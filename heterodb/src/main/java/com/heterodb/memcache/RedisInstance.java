package com.heterodb.memcache;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import redis.clients.jedis.ShardedJedis;

import com.heterodb.common.DB;

public class RedisInstance extends DB{
	
	private ShardedJedis shardedJedis;
	
	public ShardedJedis getClient() {
		return shardedJedis;
	}
	
	public RedisInstance() {
		shardedJedis = RedisFactory.getClient();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		RedisFactory.close(shardedJedis);
	}

	@Override
	public int read(String database, String table, String key,
			Set<String> fields, Map<String, String> result) {
		// TODO Auto-generated method stub
		Map<String, String> response = shardedJedis.hgetAll(key);
		if(fields == null) {
			result.putAll(response);
		}
		else {
			String[] fieldArray = (String[]) fields.toArray();
			List<String> values = shardedJedis.hmget(key, fieldArray);
			
			Iterator<String> resultValue = values.iterator();
			
			for(String resultKey : fieldArray) {
				result.put(resultKey, resultValue.next());
			}

		}
		
		return result.isEmpty() ? 1 : 0;
	}

	@Override
	public int update(String database, String table, String key,
			Map<String, String> result) {
		// TODO Auto-generated method stub
		
		return insert(database, table, key, result);
	}
	
	@Override
	public int insert(String database, String table, String key,
			Map<String, String> values) {
		// TODO Auto-generated method stub
		if((shardedJedis.hmset(key, values)).equals("OK")) {
			return 0;
		}
		return 1;
	}

	@Override
	public int scan(String database, String table, String startkey,
			int recordcount, Set<String> fields,
			Vector<Map<String, String>> result) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(String database, String table, String key) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
