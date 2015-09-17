package executors;

import Enums.StatusCode;
import com.google.common.io.Files;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DLexecutor extends CommonCommandExecutor {
    private File folderToDownload;

    public DLexecutor(List<String> args) {
        this.args = args;
    }

    @Override
    public ExecutionResult execute() {
        if (args.isEmpty()) {
            result.setCode(StatusCode.ERROR);
            result.setDescription("You should type filename, regex or line.");
            return result;
        }

        createFolder();

        int lineNumber = getNumber(args.get(0));
        if (lineNumber != ERROR_CODE)
            return downloadByLine(lineNumber);
        else
            return downloadByFileName();
    }

    private ExecutionResult downloadByLine(int lineNumber) {
        List<String> dirContains = (List<String>) additionalInfo;
        String filename = dirContains.get(lineNumber - 1);

        tryToDownloadFile(filename);
        return result;
    }

    private ExecutionResult downloadByFileName() {
        String possibleFileNameWithRegex = "a:/(logs|tpp).*" + args.get(0).replace("*", ".*") + ".*";
        String actualFileName;

        List<String> dirContains = (List<String>) additionalInfo;

        for (String dirElement : dirContains)
            if (dirElement.matches(possibleFileNameWithRegex)) {
                actualFileName = dirElement;

                tryToDownloadFile(actualFileName);
            }

        return result;
    }

    private void createFolder() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH-MM");
        String folderName = sdf.format(new Date());

        folderToDownload = new File("Downloaded\\" + folderName);
        folderToDownload.mkdir();
    }

    private void tryToDownloadFile(String filename) {
        File downloadedFile = bgsEntity.downloadFile(filename);

        if (downloadedFile.length() != 0) {
            saveFile(downloadedFile);
            result.setCode(StatusCode.OK);
            return;
        }

        result.setCode(StatusCode.ERROR);
        result.setDescription("Could not download file " + filename);
    }

    private void saveFile(File file) {
        try {
            Files.copy(file, new File(folderToDownload, file.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
