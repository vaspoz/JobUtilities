package shellMain;

import Enums.ShellCommands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Command {
    private List<String> args = Collections.emptyList();
    private ShellCommands command = ShellCommands.EMPTY;

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public void setCommand(ShellCommands command) {
        this.command = command;
    }

    public List<String> getArgs() {
        return args;
    }

    public ShellCommands getCommand() {
        return command;
    }

    public void parse(String inputString) {
        command = parseCommandFromString(inputString);
        args = parseArgsFromString(inputString);
    }

    private ShellCommands parseCommandFromString(String command) {
        command = command.trim().toLowerCase();
        String[] splitted = command.split(" ");
        command = splitted[0];

        return ShellCommands.getByCommand(command);
    }

    private List<String> parseArgsFromString(String command) {
        command = command.trim();
        String[] splitted = command.split(" ");
        String[] args = Arrays.copyOfRange(splitted, 1, splitted.length);

        return Arrays.asList(args);
    }
}
