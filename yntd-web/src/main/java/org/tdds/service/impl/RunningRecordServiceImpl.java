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
import org.tdds.entity.RunningRecord;
import org.tdds.mapper.RunningRecordMapper;
import org.tdds.service.RunningRecordService;

import cn.hxz.webapp.util.DateUtils;
import net.chenke.playweb.QueryFilters;
import net.chenke.playweb.support.mybatis.Page;
import net.chenke.playweb.support.mybatis.PageImpl;
import net.chenke.playweb.support.mybatis.PageRequest;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class RunningRecordServiceImpl implements RunningRecordService {
	
	private  static final String ORDER_BY="id desc";

	@Autowired
	private RunningRecordMapper runningRecordDao;
	
	
	
	@Override
	public void insert(MonitoringList monitoringList, Machine machine) {
		Date date = new Date();
		RunningRecord entity = new RunningRecord();
			entity.setMachineId(machine.getId());
			entity.setMachineName(machine.getName());
			entity.setStartTime(machine.getStartTime());
			entity.setEndTime(date);
			entity.setTimediff(DateUtils.getDatePoor(machine.getStartTime(), date, "min"));
			entity.setMachineMode(monitoringList.getMachineMode());
			entity.setMachineStatus(monitoringList.getMachineStatus());
			entity.setMachiningTimeProgress(monitoringList.getMachiningTimeProgress());
			entity.setMainProgramNo(monitoringList.getMainprogramNo());
			entity.setMaintenanceSignal(monitoringList.getMaintenanceSignal());
			entity.setOverrideFeed(monitoringList.getOverrideFeed());
			entity.setOverrideRapid(monitoringList.getOverrideRapid());
			entity.setOverrideSpindle(monitoringList.getOverrideSpindle());
			entity.setPartsCountResult(monitoringList.getPartscountResult());
			entity.setPartsCountTarget(monitoringList.getPartscountTarget());
			runningRecordDao.insert(entity);
	}

	@Override
	public Page<RunningRecord> findAllRecords(QueryFilters filters, PageRequest pageable) {
		Example example = new Example(RunningRecord.class);
		example.setOrderByClause(ORDER_BY);
		Criteria criteria = example.createCriteria();
		if(StringUtils.hasText(Objects.toString(filters.get("recordTime"), null))){
			String recordTime = Objects.toString(filters.get("recordTime"), null);
			if(recordTime.indexOf("&")>-1){
				String startTime=recordTime.split("&")[0];
				String endTime=recordTime.split("&")[1];
				criteria.andBetween("startTime", startTime, endTime);
				criteria.andBetween("endTime", startTime, endTime);
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
 		List<RunningRecord> entities=runningRecordDao.selectByExampleAndRowBounds(example, pageable);
		return new PageImpl<RunningRecord>(entities, pageable);
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
		return runningRecordDao.exportData(filter);
	}

	@Override
	public List<String> findTimeLineTimes(Long id) {
		return runningRecordDao.findTimeLineTimes(id);
	}

	@Override
	public Double findRunningData(Map<String, Object> map) {
		return runningRecordDao.findRunningData(map);
	}

	@Override
	public Double findRankData(Long machineId) {
		return runningRecordDao.findRankData(machineId);
	}

	@Override
	public Map<String, Object> findAllRecordsByMachineId(Long id) {
		 
		return runningRecordDao.findAllRecordsByMachineId(id);
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
		return runningRecordDao.findTimeDiffByFilters(filter);
	}

	@Override
	public List<Map<String, Object>> findTimeArrays(Long machineId) {
		 
		return runningRecordDao.findTimeArrays(machineId);
	}
}
