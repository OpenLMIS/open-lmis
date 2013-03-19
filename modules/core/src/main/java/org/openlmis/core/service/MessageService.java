package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@NoArgsConstructor
public class MessageService {

  MessageSource messageSource;

  @Autowired
  public MessageService(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public String message(String key) {
    return message(key, null, Locale.getDefault());
  }

  public String message(String key, Object[] args) {
    return message(key, args, Locale.getDefault());
  }

  private String message(String key, Object[] args, Locale locale) {
    return messageSource.getMessage(key, args, locale);
  }
}
