package cn.hxz.webapp.util;
 
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

 
public class MqttDataUtils{
 
	private static final String BROKER="BROKER";
	
	private static final String CLIENT_ID="CLIENT_ID";
	
	private static final String USERNAME="USERNAME";
	
	private static final String PASSWORD="PASSWORD";
	
	private static final String TOPIC="TOPIC";
	
	private static MqttClient client;
	private static MqttTopic topic;
	private static MqttMessage message;
	private static final String CONFIG_FILE="mqtt/mqtt.properties";
	private static String getValue(String key) {
		Properties prop = new Properties();
		InputStream in = ExcelExportUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
		try {
			prop.load(new InputStreamReader(in, "utf-8"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "";

		}
		return prop.getProperty(key);
	}
	
	public static Boolean connect() {
		Boolean success=true;
		String broker=getValue(BROKER);
		String clientId=getValue(CLIENT_ID);
		try {
			client = new MqttClient(broker, clientId, new MemoryPersistence());
		} catch (MqttException e1) {
			success=false;
			e1.printStackTrace();
		}
		String userName=getValue(USERNAME);
		String passWord=getValue(PASSWORD);
		String topics=getValue(TOPIC);
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setUserName(userName);
		options.setPassword(passWord.toCharArray());
		// 设置超时时间
		options.setConnectionTimeout(10);
		// 设置会话心跳时间
		options.setKeepAliveInterval(20);
		topic = client.getTopic(topics);
//		// setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
//		options.setWill(topic, (CLIENT_ID+"已挂").getBytes(), 2, true);
		try {
			client.setCallback(new MyCallback(clientId));
			client.connect(options);
		} catch (Exception e) {
			success=false;
			e.printStackTrace();
		}
		return success;
	}

	public static void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException, MqttException {
		MqttDeliveryToken token = topic.publish(message);
		token.waitForCompletion();
		System.out.println("message is published completely! " + token.isComplete());
	}

	public static void uploadDataBYMqttData(String data){
		message = new MqttMessage();
		message.setQos(2);
		message.setRetained(false);
		message.setPayload(data.getBytes());
		try {
			MqttDataUtils.publish(topic,message);
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

}
