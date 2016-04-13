package org.openlmis.report.model.dto;


import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RequisitionDTOTest {
    private RequisitionDTO requisitionDTO = new RequisitionDTO();

    @Test
    public void shouldSetTypeAsNormalWhenIsNotEmergency() throws Exception {
        requisitionDTO.setEmergency(false);
        requisitionDTO.assignType();

        assertThat(requisitionDTO.getType(), is(RequisitionDTO.RequisitionType.NORMAL_TYPE));
    }

    @Test
    public void shouldSetTypeAsEmergencyWhenIsEmergency() throws Exception {
        requisitionDTO.setEmergency(true);
        requisitionDTO.assignType();

        assertThat(requisitionDTO.getType(), is(RequisitionDTO.RequisitionType.EMERGENCY_TYPE));
    }
}