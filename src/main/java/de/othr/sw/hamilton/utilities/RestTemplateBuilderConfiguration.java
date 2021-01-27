package de.othr.sw.hamilton.utilities;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateBuilderConfiguration {
    @Value("${appconfig.stonks.url}")
    private String stonksUrl;

    @Value("${appconfig.voci.url}")
    private String vociUrl;

    @Bean
    @Qualifier("stonks")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public RestTemplate createStonksRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(stonksUrl)
                .build();
    }

    @Bean
    @Qualifier("voci")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public RestTemplate createVociRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(vociUrl)
                .build();
    }
}
