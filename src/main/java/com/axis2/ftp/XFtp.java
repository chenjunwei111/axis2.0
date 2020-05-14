package com.axis2.ftp;

import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * suport FTP&STP
 * 
 * @author YUAN
 * 
 */
public class XFtp {
	private Connection conn;
	protected String host;
	protected String username;
	protected String password;
	protected Integer port;


	private static final Logger log = Logger.getLogger(XFtp.class.getClass());


	/**
	 * @param host
	 * @param username
	 * @param password
	 * @param port
	 */
	public XFtp(String host, String username, String password, Integer port) {
		this.host = host;
		this.username = username;
		this.password = password;
		this.port = port;
	}

	/**
	 * @param host
	 * @param username
	 * @param password
	 */
	public XFtp(String host, String username, String password) {
		this.host = host;
		this.username = username;
		this.password = password;
		this.port = 21;
	}

	/**
	 * 创建连接
	 */

	public Connection createConnection() {
		this.conn = new Connection().setConnections(this);
		return this.conn;
	}

	/**
	 * 下载文件
	 * 
	 * @param ftpDir      FTP目录 FTP目录
	 * @param ftpFileName FTP文件名
	 * @param localDir    保存路径
	 * @return
	 */
	public boolean receiveFile(String ftpDir, String ftpFileName, String localDir) {
		if (!this.conn.hasConnected()) {
			log.error("************************ftp连接失败。。。。");
			return false;
		}
		if (ftpDir == null || "".equals(ftpDir)) {
			log.error("************************ftp文件夹地址为空。。。。");
			return false;
		}
		FileOutputStream fs = null;
		FTPFile[] ff = null;
		OutputStream is = null;
		try {
			if (this.conn.xftp.equals(Connection.xftpChannel.SFTP)) {
				this.conn.sftp.cd(ftpDir);
				String saveFile = localDir + File.separator + ftpFileName;
				File file = new File(saveFile);
				fs = new FileOutputStream(file);
				this.conn.sftp.get(ftpFileName, fs);
				return true;
			} else if (this.conn.xftp.equals(Connection.xftpChannel.FTP)) {
				ftpFileName = ftpFileName.toLowerCase();
				this.conn.ftp.enterLocalPassiveMode();//FTP
//				log.info("ftp下载1:"+ftpFileName);
//				log.info("ftp下载2:"+ftpDir);
				boolean b=this.conn.ftp.changeWorkingDirectory(ftpDir);
//				log.info("路径切换"+b);
				ff = this.conn.ftp.listFiles();
//				log.info("ftp下载数量:"+ff.length);
				for (FTPFile _ff : ff) {
					if (_ff.getName().toLowerCase().equals(ftpFileName)) {
						//设置二进制转化，针对xlsx文件
						this.conn.ftp.setFileType(FTP.BINARY_FILE_TYPE);
						File localFile = new File(localDir + File.separator + _ff.getName());
						is = new FileOutputStream(localFile);
						this.conn.ftp.retrieveFile(_ff.getName(), is);
						is.close();
//						log.info("ftp下载文件4："+ftpFileName);
					}
				}
			}
		} catch (Exception e) {
           e.printStackTrace();
           log.error("************************ftp错误：",e);
			return false;
		} finally {
			try {
				if (fs != null) {
					fs.close();
					fs = null;
				}
				if (ff != null) {
					ff = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (Exception e2) {
				log.error("************************ftp错误：",e2);
				e2.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 下载文件夹
	 * 
	 * @param ftpDir   FTP目录 FTP目录
	 * @param localDir 保存路径
	 * @return
	 */
	public boolean receiveDir(String ftpDir, String localDir) {
		if (!this.conn.hasConnected()) {
			return false;
		}
		if (ftpDir == null || "".equals(ftpDir)) {
			return false;
		}

		FileOutputStream fs = null;
		FTPFile[] ff = null;
		OutputStream is = null;

		try {
			List<Map<String, Object>> maps = this.mapFiles(ftpDir, null, null);
			if (maps == null) {
				return false;
			}
			if (this.conn.xftp.equals(Connection.xftpChannel.SFTP)) {
				this.conn.sftp.cd(ftpDir);
				for (Map<String, Object> _map : maps) {
					String filename = _map.get("FILE").toString();
					String saveFile = localDir + File.separator + filename;
					File file = new File(saveFile);
					fs = new FileOutputStream(file);
					this.conn.sftp.get(filename, fs);
				}
			} else if (this.conn.xftp.equals(Connection.xftpChannel.FTP)) {
				for (Map<String, Object> _map : maps) {
					String filename = _map.get("FILE").toString();
					String saveFile = localDir + File.separator + filename;
					File file = new File(saveFile);
					is = new FileOutputStream(file);
					this.conn.ftp.retrieveFile(filename, is);
				}
				is.close();
			}
			maps = null;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("************************ftp错误",e);

			return false;
		} finally {
			try {
				if (fs != null) {
					fs.close();
					fs = null;
				}
				if (ff != null) {
					ff = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (Exception e2) {
				log.error("************************ftp错误",e2);

				e2.printStackTrace();
			}
		}
	}

	/**
	 * 文件列表
	 * 
	 * @param ftpDir    FTP目录 FTP目录
	 * @param filter    (且关系用&间隔,或关系用|间隔，格式：开始于(filter*)、结束于(*filter)、无限制(filter)、中间(*
	 *                  filter*)
	 * @param sortByPro 按属性排序，包括FILE、MTIME、SIZE
	 * 
	 * @return 文件列表
	 * @throws Exception
	 */
	public List<String> listFile(String ftpDir, String filter, String sortByPro) {
		if (ftpDir == null || "".equals(ftpDir)) {
			return null;
		}
		if (!this.conn.hasConnected()) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		try {
			List<Map<String, Object>> mapFiles = this.mapFiles(ftpDir, filter, sortByPro);
			for (Map<String, Object> map : mapFiles) {
				list.add(map.get("FILE").toString());
			}
		} catch (Exception e) {
			log.error("************************ftp错误",e);
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 文件列表
	 * 
	 * @param ftpDir    FTP目录 /ftpDir/
	 * @param filter    (且关系用&间隔,或关系用|间隔，格式：开始于(filter*)、结束于(*filter)、无限制(filter)、中间(*
	 *                  filter*)
	 * @param sortBypro 按属性排序，包括FILE、MTIME、SIZE
	 * 
	 * @return Map<String, Object> key:FILE/MTIME/SIZE
	 * @throws Exception
	 */
	public List<Map<String, Object>> mapFiles(String ftpDir, String filter, String sortBypro) {
		if (!this.conn.hasConnected()) {
			return null;
		}
		if (ftpDir == null || "".equals(ftpDir)) {
			return null;
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			if (this.conn.xftp.equals(Connection.xftpChannel.SFTP)) {
				Vector<?> v = this.conn.sftp.ls(ftpDir);
				SftpATTRS sftpattr = null;
				for (Object obj : v) {
					if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
						map = new HashMap<String, Object>();
						String fileName = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getFilename();
						if (fileName.startsWith(".")) {
							continue;
						}
						if (Filter(fileName, filter)) {
							sftpattr = ((com.jcraft.jsch.ChannelSftp.LsEntry) obj).getAttrs();
							if (!sftpattr.isDir()) {
								map.put("MTIME", this.stampToDate(sftpattr.getMtimeString()));
								map.put("SIZE", sftpattr.getSize());
								map.put("FILE", fileName);
								list.add(map);
							}
						}
					}
				}

			} else if (this.conn.xftp.equals(Connection.xftpChannel.FTP)) {
				FTPFile[] ff = null;
				if (this.conn.ftp.changeWorkingDirectory(ftpDir)) {
					ff = this.conn.ftp.listFiles();
					for (FTPFile _ff : ff) {
						map = new HashMap<String, Object>();
						String fileName = _ff.getName();
						if (Filter(fileName, filter)) {
							if (fileName.startsWith(".") || !_ff.isFile()) {
								continue;
							}

							map.put("MTIME", stampToDate(_ff.getTimestamp().getTime().toString()));
							map.put("SIZE", _ff.getSize());
							map.put("FILE", fileName);
							list.add(map);
						}
					}
				}
				ff = null;
			}
			if (sortBypro != null) {
				final String _sort = sortBypro;
				Collections.sort(list, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						String name1 = o1.get(_sort).toString();
						String name2 = o2.get(_sort).toString();
						return name1.compareTo(name2);
					}
				});
			}
		} catch (Exception e) {
			log.error("************************ftp错误",e);
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 上传文件 FTP目录 /ftpDir/
	 * 
	 * @param ftpDir
	 * @param localFile
	 * @return
	 */
	public boolean pushFile(String ftpDir, String localFile) {
		if (ftpDir == null || "".equals(ftpDir)) {
			return false;
		}
		if (!this.conn.hasConnected()) {
			return false;
		}
		FileInputStream fs = null;
		try {
			if (this.conn.xftp.equals(Connection.xftpChannel.SFTP)) {
				if (!markDirs(ftpDir)) {
					return false;
				}
				File f = new File(localFile);
				if (!f.exists()) {
					return false;
				}
				fs = new FileInputStream(localFile);
				this.conn.sftp.put(fs, f.getName());
			} else if (this.conn.xftp.equals(Connection.xftpChannel.FTP)) {
				if (!markDirs(ftpDir)) {
					return false;
				}
				File f = new File(localFile);
				if (!f.exists()) {
					return false;
				}
				this.conn.ftp.setFileType(FTP.BINARY_FILE_TYPE);
				fs = new FileInputStream(localFile);
				if (!this.conn.ftp.storeFile(f.getName(), fs)) {
					return false;
				}
			}
			return true;

		} catch (Exception e) {
			log.error("************************ftp错误",e);
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fs != null) {
					fs.close();
					fs = null;
				}
			} catch (Exception e2) {
				log.error("************************ftp错误",e2);
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 上传文件/文件夹 FTP目录 /ftpDir/
	 * 
	 * @param ftpDir
	 * @param file
	 * @return
	 */
	public boolean pushFile(String ftpDir, File file) {
		boolean suss = true;
		try {
			if (ftpDir == null || "".equals(ftpDir)) {
				return false;
			}
			if (!this.conn.hasConnected()) {
				return false;
			}
			for (File f : file.listFiles()) {
				if (f.isDirectory()) {
					String dir = ftpDir + File.separator + f.getName();
					if (!markDirs(dir)) {
						continue;
					}
					pushFile(dir, f);
				} else if (f.isFile()) {
					if (!pushFile(ftpDir, f.getPath())) {
						suss = false;
					}
				}
			}
		} catch (Exception e) {
			log.error("************************ftp错误",e);
			e.printStackTrace();
			return false;
		}
		return suss;
	}

	/**
	 * 创建目录
	 * 
	 * @param ftpDir
	 * @return
	 */
	public boolean markDirs(String ftpDir) {
		try {
			if (this.conn.xftp.equals(Connection.xftpChannel.SFTP)) {
				try {
					this.conn.sftp.cd(ftpDir);
				} catch (SftpException e) {
					String[] dirs = ftpDir.split("/");
					String tempPath = "";
					for (String dir : dirs) {
						if (null == dir || "".equals(dir)) {
							continue;
						}
						tempPath += "/" + dir;
						try {
							this.conn.sftp.cd(tempPath);
						} catch (SftpException ex) {
							this.conn.sftp.mkdir(tempPath);
							this.conn.sftp.cd(tempPath);
						}
					}
				}
			} else if (this.conn.xftp.equals(Connection.xftpChannel.FTP)) {
				if (!this.conn.ftp.changeWorkingDirectory(ftpDir)) {
					String[] dirs = ftpDir.split("/");
					String tempPath = "";
					for (String dir : dirs) {
						if (null == dir || "".equals(dir)) {
							continue;
						}
						tempPath += "/" + dir;
						if (!this.conn.ftp.changeWorkingDirectory(tempPath)) {
							if (!this.conn.ftp.makeDirectory(tempPath)) {

								return false;
							} else {

								this.conn.ftp.changeWorkingDirectory(tempPath);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			log.error("************************ftp错误",e);
			return false;
		}
		return true;
	}

	/**
	 * 删除文件 FTP目录 /ftpDir/
	 * 
	 * @param ftpDir
	 * @param ftpFileName
	 * @return
	 */
	public boolean removeFile(String ftpDir, String ftpFileName) {
		if (ftpDir == null || "".equals(ftpDir)) {
			return false;
		}
		if (!this.conn.hasConnected()) {
			return false;
		}
		try {
			if (this.conn.xftp.equals(Connection.xftpChannel.SFTP)) {
				this.conn.sftp.cd(ftpDir);
				this.conn.sftp.rm(ftpFileName);

			} else if (this.conn.xftp.equals(Connection.xftpChannel.FTP)) {
				this.conn.ftp.changeWorkingDirectory(ftpDir);
				this.conn.ftp.deleteFile(ftpFileName);
			}
			return true;
		} catch (Exception e) {
			log.error("************************ftp错误",e);
			return false;
		}
	}

	/**
	 * 删除文件夹 FTP目录 /ftpDir/
	 * 
	 * @param ftpDir
	 * @param ftpDirName
	 * @return
	 */
	public boolean removeDir(String ftpDir, String ftpDirName) {
		if (ftpDir == null || "".equals(ftpDir)) {
			return false;
		}
		if (!this.conn.hasConnected()) {
			return false;
		}
		try {
			if (this.conn.xftp.equals(Connection.xftpChannel.SFTP)) {
				this.conn.sftp.cd(ftpDir);
				this.conn.sftp.rmdir(ftpDirName);
			} else if (this.conn.xftp.equals(Connection.xftpChannel.FTP)) {
				this.conn.ftp.changeWorkingDirectory(ftpDir);
				this.conn.ftp.removeDirectory(ftpDirName);
			}
			return true;
		} catch (Exception e) {
			log.error("************************ftp错误",e);
			return false;
		}
	}

	/**
	 * 是否连接
	 * 
	 * @return
	 */
	public boolean hasConnected() {
		if (this.conn != null) {
			return this.conn.hasConnected();
		}
		return false;
	}

	// 开始于(filter*)、结束于(*filter)、无限制(filter)、中间(*filter*)
	private boolean Filter(String F, String f) {
		if (f == null || f.equals("")) {
			return true;
		}
		String[] farr;
		String f1 = F.toLowerCase();
		String f2 = "";
		Integer idx = 0;
		if (f.indexOf("|") > 0) {
			farr = f.split("|");
			for (String _f : farr) {
				f2 = _f.toLowerCase().replace("*", "");
				if (_f.endsWith("*")) {
					if (f1.endsWith(f2)) {
						return true;
					}
				} else if (_f.startsWith("*")) {
					if (f1.startsWith(f2)) {
						return true;
					}
				} else if (_f.startsWith("*") && _f.endsWith("*")) {
					idx = f1.indexOf(f2);
					if (idx > 0 && !f1.endsWith(f2)) {
						return true;
					}
				} else {
					if (f1.indexOf(f2) >= 0) {
						return true;
					}
				}
			}
			return false;
		} else {
			farr = f.split("&");
			for (String _f : farr) {
				f2 = _f.toLowerCase().replace("*", "");
				if (_f.endsWith("*")) {
					if (!f1.startsWith(f2)) {
						return false;
					}
				} else if (_f.startsWith("*")) {
					if (!f1.endsWith(f2)) {
						return false;
					}
				} else if (_f.startsWith("*") && _f.endsWith("*")) {
					idx = f1.indexOf(f2);
					if (idx <= 0 || f1.endsWith(f2)) {
						return false;
					}
				} else {
					if (f1.indexOf(f2) < 0) {
						return false;
					}
				}
			}
			return true;
		}
	}

	/*
	 * 将时间戳转换为时间
	 */
	private String stampToDate(String s) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		Date date = sdf.parse(s);
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf1.format(date);
	}

}
