package cn.hxz.webapp.util;

import org.apache.commons.lang3.RandomStringUtils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;


public class SendSmsUtils {
	
	private final static String REGION= "cn-ningxia";
	
	private final static String ACCESS_KEY_Id= "accessKeyId";
	
	private final static String ACCESS_SECRET="accessSecret";
	
	
	public static void SendSms(String content,String tel) {
		DefaultProfile profile = DefaultProfile.getProfile(REGION, ACCESS_KEY_Id, ACCESS_SECRET );
	    IAcsClient client = new DefaultAcsClient(profile);
	   
	    
	    CommonRequest request = new CommonRequest();
	      /*  request.setSysMethod(MethodType.POST);
	        request.setSysDomain("dysmsapi.aliyuncs.com");
	        request.setSysVersion("2017-05-25");
	        request.setSysAction("SendSms");
	        request.putQueryParameter("RegionId", REGION);*/
	 	}
	
 
	
}
