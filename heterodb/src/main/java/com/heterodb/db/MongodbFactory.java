package com.heterodb.db;

import java.net.UnknownHostException;

import com.heterodb.common.DBConfiguration;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

public class MongodbFactory {
	
	private static Mongo mongo;
	private static final String MONGO_HOSTNAME = "mongo_hostname";
	private static final String MONGO_PORT = "mongo_port";
	private static final String CONNECTION_MULTIPLIER = "connection_multiplier";
	private static final String CONNECTION_HOST = "connection_host";
	
	static {
		String hostname = DBConfiguration.get(MONGO_HOSTNAME, "localhost");
		int port = DBConfiguration.getInt(MONGO_PORT, 27017);
		int threads = DBConfiguration.getInt(CONNECTION_MULTIPLIER, 5);
		int connections = DBConfiguration.getInt(CONNECTION_HOST, 10);
		try {
			MongoOptions options = new MongoOptions();
			options.setThreadsAllowedToBlockForConnectionMultiplier(threads);
			options.setConnectionsPerHost(connections);
			mongo = new Mongo(new ServerAddress(hostname, port), options);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Mongo getMongoInstance() {
		return mongo;
	}
	
	public static void shutdown() {
		mongo.close();
	}
}