package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

public class GET_IMEIexecutor extends CommonCommandExecutor {
    @Override
    public ExecutionResult execute() {
        String imei = bgsEntity.getIMEI();
        if (imei.toLowerCase().equals("error")) {
            result.setCode(StatusCode.ERROR);
            result.setDescription("Could not receive IMEI.");
            return result;
        }

        System.out.println("\tIMEI: " + imei);
        result.setCode(StatusCode.OK);
        return result;

    }
}
