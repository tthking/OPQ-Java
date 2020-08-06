package cn.lliiooll.opq.core.data.message.data;

import cn.lliiooll.opq.core.OPQGlobal;
import cn.lliiooll.opq.core.data.user.Friend;
import cn.lliiooll.opq.core.data.user.User;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

@EqualsAndHashCode(callSuper = true)
@Data
public class VoiceMessage extends BaseMessage {

    public final long size;
    public final String url;
    public final String md5;
    public byte[] voice;

    @SneakyThrows
    public VoiceMessage(JSONObject data, long msgid, long random, long time, User sender) {
        super(msgid, random, time, sender);
        this.url = data.getString("Url");
        this.voice = new byte[0];
        this.md5 = DigestUtils.md5Hex(this.voice);
        this.size = this.voice.length;
    }

    @SneakyThrows
    public byte[] getVoice() {
        this.voice = Arrays.equals(this.voice, new byte[0]) ? IOUtils.toByteArray(new URL(url).openConnection().getInputStream()) : this.voice;
        return this.voice;
    }

    @SneakyThrows
    public VoiceMessage(String url) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
        this.url = url;
        this.voice = IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
        this.md5 = DigestUtils.md5Hex(this.voice);
        this.size = this.voice.length;
    }

    @SneakyThrows
    public VoiceMessage(File file) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
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
