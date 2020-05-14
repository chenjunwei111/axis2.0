package com.axis2.util;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
* Description Jdbc
* @Author junwei
* @Date 16:13 2019/9/26
**/
public class JdbcServer {
	static Logger logger = Logger.getLogger(JdbcServer.class);
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String url = ConfigReaderUtils.getProperty("url");
	static String username = ConfigReaderUtils.getProperty("username");
	static String password = ConfigReaderUtils.getProperty("password");
	/*static String url = "";
	static String username = "";
	static String password = "";*/
	
	/**
	 * 设置用户、密码、ORCL等  Class.forName("oracle.jdbc.driver.OracleDriver");
	 */
	/*public static void set(String ip, Integer port, String user, String pwd, String server) {
		url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + server;
		username = user;
		password = pwd;
	}*/

	public static Connection getConn() {
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			logger.error("jdbc init error-->" + e.getMessage());
		}

		return conn;
	}

	/**
	 * 插入数据 object对象属性与表字段名保持一样（不区别大小）
	 * 
	 * @author YUAN
	 * @param comander ex:insert into table(A,B,C) values(to_date(?,'yyyy-MM-dd
	 *                 HH24:mi:ss'),?,?)
	 * @param object
	 * @return boolean
	 */
	public static synchronized boolean insert(String comander, Object object) {
		if (comander.length() == 0 || object == null) {
			return false;
		}
		try {
			List<Field> Fileds = Arrays.asList(object.getClass().getDeclaredFields());
			Map<String, Object> keyMap = new HashMap<String, Object>();
			for (Field f : Fileds) {
				keyMap.put(f.getName().toUpperCase(), f.get(object));
			}
			return insert(comander, keyMap);
		} catch (Exception e) {
			logger.error("insert object error-->" + e.getMessage());
			return false;
		}
	}

	/**
	 * 插入数据 object对象集合属性与表字段名保持一样（不区别大小）
	 * 
	 * @author YUAN
	 * @param comander ex:insert into table(A,B,C) values(to_date(?,'yyyy-MM-dd
	 *                 HH24:mi:ss'),?,?)
	 * @param objects
	 * @return boolean
	 */
	public static synchronized boolean inserts(String comander, ArrayList<Object> objects) {
		if (comander.length() == 0 || objects == null) {
			return false;
		}
		try {
			List<LinkedHashMap<String, Object>> list = new ArrayList<LinkedHashMap<String, Object>>();
			List<Field> Fileds = null;
			LinkedHashMap<String, Object> keyMap = null;
			for (Object object : objects) {
				keyMap = new LinkedHashMap<String, Object>();
				Fileds = Arrays.asList(object.getClass().getDeclaredFields());
				for (Field f : Fileds) {
					keyMap.put(f.getName().toUpperCase(), f.get(object));
				}
				list.add(keyMap);
				keyMap = null;
			}
			return inserts(comander, list);
		} catch (Exception e) {
			logger.error("insert objects error-->" + e.getMessage());
			return false;
		}
	}

	/**
	 * 插入数据 datas对象集合属性与表字段名保持一样（不区别大小）
	 * 
	 * @param comander ex:insert into table(A,B,C) values(to_date(?,'yyyy-MM-dd
	 *                 HH24:mi:ss'),?,?)
	 * @param datas
	 * @return
	 */
	public static synchronized boolean inserts(String comander, List<LinkedHashMap<String, Object>> datas) {
		if (comander.length() == 0 || datas == null || datas.size() == 0) {
			return false;
		}
		Integer fst = comander.indexOf("(");
		Integer last = comander.indexOf(")");
		if (fst < 0 || last < 0) {
			return false;
		}
		String[] fileds = comander.substring(fst + 1, last).toUpperCase().replaceAll(" ", "").split(",");
		if (fileds.length == 0) {
			return false;
		}
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = getConn();
			if (conn == null) {
				return false;
			}
			pstm = conn.prepareStatement(comander);
			conn.setAutoCommit(false);
			for (LinkedHashMap<String, Object> data : datas) {
				for (int i = 0; i < fileds.length; i++) {
					if (data.containsKey(fileds[i])) {
						pstm.setObject(i + 1, data.get(fileds[i]));
					} else {
						pstm.setObject(i + 1, null);
					}
				}
				pstm.addBatch();
			}
			pstm.executeBatch();
			conn.commit();
			return true;
		} catch (Exception e) {
//			logger.error("execute inserts datas error-->" + e.getMessage());
			logger.error("execute inserts datas error-->" + e);
			System.err.println("execute inserts datas error-->>>>>>>>>"+e.getMessage());
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error("Connection close error-->" + e.getMessage());
				}
			}
			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					logger.error("PreparedStatement close error-->" + e.getMessage());
				}
			}
		}
	}


	/**
	 * 注入sql
	 * @param  sql
	 * @return
	 */
	public static synchronized boolean diyCommitSql(String sql, List<Object> datas) {
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = getConn();
			if (conn == null) {
				return false;
			}
			pstm = conn.prepareStatement(sql);
			conn.setAutoCommit(false);

			int i=1;
			for(Object ob:datas){
				pstm.setObject(i,ob);
				i++;
			}
			pstm.executeBatch();
			conn.commit();
			return true;
		} catch (Exception e) {
			logger.error("execute inserts datas error-->" + e.getMessage());
			System.err.println("execute inserts datas error-->>>>>>>>>"+e.getMessage());
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error("Connection close error-->" + e.getMessage());
				}
			}
			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					logger.error("PreparedStatement close error-->" + e.getMessage());
				}
			}
		}
	}



	/**
	 * 插入数据
	 * 
	 * @author YUAN
	 * @param comander ex:insert into table(A,B,C) values(to_date(?,'yyyy-MM-dd
	 *                 HH24:mi:ss'),?,?)
	 * @param data     key 与comander字段对应(大写)
	 * @return boolean
	 */
	public static boolean insert(String comander, LinkedHashMap<String, Object> data) {
		if (comander.length() == 0 || data == null) {
			return false;
		}
		Integer fst = comander.indexOf("(");
		Integer last = comander.indexOf(")");
		if (fst < 0 || last < 0) {
			return false;
		}
		String[] fileds = comander.substring(fst + 1, last).toUpperCase().replaceAll(" ", "").split(",");
		if (fileds.length == 0) {
			return false;
		}
		Connection conn = null;
		PreparedStatement pstm = null;

		try {

			conn = getConn();
			if (conn == null) {
				return false;
			}
			pstm = conn.prepareStatement(comander);
			conn.setAutoCommit(false);
			for (int i = 0; i < fileds.length; i++) {
				if (data.containsKey(fileds[i])) {
					pstm.setObject(i + 1, data.get(fileds[i]));
				} else {
					pstm.setObject(i + 1, null);
				}
			}

			pstm.addBatch();
			pstm.executeBatch();
			conn.commit();
			return true;
		} catch (Exception e) {

			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error("Connection close error-->" + e.getMessage());
				}
			}
			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					logger.error("PreparedStatement close error-->" + e.getMessage());
				}
			}
		}
	}

	/**
	 * 查询execute
	 * 
	 * @param statement
	 * @return
	 */
	public static synchronized ResultSet executeStatement(String statement) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConn();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			return rs;

		} catch (Exception e) {
			logger.error("executeStatement error:" + statement, e);
			return null;
		}
	}

	/**
	 * ִ�����
	 * 
	 * @param statement
	 * @return
	 */
	public static synchronized List<Object> queryStatement(Object o, String statement) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConn();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			List<Field> Fileds = null;
			List<Object> objects = new ArrayList<Object>();
			if (rs != null) {
				List<String> fs = new ArrayList<String>();
				ResultSetMetaData _sFileds = rs.getMetaData();
				for (int i = 0; i < _sFileds.getColumnCount(); i++) {
					fs.add(_sFileds.getColumnName(i).toUpperCase());
				}
				while (rs.next()) {
					Object t = o.getClass().newInstance();
					Fileds = Arrays.asList(t.getClass().getDeclaredFields());
					for (int i = 0; i < Fileds.size(); i++) {
						Field f = Fileds.get(i);
						String s = f.getName().toUpperCase();
						if (fs.contains(s)) {
							f.set(t, rs.getObject(s));
						}
					}
					objects.add(t);
				}
			}
			return objects;

		} catch (Exception e) {
			logger.error("executeStatement error:" + statement, e);
			return null;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
	}

	/**
	 *
	 * 
	 * @param o
	 * @param statement
	 * @return
	 */
	public static synchronized <T> List<T> queryStatement(Class<T> o, String statement) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConn();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			List<Field> Fileds = null;
			List<T> objects = new ArrayList<T>();
			if (rs != null) {
				List<String> fs = new ArrayList<String>();
				ResultSetMetaData _sFileds = rs.getMetaData();
				for (int i = 0; i < _sFileds.getColumnCount(); i++) {
					fs.add(_sFileds.getColumnName(i).toUpperCase());
				}
				while (rs.next()) {
					T t = o.newInstance();
					Fileds = Arrays.asList(t.getClass().getDeclaredFields());
					for (int i = 0; i < Fileds.size(); i++) {
						Field f = Fileds.get(i);
						String s = f.getName().toUpperCase();
						if (fs.contains(s)) {
							f.set(t, rs.getObject(s));
						}
					}
					objects.add(t);
				}
			}
			return objects;

		} catch (Exception e) {
			logger.error("executeStatement error:" + statement, e);
			return null;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}



	/**
	 * Description 天级别分区 调用(DBLINK)存储过程：PRO_PARTITION_DATE创建天级别的单分区
	 *
	 * @param table 表名
	 * @param date  日期
	 *  @param isDel  删除数据 （0不删/1删）
	 * @Author junwei
	 * @Date 16:56 2019/5/20
	 **/
	public static synchronized boolean createDbLinkDatePartition(String table, String date,Integer isDel)
			throws Exception {
		String sql = "{call PRO_PARTITION_DATE(?,?,?)}";
		date = date.replace("-", "");
		CallableStatement proc = null;
		Connection conn = null;
		try {
			// 建立连接
			conn = getConn();
			proc = conn.prepareCall(sql);
			// 将数据库对象数据类型注册为java中的类型
			// I_DATE
			proc.setString(1, date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8));
			// 时间，格式：YYYY-MM-DD// I_TABLE，表名
			proc.setString(2, table);
			// 是否清空分区内数据,1清空分区，0不清空分区数据，累加
			proc.setString(3,isDel.toString() );
			// 执行
			proc.execute();
			return true;
		} catch (Exception ex) {
			logger.error("调用存储过程：createDbLinkDatePartition 创建分区时出错，详细：" + table + "-->" + ex.getMessage(), ex);
			return false;
		} finally {
			JdbcServer.close(null, proc, conn);
		}
	}


	/**
	 * 释放资源
	 *
	 * @param rs
	 * @param ps
	 * @param conn
	 */
	public static void close(ResultSet rs, Statement ps, Connection conn) {
		// 7 释放资源
		// 7.1 释放结果集
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// 7.2 静态处理块 释放资源
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// 7.3 释放连接
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


}
