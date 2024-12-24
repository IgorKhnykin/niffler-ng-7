package guru.qa.niffler.model;

public record ErrorJson(String type,
                        String title,
                        int status,
                        String detail,
                        String instance) {
}
