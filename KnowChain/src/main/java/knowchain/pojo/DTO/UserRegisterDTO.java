package knowchain.pojo.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterDTO implements Serializable {

    private String username;
    
    private String password; // 注意：前端已验证长度>=6
    
    private String mailbox;
    
    private String confirmPassword;
}
