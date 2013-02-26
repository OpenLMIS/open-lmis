package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class GeographicZoneRepositoryTest {

  GeographicZoneRepository repository;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private GeographicZoneMapper mapper;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    repository = new GeographicZoneRepository(mapper);
  }

  @Test
  public void shouldSaveGeographicZone() throws Exception {

    GeographicZone geographicZone = new GeographicZone();

    repository.save(geographicZone);

    verify(mapper).insert(geographicZone);
  }

  @Test
  public void shouldThrowErrorIfDuplicateCodeFound() throws Exception {

    GeographicZone geographicZone = new GeographicZone();
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Geographic Zone Code found");

    doThrow(new DuplicateKeyException("Duplicate Geographic Zone Code found")).when(mapper).insert(geographicZone);

    repository.save(geographicZone);

  }

  @Test
  public void shouldThrowInvalidGeographicLevelCodeErrorIfInvalidGeographicCodeFound() throws Exception {
    GeographicZone geographicZone = new GeographicZone();
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Geographic Level Code");

    doThrow(new DataException("Invalid Geographic Level Code")).when(mapper).insert(geographicZone);

    repository.save(geographicZone);

  }
}
