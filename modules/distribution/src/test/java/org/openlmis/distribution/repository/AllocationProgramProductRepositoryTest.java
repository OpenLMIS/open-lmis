package org.openlmis.distribution.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.distribution.repository.mapper.IsaMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class AllocationProgramProductRepositoryTest {

  @InjectMocks
  AllocationProgramProductRepository repository;

  @Mock
  IsaMapper isaMapper;


  @Test
  public void shouldInsertISA() throws Exception {
    ProgramProductISA isa = new ProgramProductISA();
    repository.insertISA(isa);
    verify(isaMapper).insert(isa);
  }

  @Test
  public void shouldUpdateISA() throws Exception {
    ProgramProductISA isa = new ProgramProductISA();
    repository.updateISA(isa);
    verify(isaMapper).update(isa);
  }

  @Test
  public void shouldGetIsa() throws Exception {
    ProgramProductISA expectedIsa = new ProgramProductISA();
    when(isaMapper.getIsa(1l)).thenReturn(expectedIsa);

    ProgramProductISA isa = repository.getIsa(1l);

    verify(isaMapper).getIsa(1l);
    assertThat(expectedIsa, is(isa));
  }
}
