package knowchain.server.service.impl;

import knowchain.common.constant.MessageConstant;
import knowchain.common.exception.AccountNotFoundException;
import knowchain.common.exception.PasswordErrorException;
import knowchain.pojo.DTO.UserDTO;
import knowchain.pojo.entity.User;
import knowchain.server.mapper.UserMapper;
import knowchain.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(UserDTO userDTO){
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        User user = userMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对）
        if(user == null){
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对
        // 进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(password);
        if(!password.equals(user.getPassword())){
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 返回实体对象
        return user;
    }
}
