
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
 * excel���빦�ܿ�����-����ģ�������Ӧ�ķ������ٵ��ö�Ӧ��service�������ݵĴ���
 * 
 * @author ������
 * @version 2.5
 * @date 2015-05-06
 */
@Controller
@RequestMapping("/server/excel")
public class ExcelController  extends BaseController{
	
	/**����ִ��ģ��service*/
	@Autowired
	private StaffCheckService staffCheckService;
	
	/**excel���ɵ�Ŀ¼(�����Ŀ��Ŀ¼)*/
	//private final String EXCELDIR="assets/data/";
	
	/**��Ƭ��excel�������ɵ�Ŀ¼(�����Ŀ��Ŀ¼)*/
	private final static String DOWNLOAD_DIR="assets/download/";
	
	/**ѹ���ļ����ɵ�Ŀ¼(�����Ŀ��Ŀ¼)*/
	private final static String ZIP_DIR="assets/";
	
	/**
	 * excel���빦�ܹ���ģ��
	 * @description ͨ������excelUtil�ͷ������,ʹ�ô�ģ����ҵ��ģ��"����"��ȫ����,�ﵽ�����,ģ�黯,�����ظ�����,���������excel���ܷ�����ص�
	 * @param request:�ͻ�������
	 * @param response:����������Ӧ
	 * @param serviceClassName:����excel����(һ��Ϊ��������)service���������(��������)��com.inspur.demo.service.DemoService
	 * @param methodName:����excel���ݵķ�����(�˷���������һ��List<List<String>>���͵Ĳ���)
	 */
//	private void excelImport(HttpServletResponse response,HttpServletRequest request,String serviceClassName,String methodName){
//		/*��ȡ��ľ���·��*/
//		ServletContext sc=request.getSession().getServletContext();//servelt������
//		String rootPath=sc.getRealPath("/").replace("\\","/");//��Ŀ�ľ���·��
//		//String packageName=serviceClassName.substring(0,serviceClassName.lastIndexOf('.'));//����
//		//String classPath=rootPath+"WEB-INF/classes/"+packageName.replace('.','/');//��ľ���·��
//		String classPath=rootPath+"WEB-INF/classes/";//��ľ���·��,��������·��
//		
//		boolean isSuccess=true;//�Ƿ���excel�ļ�
//		
//		/*����excel����������(������Ի��������������Լ�������Ϊ��static��ʹ��ǰ�贴��ʵ��)*/
//		ExcelUtils excelUtils=new ExcelUtils();
//		excelUtils.setDir(this.EXCELDIR);//�����ñ������ɵ�excelĿ¼��Ĭ��Ϊassert/data/
//        excelUtils.setIsDebug(true);//�ɿ�������ģʽ��ȷ������ɹر�Ĭ�Ϲر�
//        excelUtils.setStartRowNum(2);//������excel�ļ��ӵ�2�оͿ�ʼ��ȡ,Ĭ�ϵ�2��(����һ�б���)
//        excelUtils.seIsCreateKey(true);//������������excel�������Զ�����32λ����Ĭ��false
//        JSONObject fileInfo=excelUtils.uploadExcel(request);//�ϴ�excel�ļ������ļ���Ϣ
//        
//        /*����excel�ļ�����������ģʽ����ػ����緢���쳣���ӡ������ݺʹ�����ʾ��Ϣ���쳣��Ϣ�������ɻ᷵�س���Ϊ0�Ҳ��ɱ�list*/
//        if(fileInfo.getBoolean("isSuccess")){//�ϴ��ɹ�
//        	List<List<String>> list=excelUtils.parseExcel(fileInfo.getString("path"));
//    		try{
//    			/*��̬����service��,�����ö�Ӧ��ҵ�񷽷�*/
//    			File file=new File(classPath);//��ȡ���URL
//    			URL url=file.toURI().toURL();
//    			//URL url=new URL("file:///"+classPath);//����·������URL
//    			ClassLoader loader=new URLClassLoader(new URL[]{url});//�����������
//    			Class<?> cls=loader.loadClass(serviceClassName);//����ָ���࣬ע��һ��Ҫ������İ���
//    			Object obj=cls.newInstance();//��ʼ��һ��ʵ��
//    			Method method=cls.getMethod(methodName,List.class);//�������Ͷ�Ӧ�Ĳ�������
//    			method.invoke(obj,list);//���õõ����ϱߵķ���method:��Ӧservice��ȥ��������(��������)
//        	}catch(Exception e){
//        		e.printStackTrace();//������
//        		this.logger.error(e.getMessage());
//        		isSuccess=false;//service�㴦������ʱʧ��
//        	}finally{
//        		ExcelUtils.deleteFile(fileInfo.getString("path"));//ɾ������excel�ļ�
//        	}
//        }else{
//        	isSuccess=false;//�ϴ�excel�ļ�ʱʧ��
//        }
//        
//         /*����ҳ��*/
//		response.setContentType("text/html;charset=UTF-8");
//		response.setHeader("Pragma", "No-cache");
//		response.setHeader("Cache-Control", "no-cache");
//		response.setDateHeader("Expires", 0L);
//		
//		JSONObject result=new JSONObject();
//		result.put("isSuccess",isSuccess? 1 : 0);//����true��false�޷�ת��
//	    try {
//			response.getWriter().write(result.toString());
//		} catch (IOException e) {
//			this.logger.error(e.getMessage());
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * excel���빦��-����ִ��ģ��
	 * 
	 * @param request �ͻ�������
	 * @param response ����������Ӧ
	 */
//	@RequestMapping("/enforceExcelImport")
//	public void enforceExcelImport(HttpServletResponse response,HttpServletRequest request){
//		this.excelImport(response,request,"com.inspur.supervise.body.service.StaffCheckService","excelInsert");
//	}
	
	/**
	 * excel���빦��-����ִ��ģ��
	 * 
	 * @param request �ͻ�������
	 * @param response ����������Ӧ
	 */
	@RequestMapping("/enforceExcelImport")
	public void enforceExcelImport(HttpServletResponse response,HttpServletRequest request){
		boolean isSuccess=true;//�Ƿ���excel�ļ�
		
		/*����excel����������(������Ի��������������Լ�������Ϊ��static��ʹ��ǰ�贴��ʵ��)*/
		ExcelUtils excelUtils=new ExcelUtils();
		//excelUtils.setDir(this.EXCELDIR);//�����ñ������ɵ�excelĿ¼��Ĭ��Ϊassert/data/
		excelUtils.setIsDebug(true);//��������ģʽ��ȷ������ɹر�Ĭ�Ϲر�
		excelUtils.setStartRowNum(2);//����excel�ļ��ӵ�2�оͿ�ʼ��ȡ,Ĭ�ϵ�2��(����һ�б���)
		excelUtils.seIsCreateKey(true);//����������excel�������Զ�����32λ����Ĭ��false
		JSONObject fileInfo=excelUtils.uploadExcel(request);//�ϴ�excel�ļ������ļ���Ϣ
		
		/*����excel�ļ�����������ģʽ����ػ����緢���쳣���ӡ������ݺʹ�����ʾ��Ϣ���쳣��Ϣ�������ɻ᷵�س���Ϊ0�Ҳ��ɱ�list*/
		if(fileInfo.getBoolean("isSuccess")){//�ϴ��ɹ�
			List<List<String>> list=excelUtils.parseExcel(fileInfo.getString("path"));
			try{
				this.staffCheckService.excelInsert(list);//��Ӧservice��ȥ��������
			}catch(Exception e){
				e.printStackTrace();//������
				this.logger.error(e.getMessage());
				isSuccess=false;//service�㴦������ʱʧ��
			}finally{
				ExcelUtils.deleteFile(fileInfo.getString("path"));//ɾ������excel�ļ�
			}
		}else{
			isSuccess=false;//�ϴ�excel�ļ�ʱʧ��
		}
		
		 /*����ҳ��*/
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);
		
		JSONObject result=new JSONObject();
		result.put("isSuccess",isSuccess? 1 : 0);//����true��false�޷�ת��
		try {
			response.getWriter().write(result.toString());
		} catch (IOException e) {
			this.logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * excel��������-ִ��֤������ģ��
	 * 
	 * @param request �ͻ�������
	 * @param response ����������Ӧ
	 * @throws Exception 
	 */
	@RequestMapping("/certifyExcelExport")
    public void certifyExcelExport(HttpServletRequest request, HttpServletResponse response){
		/*ҳ��������ݴ���*/
		String fileName = this.getPara("fileName");//��ȡ�ļ�����
		String certifyType= this.getPara("certifyType");//��ȡ֤������
		try {
			fileName=URLDecoder.decode(fileName ,"UTF-8");//ת���ֹ��������
			certifyType=URLDecoder.decode(certifyType ,"UTF-8");//ת���ֹ��������
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			this.logger.error(e.getMessage());
		}
		
		/*����service�õ�excel����list*/
		List<List<String>> dataList=null;
		try {
			dataList = this.staffCheckService.getExcelDada(certifyType);
		} catch (Exception e1) {
			dataList=Collections.emptyList();
			e1.printStackTrace();
			this.logger.error(e1.getMessage());
		}
		
		/*������Ӧ���򴴽���ͷlist���ļ�·��*/
		String prefix="�ල";
		if(certifyType.equals("ִ��֤")){
			prefix="ִ��";
		}
		String[] titles=new String[]{"֤�����","����","��λ","ְ��","�Ա�","���֤��",prefix+"����",prefix+"����","��֤����","��Ч����","��������","������Դ"};
		List<String> titleList=Arrays.asList(titles);//excel��ͷlist
		String rootPath=request.getSession().getServletContext().getRealPath("/").replace("\\","/");
		String ctxPath = rootPath+DOWNLOAD_DIR;
		String downLoadPath = ctxPath + fileName;//�õ���Ҫ����excel�ļ��ľ���·��
		
		/*����excel�����ݱ�ͷ,����,�ļ�·��,����excel*/
		ExcelUtils excelUtils=new ExcelUtils();
		excelUtils.createExcel(titleList,dataList,downLoadPath);
		
		/*��ȡԶ���ļ����������������Ƭ�ļ�*/
		this.createPhotos(certifyType);
		
		/*��excel�ļ�����Ƭѹ������zip�ļ�*/
		String zipName="��֤��Ա��Ϣ.zip";
		String zipFilePath=rootPath+ZIP_DIR+zipName;//ѹ���ļ�����Ŀ¼(�����뱻ѹ���ļ������ļ���һ��Ŀ¼)
		ZipUtils.zip(ctxPath,zipFilePath);
		
		/*��Ӧҳ�淵���й���Ϣ*/
		OutputStream os=null;
		try {
			os = response.getOutputStream();
		} catch (IOException e) {
			//e.printStackTrace();
			this.logger.error(e.getMessage());
		}
	    try{
	    	response.reset();
	    	response.setHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(zipName,"UTF-8"));//���������
	    	response.setContentType("application/octet-stream; charset=UTF-8");
	        os.write(FileUtils.readFileToByteArray(new File(zipFilePath)));
	        os.flush();
	    }catch(Exception e){
	    	//e.printStackTrace();
	    	this.logger.error(e.getMessage());
	    }finally {
	    	ExcelUtils.deleteFile(ctxPath);//ɾ�����ɵ���ʱ�ļ�(zip�ļ��ɲ�ɾ���´λ�ֱ�Ӹ���)
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
	 * ִ��֤����������Ƭ���ɣ��뵼�����ɵ�excel��ͬһĿ¼��-����֤�����Ͷ�ȡ������Ӧ��Ƭ
	 * 
	 * @param certifyType:֤������
	 * @throws Exception 
	 */
	private void createPhotos(String certifyType){
		/*����·��*/
		String rootPath=request.getSession().getServletContext().getRealPath("/").replace("\\","/");
		String ctxPath = rootPath+DOWNLOAD_DIR;
		
		/*����service�õ�����ָ��������Ա����Ƭ��Ϣlist<map>*/
		List<Map<String,String>> photoListMap=null;
		try {
			photoListMap = this.staffCheckService.getAllPhotosInfo(certifyType);
		} catch (Exception e1) {
			photoListMap=Collections.emptyList();
			e1.printStackTrace();
			this.logger.error(e1.getMessage());
		}
		
		/*����URL��ȡԶ���ļ��ڱ�������*/
		this.systemConstant.get("app.file.api.url");
		String skyURL=(String)this.systemConstant.get("app.file.api.url");
		for(Map<String,String> map:photoListMap){
			String http=skyURL+map.get("skyId");
			String photoName=map.get("id")+"-"+map.get("name")+".jpg";//��������
			URLFileUtils.createFile(http,ctxPath+photoName);//�������ɵ���Ƭ��excel�ļ���ͬһ�ļ�����
		}
	}
	
    /**
	 * ����ʵ�ֵ�����Ҫ�ķ���
	 */
	@Override
	public String add(HttpServletResponse arg0) {
		return null;
	}

	/**
	 * ����ʵ�ֵ�����Ҫ�ķ���
	 */
	@Override
	public void del(HttpServletResponse arg0) {
	}

	/**
	 * ����ʵ�ֵ�����Ҫ�ķ���
	 */
	@Override
	public String index(HttpServletResponse arg0, ModelMap arg1) {
		return null;
	}

	/**
	 * ����ʵ�ֵ�����Ҫ�ķ���
	 */
	@Override
	public void query(Integer arg0, Integer arg1, HttpServletRequest arg2, HttpServletResponse arg3, ModelMap arg4) {
	}

	/**
	 * ����ʵ�ֵ�����Ҫ�ķ���
	 */
	@Override
	public void save(HttpServletResponse arg0) {
	}

	/**
	 * ����ʵ�ֵ�����Ҫ�ķ���
	 */
	@Override
	public String show(HttpServletResponse arg0) {
		return null;
	}

	/**
	 * ����ʵ�ֵ�����Ҫ�ķ���
	 */
	@Override
	public void update(HttpServletResponse arg0) {
	}
	
}

