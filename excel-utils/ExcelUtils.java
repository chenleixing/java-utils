package com.inspur.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSONObject;

/**
 * excel文件上传、读取、文件删除、相关属性的设置等(各个属性值设置暂未做非法的判断和处理)
 * 
 * @description 私有方法为内容专属调用，公共方法可外部调用
 * @description 为个性化处理可设置开始读取的行号及是否启用调试模式等，所有方法及属性为非static,使用前需创建此类的实例
 * @author 陈磊兴
 * @date 2015-05-06
 * @version 1.3
 */
public final class ExcelUtils{
	/**私有属性，文件上传的项目根路径下的路径,默认为"assets/data/"，可通过get获取和set预先设置*/
	private String dir="assets/data/";
	
	/**私有属性，读取excel文件的开始行标，默认从第2行，跳过一行标题行可通过get获取和set预先设置*/
	private int startRowNum=2;
	
	/**私有属性，读取excel文件的开始列标，默认从第1列，可通过get获取和set预先设置*/
	private int startColNum=1;
	
	/**私有属性，读取excel数据时是否预先生成主键(随机32位)作为行数据list中第一个元素，默认false，可通过get获取和set预先设置*/
	private boolean isCreateKey=false;
	
	/**私有属性，是否开启调试模式（控制台打印报错信息和相关数据）默认关闭，主要针对excel读取功能，可通过get获取和set预先设置*/
	private boolean isDebug=false;
	
	/**
	 * 把excel文件上传到本地，单文件上传:利用spring自带上传功能CommonsMultipartResolver实现上传
	 * 
	 * @param request 客户端的请求
	 * @return JSONObject
	 * JSONObject相关信息:[{
	 * isSuccess:是否成功上传true或false
	 * isMadeDir:上传过程中创建文件目录是否成功true或false
	 * path:上传到本地后的文件绝对路径，上传失败为空""
	 * name:文件的名称包含扩展名，上传失败为空""
	 * }]
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public JSONObject uploadExcel(HttpServletRequest request){
		ServletContext sc=request.getSession().getServletContext();//servelt上下文
		String rootPath=sc.getRealPath("/").replace("\\","/");//项目的绝对路径
		
		boolean isMkdir=true;//是否成功创建目录
		boolean isSuccess=true;//是否上传成功
		JSONObject excelInfo = new JSONObject();//上传excel文件的信息，方法返回值
		
		/*上传文件*/
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(sc);   
		if (multipartResolver.isMultipart(request)) {//其实只有单个文件
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;  
            List<MultipartFile> iter = multiRequest.getFiles("files[]");//前台$.fileUpload上传组件文件表单
            try{
            	for(MultipartFile file:iter) {//其实只有单个文件
	    			String fileName = file.getOriginalFilename();//原文件名称
	    			File directory = new File(rootPath+this.dir);
	    			if(!directory.exists()){
	    				isMkdir=directory.mkdirs();//创建目录
	    			}
	    			FileCopyUtils.copy(file.getBytes(), new FileOutputStream(rootPath +this.dir+ fileName));
	    			//String downPath=dir+fileName;客户端下载路径
	    			//downPath=downPath.replace("assets", "static");//显示层路径"assets"需通过"static"才能访问到
	    			excelInfo.put("path", rootPath+this.dir+fileName);
	    			excelInfo.put("name", fileName);
            	}
            }catch(Exception e){
            	isSuccess=false;
            	excelInfo.put("path","");//异常捕获为excelInfo存入数据之前，故需异常中再处理
    			excelInfo.put("name","");//异常捕获为excelInfo存入数据之前，故需异常中再处理
            }
        }
		excelInfo.put("isSuccess",isSuccess);
		excelInfo.put("isMadeDir",isMkdir);
		
		return excelInfo; 
	}
	
	/**
	 * 删除本地文件或者文件夹（所有子目录及包含的子文件都会被删除）
	 * 
	 * @description:删除文件或者文件夹（包含的所有子文件和子目录也全被递归删除）
	 * @param filePath:文件或者文件夹的绝对路径
	 * @return boolean:是否删除成功false或true
	 */
	public static boolean deleteFile(String filePath){
		boolean isDelete=true;//是否删除成功
		
		File file = new File(filePath);
		if(file.exists()){//删除文件或者文件夹（包括各个子目录和子文件）
			if(file.isDirectory()){
				try {
					FileUtils.deleteDirectory(file);//递归删除子文件夹和子目录
				} catch (IOException e) {
					isDelete=false;
					//e.printStackTrace();
				}
			}else{
				isDelete=file.delete();
			}
		}else{
			isDelete=false;
		}
		return isDelete;
	}

	/**
	 * excel文件读取内容-从excel第startRowNum行开始读取，默认跳过标题一行开始读取
	 * 
	 * @description 以List<List<String>>返回所有数据,方面事务控制与批量插入、代码重用等
	 * @param filePath excel文件的绝对路径
	 * @return List<List<String>>，包括所有excel数据，如读取异常或空文件则返回长度为0且大小不可变List<List<>>:
	 * 外层List<List<>>为所有行的数据，里层List<String>为每行的所有数据
	 * 各个List的数据顺序依次为excel表格数据顺序
	 */
	public List<List<String>> parseExcel(String filePath){
        /*初始化excel表格*/
		Sheet sheet=null;
		try {
			InputStream is = new FileInputStream(filePath);
		    POIFSFileSystem fs=null;
		    Workbook wb=null;
		    if(!this.isExcel2007(filePath)){//是否为2007版
				fs = new POIFSFileSystem(is);
			   wb = new HSSFWorkbook(fs);
		    }else{
			   wb = new XSSFWorkbook(is);
		    }
		    sheet = wb.getSheetAt(0);
		}catch (IOException e) {
			if(this.isDebug){//方便调试和发现问题
				System.out.println("初始化excel表格失败！");
				e.printStackTrace();
			}
			return Collections.emptyList();//返回长度为0大小不可变list
		}
                 
		/* 得到总行数 */
        int rowNum = sheet.getLastRowNum();
        Row row = sheet.getRow(0);
        int colNum = row.getPhysicalNumberOfCells();
       
        /*遍历各行，读取数据存入List中*/
        List<List<String>> list=new ArrayList<List<String>>(rowNum-this.startRowNum+2);//创建ArrayList，初始化大小为rowNum-this.startRowNum+1
        String cellValue=null;
        try{
        	for (int i =this.startRowNum-1; i <= rowNum; i++) {//按行遍历
	            row = sheet.getRow(i);
	            int j =this.startColNum-1;
	            List<String> rowList=null;
	            if(this.isCreateKey){//创建每行ArrayList，根据是否自动创建主键初始化大小
	            	rowList=new ArrayList<String>(colNum-this.startColNum);
	            	String id=UUID.randomUUID().toString().replaceAll("-", "");//得到32位主键
	            	rowList.add(id);
	            }else{
	            	rowList=new ArrayList<String>(colNum-this.startColNum-1);
	            }
	            
	            while (j<colNum){//遍历当前行的每列
	            	cellValue=this.getStringCellValue(row.getCell((short) j));
	            	cellValue=cellValue.trim();
	            	rowList.add(cellValue);
	                j++;
	            }
	            list.add(rowList);//添加一行数据
        	}
		}catch (Exception e) {
			if(this.isDebug){
				/*方便调试,此处经常出错*/
				System.out.println("excel表格中有非法格式内容，读取发生异常！");
				int index=0;
				for(List<String> temp:list){
					index++;
					if(temp==null){
						System.out.println("错误数据出现内容行第在:"+index+"行！");
					}
				}
				for(List<String> temp:list){
					System.out.println("异常发生时读取到的所有数据有:"+temp.toString());
				}
				e.printStackTrace();
			}
			return Collections.emptyList();//返回长度为0大小不可变list
		}
        return list;
    }  
		
    /** 
     * 私有方法,是否是2007的excel，返回true是2007
     * 
     * @param filePath文件完整路径 
     * @return boolean 
     */  
    private boolean isExcel2007(String filePath){
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }
    
	/**
	 * 私有方法，根据cell获取单元格数据内容返回字符串类型的数据
	 * 
	 * @param cell Excel单元格类型
	 * @return String 单元格数据内容，字符串类型
	 */
    private String getStringCellValue(Cell cell) {
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//日期处理格式化
        String strCell = "";
        switch (cell.getCellType()) {
	        case HSSFCell.CELL_TYPE_STRING:
	            strCell = cell.getStringCellValue();
	            break;
	        case HSSFCell.CELL_TYPE_NUMERIC:
	        	if(HSSFDateUtil.isCellDateFormatted(cell)){
	        		strCell = sdf.format(cell.getDateCellValue());
	            }else{
	            	strCell = String.valueOf(cell.getNumericCellValue());
	            }
	        	break;
	        default:strCell="";//excel中文本格式为空处理情况
        } 
        return strCell;
    }
    
    /**
	 * 获取excel文件的存放目录
	 * 
	 * @return String
	 */
    public String getDir() {
		return this.dir;
	}

    /**
	 * 可预先设置excel存放目录
	 */
	public void setDir(String dir) {
		this.dir = dir;
	}

	/**
	 * 获取从第几行开始读取excel数据默认为2(跳过一行标题)
	 * 
	 * @return int
	 */
	public int getStartRowNum() {
		return this.startRowNum;
	}

	/**
	 * 可预先设置从第几行开始读取excel数据默认为2(跳过一行标题)
	 */
	public void setStartRowNum(int startRowNum) {
		this.startRowNum = startRowNum;
	}

	/**
	 * 返回调试模式是否开启（控制台打印报错信息和相关数据）默认关闭，主要针对excel读取功能
	 * 
	 * @return boolean true或false
	 */
	public boolean getIsDebug() {
		return this.isDebug;
	}

	/**
	 * 可预先设置调试模式是否开启（控制台打印报错信息和相关数据，方面调试）默认关闭，主要针对excel读取功能
	 */
	public void setIsDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}
	
	/**
	 * 返回是否启动了自动生成主键
	 * 
	 * @return boolean true或false
	 */
	public boolean getIsCreateKey() {
		return this.isCreateKey;
	}
	
	/**
	 * 可预先设置是否启用自动生成主键(行数据list中首位元素)默认false
	 */
	public void seIsCreateKey(boolean isCreateKey) {
		this.isCreateKey = isCreateKey;
	}
	
	/**
	 * 可预先设置从第几列开始读取excel数据默认从1
	 */
	public void setStartColNum(int startColNum) {
		this.startColNum = startColNum;
	}
	
	/**
	 * 获取从第几列开始读取excel数据默认为1
	 * 
	 * @return int
	 */
	public int getStartColNum() {
		return startColNum;
	}
	
	/**
	 * 创建excel文件-根据tileList，dataList在本地创建excel文件
	 * 
	 * @param request:客户端的请求，为获取项目的绝对路径
	 * @param titleList:excel文件各个标题组成的列表
	 *  @param dataList:excel文件中各个行的数据List<List<String>>数据形式
	 * @param excelFileName:excel文件名称包括扩展名
	 * @return JSONObject
	 * JSONObject相关信息:{
	 * isSuccess:是否创建成功
	 * path:文件的绝对路径，创建失败为空""
	 * }
	 * @author 陈磊兴
	 * @date 2015-05-14
	 */
	public void createExcel(HttpServletRequest request,List<String> titleList,List<List<String>> dataList,String excelFileName){
		/*路径获取*/
		ServletContext sc=request.getSession().getServletContext();//servelt上下文
		String rootPath=sc.getRealPath("/").replace("\\","/");//项目的绝对路径
		String excelPath=rootPath+this.dir+excelFileName;//文件的绝对路径
		this.createExcel(titleList, dataList, excelPath);
	}
	
	/**
	 * 创建excel文件-根据tileList，dataList在本地创建excel文件
	 * 
	 * @param titleList:excel文件各个标题组成的列表
	 * @param dataList:excel文件中各个行的数据List<List<String>>数据形式
	 * @param absolutePathFileName:被创建文件的绝对路径名包括扩展名
	 * @return isSuccess:是否创建成功
	 * 
	 * @author 陈磊兴
	 * @date 2015-05-14
	 */
	public boolean createExcel(List<String> titleList,List<List<String>> dataList,String absolutePathFileName){
		boolean isSuccess=true;//是否创建成功
		
		/*防止目录不存在创建目录*/
		//boolean isMkdir=true;
		File directory = new File(absolutePathFileName.substring(0, absolutePathFileName.lastIndexOf('/')));
		if(!directory.exists()){
			isSuccess=directory.mkdirs();
			if(this.isDebug){//方便调试和发现问题
				System.out.println("文件目录创建失败！");
			}
		}
		
		/*创建excel基本信息*/
		HSSFWorkbook wb = new HSSFWorkbook();//创建一个webbook，对应一个Excel文件
		HSSFSheet sheet = wb.createSheet("导出数据");//在webbook中添加一个sheet,对应Excel文件中的sheet
		
		/*创建表头样式水平垂直居中加粗字体*/
		HSSFCellStyle titleStyle = wb.createCellStyle();
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont headerFont = (HSSFFont) wb.createFont();//创建字体样式
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//字体加粗
		//headerFont.setFontName("Times New Roman");//设置字体类型
		//headerFont.setFontHeightInPoints((short) 8);//设置字体大小
		titleStyle.setFont(headerFont);//为标题样式设置字体样式

	
		try{
			/*在sheet中添加表头到第0行,根据titleList添加各个表头和表头样式注意老版本poi对Excel的行数列数有限制*/
			HSSFRow row = sheet.createRow(0);//创建第一行，表头
			HSSFCell cell =null;
			int titleSize=titleList.size();
			for(int i=0;i<titleSize;i++){
				cell=row.createCell(i);
				cell.setCellValue(titleList.get(i));
				cell.setCellStyle(titleStyle);
			}
			
			/*根据dataList写入数据*/
			int dataSize=dataList.size();
			for(int j=0;j<dataSize;j++){
				List<String> rowList=dataList.get(j);//行数据
				row=sheet.createRow(j+1);
				for(int h=0;h<titleSize;h++){//向当前行每列写入数据
					cell=row.createCell(h);
					cell.setCellValue(rowList.get(h));
				}
			}
		}catch(Exception e){
			if(this.isDebug){//方便调试和发现问题
				System.out.println("excel表头或数据创建失败！");
				e.printStackTrace();
			}
			isSuccess=false;
		}
		
		/*将文件存到指定位,关闭IO流*/
		FileOutputStream os=null;
		try{
			os = new FileOutputStream(absolutePathFileName);
			wb.write(os);
		}catch(Exception e){
			if(this.isDebug){//方便调试和发现问题
				System.out.println("创建文件输出流失败！");
				e.printStackTrace();
			}
			isSuccess=false;
		}finally{
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					if(this.isDebug){//方便调试和发现问题
						System.out.println("关闭IO流失败！");
						e.printStackTrace();
					}
					isSuccess=false;
				}
			}
		}
		return isSuccess;
	}
}