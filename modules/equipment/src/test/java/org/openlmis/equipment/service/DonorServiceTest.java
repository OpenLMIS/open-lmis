package org.openlmis.equipment.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.repository.DonorRepository;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DonorServiceTest {

  @Mock
  DonorRepository repository;

  @InjectMocks
  DonorService service;

  @Test
  public void shouldInsertOnSave() throws Exception {
    Donor donor = new Donor();
    service.save(donor);
    verify(repository).insert(donor);
    verify(repository, never()).update(donor);
  }

  @Test
  public void shouldUpdateExistingRecordOnSave() throws Exception {
    Donor donor = new Donor();
    donor.setId(3L);
    service.save(donor);
    verify(repository, never()).insert(donor);
    verify(repository).update(donor);
  }

  @Test
  public void shouldGetAll() throws Exception {
    service.getAll();
    verify(repository).getAll();
  }

  @Test
  public void shouldGetAllWithDetails() throws Exception {
    service.getAllWithDetails();
    verify(repository).getAllWithDetails();

  }

  @Test
  public void shouldRemoveDonor() throws Exception {
    service.removeDonor(3L);
    verify(repository).removeDonor(3L);
  }

  @Test
  public void shouldGetById() throws Exception {
    service.getById(3L);
    verify(repository).getDonorById(3L);
  }
}