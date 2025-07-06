package knowchain.server.service.impl;

import knowchain.common.constant.MessageConstant;
import knowchain.common.exception.AccountNotFoundException;
import knowchain.common.exception.PasswordErrorException;
import knowchain.pojo.DTO.UserDTO;
import knowchain.pojo.DTO.UserRegisterDTO;
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
    }    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        // 1、检查用户名是否已存在
        User existingUser = userMapper.getByUsername(userRegisterDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2、检查邮箱是否已存在
        User existingEmailUser = userMapper.getByEmail(userRegisterDTO.getMailbox());
        if (existingEmailUser != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 3、检查密码长度
        if (userRegisterDTO.getPassword() == null || userRegisterDTO.getPassword().length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }

        // 4、检查密码和确认密码是否一致
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 5、创建新用户
        User newUser = new User();
        newUser.setUsername(userRegisterDTO.getUsername());
        newUser.setMailbox(userRegisterDTO.getMailbox());
        // 对密码进行MD5加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes());
        newUser.setPassword(encryptedPassword);

        // 6、保存到数据库
        userMapper.insert(newUser);
    }

    @Override
    public void requestReset(String mailbox) {
        // 1、检查邮箱是否存在
        User user = userMapper.getByEmail(mailbox);
        if (user == null) {
            throw new RuntimeException("邮箱不存在");
        }

        // 2、这里应该发送重置密码邮件
        // 暂时只是记录日志，实际项目中需要集成邮件服务
        System.out.println("向邮箱 " + mailbox + " 发送重置密码邮件");
        
        // TODO: 集成邮件服务发送重置密码链接
    }
}
