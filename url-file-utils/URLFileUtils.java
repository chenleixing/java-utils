package com.inspur.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 远程或网络文件读写功能(由于项目用到模块为单一用户单一模块使用出于性能暂不做安全同步处理，如果多个模块或者多用户用到强烈建议各个方法加上synchronized否则易数据错读或者IO异常）
 * 
 * @description 用于对网络文件进行读写的工具
 * @author 陈磊兴
 * @date 2015-06-11
 * @version 1.0
 */
public class URLFileUtils {
	
	/**读写文件时缓冲区大小(字节)**/
	private static final int BUFFERED_SIZE = 2024;
	
	/**
	 * 根据网络文件的URL和文件名称(含绝对路径和扩展名)创建本地文件
	 * 
	 * @description 读取远程文件在本地创建此文件
	 * @param strUrl:网络文件的地址
	 * @param fileName:文件名称(含绝对路径和扩展名)
	 * @return boolean:是否创建成功
	 */
	public static boolean createFile(String strUrl,String fileName){
		boolean isSuccess=true;//是否创建成功
		
		/*防止目录不存在创建目录*/
		//boolean isMkdir=true;
		File directory = new File(fileName.substring(0, fileName.lastIndexOf('/')));
		if(!directory.exists()){
			isSuccess=directory.mkdirs();
		}
		/*远程文件读取在本地创建*/
		DataInputStream input=null;//输入字节流 
		DataOutputStream output=null;//输出字节流
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();//获取远程对象链接
			conn.connect();//打开链接
			if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){//如果链接成功
				//int fileSize=conn.getContentLength();//可获取数据大小
				input = new DataInputStream(conn.getInputStream());//输入字节流
				output = new DataOutputStream(new FileOutputStream(fileName));//输出字节流 
				byte[] buffer = new byte[BUFFERED_SIZE];//缓冲区
				int count = 0;
				while ((count = input.read(buffer)) > 0) {
					output.write(buffer, 0, count);
				}
			}else{
				isSuccess=false;
			}
		} catch (Exception e) {
			isSuccess=false;
			//e.printStackTrace();
		}finally{
			/*关闭IO流*/
			try {
				if(output!=null){
					output.close();
				}
			} catch (IOException e) {
				isSuccess=false;
				//e.printStackTrace();
			}
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					isSuccess=false;
					//e.printStackTrace();
				}
			}
		}
		return isSuccess;
	}
}
