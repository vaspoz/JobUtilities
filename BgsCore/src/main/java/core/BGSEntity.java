package core;

import com.google.common.io.Files;
import jssc.SerialPortException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BGSEntity {
    private COMPort port;

    private final String AT_LS = "AT^SFSA=\"ls\",";
    private final String AT_REMOVE = "AT^SFSA=\"remove\",";
    private final int MAX_BYTES = 1500;
    private final String AT_OPEN_STREAM = "AT^SFSA=\"open\",\"";
    private final String READ_FLAG = "\",8";
    private final String AT_READ_FILE = "AT^SFSA=\"read\",";
    private final String MAX_BYTE_COUNT = "," + MAX_BYTES;
    private final String AT_CLOSE_STREAM = "AT^SFSA=\"close\",";
    private final String AT_STAT = "AT^SFSA=\"stat\",";
    private final String JRC_REGEX = "(.*)(JRC)(.+)(jad)(.*)";
    private final String AUTOSTART_OFF = "AT^SCFG=\"Userware/Autostart\",\"\",\"0\"";
    private final String AUTOSTART_ON = "AT^SCFG=\"Userware/Autostart\",\"\",\"1\"";
    private final String AT_INSTALLED_LIST = "AT^SJAM=4";
    private final String AT_RUNNED_LIST = "AT^SJAM=5";
    private final String AT_DOWNLOAD_COMMAND = "AT^SJDL=1,";
    private final String AT_GET_IMEI = "AT+GSN";
    private final String AT_RUN_MIDLET = "AT^SJAM=1,";
    private final String AT_STOP_MIDLET = "AT^SJAM=2,";
    private final String AT_INSTALL_MIDLET = "AT^SJAM=0,";
    private final String AT_UNINSTALL_MIDLET = "AT^SJAM=3,";

    private final int SESSION_ID_INDEX = 0;
    private final int STATUS_INDEX = 1;
    private int sessionID = 0;
    private int fileSize;

    private BGSEntity(COMPort port) {
        this.port = port;
    }

    public static BGSEntity getEntity(COMPort port) {
        return new BGSEntity(port);
    }

    public void activate() throws SerialPortException {
        port.open();
    }

    public void deactivate() {
        port.close();
    }

    public boolean AT() {
        List<String> answers = port.sendMessage("AT");
        if (answers.contains("OK"))
            return true;
        return false;
    }

    public void setAutostartON() {

    }

    public void setAutostartOFF() {

    }

    public List<String> getInstalledMidlets() {
        List<String> answers = port.sendMessage(AT_INSTALLED_LIST);
        return cleanAnswerFromSJAM(answers);
    }

    public List<String> getRunnedMidlets() {
        List<String> answer = port.sendMessage(AT_RUNNED_LIST);
        return cleanAnswerFromSJAM(answer);
    }

    public boolean installMidlet(String midletName) {
        List<String> answers = port.sendMessage(AT_INSTALL_MIDLET + midletName + ",\"\"");

        if (answers.contains("ERROR"))
            return false;

        return true;
    }

    public boolean uninstallMidlet(String midletName) {
        if (midletName.matches(JRC_REGEX))
            return false;
        List<String> answers = port.sendMessage(AT_UNINSTALL_MIDLET + midletName + ",\"\"");

        if (answers.contains("ERROR"))
            return false;

        return true;
    }

    public boolean runMidlet(String midletName) {
        List<String> answers = port.sendMessage(AT_RUN_MIDLET + midletName + ",\"\"");

        if (answers.contains("ERROR"))
            return false;

        return true;
    }

    public boolean stopMidlet(String midletName) {
        List<String> answers = port.sendMessage(AT_STOP_MIDLET + midletName + ",\"\"");

        if (answers.contains("ERROR"))
            return false;

        return true;
    }

    public String getIMEI() {
        List<String> imeiReceived = port.sendMessage(AT_GET_IMEI);
        if (imeiReceived.contains("ERROR"))
            return "ERROR";

        return imeiReceived.get(1);
    }

    public List<String> getFileList(String path) {
        String pathToSend = "\"" + path + "\"";

        List<String> answers = port.sendMessage(AT_LS + pathToSend);
        if (answers.contains("ERROR"))
            return Collections.emptyList();

        answers = cleanAnswerFromSFSA(answers);
        answers = addPath(answers, path);
        return answers;
    }

    public boolean removeFile(String absoluteFilepath) {
        absoluteFilepath = "\"" + absoluteFilepath + "\"";
        List<String> removeCommandAnswer = port.sendMessage(AT_REMOVE + absoluteFilepath);

        if (removeCommandAnswer.contains("ERROR"))
            return false;
        return true;

    }

    public StringBuilder catFile(String absoluteFilepath) {
        StringBuilder file = new StringBuilder();

        File receivedFile = downloadFile(absoluteFilepath);

        BufferedReader reader = getReaderFrom(receivedFile);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                file.append(line);
                file.append("\n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    private BufferedReader getReaderFrom(File file) {
        try {
            return new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            throw new AssertionError();
        }
    }

    public boolean sendFile(File file) {
        String filename = file.getName();
        List<String> openStreamAnswer = port.sendMessage(AT_DOWNLOAD_COMMAND + file.length() +
                "," + "\"tpp/" + filename + "\"");
        if (!openStreamAnswer.contains("CONNECT"))
            return false;

        List<String> sendFileAnswer = port.sendFile(file);
        if (sendFileAnswer.contains("OK"))
            return true;

        return false;
    }

    public File downloadFile(String absoluteFilename) {

        String fileName = getFilename(absoluteFilename);
        File downloadedFile = new File(Files.createTempDir().getAbsolutePath() + "\\" + fileName);

        boolean openResult = openStreamAndSaveSessionID(absoluteFilename);
        if (!openResult) {
            try {
                downloadedFile.createNewFile();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return downloadedFile;
        }

        fileSize = getFileSize(absoluteFilename);
        writeFromStreamTo(downloadedFile);

        port.sendMessage(AT_CLOSE_STREAM + sessionID);

        return downloadedFile;
    }

    private String getFilename(String absoluteFilename) {
        int slashIndex = absoluteFilename.lastIndexOf("\\");
        boolean linuxSlashType = slashIndex > 0;

        if (!linuxSlashType)
            slashIndex = absoluteFilename.lastIndexOf("/");

        return absoluteFilename.substring(slashIndex + 1);
    }

    private boolean openStreamAndSaveSessionID(String fileAbsolutePath) {
        int DEFAULT_STREAM = 0;
        port.sendMessage(AT_CLOSE_STREAM + DEFAULT_STREAM);

        List<String> openCommandAnswer = port.sendMessage(AT_OPEN_STREAM + fileAbsolutePath + READ_FLAG);
        if (openCommandAnswer.contains("ERROR"))
            return false;
        if (openStreamStatusIsWrong(openCommandAnswer))
            return false;

        sessionID = getSessionIDAndStatus(openCommandAnswer)[SESSION_ID_INDEX];

        return true;
    }

    private boolean openStreamStatusIsWrong(List<String> answer) {
        int[] parsedResult = getSessionIDAndStatus(answer);

        return parsedResult[STATUS_INDEX] != 0;

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

    private int getFileSize(String absoluteFilepath) {
        List<String> answer = port.sendMessage(AT_STAT + "\"" + absoluteFilepath + "\"");
        for (String line : answer)
            if (line.startsWith("^SFSA: ")) {
                line = line.replace("^SFSA: ", "").replace("\r", "");
                return Integer.valueOf(line);
            }

        return getFileSize(absoluteFilepath);
    }

    private void writeFromStreamTo(File destinationFile) {
        try {
            FileWriter writer = new FileWriter(destinationFile);
            BufferedWriter out = new BufferedWriter(writer);

            boolean isBreak = false;

            int receivedBytes = 0;

            System.out.println("[" + destinationFile.getName() + "]");
            while (true) {
                List<String> answer = port.sendMessage(AT_READ_FILE + sessionID + MAX_BYTE_COUNT);

                String lineWithBytesCount = answer.get(1);
                int bytes = getAmountOfBytes(lineWithBytesCount);

                receivedBytes += bytes;
                if (bytes < MAX_BYTES)
                    isBreak = true;

                //first, second and the last lines contains AT-command info, thus it is useless information
                answer.remove(0);
                answer.remove(0);
                answer.remove(answer.size() - 1);

                for (String line : answer) {
                    out.write(line);
                    out.newLine();
                }

                System.out.println("\tFile size:\t" + fileSize + " bytes. Received: " + receivedBytes + " bytes.");

                if (isBreak)
                    break;
            }
            System.out.println();

            out.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    private int getAmountOfBytes(String string) {
        string = string.replace("^SFSA: ", "");
        String[] splitted = string.split(",");

        try {
            return Integer.valueOf(splitted[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    private List<String> cleanAnswerFromSFSA(List<String> list) {
        return cleanAnswer(list, "SFSA");
    }

    private List<String> cleanAnswerFromSJAM(List<String> list) {
        return cleanAnswer(list, "SJAM");
    }

    private List<String> cleanAnswer(List<String> list, String regex) {
        List<String> cleaned = new ArrayList<>();

        for (String line : list) {
            if (line.startsWith("AT^"))
                continue;
            if (!line.startsWith("^" + regex))
                continue;

            line = line.replaceAll("\"", "");
            line = line.replaceAll("\\^" + regex + ": ", "");

            if (!line.equals("0"))
                cleaned.add(line);
        }

        return cleaned;
    }

    private List<String> addPath(List<String> list, String path) {
        List<String> absolutePathList = new ArrayList<>();
        for (String file : list) {
            absolutePathList.add(path + file);
        }
        return absolutePathList;
    }
}
