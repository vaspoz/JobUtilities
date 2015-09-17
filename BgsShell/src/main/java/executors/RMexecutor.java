package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

import java.util.List;

public class RMexecutor extends CommonCommandExecutor {
    public RMexecutor(List<String> args) {
        this.args = args;
    }

    @Override
    public ExecutionResult execute() {
        int lineNumber = getNumber(args.get(0));

        if (lineNumber != ERROR_CODE)
            return removeByLineNumber(lineNumber);
        else
            return removeByFilename();
    }

    private ExecutionResult removeByLineNumber(int lineNumber) {
        List<String> dirContains = (List<String>) additionalInfo;
        String fileName = dirContains.get(lineNumber - 1);

        boolean removeResult = bgsEntity.removeFile(fileName);

        if (removeResult) {
            result.setCode(StatusCode.OK);
            System.out.println("\t\"" + fileName + "\" has been deleted.");
        } else {
            result.setCode(StatusCode.ERROR);
            result.setDescription("Some files could not been deleted.");
            System.out.println("\t\"" + fileName + "\" has NOT been deleted.");
        }

        return result;
    }

    private ExecutionResult removeByFilename() {
        String possibleFileNameWithRegex = ".+" + args.get(0).replace("*", ".+") + ".+";
        String actualFileName;

        List<String> dirContains = (List<String>) additionalInfo;
        for (String dirElement : dirContains) {
            if (dirElement.matches(possibleFileNameWithRegex)) {
                actualFileName = dirElement;

                boolean removeResult = bgsEntity.removeFile(actualFileName);

                if (removeResult) {
                    result.setCode(StatusCode.OK);
                    System.out.println("\t\"" + actualFileName + "\" has been deleted.");
                } else {
                    result.setCode(StatusCode.ERROR);
                    result.setDescription("Some files could not been deleted.");
                    System.out.println("\t\"" + actualFileName + "\" has NOT been deleted.");
                }
            }
        }

        return result;
    }
}
