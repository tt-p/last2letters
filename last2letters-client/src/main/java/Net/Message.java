package Net;

public class Message {

    private final ApplicationLayer al;
    private final String message;

    public Message(ApplicationLayer al, String message) {
        this.al = al;
        this.message = message;
    }

    public ApplicationLayer getAl() {
        return al;
    }

    public String getMessage() {
        return message;
    }

}
