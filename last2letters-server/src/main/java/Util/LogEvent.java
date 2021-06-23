package Util;

import java.util.EventObject;

public class LogEvent extends EventObject {

    private final String message;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public LogEvent(Object source, String message) {
        super(source);
        this.message = message + "\n";
    }

    public LogEvent(Object source, String... messages) {
        super(source);
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message + "\n");
        }
        this.message = sb.toString();
    }

    public String getMessage() {
        return message;
    }
}
