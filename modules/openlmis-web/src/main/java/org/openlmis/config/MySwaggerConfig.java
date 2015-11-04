/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.config;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;

@EnableSwagger
public class MySwaggerConfig {

  private SpringSwaggerConfig springSwaggerConfig;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
    this.springSwaggerConfig = springSwaggerConfig;
    // hack - remove duplicate handlers that were injected erronously.
    // this avoids the duplicate api endpoints on the swagger ui.
    do {
      this.springSwaggerConfig.swaggerRequestMappingHandlerMappings().remove(1);
    } while (this.springSwaggerConfig.swaggerRequestMappingHandlerMappings().size() > 1);
  }

  @Bean
  public SwaggerSpringMvcPlugin customImplementation() {
    return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
            .apiInfo(apiInfo())
            .swaggerGroup("Rest API")
            .genericModelSubstitutes(ResponseEntity.class)
            .includePatterns(".*rest-api.*", ".*/api/.*");
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
            "e-LMIS REST API",
            "Please use this API to connect to the e-LMIS. This service allows you to build applications that directly interact with the e-LMIS",
            "TOS",
            "info@elmis-dev.org",
            "API License",
            "https://github.com/OpenLMIS/open-lmis/blob/master/LICENSE"
    );
  }
}
