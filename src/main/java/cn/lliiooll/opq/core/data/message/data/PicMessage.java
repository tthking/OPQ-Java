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
public class PicMessage implements Message {

    public final long size;
    public final String url;
    public final String md5;
    public final byte[] img;

    @SneakyThrows
    public PicMessage(JSONObject data) {
        data = data.containsKey("GroupPic") ? data.getJSONArray("GroupPic").getJSONObject(0) : (data.containsKey("FriendPic") ? data.getJSONArray("FriendPic").getJSONObject(0) : data);
        this.url = data.getString("Url").replace("\\\\", "\\");
        this.md5 = data.getString("FileMd5");
        this.size = data.getLongValue("FileSize");
        this.img = data.containsKey("ForwordBuf") ? Base64.getDecoder().decode(data.getString("ForwordBuf")) : IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
    }

    @SneakyThrows
    public PicMessage(String url) {
        this.url = url;
        this.img = IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
        this.md5 = DigestUtils.md5Hex(this.img);
        this.size = this.img.length;
    }

    @SneakyThrows
    public PicMessage(File file) {
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
