package knowchain.server.mapper;

import knowchain.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from usertable where username = #{username}")
    User getByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     * @param email
     * @return
     */
    @Select("select * from usertable where mailbox = #{email}")
    User getByEmail(@Param("email") String email);

    /**
     * 插入新用户
     * @param user
     */
    @Insert("insert into usertable(username, password, mailbox) values(#{username}, #{password}, #{mailbox})")
    void insert(User user);
}
