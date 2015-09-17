import core.COMPort;
import jssc.SerialPortException;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BGSLogLoader {
    private static final Logger log = LoggerFactory.getLogger(BGSLogLoader.class);

    private COMPort port;
    private Configuration cfg;

    private final int MAX_BYTES = 1500;
    private final String AT_OPEN_STREAM = "AT^SFSA=\"open\",\"";
    private final String READ_FLAG = "\",8";
    private final String AT_READ_FILE = "AT^SFSA=\"read\",";
    private final String MAX_BYTE_COUNT = "," + MAX_BYTES;
    private final String AT_CLOSE_STREAM = "AT^SFSA=\"close\",";
    private final String AT_STAT = "AT^SFSA=\"stat\",";

    private final int SESSION_ID_INDEX = 0;
    private final int STATUS_INDEX = 1;

    private int sessionID = 0;
    private int fileSize;

    private String DELIMITER = "========================================";

    public BGSLogLoader() {
        cfg = ConfigFactory.create(Configuration.class);
        String portName = cfg.comport();
        port = new COMPort(portName);
    }

    public void loadLogFiles() throws SerialPortException{
        openPort();
        startLoadingFiles();
        closePort();
    }

    private void openPort() throws SerialPortException{
        port.open();
        log.info("ComPort has been opened.");
    }

    private void startLoadingFiles() {
        List<LogFile> logFilesForDownload = getListFromConfiguration();

        showDownloadFiles(logFilesForDownload);

        for (LogFile logFile : logFilesForDownload) {
            log.info("Start downloading file: " + logFile.getAbsPath());
            loadFile(logFile);
            log.info(DELIMITER);
        }
    }

    private List<LogFile> getListFromConfiguration() {
        List<LogFile> logList = new ArrayList<>();

        if (cfg.isDebug())
            logList.add(LogFile.DEBUG);
        if (cfg.isDebug0())
            logList.add(LogFile.DEBUG0);
        if (cfg.isDebug1())
            logList.add(LogFile.DEBUG1);
        if (cfg.isDebug2())
            logList.add(LogFile.DEBUG2);
        if (cfg.isDebug3())
            logList.add(LogFile.DEBUG3);
        if (cfg.isInfo())
            logList.add(LogFile.INFO);
        if (cfg.isInfo0())
            logList.add(LogFile.INFO0);
        if (cfg.isInfo1())
            logList.add(LogFile.INFO1);
        if (cfg.isInfo2())
            logList.add(LogFile.INFO2);
        if (cfg.isInfo3())
            logList.add(LogFile.INFO3);
        if (cfg.isError())
            logList.add(LogFile.ERROR);
        if (cfg.isError0())
            logList.add(LogFile.ERROR0);
        if (cfg.isError1())
            logList.add(LogFile.ERROR1);
        if (cfg.isError2())
            logList.add(LogFile.ERROR2);
        if (cfg.isError3())
            logList.add(LogFile.ERROR3);

        return logList;
    }

    private void showDownloadFiles(List<LogFile> files) {
        log.info("Filelist for download:");
        for (LogFile logFile : files) {
            log.info("\t" + logFile.getFileName());
        }
        log.info(DELIMITER);
    }

    public void loadFile(LogFile file) {
        port.sendMessage(AT_CLOSE_STREAM + sessionID);

        String openCommand = AT_OPEN_STREAM + file.getAbsPath() + READ_FLAG;
        log.info("Send open at-command: " + openCommand);
        List<String> answer = port.sendMessage(openCommand);

        if (answerStatusIsWrong(answer))
            return;

        sessionID = getSessionIDAndStatus(answer)[SESSION_ID_INDEX];
        try {
            fileSize = getFileSize(file);
        } catch (InterruptedIOException e) {
            e.printStackTrace();
            return;
        }

        File outFile = new File("Downloaded\\" + file.getFileName());
        downLoadFileTo(outFile);

        String closeCommand = AT_CLOSE_STREAM + "," + sessionID;
        log.info("Send close at-command: " + closeCommand);
        port.sendMessage(closeCommand);
    }

    private boolean answerStatusIsWrong(List<String> answer) {
        int[] parsedResult = getSessionIDAndStatus(answer);

        if (parsedResult[STATUS_INDEX] != 0) {
            log.warn("Error occurred");
            return true;
        }

        return false;
    }

    private int[] getSessionIDAndStatus(List<String> answers) {
        int[] ss = new int[2];
        ss[SESSION_ID_INDEX] = -1;
        ss[STATUS_INDEX] = -1;

        for (String answer : answers) {
            if (!answer.startsWith("^SFSA:"))
                continue;

            answer = answer.replace("^SFSA: ", "").replace("\r", "");
            String[] splitted = answer.split(",");

            ss[SESSION_ID_INDEX] = Integer.valueOf(splitted[0]);
            ss[STATUS_INDEX] = Integer.valueOf(splitted[1]);
        }

        return ss;
    }

    private int getFileSize(LogFile file) throws InterruptedIOException {
        List<String> answer = port.sendMessage(AT_STAT + "\"" + file.getAbsPath() + "\"");
        for (String line : answer)
            if (line.startsWith("^SFSA: ")) {
                line = line.replace("^SFSA: ", "").replace("\r", "");
                return Integer.valueOf(line);
            }

        throw new InterruptedIOException("Wrong 'stat' answer: " + answer);
    }

    private void downLoadFileTo(File destinationFile) {
        try {
            FileWriter writer = new FileWriter(destinationFile);
            BufferedWriter out = new BufferedWriter(writer);

            boolean isBreak = false;

            int receivedBytes = 0;

            while (true) {
                List<String> answer = port.sendMessage(AT_READ_FILE + sessionID + MAX_BYTE_COUNT);

                String lineWithBytesCount = answer.get(1);
                int bytes = 0;
                try {
                    bytes = getAmountOfBytes(lineWithBytesCount);
                } catch (Exception e) {
                    System.out.println("! Received answer for \"read file\" command:");
                    for (String s : answer)
                        System.out.println(s);
                    throw new AssertionError();
                }
                receivedBytes += bytes;
                if (bytes < MAX_BYTES)
                    isBreak = true;

                //first, second and the last lines contains AT-command info, thus it is useless information
                answer.remove(0);
                answer.remove(0);
                answer.remove(answer.size()-1);

                for (String line : answer) {
                    out.write(line);
                    out.newLine();
                }

                log.info("File size:\t" + fileSize + " bytes. Received: " + receivedBytes + " bytes.");

                if (isBreak)
                    break;
            }

            out.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private int getAmountOfBytes(String string) {
        string = string.replace("^SFSA: ", "");
        String[] splitted = string.split(",");

        return Integer.valueOf(splitted[0]);
    }

    private void closePort() {
        port.close();
        log.info("ComPort has been closed.");
    }
}
