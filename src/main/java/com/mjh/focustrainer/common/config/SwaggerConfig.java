package com.mjh.focustrainer.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI focusTrainerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FocusTrainer API 문서")
                        .description("집중력 훈련 서비스의 인증 및 훈련 관련 API 명세서")
                        .version("v1.0.0")
                        .license(new License().name("MIT License")));
    }

}
