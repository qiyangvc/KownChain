package knowchain.server.service;

import knowchain.pojo.DTO.UserDTO;
import knowchain.pojo.entity.User;

public interface UserService {

    /**
     * 登录
     * @param userDTO
     * @return
     */
    User login(UserDTO userDTO);

}
