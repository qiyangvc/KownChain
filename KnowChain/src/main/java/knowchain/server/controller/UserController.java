package knowchain.server.controller;

import knowchain.common.result.Result;
import knowchain.pojo.DTO.UserDTO;
import knowchain.pojo.VO.UserVO;
import knowchain.pojo.entity.User;
import knowchain.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        // TODO 登录成功后，生成jwt令牌




        UserVO userVO = UserVO.builder()
                .userid(user.getUserid())
                .username(user.getUsername())
                .mailbox(user.getMailbox())
                .build();

        return Result.success(userVO);

    }
}
