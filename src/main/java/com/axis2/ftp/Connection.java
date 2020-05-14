package com.axis2.ftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.util.Properties;

/**
* Description
* @Author junwei
* @Date 10:25 2019/10/22
**/
public  class Connection {
	private String host;
	private String username;
	private String password;
	private Integer port;
	protected xftpChannel xftp;
	protected ChannelSftp sftp;
	protected FTPClient ftp;

	protected Connection setConnections(XFtp xFtp) {
		this.host = xFtp.host;
		this.username = xFtp.username;
		this.password = xFtp.password;
		this.port = xFtp.port;
		return this;
	}

	protected   Connection()  {
		 
	}
	
	/**
	 * 选择通道
	 * 
	 * @param xftpChannel
	 */
	public void openxFtpChannel(xftpChannel xftpChannel) {
		this.xftp = xftpChannel;
	}

	/**
	 * 登录
	 * 
	 * @return
	 */
	public boolean connectServer() {
		sftp = null;
		ftp = null;
		boolean hasconn = false;
		try {
			if (xftp.equals(xftpChannel.SFTP)) {
				JSch jsch = new JSch();

				Session session = jsch.getSession(this.username, this.host,
						this.port);

				if (password != null) {
					session.setPassword(password);
				}
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");

				session.setConfig(config);
				session.connect();

				Channel channel = session.openChannel("sftp");
				channel.connect();
				sftp = (ChannelSftp) channel;
				hasconn = sftp.isConnected();

			} else if (xftp.equals(xftpChannel.FTP)) {
				ftp = new FTPClient();
				ftp.connect(host, port);
				hasconn=ftp.login(username, password);
				if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
					ftp.disconnect();
					hasconn = false;
				}
			}
			return hasconn;

		} catch (Exception e) {
			e.printStackTrace();
			sftp = null;
			ftp = null;
			return false;
		} finally {
			if (!hasconn) {
				ftp = null;
				sftp = null;
			}
		}
	}

	/**
	 * 断开
	 */
	public void disConnectServer() {
		try {
			if (this.sftp != null) {
				this.sftp.disconnect();

			} else if (this.ftp != null) {
				this.ftp.logout();
			}
			this.sftp=null;
			this.ftp=null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean hasConnected() {
		return (this.sftp != null) || (this.ftp != null);
	}

	public enum xftpChannel {
		FTP, SFTP
	}
}
