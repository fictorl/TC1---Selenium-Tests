import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
        assertEquals(actualMessage, expectedMessage);
    }


    public void verifyIfToastMessageIsDiferentThen(String stringNotExpected, WebDriver driver) {
        WebElement toast = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(700))
                .ignoring(Exception.class)
                .until(new Function<WebDriver, WebElement>() {
                           @Override
                           public WebElement apply(WebDriver webDriver) {
                               WebElement toastElement = driver.findElement((By.xpath("/html/body/div[@class='notificacao']/p")));
                               if (toastElement.isDisplayed()) return toastElement;
                               return null;
                           }
                       }
                );
        Assertions.assertNotEquals(stringNotExpected, toast.getText());
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
        
        @Nested
        @DisplayName("CPF input tests")
        class cpfInputTests{
            @Test
            @DisplayName("Testing when CPF input doesn´t follow the correct format")
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

            @Test
            @DisplayName("Testing when CPF follow the correct format")
            void testingWhenCpfFollowTheCorrectFormat() {
                goToRegistrationPage();
                String name = "João Silva";
                registerPerson(driver,
                        "123.456.789-10",
                        name,
                        "Rua das Flores",
                        "123",
                        "12345-678",
                        "2000-12-31",
                        "Engenheiro" );
                String expectedMessage = name + " adicionado com sucesso!";
                verifyToastMessage(driver, expectedMessage);
            }        
        }

        


        @Nested
        @DisplayName("Nested class name")
        class nestedClassName{
            @Test
            @DisplayName("Testing Profissao input when it is filed with special characters")
            void testingProfissaoInputFormat() throws InterruptedException {
                goToRegistrationPage();
                String name = "João Silva";
                registerPerson(driver,
                        "123.123.123-15",
                        name,
                        "Rua das Flores",
                        "123",
                        "12345-678",
                        "2000-12-31",
                        "#@123" );
                String successedRegistration = name + " adicionado com sucesso!";
                verifyIfToastMessageIsDiferentThen(successedRegistration, driver);
            }

            @Test
            @DisplayName("Testing Profissao input when it is empty")
            void testingProfissaoInputWhenItIsEmpty() {
                goToRegistrationPage();
                String name = "João Silva";
                registerPerson(driver,
                        "123.123.123-15",
                        name,
                        "Rua das Flores",
                        "123",
                        "12345-678",
                        "2000-12-31",
                        "" );
                WebElement nomeField = driver.findElement(By.id("iProfissao"));
                String validationMessage = nomeField.getAttribute("validationMessage");
                assertEquals("Preencha este campo.", validationMessage, "O campo 'Nome' deve exibir a mensagem 'Preencha este campo'.");
            }        
        }

        @Nested
        @DisplayName("Tests Nome input")
        class testsNomeInput{
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
                assertEquals("Preencha este campo.", validationMessage, "O campo 'Nome' deve exibir a mensagem 'Preencha este campo'.");
            }        
        }
        

        @Nested
        @DisplayName("Tests Rua input")
        class testsRuaInput{
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
                assertEquals("Preencha este campo.", validationMessage, "O campo 'Rua' deve exibir a mensagem 'Preencha este campo'.");
            }        
        }
        

        @Nested
        @DisplayName("Tests Numero input")
        class testsNumeroInput{

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
                assertEquals("Preencha este campo.", validationMessage, "O campo 'Número' deve exibir a mensagem 'Preencha este campo'.");
            }
        }

        @Nested
        @DisplayName("Nested CEP inputs")
        class testsCEPInputs{
            @Test
            @DisplayName("Should not submit form if the CEP field is empty")
            void shouldNotSubmitFormIfTheCEPFieldIsEmpty(){
                    goToRegistrationPage();

                    registerPerson(driver,
                            "123.456.789-01",
                            "Marcao Pescador",
                            "Rua Eugenio Franco",
                            "283",
                            "",
                            "1970-12-06",
                            "Pedreiro");

                    WebElement CEPField = driver.findElement(By.id("iCEP"));
                    String validationMessage = CEPField.getAttribute("validationMessage");
                    assertEquals("Preencha este campo.", validationMessage, "O campo 'CEP' deve exibir 'Preencha este campo'.");
                }

            @Test
            @DisplayName("Should not submit form if the CEP field is in wrong format")
            void shouldNotSubmitFormIfTheCEPFieldIsInWrongFormat(){
                goToRegistrationPage();

                registerPerson(driver,
                        "123.456.789-01",
                        "Marcao Pescador",
                        "Rua Eugenio Franco",
                        "283",
                        "?0%0000..00#0000000@00!!0",
                        "1970-12-06",
                        "Pedreiro");

                String successedRegistration = "Formato de CEP inválido!";
                verifyIfToastMessageIsDiferentThen(successedRegistration, driver);
            }
        }
    }

    @Nested
    @DisplayName("CRUD Tests")
    class CrudTest {

        @Nested
        @DisplayName("Creation Tests")
        class creationTests {

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
                        "Engenheiro");

                goToMainPage();
                goToRegistrationPage();

                registerPerson(driver,
                        cpf,
                        "João Silva",
                        "Rua das Palmeiras",
                        "456",
                        "98765-432",
                        "1995-05-15",
                        "Médico");

                String expectedMessageTwo = "O CPF 123.456.789-01 ja foi cadastrado!";
                verifyToastMessage(driver, expectedMessageTwo);
            }
        }

        @Nested
        @DisplayName("Editing  Tests")
        class editingTests {
            @Test
            @DisplayName("Should not allow editing the CPF field")
            void shouldNotAllowEditingCpfField() throws InterruptedException {
                goToRegistrationPage();

                String cpf = "123.456.789-02";
                registerPerson(driver,
                        cpf,
                        "Antonia Francisca",
                        "Rua das Flores",
                        "123",
                        "12345-678",
                        "2000-12-31",
                        "Desenvolvedora de software");

                goToMainPage();
                WebElement editButton = driver.findElement(By.id(cpf));
                editButton.click();
                fluentWaiterCertainPage(driver, "Adicionar Pessoa");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement cpfField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("iCpf")));
                Assertions.assertFalse(cpfField.isEnabled());
            }

            @Test
            @DisplayName("It should show validation messages and not submit the form on the edit page if any field is empty")
            void itShouldShowValidationMessagesAndNotSubmitTheFormOnTheEditPageIfAnyFieldIsEmpty() throws InterruptedException {
                goToRegistrationPage();

                String cpf = "123.456.789-02";
                registerPerson(driver,
                        cpf,
                        "Antonia Francisca",
                        "Rua das Flores",
                        "123",
                        "12345-678",
                        "2000-12-31",
                        "Desenvolvedora de software");

                goToMainPage();
                WebElement editButton = driver.findElement(By.id(cpf));
                editButton.click();

                fluentWaiterCertainPage(driver, "Adicionar Pessoa");

                WebElement nomeField = driver.findElement(By.id("iNome"));
                nomeField.clear();
                driver.findElement(By.id("cadastrarPessoa")).click();
                String nomeValidationMessage = nomeField.getAttribute("validationMessage");
                assertEquals("Preencha este campo.", nomeValidationMessage, "O campo 'Nome' deve exibir a mensagem 'Preencha este campo'.");
                nomeField.sendKeys("Antonia Francisca");

                WebElement ruaField = driver.findElement(By.id("iRua"));
                ruaField.clear();
                driver.findElement(By.id("cadastrarPessoa")).click();
                String ruaValidationMessage = ruaField.getAttribute("validationMessage");
                assertEquals("Preencha este campo.", ruaValidationMessage, "O campo 'Rua' deve exibir a mensagem 'Preencha este campo'.");
                ruaField.sendKeys("Rua das Flores");

                WebElement numeroField = driver.findElement(By.id("iNumero"));
                numeroField.clear();
                driver.findElement(By.id("cadastrarPessoa")).click();
                String numeroValidationMessage = numeroField.getAttribute("validationMessage");
                assertEquals("Preencha este campo.", numeroValidationMessage, "O campo 'Número' deve exibir a mensagem 'Preencha este campo'.");
                numeroField.sendKeys("123");

                WebElement cepField = driver.findElement(By.id("iCep"));
                cepField.clear();
                driver.findElement(By.id("cadastrarPessoa")).click();
                String cepValidationMessage = cepField.getAttribute("validationMessage");
                assertEquals("Preencha este campo.", cepValidationMessage, "O campo 'CEP' deve exibir a mensagem 'Preencha este campo'.");
                cepField.sendKeys("12345-678");

                WebElement dataNascField = driver.findElement(By.id("iDataNasc"));
                dataNascField.clear();
                driver.findElement(By.id("cadastrarPessoa")).click();
                String dataNascValidationMessage = dataNascField.getAttribute("validationMessage");
                assertEquals("Preencha este campo.", dataNascValidationMessage, "O campo 'Data de Nascimento' deve exibir a mensagem 'Preencha este campo'.");
                dataNascField.sendKeys("2000-12-31");

                WebElement profissaoField = driver.findElement(By.id("iProfissao"));
                profissaoField.clear();
                driver.findElement(By.id("cadastrarPessoa")).click();
                String profissaoValidationMessage = profissaoField.getAttribute("validationMessage");
                assertEquals("Preencha este campo.", profissaoValidationMessage, "O campo 'Profissão' deve exibir a mensagem 'Preencha este campo'.");
            }

            @Test
            @DisplayName("Should not allow editing a person with a future birth date")
            void shouldNotAllowEditingWithFutureBirthDate() throws InterruptedException {

                goToRegistrationPage();

                String cpf = "123.456.789-02";
                String nome = "Antonia Francisca";
                String initialBirthDate = "2000-12-31";
                registerPerson(driver,
                        cpf,
                        nome,
                        "Rua das Flores",
                        "123",
                        "12345-678",
                        initialBirthDate,
                        "Desenvolvedora de software");

                goToMainPage();
                WebElement editButton = driver.findElement(By.id(cpf));
                editButton.click();
                fluentWaiterCertainPage(driver, "Adicionar Pessoa");

                String futureDate = "2100-01-01";
                WebElement birthDateField = driver.findElement(By.id("iDataNasc"));
                birthDateField.clear();
                birthDateField.sendKeys(futureDate);
                driver.findElement(By.id("cadastrarPessoa")).click();

                goToMainPage();
                WebElement birthDateFieldOnMainPage = driver.findElement(By.id("iDataNasc"));
                String displayedBirthDate = birthDateFieldOnMainPage.getAttribute("value");

                Assertions.assertNotEquals(futureDate, displayedBirthDate, "A data de nascimento não deve ser editada para uma data futura!");
            }

        }
    }

    @Nested
    @DisplayName("Navigation Tests")
    class NavigationTest {

        @Nested
        @DisplayName("MainPageNavigationTests")
        class MainPageNavigationTests{

            @Test
            @DisplayName("Should search person by CPF")
            void shouldSearchPersonByCpf() {

                goToRegistrationPage();

                String cpf = "123.456.789-60";
                String nome = "Analu";

                registerPerson(driver,
                        cpf,
                        nome,
                        "Rua das Flores",
                        "123",
                        "12345-678",
                        "2000-12-31",
                        "Desenvolvedora de software");

                goToMainPage();
                WebElement cpfField = driver.findElement(By.id("iPesquisa"));
                cpfField.sendKeys("123.456.789-60");

                WebElement searchButton = driver.findElement(By.xpath("//img[@alt='Imagem de pesquisa']"));
                searchButton.click();

                String expectedMessageTwo = "1 pessoas encontradas!";
                verifyToastMessage(driver, expectedMessageTwo);
            }

            @Test
            @DisplayName("Should show alert when CPF is not registered")
            void shouldShowAlertWhenCpfIsNotRegistered() {

                goToRegistrationPage();

                String cpf = "123.456.789-60";
                String nome = "Analu";

                registerPerson(driver,
                        cpf,
                        nome,
                        "Rua das Flores",
                        "123",
                        "12345-678",
                        "2000-12-31",
                        "Desenvolvedora de software");

                goToMainPage();
                WebElement cpfField = driver.findElement(By.id("iPesquisa"));
                cpfField.sendKeys("123.456.789-61");

                WebElement searchButton = driver.findElement(By.xpath("//img[@alt='Imagem de pesquisa']"));
                searchButton.click();

                String expectedMessageTwo = "Nenhuma pessoa encontrada!";
                verifyToastMessage(driver, expectedMessageTwo);
            }

            @Test
            @DisplayName("Should navigate to add person page when add button is clicked")
            void shouldNavigateToAddPersonPage() {
                WebElement addButton = driver.findElement(By.xpath("//a[@href='./addPessoa.html']"));
                addButton.click();

                WebElement registrationPageElement = driver.findElement(By.xpath("//h1[text()='Cadastrar Pessoa']"));

                assertTrue(registrationPageElement.isDisplayed(), "A navegação para a página de cadastro falhou.");
            }


        }
    }
}

