package org.openlmis.web.controller;

import org.junit.Test;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.openlmis.web.controller.VendorEventFeedServiceHelper.parseAtomFeedContent;
import static org.openlmis.web.controller.VendorEventFeedServiceHelper.vendorMappingTemplate;

public class VendorEventFeedServiceHelperTest {
    @Test
    public void shouldReturnAtomFeedActualContent() {
        String atomFeedData = "{\"code\":\"12312\"}";
        String atomFeedContentValue = "<![CDATA[" + atomFeedData + "]]>";
        assertThat(parseAtomFeedContent(atomFeedContentValue), is(atomFeedData));
    }

    @Test
    public void shouldReturnEmptyStringForInvalidContentSection() {
        String atomFeedData = "{\"code\":\"12312\"}";
        String atomFeedContentValue = "[" + atomFeedData + "]";
        assertThat(parseAtomFeedContent(atomFeedContentValue), is(EMPTY));
    }

    @Test
    public void shouldReturnEmptyStringForEmptyContentSection() {
        String atomFeedContentValue = "<![CDATA[]]>";
        assertThat(parseAtomFeedContent(atomFeedContentValue), is(EMPTY));
    }

    @Test
    public void shouldReturnNullIfVendorMappingTemplateNotFound() throws IOException {
        assertNull(vendorMappingTemplate("vendorMapping_xyz_facility.xml"));
    }

    @Test
    public void shouldReturnFileIfVendorMappingTemplateExists() throws IOException {
        assertNotNull(vendorMappingTemplate("vendorMapping_commtrack_facility.xml"));
    }
}
