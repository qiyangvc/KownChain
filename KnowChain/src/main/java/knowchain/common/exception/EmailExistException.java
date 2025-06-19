package knowchain.common.exception;

/**
 * 邮箱已存在异常
 */
public class EmailExistException extends BaseException {

    public EmailExistException() {
    }

    public EmailExistException(String msg) {
        super(msg);
    }
}
