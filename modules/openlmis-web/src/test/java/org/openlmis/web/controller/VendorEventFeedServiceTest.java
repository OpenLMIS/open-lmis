/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Test;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class VendorEventFeedServiceTest {

  VendorEventFeedService vendorEventFeedService = new VendorEventFeedService();

  @Test
  public void shouldReturnAtomFeedActualContent() {
    String atomFeedData = "{\"code\":\"12312\"}";
    String atomFeedContentValue = "<![CDATA[" + atomFeedData + "]]>";


    assertThat(vendorEventFeedService.parseAtomFeedContent(atomFeedContentValue), is(atomFeedData));
  }

  @Test
  public void shouldReturnEmptyStringForInvalidContentSection() {
    String atomFeedData = "{\"code\":\"12312\"}";
    String atomFeedContentValue = "[" + atomFeedData + "]";
    assertThat(vendorEventFeedService.parseAtomFeedContent(atomFeedContentValue), is(EMPTY));
  }

  @Test
  public void shouldReturnEmptyStringForEmptyContentSection() {
    String atomFeedContentValue = "<![CDATA[]]>";
    assertThat(vendorEventFeedService.parseAtomFeedContent(atomFeedContentValue), is(EMPTY));
  }

  @Test
  public void shouldReturnNullIfVendorMappingTemplateNotFound() throws IOException {
    assertNull(vendorEventFeedService.vendorMappingTemplate("vendorMapping_xyz_facility.xml"));
  }

  @Test
  public void shouldReturnFileIfVendorMappingTemplateExists() throws IOException {
    assertNotNull(vendorEventFeedService.vendorMappingTemplate("vendorMapping_commtrack_facility.xml"));
  }
}
