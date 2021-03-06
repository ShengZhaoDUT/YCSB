package com.heterodb.db;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.heterodb.common.DB;
import com.heterodb.common.DBConfiguration;

public class HBaseInstance extends DB{

	private HTableInterface htable;
	private static final String KEY = "_key";
	private static final String COLUMNFAMILY = "sync_column_family";
	private byte columnFamilyBytes[] = Bytes.toBytes(DBConfiguration.get(COLUMNFAMILY, "default"));
	
	public HBaseInstance(String table) {
		htable = HBaseFactory.getHBaseInstance(table);
		try {
			htable.setWriteBufferSize(1024*1024*12);
			htable.setAutoFlush(false,true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		try {
			htable.flushCommits();
			htable.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int read(String database, String table, String key, Set<String> fields,
			Map<String, String> result) {
		// TODO Auto-generated method stub
		Get get = new Get(Bytes.toBytes(key));
		if(fields == null) {
			get.addFamily(columnFamilyBytes);
		}
		else {
			for(String field : fields) {
				get.addColumn(columnFamilyBytes, Bytes.toBytes(field));
			}
		}
		Result r = null;
		try {
			r = htable.get(get);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		}
		for(Cell cell : r.rawCells()) {
			result.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
		}
		return 0;
	}

	@Override
	public int update(String database, String table, String key, Map<String, String> result) {
		// TODO Auto-generated method stub
		Put put = new Put(Bytes.toBytes(key));
		for(Map.Entry<String, String> entry : result.entrySet()) {
			put.add(columnFamilyBytes, Bytes.toBytes(entry.getKey()), Bytes.toBytes(entry.getValue()));
		}
		try {
			htable.put(put);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		}
		return 0;
	}

	@Override
	public int insert(String database, String table, String key, Map<String, String> values) {
		// TODO Auto-generated method stub
		update(null, table, key, values);
		return 0;
	}

	@Override
	public int scan(String database, String table, String startkey, int recordcount,
			Set<String> fields, Vector<Map<String, String>> result) {
		// TODO Auto-generated method stub
		Scan s = new Scan(Bytes.toBytes(startkey));
		s.setCaching(recordcount);
		columnFamilyBytes = Bytes.toBytes("f1");
		if(fields == null)
			s.addFamily(columnFamilyBytes);
		else
			for(String field : fields)
			{
				s.addColumn(columnFamilyBytes, Bytes.toBytes(field));
			}
		ResultScanner scanner = null;
		try {
			scanner = htable.getScanner(s);
			int numResults = 0;
			for(Result rr = scanner.next(); rr != null; rr = scanner.next())
			{
				String keyString = Bytes.toString(rr.getRow());
				HashMap<String, String> rowResult = new HashMap<String, String>();
				rowResult.put(KEY, keyString);
				for(Cell kv : rr.rawCells())
				{
					rowResult.put(Bytes.toString(CellUtil.cloneQualifier(kv)), Bytes.toString(CellUtil.cloneValue(kv)));
				}
				result.add(rowResult);
				numResults++;
				if(numResults >= recordcount)
					break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			scanner.close();
		}
		
		return 0;
	}

	@Override
	public int delete(String database, String table, String key) {
		// TODO Auto-generated method stub
		Delete del = new Delete(Bytes.toBytes(key));
		try {
			htable.delete(del);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		}
		return 0;
	}
	
}
