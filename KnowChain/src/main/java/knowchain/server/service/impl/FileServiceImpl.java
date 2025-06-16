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
import java.util.ArrayList;
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
    public Result<String> uploadFile(MultipartFile file, BigInteger parentfid, BigInteger userid) throws IOException, SecurityException {

        // 文件名
        String originalFileName = file.getOriginalFilename();
        // 文件大小
        long size = file.getSize();

        /* 检查工作 */
        // 校验文件格式
        if(originalFileName == null ||
                !(originalFileName.endsWith(".ppt") ||
                        originalFileName.endsWith(".pptx") ||
                        originalFileName.endsWith(".pdf"))) {
            return Result.error(UNSUPPORTED_FORMAT);
        }

        // 确保上传目录存在(不存在就新建文件夹)
        File uploadDir = new File(FileConstant.UploadFilePATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 确认是否归属于文件夹
        FileAndDirTable currDir = fileMapper.getByFID(parentfid);
        if(parentfid != null){
            if(!currDir.isDir()){
                return Result.error(FOLDER_NOT_FOUND);
            }
        }

        // 检查是否已经存在
        if((parentfid != null && fileMapper.getByUIDandFNameandParentFID(userid, originalFileName, parentfid) != null)
                || (parentfid == null && fileMapper.getByUIDandFNameandNULLParentFID(userid, originalFileName) != null)) {
            return Result.error(FILE_EXIST_ERROR);
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

        // 生成存储的文件名(避免重名所以把fid当作文件名前缀)
        String fileName = currFile.getFID().toString() + "_" + originalFileName;
        String url;

        // 保存文件
        if(parentfid == null){
            url = uploadDir.getAbsolutePath() + File.separator + fileName;
        }
        else{
            url = currDir.getURL() + File.separator + fileName;
        }
        File dest = new File(url);
        file.transferTo(dest);

        // 将数据库的url修改
        fileMapper.updateURL(url, currFile.getFID());

        return Result.success(String.format("文件 %s 上传成功!", originalFileName));
    }
}
