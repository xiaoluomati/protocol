package cn.edu.nju.writer;

import cn.edu.nju.mqtt.MqttHandler;

public class MqttWriter extends Thread {

    private MqttHandler mqttHandler;

    public MqttWriter(MqttHandler mqttHandler) {
        this.mqttHandler = mqttHandler;
    }

    @Override
    public void run() {
        while (true){
            DataCache dataCache = DataCache.getInstance();
            String dev1 = dataCache.get("Temperature");
            dataCache.clear("Temperature");
            if(!dev1.isEmpty()){
                mqttHandler.publishMessage(dev1);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
