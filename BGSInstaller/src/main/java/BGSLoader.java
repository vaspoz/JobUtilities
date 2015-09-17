import Properties.Configuration;
import core.COMPort;
import jssc.SerialPortException;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BGSLoader {
    private final Logger log = LoggerFactory.getLogger(BGSLoader.class);

    private COMPort port;
    private Configuration config;
    private String portName;
    private File jadFile;
    private File jarFile;

    private final String DEFAULT_JAD_REGEX = "(.*)(JRC)(.+)(jad)(.*)";
    private final String AUTOSTART_OFF = "AT^SCFG=\"Userware/Autostart\",\"\",\"0\"";
    private final String AUTOSTART_ON = "AT^SCFG=\"Userware/Autostart\",\"\",\"1\"";
    private final String INSTALLED_LIST = "AT^SJAM=4";
    private final String DOWNLOAD_COMMAND = "AT^SJDL=1,";
    private final String GET_IMEI = "AT+GSN";

    public BGSLoader() {
        config = ConfigFactory.create(Configuration.class);
        portName = config.portName();
        port = new COMPort(portName);
    }

    public void openConnection() throws SerialPortException{
        port.open();
        log.info("Com-port has been opened.");
    }

    public void setPreConfiguration() {
        log.info("================ Setup configuration =================");
        log.info("Setup ports ASC1 and ASC0..");
        List<String> answers = port.sendMessage("AT^SCFG=\"Serial/Interface/Allocation\",\"1\"");
        showResult(answers);

        log.info("Mapping ASC1 to standard output..");
        answers = port.sendMessage("AT^SCFG=\"GPIO/mode/ASC1\",\"std\"");
        showResult(answers);

        log.info("Mode \"Save energy\" turning off..");
        answers = port.sendMessage("AT^SPOW=1,0,0");
        showResult(answers);

        log.info("Turning on logging to file..");
        answers = port.sendMessage("AT^SCFG=\"Userware/Stdout\",\"FILE\",\"65535\",\"a:/logs/log.txt\",\"buffered\"");
        showResult(answers);

        log.info("======================================================");
    }


    public void startJDCandAutostartOFF() {
        log.info("Starting JRC midlet (if necessary).");
        startJDC();
        log.info("OK.");
        log.info("Turn autostart off..");

        List<String> answers = port.sendMessage(AUTOSTART_OFF);
        showResult(answers);
    }

    private void startJDC() {
        List<String> running = port.sendMessage("AT^SJAM=5");
        for (String s : running) {
            if (s.contains("\"a:/JRC-1.50.3.jad\""))
                return;
        }
        port.sendMessage("AT^SJAM=1,\"a:/JRC-1.50.3.jad\",\"\"");
    }

    public void deleteInstalledFirmwares() {
        log.info("Find out list of installed midlets.");
        List<String> answers = port.sendMessage(INSTALLED_LIST);

        boolean didntFindInstalledMidlets = true;
        for (String answer : answers) {
            String jadName = matchAndGetStringOrNull(answer, "jad");


            if (jadName == null || jadName.matches(DEFAULT_JAD_REGEX)) {
                continue;
            }

            didntFindInstalledMidlets = false;
            log.info("Found midlet: " + jadName);

            log.info("> Turning off midlet..");
            List<String> received = port.sendMessage("AT^SJAM=2," + jadName + ",\"\"");
            showResult(received);

            log.info("> Uninstall..");
            received = port.sendMessage("AT^SJAM=3," + jadName + ",\"\"");
            showResult(received);
        }

        if (didntFindInstalledMidlets) {
            log.info("> Could not find installed midlets.");
        }
    }

    private String matchAndGetStringOrNull(String string, String fileExtension) {
        Pattern pattern = Pattern.compile("(\").+(" + fileExtension + ")");
        Matcher matcher = pattern.matcher(string);

        if (matcher.find()) {
            String result = matcher.group().substring(1);
            return result;
        }
        return null;
    }

    public void deleteExistingFiles() {
        log.info("Delete existing *.jad and *.jar files");
        log.info("> Observe..");

        List<String> answers = port.sendMessage("AT^SFSA=\"ls\",\"a:/tpp\"");
        boolean didntFoundJad = true;
        for (String answer : answers) {
            String fileName = matchAndGetStringOrNull(answer, "jad|jar");

            if (fileName == null) {
                continue;
            }

            didntFoundJad = false;
            log.info("> Remove file: " + fileName);
            List<String> removeAnswer = port.sendMessage("AT^SFSA=\"remove\",\"a:/tpp/" + fileName + "\"");
            showResult(removeAnswer);
        }
        if (didntFoundJad) {
            log.info("> Could not find files to remove.");
        }
    }

    public void sendFiles() {
        File[] files = new File("build\\" + config.buildDirectory()).listFiles();
        parseFiles(files);

        sendFile(jadFile);
        sendFile(jarFile);
    }

    private void parseFiles(File[] files) {
        for (File file : files) {
            if (file.getName().matches(".+jad")) {
                jadFile = file;
            }
            if (file.getName().matches(".+jar")) {
                jarFile = file;
            }
        }
    }

    private void sendFile(File file) {
        String fileName = file.getName();
        log.info("Send file: " + fileName);

        List<String> answers = port.sendMessage(DOWNLOAD_COMMAND + file.length() + "," + "\"tpp/" + fileName + "\"");
        if (!answers.contains("CONNECT")) {
            return;
        }

        answers = port.sendFile(file);
        showResult(answers);
    }

    public void installMidlet() {
        String midletName = jadFile.getName();
        log.info("Installing midlet: " + midletName);

        List<String> answers = port.sendMessage("AT^SJAM=0,\"a:/tpp/" + midletName + "\",\"\" ");
        showResult(answers);
    }

    public void showIMEI() {
        List<String> answers = port.sendMessage(GET_IMEI);
        String IMEI = answers.get(1);
        log.info("IMEI: " + IMEI);
    }

    public void closeConnection() throws IOException {
        log.info("Turn autostart on...");
        List<String> answers = port.sendMessage(AUTOSTART_ON);
        showResult(answers);
        port.close();

        log.info("Setup midlet has been done!");

        log.info("Press any key...");
        System.out.print("> ");
        System.in.read();
    }

    private void showResult(List<String> results) {
        if (results.contains("OK")) {
            log.info("> OK");
        } else {
            log.info("> FAIL");
        }

    }
}
