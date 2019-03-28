package cn.edu.nju.serial;


import cn.edu.nju.SerialMqttBridge;
import cn.edu.nju.config.ConfigHandler;
import cn.edu.nju.writer.DataCache;
import jssc.SerialPort;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SerialHandler {

    private Logger logger = LoggerFactory.getLogger(SerialHandler.class);

    private static SerialHandler instance;
    private SerialMqttBridge serialMqttBridge;

    private SerialPort serialPort;


    private SerialHandler(SerialMqttBridge serialMqttBridge) throws Exception {
        this.serialMqttBridge = serialMqttBridge;
        establishSerialConnection();
    }

    public static SerialHandler getInstance(SerialMqttBridge serialMqttBridge) throws Exception {
        if (instance == null) {
            instance = new SerialHandler(serialMqttBridge);
        }
        return instance;
    }

    /*********************************************************************************************************************************************************************
     * @throws SerialPortException
     */
    private void establishSerialConnection() throws SerialPortException {

        ConfigHandler configHandler = serialMqttBridge.getConfigHandler();
        serialPort = new SerialPort(configHandler.getSerialPort());
        serialPort.openPort();
        serialPort.setParams(configHandler.getBaudRate(), configHandler.getDataBits(), configHandler.getStopBits(), configHandler.getParity());
        serialPort.addEventListener(new SerialPortListener(this));
    }

    /*********************************************************************************************************************************************************************
     * @return cn.edu.nju.serial port handle
     */
    public SerialPort getSerialPort() {
        return serialPort;
    }

    /*********************************************************************************************************************************************************************
     * @param message
     */
    public void processMessage(String message) {

        if (!serialMqttBridge.isInitialized()) {
            return;
        }

        /*
         * If defined: Log message content
         */
        if (serialMqttBridge.getConfigHandler().logSerialInbound()) {
            logger.info("Serial/IN: " + message);
        }
        DataCache cache = DataCache.getInstance();
        String reg = "(.*):(.*)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(message);
        if(matcher.find()){
            String s = matcher.group(2);
            String string = "data:" +
                    s +
                    ",time:" +
                    new Date().getTime() +
                    ";";
            cache.append(matcher.group(1), string);
        }
    }

    /*********************************************************************************************************************************************************************
     * @param serialMessage
     */
    public void sendMessage(String serialMessage) {

        /*
         * If defined: Log outgoing cn.edu.nju.serial message
         */
        if (serialMqttBridge.getConfigHandler().logSerialOutbound()) {
            logger.info("Serial/Out: " + serialMessage);
        }

        /*
         * Send out cn.edu.nju.serial message
         */
        try {
            serialPort.writeBytes((serialMessage + "\n").getBytes());
        } catch (SerialPortException e) {
            logger.error("Exception", e);
        }
    }
}
