public class MD5Generator {
    public static void main(String[] args) {
        String[] passwords = {"admin123", "test123"};
        
        for (String password : passwords) {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(password.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }
                System.out.println(password + " MD5: " + sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
