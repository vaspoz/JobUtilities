package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

import java.util.List;

public class CATexecutor extends CommonCommandExecutor {

    public CATexecutor(List<String> args) {
        this.args = args;
    }

    @Override
    public ExecutionResult execute() {

        int lineCount = getNumber(args.get(0));
        if (lineCount == ERROR_CODE) {
            result.setCode(StatusCode.ERROR);
            result.setDescription("You should type line number (result of previous command) with required file.");
            return result;
        }

        List<String> container = (List<String>) additionalInfo;
        String filename = container.get(lineCount - 1);

        StringBuilder file = bgsEntity.catFile(filename);
        System.out.println("=======================================");
        System.out.println(file.toString());
        System.out.println("=======================================");

        result.setCode(StatusCode.OK);
        return result;
    }
}
