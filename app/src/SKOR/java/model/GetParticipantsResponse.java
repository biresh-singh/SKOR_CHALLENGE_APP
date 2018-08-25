package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by biresh.singh on 05-08-2018.
 */

public class GetParticipantsResponse {

    @SerializedName("result")
    @Expose
    private List<Participants> Result;

    @SerializedName("status")
    @Expose
    private int Status;

    @SerializedName("message")
    @Expose
    private String StatusMessage;

    public GetParticipantsResponse() {
    }


    public List<Participants> getResult() {
        return Result;
    }

    public void setResults(List<Participants> Result) {
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
