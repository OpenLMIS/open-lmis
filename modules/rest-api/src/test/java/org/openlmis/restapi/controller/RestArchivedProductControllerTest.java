package org.openlmis.restapi.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.restapi.service.ArchivedProductService;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class RestArchivedProductControllerTest {

    @Mock
    ArchivedProductService archivedProductService;

    @Test
    public void shouldSaveArchivedProductCodes() {
        ArrayList<String> codes = new ArrayList<>();

        RestArchivedProductController restArchivedProductController = new RestArchivedProductController();
        restArchivedProductController.archivedProductService = archivedProductService;

        restArchivedProductController.updateArchivedProductsList(1l, codes);

        Mockito.verify(archivedProductService).updateArchivedProductList(1l, codes);
    }
}