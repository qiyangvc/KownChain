package knowchain.server.service;

import knowchain.common.result.Result;
import knowchain.pojo.VO.FileAndDirItem;
import knowchain.pojo.entity.FileAndDirTable;
import org.springframework.web.bind.annotation.RequestParam;
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
                             BigInteger userid) throws IOException, SecurityException;

}
