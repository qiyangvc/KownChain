package knowchain.common.utils;

import knowchain.pojo.VO.FileAndDirItem;
import knowchain.pojo.entity.FileAndDirTable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileAndDirUtils {

    /*
        将存储在数据库中的父树(节点都指向父节点)转换为正常的子树(节点都指向子节点)
     */

    public static List<FileAndDirItem> ParentTrees2NormalTrees(List<FileAndDirTable> fileAndDirTableList) {

        Map<BigInteger, FileAndDirItem> nodes = new HashMap<BigInteger, FileAndDirItem>();

        List<FileAndDirItem> retList = new ArrayList<>();

        // 遍历列表,创建所有节点的FileAndDirItem,并创建map
        for(FileAndDirTable fileAndDirTable : fileAndDirTableList) {

            FileAndDirItem temp = new FileAndDirItem(
                    fileAndDirTable.getFID(),
                    fileAndDirTable.getFName(),
                    fileAndDirTable.getURL(),
                    fileAndDirTable.isDir()
            );
            nodes.put(temp.getFid(), temp);

        }

        // 构建目录森林并找到每棵树的根节点
        for (FileAndDirTable currTable : fileAndDirTableList) {

            FileAndDirItem currItem = nodes.get(currTable.getFID());

            if (currTable.getParentFID() == null) {
                retList.add(currItem);
                continue;
            }

            // 有父亲的节点就构建树
            FileAndDirItem currParent = nodes.get(currTable.getParentFID());
            currParent.getChildren().add(currItem);

        }

        return retList;

    }

}
