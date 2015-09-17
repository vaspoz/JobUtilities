package shellMain;

import Enums.ShellCommands;
import Enums.StatusCode;
import core.COMPort;

import java.util.Scanner;

public class BGSShell {
    private final String PROMPT = "> ";
    private final Scanner scanner;
    private ShellCommands shellCommand;
    private StatusCode statusCode;

    ResultHandler resultHandler;
    ExecutorManager manager;
    CommonCommandExecutor executor;

    public BGSShell() {
        scanner = new Scanner(System.in);
        manager = ExecutorManager.create();
        resultHandler = ResultHandler.create();
    }

    public void startShell() {
        COMPort port = customizePort();
        ExecutionResult result = manager.init(port);
        resultHandler.handle(result);

        while (true) {
            System.out.print(PROMPT);

            Command command =  customizeCommand();
            if (command.getCommand() == ShellCommands.EMPTY)
                continue;

            executor = CommandExecutorFactory.getExecutorForCommand(command);

            manager.injectExecutor(executor);

            result = manager.executeCommand();

            resultHandler.handle(result);
        }

    }

    private COMPort customizePort() {
        System.out.print("Enter port name: ");
        String portName = scanner.nextLine();
        if (portName.equals("")) {
            System.out.println("Unknown port.");
            System.exit(0);
        }

        if (!portName.startsWith("COM"))
            portName = "COM" + portName;

        return new COMPort(portName);
    }

    private Command customizeCommand() {
        Command command = new Command();

        String enteredString = scanner.nextLine();
        if (enteredString.equals("")) {
            command.setCommand(ShellCommands.EMPTY);
            return command;
        }

        command.parse(enteredString);
        return command;
    }

    public static void main(String[] args) {
        BGSShell shell2 = new BGSShell();
        shell2.startShell();
    }
}
