/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@Category(UnitTests.class)
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
  public void shouldReturnNullIfVendorMappingTemplateNotFound() {
    assertNull(vendorEventFeedService.vendorMappingTemplate("vendorMapping_xyz_facility.xml"));
  }

  @Test
  public void shouldReturnFileIfVendorMappingTemplateExists() {
    assertNotNull(vendorEventFeedService.vendorMappingTemplate("vendorMapping_commtrack_facilities.xml"));
  }
}