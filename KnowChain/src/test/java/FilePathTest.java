import knowchain.common.constant.FileConstant;

/**
 * 文件路径测试类
 */
public class FilePathTest {
    
    public static void main(String[] args) {
        System.out.println("=== KnowChain 文件路径测试 ===");
        
        // 测试用户文档路径生成
        Long userId1 = 1L;
        Long userId2 = 2L;
        
        System.out.println("用户" + userId1 + "的相对路径: " + FileConstant.getUserDocsPath(userId1));
        System.out.println("用户" + userId2 + "的相对路径: " + FileConstant.getUserDocsPath(userId2));
        
        System.out.println();
        System.out.println("当前工作目录: " + System.getProperty("user.dir"));
        
        // 显示项目根目录推导过程
        String currentDir = System.getProperty("user.dir");
        String projectRoot = new java.io.File(currentDir).getParentFile().getParent();
        System.out.println("项目根目录: " + projectRoot);
        
        System.out.println("用户" + userId1 + "的绝对路径: " + FileConstant.getUserDocsAbsolutePath(userId1));
        System.out.println("用户" + userId2 + "的绝对路径: " + FileConstant.getUserDocsAbsolutePath(userId2));
        
        // 测试文件路径示例
        System.out.println();
        System.out.println("=== 文件路径示例 ===");
        String fileName1 = "文档1.txt";
        String fileName2 = "项目文件夹/项目文件.md";
        
        System.out.println("文件1相对路径: " + FileConstant.getUserDocsPath(userId1) + fileName1);
        System.out.println("文件2相对路径: " + FileConstant.getUserDocsPath(userId1) + fileName2);
        
        System.out.println("文件1绝对路径: " + FileConstant.getUserDocsAbsolutePath(userId1) + fileName1);
        System.out.println("文件2绝对路径: " + FileConstant.getUserDocsAbsolutePath(userId1) + fileName2);
        
        // 验证目录是否存在
        System.out.println();
        System.out.println("=== 目录存在性验证 ===");
        java.io.File projectRootDir = new java.io.File(projectRoot);
        java.io.File docsDir = new java.io.File(projectRoot + "/docs");
        java.io.File usersDir = new java.io.File(projectRoot + "/docs/users");
        
        System.out.println("项目根目录存在: " + projectRootDir.exists());
        System.out.println("docs目录存在: " + docsDir.exists());
        System.out.println("users目录存在: " + usersDir.exists());
    }
}
