package executors;

import shellMain.Command;
import shellMain.CommandExecutorFactory;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

import java.util.List;

public class GETexecutor extends CommonCommandExecutor {
    CommonCommandExecutor getExecutor;

    public GETexecutor(List<String> args) {
        this.args = args;

        Command command = new Command();
        command.parse(args.get(0));
        getExecutor = CommandExecutorFactory.getExecutorForCommand(command);
    }
    @Override
    public ExecutionResult execute() {
        getExecutor.setBGSEntity(bgsEntity);
        getExecutor.setAdditionalInfo(additionalInfo);

        ExecutionResult internalResult = getExecutor.execute();
        result.setCode(internalResult.getCode());
        result.setDescription(internalResult.getDescription());
        result.setAdditionalInfo(internalResult.getAdditionalInfo());

        return result;
    }
}
