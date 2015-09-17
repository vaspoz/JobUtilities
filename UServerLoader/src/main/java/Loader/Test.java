package Loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Text;

import java.io.File;

public class Test {
    static Logger log = LoggerFactory.getLogger(Text.class);

    public static void main(String[] args) throws Exception {
        log.info("вввввввааа");
        log.debug("вввввввааа");
        log.warn("вввввввааа");
        log.trace("вввввввааа");
        log.error("вввввввааа");
        System.out.println("a" + System.getProperty("line.separator") + "b" + File.separator);
    }
}
