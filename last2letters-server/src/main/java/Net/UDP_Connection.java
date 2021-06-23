package Net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDP_Connection implements IConnection {

    private static final int startingPort = 15000;
    private static int portsCreated = 0;
    private static DatagramSocket serverSocket;
    private int port;
    private InetAddress address;
    private DatagramSocket socket;

    public UDP_Connection(int port) {
        this.port = port;
        start();
    }

    @Override
    public void start() {
        try {
            if (serverSocket == null) {
                serverSocket = new DatagramSocket(port);
            }
            if (socket == null) {
                socket = serverSocket;
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        socket.close();
        serverSocket.close();
    }

    @Override
    public void connectClient() {
        String result = ApplicationLayer.ERROR.code;

        try {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            serverSocket.receive(packet);
            result = new String(packet.getData(), 0, packet.getLength());

            if (ApplicationLayer.convert(result) == ApplicationLayer.HANDSHAKE) {

                address = packet.getAddress();
                port = packet.getPort();

                sendMessage(ApplicationLayer.HANDSHAKE.code);
                int newPort = startingPort + portsCreated++;
                sendMessage(ApplicationLayer.OK.code + newPort);

                socket = new DatagramSocket(newPort);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        try {
            byte[] buf = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String receiveMessage() {
        String result = ApplicationLayer.ERROR.code;
        try {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            result = new String(packet.getData(), 0, packet.getLength());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean isConnected() {
        return !socket.isClosed();
    }

    @Override
    public InetAddress getAddress() {
        return address;
    }

    @Override
    public int getPort() {
        return port;
    }

}
