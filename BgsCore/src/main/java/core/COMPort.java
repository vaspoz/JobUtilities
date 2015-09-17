package core;

import Utils.StringUtils;
import jssc.SerialNativeInterface;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class COMPort {
    private SerialPort serialPort;
    private ComListener listener;
    private static SerialNativeInterface nativeInterface;

    public COMPort(String port) {
        serialPort = new SerialPort(port);
        listener = new ComListener(serialPort);
        nativeInterface = new SerialNativeInterface();
    }

    public void open() throws SerialPortException{
        tryToOpenPort();
    }

    public void close() {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private void tryToOpenPort() throws SerialPortException {
        serialPort.openPort();
        serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.addEventListener(listener);
    }

    public List<String> sendMessage(String command) {
        command += "\r\n";

        try {
            serialPort.writeBytes(command.getBytes());
            String receivedResult = "";

            while (!receivedResult.contains("OK") &&
                    !receivedResult.contains("ERROR") &&
                    !receivedResult.contains("CONNECT")) {
                byte[] receivedData = listener.getReceivedBuffer();
                if (receivedData == null) {
                    continue;
                }
                receivedResult = StringUtils.asciiToString(receivedData);
            }
            listener.clearReceivedBuffer();

            List<String> answer = Arrays.asList(receivedResult.split("\n"));

            List<String> cleanedAnswer = cleanAnswerAndReturn(answer);

            return cleanedAnswer;
        } catch (SerialPortException spe) {
            spe.printStackTrace();
            return null;
        }
    }

    private List<String> cleanAnswerAndReturn(List<String> answer) {
        List<String> cleaned = new ArrayList<>();
        for (String s : answer) {
            s = s.replaceAll("\r", "").trim();
            if (s != "")
                cleaned.add(s);
        }

        return cleaned;
    }

    public List<String> sendFile(File file) {
        try {
            InputStream input = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = input.read(buf)) == 1024) {
                serialPort.writeBytes(buf);
            }
            buf = Arrays.copyOf(buf, len);
            serialPort.writeBytes(buf);

            String receivedResult = "";

            while (!receivedResult.contains("OK") &&
                    !receivedResult.contains("ERROR") &&
                    !receivedResult.contains("CONNECT")) {
                byte[] receivedData = listener.getReceivedBuffer();
                if (receivedData == null) {
                    continue;
                }
                receivedResult = StringUtils.asciiToString(receivedData);
            }
            listener.clearReceivedBuffer();

            List<String> answer = Arrays.asList(receivedResult.split("\n"));
            List<String> cleanedAnswer = cleanAnswerAndReturn(answer);

            return cleanedAnswer;

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SerialPortException spe) {
            spe.printStackTrace();
        }
        return null;
    }
}
