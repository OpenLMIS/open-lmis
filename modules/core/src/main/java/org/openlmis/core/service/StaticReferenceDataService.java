package org.openlmis.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@PropertySource({"classpath:/default.properties", "classpath:${environmentName}/app.properties"})
public class StaticReferenceDataService {

  @Autowired
  Environment environment;

  public String getPropertyValue(String propertyName) {
    return environment.getProperty(propertyName);
  }

}
