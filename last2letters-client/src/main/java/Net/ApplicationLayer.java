package Net;

public enum ApplicationLayer {

    HANDSHAKE("10"),
    REGISTER("11"),
    PLAY("12"),
    CANCEL("13"),
    QUIT("14"),
    OK("20"),
    CONNECTED("21"),
    START("31"),
    TURN("32"),
    ACTION("33"),
    LEAVE("34"),
    END("35"),
    ERROR("51");

    public String code;

    ApplicationLayer(String s) {
        this.code = s;
    }

    public static ApplicationLayer convert(String str) {
        switch (str) {
            case "10":
                return HANDSHAKE;
            case "11":
                return REGISTER;
            case "12":
                return PLAY;
            case "13":
                return CANCEL;
            case "14":
                return QUIT;
            case "20":
                return OK;
            case "21":
                return CONNECTED;
            case "31":
                return START;
            case "32":
                return TURN;
            case "33":
                return ACTION;
            case "34":
                return LEAVE;
            case "35":
                return END;
            case "51":
                return ERROR;
        }
        return ERROR;
    }
}