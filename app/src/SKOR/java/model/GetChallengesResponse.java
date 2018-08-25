package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by biresh.singh on 05-08-2018.
 */

public class GetChallengesResponse {

    @SerializedName("result")
    @Expose
    private GetAllChallenge Result;

    @SerializedName("status")
    @Expose
    private int Status;

    @SerializedName("message")
    @Expose
    private String StatusMessage;

    public GetChallengesResponse() {
    }


    public GetAllChallenge getResult() {
        return Result;
    }

    public void setResults(GetAllChallenge Result) {
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
