package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

public class UNKNOWNexecutor extends CommonCommandExecutor {
    @Override
    public ExecutionResult execute() {
        result.setCode(StatusCode.ERROR);
        result.setDescription("Unknown command. Try \"help\"");

        return result;
    }
}
