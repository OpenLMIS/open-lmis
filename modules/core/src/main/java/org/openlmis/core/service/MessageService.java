/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.message.ExposedMessageSource;
import org.openlmis.core.message.ExposedMessageSourceImpl;
import org.openlmis.core.message.OpenLmisMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;
import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

/**
 * Exposes the services for translating keys to corresponding message according to the selected locale.
 */

@Service
@NoArgsConstructor
@Scope(value = SCOPE_SESSION, proxyMode = TARGET_CLASS)
public class MessageService {

  private ExposedMessageSource messageSource;

  @Setter
  @Getter
  private Locale currentLocale;

  private String locales;

  @Scope(SCOPE_REQUEST)
  public static MessageService getRequestInstance() {
    ExposedMessageSourceImpl exposedMessageSource = new ExposedMessageSourceImpl();
    exposedMessageSource.setBasename("messages");
    return new MessageService(exposedMessageSource, "en");
  }

  @Autowired
  public MessageService(ExposedMessageSource messageSource, @Value("${locales.supported}") String locales) {
    this.messageSource = messageSource;
    this.locales = locales;
    this.currentLocale = new Locale("en");
  }

  public String message(String key) {
    return message(key, currentLocale, (Object) null);
  }

  public String message(OpenLmisMessage openLmisMessage) {
    return message(openLmisMessage.getCode(), (Object[]) openLmisMessage.getParams());
  }

  @SuppressWarnings("non-varargs")
  public String message(String key, Object... args) {
    return message(key, currentLocale, args);
  }

  private String message(String key, Locale locale, Object... args) {
    return messageSource.getMessage(key, args, key, locale);
  }

  public Set<String> getLocales() {
    Set<String> localeSet = new HashSet<>();

    String[] localeCodes = locales.split(",");

    for (String locale : localeCodes) {
      localeSet.add(locale.trim());
    }

    return localeSet;
  }

  /**
   * Return all messages using the current locale.
   *
   * @return a map of all messages.
   */
  public Map<String, String> allMessages() {
    return allMessages(currentLocale);
  }


  /**
   * Return all messages of the given locale.
   *
   * @param locale the locale of the messages to return.
   * @return a map of all messages for the given locale.
   */
  public Map<String, String> allMessages(Locale locale) {
    return messageSource.getAll(currentLocale);
  }
}
