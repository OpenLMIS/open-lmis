/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class LocaleService {

  @Value("${locales.supported}")
  private String locales;

  @Autowired
  MessageService messageService;

  public Map<String, String> getLocales() {

    Map<String, String> localeMap = new HashMap<>();

    String[] localeMapSource = locales.split(",");

    for (String locale : localeMapSource) {
      String[] keyValuePair = locale.split(":");
      localeMap.put(keyValuePair[0], keyValuePair[1]);
    }

    return localeMap;
  }

  public void changeLocale(HttpServletRequest request) {
    messageService.setCurrentLocale(RequestContextUtils.getLocale(request));
  }


}
