package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.repository.mapper.ArchivedProductsMapper;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ArchivedProductRepositoryTest {
    @Mock
    private ArchivedProductsMapper mapper;
    private List<String> codes;
    private ArchivedProductRepository archivedProductRepository;

    @Before
    public void setUp() throws Exception {
        archivedProductRepository = new ArchivedProductRepository();
        archivedProductRepository.archivedProductsMapper = mapper;
        codes = new ArrayList<>();
    }

    @Test
    public void shouldSaveArchivedProductList() {
        codes.add("code1");
        codes.add("code2");

        archivedProductRepository.updateArchivedProductList(1L, codes);

        Mockito.verify(mapper).updateArchivedProductList(1L, "code1");
        Mockito.verify(mapper).updateArchivedProductList(1L, "code2");
    }

    @Test
    public void shouldClearArchivedProdutsBeforeUpdateArchivedProductList() {
        codes.add("code1");

        archivedProductRepository.updateArchivedProductList(1L, codes);

        Mockito.verify(mapper).clearArchivedProductList(1L);
        Mockito.verify(mapper).updateArchivedProductList(1L, "code1");
    }

    @Test
    public void shouldListArchivedProducts() {
        archivedProductRepository.getAllArchivedProducts(1L);

        Mockito.verify(mapper).listArchivedProducts(1L);
    }
}