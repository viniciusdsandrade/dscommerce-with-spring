package com.resftul.dscommerce.doc;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("OpenApiConfig")
@Schema(description = "Configuração global do OpenAPI para a aplicação.")
public class OpenApiConfig {

    @Value("${api.version}")
    private String apiVersion;

    @Bean("customOpenAPI")
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API para Gerenciamento de Comércio Eletrônico")
                        .version(apiVersion)
                        .license(new License().url("https://github.com/viniciusdsandrade/dscommerce-with-spring"))
                        .contact(new Contact()
                                .name("Linkedin")
                                .url("https://www.linkedin.com/in/viniciusdsandrade/"))
                        .contact(new Contact()
                                .name("GitHub")
                                .url("https://github.com/viniciusdsandrade"))
                        .contact(new Contact()
                                .name("E-mail")
                                .url("mailto:vinicius_andrade2010@hotmail.com"))
                );
    }

    @Bean("publicApi")
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }
}