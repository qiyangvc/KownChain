package knowchain.pojo.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class User {

    private BigInteger userid;
    private String username;
    private String password;
    private String mailbox;

}
