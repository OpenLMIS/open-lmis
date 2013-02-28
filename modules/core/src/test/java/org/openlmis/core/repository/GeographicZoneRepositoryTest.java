package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class GeographicZoneRepositoryTest {

  GeographicZoneRepository repository;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private GeographicZoneMapper mapper;

  private GeographicZone geographicZone;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    repository = new GeographicZoneRepository(mapper);
    geographicZone = new GeographicZone();
    geographicZone.setLevel(new GeographicLevel(null, "abc", null));
    geographicZone.setParent(new GeographicZone(null, "xyz", null, null, null, null));
  }

  @Test
  public void shouldSaveGeographicZone() throws Exception {
    when(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(new GeographicLevel(1, "abc", "abc"));
    when(mapper.getGeographicZoneByCode(geographicZone.getParent().getCode())).thenReturn(new GeographicZone(1, "xyz", "xyz", null, null, null));

    repository.save(geographicZone);

    verify(mapper).getGeographicLevelByCode("abc");
    verify(mapper).getGeographicZoneByCode("xyz");
    assertThat(geographicZone.getLevel().getId(), is(1));
    assertThat(geographicZone.getParent().getId(), is(1));
    verify(mapper).insert(geographicZone);
  }

  @Test
  public void shouldThrowAnExceptionIfParentCodeIsInvalid() throws Exception {

    when(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(new GeographicLevel(1, "abc", "abc"));
    when(mapper.getGeographicZoneByCode(geographicZone.getParent().getCode())).thenReturn(null);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Geographic Zone Parent Code");

    repository.save(geographicZone);
  }

  @Test
  public void shouldThrowAnExceptionIfGeographicLevelCodeIsInvalid() throws Exception {

    when(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(null);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Geographic Level Code");

    repository.save(geographicZone);
  }

  @Test
  public void shouldThrowErrorIfDuplicateCodeFound() throws Exception {
    when(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(new GeographicLevel(1, "abc", "abc"));
    when(mapper.getGeographicZoneByCode(geographicZone.getParent().getCode())).thenReturn(new GeographicZone(1, "xyz", "xyz", null, null, null));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Geographic Zone Code");

    doThrow(new DuplicateKeyException("Duplicate Geographic Zone Code")).when(mapper).insert(geographicZone);

    repository.save(geographicZone);
  }

  @Test
  public void shouldThrowErrorIfIncorrectDataLength() throws Exception {
    when(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(new GeographicLevel(1, "abc", "abc"));
    when(mapper.getGeographicZoneByCode(geographicZone.getParent().getCode())).thenReturn(new GeographicZone(1, "xyz", "xyz", null, null, null));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Incorrect Data Length");

    doThrow(new DataIntegrityViolationException("Incorrect Data Length")).when(mapper).insert(geographicZone);

    repository.save(geographicZone);
  }

  @Test
  public void shouldGetZoneByCode() throws Exception {
    GeographicZone expected = new GeographicZone();
    when(mapper.getGeographicZoneByCode("code")).thenReturn(expected);

    GeographicZone zone = repository.getByCode("code");

    assertThat(expected, is(zone));
    verify(mapper).getGeographicZoneByCode("code");
  }
}
