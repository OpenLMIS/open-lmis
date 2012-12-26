package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.springframework.dao.DuplicateKeyException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SupervisoryNodeRepositoryTest {

    SupervisoryNode supervisoryNode;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    SupervisoryNodeMapper supervisoryNodeMapper;
    @Mock
    FacilityRepository facilityRepository;

    private SupervisoryNodeRepository repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        supervisoryNode = new SupervisoryNode();
        SupervisoryNode parent = new SupervisoryNode();
        parent.setCode("PSN");
        supervisoryNode.setParent(parent);
        supervisoryNode.setFacility(new Facility());
        repository = new SupervisoryNodeRepository(supervisoryNodeMapper, facilityRepository);
    }

    @Test
    public void shouldGiveErrorIfDuplicateCodeFound() throws Exception {
        doThrow(new DuplicateKeyException("")).when(supervisoryNodeMapper).insert(supervisoryNode);
        when(supervisoryNodeMapper.getIdForCode(supervisoryNode.getParent().getCode())).thenReturn(1);

        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Duplicate SupervisoryNode Code");

        repository.save(supervisoryNode);

        verify(supervisoryNodeMapper).insert(supervisoryNode);
    }

    @Test
    public void shouldGiveErrorIfParentNodeCodeDoesNotExist() throws Exception {

        when(supervisoryNodeMapper.getIdForCode(supervisoryNode.getParent().getCode())).thenReturn(null);

        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Supervisory Node as Parent does not exist");

        repository.save(supervisoryNode);

        verify(supervisoryNodeMapper).getIdForCode(supervisoryNode.getParent().getCode());
    }

    @Test
    public void shouldGiveErrorIfFacilityCodeDoesNotExist() throws Exception {
        when(supervisoryNodeMapper.getIdForCode(supervisoryNode.getParent().getCode())).thenReturn(1);
        when(facilityRepository.getIdForCode(supervisoryNode.getFacility().getCode())).thenThrow(new DataException("Invalid Facility Code"));

        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Invalid Facility Code");

        repository.save(supervisoryNode);

        verify(facilityRepository).getIdForCode(supervisoryNode.getFacility().getCode());
        verify(supervisoryNodeMapper).getIdForCode(supervisoryNode.getParent().getCode());
    }

    @Test
    public void shouldSaveSupervisoryNode() throws Exception {
        when(supervisoryNodeMapper.getIdForCode(supervisoryNode.getParent().getCode())).thenReturn(1);
        when(facilityRepository.getIdForCode(supervisoryNode.getFacility().getCode())).thenReturn(1);

        repository.save(supervisoryNode);

        verify(facilityRepository).getIdForCode(supervisoryNode.getFacility().getCode());
        verify(supervisoryNodeMapper).getIdForCode(supervisoryNode.getParent().getCode());
        assertThat(supervisoryNode.getParent().getId(), is(1));
        assertThat(supervisoryNode.getFacility().getId(), is(1));
        verify(supervisoryNodeMapper).insert(supervisoryNode);
    }

    @Test
    public void shouldSaveSupervisoryNodeIfParentNotSupplied() throws Exception {
        when(facilityRepository.getIdForCode(supervisoryNode.getFacility().getCode())).thenReturn(1);
        supervisoryNode.setParent(null);
        repository.save(supervisoryNode);

        verify(facilityRepository).getIdForCode(supervisoryNode.getFacility().getCode());
        verify(supervisoryNodeMapper, never()).getIdForCode(anyString());
        assertThat(supervisoryNode.getParent(), is(nullValue()));
        assertThat(supervisoryNode.getFacility().getId(), is(1));
        verify(supervisoryNodeMapper).insert(supervisoryNode);
    }

    @Test
    public void shouldReturnIdForTheGivenCode() {
        when(supervisoryNodeMapper.getIdForCode("ABC")).thenReturn(10);
        assertThat(repository.getIdForCode("ABC"), is(10));
    }

    @Test
    public void shouldThrowExceptionWhenCodeDoesNotExist() {
        when(supervisoryNodeMapper.getIdForCode("ABC")).thenReturn(null);
        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Invalid SupervisoryNode Code");
        repository.getIdForCode("ABC");
    }

    @Test
    public void shouldReturnParentIdForASupervisoryNode() {
        when(supervisoryNodeMapper.getSupervisoryNode(10)).thenReturn(supervisoryNode);

        assertThat(repository.getSupervisoryNodeParentId(10), is(nullValue()));

        supervisoryNode.getParent().setId(20);
        assertThat(repository.getSupervisoryNodeParentId(10), is(20));
    }
}
