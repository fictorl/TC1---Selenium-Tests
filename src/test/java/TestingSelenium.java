import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestingSelenium{
    private WebDriver driver;
    @BeforeEach
    void setUp(){
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
    }
    @AfterEach
    void tearDown(){
        driver.quit();
    }

    void goToRegistrationPage(){
        final WebElement searchButton = driver.findElement(By.xpath("/html/body/main/article/div/div[1]/form/a"));
        searchButton.click();
    }

    @Test
    @DisplayName("Should open and close chrome browser using Manager")
    void shouldOpenAndCloseChromeBrowserUsingManager() throws InterruptedException {
        driver.get("http://devhub.dev.br/");
        Thread.sleep(1000);
        driver.quit();
    }
}
