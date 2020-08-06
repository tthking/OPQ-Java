package cn.lliiooll.opq.core.keeplive;

public class AsyncKeepLive {
    private static long send = 0L;
    private static long replay = 0L;

    public static void waitReplay(long time) {
        send = time;
    }

    public static void replay(long time) {
        replay = time;
    }

    public static String getTimeout() {
        long timeout = (replay - send);
        return timeout + "ms";
    }
}
