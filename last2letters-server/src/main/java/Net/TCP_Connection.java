package Net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP_Connection implements IConnection {

    private static ServerSocket serverSocket;
    private final int port;
    private Socket socket;

    public TCP_Connection(int port) {
        this.port = port;
        start();
    }

    @Override
    public void start() {
        try {
            if (serverSocket == null) {
                serverSocket = new ServerSocket(port);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectClient() {
        try {
            socket = serverSocket.accept();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        try {
            DataOutputStream outCli = new DataOutputStream(socket.getOutputStream());
            outCli.writeUTF(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected() && !socket.isClosed();
    }

    @Override
    public InetAddress getAddress() {
        return socket.getInetAddress();
    }

    @Override
    public int getPort() {
        return socket.getPort();
    }

    @Override
    public String receiveMessage() {
        String result = ApplicationLayer.ERROR.code;
        try {
            DataInputStream inCli = new DataInputStream(socket.getInputStream());
            result = inCli.readUTF();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

}
