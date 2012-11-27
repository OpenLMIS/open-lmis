package org.openlmis;

public class LmisThreadLocal {

    public static final ThreadLocal lmisThreadLocal = new ThreadLocal();

    public static void set(String userName) {
        lmisThreadLocal.set(userName);
    }

    public static void unset() {
        lmisThreadLocal.remove();
    }

    public static Object get() {
        return lmisThreadLocal.get();
    }
}
