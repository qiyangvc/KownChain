package knowchain.server.service;

import knowchain.pojo.DTO.UserDTO;
import knowchain.pojo.DTO.UserRegisterDTO;
import knowchain.pojo.entity.User;

public interface UserService {

    /**
     * 登录
     * @param userDTO
     * @return
     */
    User login(UserDTO userDTO);

    /**
     * 用户注册
     * @param userRegisterDTO
     * @return
     */
    void register(UserRegisterDTO userRegisterDTO);

    /**
     * 重置密码请求
     * @param mailbox
     * @return
     */
    void requestReset(String mailbox);
}
