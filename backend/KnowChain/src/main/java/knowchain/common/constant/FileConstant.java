package knowchain.common.constant;

/* 文件存储路径配置 */
public class FileConstant {
    public static final String UploadFilePATH = "C:/KnowChain/uploads";
    public static final String UPLOAD_DIR = "C:/KnowChain/uploads/";
    
    // 用户文档根目录
    public static final String DOCS_ROOT = "docs/";
    public static final String USERS_DIR = "users/";
    
    /**
     * 获取用户文档目录路径
     * @param userId 用户ID
     * @return 用户文档目录路径 (docs/users/userid/)
     */
    public static String getUserDocsPath(Long userId) {
        return DOCS_ROOT + USERS_DIR + userId + "/";
    }
      /**
     * 获取用户文档绝对路径 (项目根目录下的docs)
     * @param userId 用户ID
     * @return 用户文档绝对路径
     */
    public static String getUserDocsAbsolutePath(Long userId) {
        // 从后端工作目录向上回退到项目根目录
        String currentDir = System.getProperty("user.dir");
        String projectRoot = new java.io.File(currentDir).getParentFile().getParent();
        return projectRoot + "/" + getUserDocsPath(userId);
    }
}
