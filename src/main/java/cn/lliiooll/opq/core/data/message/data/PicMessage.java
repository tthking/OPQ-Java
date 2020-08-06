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
import java.util.Base64;

@EqualsAndHashCode(callSuper = true)
@Data
public class PicMessage extends BaseMessage {

    public final long size;
    public final String url;
    public final String md5;
    public byte[] img;

    @SneakyThrows
    public PicMessage(JSONObject data, long msgid, long random, long time, User sender) {
        super(msgid, random, time, sender);
        data = data.containsKey("GroupPic") ? data.getJSONArray("GroupPic").getJSONObject(0) : (data.containsKey("FriendPic") ? data.getJSONArray("FriendPic").getJSONObject(0) : data);
        this.url = data.getString("Url").replace("\\\\", "\\");
        this.md5 = data.getString("FileMd5");
        this.size = data.getLongValue("FileSize");
        this.img = data.containsKey("ForwordBuf") ? Base64.getDecoder().decode(data.getString("ForwordBuf")) : new byte[0];
    }


    @SneakyThrows
    public byte[] getImg() {
        this.img = Arrays.equals(this.img, new byte[0]) ? IOUtils.toByteArray(new URL(url).openConnection().getInputStream()) : this.img;
        return this.img;
    }

    @SneakyThrows
    public PicMessage(String url) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
        this.url = url;
        this.img = IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
        this.md5 = DigestUtils.md5Hex(this.img);
        this.size = this.img.length;
    }

    @SneakyThrows
    public PicMessage(File file) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
        this.url = "";
        this.img = FileUtils.readFileToByteArray(file);
        this.md5 = DigestUtils.md5Hex(this.img);
        this.size = this.img.length;
    }

    @Override
    public String messageToString() {
        return "[图片]";
    }
}
