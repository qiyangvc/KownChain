package knowchain.server.service;

import knowchain.common.result.Result;
import knowchain.pojo.VO.TODOItem;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.util.List;

public interface TodoService {

    /**
     * 新增待办
     * @param date 日期
     * @param content 任务名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userid 用户id
     * @return 新增结果
     */
    Result<String> addTodoImpl(String date,
                               String content,
                               String startTime,
                               String endTime,
                               BigInteger userid);

    
    /**
     * 删除待办
     * @param tdid 任务id
     * @return 删除结果
     */
    Result<String> deleteTodoImpl(BigInteger tdid);


    /**
     * 修改待办
     * @param tdid 任务id
     * @param date 日期
     * @param content 任务名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 修改结果
     */
    Result<String> modifyTodoImpl(BigInteger tdid,
                                  String date,
                                  String content,
                                  String startTime,
                                  String endTime);

    /**
     * 获取待办列表
     * @param userid 用户id
     * @param date 日期
     * @return 待办列表
     */
    Result<List<TODOItem>> getTodo(BigInteger userid,
                                          String date);

}
