package core;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.concurrent.TimeUnit;

public class ComListener implements SerialPortEventListener {
    volatile private byte[] receivedBuffer;
    private SerialPort serialPort;

    public ComListener(SerialPort port) {
        serialPort = port;
    }

    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
            try {
                TimeUnit.SECONDS.sleep(2);
                receivedBuffer = serialPort.readBytes();
            } catch (SerialPortException spe) {
                spe.printStackTrace();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public byte[] getReceivedBuffer() {
        return receivedBuffer;
    }

    public void clearReceivedBuffer() {
        receivedBuffer = null;
    }
}
