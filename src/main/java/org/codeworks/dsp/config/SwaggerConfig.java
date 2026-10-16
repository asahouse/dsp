package org.codeworks.dsp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger 2 API 接口文档配置类
 * 提供在线交互式接口调试界面 (/swagger-ui.html)
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.codeworks.dsp"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("DSP 程序化广告交易平台 API 文档 (OpenRTB 2.5 / Prebid Server)")
                .description("包含 OpenRTB 2.5 实时竞价出价引擎、第二价格胜出计费通知、广告主、投放活动、创意物料与数据报表全功能 RESTful 接口。")
                .version("2.5.0")
                .contact(new Contact("DSP 架构研发组", "https://dsp.local", "architecture@dsp.local"))
                .build();
    }
}
