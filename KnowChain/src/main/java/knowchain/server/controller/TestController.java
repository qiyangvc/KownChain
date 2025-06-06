package knowchain.server.controller;


import knowchain.common.result.Result;
import knowchain.pojo.entity.User;
import knowchain.server.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {


    @GetMapping("/test")
    public String Test(){
        return "测试成功";
    }

    @GetMapping("/test1")
    public Result<String> Test1(){
        return Result.success("test1测试成功");
    }
}
