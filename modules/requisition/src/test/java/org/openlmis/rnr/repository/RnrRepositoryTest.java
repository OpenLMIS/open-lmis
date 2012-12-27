package org.openlmis.rnr.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper;
import org.openlmis.rnr.repository.mapper.RnrLineItemMapper;
import org.openlmis.rnr.repository.mapper.RnrMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RnrRepositoryTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

    public static final Integer HIV = 1;

    @Mock
    RnrMapper rnrMapper;
    @Mock
    RnrLineItemMapper rnrLineItemMapper;
    @Mock
    LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;

    private RnrRepository rnrRepository;
    public Integer facilityId = 1;

    private LossesAndAdjustments lossesAndAdjustments = new LossesAndAdjustments();
    RnrLineItem rnrLineItem1;
    RnrLineItem rnrLineItem2;
    Rnr rnr;

    @Before
    public void setUp() throws Exception {
        rnrRepository = new RnrRepository(rnrMapper, rnrLineItemMapper, lossesAndAdjustmentsMapper);
        rnr = new Rnr();
        rnrLineItem1 = new RnrLineItem();
        rnrLineItem2 = new RnrLineItem();
        rnr.add(rnrLineItem1);
        rnr.add(rnrLineItem2);
        rnrLineItem1.addLossesAndAdjustments(lossesAndAdjustments);
        rnrLineItem2.addLossesAndAdjustments(lossesAndAdjustments);
    }

    @Test
    public void shouldInsertRnrAndItsLineItems() throws Exception {
        rnr.setId(1);
        rnrRepository.insert(rnr);

        verify(rnrMapper).insert(rnr);
        verify(rnrLineItemMapper, times(2)).insert(any(RnrLineItem.class));
        verify(lossesAndAdjustmentsMapper, times(2)).insert(any(RnrLineItem.class), any(LossesAndAdjustments.class));
        RnrLineItem rnrLineItem = rnr.getLineItems().get(0);
        assertThat(rnrLineItem.getRnrId(), is(1));
    }

    @Test
    public void shouldUpdateRnrAndItsLineItems() throws Exception {
        rnrRepository.update(rnr);

        verify(rnrMapper).update(rnr);
        verify(lossesAndAdjustmentsMapper, times(2)).update(any(RnrLineItem.class), any(LossesAndAdjustments.class));
        verify(rnrLineItemMapper, times(2)).update(any(RnrLineItem.class));
    }

    @Test
    public void shouldReturnRnrAndItsLineItemsByFacilityAndProgram() {
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
    public void shouldThrowErrorIfRnrNotDefined(){
        when(rnrMapper.getRequisitionByFacilityAndProgram(facilityId, HIV)).thenReturn(null);
        expectedException.expect(DataException.class);
        expectedException.expectMessage("Requisition does not exist. Please initiate.");
        Rnr rnr = rnrRepository.getRequisitionByFacilityAndProgram(facilityId, HIV);
    }
}
