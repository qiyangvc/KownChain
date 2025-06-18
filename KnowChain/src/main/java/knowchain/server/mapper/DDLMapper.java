package knowchain.server.mapper;

import knowchain.pojo.VO.DDLItem;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface DDLMapper {

    // 插入一条DDL
    @Insert("INSERT INTO ddltable (dTitle, dNotes, dCreateTime, dEndTime, userID) VALUES (#{title}, #{notes}, #{createtime}, #{endtime}, #{userid})")
    void insert(@Param("title") String title,
                @Param("notes") String notes,
                @Param("createtime") Timestamp createtime,
                @Param("endtime") Timestamp endtime,
                @Param("userid") BigInteger userid);
    @Insert("INSERT INTO ddltable (dTitle, dCreateTime, dEndTime, userID) VALUES (#{title}, #{createtime}, #{endtime}, #{userid})")
    void insertWithoutNotes(@Param("title") String title,
                            @Param("createtime") Timestamp createtime,
                            @Param("endtime") Timestamp endtime,
                            @Param("userid") BigInteger userid);


    // 通过did选择
    @Select("SELECT * FROM ddltable WHERE dID = #{did}")
    DDLItem getByDID(@Param("did") BigInteger did);
    // 根据用户ID选择
    @Select("SELECT * FROM ddltable WHERE userID = #{userid} ORDER BY dEndTime ASC")
    List<DDLItem> getByUserID(@Param("userid") BigInteger userid);


    // 通过did删除一条ddl
    @Delete("DELETE FROM ddltable WHERE dID = #{did}")
    void deleteByDID(@Param("did") BigInteger did);


    // 更新DDL标题
    @Update("UPDATE ddltable SET dTitle = #{title} WHERE dID = #{did}")
    void updateTitle(@Param("title") String title, @Param("did") BigInteger did);
    // 更新DDL备注
    @Update("UPDATE ddltable SET dNotes = #{notes} WHERE dID = #{did}")
    void updateNotes(@Param("notes") String notes, @Param("did") BigInteger did);
    // 更新DDL截止时间
    @Update("UPDATE ddltable SET dEndTime = #{endtime} WHERE dID = #{did}")
    void updateEndTime(@Param("endtime") Timestamp endtime, @Param("did") BigInteger did);

}
