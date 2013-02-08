package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.defaultSupervisoryNode;


public class RequisitionGroupServiceTest {

  private RequisitionGroupService requisitionGroupService;

  @Mock
  private RequisitionGroupRepository requisitionGroupRepository;

  @Mock
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    requisitionGroupService = new RequisitionGroupService(requisitionGroupRepository, supervisoryNodeRepository);
  }

  @Test
  public void shouldSaveARequisitionGroup() {
    SupervisoryNode supervisoryNode = make(a(defaultSupervisoryNode));
    RequisitionGroup requisitionGroup = make(a(defaultRequisitionGroup));
    requisitionGroup.setSupervisoryNode(supervisoryNode);

    requisitionGroupService.save(requisitionGroup);

    verify(supervisoryNodeRepository).getIdForCode(supervisoryNode.getCode());
    verify(requisitionGroupRepository).insert(requisitionGroup);
  }
}
