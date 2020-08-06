package cn.lliiooll.opq.core.queue;

import cn.lliiooll.opq.utils.TaskUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

public class IQueue {
    private static final ArrayBlockingQueue<IRequest> queue = new ArrayBlockingQueue<>(3);
    private static ExecutorService main = TaskUtils.create("QueueTask-%d");

    public static void init() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (!queue.isEmpty()) {
                        IRequest request = queue.poll();
                        if (request != null) {
                            try {
                                LogManager.getLogger().info("==============================");
                                LogManager.getLogger().info(request.getRequest());
                                LogManager.getLogger().info("==============================");
                                String reslut = new OkHttpClient.Builder()
                                        .build()
                                        .newCall(new Request.Builder()
                                                .url(request.getUrl())
                                                .post(RequestBody.create(request.getRequest(), MediaType.parse("application/json; charset=utf-8")))
                                                .build())
                                        .execute()
                                        .body()
                                        .string();
                                main.execute(() -> {
                                    LogManager.getLogger().info("++++++++++++++++++++++++++++++");
                                    LogManager.getLogger().info(reslut);
                                    LogManager.getLogger().info("++++++++++++++++++++++++++++++");
                                    request.getAction().onFinisher(reslut);
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }, "QueueThread").start();
    }

    public static void sendRequest(IRequest request) {
        main.execute(() -> {
            try {
                queue.put(request);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
