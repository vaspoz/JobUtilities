package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

public class ATexecutor extends CommonCommandExecutor {
    @Override
    public ExecutionResult execute() {
        boolean at = bgsEntity.AT();

        if (at) {
            result.setCode(StatusCode.OK);
        } else {
            result.setCode(StatusCode.ERROR);
            result.setDescription("Could not receive at-respond.");
        }

        return result;
    }
}
