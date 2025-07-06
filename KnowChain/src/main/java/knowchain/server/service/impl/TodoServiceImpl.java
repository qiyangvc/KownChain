package knowchain.server.service.impl;

import knowchain.common.result.Result;
import knowchain.pojo.VO.TODOItem;
import knowchain.pojo.entity.TodoTable;
import knowchain.server.mapper.TodoMapper;
import knowchain.server.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static knowchain.common.constant.MessageConstant.*;

@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoMapper todoMapper;

    @Override
    public Result<String> addTodoImpl(String date,
                                      String content,
                                      String startTime,
                                      String endTime,
                                      BigInteger userid) {
        // 参数校验
        if (date == null || date.trim().isEmpty()) {
            return Result.error(TDDATE_EMPTY_ERROR);
        }
        if (content == null || content.trim().isEmpty()) {
            return Result.error(TDCONTENT_EMPTY_ERROR);
        }
        try {
            Date sqlDate = Date.valueOf(date);
            Time sqlStartTime = Time.valueOf(startTime);
            Time sqlEndTime = Time.valueOf(endTime);

            TodoTable todo = new TodoTable();
            todo.setTdDate(sqlDate);
            todo.setTdContent(content);
            todo.setTdStartTime(sqlStartTime);
            todo.setTdEndTime(sqlEndTime);
            todo.setUserID(userid);

            todoMapper.insert(todo);
            return Result.success(TODO_CREATE_SUCCESS);
        } catch (Exception e) {
            return Result.error("新增待办失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> deleteTodoImpl(BigInteger tdid) {
        if (tdid == null) {
            return Result.error(TDID_EMPTY_ERROR);
        }
        try {
            todoMapper.deleteById(tdid);
            return Result.success(TODO_DELETE_SUCCESS);
        } catch (Exception e) {
            return Result.error("删除待办失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> modifyTodoImpl(BigInteger tdid,
                                         String date,
                                         String content,
                                         String startTime,
                                         String endTime) {
        if (tdid == null) {
            return Result.error(TDID_EMPTY_ERROR);
        }
        try {
            TodoTable todo = todoMapper.getById(tdid);
            if (todo == null) {
                return Result.error(TODO_NOT_FOUND);
            }
            if (date != null) {
                todo.setTdDate(Date.valueOf(date));
            }
            if (content != null) {
                todo.setTdContent(content);
            }
            if (startTime != null) {
                todo.setTdStartTime(Time.valueOf(startTime));
            }
            if (endTime != null) {
                todo.setTdEndTime(Time.valueOf(endTime));
            }
            todoMapper.update(todo);
            return Result.success(TODO_MODIFY_SUCCESS);
        } catch (Exception e) {
            return Result.error("修改待办失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<TODOItem>> getTodo(BigInteger userid, String date) {
        if (userid == null || date == null || date.trim().isEmpty()) {
            return Result.error(ARGUMENT_EMPTY_ERROR);
        }
        try {
            Date sqlDate = Date.valueOf(date);
            List<TodoTable> todoList = todoMapper.getByUserIdAndDate(userid, sqlDate);
            List<TODOItem> result = new ArrayList<>();
            for (TodoTable todo : todoList) {
                TODOItem item = TODOItem.builder()
                        .tdID(todo.getTdID())
                        .tdDate(new java.sql.Date(todo.getTdDate().getTime()))
                        .tdContent(todo.getTdContent())
                        .tdStartTime(todo.getTdStartTime())
                        .tdEndTime(todo.getTdEndTime())
                        .build();
                result.add(item);
            }
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取待办失败：" + e.getMessage());
        }
    }
}