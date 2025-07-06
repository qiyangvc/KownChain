package knowchain.server.service;

import knowchain.common.result.Result;
import knowchain.pojo.VO.DDLItem;

import java.math.BigInteger;
import java.time.format.DateTimeParseException;
import java.util.List;

public interface DDLService {


    /**
     * 创建DDL
     * @param title DDL标题
     * @param notes DDL备注(可选)
     * @param endtime DDL截止时间
     * @param userid 用户ID
     * @return 创建结果
     */
    Result<String> createDDL(String title,
                             String notes,
                             String endtime,
                             BigInteger userid) throws NullPointerException, IllegalArgumentException;


    /**
     * 删除DDL
     * @param did DDL ID
     * @return 删除结果
     */
    Result<String> deleteDDL(BigInteger did);


    /**
     * 修改DDL信息
     * @param did DDL ID
     * @param title 新标题(可选)
     * @param notes 新备注(可选)
     * @param endtime 新截止时间(可选)
     * @return 修改结果
     */
    Result<String> modifyDDL(BigInteger did,
                             String title,
                             String notes,
                             String endtime) throws IllegalArgumentException, DateTimeParseException, NullPointerException;


    /**
     * 获取用户的DDL列表
     * @param userid 用户ID
     * @return DDL列表
     */
    Result<List<DDLItem>> getDDLByUserID(BigInteger userid);


}
