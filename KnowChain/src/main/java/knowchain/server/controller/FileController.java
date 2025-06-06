package knowchain.server.controller;

import knowchain.common.result.Result;
import knowchain.pojo.VO.FileAndDirItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/file")
public class FileController {

    // 获取文件和文件夹树形列表
    @GetMapping("/getAll/{userID}")
    public Result<List<FileAndDirItem>> getFileAndDirList(@PathVariable String userID){

        // 测试
        List<FileAndDirItem> test = new ArrayList<>();
        test.add(new FileAndDirItem(0,"测试","/",false));

        return Result.success(test);
    }

}
