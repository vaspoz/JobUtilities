package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

public class EXITexecutor extends CommonCommandExecutor {
    @Override
    public ExecutionResult execute() {
        bgsEntity.deactivate();
        result.setCode(StatusCode.EXIT);
        result.setDescription("Bye!");
        return result;
    }
}
