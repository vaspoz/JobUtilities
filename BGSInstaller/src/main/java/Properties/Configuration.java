package Properties;

import org.aeonbits.owner.Config;

@Config.Sources({"file:Config.xml"})
public interface Configuration extends Config {
    @Key("properties.comport")
    String portName();

    @Key("properties.buildDirectory")
    String buildDirectory();
}
