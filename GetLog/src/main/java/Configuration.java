import org.aeonbits.owner.Config;

@Config.Sources({"file:Config.ini"})
public interface Configuration extends Config{

    String comport();

    @Config.Key("debug")
    Boolean isDebug();
    @Config.Key("debug0")
    Boolean isDebug0();
    @Config.Key("debug1")
    Boolean isDebug1();
    @Config.Key("debug2")
    Boolean isDebug2();
    @Config.Key("debug3")
    Boolean isDebug3();

    @Config.Key("info")
    Boolean isInfo();
    @Config.Key("info0")
    Boolean isInfo0();
    @Config.Key("info1")
    Boolean isInfo1();
    @Config.Key("info2")
    Boolean isInfo2();
    @Config.Key("info3")
    Boolean isInfo3();

    @Config.Key("error")
    Boolean isError();
    @Config.Key("error0")
    Boolean isError0();
    @Config.Key("error1")
    Boolean isError1();
    @Config.Key("error2")
    Boolean isError2();
    @Config.Key("error3")
    Boolean isError3();
}

