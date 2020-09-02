package com.axis2.util;

import com.axis2.impl.EomsServiceImpl;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

/**
 * 获取文件的相关信息
 * 
 * @author Chan
 * 
 */

public class UtilFile {

	private static final Logger log = Logger.getLogger(EomsServiceImpl.class.getClass());

	/**
	 * 获取文件名 D:/testdata/201710091030.zip ==>> 201710091030.zip
	 * 
	 * @param fName
	 * @return
	 */
	public static String getFileName(String fName) {
		String _fName = fName.replaceAll("\\\\", "/");
		return _fName.substring(_fName.lastIndexOf("/") + 1);
	}

	/**
	 * 获取文件名，不加后缀 D:/testdata/201710091030.zip ==>> 201710091030
	 * 
	 * @param fName
	 * @return
	 */
	public static String getFileNameNoSuffix(String fName) {
		String _fName = fName.replaceAll("\\\\", "/");
		return _fName.substring(_fName.lastIndexOf("/") + 1,
				_fName.indexOf("."));
	}

	/**
	 * 获取文件名，不加后缀 D:/testdata/201710091030.zip ==>> 201710091030
	 * 
	 * @param fName
	 * @return
	 */
	public static String getFileNameNoLastSuffix(String fName) {
		String _fName = fName.replaceAll("\\\\", "/");
		return _fName.substring(_fName.lastIndexOf("/") + 1,
				_fName.lastIndexOf("."));
	}

	/**
	 * 获取文件路径 D:/testdata/201710091030.zip ==>> D:/testdata
	 * 
	 * @param fName
	 * @return
	 */
	public static String getFilePath(String fName) {
		String _fName = fName.replaceAll("\\\\", "/");
		return _fName.substring(0, _fName.lastIndexOf("/"));
	}

	/**
	 * 获取文件路径 D:/testdata/201710091030.zip ==>> D:/testdata/201710091030
	 * 
	 * @param fName
	 * @return
	 */
	public static String getFilePathAndName(String fName) {
		String _fName = fName.replaceAll("\\\\", "/");
		return _fName.substring(0, _fName.lastIndexOf("."));
	}

	public static String getNewFilePath(String fName) {
		return fName.replaceAll("\\\\", "/");
	}

	public static void mkFolder(String fileName) {
		File f = new File(fileName);
		if (!f.exists()) {
			f.mkdir();
		}
		if (f != null)
			f = null;
	}

	public static File mkFile(String fileName) {
		String newFilePath = UtilFile.getNewFilePath(fileName);
		String path = UtilFile.getFilePath(newFilePath);
		mkFolder(path);
		File f = new File(newFilePath);
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

	// 文件转移
	public static boolean CopySingleFile(String oldPathFile, String newPathFile) {
		UtilFile.mkFolder(newPathFile);
		boolean isOk = true;
		File oldfile = null;
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			oldfile = new File(oldPathFile);
			if (oldfile.exists()) { // 文件存在时
				inStream = new FileInputStream(oldPathFile); // 读入原文件
				fs = new FileOutputStream(newPathFile);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
			}
		} catch (Exception e) {
			isOk = false;
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
					inStream = null;
				}
				if (fs != null) {
					fs.close();
					fs = null;
				}
				if (oldfile != null) {
					oldfile = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return isOk;
	}

	/**
	 * 删除某个文件夹下的所有文件,不包含目录
	 * 
	 * @param delpath
	 *            String
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return boolean
	 */
	public static boolean deletefile(String delpath) {
		try {

			File file = new File(getNewFilePath(delpath));
			// 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					String fileName=getNewFilePath(delpath + "\\"
							+ filelist[i]);
					File delfile = new File(fileName);
					if (!delfile.isDirectory()) {
						boolean res=delfile.delete();
						if(!res){
							log.info(fileName+"\n删除失败");
						}else{
							log.error(fileName+"\n删除成功");
						}
					} else if (delfile.isDirectory()) {
						deletefile(getNewFilePath(delpath + "\\" + filelist[i]));
					}
				}

			}

		} catch (Exception e) {
			log.error("文件删除失败：",e);
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 删除某个文件夹下的所有文件夹和文件,包含本目录
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	/**
	 * 输出某个文件夹下的所有文件夹和文件路径
	 * 
	 * @param filepath
	 *            String
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return boolean
	 */
	public static boolean readfile(String filepath) {
		try {

			File file = new File(filepath);
			// System.out.println("遍历的路径为：" + file.getAbsolutePath());
			// 当且仅当此抽象路径名表示的文件存在且 是一个目录时（即文件夹下有子文件时），返回 true
			if (!file.isDirectory()) {
				// System.out.println("该文件的绝对路径：" + file.getAbsolutePath());
				// System.out.println("名称：" + file.getName());
			} else if (file.isDirectory()) {
				// 得到目录中的文件和目录
				String[] filelist = file.list();
				if (filelist.length == 0) {
					// System.out.println(file.getAbsolutePath()
					// + "文件夹下，没有子文件夹或文件");
				} else {
					// System.out
					// .println(file.getAbsolutePath() + "文件夹下，有子文件夹或文件");
				}
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(getNewFilePath(filepath + "\\"
							+ filelist[i]));
					// System.out.println("遍历的路径为：" +
					// readfile.getAbsolutePath());
					if (!readfile.isDirectory()) {
						// System.out.println("该文件的路径："
						// + readfile.getAbsolutePath());
						// System.out.println("名称：" + readfile.getName());
					} else if (readfile.isDirectory()) {
						// System.out.println("-----------递归循环-----------");
						readfile(getNewFilePath(filepath + "\\" + filelist[i]));
					}
				}
			}
		} catch (Exception e) {
			// System.out.println("readfile() Exception:" + e.getMessage());
		}
		return true;
	}

	/**
	 * 获取文件名
	 * 
	 * @param path
	 * @param fileList
	 */
	public static void getFileList(String path, ArrayList<String> fileList) {
		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				return;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						getFileList(file2.getAbsolutePath(), fileList);
					} else {
						fileList.add(file2.getPath());
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {

		// "C:\\Users\\Administrator\\Desktop\\mrs\\TD-LTE_MRS_ZTE_OMC1_36888_20160705121500.zip"
		// "D:/testdata/201710091030.zip"
		ArrayList<String> files = new ArrayList<String>();
		getFileList("D:\\ftptest\\mdttarFile\\20171009\\201710091045", files);

		System.out.println(files.size());
		// deletefile("D:\\testdata\\201710091030");
		// System.out
		// .println(FileHelper
		// .getFileName("C:\\Users\\Administrator\\Desktop\\mrs\\TD-LTE_MRS_ZTE_OMC1_36888_20160705121500.zip"));
		// System.out
		// .println(FileHelper
		// .getFileNameNoSuffix("C:\\Users\\Administrator\\Desktop\\mrs\\TD-LTE_MRS_ZTE_OMC1_36888_20160705121500.zip"));
		// System.out
		// .println(FileHelper
		// .getFilePath("C:\\Users\\Administrator\\Desktop\\mrs\\TD-LTE_MRS_ZTE_OMC1_36888_20160705121500.zip"));
		// System.out
		// .println(FileHelper
		// .getFilePathAndName("C:\\Users\\Administrator\\Desktop\\mrs\\TD-LTE_MRS_ZTE_OMC1_36888_20160705121500.zip"));

	}
}
