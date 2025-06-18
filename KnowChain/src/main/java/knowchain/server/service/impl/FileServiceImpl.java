package knowchain.server.service.impl;

import knowchain.common.constant.FileConstant;
import knowchain.common.result.Result;
import knowchain.common.utils.FileAndDirUtils;
import knowchain.pojo.VO.FileAndDirItem;
import knowchain.pojo.entity.FileAndDirTable;
import knowchain.server.mapper.FileMapper;
import knowchain.server.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static knowchain.common.constant.MessageConstant.*;

@Service
public class FileServiceImpl implements FileService {


    @Autowired
    private FileMapper fileMapper;


    @Override
    public List<FileAndDirItem> generateFileAndDirList(List<FileAndDirTable> fileAndDirTableList) {

        return FileAndDirUtils.ParentTrees2NormalTrees(fileAndDirTableList);

    }


    @Override
    public Result<String> uploadFile(MultipartFile file, BigInteger parentfid, BigInteger userid) throws IOException, SecurityException, IllegalStateException {

        // 文件名
        String originalFileName = file.getOriginalFilename();
        // 文件大小
        long size = file.getSize();

        String UPLOAD_SUCCESS = String.format("文件 %s 上传成功", originalFileName);
        String UPLOAD_FAILED = String.format("文件 %s 上传失败", originalFileName);

        /* 检查工作 */
        // 校验文件格式
        if(originalFileName == null ||
                !(originalFileName.endsWith(".ppt") ||
                        originalFileName.endsWith(".pptx") ||
                        originalFileName.endsWith(".pdf"))) {
            return Result.error(UNSUPPORTED_FORMAT);
//            return Result.error(UPLOAD_FAILED);
        }

        // 确保上传目录存在(不存在就新建文件夹)
        // 文件根目录为上传文件根目录中的#{userid}文件夹
        String uploadPath = FileConstant.UploadFilePATH + File.separator + userid.toString();
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 确认是否归属于文件夹
        FileAndDirTable currDir = fileMapper.getByFID(parentfid);
        if(parentfid != null){
            if(!currDir.isDir()){
                return Result.error(FOLDER_NOT_FOUND);
//                return Result.error(UPLOAD_FAILED);
            }
        }

        // 检查是否已经存在
        if((parentfid != null && fileMapper.getByUIDandFNameandParentFID(userid, originalFileName, parentfid) != null)
                || (parentfid == null && fileMapper.getByUIDandFNameandNULLParentFID(userid, originalFileName) != null)) {
            return Result.error(EXIST_ERROR);
//            return Result.error(UPLOAD_FAILED);
        }

        /* 检查完毕，开始上传 */
        // 记录到数据库
        fileMapper.insertWithoutURL(originalFileName, parentfid, false, userid);

        // 获取刚插入文件的FileAndDirTable
        FileAndDirTable currFile;
        if(parentfid != null){
            currFile = fileMapper.getByUIDandFNameandParentFID(userid, originalFileName, parentfid);
        }
        else{
            currFile = fileMapper.getByUIDandFNameandNULLParentFID(userid, originalFileName);
        }

        String url;
        // 保存文件
        if(parentfid == null){
            url = uploadDir.getAbsolutePath() + File.separator + originalFileName;
        }
        else{
            url = currDir.getURL() + File.separator + originalFileName;
        }
        File dest = new File(url);
        file.transferTo(dest);

        // 将数据库的url修改
        fileMapper.updateURL(url, currFile.getFID());

        return Result.success(UPLOAD_SUCCESS);
    }


    @Override
    public Result<String> addFileOrDirectory(String name, BigInteger parentFID, BigInteger userID, boolean isDir) throws SecurityException {
        /* 检查 */
        // 检查文件/文件夹名是否为空
        if(name == null || name.trim().isEmpty()) {
            return Result.error(NAME_NULL_ERROR);
        }

        // 如果是文件，检查文件格式 - 只允许.md格式
        if(!isDir && !name.endsWith(".md")) {
            return Result.error(UNSUPPORTED_UPLOAD_FORMAT);
        }

        // 确保上传目录存在
        String uploadPath = FileConstant.UploadFilePATH + File.separator + userID;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 检查是否已存在同名文件/文件夹
        if((parentFID != null && fileMapper.getByUIDandFNameandParentFID(userID, name, parentFID) != null)
                || (parentFID == null && fileMapper.getByUIDandFNameandNULLParentFID(userID, name) != null)) {
            return Result.error(EXIST_ERROR);
        }

        // 如果有父目录,检查父目录是否存在且是文件夹
        FileAndDirTable currDir = fileMapper.getByFID(parentFID);
        if(parentFID != null) {
            if(currDir == null || !currDir.isDir()) {
                return Result.error(FOLDER_NOT_FOUND);
            }
        }

        /* 开始创建 */
        String filePath;
        if(parentFID != null) {
            filePath = currDir.getURL() + File.separator + name;
        } else {
            filePath = uploadDir.getAbsolutePath() + File.separator + name;
        }

        File newFile = new File(filePath);
        if(isDir) {
            // 创建文件夹
            if(!newFile.mkdirs()) {
                return Result.error("创建文件夹失败");
            }
        } else {
            // 创建空文件
            try {
                if(!newFile.createNewFile()) {
                    return Result.error("创建文件失败");
                }
            } catch(IOException e) {
                return Result.error("创建文件失败: " + e.getMessage());
            }
        }

        // 在数据库中创建记录
        fileMapper.insert(name, filePath, parentFID, isDir, userID);

        return Result.success(isDir ? "文件夹创建成功" : "文件创建成功");
    }


    @Override
    public Result<String> saveFile(BigInteger fileId, String content) throws IOException {

        // 获取文件信息
        FileAndDirTable fileAndDirTable = fileMapper.getByFID(fileId);
        if(fileAndDirTable == null) {
            return Result.error(NOT_FOUND_ERROR);
        }

        // 检查是否是文件夹
        if(fileAndDirTable.isDir()) {
            return Result.error(SAVE_TYPE_ERROR_FOLDER);
        }

        // 检查文件格式是否为.md
        String fileName = fileAndDirTable.getFName().toLowerCase();
        if(!fileName.endsWith(".md")) {
            return Result.error(SAVE_TYPE_ERROR_FILE);
        }

        // 获取文件
        File file = new File(fileAndDirTable.getURL());
        if(!file.exists() || !file.canWrite()) {
            return Result.error(NOT_FOUND_ERROR);
        }

        // 写入新内容 - 使用UTF-8编码
        Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
        return Result.success(SAVE_SUCCESS);

    }


    @Override
    public Result<String> renameFileOrDirectory(BigInteger fid, String newName) throws SecurityException, NullPointerException {

        /* 检查 */
        // 获取当前文件/文件夹信息
        FileAndDirTable currFile = fileMapper.getByFID(fid);
        if(currFile == null) {
            return Result.error(NOT_FOUND_ERROR);

        }

        // 检查新名称格式
        if(newName == null || newName.trim().isEmpty()) {
            return Result.error(NAME_NULL_ERROR);

        }

        // 检查是否有同名文件/文件夹
        if((currFile.getParentFID() != null &&
                fileMapper.getByUIDandFNameandParentFID(currFile.getUserID(), newName, currFile.getParentFID()) != null)
                || (currFile.getParentFID() == null &&
                fileMapper.getByUIDandFNameandNULLParentFID(currFile.getUserID(), newName) != null)) {
            return Result.error(EXIST_ERROR);

        }

        /* 重命名 */
        // 重命名实际文件/文件夹
        File oldFile = new File(currFile.getURL());
        if(!oldFile.exists()) {
            return Result.error(NOT_FOUND_ERROR);

        }

        // 构造新的文件路径
        String newPath = oldFile.getParent() + File.separator + newName;
        File newFile = new File(newPath);
        if(!oldFile.renameTo(newFile)) {
            return Result.error(RENAME_FAILED);
        }

        // 更新数据库
        fileMapper.updatefName(newName, fid);
        fileMapper.updateURL(newPath, fid);

        return Result.success();
    }


    @Override
    public Result<String> changeFileOrDirPosition(BigInteger fid, BigInteger parentFID, BigInteger userID) throws SecurityException, NullPointerException {
        // 检查目标文件/文件夹是否存在
        FileAndDirTable currFile = fileMapper.getByFID(fid);
        if(currFile == null) {
            return Result.error(NOT_FOUND_ERROR);
        }

        // 检查用户是否有权限(是否是自己的文件)
        if(!currFile.getUserID().equals(userID)) {
            return Result.error(ACCESS_RESTRICTED);
        }

        // 检查目标父文件夹是否存在且是文件夹
        if(parentFID != null) {
            FileAndDirTable parentDir = fileMapper.getByFID(parentFID);
            if(parentDir == null || !parentDir.isDir()) {
                return Result.error(FOLDER_NOT_FOUND);
            }

            // 检查目标位置是否已存在同名文件/文件夹
            if(fileMapper.getByUIDandFNameandParentFID(userID, currFile.getFName(), parentFID) != null) {
                return Result.error(EXIST_ERROR);
            }
        } else {
            // 如果移动到根目录,检查是否已有同名文件
            if(fileMapper.getByUIDandFNameandNULLParentFID(userID, currFile.getFName()) != null) {
                return Result.error(EXIST_ERROR);
            }
        }

        // 获取当前文件的位置和新位置
        File oldFile = new File(currFile.getURL());
        String newPath;

        if(parentFID == null) {
            // 移动到根目录
            newPath = FileConstant.UploadFilePATH + File.separator + userID + File.separator + currFile.getFName();
        } else {
            // 移动到指定文件夹
            FileAndDirTable parentDir = fileMapper.getByFID(parentFID);
            newPath = parentDir.getURL() + File.separator + currFile.getFName();
        }

        File newFile = new File(newPath);

        // 移动文件
        if(!oldFile.renameTo(newFile)) {
            return Result.error(CHANGE_POSITION_FAILED);
        }

        // 更新数据库
        fileMapper.updateParentFID(parentFID, fid);
        fileMapper.updateURL(newPath, fid);

        return Result.success();
    }


    @Override
    public Result<String> deleteFileOrDirectory(BigInteger fid) throws SecurityException {
        // 获取文件/文件夹信息
        FileAndDirTable currFile = fileMapper.getByFID(fid);
        if(currFile == null) {
            return Result.error(NOT_FOUND_ERROR);
        }

        // 如果是文件夹,递归删除所有子文件和子文件夹
        if(currFile.isDir()) {
            recursiveDelete(fid);
        }

        // 删除文件系统中的实际文件/文件夹
        File fileToDelete = new File(currFile.getURL());
        if(fileToDelete.exists() && !fileToDelete.delete()) {
            return Result.error(REMOVE_FAILED);
        }

        // 删除数据库记录
        fileMapper.deleteByFid(fid);

        return Result.success(REMOVE_SUCCESS);

    }
    /**
     * 递归删除文件夹及其内容, 用于deleteFileOrDirectory
     * @param dirFid 文件夹ID
     */
    private void recursiveDelete(BigInteger dirFid) {
        // 获取该文件夹下的所有文件和文件夹
        List<FileAndDirTable> children = fileMapper.getByParentFID(dirFid);

        if(children != null && !children.isEmpty()) {
            for(FileAndDirTable child : children) {
                // 如果是文件夹,递归删除
                if(child.isDir()) {
                    recursiveDelete(child.getFID());
                }

                // 删除文件系统中的实际文件/文件夹
                File fileToDelete = new File(child.getURL());
                if(fileToDelete.exists()) {
                    fileToDelete.delete();
                }

                // 删除数据库记录
                fileMapper.deleteByFid(child.getFID());
            }
        }
    }
}
