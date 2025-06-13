package knowchain.common.properties;


import lombok.Data;

@Data
public class JwtProperties {

    /**
     * 生成jwt令牌相关配置
     */
    private String userSecretKey = "knowchain";
    private long userTtl = 36000000;
    private String userTokenName = "authentication";
}
