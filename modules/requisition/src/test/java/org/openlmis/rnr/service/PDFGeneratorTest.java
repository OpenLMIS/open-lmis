package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.mockito.Mockito.*;

public class PDFGeneratorTest {

    private PDFGenerator PDFGenerator;
    private Rnr requisition;
    private PhantomWrapper phantom;
    private StaticReferenceDataService staticReferenceDataService;
    private RequestAttributes attributes;
    private ProgramService programService;
    private RequisitionService requisitionService;
    private Facility facility;

    @Before
    public void setUp() throws Exception {
        phantom = mock(PhantomWrapper.class);
        staticReferenceDataService = mock(StaticReferenceDataService.class);
        attributes = mock(RequestAttributes.class);
        programService = mock(ProgramService.class);
        requisitionService = mock(RequisitionService.class);

        PDFGenerator = new PDFGenerator(staticReferenceDataService, phantom, programService, requisitionService, "test.pdf");

        facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.name, "HF2")));
        requisition = make(a(RequisitionBuilder.defaultRequisition).but(with(RequisitionBuilder.facility, facility)));
    }

    @Test
    public void shouldCallPhantomToGeneratePDF() throws Exception {
        testParameterParsingForPhantom("MMIA", "mmia");
        testParameterParsingForPhantom("ESS_MEDS", "via");
    }

    @Test
    public void shouldCallPhantomToGeneratePDFForVIAProgram() throws Exception {
        testParameterParsingForPhantom("VIA", "via");
    }

    private void testParameterParsingForPhantom(String programCode, String urlPart) throws IOException, URISyntaxException, InterruptedException {
        //given
        when(attributes.getSessionId()).thenReturn("helloid");
        when(staticReferenceDataService.getPropertyValue("app.url")).thenReturn("localhost:9091");
        Program program = new Program();
        program.setCode(programCode);
        when(programService.getById(anyLong())).thenReturn(program);
        when(requisitionService.getFullRequisitionById(requisition.getId())).thenReturn(requisition);

        RequestContextHolder.setRequestAttributes(attributes);

        //when
        PDFGenerator.generatePdf(requisition.getId(), requisition.getProgram().getId(), "/app/tomcat/openlmis/emailattachment/cache");

        //then
        verify(phantom).generatePDF(
                "localhost:9091/public/pages/logistics/rnr/index.html#/view-requisition-" + urlPart + "/1",
                "/app/tomcat/openlmis/emailattachment/cache/Requi1_HF2_Month1_Yellow Fever.pdf",
                "helloid", urlPart);
    }
}