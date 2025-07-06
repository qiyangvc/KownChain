package knowchain.server.service;

import knowchain.common.result.Result;
import knowchain.pojo.VO.FileAndDirItem;
import knowchain.pojo.entity.FileAndDirTable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public interface FileService {

    /**
     * 生成文件和文件夹树型列表
     * @param fileAndDirTableList
     * @return
     */
    List<FileAndDirItem> generateFileAndDirList(List<FileAndDirTable> fileAndDirTableList);


    /**
     * 上传文件(支持的格式:.ppt .pptx .pdf格式文件)
     * @param file
     * @param parentfid
     * @param userid
     * @return
     */
    Result<String> uploadFile(MultipartFile file,
                              BigInteger parentfid,
                              BigInteger userid) throws IOException, SecurityException, IllegalStateException;


    /**
     * 新建文件夹
     * @param name 文件夹名
     * @param parentFID 父目录fID
     * @return 创建文件夹是否成功结果
     */
    Result<String> addDirectory(String name,
                                BigInteger parentFID,
                                BigInteger userID) throws SecurityException;


    /**
     * 重命名文件或文件夹
     * @param fid 文件/文件夹ID
     * @param newName 新名称
     * @return 重命名结果
     */
    Result<String> renameFileOrDirectory(BigInteger fid,
                                         String newName) throws SecurityException, NullPointerException;


    /**
     * 修改文件或文件夹位置
     * @param fid 文件/文件夹ID
     * @param parentFID 要移动到的父目录ID
     * @param userID 用户ID
     * @return 移动结果
     */
    Result<String> changeFileOrDirPosition(BigInteger fid, BigInteger parentFID, BigInteger userID) throws SecurityException, NullPointerException;


    /**
     * 删除文件或文件夹
     * @param fid 文件/文件夹ID
     * @return 删除结果
     */
    Result<String> deleteFileOrDirectory(BigInteger fid) throws SecurityException;


    /**
     * 获取文件内容
     * @param fileId 文件ID
     * @return 文件内容
     */
    String getFileContent(BigInteger fileId) throws Exception;

    /**
     * 保存文件内容
     * @param fileId 文件ID
     * @param content 文件内容
     * @return 保存结果
     */
    Result<String> saveFileContent(BigInteger fileId, String content) throws Exception;    /**
     * 创建文件或文件夹
     * @param userId 用户ID
     * @param fileName 文件/文件夹名称
     * @param parentId 父目录ID
     * @param isDirectory 是否为目录
     * @return 创建结果
     */
    Result<String> createFile(BigInteger userId, String fileName, BigInteger parentId, Boolean isDirectory) throws Exception;
}
