package shellMain;

import Enums.StatusCode;

import java.util.List;

public class ExecutionResult {

    private List<String> answerList;
    private StatusCode code;
    private String description;
    private Object additionalInfo;

    public void setAdditionalInfo(Object additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Object getAdditionalInfo() {

        return additionalInfo;
    }

    public void setAnswerList(List<String> answerList) {
        this.answerList = answerList;
    }

    public void setCode(StatusCode code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAnswerList() {
        return answerList;
    }

    public StatusCode getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
