package cn.lliiooll.opq.core.queue;

import lombok.Data;

@Data
public class IRequest {
    private RequestFinished action;
    private String request;
    private String url;

    public IRequest(String url, String request, RequestFinished runnable) {
        this.url = url;
        this.request = request;
        this.action = runnable;
    }
}
