package com.inspur.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 压缩与解压文件功能
 * (由于项目用到模块为单一用户单一模块使用出于性能暂不做安全同步处理，如果多个模块或者多用户用到强烈建议各个方法加上synchronized否则易数据错读或者IO异常）
 * @description 对指定的文件或者文件夹压缩或者解压
 * @author 陈磊兴
 * @date 2015-06-11
 * @version 1.0
 */
public class ZipUtils {
	
	/**生成ZIP文件时缓冲区大小(字节)**/
	private static final int BUFFERED_SIZE = 1024;
	
	/**
	 * 压缩指定文件或者文件夹-根据文件或文件夹路径以及将要生成的压缩文件名称,默认采用UTF-8编码方式
	 * 
	 * @description 压缩指定文件或文件夹
	 * @param filePath:被压缩的文件或者文件夹的路径（绝对路径）
	 * @param zipFileName:生成压缩文件的名称(含绝对路径和扩展名".zip")
	 * @return boolean:是否创建成功
	 */
	public static boolean zip(String filePath,String zipFileName){
		return zip(filePath,zipFileName,"UTF-8");
	}
	
	/**
	 * 压缩指定文件或者文件夹-根据文件或文件夹路径以及将要生成的压缩文件名称
	 * 
	 * @description 压缩指定文件或文件夹
	 * @param filePath:被压缩的文件或者文件夹的路径（绝对路径）
	 * @param zipFileName:生成压缩文件的名称(含绝对路径和扩展名".zip")
	 * @param charset:压缩文件的编码方式
	 * @return boolean:是否创建成功
	 */
	public static boolean zip(String filePath,String zipFileName,String charset){
		boolean isSuccess=true;//是否创建成功
		//boolean isMkdir=true;//是否成功创建目录
		
		/*创建IO文件,调用方法压缩子文件*/
		File inputFile=new File(filePath);
		if(inputFile.isDirectory()){//目录如果不存在
			if(!inputFile.exists()){
				isSuccess=inputFile.mkdirs();
			}
		}
		FileOutputStream outZipStream=null;
		ZipOutputStream outZip=null;
		try {
			outZipStream = new FileOutputStream(zipFileName);
			outZip = new ZipOutputStream(outZipStream);
			outZip.setEncoding(charset);//设置编码方式
			doZip(inputFile,outZip,"");//递归遍历压缩各个子文件
		} catch (Exception e) {
			//e.printStackTrace();
			isSuccess=false;
		}finally{
			if(outZip!=null){//关闭IO流
				try {
					outZip.close();
				} catch (IOException e) {
					isSuccess=false;
					//e.printStackTrace();
				}
			}
			if(outZipStream!=null){//关闭IO流
				try {
					outZipStream.close();
				} catch (IOException e) {
					isSuccess=false;
					//e.printStackTrace();
				}
			}
		}
		return isSuccess;
	}
	
	/**
	 * 根据file和zip输出字节流递归压缩各个子文件
	 * 
	 * @description 递归压缩各个子文件
	 * @param inputFile:被压缩的文件或者文件夹（绝对路径）的File对象
	 * @param outZip:生成压缩文件的(含绝对路径和扩展名".zip")字节输出流
	 * @param base:被压缩文件或者文件夹的路径(压缩文件第一级为多级如“/file/xx.jpg”)，用于自身递归调用传值
	 * @throws IOException
	 */
	private static void doZip(File inputFile,ZipOutputStream outZip,String base) throws IOException{
		if (inputFile.isDirectory()) {//列出所有当前目录下的子文件夹
	        File[] inputFiles = inputFile.listFiles();
	        outZip.putNextEntry(new ZipEntry(base + "/"));
	        base = base.length() == 0 ? "" : base + "/";
	        for (int i = 0; i < inputFiles.length; i++) {//根据各个子文件夹递归调用
	        	//String fileName=new String(inputFiles[i].getName().getBytes("GBK"),"UTF-8");//文件名转码防止乱码
	        	doZip(inputFiles[i], outZip, base + inputFiles[i].getName());
	        }
        } else {//如果是文件，结束递归，创建文件
            if (base.length() > 0) {
            	outZip.putNextEntry(new ZipEntry(base));
            } else {
            	outZip.putNextEntry(new ZipEntry(inputFile.getName()));
            }
            FileInputStream in = new FileInputStream(inputFile);
            try {//通过字节流把文件写入zip文件中
                int c;
                byte[] by = new byte[BUFFERED_SIZE];
                while ((c = in.read(by)) != -1) {
                	outZip.write(by, 0, c);
                }
            } catch (IOException e) {
                throw e;
            } finally {
                in.close();
            }
        }
	}
}
