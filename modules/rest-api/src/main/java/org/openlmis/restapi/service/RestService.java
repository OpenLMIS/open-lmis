/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.UserService;
import org.openlmis.core.service.VendorService;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@NoArgsConstructor
public class RestService {

  public static final String USER_USERNAME_INCORRECT = "user.username.incorrect";
  @Autowired
  UserService userService;

  @Autowired
  RequisitionService requisitionService;

  @Autowired
  VendorService vendorService;

  @Transactional
  public Rnr submitReport(Report report) {
    fillVendor(report);
    report.validate();

    User user = getValidatedUser(report);


    Rnr requisition = requisitionService.initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), user.getId());

    requisition.setFullSupplyLineItems(report.getProducts());

    requisitionService.submit(requisition);

    requisitionService.authorize(requisition);

    return requisition;
  }

  private void fillVendor(Report report) {
    Vendor vendor = vendorService.getByName(report.getVendor().getName());
    report.setVendor(vendor);
  }

  private User getValidatedUser(Report report) {
    User reportUser = new User();
    reportUser.setUserName(report.getUserId());
    reportUser.setVendorId(report.getVendor().getId());
    User user = userService.getByUsernameAndVendorId(reportUser);
    if (user == null) {
      throw new DataException(USER_USERNAME_INCORRECT);
    }
    return user;
  }

}
