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
    public Result<String> addDirectory(String name, BigInteger parentFID, BigInteger userID) throws SecurityException{

        /* 检查 */
        // 检查文件夹名是否为空
        if(name == null || name.trim().isEmpty()) {
            return Result.error(NAME_NULL_ERROR);
        }

        // 确保用户文档目录存在
        String userDocsPath = FileConstant.getUserDocsAbsolutePath(userID.longValue());
        java.nio.file.Path userDirPath = java.nio.file.Paths.get(userDocsPath);
        try {
            if (!java.nio.file.Files.exists(userDirPath)) {
                java.nio.file.Files.createDirectories(userDirPath);
            }
        } catch (Exception e) {
            return Result.error("创建用户目录失败：" + e.getMessage());
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

        /* 检查完毕,开始新建文件夹 */
        try {            // 构建文件夹的相对路径
            String relativePath = buildFileRelativePath(userID, parentFID, name);
            String absolutePath = userDocsPath + relativePath;
            
            // 在文件系统中创建对应的文件夹
            java.nio.file.Path dirPath = java.nio.file.Paths.get(absolutePath);
            if (!java.nio.file.Files.exists(dirPath)) {
                java.nio.file.Files.createDirectories(dirPath);
            }

            // 在数据库中创建文件夹记录，保存相对路径（去掉末尾的文件夹名，因为buildFileRelativePath已包含）
            String folderRelativePath = FileConstant.getUserDocsPath(userID.longValue()) + relativePath;
            fileMapper.insert(name, folderRelativePath, parentFID, true, userID);

            return Result.success("文件夹创建成功");
        } catch (Exception e) {
            return Result.error("创建文件夹失败：" + e.getMessage());
        }
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


    @Override    public Result<String> deleteFileOrDirectory(BigInteger fid) throws SecurityException {
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
        String relativePath = currFile.getURL();
        if (relativePath != null && !relativePath.isEmpty()) {
            try {
                // 构建绝对路径
                String absolutePath;
                if (relativePath.startsWith("docs/users/")) {
                    // 新的路径格式 - 使用项目根目录
                    String currentDir = System.getProperty("user.dir");
                    String projectRoot = new java.io.File(currentDir).getParentFile().getParent();
                    absolutePath = projectRoot + "/" + relativePath;
                } else {
                    // 兼容旧的路径格式
                    absolutePath = relativePath;
                }
                
                java.nio.file.Path pathToDelete = java.nio.file.Paths.get(absolutePath);
                if (java.nio.file.Files.exists(pathToDelete)) {
                    if (currFile.isDir()) {
                        // 删除文件夹及其所有内容
                        deleteDirectoryRecursively(pathToDelete);
                    } else {
                        // 删除文件
                        java.nio.file.Files.delete(pathToDelete);
                    }
                }
            } catch (Exception e) {
                System.err.println("删除物理文件失败: " + e.getMessage());
                // 继续删除数据库记录，不因为物理文件删除失败而中断
            }
        }

        // 删除数据库记录
        fileMapper.deleteByFid(fid);

        return Result.success(REMOVE_SUCCESS);

    }

    /**
     * 递归删除目录及其所有内容
     * @param dirPath 要删除的目录路径
     * @throws Exception 删除失败时抛出异常
     */
    private void deleteDirectoryRecursively(java.nio.file.Path dirPath) throws Exception {
        if (!java.nio.file.Files.exists(dirPath)) {
            return;
        }
        
        if (java.nio.file.Files.isDirectory(dirPath)) {
            // 遍历目录中的所有文件和子目录
            try (java.util.stream.Stream<java.nio.file.Path> paths = java.nio.file.Files.walk(dirPath)) {
                paths.sorted(java.util.Comparator.reverseOrder()) // 先删除子项，再删除父项
                     .forEach(path -> {
                         try {
                             java.nio.file.Files.delete(path);
                         } catch (Exception e) {
                             System.err.println("删除文件失败: " + path + ", 错误: " + e.getMessage());
                         }
                     });
            }
        } else {
            // 如果是文件，直接删除
            java.nio.file.Files.delete(dirPath);
        }
    }    /**
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
                String relativePath = child.getURL();
                if (relativePath != null && !relativePath.isEmpty()) {
                    try {
                        // 构建绝对路径
                        String absolutePath;
                        if (relativePath.startsWith("docs/users/")) {
                            // 新的路径格式 - 使用项目根目录
                            String currentDir = System.getProperty("user.dir");
                            String projectRoot = new java.io.File(currentDir).getParentFile().getParent();
                            absolutePath = projectRoot + "/" + relativePath;
                        } else {
                            // 兼容旧的路径格式
                            absolutePath = relativePath;
                        }
                        
                        java.nio.file.Path pathToDelete = java.nio.file.Paths.get(absolutePath);
                        if (java.nio.file.Files.exists(pathToDelete)) {
                            if (child.isDir()) {
                                // 删除文件夹及其所有内容
                                deleteDirectoryRecursively(pathToDelete);
                            } else {
                                // 删除文件
                                java.nio.file.Files.delete(pathToDelete);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("递归删除物理文件失败: " + e.getMessage());
                    }
                }

                // 删除数据库记录
                fileMapper.deleteByFid(child.getFID());
            }
        }
    }@Override
    public String getFileContent(BigInteger fileId) throws Exception {
        // 根据文件ID获取文件信息
        FileAndDirTable fileInfo = fileMapper.getByFid(fileId);
        if (fileInfo == null) {
            throw new Exception("文件不存在");
        }
        
        if (fileInfo.isDir()) {
            throw new Exception("不能获取文件夹内容");
        }
        
        // 读取文件内容
        String relativePath = fileInfo.getURL();
        if (relativePath == null || relativePath.isEmpty()) {
            throw new Exception("文件路径为空");
        }
          try {
            // 构建绝对路径
            String absolutePath;
            if (relativePath.startsWith("docs/users/")) {
                // 新的路径格式 - 使用项目根目录
                String currentDir = System.getProperty("user.dir");
                String projectRoot = new java.io.File(currentDir).getParentFile().getParent();
                absolutePath = projectRoot + "/" + relativePath;
            } else {
                // 兼容旧的路径格式
                absolutePath = relativePath;
            }
            
            java.nio.file.Path path = java.nio.file.Paths.get(absolutePath);
            if (!java.nio.file.Files.exists(path)) {
                throw new Exception("文件不存在：" + absolutePath);
            }
            
            // 读取文件内容并返回
            byte[] bytes = java.nio.file.Files.readAllBytes(path);
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new Exception("读取文件内容失败：" + e.getMessage());
        }
    }    @Override
    public Result<String> saveFileContent(BigInteger fileId, String content) throws Exception {
        // 根据文件ID获取文件信息
        FileAndDirTable fileInfo = fileMapper.getByFid(fileId);
        if (fileInfo == null) {
            return Result.error("文件不存在");
        }
        
        if (fileInfo.isDir()) {
            return Result.error("不能保存文件夹内容");
        }
        
        // 保存文件内容
        String relativePath = fileInfo.getURL();
        if (relativePath == null || relativePath.isEmpty()) {
            return Result.error("文件路径为空");
        }
          try {
            // 构建绝对路径
            String absolutePath;
            if (relativePath.startsWith("docs/users/")) {
                // 新的路径格式 - 使用项目根目录
                String currentDir = System.getProperty("user.dir");
                String projectRoot = new java.io.File(currentDir).getParentFile().getParent();
                absolutePath = projectRoot + "/" + relativePath;
            } else {
                // 兼容旧的路径格式
                absolutePath = relativePath;
            }
            
            java.nio.file.Path path = java.nio.file.Paths.get(absolutePath);
            
            // 确保父目录存在
            java.nio.file.Path parentPath = path.getParent();
            if (parentPath != null && !java.nio.file.Files.exists(parentPath)) {
                java.nio.file.Files.createDirectories(parentPath);
            }
            
            java.nio.file.Files.write(path, content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Result.success("文件保存成功");
        } catch (Exception e) {
            return Result.error("保存文件失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> createFile(BigInteger userId, String fileName, BigInteger parentId, Boolean isDirectory) throws Exception {
        try {
            // 检查文件名是否为空
            if (fileName == null || fileName.trim().isEmpty()) {
                return Result.error("文件名不能为空");
            }
            
            // 检查同级目录下是否已存在同名文件
            List<FileAndDirTable> existingFiles = fileMapper.getByUserIDAndParentFID(userId, parentId);
            for (FileAndDirTable file : existingFiles) {
                if (fileName.equals(file.getFName())) {
                    return Result.error("同名文件已存在");
                }
            }
            
            // 创建文件/文件夹记录
            FileAndDirTable newFile = new FileAndDirTable();
            newFile.setFName(fileName);
            newFile.setParentFID(parentId);
            newFile.setUserID(userId);
            newFile.setDir(isDirectory);            if (isDirectory) {
                // 创建文件夹，也需要保存路径信息
                String userDocsPath = FileConstant.getUserDocsAbsolutePath(userId.longValue());
                java.nio.file.Path userDirPath = java.nio.file.Paths.get(userDocsPath);
                
                // 确保用户目录存在
                if (!java.nio.file.Files.exists(userDirPath)) {
                    java.nio.file.Files.createDirectories(userDirPath);
                }
                
                // 根据parentId构建完整的文件夹路径
                String relativePath = buildFileRelativePath(userId, parentId, fileName);
                String fullPath = userDocsPath + relativePath;
                
                // 创建文件夹
                java.nio.file.Path dirPath = java.nio.file.Paths.get(fullPath);
                if (!java.nio.file.Files.exists(dirPath)) {
                    java.nio.file.Files.createDirectories(dirPath);
                }
                
                // 存储文件夹的相对路径
                newFile.setURL(FileConstant.getUserDocsPath(userId.longValue()) + relativePath);
            } else {
                // 创建文件，使用docs/users/userid/目录结构
                String userDocsPath = FileConstant.getUserDocsAbsolutePath(userId.longValue());
                java.nio.file.Path userDirPath = java.nio.file.Paths.get(userDocsPath);
                
                // 确保用户目录存在
                if (!java.nio.file.Files.exists(userDirPath)) {
                    java.nio.file.Files.createDirectories(userDirPath);
                }
                
                // 根据parentId构建完整的文件路径
                String relativePath = buildFileRelativePath(userId, parentId, fileName);
                String fullPath = userDocsPath + relativePath;
                
                // 确保父目录存在
                java.nio.file.Path fileParentPath = java.nio.file.Paths.get(fullPath).getParent();
                if (fileParentPath != null && !java.nio.file.Files.exists(fileParentPath)) {
                    java.nio.file.Files.createDirectories(fileParentPath);
                }
                
                java.nio.file.Path filePathObj = java.nio.file.Paths.get(fullPath);
                
                // 创建空文件
                if (!java.nio.file.Files.exists(filePathObj)) {
                    java.nio.file.Files.createFile(filePathObj);
                }
                
                // 存储相对路径而不是绝对路径
                newFile.setURL(FileConstant.getUserDocsPath(userId.longValue()) + relativePath);
            }
              // 保存到数据库
            fileMapper.insertEntity(newFile);
            
            return Result.success(isDirectory ? "文件夹创建成功" : "文件创建成功");
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }    /**
     * 根据父目录ID构建文件的相对路径
     * @param userId 用户ID
     * @param parentId 父目录ID
     * @param fileName 文件名
     * @return 相对路径
     */
    private String buildFileRelativePath(BigInteger userId, BigInteger parentId, String fileName) {
        if (parentId == null) {
            // 根目录文件
            return fileName;
        }
        
        // 递归构建父目录路径
        StringBuilder pathBuilder = new StringBuilder();
        buildParentPath(parentId, pathBuilder);
        pathBuilder.append(fileName);
        
        return pathBuilder.toString();
    }
    
    /**
     * 递归构建父目录路径
     * @param parentId 父目录ID
     * @param pathBuilder 路径构建器
     */
    private void buildParentPath(BigInteger parentId, StringBuilder pathBuilder) {
        if (parentId == null) {
            return;
        }
        
        try {
            FileAndDirTable parentDir = fileMapper.getByFid(parentId);
            if (parentDir != null) {
                // 递归构建父目录的路径
                buildParentPath(parentDir.getParentFID(), pathBuilder);
                // 添加当前目录名和分隔符
                pathBuilder.append(parentDir.getFName()).append("/");
            }
        } catch (Exception e) {
            // 如果查询失败，忽略错误
            System.err.println("Failed to build parent path for parentId: " + parentId);
        }
    }
}
