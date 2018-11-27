package org.openlmis.report.generate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.report.generator.impl.ALReportGenerator;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ALReportGeneratorTest {

    @Mock
    MessageService messageService;

    @Mock
    RequisitionService requisitionService;

    @InjectMocks
    ALReportGenerator alReportGenerator;

    @Before
    public void init() {
        Map<String, String> map = new HashMap<>();
        map.put("report.header.al.treatments", "Nº of treatments\\ndispensed in the month");
        map.put("report.header.al.exist.stock", "Existent stock\\nat the end of Period");
        when(messageService.allMessages()).thenReturn(map);
    }

    @Test
    public void shouldReturnNormalData() {

        Date startDate = DateUtil.parseDate("2017-01-19 23:59:59");
        Date endDate = DateUtil.parseDate("2018-11-19 23:59:59");
        when(requisitionService.alReportRequisitionsByFacilityId(1,
                startDate, endDate)).thenReturn(mockRnrList());
        assertThat((List<List<String>>)(alReportGenerator.generate(paramMap()).get("KEY_EXCEL_CONTENT")),
                is(returnValue()));
        verify(requisitionService).alReportRequisitionsByFacilityId(1,
                startDate, endDate);
    }

    private Map<Object, Object> paramMap() {
        Map<Object, Object> paraMap = new HashMap<>();
        paraMap.put("startTime", "2017-01-19 23:59:59");
        paraMap.put("endTime", "2018-11-19 23:59:59");
        paraMap.put("reportType", "alReport");
        paraMap.put("provinceId", "1");
        paraMap.put("districtId", "1");
        paraMap.put("facilityId", "1");
        return paraMap;
    }

    private List<Rnr> mockRnrList() {
        List<Rnr> list = new ArrayList<Rnr>();
        Rnr rnr = new Rnr();
        list.add(rnr);
        List<RegimenLineItem> regimenLineItems = new ArrayList<RegimenLineItem>();
        rnr.setRegimenLineItems(regimenLineItems);
        RegimenLineItem item1 = new RegimenLineItem();
        item1.setName("Consultas AL US/APE Malaria 1x6");
        item1.setChw(1);
        item1.setHf(1);
        RegimenLineItem item2 = new RegimenLineItem();
        item2.setName("Consultas AL US/APE Malaria 2x6");
        item2.setChw(1);
        item2.setHf(1);
        RegimenLineItem item3 = new RegimenLineItem();
        item3.setName("Consultas AL US/APE Malaria 3x6");
        item3.setChw(1);
        item3.setHf(1);
        RegimenLineItem item4 = new RegimenLineItem();
        item4.setName("Consultas AL US/APE Malaria 4x6");
        item4.setChw(1);
        item4.setHf(1);
        RegimenLineItem item5 = new RegimenLineItem();
        item5.setName("Consultas AL STOCK Malaria 1x6");
        item5.setChw(1);
        item5.setHf(1);
        RegimenLineItem item6 = new RegimenLineItem();
        item6.setName("Consultas AL STOCK Malaria 2x6");
        item6.setChw(1);
        item6.setHf(1);
        RegimenLineItem item7 = new RegimenLineItem();
        item7.setName("Consultas AL STOCK Malaria 3x6");
        item7.setChw(1);
        item7.setHf(1);
        RegimenLineItem item8 = new RegimenLineItem();
        item8.setName("Consultas AL STOCK Malaria 4x6");
        item8.setChw(1);
        item8.setHf(1);
        regimenLineItems.add(item1);
        regimenLineItems.add(item2);
        regimenLineItems.add(item3);
        regimenLineItems.add(item4);
        regimenLineItems.add(item5);
        regimenLineItems.add(item6);
        regimenLineItems.add(item7);
        regimenLineItems.add(item8);
        return list;
    }

    private List<List<String>> returnValue() {
        List<List<String>> returnValue = new ArrayList<>();
        List<String> hf = new ArrayList<String>();
        hf.add("HF");
        listValueAdd(hf, "1", 8);
        List<String> chw = new ArrayList<String>();
        chw.add("CHW");
        listValueAdd(chw, "1", 8);
        List<String> total = new ArrayList<String>();
        total.add("TOTAL");
        listValueAdd(total, "2", 8);
        returnValue.add(hf);
        returnValue.add(chw);
        returnValue.add(total);
        return returnValue;
    }

    private void listValueAdd(List<String> list, String s, int n) {
        for (int i = 0; i < n; ++i) {
            list.add(s);
        }
    }

    @Test
    public void shouldReturnKeyNoData() {

        Map<Object, Object> paramMap = paramMap();
        Date startDate = DateUtil.parseDate("2017-01-19 23:59:59");
        Date endDate = DateUtil.parseDate("2018-11-19 23:59:59");
        when(requisitionService.alReportRequisitionsByFacilityId(1,
                startDate, endDate)).thenReturn(new ArrayList<Rnr>());
        alReportGenerator.generate(paramMap);
        assertThat((Boolean)(paramMap.get("KEY_NO_DATA")), is(true));
        verify(requisitionService).alReportRequisitionsByFacilityId(1,
                startDate, endDate);
    }
}
