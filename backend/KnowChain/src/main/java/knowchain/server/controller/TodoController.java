package knowchain.server.controller;

import knowchain.common.result.Result;
import knowchain.pojo.VO.TODOItem;
import knowchain.server.handler.GlobalExceptionHandler;
import knowchain.server.mapper.TodoMapper;
import knowchain.server.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/todo")
public class TodoController {

    @Autowired
    TodoService todoService;

    /*
        新增待办
     */
    @PostMapping("/add")
    public Result<String> addTodo(@RequestParam("tdDate") String date,
                                  @RequestParam("tdContent") String content,
                                  @RequestParam("tdStartTime") String startTime,
                                  @RequestParam("tdEndTime") String endTime,
                                  @RequestParam("userID") BigInteger userid) {
        try {
            return todoService.addTodoImpl(date, content, startTime, endTime, userid);
        } catch (Exception e) {
            return GlobalExceptionHandler.exceptionHandler(e);
        }
    }


    /*
        删除待办
     */
    @PutMapping("/delete")
    public Result<String> deleteTodo(@RequestParam("tdid") BigInteger tdid) {
        try {
            return todoService.deleteTodoImpl(tdid);
        } catch (Exception e) {
            return GlobalExceptionHandler.exceptionHandler(e);
        }
    }


    /*
        修改待办
     */
    @PutMapping("/modify")
    public Result<String> modifyTodo(@RequestParam("tdID") BigInteger tdid,
                                     @RequestParam(value = "tdDate", required = false) String date,
                                     @RequestParam(value = "tdContent", required = false) String content,
                                     @RequestParam(value = "tdStartTime", required = false) String startTime,
                                     @RequestParam(value = "tdEndTime", required = false) String endTime) {
        try {
            return todoService.modifyTodoImpl(tdid, date, content, startTime, endTime);
        } catch (Exception e) {
            return GlobalExceptionHandler.exceptionHandler(e);
        }
    }


    /*
        按用户ID和日期获取待办列表
     */
    @GetMapping("/getByUidAndDate")
    public Result<List<TODOItem>> getTodo(@RequestParam("userID") BigInteger userid,
                                          @RequestParam("tdDate") String date) {
        try {
            return todoService.getTodo(userid, date);
        } catch (Exception e) {
            return GlobalExceptionHandler.exceptionHandler(e);
        }
    }
}
