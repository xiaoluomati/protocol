package cn.edu.nju.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@ClientEndpoint
public class WebsocketHandler{

    private Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);
    private static WebsocketHandler instance;
    Session userSession = null;
    private static final int MAX_CAP = 9;
    private List<String> msgList;

    public WebsocketHandler(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
            this.msgList = new ArrayList<>(MAX_CAP);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static WebsocketHandler getInstance() throws Exception{
        if (instance == null) {
            instance = new WebsocketHandler(new URI("ws://localhost:1551"));
        }
        return instance;
    }


    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket");
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("receive message : " + message);
    }

    public void sendMessage(String message) {
        logger.info("Websocket Out" + message);
        this.userSession.getAsyncRemote().sendText(message);
    }

    public void process(String message) {
        synchronized (this) {
            if (msgList.size() == MAX_CAP) {
                send();
            }
            msgList.add(message);
        }
    }

    private void send() {
        String message = preprocess();
        sendMessage(message);
    }


    private String preprocess() {
//        List<Double> tempList = new ArrayList<>();
//        for (String msg : msgList) {
//            JSONObject jsonObject = JSONObject.parseObject(msg);
//            Double light = jsonObject.getDouble("data");
//            tempList.add(light);
//        }
//        tempList.sort(Double::compareTo);
//        double middle = tempList.get(MAX_CAP / 2);
//        msgList.clear();
//        LightData lightData = new LightData(LocalDateTime.now(), middle);
//        return JSONObject.toJSONString(lightData);
        return "testdata";
    }
}