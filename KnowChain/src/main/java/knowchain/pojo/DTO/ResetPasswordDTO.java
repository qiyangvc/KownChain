package knowchain.pojo.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResetPasswordDTO implements Serializable {

    private String mailbox;
}
