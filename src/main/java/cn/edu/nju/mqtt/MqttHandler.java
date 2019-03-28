package cn.edu.nju.mqtt;


import cn.edu.nju.SerialMqttBridge;
import cn.edu.nju.config.ConfigHandler;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttHandler {

  private Logger logger = LoggerFactory.getLogger(MqttHandler.class);

  private static MqttHandler instance;
  private SerialMqttBridge serialMqttBridge;

  private MqttClient mqttClient;
  private MqttSubscriptionCallback mqttCallback;

  private MqttHandler(SerialMqttBridge serialMqttBride) throws Exception {
    this.serialMqttBridge = serialMqttBride;
    connectAndSubscribe();
  }


  public static MqttHandler getInstance(SerialMqttBridge serialMqttBride) throws Exception {
    if (instance == null) {
      instance = new MqttHandler(serialMqttBride);
    }
    return instance;
  }

  /*********************************************************************************************************************************************************************
   *
   */
  private void connectAndSubscribe() throws Exception {

    ConfigHandler configHandler = serialMqttBridge.getConfigHandler();

    mqttClient = new MqttClient(configHandler.getMqttBrokerUrl(), configHandler.getMqttClientId(), null);
    MqttConnectOptions connOpts = new MqttConnectOptions();
    connOpts.setCleanSession(true);
    connOpts.setAutomaticReconnect(true);

    // Authentication
    if (configHandler.getMqttBrokerUsername() != null && configHandler.getMqttBrokerPassword() != null) {
      connOpts.setUserName(configHandler.getMqttBrokerUsername());
      connOpts.setPassword(configHandler.getMqttBrokerPassword().toCharArray());
    }

    // MqttCallback
    mqttCallback = new MqttSubscriptionCallback(this);
    mqttClient.setCallback(mqttCallback);
    mqttClient.connect(connOpts);
  }

  public void publishMessage( String message) {

    MqttMessage mqttMessage = new MqttMessage(message.getBytes());
    mqttMessage.setQos(serialMqttBridge.getConfigHandler().getMqttQosPublish());

    String configuredTopicPublish = serialMqttBridge.getConfigHandler().getMqttTopicPublish();

      /*
     * If defined: Log outgoing MQTT message
     */
    if (serialMqttBridge.getConfigHandler().logMqttOutbound()) {
      logger.info("MQTT/Out: " + configuredTopicPublish + " | " + message);
    }

    /*
     * Publish MQTT message to broker
     */

    try {
      mqttClient.publish(configuredTopicPublish, mqttMessage);
    }
    catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  /*********************************************************************************************************************************************************************
   *
   */
  public void processMessage(String topic, MqttMessage message) {

    if (!serialMqttBridge.isInitialized()) {
      return;
    }

    /*
     * If defined: Log message content
     */
    if (serialMqttBridge.getConfigHandler().logMqttInbound()) {
      logger.info("MQTT/IN: " + topic + " | " + new String(message.getPayload()));
    }

    String serialMessage = "";


    /*
     * Ask cn.edu.nju.serial handler to send message
     */
    serialMqttBridge.getSerialHandler().sendMessage(serialMessage);
  }
}
