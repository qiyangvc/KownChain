package knowchain.pojo.VO;

import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DDLItem implements Serializable {

    @Getter private BigInteger dID;
    @Getter @Setter private String dTitle;
    @Getter @Setter private String dNotes;
    @Getter private Timestamp dCreateTime;
    @Getter @Setter private Timestamp dEndTime;

}
