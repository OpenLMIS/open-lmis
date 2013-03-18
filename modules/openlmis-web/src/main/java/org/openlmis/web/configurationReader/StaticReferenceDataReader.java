package org.openlmis.web.configurationReader;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Data
@Component
@NoArgsConstructor
public class StaticReferenceDataReader {

  private MessageSource messageSource;

  @Autowired
  public StaticReferenceDataReader(MessageSource messageSource) {

    this.messageSource = messageSource;
  }

  public String getCurrency() {
    Object[] noArgs = null;
    return messageSource.getMessage("currencySymbol", noArgs, Locale.getDefault());
  }
}
