package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.GeographicLevelMapper;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class GeographicZoneRepositoryTest {

  public static final String ROOT_GEOGRAPHIC_ZONE_CODE = "Root";
  public static final String ROOT_GEOGRAPHIC_ZONE_NAME = "Root";

  GeographicZoneRepository repository;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private GeographicZoneMapper mapper;
  @Mock
  private GeographicLevelMapper geographicLevelMapper;
  private GeographicZone geographicZone;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    repository = new GeographicZoneRepository(mapper, geographicLevelMapper);
    geographicZone = new GeographicZone();
    geographicZone.setLevel(new GeographicLevel(null, "abc", null, null));
    geographicZone.setParent(new GeographicZone(null, "xyz", null, null, null, null));
  }

  @Test
  public void shouldSaveGeographicZone() throws Exception {
    when(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(new GeographicLevel(1, "abc", "abc", 1));
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

    when(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(new GeographicLevel(1, "abc", "abc", 1));
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
    when(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(new GeographicLevel(1, "abc", "abc", 1));
    when(mapper.getGeographicZoneByCode(geographicZone.getParent().getCode())).thenReturn(new GeographicZone(1, "xyz", "xyz", null, null, null));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Geographic Zone Code");

    doThrow(new DuplicateKeyException("Duplicate Geographic Zone Code")).when(mapper).insert(geographicZone);

    repository.save(geographicZone);
  }

  @Test
  public void shouldThrowErrorIfIncorrectDataLength() throws Exception {
    when(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(new GeographicLevel(1, "abc", "abc", 1));
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

  @Test
  public void shouldSetRootAsParentIfParentIsNull() throws Exception {
    GeographicZone expected = new GeographicZone(1, "Root", "Root", null, null, null);
    when(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode())).thenReturn(new GeographicLevel(1, "abc", "abc", 1));
    when(mapper.getGeographicZoneByCode(ROOT_GEOGRAPHIC_ZONE_CODE)).thenReturn(expected);
    geographicZone.setParent(null);

    repository.save(geographicZone);

    assertThat(geographicZone.getParent().getCode(), is(ROOT_GEOGRAPHIC_ZONE_CODE));
    assertThat(geographicZone.getParent().getName(), is(ROOT_GEOGRAPHIC_ZONE_NAME));
  }

  @Test
  public void shouldGetLowestGeographicLevel() {
    when(geographicLevelMapper.getLowestGeographicLevel()).thenReturn(1);
    assertThat(repository.getLowestGeographicLevel(), is(1));
  }
}
