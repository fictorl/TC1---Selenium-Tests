import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;


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

    public void fluentWaiterCertainComponentById(WebDriver driver, String id) {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10)) // tempo máximo de espera
                .pollingEvery(Duration.ofMillis(700)) // frequência de verificação
                .ignoring(Exception.class) // ignorar exceções durante a verificação
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        return driver.findElement(By.id(id)).isDisplayed();
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

    private void addingTelephoneNumberToPerson(String cpf, String phoneNumber) throws InterruptedException {
        WebElement editButton = findPersonEditButton(cpf);

        if (Objects.isNull(editButton)) fail("Person was not created before editing phone number");
        editButton.click();

        fluentWaiterCertainPage(driver, "Adicionar Pessoa");

        WebElement addPhoneToPersonButton = driver.findElement(By.id("formCadastroPessoa"))
                .findElements(By.tagName("button")).get(1);
        addPhoneToPersonButton.click();

        fluentWaiterCertainComponentById(driver, "formCadastrarTelefonePessoa");

        driver.findElement(By.id("formCadastrarTelefonePessoa"))
                .findElement(By.id("iTelefone")).sendKeys(phoneNumber);

        driver.findElement(By.id("formCadastrarTelefonePessoa"))
                .findElements(By.tagName("button")).get(1).click();

        driver.findElement(By.id("cadastrarPessoa")).click();

        goToMainPage();
    }

    private void goToEditPersonPage(String cpf) {
        WebElement editButton = findPersonEditButton(cpf);

        if (Objects.isNull(editButton)) fail("Person was not created before");
        editButton.click();
    }

    private void addingEmailToPerson(String cpf, String email) throws InterruptedException {

        fluentWaiterCertainPage(driver, "Adicionar Pessoa");

        WebElement addEmailToPersonButton = driver.findElement(By.id("formCadastroPessoa"))
                .findElements(By.tagName("button")).get(0);
        addEmailToPersonButton.click();

        fluentWaiterCertainComponentById(driver, "formCadastrarEmailPessoa");

        driver.findElement(By.id("formCadastrarEmailPessoa"))
                .findElement(By.id("iEmail")).sendKeys(email);

        driver.findElement(By.id("formCadastrarEmailPessoa"))
                .findElements(By.tagName("button")).get(1).click();

        driver.findElement(By.id("cadastrarPessoa")).click();

        goToMainPage();
    }


    private void deletingCertainTelephoneNumberOfThePerson(String phoneNumber) {
        List<WebElement> telephoneList = driver.findElement(By.id("formCadastroPessoa"))
                .findElement(By.id("cadastroPessoaTelefones")).findElements(By.tagName("li"));

        for (WebElement telephone : telephoneList) {
            if (telephone.getText().equals(phoneNumber)) {
                telephone.findElement(By.tagName("img")).click();
                break;
            }
        }

        driver.findElement(By.id("cadastrarPessoa")).click();
    }

    private void deletingCertainEmailOfThePerson(String email) {
        List<WebElement> telephoneList = driver.findElement(By.id("formCadastroPessoa"))
                .findElement(By.id("cadastroPessoaEmails")).findElements(By.tagName("li"));

        for (WebElement telephone : telephoneList) {
            if (telephone.getText().equals(email)) {
                telephone.findElement(By.tagName("img")).click();
                break;
            }
        }

        driver.findElement(By.id("cadastrarPessoa")).click();
    }

    public void deletePerson(WebDriver driver, String cpf, List<WebElement> tableRows) {
        fluentWaiterCertainPage(driver, "Pessoas");

        WebElement personToDeleteRow = null;

        for (WebElement row : tableRows) {
            if (row.findElement(By.id(cpf)).isDisplayed()) personToDeleteRow = row;
        }

        personToDeleteRow.findElements(By.tagName("button")).get(1).click();
    }

    public boolean checkingIfPersonWasDeleted(WebDriver driver, String cpf, List<WebElement> tableRows) {
        fluentWaiterCertainPage(driver, "Pessoas");
        return tableRows.stream().noneMatch( row -> row.findElement(By.tagName("td")).getText() == cpf);
    }

    private WebElement findPersonEditButton(String cpf) {
        WebElement tableBody = driver.findElement(By.tagName("tbody"));
        List<WebElement> tableRows = tableBody.findElements(By.tagName("tr"));
        WebElement personToEditRow = null;

        for (WebElement row : tableRows) {
            if (row.findElement(By.id(cpf)).isDisplayed()) personToEditRow = row;
        }

        if (personToEditRow == null) return null;

        WebElement editButton = personToEditRow.findElements(By.id(cpf)).get(0);
        return editButton;
    }

    private WebElement findPersonDeleteButton(String cpf) {
        WebElement tableBody = driver.findElement(By.tagName("tbody"));
        List<WebElement> tableRows = tableBody.findElements(By.tagName("tr"));
        WebElement personToDeleteRow = null;

        for (WebElement row : tableRows) {
            if (row.findElement(By.id(cpf)).isDisplayed()) personToDeleteRow = row;
        }

        if (personToDeleteRow == null) return null;

        WebElement deleteButton = personToDeleteRow.findElements(By.id(cpf)).get(1);
        return deleteButton;
    }

    private List<String> listOfRegisteredCPFs() {
        fluentWaiterCertainPage(driver, "Pessoas");
        List<WebElement> listOfPeopleTableRows = driver.findElement(By.tagName("tbody"))
                .findElements(By.tagName("tr"));

        List<String> peopleList = new ArrayList<>();

        for (WebElement row : listOfPeopleTableRows) {
            peopleList.add(row.findElement(By.tagName("td")).getText());
        }
        return peopleList;
    }

    private List<String> listOfRegisteredEmailsOfAPerson(String cpf) {
        fluentWaiterCertainPage(driver, "Pessoas");

        goToEditPersonPage(cpf);

        fluentWaiterCertainPage(driver, "Adicionar Pessoa");

        List<WebElement> ListOfTagLiForEmails = driver.findElement(By.id("formCadastroPessoa"))
                .findElement(By.id("cadastroPessoaEmails")).findElements(By.tagName("li"));

        List<String> emailList = new ArrayList<>();

        for (WebElement row : ListOfTagLiForEmails) {
            emailList.add(row.getText());
        }
        return emailList;
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789 "
                + "!@#$%^&*()-_=+[]{};:'\",.<>?/|\\";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }

        return stringBuilder.toString();
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
                String randomCEP = generateRandomString(20);

                registerPerson(driver,
                        "123.456.789-01",
                        "Marcao Pescador",
                        "Rua Eugenio Franco",
                        "283",
                        randomCEP,
                        "1970-12-06",
                        "Pedreiro");

                String successedRegistration = "Formato de CEP inválido!";
                verifyIfToastMessageIsDiferentThen(successedRegistration, driver);
            }
        }
        @Nested
        @DisplayName("Nested data de nascimento inputs")
        class testsDataInputs{
            @Test
            @DisplayName("Should not submit form if date field is empty")
            void shouldNotSubmitFormIfDateFieldIsEmpty(){
                goToRegistrationPage();

                registerPerson(driver,
                        "321.654.987-10",
                        "Carlos Caminhoneiro",
                        "Rua Eurides Faria",
                        "666",
                        "13568-826",
                        "",
                        "Dragueiro");

                WebElement CEPField = driver.findElement(By.id("iDataNasc"));
                String validationMessage = CEPField.getAttribute("validationMessage");
                assertEquals("Preencha este campo.", validationMessage, "O campo 'Data de Nascimento' deve exibir 'Preencha este campo'.");
            }
            @Test
            @DisplayName("Should not submit form if date field is in wrong format")
            void shouldNotSubmitFormIfTheDateFieldIsInWrongFormat(){
                goToRegistrationPage();
                String randomDataNasc = generateRandomString(20);

                registerPerson(driver,
                        "321.654.987-10",
                        "Carlos Caminhoneiro",
                        "Rua Eurides Faria",
                        "666",
                        "13568-826",
                        randomDataNasc,
                        "Dragueiro");

                String successedRegistration = "Formato de Data inválido!";
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

            @Test
            @DisplayName("Should not register a person with a very long name or special characters")
            void shouldNotRegisterAPersonWithAVeryLongNameOrSpecialCharacters(){
                goToRegistrationPage();
                String randomName = generateRandomString(150);

                registerPerson(driver,
                        "321.654.987-10",
                        randomName,
                        "Rua Copacabana",
                        "171",
                        "13568-826",
                        "1111-11-11",
                        "Piscineiro");

                String successedRegistration = "Formato de Nome inválido!";
                verifyIfToastMessageIsDiferentThen(successedRegistration, driver);
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

        @Nested
        @DisplayName("Deleting Tests")
        class deletingTests{

            // should not delite a person more than once
            @Test
            @DisplayName("Should not delete a person more than once")
            void shouldNotDeleteAPersonMoreThanOnce() throws InterruptedException {
                goToRegistrationPage();
                String cpf = "123.456.789-01";
                registerPerson(driver,
                        cpf,
                        "Maria Joseeeé",
                        "Rua das Flores",
                        "123",
                        "12345-678",
                        "2000-12-31",
                        "Engenheiro");
                goToMainPage();

                WebElement personDeleteButton = findPersonDeleteButton(cpf);
                if (personDeleteButton != null) personDeleteButton.click();

                WebElement refreshPersonDeleteButton = findPersonDeleteButton(cpf);

                assertTrue(refreshPersonDeleteButton == null);
            }

            @Test
            @DisplayName("Should not delete the same person´s phone number more than once")
            void shouldNotDeleteTheSamePersonSPhoneNumberMoreThanOnce() throws InterruptedException {
                goToRegistrationPage();

                String cpf = "123.456.789-01";
                registerPerson(driver,
                        cpf,
                        "Maria Joseeeé",
                        "Rua das Flores",
                        "123",
                        "12345-678",
                        "2000-12-31",
                        "Engenheiro");
                goToMainPage();

                String phoneNumber = "(19) 9999-8888";

                addingTelephoneNumberToPerson(cpf, phoneNumber);
                WebElement editButton = findPersonEditButton(cpf);
                editButton.click();

                fluentWaiterCertainPage(driver,"Adicionar Pessoa");
                List<String> firstPhonesList = driver.findElement(By.id("formCadastroPessoa"))
                        .findElements(By.tagName("ol")).get(1).findElements(By.tagName("li"))
                        .stream().map( item -> item.getText().toString()).toList();

                System.out.print(firstPhonesList);

                //deletar telefone

                deletingCertainTelephoneNumberOfThePerson(phoneNumber);

                goToMainPage();
                editButton = findPersonEditButton(cpf);
                editButton.click();

                fluentWaiterCertainPage(driver,"Adicionar Pessoa");
                List<String> secondPhonesList = driver.findElement(By.id("formCadastroPessoa"))
                        .findElements(By.tagName("ol")).get(1).findElements(By.tagName("li"))
                        .stream().map( item -> item.getText().toString()).toList();

                assertEquals(secondPhonesList.getFirst(), "Nenhum telefone cadastrado!");
            }

            @Test
            @DisplayName("Should not delete the same person´s email more than once")
            void shouldNotDeleteTheSamePersonSEmailMoreThanOnce() throws InterruptedException {
                goToRegistrationPage();

                String cpf = "123.456.789-01";
                registerPerson(driver,
                        cpf,
                        "Maria Joseeeé",
                        "Rua das Flores",
                        "123",
                        "12345-678",
                        "2000-12-31",
                        "Engenheiro");
                goToMainPage();

                String email = "maria@gmail.com";

                goToEditPersonPage(cpf);
                addingEmailToPerson(cpf, email);
                WebElement editButton = findPersonEditButton(cpf);
                editButton.click();

                fluentWaiterCertainPage(driver,"Adicionar Pessoa");

                //deletar email
                deletingCertainEmailOfThePerson(email);

                goToMainPage();
                editButton = findPersonEditButton(cpf);
                editButton.click();

                fluentWaiterCertainPage(driver,"Adicionar Pessoa");
                List<String> secondPhonesList = driver.findElement(By.id("formCadastroPessoa"))
                        .findElements(By.tagName("ol")).get(0).findElements(By.tagName("li"))
                        .stream().map( item -> item.getText().toString()).toList();

                assertEquals(secondPhonesList.getFirst(), "Nenhum email cadastrado!");
            }

        }

        @Nested
        @DisplayName("Listing Tests")
        class listingTests{

            @Nested
            @DisplayName("after creating operations")
            class afterCreationOperations {

                @Test
                @DisplayName("Should list all people just added")
                void shouldNotListAPersonJustAddedMoreOrLessThanOnce() {
                goToRegistrationPage();

                List<String> expectedCPFsList = new ArrayList<>();
                expectedCPFsList.add("123.456.789-01");
                expectedCPFsList.add("123.456.789-02");

                    for (String cpf : expectedCPFsList) {
                        registerPerson(driver,
                                cpf,
                                "Maria Joseeeé",
                                "Rua das Flores",
                                "123",
                                "12345-678",
                                "2000-12-31",
                                "Engenheiro");
                    }

                goToMainPage();

                List<String> CPFsList = listOfRegisteredCPFs();

                assertEquals(CPFsList, expectedCPFsList);
            }

                // should not list a person´s email just added more or less than once

                // should not list a person´s phone number just added more or less than once
            }

            @Nested
            @DisplayName("after deleting operations")
            class afterDeletionOperation{

                // should not list a person just deleted

                // should not list a person´s email just deleted

                // should not list a person´s phone number just deleted
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
        @Nested
        @DisplayName("EmailAndTelefoneNavigationTests")
        class EmailAndTelefoneNavigationTests{
            @Test
            @DisplayName("Should navigate to email inclusion and cancel")
            void shouldNavigateToEmailInclusionAndCancel() {
                goToRegistrationPage();
                WebElement addEmailElement = driver.findElement(By.xpath("//*[@id=\"formCadastroPessoa\"]/div[1]/button"));
                addEmailElement.click();
                WebElement emailField = driver.findElement(By.id("iEmail"));
                emailField.sendKeys("lorinho@gmail.com");
                WebElement cancelButton = driver.findElement(By.xpath("//*[@id=\"formCadastrarEmailPessoa\"]/footer/button[1]"));
                cancelButton.click();

                List<WebElement> emailList = driver.findElements(By.xpath("//*[@id='cadastroPessoaEmails']/li"));
                boolean emailFound = emailList.stream()
                        .anyMatch(email -> email.getText().equals("lorinho@gmail.com"));

                assertFalse(emailFound, "O email 'lorinho@gmail.com' foi adicionado, mas deveria ter sido cancelado.");
            }
            @Test
            @DisplayName("Should navigate to email and including it")
            void shouldNavigateToEmailAndIncludingIt() {
                goToRegistrationPage();

                WebElement addEmailElement = driver.findElement(By.xpath("//*[@id=\"formCadastroPessoa\"]/div[1]/button"));
                addEmailElement.click();
                WebElement emailField = driver.findElement(By.id("iEmail"));
                emailField.sendKeys("marujo@gmail.com");
                WebElement registrationButton = driver.findElement(By.xpath("//*[@id=\"formCadastrarEmailPessoa\"]/footer/button[2]"));
                registrationButton.click();

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='cadastroPessoaEmails']/li[contains(text(), 'marujo@gmail.com')]")));

                List<WebElement> emailList = driver.findElements(By.xpath("//*[@id='cadastroPessoaEmails']/li"));
                boolean emailFound = emailList.stream()
                        .anyMatch(email -> email.getText().equals("marujo@gmail.com"));

                assertTrue(emailFound, "O email 'marujo@gmail.com' não foi encontrado na lista.");
            }
            @Test
            @DisplayName("Should navigate to telefone inclusion and cancel")
            void shouldNavigateToTelefoneInclusionAndCancel() {
                goToRegistrationPage();
                WebElement addTelefoneElement = driver.findElement(By.xpath("//*[@id=\"formCadastroPessoa\"]/div[2]/button"));
                addTelefoneElement.click();
                WebElement telefoneField = driver.findElement(By.id("iTelefone"));
                telefoneField.sendKeys("+55 16 99133-1123");
                WebElement cancelButton = driver.findElement(By.xpath("//*[@id=\"formCadastrarTelefonePessoa\"]/footer/button[1]"));
                cancelButton.click();

                List<WebElement> telefoneList = driver.findElements(By.xpath("//*[@id='cadastroPessoaEmails']/li"));
                boolean emailFound = telefoneList.stream()
                        .anyMatch(email -> email.getText().equals("+55 16 99133-1123"));

                assertFalse(emailFound, "O telefone '+55 16 99133-1123' foi adicionado, mas deveria ter sido cancelado.");
            }
        }
    }
}

