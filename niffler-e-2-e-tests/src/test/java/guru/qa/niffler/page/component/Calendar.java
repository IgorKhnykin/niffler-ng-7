package guru.qa.niffler.page.component;

import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

public class Calendar extends BaseComponent<Calendar>{

    java.util.Calendar calendar = java.util.Calendar.getInstance();

    public Calendar() {
        super($(".MuiDateCalendar-root"));
    }

    public Calendar selectDateInCalendar(Date date) {
        calendar.setTime(date);
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        calendar.setTime(new Date());
        int todayDay = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        if  (day > todayDay) {
            throw new RuntimeException("Нельзя указать дату больше нынешней");
        } else {
            self.$x(".//button[text()='%d']".formatted(day)).click();
        }
        return this;
    }
}
