package knowchain.common.exception;

/**
 * 密码不匹配异常
 */
public class PasswordMismatchException extends BaseException {

    public PasswordMismatchException() {
    }

    public PasswordMismatchException(String msg) {
        super(msg);
    }
}
