package Net;

import java.net.InetAddress;

public interface IConnection {

    void start();

    void stop();

    void connectClient();

    void sendMessage(String message);

    boolean isConnected();

    InetAddress getAddress();

    int getPort();

    String receiveMessage();

}
