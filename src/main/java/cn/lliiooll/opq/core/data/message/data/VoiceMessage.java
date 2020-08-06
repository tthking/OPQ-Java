package cn.lliiooll.opq.core.data.message.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.net.URL;

@Data
public class VoiceMessage implements Message {

    public final long size;
    public final String url;
    public final String md5;
    public final byte[] voice;

    @SneakyThrows
    public VoiceMessage(JSONObject data) {
        this.url = data.getString("Url");
        this.voice = IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
        this.md5 = DigestUtils.md5Hex(this.voice);
        this.size = this.voice.length;
    }

    @SneakyThrows
    public VoiceMessage(String url) {
        this.url = url;
        this.voice = IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
        this.md5 = DigestUtils.md5Hex(this.voice);
        this.size = this.voice.length;
    }

    @SneakyThrows
    public VoiceMessage(File file) {
        this.url = "";
        this.voice = FileUtils.readFileToByteArray(file);
        this.md5 = DigestUtils.md5Hex(this.voice);
        this.size = this.voice.length;
    }

    @Override
    public String messageToString() {
        return "[语音]";
    }
}
