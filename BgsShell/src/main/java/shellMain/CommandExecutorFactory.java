package shellMain;

import executors.*;

public class CommandExecutorFactory {
    private CommandExecutorFactory() {
    }

    public static CommonCommandExecutor getExecutorForCommand(Command command) {
        switch (command.getCommand()) {
            case AT:
                return new ATexecutor();
            case LS:
                return new LSexecutor(command.getArgs());
            case RM:
                return new RMexecutor(command.getArgs());
            case UP_CONF:
                return new UPCONFexecutor(command.getArgs());
            case DL:
                return new DLexecutor(command.getArgs());
            case CAT:
                return new CATexecutor(command.getArgs());
            case START:
                return new STARTexecutor(command.getArgs());
            case STOP:
                return new STOPexecutor(command.getArgs());
            case INSTALL:
                return new INSTALLexecutor(command.getArgs());
            case UNINSTALL:
                return new UNINSTALLexecutor(command.getArgs());
            case HELP:
                return new HELPexecutor();
            case GET:
                return new GETexecutor(command.getArgs());
            case GET_IMEI:
                return new GET_IMEIexecutor();
            case GET_RUNNED:
                return new GET_RUNNEDexecutor();
            case GET_INSTALLED:
                return new GET_INSTALLEDexecutor();
            case QUIT:
            case EXIT:
                return new EXITexecutor();
            case UNKNOWN:
                return new UNKNOWNexecutor();
            default:
                throw new AssertionError();
        }
    }
}
