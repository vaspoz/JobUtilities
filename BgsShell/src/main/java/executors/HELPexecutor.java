package executors;

import Enums.StatusCode;
import shellMain.CommonCommandExecutor;
import shellMain.ExecutionResult;

public class HELPexecutor extends CommonCommandExecutor {
    @Override
    public ExecutionResult execute() {
        StringBuffer help = new StringBuffer();
        help.append("\t").append(format("ls <path>")).
                append("Show list of files in <path>.").append("\n\n");

        help.append("\t").append(format("rm ( <part_name> | <line number> )")).
                append("Delete all files that included <part_name>. Or you could remove exact file from <line number>.").append("\n\n");

        help.append("\t").append(format("upconf")).
                append("Copy all files from \"ftp\\\" to \"a:/tpp/\"").append("\n\n");

        help.append("\t").append(format("dl ( <part_name> | <line number> )")).
                append("Download all files that included <part_name>. Or you could download exact file from <line number>.").append("\n\n");

        help.append("\t").append(format("cat <line number>")).append("Show file in <line number> from previous command.").append("\n\n");

        help.append("\t").append("get <command>").append("\n")
                .append("\t\t").append(format("imei")).append("Will return IMEI.").append("\n")
                .append("\t\t").append(format("run")).append("Will return list of running midlets.").append("\n")
                .append("\t\t").append(format("install")).append("Will return list of installed midlets.").append("\n\n");

        System.out.println(help);

        result.setCode(StatusCode.OK);
        return result;
    }

    private String format(String s) {
        return String.format("%-40s", s);
    }
}
