package shellMain;

import Enums.StatusCode;
import core.BGSEntity;
import core.COMPort;
import jssc.SerialPortException;

public class ExecutorManager {
    private CommonCommandExecutor executor;
    private ExecutionResult executionResult;
    private BGSEntity bgsEntity;
    private Object additionalInfo;

    private ExecutorManager() {
        executionResult = new ExecutionResult();
    }

    public static ExecutorManager create() {
        return new ExecutorManager();
    }


    public ExecutionResult init(COMPort port) {
        try {
            port.open();
            executionResult.setCode(StatusCode.OK);
        } catch (SerialPortException e) {
            executionResult.setCode(StatusCode.EXIT);
            executionResult.setDescription("Can not open port.");
        }

        bgsEntity = BGSEntity.getEntity(port);
        return executionResult;
    }

    public void injectExecutor(CommonCommandExecutor executor) {
        this.executor = executor;
        this.executor.setBGSEntity(bgsEntity);
        this.executor.setAdditionalInfo(additionalInfo);
    }

    public ExecutionResult executeCommand() {
        ExecutionResult result =  executor.execute();
        additionalInfo = result.getAdditionalInfo();
        return result;
    }

}
