package org.openlmis.rnr.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.rnr.domain.Rnr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class MMIAPDFGenerator {

    @Value("${email.attachment.cache.path}")
    protected String cachePath;

    @Autowired
    private StaticReferenceDataService staticReferenceDataService;

    @Autowired
    private PhantomWrapper phantom;

    protected static Logger logger = LoggerFactory.getLogger(MMIAPDFGenerator.class);

    public String generateMMIAPdf(Rnr requisition, String fileNameForMMIAPdf) {
        String url = staticReferenceDataService.getPropertyValue("app.url") +
                "/public/pages/logistics/rnr/index.html#/view-requisition-mmia/" +
                requisition.getId() + "/" + requisition.getProgram().getId();

        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        String pathname = cachePath + "/" + fileNameForMMIAPdf;

        try {
            phantom.generatePDF(url, sessionId, pathname);
        } catch (Exception e) {
            logger.error("error occured when calling phantom" + e.getMessage());
        }

        return pathname;
    }

}
