package knowchain.common.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {

    /*
        用户管理常量
     */
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";


    /*
        文件管理常量
     */
    // 不成功
    public static final String UNSUPPORTED_FORMAT = "不支持的文件格式,仅支持 .ppt .pptx .pdf 格式";
    public static final String FOLDER_NOT_FOUND = "指定的父文件夹不存在！";
    public static final String EXIST_ERROR = "同名文件/文件夹已存在！";
    public static final String NAME_NULL_ERROR = "文件/文件夹名为空！";
    public static final String NOT_FOUND_ERROR = "指定的文件/文件夹不存在！";
    public static final String FETCH_FAILED = "获取失败";
    public static final String ADD_DIR_FAILED = "新建文件夹失败";
    public static final String RENAME_FAILED = "重命名失败";
    public static final String CHANGE_POSITION_FAILED = "移动文件或文件夹失败";
    public static final String ACCESS_RESTRICTED = "权限不足";
    public static final String GET_STREAM_FAILED = "文件流读取失败";
    public static final String REMOVE_FAILED = "删除文件或文件夹失败";
    public static final String UNSUPPORTED_UPLOAD_FORMAT = "不支持的文件格式,新建文件只支持 .md 格式";
    public static final String SAVE_TYPE_ERROR_FOLDER = "不能保存文件夹内容";
    public static final String SAVE_TYPE_ERROR_FILE = "只能保存.md格式的文件";
    // 成功
    public static final String REMOVE_SUCCESS = "删除成功";
    public static final String SAVE_SUCCESS = "文件保存成功";


    /*
        DDL管理常量
     */
    // 不成功
    public static final String DDL_TITLE_EMPTY = "DDL标题不能为空";
    public static final String DDL_END_TIME_ERROR = "DDL结束时间不能早于当前时间";
    public static final String DDL_NOT_FOUND = "指定的DDL不存在";
    public static final String DDL_TITLE_OUT_OF_LENGTH = "标题长度超出限制";
    // 成功
    public static final String DDL_CREATE_SUCCESS = "DDL创建成功";
    public static final String DDL_DELETE_SUCCESS = "DDL删除成功";
    public static final String DDL_MODIFY_SUCCESS = "DDL修改成功";


    /*
        待办管理常量
     */
    // 不成功
    public static final String TDDATE_EMPTY_ERROR = "日期不能为空";
    public static final String TDCONTENT_EMPTY_ERROR = "任务内容不能为空";
    public static final String TDID_EMPTY_ERROR = "任务ID不能为空";
    public static final String TODO_NOT_FOUND = "待办不存在";
    // 成功
    public static final String TODO_CREATE_SUCCESS = "新增待办成功";
    public static final String TODO_DELETE_SUCCESS = "删除待办成功";
    public static final String TODO_MODIFY_SUCCESS = "修改待办成功";


    /*
        其他常量
     */
    public static final String TIMESTAMP_FORMAT_ERROR = "TimeStamp日期格式错误,请使用 yyyy-MM-ddTHH:mm:ss 格式";
    public static final String ARGUMENT_EMPTY_ERROR = "参数不能为空";
}
