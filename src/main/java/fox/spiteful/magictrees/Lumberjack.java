package fox.spiteful.magictrees;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class Lumberjack {
    public static final Logger logger = LogManager.getLogger("Magic Trees");
    public static void log(Level level, Throwable e, String message) {
        log(level, message);
        e.printStackTrace();
    }
    public static void log(Level level, String message) {
        logger.log(level, message);
    }
}