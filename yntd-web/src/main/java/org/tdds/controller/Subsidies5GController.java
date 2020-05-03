package org.tdds.controller;

 

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
 
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tdds.entity.Activities;
import org.tdds.entity.Recipients;
import org.tdds.service.RecipientsService;

import cn.hxz.webapp.syscore.support.BasePortalController;
import cn.hxz.webapp.util.MobileOperatorUtils;
 

@Controller
@RequestMapping("/activities")
public class Subsidies5GController extends BasePortalController{
	
	@Autowired
	private RecipientsService bizRecipients;
	
	

	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Object save(String subsidiesTel,String remTel) {
		Recipients entity = new Recipients();
		entity.setTel(subsidiesTel);
		entity.setRecomTel(remTel);
		int num =  bizRecipients.save(entity);
		Map<String, Object> map = new HashMap<String, Object>();
		//String credentials = HashUtils.MD5(subsidiesTel, remTel);
		//UsernamePasswordToken token = new UsernamePasswordToken(subsidiesTel, credentials, false, ShiroUtils.getHost());
		//ShiroUtils.login(token);
		 if(num==1) {
			map.put("result",true);
			map.put("message","登陆成功");
			map.put("data",subsidiesTel);
		}else {
			map.put("result",false);
			map.put("message","登陆失败");
		}
		return map;
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/sendSMS", method = RequestMethod.GET)
	public Object sendSms(HttpServletRequest request,HttpServletResponse response,@RequestParam("tel") String tel) {
 		Map<String, Object> map = new HashMap<String, Object>();
 		if(StringUtils.isBlank(tel) || StringUtils.isEmpty(tel)) {
			map.put("message", "手机号码不能为空");
			map.put("result",false);
			return map;
		}
 		
		if(!MobileOperatorUtils.isChineTelecom(tel)) {
			map.put("message", "此活动只有电信用户才能参加");
			map.put("result",false);
			return map;
		}
		//String code= RandomStringUtils.randomAlphanumeric(5);
		String code = "nxdx";
		try {
			//SendSmsUtils.SendSms(content, tel);
			Cookie cookie = new Cookie(tel,code);
			cookie.setMaxAge(60);
			cookie.setPath("/");
			response.addCookie(cookie);
			map.put("message", "验证码已发送请注意查收！");
			map.put("result",true);
			map.put("data",code);
		 } catch (Exception e) {
			map.put("message", "发送验证码出错");
			map.put("result",false);
			return map;
		}
		return map;
	 }
		
 
	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public String page() {
		return this.view("/www/page");
	}
		
	@RequestMapping(value = "/allActivities", method = RequestMethod.GET)
	@ResponseBody
	public Object allActivities(ModelMap map) {
		List<Activities> entities = bizRecipients.findAllActivities();
		if(!CollectionUtils.isEmpty(entities)) {
			map.put("data", entities);
			map.put("result", true);
			map.put("message", "查询活动成功");
		 }
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Object update(String currentSubsidiesTel,String activitiesId) {
		Recipients entity = bizRecipients.getOne(currentSubsidiesTel);
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(activitiesId) || StringUtils.isBlank(currentSubsidiesTel)) {
			map.put("result", false);
			return map;
		}
		 entity.setActivitiesId(Long.valueOf(activitiesId));
		 int num= bizRecipients.update(entity);
		 if(num==1) {
			 map.put("result",true);
		 }else {
			 map.put("result",false);
		 }
		return map;
	}
	
	 
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success() {
		return this.view("/www/success");
	}
	
	@RequestMapping(value = "/getSubsidies", method = RequestMethod.GET)
	@ResponseBody
	public Object getSubsidies(String tel) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isEmpty(tel)||StringUtils.isBlank(tel)) {
			map.put("result", false);
			return map;
		}
		Recipients entity = bizRecipients.getOne(tel);
		if(entity!=null) {
			map.put("result", true);
		}else {
			map.put("result", false);
		}
		return map;
	}
	
}

