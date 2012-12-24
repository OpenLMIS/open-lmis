package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.builder.SupplyLineBuilder;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.repository.SupplyLineRepository;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SupplyLineHandlerTest {

    SupplyLineRepository supplyLineRepository;
    SupplyLineHandler supplyLineHandler;

    @Before
    public void setUp() {
        supplyLineRepository = mock(SupplyLineRepository.class);
        supplyLineHandler = new SupplyLineHandler(supplyLineRepository);
    }

    @Test
    public void shouldSaveSupplyLine() {
        SupplyLine supplyLine = make(a(SupplyLineBuilder.defaultSupplyLine));
        supplyLineHandler.save(supplyLine, "user");
        assertThat(supplyLine.getModifiedBy(), is("user"));
        verify(supplyLineRepository).insert(supplyLine);

    }
}


