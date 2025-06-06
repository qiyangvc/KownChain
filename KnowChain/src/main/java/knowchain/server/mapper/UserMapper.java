package knowchain.server.mapper;

import knowchain.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from usertable where username = #{username}")
    User getByUsername(@Param("username") String username);
}
