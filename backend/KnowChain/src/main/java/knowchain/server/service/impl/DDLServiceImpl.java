package knowchain.server.service.impl;


import knowchain.common.result.Result;
import knowchain.pojo.VO.DDLItem;
import knowchain.server.mapper.DDLMapper;
import knowchain.server.service.DDLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static knowchain.common.constant.MessageConstant.*;
import static knowchain.common.constant.TimeConstant.TimeStampPattern;

@Service
public class DDLServiceImpl implements DDLService {


    @Autowired
    private DDLMapper ddlMapper;


    @Override
    public Result<String> createDDL(String title, String notes, String endtime, BigInteger userid) throws NullPointerException, IllegalArgumentException {

        // 检查标题是否为空
        if(title == null || title.trim().isEmpty()) {
            return Result.error(DDL_TITLE_EMPTY);
        }

        // 生成当前时间作为创建时间
        Timestamp createTime = Timestamp.valueOf(LocalDateTime.now());

        // 解析结束时间字符串为Timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TimeStampPattern);
        LocalDateTime endDateTime = LocalDateTime.parse(endtime, formatter);
        Timestamp endTimeStamp = Timestamp.valueOf(endDateTime);

        // 检查结束时间是否早于当前时间
        if(endTimeStamp.before(createTime)) {
            return Result.error(DDL_END_TIME_ERROR);
        }

        // 插入DDL记录
        if(notes != null && !notes.trim().isEmpty()) {
            ddlMapper.insert(title, notes, createTime, endTimeStamp, userid);
        } else {
            ddlMapper.insertWithoutNotes(title, createTime, endTimeStamp, userid);
        }

        return Result.success(DDL_CREATE_SUCCESS);

    }


    @Override
    public Result<String> deleteDDL(BigInteger did) {

        // 检查DDL是否存在
        if(ddlMapper.getByDID(did) == null) {
            return Result.error(DDL_NOT_FOUND);
        }

        // 执行删除操作
        ddlMapper.deleteByDID(did);

        return Result.success(DDL_DELETE_SUCCESS);

    }


    @Override
    public Result<String> modifyDDL(BigInteger did, String title, String notes, String endtime) throws IllegalArgumentException, DateTimeParseException, NullPointerException {
        // 检查DDL是否存在
        DDLItem ddl = ddlMapper.getByDID(did);
        if (ddl == null) {
            return Result.error(DDL_NOT_FOUND);
        }

        // 更新标题(如果提供了新标题)
        if (title != null && !title.trim().isEmpty()) {
            if (title.length() > 255) { // 假设数据库字段长度为255
                return Result.error(DDL_TITLE_OUT_OF_LENGTH);
            }
            ddlMapper.updateTitle(title, did);
        }

        // 更新备注(如果提供了新备注)
        if (notes != null) {
            ddlMapper.updateNotes(notes, did);
        }

        // 更新截止时间(如果提供了新时间)
        if (endtime != null && !endtime.trim().isEmpty()) {
            try {
                // 解析新的截止时间
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TimeStampPattern);
                LocalDateTime endDateTime = LocalDateTime.parse(endtime, formatter);
                Timestamp endTimeStamp = Timestamp.valueOf(endDateTime);

                // 检查新截止时间是否合法(不能早于创建时间)
                if (endTimeStamp.before(ddl.getDCreateTime())) {
                    return Result.error(DDL_END_TIME_ERROR);
                }

                ddlMapper.updateEndTime(endTimeStamp, did);
            } catch (DateTimeParseException e) {
                return Result.error(TIMESTAMP_FORMAT_ERROR);
            }
        }

        return Result.success(DDL_MODIFY_SUCCESS);
    }


    public Result<List<DDLItem>> getDDLByUserID(BigInteger userid) {

        // 获取该用户的所有DDL
        List<DDLItem> ddlList = ddlMapper.getByUserID(userid);

        if(ddlList == null || ddlList.isEmpty()) {
            return Result.success(new ArrayList<>()); // 返回空列表
        }

        return Result.success(ddlList);

    }
}
