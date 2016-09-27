package com.github.lzyzsd.jsbridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Tool {

	/*public static String getMd5ByFile(File file) {
        String value = null;
		FileInputStream in = null;
		MappedByteBuffer byteBuffer = null;
		try {
			in = new FileInputStream(file);
			byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest md5 = MessageDigest.getInstance("MD5Tool");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.getChannel().close();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}

			if (null != byteBuffer) {
				byteBuffer.clear();
				byteBuffer = null;
			}
		}
		return value;
	}*/

    public static String getMd5ByFile(File file) {
        FileInputStream fis = null;
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5Tool");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            BigInteger bi = new BigInteger(1, md5.digest());
            return bi.toString(16);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }

                if (md5 != null) {
                    md5.reset();
                    md5 = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String toMd5(String md5Str) {
        String result = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(md5Str.getBytes("utf-8"));
            result = toHexString(md5.digest());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (int b : bytes) {
            if (b < 0)
                b += 256;
            if (b < 16)
                hexString.append("0");
            hexString.append(Integer.toHexString(b));
        }
        return hexString.toString();
    }

    public final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
}
