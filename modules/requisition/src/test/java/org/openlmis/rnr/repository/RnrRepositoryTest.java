package org.openlmis.rnr.repository;

import org.junit.Test;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.mapper.RnrLineItemMapper;
import org.openlmis.rnr.repository.mapper.RnrMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RnrRepositoryTest {

    public static final String HIV = "HIV";
    private RnrMapper rnrMapper = mock(RnrMapper.class);
    private RnrLineItemMapper rnrLineItemMapper = mock(RnrLineItemMapper.class);
    private RnrRepository rnrRepository = new RnrRepository(rnrMapper, rnrLineItemMapper);
    public Integer facilityId = 1;

    @Test
    public void shouldInsertRnrAndItsLineItems() throws Exception {
        Rnr rnr = new Rnr();
        rnr.add(new RnrLineItem());
        rnr.add(new RnrLineItem());
        when(rnrMapper.insert(rnr)).thenReturn(1);
        rnrRepository.insert(rnr);
        verify(rnrMapper).insert(rnr);
        verify(rnrLineItemMapper, times(2)).insert(any(RnrLineItem.class));
        assertThat(rnr.getId(), is(1));
        RnrLineItem rnrLineItem = rnr.getLineItems().get(0);
        assertThat(rnrLineItem.getRnrId(), is(1));
    }

    @Test
    public void shouldUpdateRnrAndItsLineItems() throws Exception {
        Rnr rnr = new Rnr();
        rnr.add(new RnrLineItem());
        rnr.add(new RnrLineItem());
        rnrRepository.update(rnr);
        verify(rnrMapper).update(rnr);
        verify(rnrLineItemMapper, times(2)).update(any(RnrLineItem.class));
    }

    @Test
    public void shouldReturnRnrAndItsLineItemsByFacilityAndProgram(){
        Rnr initiatedRequisition = new Rnr(facilityId, HIV, RnrStatus.INITIATED, "user");
        initiatedRequisition.setId(1);
        when(rnrMapper.getRequisitionByFacilityAndProgram(facilityId, HIV)).thenReturn(initiatedRequisition);
        List<RnrLineItem> lineItems = new ArrayList<>();
        when(rnrLineItemMapper.getRnrLineItemsByRnrId(1)).thenReturn(lineItems);
        Rnr rnr = rnrRepository.getRequisitionByFacilityAndProgram(facilityId, HIV);
        assertThat(rnr, is(equalTo(initiatedRequisition)));
        assertThat(rnr.getLineItems(), is(equalTo(lineItems)));
    }

    @Test
    public void shouldReturnEmptyRnrIfRnrByFacilityAndProgramDoesNotExist(){
        when(rnrMapper.getRequisitionByFacilityAndProgram(facilityId, HIV)).thenReturn(null);
        Rnr rnr = rnrRepository.getRequisitionByFacilityAndProgram(facilityId, HIV);
        assertThat(rnr, is(notNullValue()));
        assertThat(rnr.getId(), is(nullValue()));
    }
}
