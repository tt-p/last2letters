package Net;

public abstract class Client implements IClient {

    protected String host;
    protected int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void sendRequest(ApplicationLayer al) {
        sendRequest(al, "");
    }

    public ApplicationLayer sendAndReceiveRequest(ApplicationLayer al) {
        return sendAndReceiveRequest(al, "");
    }

}
