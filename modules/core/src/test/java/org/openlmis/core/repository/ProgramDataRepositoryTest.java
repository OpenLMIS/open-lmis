package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.core.domain.moz.ProgramDataItem;
import org.openlmis.core.repository.mapper.ProgramDataItemMapper;
import org.openlmis.core.repository.mapper.ProgramDataMapper;
import org.openlmis.db.categories.UnitTests;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramDataRepositoryTest {

  @Mock
  private ProgramDataMapper programDataMapper;

  @Mock
  private ProgramDataItemMapper programDataItemMapper;

  @InjectMocks
  private ProgramDataRepository programDataRepository;

  @Test
  public void shouldCallProgramDataMapper() {
    ProgramDataForm programDataForm = new ProgramDataForm();
    ProgramDataItem programDataItem1 = new ProgramDataItem();
    programDataItem1.setName("ABC");
    ProgramDataItem programDataItem2 = new ProgramDataItem();
    programDataItem2.setName("DEF");
    programDataForm.setProgramDataItems(asList(programDataItem1, programDataItem2));
    programDataRepository.createProgramDataForm(programDataForm);
    verify(programDataMapper).insert(programDataForm);
    verify(programDataItemMapper).insert(programDataItem1);
    verify(programDataItemMapper).insert(programDataItem2);
  }


}