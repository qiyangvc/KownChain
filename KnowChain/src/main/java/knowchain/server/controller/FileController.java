package knowchain.server.controller;

import knowchain.common.result.Result;
import knowchain.pojo.DTO.SaveFileDTO;
import knowchain.pojo.VO.FileAndDirItem;
import knowchain.pojo.entity.FileAndDirTable;
import knowchain.server.handler.GlobalExceptionHandler;
import knowchain.server.mapper.FileMapper;
import knowchain.server.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

            return GlobalExceptionHandler.exceptionHandler(e);

        }

    }


    /*

     根据fID获取文件数据流

     */
    @GetMapping("/content/{fileId}")
    public Result<?> getFileStream(@PathVariable String fileId) {

        try {

            // 获取文件
            BigInteger fid = new BigInteger(fileId);
            FileAndDirTable fileAndDirTable = fileMapper.getByFID(fid);

            // 检查文件是否存在
            if (fileAndDirTable == null) {
                return Result.error(NOT_FOUND_ERROR);
            }

            File file = new File(fileAndDirTable.getURL());

            // 检查文件是否存在且可读
            if (!file.exists() || !file.canRead()) {
                return Result.error(NOT_FOUND_ERROR);
            }

            String fileName = fileAndDirTable.getFName().toLowerCase();
            String contentType;

            if (fileName.endsWith(".pdf")) {
                contentType = "application/pdf";
                byte[] content = Files.readAllBytes(file.toPath());
                // 返回Result，msg中带contentType
                return Result.success()
                        .setMsg(contentType)
                        .setData(content);
            } else if (fileName.endsWith(".ppt")) {
                contentType = "application/vnd.ms-powerpoint";
                byte[] content = Files.readAllBytes(file.toPath());
                return Result.success()
                        .setMsg(contentType)
                        .setData(content);
            } else if (fileName.endsWith(".pptx")) {
                contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
                byte[] content = Files.readAllBytes(file.toPath());
                return Result.success()
                        .setMsg(contentType)
                        .setData(content);
            } else if (fileName.endsWith(".md")) {
                contentType = "text/markdown; charset=UTF-8";
                String content = Files.readString(file.toPath());
                return Result.success()
                        .setMsg(contentType)
                        .setData(content);
            } else {
                return Result.error(UNSUPPORTED_FORMAT);
            }

        } catch (Exception e) {

            return GlobalExceptionHandler.exceptionHandler(e);

        }

    }


    /*

     新建文件/文件夹

     */
    @PostMapping("/create")
    public Result<String> addFileOrDirectory(
            @RequestParam("isDirectory") boolean isDir,
            @RequestParam("fileName") String name,
            @RequestParam(value = "parentId", required = false) BigInteger parentFID,
            @RequestParam("userId") BigInteger userid
    ){

        try {

            return fileService.addFileOrDirectory(name, parentFID, userid, isDir);

        } catch (Exception e) {

            return GlobalExceptionHandler.exceptionHandler(e);

        }

    }


    /*

        文件保存

     */
    @PostMapping("/save")
    public Result<String> saveFile(@RequestBody SaveFileDTO saveFileDTO) {
        try {

            return fileService.saveFile(saveFileDTO.getFileId(), saveFileDTO.getContent());

        } catch(Exception e) {

            return GlobalExceptionHandler.exceptionHandler(e);

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

            return GlobalExceptionHandler.exceptionHandler(e);

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

            return GlobalExceptionHandler.exceptionHandler(e);

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

            return GlobalExceptionHandler.exceptionHandler(e);

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

            return GlobalExceptionHandler.exceptionHandler(e);

        }

    }


}
