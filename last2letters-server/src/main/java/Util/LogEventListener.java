package Util;

import java.util.EventListener;

public interface LogEventListener extends EventListener {

    void HandleLog(LogEvent le);

}
