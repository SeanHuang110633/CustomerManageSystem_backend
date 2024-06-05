package com.RM.manageSystem.utils;

public class ThreadLocalUtil {
    // 定義一個ThreadLocal實例，此實例是私有的(static final)常數。
    // 保證這個變數會是同一個實例在該類別中的所有地方。
    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    // 定義了一個靜態方法get，它返回當前線程的ThreadLocal變量的副本。
    // 不知道具體類型，使用了泛型<T>，並且強制轉型成(T)。
    public static <T> T get() { return (T) THREAD_LOCAL.get(); }

    // 定義了一個靜態方法set，它允許我們設置當前線程的ThreadLocal變量的值。
    // 參數是Object類型，因此可以接受任何類型的對象。
    public static void set(Object value) { THREAD_LOCAL.set(value); }

    // 定義了一個靜態方法remove，將當前線程的ThreadLocal變量的副本從線程的ThreadLocalMap中移除。
    // 在線程即將終結時調用，避免內存洩漏。
    public static void remove() { THREAD_LOCAL.remove(); }
}

