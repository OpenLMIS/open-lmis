/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.demographics.test;

import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.*;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.demographics.domain.AnnualDistrictEstimateEntry;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.demographics.repository.AnnualDistrictEstimateRepository;
import org.openlmis.demographics.service.AnnualFacilityDemographicEstimateService;
import org.openlmis.demographics.service.PopulationService;
import org.openlmis.report.mapper.lookup.FacilityLevelMapper;
import org.openlmis.report.model.dto.FacilityLevelTree;

import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.when;

public class FacilityTreeTest
{
    @Mock
    protected FacilityProgramProductRepository facilityProgramProductRepository;

    @Mock
    protected FacilityRepository facilityRepository;

    @Mock
    protected FacilityService facilityService;

    @Mock
    protected FacilityLevelMapper levelMapper;

    @Mock
    protected SupervisoryNodeRepository supervisoryNodeRepository;

    @Mock
    protected RequisitionGroupRepository requisitionGroupRepository;

    @Mock
    protected RequisitionGroupMemberRepository requisitionGroupMemberRepository;

    @Mock
    protected AnnualFacilityDemographicEstimateService annualFacilityDemographicEstimateService;

    @Mock
    protected AnnualDistrictEstimateRepository annualDistrictEstimateRepository;


    protected Program program;
    protected ProgramProduct programProduct;

    protected Facility cvs;
    protected Facility rvs1, rvs2;
    protected Facility dvs1, dvs2, dvs3;
    protected Facility sdp1, sdp2, sdp3, sdp4, sdp5;

    protected EstimateCategory category;

    public void setup()
    {
        program = make(a(ProgramBuilder.defaultProgram));
        programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));

        category = new EstimateCategory();
        category.setId(1L);
        category.setName("Population");

        setupFacilityHierarchy();
    }

    protected void setupFacilityHierarchy()
    {
        cvs = createAndRegisterFacility("cvs", false, 1L, "CVS");
        SupervisoryNode cvsNode = createSupervisoryNode(cvs, 1L);

        rvs1 = createAndRegisterFacility("rvs", false, 2L, "RVS1");
        rvs2 = createAndRegisterFacility("rvs", false, 3L, "RVS2");
        RequisitionGroup cvsGroup = createRequisitionGroup(12L);
        RequisitionGroupMember rvs1Member = new RequisitionGroupMember(cvsGroup, rvs1);
        RequisitionGroupMember rvs2Member = new RequisitionGroupMember(cvsGroup, rvs2);
        SupervisoryNode rvs1Node = createSupervisoryNode(rvs1, 2L);
        SupervisoryNode rvs2Node = createSupervisoryNode(rvs2, 3L);

        dvs1 = createAndRegisterFacility("dvs", false, 4L, "DVS1");
        dvs2 = createAndRegisterFacility("dvs", false, 5L, "DVS2");
        RequisitionGroup rvs1Group = createRequisitionGroup(13L);
        RequisitionGroupMember dvs1Member = new RequisitionGroupMember(rvs1Group, dvs1);
        RequisitionGroupMember dvs2Member = new RequisitionGroupMember(rvs1Group, dvs2);
        SupervisoryNode dvs1Node = createSupervisoryNode(dvs1, 4L);
        SupervisoryNode dvs2Node = createSupervisoryNode(dvs2, 5L);

        dvs3 = createAndRegisterFacility("dvs", false, 6L, "DVS3");
        RequisitionGroup rvs2Group = createRequisitionGroup(14L);
        RequisitionGroupMember dvs3Member = new RequisitionGroupMember(rvs2Group, dvs3);
        SupervisoryNode dvs3Node = createSupervisoryNode(dvs3, 6L);

        sdp1 = createAndRegisterFacility("heac", true, 7L, "SDP1");
        sdp2 = createAndRegisterFacility("heac", true, 8L, "SDP2");
        RequisitionGroup dvs1Group = createRequisitionGroup(15L);
        RequisitionGroupMember sdp1Member = new RequisitionGroupMember(dvs1Group, sdp1);
        RequisitionGroupMember sdp2Member = new RequisitionGroupMember(dvs1Group, sdp2);

        sdp3 = createAndRegisterFacility("heac", true, 9L, "SDP3");
        RequisitionGroup dvs2Group = createRequisitionGroup(16L);
        RequisitionGroupMember sdp3Member = new RequisitionGroupMember(dvs2Group, sdp3);

        sdp4 = createAndRegisterFacility("heac", true, 10L, "SDP4");
        sdp5 = createAndRegisterFacility("heac", true, 11L, "SDP5");
        RequisitionGroup dvs3Group = createRequisitionGroup(17L);
        RequisitionGroupMember sdp4Member = new RequisitionGroupMember(dvs3Group, sdp4);
        RequisitionGroupMember sdp5Member = new RequisitionGroupMember(dvs3Group, sdp5);

        when(supervisoryNodeRepository.getFor(sdp1, program)).thenReturn(dvs1Node);
        when(supervisoryNodeRepository.getFor(sdp2, program)).thenReturn(dvs1Node);
        when(supervisoryNodeRepository.getFor(sdp3, program)).thenReturn(dvs2Node);
        when(supervisoryNodeRepository.getFor(sdp4, program)).thenReturn(dvs3Node);
        when(supervisoryNodeRepository.getFor(sdp5, program)).thenReturn(dvs3Node);
        when(supervisoryNodeRepository.getFor(dvs1, program)).thenReturn(rvs1Node);
        when(supervisoryNodeRepository.getFor(dvs2, program)).thenReturn(rvs1Node);
        when(supervisoryNodeRepository.getFor(dvs3, program)).thenReturn(rvs2Node);
        when(supervisoryNodeRepository.getFor(rvs1, program)).thenReturn(cvsNode);
        when(supervisoryNodeRepository.getFor(rvs2, program)).thenReturn(cvsNode);
        when(supervisoryNodeRepository.getAllParentSupervisoryNodesInHierarchy(dvs1Node)).thenReturn(Arrays.asList(dvs1Node, rvs1Node, cvsNode));
        when(supervisoryNodeRepository.getAllChildSupervisoryNodesInHierarchy(dvs1Node)).thenReturn(Collections.singletonList(dvs1Node));
        when(supervisoryNodeRepository.getAllChildSupervisoryNodesInHierarchy(rvs1Node)).thenReturn(Arrays.asList(rvs1Node, dvs1Node, dvs2Node));
        when(supervisoryNodeRepository.getAllChildSupervisoryNodesInHierarchy(cvsNode)).thenReturn(Arrays.asList(cvsNode, rvs1Node, rvs2Node, dvs1Node, dvs2Node, dvs3Node));
        when(requisitionGroupRepository.getRequisitionGroups(Collections.singletonList(dvs1Node))).thenReturn(Collections.singletonList(dvs1Group));
        when(requisitionGroupRepository.getRequisitionGroups(Collections.singletonList(dvs2Node))).thenReturn(Collections.singletonList(dvs2Group));
        when(requisitionGroupRepository.getRequisitionGroups(Collections.singletonList(dvs3Node))).thenReturn(Collections.singletonList(dvs3Group));
        when(requisitionGroupRepository.getRequisitionGroups(Collections.singletonList(rvs1Node))).thenReturn(Collections.singletonList(rvs1Group));
        when(requisitionGroupRepository.getRequisitionGroups(Collections.singletonList(rvs2Node))).thenReturn(Collections.singletonList(rvs2Group));
        when(requisitionGroupRepository.getRequisitionGroups(Collections.singletonList(cvsNode))).thenReturn(Collections.singletonList(cvsGroup));
        when(requisitionGroupRepository.getRequisitionGroups(Arrays.asList(rvs1Node, dvs1Node, dvs2Node))).thenReturn(Arrays.asList(dvs1Group, dvs2Group, rvs1Group));
        when(requisitionGroupRepository.getRequisitionGroups(Arrays.asList(cvsNode, rvs1Node, rvs2Node, dvs1Node, dvs2Node, dvs3Node))).thenReturn(Arrays.asList(dvs1Group, dvs2Group, dvs3Group, rvs1Group, rvs2Group, cvsGroup));
        when(requisitionGroupMemberRepository.getMembersBy(dvs1Group.getId())).thenReturn(Arrays.asList(sdp1Member, sdp2Member));
        when(requisitionGroupMemberRepository.getMembersBy(dvs2Group.getId())).thenReturn(Collections.singletonList(sdp3Member));
        when(requisitionGroupMemberRepository.getMembersBy(dvs3Group.getId())).thenReturn(Arrays.asList(sdp4Member, sdp5Member));
        when(requisitionGroupMemberRepository.getMembersBy(rvs1Group.getId())).thenReturn(Arrays.asList(dvs1Member, dvs2Member));
        when(requisitionGroupMemberRepository.getMembersBy(rvs2Group.getId())).thenReturn(Collections.singletonList(dvs3Member));
        when(requisitionGroupMemberRepository.getMembersBy(cvsGroup.getId())).thenReturn(Arrays.asList(rvs1Member, rvs2Member));
        when(facilityRepository.getById(sdp1.getId())).thenReturn(sdp1);
        when(facilityRepository.getById(sdp2.getId())).thenReturn(sdp2);
        when(facilityRepository.getById(sdp3.getId())).thenReturn(sdp3);
        when(facilityRepository.getById(sdp4.getId())).thenReturn(sdp4);
        when(facilityRepository.getById(sdp5.getId())).thenReturn(sdp5);
        when(facilityRepository.getById(dvs1.getId())).thenReturn(dvs1);
        when(facilityRepository.getById(dvs2.getId())).thenReturn(dvs2);
        when(facilityRepository.getById(dvs3.getId())).thenReturn(dvs3);
        when(facilityRepository.getById(rvs1.getId())).thenReturn(rvs1);
        when(facilityRepository.getById(rvs2.getId())).thenReturn(rvs2);
        when(facilityRepository.getById(cvs.getId())).thenReturn(cvs);
        when(facilityRepository.getAllByFacilityTypeCode("rvs")).thenReturn(Arrays.asList(rvs1, rvs2));

        when(facilityProgramProductRepository.getOverriddenIsa(programProduct.getId(), sdp1.getId())).thenReturn(new ISA(0.0, 0, 7.5, 0.0, 0, 0, 0, category.getId()));
        when(facilityProgramProductRepository.getOverriddenIsa(programProduct.getId(), sdp2.getId())).thenReturn(new ISA(0.0, 0, 9.0, 0.0, 0, 0, 0, category.getId()));
        when(facilityProgramProductRepository.getOverriddenIsa(programProduct.getId(), sdp3.getId())).thenReturn(new ISA(0.0, 0, 9.5, 0.0, 0, 0, 0, category.getId()));
        when(facilityProgramProductRepository.getOverriddenIsa(programProduct.getId(), sdp4.getId())).thenReturn(new ISA(0.0, 0, 8.0, 0.0, 0, 0, 0, category.getId()));
        when(facilityProgramProductRepository.getOverriddenIsa(programProduct.getId(), sdp5.getId())).thenReturn(new ISA(0.0, 0, 10.0, 0.0, 0, 0, 0, category.getId()));

        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+dvs1Group.getId()+"}")).thenReturn(getTreeList(Arrays.asList(sdp1.getId(), sdp2.getId())));
        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+dvs2Group.getId()+"}")).thenReturn(getTreeList(Arrays.asList(sdp3.getId())));
        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+dvs3Group.getId()+"}")).thenReturn(getTreeList(Arrays.asList(sdp4.getId(), sdp5.getId())));
        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+rvs1Group.getId()+"}")).thenReturn(getTreeList(Arrays.asList(dvs1.getId(), dvs2.getId())));
        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+rvs2Group.getId()+"}")).thenReturn(getTreeList(Arrays.asList(dvs3.getId())));
        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+cvsGroup.getId()+"}")).thenReturn(getTreeList(Arrays.asList(rvs1.getId(), rvs2.getId())));
    }



    private Facility createAndRegisterFacility(String facilityTypeCode, boolean isSdp, Long id, String facilityName)
    {
        Facility facility = new Facility();
        facility.setSdp(isSdp);
        facility.setName(facilityName);

        GeographicZone geoZone = new GeographicZone();
        geoZone.setId(id);
        facility.setGeographicZone(geoZone);

        FacilityType type = new FacilityType(facilityTypeCode);
        facility.setFacilityType(type);

        facility.setCatchmentPopulation(id * 900);

        if (id != null) {
            facility.setId(id);
        }

        when(facilityService.getById(id)).thenReturn(facility);

        return facility;
    }


    private SupervisoryNode createSupervisoryNode(Facility facility, Long id)
    {
        SupervisoryNode supervisoryNode = new SupervisoryNode();
        supervisoryNode.setFacility(facility);
        supervisoryNode.setId(id);
        return supervisoryNode;
    }

    private RequisitionGroup createRequisitionGroup(Long id)
    {
        RequisitionGroup requisitionGroup = new RequisitionGroup();
        requisitionGroup.setId(id);
        return requisitionGroup;
    }



    private List<FacilityLevelTree> getTreeList(List<Long> facilityIds)
    {
        List<FacilityLevelTree> facilityTreeList = new ArrayList<FacilityLevelTree>();
        for(Long facilityId: facilityIds)
        {
            FacilityLevelTree tree = new FacilityLevelTree();
            tree.setFacilityId(facilityId);
            facilityTreeList.add(tree);
        }
        return facilityTreeList;
    }

}
