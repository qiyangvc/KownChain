package knowchain.pojo.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class FileAndDirTable {

    private BigInteger fID;
    private String fName;
    private String URL;
    private BigInteger parentFID;
    private boolean isDir;
    private BigInteger userID;

}
