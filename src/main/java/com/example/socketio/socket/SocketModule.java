package com.example.socketio.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.example.socketio.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketModule {

    private final SocketIOServer socketIOServer;


    public SocketModule(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
        socketIOServer.addConnectListener(onConnected());
        socketIOServer.addDisconnectListener(onDisconnected());
        socketIOServer.addEventListener("send_message", Message.class,onMessageReceived());
    }

    private DataListener<Message> onMessageReceived() {
        return (senderClient,data,ackSender)->{
            log.info(String.format("%s -> %s",senderClient.getSessionId(),data.getContent()));
            //grup halinde mesajlaşma
            String room =  senderClient.getHandshakeData().getSingleUrlParam("room");

            senderClient.getNamespace().getRoomOperations(room)
                    .getClients().forEach(
                            x ->{
                                if (!x.getSessionId().equals(senderClient.getSessionId())){
                                    x.sendEvent("get_message",data);
                                }
                            }
                            );

            //herkes ile mesajlaşma
            /*senderClient.getNamespace()
                    //.getBroadcastOperations() -> herkese mesajı yollar
                    .getAllClients()
                    .forEach(
                            x -> {
                                if (!x.getSessionId().equals(senderClient.getSessionId())){
                                    x.sendEvent("get_message",data.getContent());
                                }
                            }
                    );*/
        };
    }

    private DisconnectListener onDisconnected() {
        return client ->{
            String room = client.getHandshakeData().getSingleUrlParam("room");
            client.getNamespace().getRoomOperations(room).sendEvent("get_message",
                    String.format("%s disconnected from -> %s",client.getSessionId(),room));
            log.info(String.format("SocketId: %s disconnected",client.getSessionId().toString()));
        };
    }

    private ConnectListener onConnected() {
        return client ->{
            String room = client.getHandshakeData().getSingleUrlParam("room");
            client.joinRoom(room);
            client.getNamespace().getRoomOperations(room).sendEvent("get_message",
                    String.format("%s connected to -> %s",client.getSessionId(),room));
            log.info(String.format("SocketId: %s connected",client.getSessionId().toString()));
        };
    }
}
