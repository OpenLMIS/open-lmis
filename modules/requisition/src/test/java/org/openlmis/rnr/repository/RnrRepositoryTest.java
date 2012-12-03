package org.openlmis.rnr.repository;

import org.junit.Test;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.mapper.RnrLineItemMapper;
import org.openlmis.rnr.repository.mapper.RnrMapper;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RnrRepositoryTest {

    private RnrMapper rnrMapper = mock(RnrMapper.class);
    private RnrLineItemMapper rnrLineItemMapper = mock(RnrLineItemMapper.class);
    private RnrRepository rnrRepository = new RnrRepository(rnrMapper, rnrLineItemMapper);

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
}
