package org.openlmis.core.repository;

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

    @Test
    public void shouldSaveArchivedProductList() {
        ArchivedProductRepository archivedProductRepository = new ArchivedProductRepository();
        archivedProductRepository.archivedProductsMapper = mapper;
        List<String> codes = new ArrayList<>();
        codes.add("code1");
        codes.add("code2");

        archivedProductRepository.updateArchivedProductList(1L, codes);

        Mockito.verify(mapper).updateArchivedProductList(1L, "code1");
        Mockito.verify(mapper).updateArchivedProductList(1L, "code2");
    }

    @Test
    public void shouldClearArchivedProdutsBeforeUpdateArchivedProductList() {
        ArchivedProductRepository archivedProductRepository = new ArchivedProductRepository();
        archivedProductRepository.archivedProductsMapper = mapper;
        List<String> codes = new ArrayList<>();
        codes.add("code1");

        archivedProductRepository.updateArchivedProductList(1L, codes);

        Mockito.verify(mapper).clearArchivedProductList(1L);
        Mockito.verify(mapper).updateArchivedProductList(1L, "code1");
    }
}