package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupplyLineMapper;
import org.springframework.dao.DuplicateKeyException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class SupplyLineRepositoryTest {

    SupplyLineRepository supplyLineRepository;

    @Mock
    SupplyLineMapper supplyLineMapper;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        supplyLineRepository = new SupplyLineRepository(supplyLineMapper);
    }

    @Test
    public void shouldInsertSupplyLine() {
        SupplyLine supplyLine = new SupplyLine();

        supplyLineRepository.insert(supplyLine);
        verify(supplyLineMapper).insert(supplyLine);
    }

    @Test
    public void shouldThrowExceptionForDuplicateSupplyLines() {
        SupplyLine supplyLine = new SupplyLine();
        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Duplicate entry for Supply Line found.");
        doThrow(new DuplicateKeyException("Duplicate entry for Supply Line found.")).when(supplyLineMapper).insert(supplyLine);
        supplyLineRepository.insert(supplyLine);
    }
}
