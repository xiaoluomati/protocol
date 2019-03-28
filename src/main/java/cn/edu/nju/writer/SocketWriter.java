package cn.edu.nju.writer;

import cn.edu.nju.websocket.WebsocketHandler;

public class SocketWriter extends Thread {

    private WebsocketHandler websocketHandler;

    public SocketWriter(WebsocketHandler websocketHandler) {
        this.websocketHandler = websocketHandler;
    }

    @Override
    public void run() {
        while (true){
            DataCache dataCache = DataCache.getInstance();
            String dev1 = dataCache.get("Light");
            dataCache.clear("Light");
            if(!dev1.isEmpty()){
                websocketHandler.sendMessage(dev1);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
