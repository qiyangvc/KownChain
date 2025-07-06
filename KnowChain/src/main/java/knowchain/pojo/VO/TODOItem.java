package knowchain.pojo.VO;

import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TODOItem implements Serializable {

    @Getter private BigInteger tdID;
    @Getter @Setter private Date tdDate;
    @Getter @Setter private String tdContent;
    @Getter @Setter private Time tdStartTime;
    @Getter @Setter private Time tdEndTime;

}
