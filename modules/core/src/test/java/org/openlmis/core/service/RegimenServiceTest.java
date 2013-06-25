package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.repository.RegimenRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegimenServiceTest {

  @Mock
  RegimenRepository repository;

  @Mock
  ProgramService programService;

  @InjectMocks
  RegimenService service;

  List<Regimen> regimens;
  final Regimen regimen = new Regimen();

  @Before
  public void setUp() throws Exception {
    regimens = new ArrayList<Regimen>() {{
      add(regimen);
    }};
  }

  @Test
  public void shouldSaveARegimens() {
    service.save(1L, regimens);
    verify(repository).insert(regimen);
    verify(repository).deleteByProgramId(1L);
    verify(programService).setRegimenTemplateConfigured(1L);
  }

  @Test
  public void shouldGetRegimensByProgram() {
    List<Regimen> expectedRegimens = new ArrayList<>();
    Long programId = 1l;
    when(repository.getByProgram(programId)).thenReturn(expectedRegimens);

    List<Regimen> regimenList = service.getByProgram(programId);

    assertThat(regimenList, is(expectedRegimens));
    verify(repository).getByProgram(programId);
  }

  @Test
  public void shouldGetAllRegimenCategories(){
    List<RegimenCategory> expectedRegimenCategories = new ArrayList<>();
    when(repository.getAllRegimenCategories()).thenReturn(expectedRegimenCategories);

    List<RegimenCategory> regimenCategories = service.getAllRegimenCategories();

    assertThat(regimenCategories, is(expectedRegimenCategories));
    verify(repository).getAllRegimenCategories();
  }

}
