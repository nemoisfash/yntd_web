package org.tdds.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.tdds.entity.Machine;
import org.tdds.entity.MonitoringList;
import org.tdds.entity.PowerOffRecord;
import org.tdds.mapper.PowerOffRecordMapper;
import org.tdds.service.PowerOffRecordService;

import cn.hxz.webapp.util.DateUtils;
import net.chenke.playweb.QueryFilters;
import net.chenke.playweb.support.mybatis.Page;
import net.chenke.playweb.support.mybatis.PageImpl;
import net.chenke.playweb.support.mybatis.PageRequest;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class PowerOffRecordServiceImpl implements PowerOffRecordService{
	
	private  static final String ORDER_BY="id desc";
	
	@Autowired
	private PowerOffRecordMapper daoPoweroff;
	
	@Override
	public void insert(MonitoringList monitoringList, Machine entity) {
	PowerOffRecord pr= new PowerOffRecord();
		Date date = new Date();
		pr.setMachineId(entity.getId());
		pr.setMachineName(entity.getName());
		pr.setStartTime(entity.getStartTime());
		pr.setEndTime(date);
		pr.setTimediff(DateUtils.getDatePoor(entity.getStartTime(), date, "min"));
		daoPoweroff.insert(pr);
	}
 
	@Override
	public Page<PowerOffRecord> findAllRecords(QueryFilters filters, PageRequest pageable){
		Example example = new Example(PowerOffRecord.class);
		example.setOrderByClause(ORDER_BY);
		Criteria criteria = example.createCriteria();
		if(StringUtils.hasText(Objects.toString(filters.get("recordTime"), null))){
			String recordTime = Objects.toString(filters.get("recordTime"), null);
			if(recordTime.indexOf("&")>-1){
				String startTime=recordTime.split("&")[0];
				String endTime=recordTime.split("&")[1];
				criteria.andEqualTo("startTime", startTime);
				criteria.andEqualTo("endTime", endTime);
			} 
			if(NumberUtils.isNumber(recordTime)){
				Integer num=Integer.valueOf(recordTime);
				Map<String, String> map = getTime(num);
				criteria.andBetween("startTime",map.get("startTime"), map.get("endTime"));
				criteria.andBetween("endTime",map.get("startTime"), map.get("endTime"));
			}
		}if(StringUtils.hasText(Objects.toString(filters.get("timediff"), null))){
			String timediff=Objects.toString(filters.get("timediff"));
			criteria.andLessThanOrEqualTo("timediff",Integer.valueOf(timediff));
		 }
		if(StringUtils.hasText(Objects.toString(filters.get("machineName"), null))){
			 criteria.andEqualTo("machineName", Objects.toString(filters.get("machineName")));
		 }
 		List<PowerOffRecord> entities=daoPoweroff.selectByExampleAndRowBounds(example, pageable);
		return new PageImpl<PowerOffRecord>(entities, pageable);
	}

	private Map<String, String> getTime(Integer flag){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar now = Calendar.getInstance();
		Map<String , String> timeMap = new HashMap<>();
		timeMap.put("endTime", sdf.format(now.getTime()));
		switch (flag) {
		case 3:
			now.add(Calendar.DAY_OF_MONTH, -3);
			timeMap.put("startTime", sdf.format(now.getTime()));
			break;
		case 7:
			now.add(Calendar.DAY_OF_MONTH, -7);
			timeMap.put("startTime", sdf.format(now.getTime()));
			break;
		case 15:
			now.add(Calendar.DAY_OF_MONTH, -15);
			timeMap.put("startTime", sdf.format(now.getTime()));
			break;
		case 30:
			now.add(Calendar.MONTH, -1);
			timeMap.put("startTime", sdf.format(now.getTime()));
			break;
		default:
			now.add(Calendar.HOUR_OF_DAY, -24);
			timeMap.put("startTime", sdf.format(now.getTime()));
			break;
		}
		return timeMap;
	}
	
	@Override
	public List<Map<String, Object>> exportData(QueryFilters filters) {
		Map<String, Object> filter = new HashMap<>();
		if(StringUtils.hasText(Objects.toString(filters.get("recordTime"), null))){
			String recordTime = Objects.toString(filters.get("recordTime"), null);
			if(recordTime.indexOf("&")>-1){
				String startTime=recordTime.split("&")[0];
				String endTime=recordTime.split("&")[1];
				filter.put("startTime", startTime);
				filter.put("endTime", endTime);
			} 
			if(NumberUtils.isNumber(recordTime)){
				Integer num=Integer.valueOf(recordTime);
				Map<String, String> map = getTime(num);
				filter.put("startTime",map.get("startTime"));
				filter.put("endTime",map.get("endTime"));
			}
		}if(StringUtils.hasText(Objects.toString(filters.get("timediff"), null))){
			String timediff=Objects.toString(filters.get("timediff"));
			filter.put("timediff",Integer.valueOf(timediff));
		 }
		if(StringUtils.hasText(Objects.toString(filters.get("machineName"), null))){
			filter.put("machineName", Objects.toString(filters.get("machineName")));
		 }
		return daoPoweroff.exportData(filter);
	}

	@Override
	public List<String> findTimeLineTimes(Long machineId){
		return daoPoweroff.findTimeLineTimes(machineId);
	}

	@Override
	public Double findPoweroffData(Map<String, Object> map) {
		 
		return daoPoweroff.findPoweroffData(map);
	}

	@Override
	public  Map<String, Object> findAllRecordsByMachineId(Long id) {
		// TODO Auto-generated method stub
		return daoPoweroff.findAllRecordsByMachineId(id);
	}

	@Override
	public Double findTimeDiffByFilters(QueryFilters filters) {
		Map<String, Object> filter = new HashMap<>();
		if(StringUtils.hasText(Objects.toString(filters.get("recordTime"), null))){
			String recordTime = Objects.toString(filters.get("recordTime"), null);
			if(recordTime.indexOf("&")>-1){
				String startTime=recordTime.split("&")[0];
				String endTime=recordTime.split("&")[1];
				filter.put("startTime", startTime);
				filter.put("endTime", endTime);
			} 
			if(NumberUtils.isNumber(recordTime)){
				Integer num=Integer.valueOf(recordTime);
				Map<String, String> map = getTime(num);
				filter.put("startTime",map.get("startTime"));
				filter.put("endTime",map.get("endTime"));
			}
		}if(StringUtils.hasText(Objects.toString(filters.get("timediff"), null))){
			String timediff=Objects.toString(filters.get("timediff"));
			filter.put("timediff",Integer.valueOf(timediff));
		 }
		if(StringUtils.hasText(Objects.toString(filters.get("machineName"), null))){
			filter.put("machineName", Objects.toString(filters.get("machineName")));
		 }
		return daoPoweroff.findTimeDiffByFilters(filter);
	}

	@Override
	public List<Map<String, Object>> findTimeArrays(Long machineId) {
		 
		return daoPoweroff.findTimeArrays(machineId);
	}

	@Override
	public int selectRepeat(Long id, Date startTime) {
		 
		return daoPoweroff.selectRepeatCount(id,startTime);
	}

	@Override
	public void deleteRepeat(Long id, Date startTime) {
		daoPoweroff.deleteRepeat(id,startTime);
	}
}
