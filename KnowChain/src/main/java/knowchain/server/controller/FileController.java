package knowchain.server.controller;

import knowchain.common.result.Result;
import knowchain.pojo.VO.FileAndDirItem;
import knowchain.pojo.entity.FileAndDirTable;
import knowchain.server.mapper.FileMapper;
import knowchain.server.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static knowchain.common.constant.MessageConstant.*;


@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private FileService fileService;


    /*

     获取文件和文件夹树形列表

     */
    @GetMapping("/getAll/{userID}")
    public Result<List<FileAndDirItem>> getFileAndDirList(@PathVariable String userID){

        try{

            List<FileAndDirTable> fileTables = fileMapper.getByUserID(new BigInteger(userID));
            List<FileAndDirItem> fileTrees = fileService.generateFileAndDirList(fileTables);

            return Result.success(fileTrees);

        } catch (Exception e) {

            return Result.error(FETCH_FAILED);

        }

    }


    /*

     根据URL获取文件数据流

     */
    @GetMapping("/getFileStream")
    public Result<byte[]> getFileStream(@RequestParam String URL) {
        try {
            // 对URL进行解码
            String decodedPath = java.net.URLDecoder.decode(URL, StandardCharsets.UTF_8);
            File file = new File(decodedPath);

            // 检查文件是否存在且可读
            if (!file.exists() || !file.canRead()) {
                return Result.error(NOT_FOUND_ERROR);
            }

            // 使用try-with-resources自动关闭流
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] content = fis.readAllBytes();
                return Result.success(content);
            }

        } catch (Exception e) {
            return Result.error(FETCH_FAILED + ":\n" + e.getMessage());
        }
    }


    /*

     新建文件/文件夹

     */
    @PostMapping("/addFileOrDir")
    public Result<String> addFileOrDirectory(
            @RequestParam("isDir") boolean isDir,
            @RequestParam("originalName") String name,
            @RequestParam(value = "parentFID", required = false) BigInteger parentFID,
            @RequestParam("userID") BigInteger userid
    ){
        try {
            return fileService.addFileOrDirectory(name, parentFID, userid, isDir);
        } catch (Exception e) {
            String errMsg = String.format("新建%s失败: %s", isDir ? "文件夹" : "文件", e.getMessage());
            log.error(errMsg);
            return Result.error(errMsg);
        }
    }


    /*

     文件/文件夹重命名

     */
    @PutMapping("/renameFileOrDir")
    public Result<String> renameFileOrDirectory(
            @RequestParam("fid") BigInteger fid,
            @RequestParam("fName") String fName
    ){
        try {

//            return (fileService.renameFileOrDirectory(fid, fName).getCode() == 1
//                    ? Result.success()
//                    : Result.error(RENAME_FAILED));

            return fileService.renameFileOrDirectory(fid, fName);

        } catch (Exception e) {

            String errString = "重命名错误:\n" + e.getMessage();
            log.error(errString);
            return Result.error(errString);

        }
    }


    /*

     上传文件(支持的格式:.ppt .pptx .pdf格式文件)

     */
    @PostMapping("/uploadFile")
    public Result<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "parentFID", required = false) BigInteger parentfid,
            @RequestParam("userID") BigInteger userid
    ){

        String UPLOAD_FAILED = String.format("文件 %s 上传失败", file.getOriginalFilename());

        try {

//            return (fileService.uploadFile(file, parentfid, userid).getCode() == 1
//                    ? Result.success()
//                    : Result.error(UPLOAD_FAILED));

            return fileService.uploadFile(file, parentfid, userid);

        } catch (Exception e) {

            String errString = String.format("文件上传出错: \n%s", e.getMessage());
            log.error(errString);
            return Result.error(errString);

        }

    }


    /*

     修改文件或文件夹位置

     */
    @PutMapping("/changeFileOrDirPosition")
    public Result<String> changeFileOrDirectoryPosition(
            @RequestParam("fid") BigInteger fid,
            @RequestParam(value = "parentFID", required = false) BigInteger parentFID,
            @RequestParam("userID") BigInteger userID
    ){

        try {

            return fileService.changeFileOrDirPosition(fid, parentFID, userID);

        } catch (Exception e) {

            String errMsg = String.format("移动文件或文件夹失败:\n %s", e.getMessage());
            log.error(errMsg);
            return Result.error(errMsg);

        }

    }


    /*

     删除文件/文件夹

     */
    @DeleteMapping("/deleteFileOrDir/{fid}")
    public Result<String> deleteFileOrDirectory(
            @PathVariable BigInteger fid
    ){

        try {

            return fileService.deleteFileOrDirectory(fid);

        } catch (Exception e) {

            String errMsg = String.format("删除错误: %s", e.getMessage());
            log.error(errMsg);
            return Result.error(errMsg);

        }

    }


}
