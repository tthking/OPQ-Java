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
public class FlashPicMessage extends BaseMessage {

    public final long size;
    public final String url;
    public final String md5;
    public final byte[] img;

    @SneakyThrows
    public FlashPicMessage(JSONObject data, long msgid, long random) {
        super(msgid, random, System.currentTimeMillis(), new User() {
        });
        data = data.containsKey("GroupPic") ? data.getJSONArray("GroupPic").getJSONObject(0) : data.getJSONArray("FriendPic").getJSONObject(0);
        this.url = data.getString("Url");
        this.md5 = data.getString("FileMd5");
        this.size = data.getLongValue("FileSize");
        this.img = data.containsKey("ForwordBuf") ? Base64.getDecoder().decode(data.getString("ForwordBuf")) : IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
    }

    @SneakyThrows
    public FlashPicMessage(String url) {
        super(0, 0, System.currentTimeMillis(), new Friend(OPQGlobal.getQq()));
        this.url = url;
        this.img = IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
        this.md5 = DigestUtils.md5Hex(this.img);
        this.size = this.img.length;
    }

    @SneakyThrows
    public FlashPicMessage(File file) {
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
