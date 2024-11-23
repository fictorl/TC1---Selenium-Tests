import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.function.Function;

public class TestingSelenium{
    private WebDriver driver;
    @BeforeEach
    void setUp(){
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.get("http://devhub.dev.br/");
        //criar fluentWaiterCertainPage(driver, pagina);
    }
    @AfterEach
    void tearDown(){
        driver.quit();
    }

    void goToRegistrationPage(){
        final WebElement searchButton = driver.findElement(By.xpath("/html/body/main/article/div/div[1]/form/a"));
        searchButton.click();
    }



    public void fluentWaiterCertainPage(WebDriver driver, String title) {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10)) // tempo máximo de espera
                .pollingEvery(Duration.ofMillis(700)) // frequência de verificação
                .ignoring(Exception.class) // ignorar exceções durante a verificação
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        return title.equals(driver.getTitle());
                    }
                });
    }


    @Test
    @DisplayName("Should open and close chrome browser using Manager")
    void shouldOpenAndCloseChromeBrowserUsingManager() throws InterruptedException {
        driver.get("http://devhub.dev.br/");
        Thread.sleep(1000);
        driver.quit();
    }

    @Test
    @DisplayName("Should open and close chrome browser using Manager")
    void basicTest() throws InterruptedException {
        driver.get("http://devhub.dev.br/");
        fluentWaiterCertainPage(driver, "Pessoas");
        goToRegistrationPage();
        fluentWaiterCertainPage(driver, "Adicionar Pessoa");
        System.out.println(driver.findElement(By.xpath("/html/body/main/header/h1")));
        System.out.println("Deu certo");
        driver.quit();
    }
}

