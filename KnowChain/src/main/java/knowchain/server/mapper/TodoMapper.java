package knowchain.server.mapper;

import knowchain.pojo.entity.TodoTable;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

@Mapper
public interface TodoMapper {

    /* 增 */
    // 插入一条待办
    @Insert("INSERT INTO todotable (tdDate, tdContent, tdStartTime, tdEndTime, userID) VALUES (#{tdDate}, #{tdContent}, #{tdStartTime}, #{tdEndTime}, #{userID})")
    void insert(TodoTable todo);

    /* 删 */
    // 按tdid删除一条待办
    @Delete("DELETE FROM todotable where tdID = #{tdid}")
    void deleteById(@Param("tdid") BigInteger tdid);

    /* 查 */
    // 按tdid获取一条待办
    @Select("SELECT * FROM todotable WHERE tdID = #{tdid}")
    TodoTable getById(@Param("tdid") BigInteger tdid);
    // 按userid和日期获取代办队列
    @Select("SELECT * FROM todotable WHERE userID = #{userid} AND tdDate = #{tddate}")
    List<TodoTable> getByUserIdAndDate(@Param("userid") BigInteger userid, @Param("tddate") Date tddate);

    /* 改 */
    // 更新一条待办
    @Update("UPDATE todotable SET tdDate = #{tdDate}, tdContent = #{tdContent}, tdStartTime = #{tdStartTime}, tdEndTime = #{tdEndTime} WHERE tdID = #{tdID}")
    void update(TodoTable todo);


}
