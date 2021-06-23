package Net;

public interface IClient {

    void start();

    void stop();

    ApplicationLayer listen();

    Message listenMessage();

    void sendRequest(ApplicationLayer al, String message);

    ApplicationLayer sendAndReceiveRequest(ApplicationLayer al, String message);

}
