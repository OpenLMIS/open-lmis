/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class VendorService {

  @Autowired
  VendorRepository repository;

  public Vendor getByName(String name) {
    return repository.getByName(name);
  }

  public boolean authenticate(Vendor vendor) {
    if (!vendor.isValid())
      return false;

    return vendor.getAuthToken().equals(repository.getToken(vendor.getName()));
  }

  public Vendor getByUserId(Long id) {
    return repository.getByUserId(id);
  }
}
