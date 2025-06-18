package knowchain.server.controller;


import knowchain.common.result.Result;
import knowchain.pojo.VO.DDLItem;
import knowchain.server.handler.GlobalExceptionHandler;
import knowchain.server.mapper.DDLMapper;
import knowchain.server.service.DDLService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ddl")
public class DDLController {

    @Autowired
    private DDLMapper ddlMapper;
    @Autowired
    private DDLService ddlService;


    /*

        创建DDL

     */
    @PostMapping("/addDDL")
    public Result<String> addDDL(@RequestParam("title") String title,
                                 @RequestParam(value = "notes", required = false) String notes,
                                 @RequestParam("userid") BigInteger userid,
                                 @RequestParam("endtime") String endtime){

        try{

            return ddlService.createDDL(title, notes, endtime, userid);

        } catch (Exception e){

            return GlobalExceptionHandler.exceptionHandler(e);

        }

    }


    /*

        删除DDL

     */
    @DeleteMapping("/deleteDDL")
    public Result<String> deleteDDL(@RequestParam("did") BigInteger did) {

        try {

            return ddlService.deleteDDL(did);

        } catch (Exception e) {

            return GlobalExceptionHandler.exceptionHandler(e);

        }

    }


    /*

        更新DDL信息

     */
    @PutMapping("/modifyDDL")
    public Result<String> modifyDDL(@RequestParam("did") BigInteger did,
                                    @RequestParam(value = "title", required = false) String title,
                                    @RequestParam(value = "notes", required = false) String notes,
                                    @RequestParam(value = "endtime", required = false) String endtime){

        try {

            return ddlService.modifyDDL(did, title, notes, endtime);

        } catch (Exception e) {

            return GlobalExceptionHandler.exceptionHandler(e);

        }

    }


    /*

        按照用户ID获取DDL队列

     */
    @GetMapping("/getDDLByUid")
    public Result<List<DDLItem>> getDDLByUid(@RequestParam("userid") BigInteger userid){

        try {

            return ddlService.getDDLByUserID(userid);

        } catch (Exception e) {

            return GlobalExceptionHandler.exceptionHandler(e);

        }

    }
}
