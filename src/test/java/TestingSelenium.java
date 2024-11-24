import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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
        fluentWaiterCertainPage(driver, "Pessoas");
    }
    @AfterEach
    void tearDown(){
        driver.quit();
    }

    void goToRegistrationPage(){
        final WebElement searchButton = driver.findElement(By.xpath("/html/body/main/article/div/div[1]/form/a"));
        searchButton.click();
    }

    void goToMainPage() {
        final WebElement searchButton = driver.findElement(By.xpath("/html/body/main/a/img"));
        searchButton.click();
        fluentWaiterCertainPage(driver, "Pessoas");
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

    public void verifyToastMessage(WebDriver driver, String expectedMessage) {
        // Aguarda até que o toast apareça
        WebElement toast = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10)) // tempo máximo de espera
                .pollingEvery(Duration.ofMillis(700)) // frequência de verificação
                .ignoring(Exception.class) // ignora exceções durante a verificação
                .until(new Function<WebDriver, WebElement>() {
                    @Override
                    public WebElement apply(WebDriver driver) {
                        // Localiza o elemento do toast
                        WebElement toastElement = driver.findElement(By.xpath("/html/body/div[@class='notificacao']/p"));
                        if (toastElement.isDisplayed()) {
                            return toastElement;
                        }
                        return null;
                    }
                });

        // Verifica o texto do toast
        String actualMessage = toast.getText();
        Assertions.assertEquals(actualMessage, expectedMessage);
    }

    public void registerPerson(WebDriver driver, String cpf, String nome, String rua, String numero, String cep, String dataNasc, String profissao) {
        fluentWaiterCertainPage(driver, "Adicionar Pessoa");
        driver.findElement(By.id("iCpf")).sendKeys(cpf);
        driver.findElement(By.id("iNome")).sendKeys(nome);
        driver.findElement(By.id("iRua")).sendKeys(rua);
        driver.findElement(By.id("iNumero")).sendKeys(numero);
        driver.findElement(By.id("iCep")).sendKeys(cep);
        driver.findElement(By.id("iDataNasc")).sendKeys(dataNasc);
        driver.findElement(By.id("iProfissao")).sendKeys(profissao);
        driver.findElement(By.id("cadastrarPessoa")).click();
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


    @Nested
    @DisplayName("Inputs tests")
    class InputsTests {

        @Test
        @DisplayName("Testing CPF input format")
        void testingCpfInputFormat() throws InterruptedException {
            goToRegistrationPage();
            String name = "João Silva";
            registerPerson(driver,
                    "123",
                    name,
                    "Rua das Flores",
                    "123",
                    "12345-678",
                    "2000-12-31",
                    "Engenheiro" );
            String expectedMessage = "Formato correto: 123.456.789-10";
            verifyToastMessage(driver, expectedMessage);
        }

        /*
        @Test
        @DisplayName("Testing CPF input empty string")
        void testingCpfInputNull() throws InterruptedException {
            goToRegistrationPage();
            String name = "João Silva";
            registerPerson(driver,
                    "123",
                    name,
                    "Rua das Flores",
                    "123",
                    "12345-678",
                    "2000-12-31",
                    "Engenheiro" );
            // pensar como fazer verificão
        }
        */

        @Test
        @DisplayName("Should not submit the form if the 'Nome' field is empty")
        void shouldNotSubmitIfNomeIsEmpty() throws InterruptedException {
            goToRegistrationPage();

            registerPerson(driver,
                    "12345678901",
                    "",
                    "Rua das Flores",
                    "123",
                    "12345-678",
                    "2000-12-31",
                    "Engenheiro" );

            WebElement nomeField = driver.findElement(By.id("iNome"));
            String validationMessage = nomeField.getAttribute("validationMessage");
            Assertions.assertEquals("Preencha este campo.", validationMessage, "O campo 'Nome' deve exibir a mensagem 'Preencha este campo'.");
        }

        @Test
        @DisplayName("Should not submit the form if the 'Rua' field is empty")
        void shouldNotSubmitIfRuaIsEmpty() throws InterruptedException {
            goToRegistrationPage();

            registerPerson(driver,
                    "12345678901",
                    "João Silva",
                    "",
                    "123",
                    "12345-678",
                    "2000-12-31",
                    "Engenheiro" );

            WebElement ruaField = driver.findElement(By.id("iRua"));
            String validationMessage = ruaField.getAttribute("validationMessage");
            Assertions.assertEquals("Preencha este campo.", validationMessage, "O campo 'Rua' deve exibir a mensagem 'Preencha este campo'.");
        }

        @Test
        @DisplayName("Should not submit the form if the 'Número' field is empty")
        void shouldNotSubmitIfNumeroIsEmpty() throws InterruptedException {
            goToRegistrationPage();

            registerPerson(driver,
                    "12345678901",
                    "João Silva",
                    "Rua das Flores",
                    "",
                    "12345-678",
                    "2000-12-31",
                    "Engenheiro" );

            WebElement numeroField = driver.findElement(By.id("iNumero"));
            String validationMessage = numeroField.getAttribute("validationMessage");
            Assertions.assertEquals("Preencha este campo.", validationMessage, "O campo 'Número' deve exibir a mensagem 'Preencha este campo'.");
        }

        @Test
        @DisplayName("Should not allow registering two people with the same CPF")
        void shouldNotAllowDuplicateCpfRegistration() throws InterruptedException {
            goToRegistrationPage();

            String cpf = "123.456.789-01";
            registerPerson(driver,
                    cpf,
                    "Maria José",
                    "Rua das Flores",
                    "123",
                    "12345-678",
                    "2000-12-31",
                    "Engenheiro" );

            goToMainPage();
            goToRegistrationPage();

            registerPerson(driver,
                    cpf,
                    "João Silva",
                    "Rua das Palmeiras",
                    "456",
                    "98765-432",
                    "1995-05-15",
                    "Médico" );

            String expectedMessageTwo = "O CPF 123.456.789-01 ja foi cadastrado!";
            verifyToastMessage(driver, expectedMessageTwo);
        }

    }
}

