package Enums;

public enum ShellCommands {
    AT ("at"),
    LS ("ls"),
    RM ("rm"),
    DL ("dl"),
    UP_CONF ("upconf"),
    CAT ("cat"),
    HELP ("help"),
    GET ("get"),
    GET_IMEI ("imei"),
    GET_INSTALLED ("install"),
    GET_RUNNED ("run"),
    START ("start"),
    STOP ("stop"),
    INSTALL ("install"),
    UNINSTALL ("uninstall"),
    QUIT ("quit"),
    EMPTY (""),
    EXIT ("exit"),
    UNKNOWN ("unk");

    private String command;

    ShellCommands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static ShellCommands getByCommand(String command) {
        for (ShellCommands shellCommand : ShellCommands.values()) {
            if (shellCommand.getCommand().equals(command))
                return shellCommand;
        }

        return ShellCommands.UNKNOWN;
    }
}
