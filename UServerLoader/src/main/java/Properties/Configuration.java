package Properties;

import org.aeonbits.owner.Config;

@Config.Sources({"file:Configuration.xml"})
public interface Configuration extends Config {

    //=============== Connection configuration ===============
    @Key("properties.connection.user")
    String user();

    @Key("properties.connection.password")
    String password();

    @Key("properties.connection.server")
    @DefaultValue("test")
    String server();

    //=============== Load configuration ===============
    @Key("properties.load.branch")
    String branch();

    @Key("properties.load.buildName")
    String buildName();

    @Key("properties.load.description")
    String description();

    //=============== Bind configuration ===============
    @Key("properties.bind.linkName")
    String linkName();
}
