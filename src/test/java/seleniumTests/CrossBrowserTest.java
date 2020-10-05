package seleniumTests;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CrossBrowserTest {
    public WebDriver driver;
    String URL = "https://www.amazon.com/";
    String node = "http://localhost:4444/";
    boolean status = false;

    @Parameters({"browser"})
    @BeforeClass
    public void invokeBrowser(String browser) throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setPlatform(Platform.ANY);
        switch (browser){
            case "chrome":
                caps.setBrowserName(BrowserType.CHROME);
                break;
            case "edge":
                caps.setBrowserName(BrowserType.EDGE);
                    break;
            case "firefox":
                caps.setBrowserName(BrowserType.FIREFOX);
                break;
            case "opera":
                caps.setBrowserName(BrowserType.OPERA);
                break;
            case "safari":
                caps.setBrowserName(BrowserType.SAFARI);
                break;
            default:
                throw new RuntimeException("No esta soportado el browser");
        }

        /* The execution happens on the Selenium Grid with the address mentioned earlier */
        try {
            driver = new RemoteWebDriver(URI.create(node).toURL(), caps);
            //driver.manage().window().maximize();
            driver.manage().deleteAllCookies();
            driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            driver.navigate().to(URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    @Test(priority = 100)
    public void verifyTitleOfThePage() {
        String expectedTitle="Amazon.com: Online Shopping for Electronics, Apparel, Computers, Books, DVDs & more";
        String actualTitle;

        actualTitle = driver.getTitle();
        Assert.assertEquals(actualTitle, expectedTitle);
    }

    @Test(priority = 200)
    public void searchProduct() {
        String productItem="Apple Watch";
        String category = "Electronics";

        WebElement selDropdown = driver.findElement(By.id("searchDropdownBox"));
        Select selCategory = new Select(selDropdown);
        selCategory.selectByVisibleText(category);
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys(productItem);
        driver.findElement(By.xpath("//input[@value='Go']")).click();
    }
    //

    @Test(priority = 300)
    public void getAllProducts() {
        List<WebElement> allProducts = driver.findElements(By.xpath("//div[@data-component-type='s-search-result']"));

        for(WebElement product:allProducts) {
            System.out.println(product.getText());
            System.out.println("---------------------");
        }
    }

    @Test (priority = 400)
    public void searchAllProductsViaScrollDown() {
        List<WebElement> allProducts = driver.findElements(By.xpath("//div[@data-component-type='s-search-result']"));

        Actions action = new Actions(driver);

        for(WebElement product:allProducts) {
            //pone el foco en el producto
            action.moveToElement(product).build().perform();
            System.out.println(product.getText());
            System.out.println("---------------------");
        }
    }

    @Test(enabled = false)
    public void searchAllProductsViaScrollDownJS() {
        List<WebElement> allProducts = driver.findElements(By.xpath("//div[@data-component-type='s-search-result']"));

        for(WebElement product:allProducts) {
            scrollDown(product.getLocation().x, product.getLocation().y);

            System.out.println(product.getText());
            System.out.println("---------------------");
        }
    }

    private void scrollDown(int x,int y) {
        JavascriptExecutor jsEngine = (JavascriptExecutor)driver;
        String jscmd = String.format("window.scrollBy(%d,%d)", x,y);

        jsEngine.executeScript(jscmd);
    }


    @AfterClass //enabled=false para ver detalles de la sesion el localost:4444/status
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("Terminaron los tests con Selenium Grid 4 Standalone");
    }
}