package knowchain.server.controller;

import knowchain.common.constant.FileConstant;
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
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;


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

        List<FileAndDirTable> fileTables = fileMapper.getByUserID(new BigInteger(userID));
        List<FileAndDirItem> fileTrees = fileService.generateFileAndDirList(fileTables);

        return Result.success(fileTrees);
    }

    /*

     根据URL获取文件数据流

     */
    @GetMapping("/getFileStream/{URL}")
    public Result<InputStream> getFileStream(@PathVariable String URL){
        try {
            URL fileUrl = new URL(URL);

            // 打开连接并获取输入流
            InputStream inputStream = fileUrl.openStream();

            // 成功返回文件数据流
            return Result.success(inputStream);
        } catch (Exception e) {
            // 失败返回异常信息
            return Result.error("无法获取文件数据流:" + e.getMessage());
        }
    }

    /*

     新建文件夹

     */
    @PostMapping("/addDir")
    public Result addDirectory(
            @RequestParam("parentFID") Long parentFID,
            @RequestParam("originalName") String name
    ){
        try {

            /* TODO */

            return Result.success();
        } catch (Exception e) {
            return Result.error("新建文件夹失败");
        }
    }

    /*

     文件夹重命名

     */
    @PutMapping("/renameFileOrDir")
    public Result renameFileOrDirectory(
            @RequestParam("fid") Long fid,
            @RequestParam("fName") String fName,
            @RequestParam("isDir") boolean isDir
    ){
        try {

            /* TODO */

            return Result.success();
        } catch (Exception e) {
            return Result.error("重命名失败");
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

        try {

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
    public Result changeFileOrDirectoryPosition(
            @RequestParam("fid") Long fid,
            @RequestParam("parentFID") Long parentFID,
            @RequestParam("isDir") boolean isDir
    ){
        try{

            /* TODO */

            return Result.success();
        } catch (Exception e){
            return Result.error("移动文件或文件夹失败");
        }
    }

    /*

     删除文件/文件夹

     */
    @DeleteMapping("/deleteFileOrDir/{fid}")
    public Result deleteFileOrDirectory(
            @RequestParam("fid") Long fid,
            @RequestParam("isDir") boolean isDir
    ){
        try{

            /* TODO */

            return Result.success();
        } catch (Exception e){
            return Result.error("删除文件或文件夹失败");
        }
    }
}
