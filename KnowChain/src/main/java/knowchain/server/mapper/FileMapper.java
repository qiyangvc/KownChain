package knowchain.server.mapper;

import knowchain.pojo.entity.FileAndDirTable;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface FileMapper {

    // 根据fID选择表
    @Select("SELECT * FROM fileanddirtable WHERE fID = #{fid}")
    FileAndDirTable getByFID(@Param("fid") BigInteger fid);

    // 根据userID选择表
    @Select("SELECT * FROM fileanddirtable WHERE userID = #{userid}")
    List<FileAndDirTable> getByUserID(@Param("userid") BigInteger userid);

    // 上传文件后插入一条
    @Insert("INSERT INTO fileanddirtable (fName, URL, parentFID, isDir, userID) VALUES (#{fname}, #{url}, #{parentfid}, #{isdir}, #{userid})")
    void insert(@Param("fname") String fname,
                @Param("url") String url,
                @Param("parentfid") BigInteger parentfid,
                @Param("isdir") boolean isdir,
                @Param("userid") BigInteger userid);

}