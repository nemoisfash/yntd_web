package org.tdds.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdds.service.LogRecordService;
import org.tdds.service.PowerOffRecordService;
import org.tdds.service.RunningRecordService;
import org.tdds.service.WaitingRecordService;
import org.tdds.service.WarningRecordService;


@Service
public class LogRecordServiceImpl implements LogRecordService {

	private static final String[] STATUS = {"RUNNING", "POWEROFF", "ALARM", "WAITING","MANUAL"};
	
	@Autowired
	private WaitingRecordService bizWaiting;
	
	@Autowired
	private PowerOffRecordService bizPowerOff;
 
	@Autowired
	private WarningRecordService bizWarning;
	
	@Autowired
	private RunningRecordService bizRunning;

	/*
	 * 西部大森 manual=running
	 * (non-Javadoc)
	 * @see org.tdds.service.LogRecordService#findData(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public Double findData(String date, String status,Long machineId) {
		Map<String, Object> map = new HashMap<>();
		if(date!=null){
			map.put("date",date);
		}
		if(machineId!=null){
			map.put("machineId",machineId);
		}
		Double count=0.0;
		if(status.equalsIgnoreCase(STATUS[0])){
			count=bizRunning.findRunningData(map);
		}else if(status.equalsIgnoreCase(STATUS[1])){
			count=bizPowerOff.findPoweroffData(map);
		}else if(status.equalsIgnoreCase(STATUS[2])){
			count=bizWarning.findAlarmData(map);
		}else if(status.equalsIgnoreCase(STATUS[3])){
			count=bizWaiting.findWaittingData(map);
		} else {
			count=bizWaiting.findWaittingData(map);
		}
		return count;
	}

	@Override
	public Map<String, Object> findTimeLineData(Long id,String status) {
		 Map<String, Object> map = new HashMap<>();
		 if(status.equalsIgnoreCase(STATUS[0])){
			 map= bizRunning.findAllRecordsByMachineId(id);
		 }else if(status.equalsIgnoreCase(STATUS[1])){
			 map= bizPowerOff.findAllRecordsByMachineId(id);
		 }else if(status.equalsIgnoreCase(STATUS[2])){
			 map=bizWarning.findAllRecordsByMachineId(id);
		 }else if(status.equalsIgnoreCase(STATUS[3])){
			 map=bizWaiting.findAllRecordsByMachineId(id);
		 }
		return map;
	}

	@Override
	public Double findRunningData(String date) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("date", date);
		Double numDouble =bizRunning.findRunningData(map);
		return numDouble;
	}

	@Override
	public List<Map<String, Object>> findTimeArrays(String status,
			Long machineId) {
		List<Map<String,Object>> times=new ArrayList<>();
		 if(status.equalsIgnoreCase(STATUS[0])){
			 times=bizRunning.findTimeArrays(machineId);
		 }else if(status.equalsIgnoreCase(STATUS[1])){
			 times=bizPowerOff.findTimeArrays(machineId);
		 }else if(status.equalsIgnoreCase(STATUS[2])){
			 times=bizWarning.findTimeArrays(machineId);
		 }else if(status.equalsIgnoreCase(STATUS[3])){
			 times=bizWaiting.findTimeArrays(machineId);
		 }
		return times;
	}
}

