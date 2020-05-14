package com.axis2.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
* Description 文件解压
* @Author junwei
* @Date 11:43 2019/10/23
**/
public class CompressionUtil {
	private static final Logger log = Logger.getLogger(CompressionUtil.class.getClass());
	
	public static String errorMsg="";
	/**
	 * 
	 * @param file
	 *            文件名，如： D:/testdata/201710091030.gz
	 * @param Suffix
	 *            文件解压之后的指定后缀，如： .csv
	 * @return 返回文件解压之后的文件名，如：D:/testdata/201710091030.csv
	 */
	private static String extractGzFile(String file, String Suffix) {
		// String outFloder = UtilFile.getFilePath(file);
		// String fileName = UtilFile.getFileName(file);

		String outFileName = null;
		FileInputStream fileInputStream = null;
		GZIPInputStream Zin = null;
		BufferedOutputStream out = null;
		try {
			outFileName = UtilFile.getFilePathAndName(file).concat(Suffix);
			fileInputStream = new FileInputStream(file);
			Zin = new GZIPInputStream(fileInputStream);
			// File outdir = new File(outFileName);
			int BUFFER_SIZE = 1024;
			byte[] buffer = new byte[BUFFER_SIZE];
			out = new BufferedOutputStream(new FileOutputStream(new File(
					outFileName)));
			int count = -1;
			while ((count = Zin.read(buffer)) != -1) {
				out.write(buffer, 0, count);
			}
		} catch (IOException e) {
			outFileName = null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					outFileName = null;
					e.printStackTrace();
				}
				out = null;
			}
			if (Zin != null) {
				try {
					Zin.close();
				} catch (IOException e) {
					outFileName = null;
					e.printStackTrace();
				}
				Zin = null;
			}
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					outFileName = null;
					e.printStackTrace();
				}
				fileInputStream = null;
			}

		}

		return outFileName;
	}

	/**
	 * 解压zip文件
	 * 
	 * @param sourcefile
	 *            待解压的zip文件
	 * @return 返回解压文件
	 * @throws IOException
	 */
	public static ArrayList<String> extractZipFiles(String sourcefile) {
		ArrayList<String> allfile = new ArrayList<String>();
		String descDir = UtilFile.getFilePath(sourcefile);
		File zipFile = null;
		ZipFile zip = null;
		String name = UtilFile.getFileNameNoLastSuffix(sourcefile) + "/";
		String rootDir = descDir.concat("/").concat(name);
		UtilFile.deletefile(rootDir);
		File pathFile = null;
		String outPath = "";
		try {
			zipFile = new File(sourcefile);
			zip = new ZipFile(zipFile);// ZipFile(zipFile,
										// Charset.forName("GBK"));
										// 解决中文文件夹乱码
			pathFile = new File(rootDir);
			if (!pathFile.exists()) {
				pathFile.mkdirs();
			}
			for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries
					.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String zipEntryName = entry.getName();

				String lastname = "";
				if (zipEntryName.replaceAll("\\\\", "/").lastIndexOf("/") != -1) {
					lastname = zipEntryName.replaceAll("\\\\", "/").substring(
							zipEntryName.replaceAll("\\\\", "/").lastIndexOf(
									"/") + 1);
				}
				InputStream in = zip.getInputStream(entry);
				outPath = (descDir.concat("/").concat(name)
						.concat(zipEntryName)).replaceAll("\\\\", "/");

				// 判断路径是否存在,不存在则创建文件路径
				File file = new File(outPath.substring(0,
						outPath.lastIndexOf('/')));
				if (!file.exists()) {
					file.mkdirs();
				}
				// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
				if (new File(outPath).isDirectory()) {
					continue;
				}

				FileOutputStream out = new FileOutputStream(outPath);
				byte[] buf1 = new byte[1024];
				int len;
				while ((len = in.read(buf1)) > 0) {
					out.write(buf1, 0, len);
				}
				if (in != null) {
					in.close();
					in = null;
				}
				if (out != null) {
					out.close();
					out = null;
				}
				allfile.add(outPath);
			}
		} catch (Exception e) {
			rootDir = "";
			outPath = "";
			allfile.clear();
			// allfile = null;
			return null;
		} finally {
			if (zipFile != null) {
				zipFile = null;
			}
			if (zip != null) {
				try {
					zip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				zip = null;
			}
			if (pathFile != null) {
				pathFile = null;
			}

		}
		return allfile;
	}

	/**
	 * @author YUAN 
	 * 解压ZIP大文件 
	 * @param sourcefile
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> extractBigZipFiles(String sourcefile) {
		errorMsg="";
		ZipInputStream zipis = null;
		ZipEntry fentry = null;
		FileOutputStream out = null;
		ArrayList<String> rtn = new ArrayList<>();
		byte[] doc = new byte[512];
		File f = new File(sourcefile);
		if (!sourcefile.toLowerCase().endsWith(".zip") || !f.exists()) {
			log.info("解压文件路径："+sourcefile);
			log.info("文件解压，找不到相应文件");
			return null;
		}
		String unzipath = sourcefile.substring(0, sourcefile.lastIndexOf("."));
		f = new File(unzipath);
		if (!f.exists()) {
			f.mkdirs();
		}		
		try {
			zipis = new ZipInputStream(new FileInputStream(sourcefile));
			while ((fentry = zipis.getNextEntry()) != null) {
				if (fentry.isDirectory()) {
					File dir = new File(unzipath + fentry.getName());
					if (!dir.exists()) {
						dir.mkdirs();
					}
				} else {
					String fname = unzipath + File.separator+ fentry.getName();
					out = new FileOutputStream(fname);
					doc = new byte[512];
					int n;
					while ((n = zipis.read(doc, 0, 512)) != -1) {
						out.write(doc, 0, n);
					}
					rtn.add(fname);

				}
			}
			out.close();
			zipis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			errorMsg="uncompress zip file failed:"+sourcefile+"--->"+ex.getMessage();
			log.error("************************uncompress zip file failed:"+sourcefile+"--->"+ex.getMessage());
			return null;
			
		} finally {
			try {
				if (out != null) {
					out.close();
					out = null;
				}
				if (zipis != null) {
					zipis.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			doc = null;
			fentry=null;
		}
		return rtn;
	}
	
	/**
	 * 解压GZ大文件
	 * @author YUAN 
	 * @param sourcefile
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> extractBigGZFiles(String sourcefile) {
		errorMsg = "";
		String ouputfile = "";
		FileInputStream fin = null;
		GZIPInputStream gzin = null;
		FileOutputStream fout = null;
		ArrayList<String> rtn = new ArrayList<String>();
		try {
			ouputfile=sourcefile.replace(".gz","");
			fin = new FileInputStream(sourcefile);
			gzin = new GZIPInputStream(fin);
			fout = new FileOutputStream(ouputfile);

			int num;
			byte[] buf = new byte[1024];

			while ((num = gzin.read(buf, 0, buf.length)) != -1) {
				fout.write(buf, 0, num);
			}
			rtn.add(ouputfile);
		} catch (IOException ex) {
			errorMsg = "uncompress gz file failed:" + sourcefile + "--->"
					+ouputfile+"-->" +ex.getMessage();
			log.error("************************uncompress gz file failed:" + sourcefile + "-->"
					+ouputfile+"-->" + ex.getMessage());
			return null;

		} finally {
			try {
				if (fin != null) {
					fin.close();
					fin = null;
				}
				if (gzin != null) {
					gzin.close();
					gzin = null;
				}
				if (fout != null) {
					fout.close();
					fout = null;
				}
			} catch (Exception e) {
			}
		}
		return rtn;
	}

	public static void main(String[] args) throws Exception {
		// 提取gz解压文件

		// String rstfile = extractGzFile(
		// "C:\\Users\\hadoop\\Desktop\\ScanMeasDetail_XISHUANGBANNA_201810241.gz",
		// ".csv");

	 
		// 提取zip文件
		ArrayList<String> rstfile = extractBigZipFiles("D:\\test\\20191022093947634.zip");
		for (String str : rstfile) {
			System.out.println(str);
		}

	}
}
