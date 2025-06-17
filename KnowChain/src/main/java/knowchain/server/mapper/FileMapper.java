package knowchain.server.mapper;

import knowchain.pojo.entity.FileAndDirTable;
import org.apache.ibatis.annotations.*;

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

    // 根据userID, fName选择表
    @Select("SELECT * FROM fileanddirtable WHERE userID = #{userid} AND fName = #{fname}")
    List<FileAndDirTable> getByUIDandFName(@Param("userid") BigInteger userid,
                                           @Param("fname") String fname);

    // 根据userID, fName, parentFID(非NULL)选择表(保证只有一条记录)
    @Select("SELECT * FROM fileanddirtable WHERE userID = #{userid} AND fName = #{fname} AND parentFID = #{pid}")
    FileAndDirTable getByUIDandFNameandParentFID(@Param("userid") BigInteger userid,
                                                 @Param("fname") String fname,
                                                 @Param("pid") BigInteger parentFID);

    // 根据userID, fName, parentFID(NULL)选择表(保证只有一条记录)
    @Select("SELECT * FROM fileanddirtable WHERE userID = #{userid} AND fName = #{fname} AND parentFID IS NULL")
    FileAndDirTable getByUIDandFNameandNULLParentFID(@Param("userid") BigInteger userid,
                                                     @Param("fname") String fname);

    // 根据parentFID选择表,相当于将文件夹下所有文件/文件夹进行选择
    @Select("SELECT * FROM fileanddirtable WHERE parentFID = #{parentfid}")
    List<FileAndDirTable> getByParentFID(@Param("parentfid") BigInteger parentfid);


    // 上传文件后插入一条
    @Insert("INSERT INTO fileanddirtable (fName, URL, parentFID, isDir, userID) VALUES (#{fname}, #{url}, #{parentfid}, #{isdir}, #{userid})")
    void insert(@Param("fname") String fname,
                @Param("url") String url,
                @Param("parentfid") BigInteger parentfid,
                @Param("isdir") boolean isdir,
                @Param("userid") BigInteger userid);
    @Insert("INSERT INTO fileanddirtable (fName, parentFID, isDir, userID) VALUES (#{fname}, #{parentfid}, #{isdir}, #{userid})")
    void insertWithoutURL(@Param("fname") String fname,
                          @Param("parentfid") BigInteger parentfid,
                          @Param("isdir") boolean isdir,
                          @Param("userid") BigInteger userid);


    // 删除
    @Delete("DELETE FROM fileanddirtable where fID = #{fid}")
    void deleteByFid(@Param("fid") BigInteger fid);


    // 修改
    @Update("UPDATE fileanddirtable SET fName = #{fname} WHERE fID = #{fid}")
    void updatefName(@Param("fname") String fname,
                     @Param("fid") BigInteger fid);
    @Update("UPDATE fileanddirtable SET URL = #{url} WHERE fID = #{fid}")
    void updateURL(@Param("url") String url,
                     @Param("fid") BigInteger fid);
    @Update("UPDATE fileanddirtable SET parentFID = #{pid} WHERE fID = #{fid}")
    void updateParentFID(@Param("pid") BigInteger parentFID,
                     @Param("fid") BigInteger fid);


}