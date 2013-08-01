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
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@NoArgsConstructor
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MessageService {

  private MessageSource messageSource;

  @Setter
  @Getter
  private Locale currentLocale;

  @Value("${locales.supported}")
  private String locales;

  @Autowired
  public MessageService(MessageSource messageSource) {
    this.messageSource = messageSource;
    this.currentLocale = Locale.getDefault();
  }

  public String message(String key) {
    return message(key, currentLocale, (Object) null);
  }

  public String message(OpenLmisMessage openLmisMessage) {
    return message(openLmisMessage.getCode(), (Object) openLmisMessage.getParams());
  }

  public String message(String key, Object... args) {
    return message(key, currentLocale, args);
  }

  private String message(String key, Locale locale, Object... args) {
    return messageSource.getMessage(key, args, key, locale);
  }

  public Map<String, String> getLocales() {
    Map<String, String> localeMap = new HashMap<>();

    String[] localeMapSource = locales.split(",");

    for (String locale : localeMapSource) {
      String[] keyValuePair = locale.split(":");
      localeMap.put(keyValuePair[0], keyValuePair[1]);
    }

    return localeMap;
  }
}
