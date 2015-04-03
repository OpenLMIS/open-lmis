package org.openlmis.vaccine.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.repository.mapper.VitaminSupplementationAgeGroupMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VitaminSupplementationAgeGroupRepositoryTest {

  @Mock
  VitaminSupplementationAgeGroupMapper mapper;

  @InjectMocks
  VitaminSupplementationAgeGroupRepository repository;

  @Test
  public void shouldGetAll() throws Exception {
    repository.getAll();
    verify(mapper).getAll();
  }
}