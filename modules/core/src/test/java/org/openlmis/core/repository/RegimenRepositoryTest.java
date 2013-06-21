package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.repository.mapper.RegimenCategoryMapper;
import org.openlmis.core.repository.mapper.RegimenMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RegimenRepositoryTest {

  @Mock
  RegimenCategoryMapper regimenCategoryMapper;

  @Mock
  RegimenMapper mapper;

  @InjectMocks
  RegimenRepository repository;


  @Test
  public void shouldInsertARegimen() {
    Regimen regimen = new Regimen();
    repository.insert(regimen);
    verify(mapper).insert(regimen);
  }

  @Test
  public void shouldUpdateARegimen() {
    Regimen regimen = new Regimen();
    repository.update(regimen);
    verify(mapper).update(regimen);
  }

  @Test
  public void shouldGetRegimenByProgram() {
    List<Regimen> expectedRegimens = new ArrayList<>();
    Long programId = 1l;
    when(mapper.getByProgram(programId)).thenReturn(expectedRegimens);

    List<Regimen> regimens = repository.getByProgram(programId);

    assertThat(regimens,is(expectedRegimens));
    verify(mapper).getByProgram(programId);
  }

  @Test
  public void shouldGetAllRegimenCategories(){
    List<RegimenCategory> expectedRegimenCategories = new ArrayList<>();
    when(regimenCategoryMapper.getAll()).thenReturn(expectedRegimenCategories);

    List<RegimenCategory> regimenCategories = repository.getAllRegimenCategories();

    assertThat(regimenCategories, is(expectedRegimenCategories));
    verify(regimenCategoryMapper).getAll();
  }

}
