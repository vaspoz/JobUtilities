package Loader;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.*;

import java.net.URL;
import java.util.List;

public class DriverAdapter extends RemoteWebDriver {
    private int millis = 30;

    protected DriverAdapter() {
        super();
    }

    public DriverAdapter(CommandExecutor executor, Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
        super(executor, desiredCapabilities, requiredCapabilities);
    }

    public DriverAdapter(CommandExecutor executor, Capabilities desiredCapabilities) {
        super(executor, desiredCapabilities);
    }

    public DriverAdapter(Capabilities desiredCapabilities) {
        super(desiredCapabilities);
    }

    public DriverAdapter(URL remoteAddress, Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
        super(remoteAddress, desiredCapabilities, requiredCapabilities);
    }

    public DriverAdapter(URL remoteAddress, Capabilities desiredCapabilities) {
        super(remoteAddress, desiredCapabilities);
    }

    @Override
    public List<WebElement> findElements(By by) {
        waitForMilliseconds(millis);
        return super.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        waitForMilliseconds(millis);
        return super.findElement(by);
    }

    @Override
    protected WebElement findElement(String by, String using) {
        waitForMilliseconds(millis);
        return super.findElement(by, using);
    }

    @Override
    protected List<WebElement> findElements(String by, String using) {
        waitForMilliseconds(millis);
        return super.findElements(by, using);
    }

    @Override
    public WebElement findElementById(String using) {
        waitForMilliseconds(millis);
        return super.findElementById(using);
    }

    @Override
    public List<WebElement> findElementsById(String using) {
        waitForMilliseconds(millis);
        return super.findElementsById(using);
    }

    @Override
    public WebElement findElementByLinkText(String using) {
        waitForMilliseconds(millis);
        return super.findElementByLinkText(using);
    }

    @Override
    public List<WebElement> findElementsByLinkText(String using) {
        waitForMilliseconds(millis);
        return super.findElementsByLinkText(using);
    }

    @Override
    public WebElement findElementByPartialLinkText(String using) {
        waitForMilliseconds(millis);
        return super.findElementByPartialLinkText(using);
    }

    @Override
    public List<WebElement> findElementsByPartialLinkText(String using) {
        waitForMilliseconds(millis);
        return super.findElementsByPartialLinkText(using);
    }

    @Override
    public WebElement findElementByTagName(String using) {
        waitForMilliseconds(millis);
        return super.findElementByTagName(using);
    }

    @Override
    public List<WebElement> findElementsByTagName(String using) {
        waitForMilliseconds(millis);
        return super.findElementsByTagName(using);
    }

    @Override
    public WebElement findElementByName(String using) {
        waitForMilliseconds(millis);
        return super.findElementByName(using);
    }

    @Override
    public List<WebElement> findElementsByName(String using) {
        waitForMilliseconds(millis);
        return super.findElementsByName(using);
    }

    @Override
    public WebElement findElementByClassName(String using) {
        waitForMilliseconds(millis);
        return super.findElementByClassName(using);
    }

    @Override
    public List<WebElement> findElementsByClassName(String using) {
        waitForMilliseconds(millis);
        return super.findElementsByClassName(using);
    }

    @Override
    public WebElement findElementByCssSelector(String using) {
        waitForMilliseconds(millis);
        return super.findElementByCssSelector(using);
    }

    @Override
    public List<WebElement> findElementsByCssSelector(String using) {
        waitForMilliseconds(millis);
        return super.findElementsByCssSelector(using);
    }

    @Override
    public WebElement findElementByXPath(String using) {
        waitForMilliseconds(millis);
        return super.findElementByXPath(using);
    }

    @Override
    public List<WebElement> findElementsByXPath(String using) {
        waitForMilliseconds(millis);
        return super.findElementsByXPath(using);
    }

    private void waitForMilliseconds(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

}
