package org.openlmis.report.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.mapper.RequisitionReportsMapper;
import org.openlmis.report.model.dto.RequisitionDTO;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
@PrepareForTest(OpenLmisResponse.class)
public class SimpleTableControllerTest {

    @Mock
    private RequisitionReportsMapper requisitionReportsMapper;

    @InjectMocks
    private SimpleTableController controller;

    @Test
    public void shouldReturnRequisitionListGivenStartAndEnd() throws Exception {
        Date startTime = new Date();
        Date endTime = new Date();

        List<RequisitionDTO> requisitionDTOs = new ArrayList<RequisitionDTO>();
        RequisitionDTO requisitionDTO = new RequisitionDTO();
        requisitionDTO.setId(2L);
        requisitionDTOs.add(requisitionDTO);

        when(requisitionReportsMapper.getRequisitionList(startTime, endTime))
                .thenReturn(requisitionDTOs);

        ResponseEntity<OpenLmisResponse> response =
                controller.requisitionReport(startTime, endTime);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat((List<RequisitionDTO>) response.getBody().getData().get("rnr_list"), is
                (requisitionDTOs));
    }

    @Test
    public void shouldReturnFormattedDateWhenDataValue() throws Exception {

        WebDataBinder dataBinder = new WebDataBinder(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = "2015-10-01 12:00:00";
        Date value = dateFormat.parse(dateString);

        controller.initBinder(dataBinder);
        CustomDateEditor editor = (CustomDateEditor) dataBinder.findCustomEditor
                (Date.class, null);
        editor.setValue(value);
        String parsedDate = editor.getAsText();
        assertThat(dateString, is(parsedDate));
    }
}