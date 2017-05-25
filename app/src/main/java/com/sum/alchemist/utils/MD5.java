package com.sum.alchemist.utils;

import com.sum.xlog.core.XLog;

import org.xutils.db.common.IOUtil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * 获取文件MD5值
 * @version v1.0
 */
public class MD5 {
	private static final String TAG = "MD5";
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * 获取文件的MD5值
	 * @param filename 文件名
	 * @return MD5串
	 */
	public static String getFileMD5(String filename) {
		InputStream fis = null;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		try {
			fis = new FileInputStream(filename);
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			return toHexString(md5.digest());
		} catch (Exception e) {
			XLog.e(TAG, "==========文件异常", e);
			return "";
		}finally{
			IOUtil.closeQuietly(fis);
			buffer = null;
			System.gc();
		}
	}

	public static String getStringMD5(String string) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(string.getBytes());
			return toHexString(md5.digest());
		} catch (Exception e) {
			XLog.e(TAG, "=== MD5加密异常 ===", e);
			return "";
		}
	}
}
