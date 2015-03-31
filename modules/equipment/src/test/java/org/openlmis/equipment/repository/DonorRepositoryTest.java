package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.repository.mapper.DonorMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DonorRepositoryTest {

  @Mock
  DonorMapper mapper;

  @InjectMocks
  DonorRepository repository;

  @Test
  public void shouldGetAll() throws Exception {
    repository.getAll();
    verify(mapper).getAll();
  }

  @Test
  public void shouldGetAllWithDetails() throws Exception {
    repository.getAllWithDetails();
    verify(mapper).getAllWithDetails();
  }

  @Test
  public void shouldInsert() throws Exception {
    Donor donor = new Donor();
    repository.insert(donor);
    verify(mapper).insert(donor);
  }

  @Test
  public void shouldUpdate() throws Exception {
    Donor donor = new Donor();
    repository.update(donor);
    verify(mapper).update(donor);
  }

  @Test
  public void shouldGetDonorById() throws Exception {
    repository.getDonorById(1L);
    verify(mapper).getById(1L);
  }

  @Test
  public void shouldRemoveDonor() throws Exception {
    repository.removeDonor(4L);
    verify(mapper).remove(4L);
  }
}