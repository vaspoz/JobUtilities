package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

import java.util.List;

public class LSexecutor extends CommonCommandExecutor {
    public LSexecutor(List<String> args) {
        this.args = args;
    }

    @Override
    public ExecutionResult execute() {
        String path = args.get(0);
        if (!path.startsWith("a:/")) {
            result.setCode(StatusCode.ERROR);
            result.setDescription("Path should starts with \"a:/\".");
            return result;
        }

        if (!path.endsWith("/")) {
            path += "/";
            args.set(0, path);
        }

        List<String> fileList = bgsEntity.getFileList(path);
        if (fileList.isEmpty()) {
            result.setCode(StatusCode.ERROR);
            result.setDescription("Could not receive any list. Try again.");
            return result;
        }

        showList(fileList);

        result.setAdditionalInfo(fileList);
        result.setCode(StatusCode.OK);
        return result;
    }
}
