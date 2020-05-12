import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@DisplayName("Тест формы заказа доставки карты")
public class CardDeliveryFormTest {

    LocalDate futureDate = LocalDate.now().plusDays(7);
    LocalDate pastDate = LocalDate.now().minusDays(15);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @BeforeEach
    void openURL() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Позитивный кейс, дата по умолчанию")
    void shouldSubmitWithDefaultDate() {
        SelenideElement form = $("form[class='form form_size_m form_theme_alfa-on-white']");
        form.$("[data-test-id=city] input").setValue("Уфа");
        form.$("[data-test-id=name] input").setValue("Евгений Рыбин");
        form.$("[data-test-id=phone] input").setValue("+79999999212");
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();
        $("[data-test-id='notification']").waitUntil(visible, 15000);
    }

    @Test
    @DisplayName("Позитивный кейс, ввод даты вручную")
    void shouldSubmitWithManualEnteredDate() {
        SelenideElement form = $("form[class='form form_size_m form_theme_alfa-on-white']");
        form.$("[data-test-id=city] input").setValue("Уфа");
        form.$("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        form.$("[data-test-id=date] input").sendKeys(formatter.format(futureDate));
        form.$("[data-test-id=name] input").setValue("Евгений Рыбин");
        form.$("[data-test-id=phone] input").setValue("+79999999212");
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();
        $("[data-test-id='notification']").waitUntil(visible, 15000);
    }

    @Test
    @DisplayName("Позитивный кейс, выбор города из выпадающего списка, выбор даты из календаря")
    void shouldSubmitWithComplexElements() {
        SelenideElement form = $("form[class='form form_size_m form_theme_alfa-on-white']");
        form.$("[data-test-id=city] input").setValue("Уф");
        $$(".menu-item").first().click();
        form.$(".icon-button").click();
        $$(".calendar__day").find(exactText("21")).click();
        form.$("[data-test-id=name] input").setValue("Евгений Рыбин");
        form.$("[data-test-id=phone] input").setValue("+79999999212");
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();
        $("[data-test-id='notification']").waitUntil(visible, 15000);
    }

    @Test
    @DisplayName("Негативный кейс, выбор города, в котором недоступна доставка")
    void shouldNotSubmitWithIncorrectCity() {
        SelenideElement form = $("form[class='form form_size_m form_theme_alfa-on-white']");
        form.$("[data-test-id=city] input").setValue("Малиновка");
        form.$("[data-test-id=name] input").setValue("Евгений Рыбин");
        form.$("[data-test-id=phone] input").setValue("+79999999212");
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();
        form.$("[data-test-id=city]").shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    @DisplayName("Негативный кейс, ввод даты из прошлого")
    void shouldNotSubmitWithIncorrectDate() {
        SelenideElement form = $("form[class='form form_size_m form_theme_alfa-on-white']");
        form.$("[data-test-id=city] input").setValue("Уфа");
        form.$("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        form.$("[data-test-id=date] input").sendKeys(formatter.format(pastDate));
        form.$("[data-test-id=name] input").setValue("Евгений Рыбин");
        form.$("[data-test-id=phone] input").setValue("+79999999212");
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();
        form.$("[data-test-id=date]").shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    @DisplayName("Негативный кейс, ввод имени на английском")
    void shouldSubmitWithIncorrectName() {
        SelenideElement form = $("form[class='form form_size_m form_theme_alfa-on-white']");
        form.$("[data-test-id=city] input").setValue("Уфа");
        form.$("[data-test-id=name] input").setValue("John Doe");
        form.$("[data-test-id=phone] input").setValue("+79999999212");
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();
        form.$("[data-test-id=name]").shouldHave(exactText("Фамилия и Имя Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    @DisplayName("Негативный кейс, отсутствие согласия на обработку персональных данных")
    void shouldSubmitWithoutAgreement() {
        SelenideElement form = $("form[class='form form_size_m form_theme_alfa-on-white']");
        form.$("[data-test-id=city] input").setValue("Уфа");
        form.$("[data-test-id=name] input").setValue("Евгений Рыбин");
        form.$("[data-test-id=phone] input").setValue("+79999999212");
        form.$(".button").click();
        form.$("[data-test-id=agreement]").shouldHave(cssValue("color", "rgba(255, 92, 92, 1)"));
    }
}
