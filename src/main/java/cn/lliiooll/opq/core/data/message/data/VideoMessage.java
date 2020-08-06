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
import java.util.Base64;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoMessage extends BaseMessage {

    public final long size;
    public final String md5;
    public final byte[] video;

    @SneakyThrows
    public VideoMessage(JSONObject data, long msgid, long random, long time, User sender) {
        super(msgid, random, time, sender);
        this.md5 = data.getString("VideoMd5");
        this.size = data.getLongValue("VideoSize");
        this.video = Base64.getDecoder().decode(data.getString("ForwordBuf"));
    }

    @SneakyThrows
    public VideoMessage(String url) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
        this.video = IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
        this.md5 = DigestUtils.md5Hex(this.video);
        this.size = this.video.length;
    }

    @SneakyThrows
    public VideoMessage(File file) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
        this.video = FileUtils.readFileToByteArray(file);
        this.md5 = DigestUtils.md5Hex(this.video);
        this.size = this.video.length;
    }

    @Override
    public String messageToString() {
        return "[视频]";
    }
}
