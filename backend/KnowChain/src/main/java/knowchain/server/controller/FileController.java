package knowchain.server.controller;

import knowchain.common.result.Result;
import knowchain.pojo.DTO.FileSaveDTO;
import knowchain.pojo.VO.FileAndDirItem;
import knowchain.pojo.entity.FileAndDirTable;
import knowchain.server.mapper.FileMapper;
import knowchain.server.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.URLDecoder;
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

     新建文件夹

     */
    @PostMapping("/addDir")
    public Result<String> addDirectory(
            @RequestParam("originalName") String name,
            @RequestParam(value = "parentFID", required = false) BigInteger parentFID,
            @RequestParam("userID") BigInteger userid
    ){
        try {

//            return (fileService.addDirectory(name, parentFID, userid).getCode() == 1
//                    ? Result.success()
//                    : Result.error(ADD_DIR_FAILED));

            return fileService.addDirectory(name, parentFID, userid);

        } catch (Exception e) {

            String errString = "新建文件夹错误:\n" + e.getMessage();
            log.error(errString);
            return Result.error(errString);

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

    /**
     * 获取文件内容
     * @param fileId 文件ID
     * @return 文件内容
     */
    @GetMapping("/content/{fileId}")
    public Result<String> getFileContent(@PathVariable BigInteger fileId) {
        try {
            String content = fileService.getFileContent(fileId);
            return Result.success(content);
        } catch (Exception e) {
            log.error("获取文件内容失败：{}", e.getMessage());
            return Result.error("获取文件内容失败：" + e.getMessage());
        }
    }

    /**
     * 保存文件内容
     * @param fileSaveDTO 文件保存DTO
     * @return 保存结果
     */
    @PostMapping("/save")
    public Result<String> saveFileContent(@RequestBody FileSaveDTO fileSaveDTO) {
        try {
            return fileService.saveFileContent(fileSaveDTO.getFileId(), fileSaveDTO.getContent());
        } catch (Exception e) {
            log.error("保存文件内容失败：{}", e.getMessage());
            return Result.error("保存文件内容失败：" + e.getMessage());
        }
    }

    /**
     * 创建文件或文件夹
     * @param userId 用户ID
     * @param fileName 文件/文件夹名称
     * @param parentId 父目录ID
     * @param isDirectory 是否为目录
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<String> createFile(
            @RequestParam("userId") BigInteger userId,
            @RequestParam("fileName") String fileName,
            @RequestParam(value = "parentId", required = false) BigInteger parentId,
            @RequestParam("isDirectory") Boolean isDirectory) {
        try {
            return fileService.createFile(userId, fileName, parentId, isDirectory);
        } catch (Exception e) {
            log.error("创建文件失败：{}", e.getMessage());
            return Result.error("创建文件失败：" + e.getMessage());
        }
    }

}
