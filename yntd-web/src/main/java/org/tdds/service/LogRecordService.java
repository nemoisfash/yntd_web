package org.tdds.service;

import java.util.List;
import java.util.Map;

public interface LogRecordService {

	Double findData(String date, String status, Long machineId);

	Map<String, Object> findTimeLineData(Long id, String status);

	Double findRunningData(String date);

	List<Map<String, Object>> findTimeArrays(String status, Long machineId);
}
