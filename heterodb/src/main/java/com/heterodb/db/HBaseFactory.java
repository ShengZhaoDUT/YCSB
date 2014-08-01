package com.heterodb.db;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;

import com.heterodb.common.DBConfiguration;

public class HBaseFactory {
	
	private static HConnection hConnection = null;
	private static HBaseAdmin hBaseAdmin = null;
	private static org.apache.hadoop.conf.Configuration hbaseConf;
	private static final String HBASE_HOSTNAME = "hbase_hostname";
	private static final String HBASE_PORT = "hbase_port";
	private static final String HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";
	private static final String HBAZE_ZOOKEEPER_PROPERTY_CLIENTPORT = "hbase.zookeeper.property.clientPort";
	
	static {
		String hostname = DBConfiguration.get(HBASE_HOSTNAME, "localhost");
		int port = DBConfiguration.getInt(HBASE_PORT, 2181);
		hbaseConf = HBaseConfiguration.create();
		hbaseConf.set(HBASE_ZOOKEEPER_QUORUM, hostname);
		hbaseConf.set(HBAZE_ZOOKEEPER_PROPERTY_CLIENTPORT, port + "");
	}
	
	static{
		String hostname = DBConfiguration.get("hbase-hostname", "localhost");
		int port = DBConfiguration.getInt("hbase-port", 2181);
		org.apache.hadoop.conf.Configuration hbaseConf = HBaseConfiguration.create();
		hbaseConf.set("hbase.zookeeper.quorum", hostname);
		hbaseConf.set("hbase.zookeeper.property.clientPort", port + "");
		try {
			hConnection = HConnectionManager.createConnection(hbaseConf);
			hBaseAdmin = new HBaseAdmin(hbaseConf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static HBaseAdmin getHBaseAdmin() {
		return hBaseAdmin;
	}
	
	public static HTableInterface getHBaseInstance(String tableName) {
		try {
			return hConnection.getTable(tableName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/*public static HTable getHBaseInstance(String tableName) {
		try {
			return new HTable(hbaseConf, tableName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}*/
}
