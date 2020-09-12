package com.blackboard.classin.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.blackboard.classin.entity.ClassinClassVideo;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;



public class FileUtil {
	
	// 定制Verifier
	public class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
    
    /**
	 * 从网络Url中下载文件
	 * @param urlStr
	 * @param fileName
	 * @param savePath
	 * @throws IOException
	 */
	public static void  downLoadFromUrl(String urlStr,String fileName,HttpServletResponse response){
		try {
			InputStream in = getInputStreamFromUrl(urlStr);
			OutputStream out = response.getOutputStream();
			
			fileName = new String(fileName.getBytes("utf-8"),"ISO8859-1");
			response.reset();
			response.setContentType("application/octet-stream");
			response.setHeader("content-disposition", "attachment;filename=" + fileName );
			 //创建缓冲区
	        byte buffer[] = new byte[1024];
	        int len = 0;
	        //循环将输入流中的内容读取到缓冲区当中
	        while((len=in.read(buffer))>0){
	            //输出缓冲区的内容到浏览器，实现文件下载
	            out.write(buffer, 0, len);
	        }
	        //关闭文件输入流
	        in.close();
	        //关闭输出流
	        out.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}
 
	/**
	 * 从输入流中获取字节数组
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static  byte[] readInputStream(InputStream inputStream) throws IOException {  
		byte[] buffer = new byte[1024];  
		int len = 0;  
		ByteArrayOutputStream bos = new ByteArrayOutputStream();  
		while((len = inputStream.read(buffer)) != -1) {  
			bos.write(buffer, 0, len);  
		}  
		bos.close();  
		return bos.toByteArray();  
	}  
 
	public static void batchDownLoadFile(HttpServletRequest request,HttpServletResponse response,List<ClassinClassVideo> videoList,String courseName) throws IOException{
		InputStream is = null;
		ZipOutputStream zos = null;
		try {
			String outputFileName = courseName + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".zip";
			response.reset();
			response.setContentType("application/octet-stream");
			response.setHeader("content-disposition", "attachment;filename=" + new String(outputFileName.getBytes("utf-8"),"iso-8859-1") );
			
			zos = new ZipOutputStream(response.getOutputStream());
			byte[] buffer = new byte[1024];
			int i = 0;
			for (ClassinClassVideo video : videoList) {
				i++;
				//设置zip里面每个文件的名称
				String fileName = courseName + "_视频" + i + ".mp4";
			    zos.putNextEntry(new ZipEntry(fileName));
			    //根据文件地址获取输入流
			    is = new URL(video.getvURL()).openConnection().getInputStream();
			    int length;
			    while ((length = is.read(buffer)) > 0) {
			        zos.write(buffer, 0, length);
			    }
			    zos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(zos != null) {
				zos.closeEntry();
				zos.close();
			}
			if(is != null) {
				is.close();
			}
			
		}
//        for (ClassinClassVideo video : videoList) {
//        	String fileName = courseName + video.getFileId() + ".mp4";
//        	fileName = new String(fileName.getBytes(),"iso-8859-1");
//            InputStream inputStream = getInputStreamFromUrl(video.getvURL());
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            int len;
//            while ((len = inputStream.read(buffer)) != -1) {
//                baos.write(buffer, 0, len);
//            }
//            if (baos != null) {
//                baos.flush();
//            }
//            byte[] bytes = baos.toByteArray();
//
//            //设置文件名
//            ArchiveEntry entry = new ZipArchiveEntry(fileName);
//            zous.putArchiveEntry(entry);
//            zous.write(bytes);
//            zous.closeArchiveEntry();
//            if (baos != null) {
//                baos.close();
//            }
//            if(inputStream != null) {
//            	inputStream.close();
//            }
//        }
//        if(zous!=null) {
//            zous.close();
//        }
//        if(out != null) {
//        	out.flush();
//        	out.close();
//        }
	}
	
	/**
     * 通过网络地址获取文件InputStream
     *
     * @param path 地址
     * @return
     */
    public static InputStream getInputStreamFromUrl(String path) {
        URL url = null;
        InputStream is = null;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            //设置超时间为3秒
			conn.setConnectTimeout(5*1000);
			//防止屏蔽程序抓取而返回403错误
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			conn.setHostnameVerifier(new FileUtil().new TrustAnyHostnameVerifier());
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }
}
