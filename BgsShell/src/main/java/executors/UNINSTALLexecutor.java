package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

import java.util.List;

public class UNINSTALLexecutor extends CommonCommandExecutor {
    public UNINSTALLexecutor(List<String> args) {
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
        if (whetherJRC(filename)) {
            result.setCode(StatusCode.ERROR);
            result.setDescription("You should not delete JRC midlet!");
            return result;
        }

        boolean status = bgsEntity.uninstallMidlet(filename);
        if (status) {
            result.setCode(StatusCode.OK);
        } else {
            result.setCode(StatusCode.ERROR);
            result.setDescription("Could not install midlet. Try one more time.");
        }

        return result;
    }
    private boolean whetherJRC(String jrcCandidate) {
        final String DEFAULT_JAD_REGEX = "(.*)(JRC)(.+)(jad)(.*)";
        if (jrcCandidate.matches(DEFAULT_JAD_REGEX))
            return true;
        return false;
    }
}
