/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
    return messageSource.getMessage("label.currency.symbol", noArgs, Locale.getDefault());
  }
}
