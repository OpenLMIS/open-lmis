package org.openlmis.rnr.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LossesAndAdjustmentsRepositoryTest {

    @Mock
    LossesAndAdjustmentsMapper lossesAndAdjustmentsMapper;

    @Test
    public void shouldSaveLossesAndAdjustments() throws Exception {
        LossesAndAdjustmentsRepository lossesAndAdjustmentsRepository = new LossesAndAdjustmentsRepository(lossesAndAdjustmentsMapper);

        LossesAndAdjustments lossesAndAdjustments = new LossesAndAdjustments();
        RnrLineItem rnrLineItem = new RnrLineItem();
        when(lossesAndAdjustmentsMapper.insert(rnrLineItem, lossesAndAdjustments)).thenReturn(1);

        lossesAndAdjustmentsRepository.save(rnrLineItem, lossesAndAdjustments);

        verify(lossesAndAdjustmentsMapper).insert(rnrLineItem, lossesAndAdjustments);
        assertThat(lossesAndAdjustments.getId(), is(1));
    }
}
