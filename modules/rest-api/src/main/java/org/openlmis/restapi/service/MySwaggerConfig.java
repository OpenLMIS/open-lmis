///*
//* This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
//*
//* This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
//*
//* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
//*
//* You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
//*/
//
//package org.openlmis.restapi.service;
//
//import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
//import com.mangofactory.swagger.models.dto.ApiInfo;
//import com.mangofactory.swagger.plugin.EnableSwagger;
//import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//
//@NoArgsConstructor
//@AllArgsConstructor
//@Configuration
//@EnableSwagger
//@ComponentScan("org.openlmis.restapi") //Loads the spring beans required by the framework
//public class MySwaggerConfig {
//
//
//  @Autowired
//  private SpringSwaggerConfig springSwaggerConfig;
//
//  /**
//   * Required to autowire SpringSwaggerConfig
//   */
//  @Autowired
//  public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
//    this.springSwaggerConfig = springSwaggerConfig;
//  }
//
//  /**
//   * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc framework - allowing for multiple
//   * swagger groups i.e. same code base multiple swagger resource listings.
//   */
//  //@Bean
//  public SwaggerSpringMvcPlugin customImplementation(){
//    return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
//      .apiInfo(apiInfo())
//      .includePatterns(".*rest-api.*");
//  }
//
//  private ApiInfo apiInfo() {
//    ApiInfo apiInfo = new ApiInfo(
//      "e-LMIS API",
//      "This API allows you to access e-LMIS data from within your own application",
//      "e-LMIS API terms of service",
//      "e-LMIS API Contact Email",
//      "e-LMIS API Licence Type",
//      "e-LMIS API License URL"
//    );
//    return apiInfo;
//  }
//
//}