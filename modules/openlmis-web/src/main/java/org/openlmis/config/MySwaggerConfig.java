package org.openlmis.config;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableSwagger
public class MySwaggerConfig {

  private SpringSwaggerConfig springSwaggerConfig;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
    this.springSwaggerConfig = springSwaggerConfig;
  }

  @Bean
  public SwaggerSpringMvcPlugin customImplementation(){

    return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
      .apiInfo(apiInfo())
      .swaggerGroup("Rest API")
      .genericModelSubstitutes(ResponseEntity.class)
      //.requestMappingPatternMatcher()
      .includePatterns(".*rest-api.*"); // assuming the API lives at something like http://myapp/api
  }

  private ApiInfo apiInfo() {
    ApiInfo apiInfo = new ApiInfo(
      "e-LMIS REST API",
      "Please use this API to connect to the e-LMIS. This service allows you to build applications that directly interact with the e-LMIS",
      "TOS",
      "info@elmis-dev.org",
      "API License",
      "API License URL"
    );
    return apiInfo;
  }
}