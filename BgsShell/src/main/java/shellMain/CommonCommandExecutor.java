package shellMain;

import core.BGSEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonCommandExecutor {
    protected List<String> args;
    protected BGSEntity bgsEntity;
    protected Object additionalInfo;
    protected ExecutionResult result = new ExecutionResult();

    protected final int ERROR_CODE = -1;

    public abstract ExecutionResult execute();

    public void setBGSEntity(BGSEntity bgsEntity) {
        this.bgsEntity = bgsEntity;
    }

    public void setAdditionalInfo(Object info) {
        this.additionalInfo = info;
        result.setAdditionalInfo(additionalInfo);
    }

    protected void showList(List<String> list) {
        int lineCount = 1;
        for (String line : list) {
            System.out.println("\t" + lineCount++ + ". " + line);
        }
    }

    protected int getNumber(String s) {
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return ERROR_CODE;
        }
    }

    protected List<String> getMidletList(List<String> dirtyMidletList) {
        List<String> cleaned = new ArrayList<>();

        for (String dirtyMidlet : dirtyMidletList) {
            int firstComma = dirtyMidlet.indexOf(",");
            String cleanedMidlet = dirtyMidlet.substring(0, firstComma);
            cleaned.add(cleanedMidlet);
        }
        return cleaned;
    }

}
