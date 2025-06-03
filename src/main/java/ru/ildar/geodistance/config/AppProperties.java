package ru.ildar.geodistance.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Yandex yandex;
    private Dadata dadata;

    @Getter
    @Setter
    public static class Yandex {
        private String apiKey;
        private String url;
    }

    @Getter
    @Setter
    public static class Dadata {
        private String token;
        private String url;
    }
}
