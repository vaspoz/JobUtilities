public enum LogFile {
    DEBUG   ("a:/logs/debug.log", "debug.log"),
    DEBUG0  ("a:/logs/debug0.log", "debug0.log"),
    DEBUG1  ("a:/logs/debug1.log", "debug1.log"),
    DEBUG2  ("a:/logs/debug2.log", "debug2.log"),
    DEBUG3  ("a:/logs/debug3.log", "debug3.log"),
    ERROR   ("a:/logs/error.log", "error.log"),
    ERROR0  ("a:/logs/error0.log", "error0.log"),
    ERROR1  ("a:/logs/error1.log", "error1.log"),
    ERROR2  ("a:/logs/error2.log", "error2.log"),
    ERROR3  ("a:/logs/error3.log", "error3.log"),
    INFO    ("a:/logs/info.log", "info.log"),
    INFO0   ("a:/logs/info0.log", "info0.log"),
    INFO1   ("a:/logs/info1.log", "info1.log"),
    INFO2   ("a:/logs/info2.log", "info2.log"),
    INFO3   ("a:/logs/info3.log", "info3.log");

    private String absPath;
    private String fileName;

    LogFile(String absPath, String filename) {
        this.absPath = absPath;
        this.fileName = filename;
    }

    public String getAbsPath() {
        return absPath;
    }

    public String getFileName() {
        return fileName;
    }
}
