package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SupervisoryNodeControllerTest {

  @Mock
  SupervisoryNodeService supervisoryNodeService;


  @Test
  public void shouldGetAllSupervisoryNodes() throws Exception {
    SupervisoryNodeController controller = new SupervisoryNodeController(supervisoryNodeService);
    List<SupervisoryNode> expectedSupervisoryNodes = new ArrayList<>();
    when(supervisoryNodeService.getAll()).thenReturn(expectedSupervisoryNodes);

    ResponseEntity<OpenLmisResponse> response = controller.getAll();

    verify(supervisoryNodeService).getAll();
    List<SupervisoryNode> actual = (List<SupervisoryNode>) response.getBody().getData().get(SupervisoryNodeController.SUPERVISORY_NODES);
    assertThat(actual, is(expectedSupervisoryNodes));
  }
}
