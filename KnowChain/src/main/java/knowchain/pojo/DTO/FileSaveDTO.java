package knowchain.pojo.DTO;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class FileSaveDTO implements Serializable {

    private BigInteger fileId;
    private String content;
}
