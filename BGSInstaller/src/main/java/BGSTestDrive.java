public class BGSTestDrive {
    public static void main(String[] args) throws Exception{
        BGSLoader loader = new BGSLoader();

        try {
            loader.openConnection();
            loader.setPreConfiguration();
            loader.startJDCandAutostartOFF();
            loader.deleteInstalledFirmwares();
            loader.deleteExistingFiles();
            loader.sendFiles();
            loader.installMidlet();
            loader.showIMEI();
            loader.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.in.read();
    }
}
