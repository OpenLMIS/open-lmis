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

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

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
    programDataItem1.setProgramDataForm(programDataForm);
    ProgramDataItem programDataItem2 = new ProgramDataItem();
    programDataItem2.setName("DEF");
    programDataItem2.setProgramDataForm(programDataForm);
    programDataForm.setProgramDataItems(asList(programDataItem1, programDataItem2));
    programDataRepository.createProgramDataForm(programDataForm);
    verify(programDataMapper).insert(programDataForm);
    verify(programDataItemMapper).insert(programDataItem1);
    verify(programDataItemMapper).insert(programDataItem2);
  }

  @Test
  public void shouldGetProgramDataFormsByFacilityId() {
    ProgramDataForm programDataForm1 = new ProgramDataForm();
    ProgramDataForm programDataForm2 = new ProgramDataForm();
    when(programDataMapper.getByFacilityId(12L)).thenReturn(asList(programDataForm1, programDataForm2));

    List<ProgramDataForm> programDataFormList = programDataRepository.getProgramDataFormsByFacilityId(12L);
    assertThat(programDataFormList.size(), is(2));
    assertThat(programDataFormList.get(0), is(programDataForm1));
    assertThat(programDataFormList.get(1), is(programDataForm2));
  }

}