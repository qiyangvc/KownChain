package knowchain.server.service.impl;

import knowchain.common.constant.MessageConstant;
import knowchain.common.exception.*;
import knowchain.pojo.DTO.UserDTO;
import knowchain.pojo.DTO.UserRegisterDTO;
import knowchain.pojo.entity.User;
import knowchain.server.mapper.UserMapper;
import knowchain.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Pattern;

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
        }        // 返回实体对象
        return user;
    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        String username = userRegisterDTO.getUsername();
        String password = userRegisterDTO.getPassword();
        String confirmPassword = userRegisterDTO.getConfirmPassword();
        String mailbox = userRegisterDTO.getMailbox();

        // 1. 参数校验
        if (username == null || username.trim().isEmpty()) {
            throw new BaseException(MessageConstant.USERNAME_EMPTY);
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BaseException(MessageConstant.PASSWORD_EMPTY);
        }
        if (mailbox == null || mailbox.trim().isEmpty()) {
            throw new BaseException(MessageConstant.EMAIL_EMPTY);
        }

        // 2. 验证密码一致性
        if (!password.equals(confirmPassword)) {
            throw new PasswordMismatchException(MessageConstant.PASSWORD_MISMATCH);
        }

        // 3. 验证邮箱格式
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!Pattern.matches(emailRegex, mailbox)) {
            throw new BaseException(MessageConstant.EMAIL_FORMAT_ERROR);
        }

        // 4. 检查用户名是否已存在
        User existingUserByUsername = userMapper.getByUsername(username);
        if (existingUserByUsername != null) {
            throw new UsernameExistException(MessageConstant.USERNAME_EXIST);
        }

        // 5. 检查邮箱是否已存在
        User existingUserByEmail = userMapper.getByMailbox(mailbox);
        if (existingUserByEmail != null) {
            throw new EmailExistException(MessageConstant.EMAIL_EXIST);
        }

        // 6. 创建新用户
        User newUser = new User();
        newUser.setUsername(username);
        // MD5加密密码
        newUser.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        newUser.setMailbox(mailbox);

        // 7. 保存到数据库
        userMapper.insert(newUser);
    }
}
