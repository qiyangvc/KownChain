package knowchain.server.service.impl;

import knowchain.common.utils.FileAndDirUtils;
import knowchain.pojo.VO.FileAndDirItem;
import knowchain.pojo.entity.FileAndDirTable;
import knowchain.server.mapper.FileMapper;
import knowchain.server.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    public List<FileAndDirItem> generateFileAndDirList(List<FileAndDirTable> fileAndDirTableList) {

        return FileAndDirUtils.ParentTrees2NormalTrees(fileAndDirTableList);

    }
}
