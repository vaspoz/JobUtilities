package shellMain;

public class ResultHandler {
    private ResultHandler() {
    }

    public static ResultHandler create() {
        return new ResultHandler();
    }

    public void handle(ExecutionResult result) {
        switch (result.getCode()) {
            case EXIT:
                System.out.println(result.getDescription());
                System.exit(0);
                break;
            case OK:
                System.out.println("\t[ok]");
                System.out.println();
                break;
            case ERROR:
                System.out.println("\t[error]");
                System.out.println("\t[" + result.getDescription() + "]");
                System.out.println();
                break;
            default:
        }
    }
}
