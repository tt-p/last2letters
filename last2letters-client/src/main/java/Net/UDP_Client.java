package Net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP_Client extends Client {

    private DatagramSocket socket;

    public UDP_Client(String host, int port) {
        super(host, port);
    }

    @Override
    public void start() {
        try {
            socket = new DatagramSocket();
            ApplicationLayer al = sendAndReceiveRequest(ApplicationLayer.HANDSHAKE);

            Message message = listenMessage();

            if (message.getAl() == ApplicationLayer.OK) {
                port = Integer.parseInt(message.getMessage());
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() {
        socket.close();
    }

    @Override
    public ApplicationLayer listen() {
        String result = ApplicationLayer.ERROR.code;
        try {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            result = new String(packet.getData(), 0, packet.getLength());

            System.out.println("listen " + result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ApplicationLayer.convert(result);
    }

    @Override
    public Message listenMessage() {
        ApplicationLayer al = ApplicationLayer.ERROR;
        String message = "";
        try {
            byte[] buf = new byte[256];

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String result = new String(packet.getData(), 0, packet.getLength());

            al = ApplicationLayer.convert(result.substring(0, 2));
            message = result.substring(2);

            System.out.println("listenMessage " + result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Message(al, message);
    }

    @Override
    public void sendRequest(ApplicationLayer al, String message) {
        try {
            String strSend = al.code + message;

            byte[] buf = strSend.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(host), port);
            socket.send(packet);

            System.out.println("SendRequest " + al + " " + message);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public ApplicationLayer sendAndReceiveRequest(ApplicationLayer al, String message) {
        String result = ApplicationLayer.ERROR.code;
        try {
            String strSend = al.code + message;

            byte[] buf = strSend.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(host), port);
            socket.send(packet);

            System.out.println("sendAndReceiveRequest send " + al + " " + message);

            socket.receive(packet);

            result = new String(packet.getData(), 0, packet.getLength());

            System.out.println("sendAndReceiveRequest received " + result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ApplicationLayer.convert(result);
    }
}
