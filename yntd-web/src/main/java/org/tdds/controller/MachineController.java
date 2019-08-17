package org.tdds.controller;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tdds.entity.Machine;
import org.tdds.entity.MonitoringList;
import org.tdds.entity.Report;
import org.tdds.service.LogRecordService;
import org.tdds.service.MachineService;
import org.tdds.service.MonitoringService;
import org.tdds.service.ReportService;
import org.tdds.service.RunningRecordService;
import org.tdds.service.WarningRecordService;

import com.alibaba.fastjson.JSONObject;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;

import cn.hxz.webapp.syscore.support.BasePortalController;
import cn.hxz.webapp.util.DateUtils;
import cn.hxz.webapp.util.echarts.StatusEnum;
import cn.hxz.webapp.util.modbus.Modbus4jUtil;

@Controller
@RequestMapping("/member")
public class MachineController extends BasePortalController {
	@Autowired
	private MachineService bizMachine;

	@Autowired
	private MonitoringService bizMonitoring;

	@Autowired
	private WarningRecordService bizWarningRecord;

	@Autowired
	private RunningRecordService bizRunningRecord;

	@Autowired
	private LogRecordService bizLogRecord;

	@Autowired
	private ReportService bizLogReport;

	private static final String[] STATUS = { "RUNNING", "POWEROFF", "ALARM",
			"WAITING", "MANUAL" };

	List<Map<String, Object>> statuslist = new ArrayList<>();

	@RequestMapping(value = "datalist", method = RequestMethod.GET)
	public Object loging(HttpServletRequest request, HttpServletResponse res) {
		List<Machine> machines = bizMachine.findMachine();
		List<MonitoringList> entities = new ArrayList<>();
		List<Map<String, Object>> list = new ArrayList<>();
		MonitoringList monitor = null;
		for (Machine machine : machines) {
			if (machine.getIo()) {
				monitor = new MonitoringList();
				monitor.setMachineName(machine.getName());
				monitor.setMachineSignal(getStatus(machine.getmIp()));
			} else {
				monitor = bizMonitoring.findByName(machine.getName());
				Map<String, Object> entity = new HashMap<>();
				entity.put("machineName", machine.getName());
				entity.put("overrideRapid", monitor.getOverrideRapid());
				entity.put("overrideSpindle", monitor.getOverrideSpindle());
				list.add(entity);
			}
			monitor.setMachineName(machine.getCode());
			entities.add(monitor);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("resault", entities);
		map.put("machines", list);
		return map;
	}

	@RequestMapping(value = "/alermMessage", method = RequestMethod.GET)
	@ResponseBody
	private Object alermMessage() {
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> entities = bizWarningRecord.findAll();
		map.put("resault", entities);
		return map;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/insertLogging", method = RequestMethod.GET)
	public Object insertLogging() {
		List<Machine> machines = bizMachine.findMachine();
		MonitoringList monitor = null;
		int i = 0;
		Boolean success = true;
		for (Machine machine : machines) {
			if (machine.getIo()) {
				monitor = new MonitoringList();
				monitor.setMachineName(machine.getName());
				monitor.setMachineSignal(getStatus(machine.getmIp()));
			} else {
				monitor = bizMonitoring.findByName(machine.getName());
			}
			i += bizMachine.update(monitor, machine);

		}
		if (i == machines.size()) {
			success = true;
		} else {
			success = false;
		}
		Map<String,Object> map = new HashMap<>();
		map.put("success",success);
		return map;
	}

	/**
	 * 每天每小时设备运行状况 一天24*60分钟
	 * 
	 * @param request
	 * @param res
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "line", method = RequestMethod.GET)
	public Object line(HttpServletRequest request, HttpServletResponse res) {
		Map<String, Object> map = new HashMap<>();
		List<String> times = new ArrayList<>();
		List<Map<String, Object>> maps = new ArrayList<>();
		List<Machine> machines = bizMachine.findMachine();
		for (Machine entity : machines) {
			if (!entity.getIo()) {

			}
		}
		map.put("xAxis",
				DateUtils.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		map.put("series", maps);

		return map;
	}

	/**
	 * 
	 * @param request
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "pie", method = RequestMethod.GET)
	@ResponseBody
	public Object pie(HttpServletRequest request, HttpServletResponse res) {
		List<Machine> machines = bizMachine.findMachine();
		List<Map<String, Object>> list = new ArrayList<>();
		for (Machine machine : machines) {
			List<Map<String, Object>> entities = new LinkedList<>();
			for (String status : STATUS) {
				if (!status.equalsIgnoreCase(STATUS[4])) {
					Map<String, Object> entity = new HashMap<>();
					Double num = bizLogRecord.findData(null, status,
							machine.getId());
					entity.put("value", num);
					entity.put("name", StatusEnum.getValue(status));
					entities.add(new JSONObject(entity));
				}
			}
			Map<String, Object> map = new HashMap<>();
			map.put("data", entities);
			map.put("machineName", machine.getName());
			list.add(map);
		}
		return list;
	}

	/**
	 * 
	 * 设备运行排名
	 * 
	 * @param request
	 * @param res
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "ranking", method = RequestMethod.GET)
	public Object ranking(HttpServletRequest request, HttpServletResponse res) {
		List<Machine> machines = bizMachine.findMachine();
		Map<String, Double> sortMap = new HashMap<>();
		for (Machine machine : machines) {
			Double num = bizRunningRecord.findRankData(machine.getId());
			sortMap.put(machine.getName(), num);
		}
		return sortMap(sortMap);
	}

	private List<Map.Entry<String, Double>> sortMap(Map<String, Double> map) {
		List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
		return list;
	}

	@RequestMapping(value = "/timeLine/categories", method = RequestMethod.GET)
	@ResponseBody
	public Object categories(HttpServletRequest request,
			HttpServletResponse response) {
		List<Machine> machines = bizMachine.findMachine();
		List<String> names = new ArrayList<>();
		for (Machine entity : machines) {
			names.add(entity.getName());
		}
		return names;
	}

	@RequestMapping(value = "/reportList", method = RequestMethod.GET)
	@ResponseBody
	public Object reportList(HttpServletRequest request,
			HttpServletResponse response) {
		List<Report> reportsList = bizLogReport.findAll();
		List<Map<String, Object>> entities = new ArrayList<>();
		for (Report report : reportsList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("machineName", report.getMachineName());
			map.put("plannedOtime", report.getPlannedOtime() + "H");
			map.put("actualOtime", report.getActualOtime() + "H");
			map.put("timeOee",
					createOee(report.getPlannedOtime(), report.getActualOtime()));
			map.put("plannedCapacity", report.getPlannedCapacity());
			map.put("actualCapacity", report.getActualCapacity());
			map.put("performanceOee",
					createOee(report.getPlannedCapacity(),
							report.getActualCapacity()));
			map.put("number", report.getNumber());
			map.put("goodNumber", report.getGoodNumber());
			map.put("goodYield",
					createOee(report.getNumber(), report.getGoodNumber()));
			entities.add(map);
		}
		return entities;
	}

	private String createOee(int dividend, int divisor) {
		String f = null;
		if (dividend != 0) {
			if (dividend == divisor) {
				f = "100";
			} else {
				Double numDouble = (Double.valueOf(divisor) / Double
						.valueOf(dividend)) * 100;
				f = new DecimalFormat("#.00").format(numDouble);
			}
		} else {
			f = "0";
		}
		return f + "%";
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/timeLine/seriesData", method = RequestMethod.GET)
	@ResponseBody
	public Object timer(HttpServletRequest request, HttpServletResponse response)
			throws ParseException {
		List<Machine> machines = bizMachine.findMachine();
		int i = 0;
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		for (Machine machine : machines) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", i);
			List<Map<String, Object>> times = new ArrayList<>();
			for (String status : STATUS) {
				List<Map<String, Object>> time = bizLogRecord.findTimeArrays(
						status, machine.getId());
				if (!time.isEmpty()) {
					times.addAll(time);
				}
			}

			if (!times.isEmpty()) {
				map.put("values", times);
			}
			list.add(map);
			i++;
		}
		return list;
	}

	/**
	 * @param running
	 *            :运行
	 * @param waitting
	 *            :等待
	 * @param warning
	 *            :报警 status=1:停机 status=0:运行
	 * @param ip
	 *            :设备ip
	 * @param port
	 *            :端口号
	 * @return io 设备采集数据状态
	 */
	private String getStatus(String ip) {
		String status = STATUS[0];
		Boolean running = null;
		Boolean waitting = null;
		Boolean warning = null;
		/* ip="192.168.0.140"; */
		try {
			running = Modbus4jUtil.readInputStatus(ip, 502, 1, 0);
			waitting = Modbus4jUtil.readInputStatus(ip, 502, 1, 1);
			warning = Modbus4jUtil.readInputStatus(ip, 502, 1, 2);
		} catch (ModbusTransportException | ErrorResponseException
				| ModbusInitException e) {
			status = STATUS[1];
		}

		if (running != null && running) {
			if (waitting) {
				status = STATUS[0];
			} else {
				status = STATUS[3];
			}
			if (warning) {
				status = STATUS[2];
			}
		} else {
			status = STATUS[1];
		}
		return status;
	}

}
