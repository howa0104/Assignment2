package logic;

//TODO this class is just a skeleton it must be completed
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class LogicFactory {

    private final static String PACKAGE = "logic.";
    private final static String SUFFIX = "Logic";

    private LogicFactory() {
    }

    public static <R> R getFor(String entityName) {
        try {
            return getFor((Class< R>) Class.forName(PACKAGE + entityName + SUFFIX));// need to figure out what to return
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex);
        }
    }

    public static < T> T getFor(Class<T> type) {

        try {
            Constructor<T> declaredConstructor = type.getDeclaredConstructor();
            return declaredConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
