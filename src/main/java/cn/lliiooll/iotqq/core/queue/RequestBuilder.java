package cn.lliiooll.iotqq.core.queue;

public class RequestBuilder {

    private RequestFinished action;
    private String request;
    private String url;

    public static RequestBuilder builder() {
        return new RequestBuilder();
    }

    public RequestBuilder setAction(RequestFinished runnable) {
        this.action = runnable;
        return this;
    }

    public RequestBuilder setRequest(String request) {
        this.request = request;
        return this;
    }

    public RequestBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public IRequest build() {
        return new IRequest(url, request, action);
    }
}
