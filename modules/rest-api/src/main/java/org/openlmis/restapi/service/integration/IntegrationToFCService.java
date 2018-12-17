package org.openlmis.restapi.service.integration;

import org.apache.log4j.Logger;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.PageRepository;
import org.openlmis.restapi.domain.integration.SynDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class IntegrationToFCService {

    Logger logger = Logger.getLogger(IntegrationToFCService.class);

    private PageRepository pageRepository;

    @Autowired
    public IntegrationToFCService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public Integer getPageInfo(String fromStartDate, String type) {
        logger.info(String.format("Get page params fromStartDate=%s, type=%s", fromStartDate, type));
        SynDataType synDataType = SynDataType.getSynDataType(type);
        Integer totalCount = pageRepository.getPageInfo(synDataType.getTableName(), createFromStartDate(fromStartDate));
        logger.info(String.format("Get page total count=%s", totalCount));
        if (totalCount <= 0) {
            return 0;
        }
        return totalCount < synDataType.getCount() ? 1 : getPage(totalCount, synDataType.getCount());
    }

    private Integer getPage(Integer totalCount, Integer count) {
        Integer page = totalCount / count;
        if (totalCount % count > 0) {
            page += 1;
        }
        return page;
    }

    private static Date createFromStartDate(String fromStartDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return simpleDateFormat.parse(fromStartDate);
        } catch (ParseException e) {
            throw new DataException("please use right fromStartDate");
        }
    }
}
