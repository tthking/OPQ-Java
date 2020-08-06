package cn.lliiooll.opq.core.data.message.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.net.URL;
import java.util.Base64;

@Data
public class VideoMessage implements Message {

    public final long size;
    ;
    public final String md5;
    public final byte[] video;

    @SneakyThrows
    public VideoMessage(JSONObject data) {
        this.md5 = data.getString("VideoMd5");
        this.size = data.getLongValue("VideoSize");
        this.video = Base64.getDecoder().decode(data.getString("ForwordBuf"));
    }

    @SneakyThrows
    public VideoMessage(String url) {
        this.video = IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
        this.md5 = DigestUtils.md5Hex(this.video);
        this.size = this.video.length;
    }

    @SneakyThrows
    public VideoMessage(File file) {
        this.video = FileUtils.readFileToByteArray(file);
        this.md5 = DigestUtils.md5Hex(this.video);
        this.size = this.video.length;
    }

    @Override
    public String messageToString() {
        return "[视频]";
    }
}
