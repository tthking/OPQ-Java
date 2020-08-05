package cn.lliiooll.iotqq.core;


public class IOTQQBuilder {

    private String url;
    private long qq;

    public static IOTQQBuilder builder() {
        return new IOTQQBuilder();
    }

    public IOTQQBuilder setURL(String url) {
        this.url = url;
        return this;
    }

    public IOTQQBuilder setQQ(long qq) {
        this.qq = qq;
        return this;
    }

    public IOTQQ build() {
        return new IOTQQ(url, qq);
    }

}
