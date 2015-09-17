import java.io.IOException;

public class Runner {
    public static void main(String[] args) throws IOException{
        try {
            BGSLogLoader loader = new BGSLogLoader();
            loader.loadLogFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Press any key...");
        System.in.read();
    }
}
