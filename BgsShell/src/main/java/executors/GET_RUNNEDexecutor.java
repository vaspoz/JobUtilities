package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

import java.util.List;

public class GET_RUNNEDexecutor extends CommonCommandExecutor {
    @Override
    public ExecutionResult execute() {
        List<String> answerWithRunnedMidlet = bgsEntity.getRunnedMidlets();
        if (answerWithRunnedMidlet.contains("ERROR")) {
            result.setCode(StatusCode.ERROR);
            result.setDescription("Could not receive answer.");
            return result;
        }

        additionalInfo = getMidletList(answerWithRunnedMidlet);
        showList((List<String>)additionalInfo);

        result.setCode(StatusCode.OK);
        result.setAdditionalInfo(additionalInfo);
        return result;
    }
}
