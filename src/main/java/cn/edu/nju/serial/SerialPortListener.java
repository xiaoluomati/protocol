package cn.edu.nju.serial;

import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialPortListener implements SerialPortEventListener {
    private Logger logger = LoggerFactory.getLogger(SerialPortListener.class);

    private SerialHandler serialHandler;
    private StringBuilder messageBuffer;

    public SerialPortListener(SerialHandler serialHandler) {
        this.serialHandler = serialHandler;
        messageBuffer = new StringBuilder();
    }

    @Override
    public void serialEvent(SerialPortEvent event) {

        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                byte buffer[] = serialHandler.getSerialPort().readBytes();
                for (byte b : buffer) {
                    if ((b == '\n') && messageBuffer.length() > 0) {
                        String message = messageBuffer.toString().replaceAll("\r", "");
                        serialHandler.processMessage(message);
                        messageBuffer.setLength(0);
                    } else {
                        messageBuffer.append((char)b);
                    }
                }
            } catch (SerialPortException ex) {
                logger.error("Exception", ex);
            }
        }
    }
}
