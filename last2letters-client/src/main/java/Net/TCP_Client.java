package Net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TCP_Client extends Client {

    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;

    public TCP_Client(String host, int port) {
        super(host, port);
    }

    @Override
    public void start() {
        try {
            socket = new Socket(InetAddress.getByName(host), port);

            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public ApplicationLayer listen() {
        String result = ApplicationLayer.ERROR.code;
        try {
            result = din.readUTF();

            System.out.println("Received " + result);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ApplicationLayer.convert(result);
    }

    @Override
    public Message listenMessage() {
        ApplicationLayer al = ApplicationLayer.ERROR;
        String message = "";
        try {
            String result = din.readUTF();
            al = ApplicationLayer.convert(result.substring(0, 2));
            message = result.substring(2);

            System.out.println("Received " + al + " " + message);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new Message(al, message);
    }

    @Override
    public void sendRequest(ApplicationLayer al, String message) {
        try {
            dout.writeUTF(al.code + message);
            dout.flush();

            System.out.println("Sent " + al + " " + message);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public ApplicationLayer sendAndReceiveRequest(ApplicationLayer al, String message) {
        String result = ApplicationLayer.ERROR.code;
        try {
            dout.writeUTF(al.code + message);
            dout.flush();

            System.out.println("Sent " + al + " " + message);

            result = din.readUTF();

            System.out.println("Received " + result);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ApplicationLayer.convert(result);
    }
}
