package org.tdds.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.tdds.entity.Machine;
import org.tdds.entity.MonitoringList;
import org.tdds.entity.PowerOffRecord;

import net.chenke.playweb.QueryFilters;
import net.chenke.playweb.support.mybatis.Page;
import net.chenke.playweb.support.mybatis.PageRequest;


public interface PowerOffRecordService {
	
	void insert(MonitoringList monitoringList, Machine entity);

	Page<PowerOffRecord> findAllRecords(QueryFilters filters, PageRequest pageable);

	List<Map<String, Object>> exportData(QueryFilters filters);

	List<String> findTimeLineTimes(Long id);

	Double findPoweroffData(Map<String, Object> map);

	Map<String, Object> findAllRecordsByMachineId(Long id);

	Double findTimeDiffByFilters(QueryFilters filters);

	List<Map<String, Object>> findTimeArrays(Long machineId);

	int selectRepeat(Long id, Date startTime);

	void deleteRepeat(Long id, Date startTime);
}
