package knowchain.server.controller;

import knowchain.common.constant.JwtClaimsConstant;
import knowchain.common.properties.JwtProperties;
import knowchain.common.result.Result;
import knowchain.common.utils.JwtUtil;
import knowchain.pojo.DTO.UserDTO;
import knowchain.pojo.DTO.UserRegisterDTO;
import knowchain.pojo.DTO.ResetPasswordDTO;
import knowchain.pojo.VO.UserVO;
import knowchain.pojo.entity.User;
import knowchain.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 登录
     * @param userDTO
     * @return
     */
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserDTO userDTO){

        System.out.println(userDTO);

        User user = userService.login(userDTO);

        System.out.println("controller: "+user);

        // 登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getUserid());
        JwtProperties jwtProperties = new JwtProperties();
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);


        UserVO userVO = UserVO.builder()
                .userid(user.getUserid())
                .username(user.getUsername())
                .mailbox(user.getMailbox())
                .token(token)
                .build();

        return Result.success(userVO);
    }

    /**
     * 用户注册
     * @param userRegisterDTO
     * @return
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterDTO userRegisterDTO){
        log.info("用户注册：{}", userRegisterDTO);
        
        try {
            userService.register(userRegisterDTO);
            return Result.success("注册成功");
        } catch (Exception e) {
            log.error("注册失败：{}", e.getMessage());
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    /**
     * 重置密码请求
     * @param resetPasswordDTO
     * @return
     */
    @PostMapping("/request-reset")
    public Result<String> requestReset(@RequestBody ResetPasswordDTO resetPasswordDTO){
        log.info("重置密码请求：{}", resetPasswordDTO);
        
        try {
            userService.requestReset(resetPasswordDTO.getMailbox());
            return Result.success("重置密码邮件已发送");
        } catch (Exception e) {
            log.error("重置密码请求失败：{}", e.getMessage());
            return Result.error("重置密码请求失败：" + e.getMessage());
        }
    }
}
