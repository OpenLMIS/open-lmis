package org.openlmis.rnr.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.rnr.domain.Rnr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class PDFGenerator {


    @Autowired
    private StaticReferenceDataService staticReferenceDataService;

    @Autowired
    private PhantomWrapper phantom;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RequisitionService requisitionService;

    protected static Logger logger = LoggerFactory.getLogger(PDFGenerator.class);

    @Getter
    private String nameForPdf;

    public String generatePdf(Long rnrId, Long programId, String path) {
        RnrProgram rnrProgram = RnrProgram.valueOf(programService.getById(programId).getCode());

        String url = staticReferenceDataService.getPropertyValue("app.url") +
                "/public/pages/logistics/rnr/index.html#/view-requisition-" +
                rnrProgram.toString() + "/" + rnrId;

        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        nameForPdf = getNameForPdf(requisitionService.getFullRequisitionById(rnrId));
        String pathname = path + "/" + nameForPdf;

        try {
            phantom.generatePDF(url, pathname, sessionId, rnrProgram.toString());
        } catch (Exception e) {
            logger.error("error occurred when calling phantom" + e.getMessage());
        }

        return pathname;
    }

    private String getNameForPdf(Rnr requisition) {
        return RequisitionEmailServiceForSIMAM.REQUI_FILE_NAME_PREFIX + requisition.getId() + "_" + requisition.getFacility().getName() + "_" + requisition.getPeriod().getName() + "_" +
                requisition.getProgram().getName() + ".pdf";
    }

    public enum RnrProgram {
        MMIA("mmia"),
        VIA("via"),
        MALARIA("al"),
        ESS_MEDS("via"),
        TEST_KIT("rapid-test"),
        PTV("ptv");

        private String programName;

        RnrProgram(String programName) {
            this.programName = programName;
        }

        @Override
        public String toString() {
            return programName;
        }
    }
}
