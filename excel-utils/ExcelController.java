
package com.inspur.supervise.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.inspur.mng.core.action.BaseController;
import com.inspur.supervise.body.service.StaffCheckService;
import com.inspur.utils.ExcelUtils;
import com.inspur.utils.URLFileUtils;
import com.inspur.utils.ZipUtils;

/**
 * excel导入功能控制器-各个模块请求对应的方法，再调用对应的service进行数据的处理
 * 
 * @author 陈磊兴
 * @version 2.5
 * @date 2015-05-06
 */
@Controller
@RequestMapping("/server/excel")
public class ExcelController  extends BaseController{
	
	/**行政执法模块service*/
	@Autowired
	private StaffCheckService staffCheckService;
	
	/**excel生成的目录(相对项目根目录)*/
	//private final String EXCELDIR="assets/data/";
	
	/**照片和excel导出生成的目录(相对项目根目录)*/
	private final static String DOWNLOAD_DIR="assets/download/";
	
	/**压缩文件生成的目录(相对项目根目录)*/
	private final static String ZIP_DIR="assets/";
	
	/**
	 * excel导入功能公共模块
	 * @description 通过利用excelUtil和反射机制,使得此模块与业务模块"几乎"完全分离,达到松耦合,模块化,代码重复利用,事务控制与excel功能分离等特点
	 * @param request:客户端请求
	 * @param response:服务器端响应
	 * @param serviceClassName:处理excel数据(一般为插入数据)service层类的名称(包括包名)如com.inspur.demo.service.DemoService
	 * @param methodName:处理excel数据的方法名(此方法有且有一个List<List<String>>类型的参数)
	 */
//	private void excelImport(HttpServletResponse response,HttpServletRequest request,String serviceClassName,String methodName){
//		/*获取类的绝对路径*/
//		ServletContext sc=request.getSession().getServletContext();//servelt上下文
//		String rootPath=sc.getRealPath("/").replace("\\","/");//项目的绝对路径
//		//String packageName=serviceClassName.substring(0,serviceClassName.lastIndexOf('.'));//包名
//		//String classPath=rootPath+"WEB-INF/classes/"+packageName.replace('.','/');//类的绝对路径
//		String classPath=rootPath+"WEB-INF/classes/";//类的绝对路径,不包括包路径
//		
//		boolean isSuccess=true;//是否导入excel文件
//		
//		/*创建excel操作工具类(方便个性化操作，所有属性及方法都为非static，使用前需创建实例)*/
//		ExcelUtils excelUtils=new ExcelUtils();
//		excelUtils.setDir(this.EXCELDIR);//可设置本地生成的excel目录，默认为assert/data/
//        excelUtils.setIsDebug(true);//可开启调试模式，确认无误可关闭默认关闭
//        excelUtils.setStartRowNum(2);//可设置excel文件从第2行就开始读取,默认第2行(跳过一行标题)
//        excelUtils.seIsCreateKey(true);//可启动读出的excel数据中自动跟上32位主键默认false
//        JSONObject fileInfo=excelUtils.uploadExcel(request);//上传excel文件返回文件信息
//        
//        /*解析excel文件，开启调试模式后相关环节如发生异常或打印相关数据和错误提示信息和异常信息，但依旧会返回长度为0且不可变list*/
//        if(fileInfo.getBoolean("isSuccess")){//上传成功
//        	List<List<String>> list=excelUtils.parseExcel(fileInfo.getString("path"));
//    		try{
//    			/*动态加载service类,并调用对应的业务方法*/
//    			File file=new File(classPath);//获取类的URL
//    			URL url=file.toURI().toURL();
//    			//URL url=new URL("file:///"+classPath);//根据路径创建URL
//    			ClassLoader loader=new URLClassLoader(new URL[]{url});//创建类加载器
//    			Class<?> cls=loader.loadClass(serviceClassName);//加载指定类，注意一定要带上类的包名
//    			Object obj=cls.newInstance();//初始化一个实例
//    			Method method=cls.getMethod(methodName,List.class);//方法名和对应的参数类型
//    			method.invoke(obj,list);//调用得到的上边的方法method:对应service层去处理数据(插入数据)
//        	}catch(Exception e){
//        		e.printStackTrace();//调试用
//        		this.logger.error(e.getMessage());
//        		isSuccess=false;//service层处理数据时失败
//        	}finally{
//        		ExcelUtils.deleteFile(fileInfo.getString("path"));//删除本地excel文件
//        	}
//        }else{
//        	isSuccess=false;//上传excel文件时失败
//        }
//        
//         /*返回页面*/
//		response.setContentType("text/html;charset=UTF-8");
//		response.setHeader("Pragma", "No-cache");
//		response.setHeader("Cache-Control", "no-cache");
//		response.setDateHeader("Expires", 0L);
//		
//		JSONObject result=new JSONObject();
//		result.put("isSuccess",isSuccess? 1 : 0);//单独true或false无法转化
//	    try {
//			response.getWriter().write(result.toString());
//		} catch (IOException e) {
//			this.logger.error(e.getMessage());
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * excel导入功能-行政执法模块
	 * 
	 * @param request 客户端请求
	 * @param response 服务器端响应
	 */
//	@RequestMapping("/enforceExcelImport")
//	public void enforceExcelImport(HttpServletResponse response,HttpServletRequest request){
//		this.excelImport(response,request,"com.inspur.supervise.body.service.StaffCheckService","excelInsert");
//	}
	
	/**
	 * excel导入功能-行政执法模块
	 * 
	 * @param request 客户端请求
	 * @param response 服务器端响应
	 */
	@RequestMapping("/enforceExcelImport")
	public void enforceExcelImport(HttpServletResponse response,HttpServletRequest request){
		boolean isSuccess=true;//是否导入excel文件
		
		/*创建excel操作工具类(方便个性化操作，所有属性及方法都为非static，使用前需创建实例)*/
		ExcelUtils excelUtils=new ExcelUtils();
		//excelUtils.setDir(this.EXCELDIR);//可设置本地生成的excel目录，默认为assert/data/
		excelUtils.setIsDebug(true);//开启调试模式，确认无误可关闭默认关闭
		excelUtils.setStartRowNum(2);//设置excel文件从第2行就开始读取,默认第2行(跳过一行标题)
		excelUtils.seIsCreateKey(true);//启动读出的excel数据中自动跟上32位主键默认false
		JSONObject fileInfo=excelUtils.uploadExcel(request);//上传excel文件返回文件信息
		
		/*解析excel文件，开启调试模式后相关环节如发生异常或打印相关数据和错误提示信息和异常信息，但依旧会返回长度为0且不可变list*/
		if(fileInfo.getBoolean("isSuccess")){//上传成功
			List<List<String>> list=excelUtils.parseExcel(fileInfo.getString("path"));
			try{
				this.staffCheckService.excelInsert(list);//对应service层去处理数据
			}catch(Exception e){
				e.printStackTrace();//调试用
				this.logger.error(e.getMessage());
				isSuccess=false;//service层处理数据时失败
			}finally{
				ExcelUtils.deleteFile(fileInfo.getString("path"));//删除本地excel文件
			}
		}else{
			isSuccess=false;//上传excel文件时失败
		}
		
		 /*返回页面*/
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);
		
		JSONObject result=new JSONObject();
		result.put("isSuccess",isSuccess? 1 : 0);//单独true或false无法转化
		try {
			response.getWriter().write(result.toString());
		} catch (IOException e) {
			this.logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * excel导出功能-执法证件制作模块
	 * 
	 * @param request 客户端请求
	 * @param response 服务器端响应
	 * @throws Exception 
	 */
	@RequestMapping("/certifyExcelExport")
    public void certifyExcelExport(HttpServletRequest request, HttpServletResponse response){
		/*页面参数数据处理*/
		String fileName = this.getPara("fileName");//获取文件名称
		String certifyType= this.getPara("certifyType");//获取证件类型
		try {
			fileName=URLDecoder.decode(fileName ,"UTF-8");//转码防止中文乱码
			certifyType=URLDecoder.decode(certifyType ,"UTF-8");//转码防止中文乱码
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			this.logger.error(e.getMessage());
		}
		
		/*调用service得到excel数据list*/
		List<List<String>> dataList=null;
		try {
			dataList = this.staffCheckService.getExcelDada(certifyType);
		} catch (Exception e1) {
			dataList=Collections.emptyList();
			e1.printStackTrace();
			this.logger.error(e1.getMessage());
		}
		
		/*根据相应规则创建表头list和文件路径*/
		String prefix="监督";
		if(certifyType.equals("执法证")){
			prefix="执法";
		}
		String[] titles=new String[]{"证件编号","姓名","单位","职务","性别","身份证号",prefix+"类型",prefix+"区域","发证日期","有效日期","编制类型","经费来源"};
		List<String> titleList=Arrays.asList(titles);//excel表头list
		String rootPath=request.getSession().getServletContext().getRealPath("/").replace("\\","/");
		String ctxPath = rootPath+DOWNLOAD_DIR;
		String downLoadPath = ctxPath + fileName;//得到将要生成excel文件的绝对路径
		
		/*创建excel，根据表头,数据,文件路径,生成excel*/
		ExcelUtils excelUtils=new ExcelUtils();
		excelUtils.createExcel(titleList,dataList,downLoadPath);
		
		/*读取远程文件，本地生成相关照片文件*/
		this.createPhotos(certifyType);
		
		/*把excel文件和照片压缩生成zip文件*/
		String zipName="制证人员信息.zip";
		String zipFilePath=rootPath+ZIP_DIR+zipName;//压缩文件生成目录(不可与被压缩文件或者文件夹一个目录)
		ZipUtils.zip(ctxPath,zipFilePath);
		
		/*响应页面返回有关信息*/
		OutputStream os=null;
		try {
			os = response.getOutputStream();
		} catch (IOException e) {
			//e.printStackTrace();
			this.logger.error(e.getMessage());
		}
	    try{
	    	response.reset();
	    	response.setHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(zipName,"UTF-8"));//译码防乱码
	    	response.setContentType("application/octet-stream; charset=UTF-8");
	        os.write(FileUtils.readFileToByteArray(new File(zipFilePath)));
	        os.flush();
	    }catch(Exception e){
	    	//e.printStackTrace();
	    	this.logger.error(e.getMessage());
	    }finally {
	    	ExcelUtils.deleteFile(ctxPath);//删除生成的临时文件(zip文件可不删除下次会直接覆盖)
	        if (os != null) {
	        	try {
					os.close();
				} catch (IOException e) {
					//e.printStackTrace();
					this.logger.error(e.getMessage());
				}
	        }
	    }
	}
	

	/**
	 * 执法证件制作中照片生成（与导出生成的excel存同一目录）-根据证件类型读取云盘相应照片
	 * 
	 * @param certifyType:证件类型
	 * @throws Exception 
	 */
	private void createPhotos(String certifyType){
		/*生成路径*/
		String rootPath=request.getSession().getServletContext().getRealPath("/").replace("\\","/");
		String ctxPath = rootPath+DOWNLOAD_DIR;
		
		/*调用service得到所有指定类型人员的照片信息list<map>*/
		List<Map<String,String>> photoListMap=null;
		try {
			photoListMap = this.staffCheckService.getAllPhotosInfo(certifyType);
		} catch (Exception e1) {
			photoListMap=Collections.emptyList();
			e1.printStackTrace();
			this.logger.error(e1.getMessage());
		}
		
		/*根据URL读取远程文件在本地生成*/
		this.systemConstant.get("app.file.api.url");
		String skyURL=(String)this.systemConstant.get("app.file.api.url");
		for(Map<String,String> map:photoListMap){
			String http=skyURL+map.get("skyId");
			String photoName=map.get("id")+"-"+map.get("name")+".jpg";//生成名字
			URLFileUtils.createFile(http,ctxPath+photoName);//所有生成的照片和excel文件存同一文件夹下
		}
	}
	
    /**
	 * 必须实现但不需要的方法
	 */
	@Override
	public String add(HttpServletResponse arg0) {
		return null;
	}

	/**
	 * 必须实现但不需要的方法
	 */
	@Override
	public void del(HttpServletResponse arg0) {
	}

	/**
	 * 必须实现但不需要的方法
	 */
	@Override
	public String index(HttpServletResponse arg0, ModelMap arg1) {
		return null;
	}

	/**
	 * 必须实现但不需要的方法
	 */
	@Override
	public void query(Integer arg0, Integer arg1, HttpServletRequest arg2, HttpServletResponse arg3, ModelMap arg4) {
	}

	/**
	 * 必须实现但不需要的方法
	 */
	@Override
	public void save(HttpServletResponse arg0) {
	}

	/**
	 * 必须实现但不需要的方法
	 */
	@Override
	public String show(HttpServletResponse arg0) {
		return null;
	}

	/**
	 * 必须实现但不需要的方法
	 */
	@Override
	public void update(HttpServletResponse arg0) {
	}
	
}

