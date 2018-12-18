package org.openlmis.restapi.service.integration;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.Soh;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.IntegrationRepository;
import org.openlmis.restapi.domain.SohDTO;
import org.openlmis.restapi.domain.integration.SynDataType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class IntegrationToFCService {

    private static Logger logger = Logger.getLogger(IntegrationToFCService.class);

    private IntegrationRepository integrationRepository;

    @Autowired
    public IntegrationToFCService(IntegrationRepository integrationRepository) {
        this.integrationRepository = integrationRepository;
    }

    public List<SohDTO> getSohByDate(String fromStartDate, int startPage) {
        int startPosition = getStartPosition(startPage, SynDataType.SOH);
        logger.info(String.format("Get soh fromStartDate=%s, startPosition=%s", fromStartDate, startPosition));
        List<Soh> sohs = integrationRepository.getSohByDate(createFromStartDate(fromStartDate), SynDataType.SOH.getCount(), startPosition);
        logger.info(String.format("Get soh from Db size=%d", sohs.size()));
        return FluentIterable.from(sohs).transform(new Function<Soh, SohDTO>() {
            @Override
            public SohDTO apply(Soh source) {
                SohDTO target = new SohDTO();
                BeanUtils.copyProperties(source, target);
                return target;
            }
        }).toList();
    }

    public Integer getPageInfo(String fromStartDate, String type) {
        logger.info(String.format("Get page params fromStartDate=%s, type=%s", fromStartDate, type));
        SynDataType synDataType = SynDataType.getSynDataType(type);
        Integer totalCount = integrationRepository.getPageInfo(synDataType.getTableName(), createFromStartDate(fromStartDate));
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

    private Date createFromStartDate(String fromStartDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return simpleDateFormat.parse(fromStartDate);
        } catch (ParseException e) {
            logger.error("please use right fromStartDate", e);
            throw new DataException("please use right fromStartDate");
        }
    }


    private int getStartPosition(int startPage, SynDataType synDataType) {
        return (startPage - 1) * synDataType.getCount();
    }
}
