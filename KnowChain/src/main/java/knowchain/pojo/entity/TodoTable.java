package knowchain.pojo.entity;

import lombok.Data;

import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;

@Data
public class TodoTable {

    private BigInteger tdID;
    private Date tdDate;
    private String tdContent;
    private Time tdStartTime;
    private Time tdEndTime;
    private BigInteger userID;

}
