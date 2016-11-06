package project1.android.com.project1.data;

/**
 * Created by ruchi on 6/11/16.
 */
public class ErrorInfo extends Data{

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    private int statusCode;

    private String errorMsg;
}
