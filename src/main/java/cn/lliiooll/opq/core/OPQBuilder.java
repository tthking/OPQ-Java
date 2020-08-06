package cn.lliiooll.opq.core;


public class OPQBuilder {

    private String url;
    private long qq;

    public static OPQBuilder builder() {
        return new OPQBuilder();
    }

    public OPQBuilder setURL(String url) {
        this.url = url;
        return this;
    }

    public OPQBuilder setQQ(long qq) {
        this.qq = qq;
        return this;
    }

    public OPQ build() {
        return new OPQ(url, qq);
    }

}
