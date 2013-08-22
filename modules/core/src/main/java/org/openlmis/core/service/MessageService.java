/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.message.OpenLmisMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;
import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

@Service
@NoArgsConstructor
@Scope(value = SCOPE_SESSION, proxyMode = TARGET_CLASS)
public class MessageService {

  private MessageSource messageSource;

  @Setter
  @Getter
  private Locale currentLocale;


  private String locales;

  @Autowired
  public MessageService(MessageSource messageSource, @Value("${locales.supported}") String locales) {
    this.messageSource = messageSource;
    this.locales = locales;
    this.currentLocale = Locale.getDefault();
  }

  @Scope(SCOPE_REQUEST)
  public static MessageService getRequestInstance() {
    ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
    resourceBundleMessageSource.setBasename("messages");
    resourceBundleMessageSource.setDefaultEncoding("UTF-8");
    return new MessageService(resourceBundleMessageSource, "en");
  }

  public String message(String key) {
    return message(key, currentLocale, (Object) null);
  }

  public String message(OpenLmisMessage openLmisMessage) {
    return message(openLmisMessage.getCode(), (Object) openLmisMessage.getParams());
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

}
