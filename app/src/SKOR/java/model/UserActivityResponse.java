package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by biresh.singh on 05-08-2018.
 */

public class UserActivityResponse {

    @SerializedName("result")
    @Expose
    private UserActivity Result;

    @SerializedName("code")
    @Expose
    private int Status;

    @SerializedName("message")
    @Expose
    private String StatusMessage;

    public UserActivityResponse() {
    }


    public UserActivity getResult() {
        return Result;
    }

    public void setResult(UserActivity Result) {
        this.Result = Result;
    }


    public int  getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }



    public String  getStatusMessage() {
        return StatusMessage;
    }

    public void setStatusMessage(String StatusMessage) {
        this.StatusMessage = StatusMessage;
    }


}
