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
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@NoArgsConstructor
public class MessageService {

  private MessageSource messageSource;

  @Setter
  @Getter
  private Locale currentLocale;

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
}
