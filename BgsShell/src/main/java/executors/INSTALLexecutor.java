package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

import java.util.List;

public class INSTALLexecutor extends CommonCommandExecutor {
    public INSTALLexecutor(List<String> args) {
        this.args = args;
    }
    @Override
    public ExecutionResult execute() {
        int lineNumber = getNumber(args.get(0));
        if (lineNumber == ERROR_CODE) {
            result.setCode(StatusCode.ERROR);
            result.setDescription("You should type the line number.");
            return result;
        }

        List<String> containes = (List<String>) additionalInfo;
        String filename = containes.get(lineNumber - 1);

        boolean status = bgsEntity.installMidlet(filename);
        if (status) {
            result.setCode(StatusCode.OK);
        } else {
            result.setCode(StatusCode.ERROR);
            result.setDescription("Could not install midlet. Try one more time.");
        }

        return result;
    }
}
