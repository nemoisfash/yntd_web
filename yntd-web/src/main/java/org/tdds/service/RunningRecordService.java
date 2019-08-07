package org.tdds.service;



import java.util.Date;
import java.util.List;
import java.util.Map;

import org.tdds.entity.Machine;
import org.tdds.entity.MonitoringList;
import org.tdds.entity.RunningRecord;

import net.chenke.playweb.QueryFilters;
import net.chenke.playweb.support.mybatis.Page;
import net.chenke.playweb.support.mybatis.PageRequest;

public interface RunningRecordService {

	void insert(MonitoringList monitoringList, Machine entity);

	Page<RunningRecord> findAllRecords(QueryFilters filters, PageRequest pageable);

	List<Map<String, Object>> exportData(QueryFilters filters);

	List<String> findTimeLineTimes(Long id);

	Double findRunningData(Map<String, Object> map);

	Double findRankData(Long machineId);

	Map<String, Object> findAllRecordsByMachineId(Long id);

	Double findTimeDiffByFilters(QueryFilters filters);

	List<Map<String, Object>> findTimeArrays(Long machineId);

	int selectRepeat(Long long1, Date date);

	void deleteRepeat(Long id, Date startTime);
}
