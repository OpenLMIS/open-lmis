package org.openlmis.restapi.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.repository.ArchivedProductRepository;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class ArchivedProductServiceTest {
    @Mock
    ArchivedProductRepository repository;

    @Test
    public void shouldUpdateArchivedProductList() {
        ArchivedProductService archivedProductService = new ArchivedProductService();
        archivedProductService.archivedProductRepository = repository;
        List<String> codes = new ArrayList<>();

        archivedProductService.updateArchivedProductList(1L, codes);

        Mockito.verify(repository).updateArchivedProductList(1L, codes);
    }

    @Test
    public void shouldGetAllArchivedProducts() {
        ArchivedProductService archivedProductService = new ArchivedProductService();
        archivedProductService.archivedProductRepository = repository;

        archivedProductService.getAllArchivedProducts(1L);

        Mockito.verify(repository).getAllArchivedProducts(1L);
    }
}