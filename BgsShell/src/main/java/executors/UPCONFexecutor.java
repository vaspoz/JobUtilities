package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

import java.io.File;
import java.util.List;

public class UPCONFexecutor extends CommonCommandExecutor {
    public UPCONFexecutor(List<String> args) {
        this.args = args;
    }

    @Override
    public ExecutionResult execute() {
        File[] filesFromFtpFolder = new File("ftp\\").listFiles();
        if (filesFromFtpFolder == null) {
            result.setDescription("There is no files in \"ftp\" folder.");
            result.setCode(StatusCode.ERROR);
            return result;
        }

        boolean copyResult = true;
        for (File file : filesFromFtpFolder) {
            if (file.isDirectory())
                continue;

            boolean removeResult = bgsEntity.removeFile("a:/tpp/" + file.getName());
            if (!removeResult) {
                result.setCode(StatusCode.ERROR);
                result.setDescription("Could not delete file \"" + file.getName() + "\" from commod.");
                return result;
            }

            copyResult &= bgsEntity.sendFile(file);
            if (copyResult)
                System.out.println("\tFile \"" + file.getName() + "\" has been sent.");
            else
                System.out.println("\tError occurred during sending file \"" + file.getName() + "\"");
        }

        result.setCode(copyResult ? StatusCode.OK : StatusCode.ERROR);
        return  result;
    }
}
