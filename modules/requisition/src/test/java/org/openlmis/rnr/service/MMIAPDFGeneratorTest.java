package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.*;

public class MMIAPDFGeneratorTest {

    private MMIAPDFGenerator mmiapdfGenerator;
    private Rnr requisition;
    private PhantomWrapper phantom;
    private StaticReferenceDataService staticReferenceDataService;
    private RequestAttributes attributes;

    @Before
    public void setUp() throws Exception {
        phantom = mock(PhantomWrapper.class);
        staticReferenceDataService = mock(StaticReferenceDataService.class);
        attributes = mock(RequestAttributes.class);

        mmiapdfGenerator = new MMIAPDFGenerator("/app/tomcat/openlmis/emailattachment/cache", staticReferenceDataService, phantom);
        requisition = make(a(RequisitionBuilder.defaultRequisition));
    }

    @Test
    public void shouldCallPhantomToGeneratePDF() throws Exception {
        //given
        when(attributes.getSessionId()).thenReturn("helloid");
        when(staticReferenceDataService.getPropertyValue("app.url")).thenReturn("localhost:9091");
        RequestContextHolder.setRequestAttributes(attributes);

        //when
        mmiapdfGenerator.generateMMIAPdf(requisition, "path.pdf");

        //then
        verify(phantom).generatePDF(
                "localhost:9091/public/pages/logistics/rnr/index.html#/view-requisition-mmia/1/3",
                "/app/tomcat/openlmis/emailattachment/cache/path.pdf",
                "helloid");
    }
}