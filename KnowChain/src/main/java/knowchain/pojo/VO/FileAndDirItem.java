package knowchain.pojo.VO;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.jdbc.Null;

import java.io.File;
import java.util.List;

// 文件和文件夹树型列表项
public class FileAndDirItem {

    @Getter private long fid; // 文件描述符
    @Getter private String fName; // 文件名
    @Getter private String URL; // 文件URL
    @Getter private boolean isDir; // 是否为文件夹
    @Getter @Setter private List<FileAndDirItem> children; // 孩子文件

    // 构造方法
    public FileAndDirItem(long fid, String fName, String URL, boolean isDir, List<FileAndDirItem> children){
        this.fid = fid;
        this.fName = fName;
        this.URL = URL;
        this.isDir = isDir;
        this.children = children;
    }

    // 构造方法-未定孩子文件重载
    public FileAndDirItem(long fid, String fName, String URL, boolean isDir){
        this.fid = fid;
        this.fName = fName;
        this.URL = URL;
        this.isDir = isDir;
    }

}
