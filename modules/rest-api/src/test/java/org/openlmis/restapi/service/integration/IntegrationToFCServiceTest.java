package org.openlmis.restapi.service.integration;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Soh;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.IntegrationRepository;
import org.openlmis.restapi.domain.SohDTO;
import org.openlmis.restapi.domain.integration.SynDataType;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class IntegrationToFCServiceTest {

    @Mock
    private IntegrationRepository integrationRepository;

    @InjectMocks
    private IntegrationToFCService integrationToFCService;

    @Test
    public void shouldGetSohByDateReturnSuccess() {
        Soh soh = new Soh();
        soh.setFacilityCode("fc-code");
        String dateStr = "20181001";
        when(integrationRepository.getSohByDate(createFromStartDate(dateStr), SynDataType.SOH.getCount(), 0))
                .thenReturn(Lists.newArrayList(soh));
        List<SohDTO> ss = integrationToFCService.getSohByDate(dateStr, 1);
        assertThat(ss.size(), is(1));
        assertThat(ss.get(0).getFacilityCode(), is("fc-code"));
    }

    @Test
    public void shouldGetPageInfoReturnSuccess() {
        String dateStr = "20181001";
        when(integrationRepository.getPageInfo(SynDataType.SOH.getTableName(), createFromStartDate(dateStr)))
                .thenReturn(2312);
        int pageNumber = integrationToFCService.getPageInfo(dateStr, "soh");
        assertThat(pageNumber, is(3));

    }

    private Date createFromStartDate(String fromStartDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return simpleDateFormat.parse(fromStartDate);
        } catch (ParseException e) {
            throw new DataException("please use right fromStartDate");
        }
    }
}