package knowchain.pojo.DTO;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class UserDTO implements Serializable {

    private String username;
    private String password;
}
