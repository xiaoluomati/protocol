package cn.edu.nju;

import cn.edu.nju.config.ConfigHandler;
import cn.edu.nju.mqtt.MqttHandler;
import cn.edu.nju.serial.SerialHandler;
import cn.edu.nju.websocket.WebsocketHandler;
import cn.edu.nju.writer.MqttWriter;
import cn.edu.nju.writer.SocketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialMqttBridge {

  Logger logger = LoggerFactory.getLogger(SerialMqttBridge.class);

  private ConfigHandler configHandler;
  private SerialHandler serialHandler;
  private MqttHandler mqttHandler;
  private WebsocketHandler websocketHandler;

  private boolean isInitialized = false;

  /*********************************************************************************************************************************************************************
   * @param args
   */
  public static void main(String[] args) {
    new SerialMqttBridge().launch();
  }

  /*********************************************************************************************************************************************************************
   *
   */
  public SerialMqttBridge() {}

  /*********************************************************************************************************************************************************************
   *
   */
  public void launch() {

    try {

      // Get cn.edu.nju.config handler instance
      logger.info("Reading configuration file");
      configHandler = ConfigHandler.getInstance();

      // Instantiate preprocessor plugins if defined in cn.edu.nju.config
      logger.info("Searching for plugins");

      // Establish cn.edu.nju.serial connection
      logger.info("Creating cn.edu.nju.serial handler and establish connection");
      serialHandler = SerialHandler.getInstance(this);

      // Establish MQTT connection
      logger.info("Creating MQTT handler and establish connection");
      mqttHandler = MqttHandler.getInstance(this);

      websocketHandler = WebsocketHandler.getInstance();

      MqttWriter mqttWriter = new MqttWriter(mqttHandler);
      mqttWriter.start();

      SocketWriter socketWriter = new SocketWriter(websocketHandler);
      socketWriter.start();


      isInitialized = true;
    }
    catch (Exception e) {
      logger.error("An error occured.", e);
      System.exit(1);
    }

  }

  /*********************************************************************************************************************************************************************
   * @return
   */
  public ConfigHandler getConfigHandler() {
    return configHandler;
  }

  /*********************************************************************************************************************************************************************
   * @return
   */
  public SerialHandler getSerialHandler() {
    return serialHandler;
  }

  /*********************************************************************************************************************************************************************
   * @return
   */
  public MqttHandler getMqttHandler() {
    return mqttHandler;
  }

  /*********************************************************************************************************************************************************************
   * @return flag whether bridge is initialized and ready for message processing
   */
  public boolean isInitialized() {
    return isInitialized;
  }
}
