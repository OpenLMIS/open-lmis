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


package org.openlmis.demographics.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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
import org.openlmis.demographics.repository.AnnualDistrictEstimateRepository;
import org.openlmis.report.mapper.lookup.FacilityLevelMapper;
import org.openlmis.report.model.dto.FacilityLevelTree;

import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class StockRequirementsServiceTest
{
    @Mock
    FacilityProgramProductRepository facilityProgramProductRepository;

    @Mock
    FacilityRepository facilityRepository;

    @Mock
    FacilityService facilityService;

    @Mock
    FacilityLevelMapper levelMapper;

    @Mock
    SupervisoryNodeRepository supervisoryNodeRepository;

    @Mock
    RequisitionGroupRepository requisitionGroupRepository;

    @Mock
    RequisitionGroupMemberRepository requisitionGroupMemberRepository;

    @Mock
    AnnualFacilityDemographicEstimateService annualFacilityDemographicEstimateService;

    @Mock
    private AnnualDistrictEstimateRepository annualDistrictEstimateRepository;

    @InjectMocks
    private StockRequirementsService stockRequirementsService;


    Program program;
    ProgramProduct programProduct;

    Facility cvs;
    Facility rvs1, rvs2;
    Facility dvs1, dvs2, dvs3;
    Facility sdp1, sdp2, sdp3, sdp4, sdp5;

    static final Integer populationSourceId_1 = 1;
    static final Integer populationSourceId_2 = 2;

    @Before
    public void setup()
    {
        program = make(a(ProgramBuilder.defaultProgram));
        programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));

        setupFacilityHierarchy();
        setupDemographyEstimates();
    }

    private Facility createAndRegisterFacility(String facilityTypeCode, boolean isSdp, Long id)
    {
        Facility facility = new Facility();
        facility.setSdp(isSdp);

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

    private void setupFacilityHierarchy()
    {
        cvs = createAndRegisterFacility("cvs", false, 1L);
        SupervisoryNode cvsNode = createSupervisoryNode(cvs, 1L);

        rvs1 = createAndRegisterFacility("rvs", false, 2L);
        rvs2 = createAndRegisterFacility("rvs", false, 3L);
        RequisitionGroup cvsGroup = createRequisitionGroup(12L);
        RequisitionGroupMember rvs1Member = new RequisitionGroupMember(cvsGroup, rvs1);
        RequisitionGroupMember rvs2Member = new RequisitionGroupMember(cvsGroup, rvs2);
        SupervisoryNode rvs1Node = createSupervisoryNode(rvs1, 2L);
        SupervisoryNode rvs2Node = createSupervisoryNode(rvs2, 3L);

        dvs1 = createAndRegisterFacility("dvs", false, 4L);
        dvs2 = createAndRegisterFacility("dvs", false, 5L);
        RequisitionGroup rvs1Group = createRequisitionGroup(13L);
        RequisitionGroupMember dvs1Member = new RequisitionGroupMember(rvs1Group, dvs1);
        RequisitionGroupMember dvs2Member = new RequisitionGroupMember(rvs1Group, dvs2);
        SupervisoryNode dvs1Node = createSupervisoryNode(dvs1, 4L);
        SupervisoryNode dvs2Node = createSupervisoryNode(dvs2, 5L);

        dvs3 = createAndRegisterFacility("dvs", false, 6L);
        RequisitionGroup rvs2Group = createRequisitionGroup(14L);
        RequisitionGroupMember dvs3Member = new RequisitionGroupMember(rvs2Group, dvs3);
        SupervisoryNode dvs3Node = createSupervisoryNode(dvs3, 6L);

        sdp1 = createAndRegisterFacility("heac", true, 7L);
        sdp2 = createAndRegisterFacility("heac", true, 8L);
        RequisitionGroup dvs1Group = createRequisitionGroup(15L);
        RequisitionGroupMember sdp1Member = new RequisitionGroupMember(dvs1Group, sdp1);
        RequisitionGroupMember sdp2Member = new RequisitionGroupMember(dvs1Group, sdp2);

        sdp3 = createAndRegisterFacility("heac", true, 9L);
        RequisitionGroup dvs2Group = createRequisitionGroup(16L);
        RequisitionGroupMember sdp3Member = new RequisitionGroupMember(dvs2Group, sdp3);

        sdp4 = createAndRegisterFacility("heac", true, 10L);
        sdp5 = createAndRegisterFacility("heac", true, 11L);
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
        when(facilityProgramProductRepository.getOverriddenIsa(programProduct.getId(), sdp1.getId())).thenReturn(new ISA(0.0, 0, 0.0, 0.0, 0, 0, 0, 0));
        when(facilityProgramProductRepository.getOverriddenIsa(programProduct.getId(), sdp2.getId())).thenReturn(new ISA(0.0, 0, 0.0, 0.0, 0, 0, 0, 0));
        when(facilityProgramProductRepository.getOverriddenIsa(programProduct.getId(), sdp3.getId())).thenReturn(new ISA(0.0, 0, 0.0, 0.0, 0, 0, 0, 0));
        when(facilityProgramProductRepository.getOverriddenIsa(programProduct.getId(), sdp4.getId())).thenReturn(new ISA(0.0, 0, 0.0, 0.0, 0, 0, 0, 0));
        when(facilityProgramProductRepository.getOverriddenIsa(programProduct.getId(), sdp5.getId())).thenReturn(new ISA(0.0, 0, 0.0, 0.0, 0, 0, 0, 0));

        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+dvs1Group.getId()+"}")).thenReturn(getTreeList(Arrays.asList(sdp1.getId(), sdp2.getId())));
        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+dvs2Group.getId()+"}")).thenReturn(getTreeList(Arrays.asList(sdp3.getId())));
        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+dvs3Group.getId()+"}")).thenReturn(getTreeList(Arrays.asList(sdp4.getId(), sdp5.getId())));
        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+rvs1Group.getId()+"}")).thenReturn(getTreeList(Arrays.asList(dvs1.getId(), dvs2.getId())));
        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+rvs2Group.getId()+"}")).thenReturn(getTreeList(Arrays.asList(dvs3.getId())));
        when(levelMapper.getFacilitiesByLevel(program.getId(), "{"+cvsGroup.getId()+"}")).thenReturn(getTreeList(Arrays.asList(rvs1.getId(), rvs2.getId())));
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

    private List<AnnualFacilityEstimateEntry> getFacilityDemographyEstimates(Long facilityId, Long programId, Integer year)
    {
        //Create an estimate for the specified facility, program, and year
        AnnualFacilityEstimateEntry entry1 = new AnnualFacilityEstimateEntry();
        entry1.setFacilityId(facilityId);
        entry1.setProgramId(programId);
        entry1.setYear(year);

        //Set the EstimateId and Value to correlated numbers
        entry1.setDemographicEstimateId(new Long(populationSourceId_1));
        entry1.setValue(facilityId * populationSourceId_1 * 100);

        //Create another estimate for the specified facility, program, and year
        AnnualFacilityEstimateEntry entry2 = new AnnualFacilityEstimateEntry();
        entry2.setFacilityId(facilityId);
        entry2.setProgramId(programId);
        entry2.setYear(year);

        //Set the EstimateId and Value to correlated numbers
        entry2.setDemographicEstimateId(new Long(populationSourceId_2));
        entry2.setValue(facilityId * populationSourceId_2 * 100);

        //Return the estimate2
        List<AnnualFacilityEstimateEntry> estimates = new LinkedList<>();
        estimates.add(entry1);
        estimates.add(entry2);
        return estimates;
    }

    private AnnualDistrictEstimateEntry getDistrictDemographyEstimate(Integer year, Long districtId, Long programId, Long categoryId)
    {
        //Create an estimate for the specified year, districtId, programId, and categoryId
        AnnualDistrictEstimateEntry estimateEntry = new AnnualDistrictEstimateEntry();
        estimateEntry.setYear(year);
        estimateEntry.setDistrictId(districtId);
        estimateEntry.setProgramId(programId);
        estimateEntry.setDemographicEstimateId(categoryId);

        //Set the value to a number correlated with the facilityId
        estimateEntry.setValue(districtId * categoryId * 100);

        return  estimateEntry;
    }

    //Note that Facility here is meant to refer to an SDP
    private void setupFacilityDemographyEstimate(Long facilityId, Long programId, Integer year)
    {
        when(annualFacilityDemographicEstimateService.getEstimateValuesForFacility(facilityId, programId, year)).thenReturn(getFacilityDemographyEstimates(facilityId, programId, year));
    }


    private void setupDistrictDemographyEstimate(Integer year, Long districtAndGeoZoneId, Long programId, Long categoryId)
    {
        when(annualDistrictEstimateRepository.getEntryBy(year, districtAndGeoZoneId, programId, categoryId)).thenReturn(getDistrictDemographyEstimate(year, districtAndGeoZoneId, programId, categoryId));
    }

    private void setupDemographyEstimates()
    {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        setupFacilityDemographyEstimate(sdp1.getId(), program.getId(), currentYear);
        setupFacilityDemographyEstimate(sdp2.getId(), program.getId(), currentYear);
        setupFacilityDemographyEstimate(sdp3.getId(), program.getId(), currentYear);
        setupFacilityDemographyEstimate(sdp4.getId(), program.getId(), currentYear);
        setupFacilityDemographyEstimate(sdp5.getId(), program.getId(), currentYear);

        setupDistrictDemographyEstimate(currentYear, dvs1.getId(), program.getId(), new Long(populationSourceId_1));
        setupDistrictDemographyEstimate(currentYear, dvs1.getId(), program.getId(), new Long(populationSourceId_2));

        setupDistrictDemographyEstimate(currentYear, dvs2.getId(), program.getId(), new Long(populationSourceId_1));
        setupDistrictDemographyEstimate(currentYear, dvs2.getId(), program.getId(), new Long(populationSourceId_2));

        setupDistrictDemographyEstimate(currentYear, dvs3.getId(), program.getId(), new Long(populationSourceId_1));
        setupDistrictDemographyEstimate(currentYear, dvs3.getId(), program.getId(), new Long(populationSourceId_2));
    }

    @Test
    public void shouldUseSpecifiedPopulationSourceForServiceDeliveryPoint()
    {
        Long returnedPopulation = stockRequirementsService.getPopulation(sdp1, program, populationSourceId_1);
        Long expectedPopulation = sdp1.getId() * populationSourceId_1 * 100;
        assertThat(returnedPopulation, is(expectedPopulation));

        returnedPopulation = stockRequirementsService.getPopulation(sdp1, program, populationSourceId_2);
        expectedPopulation = sdp1.getId() * populationSourceId_2 * 100;
        assertThat(returnedPopulation, is(expectedPopulation));
    }

    @Test
    public void shouldUseSpecifiedPopulationSourceForDistrictVaccineStore()
    {
        Long returnedPopulation = stockRequirementsService.getPopulation(dvs1, program, populationSourceId_1);
        Long expectedPopulation = dvs1.getId() * populationSourceId_1 * 100;
        assertThat(returnedPopulation, is(expectedPopulation));

        returnedPopulation = stockRequirementsService.getPopulation(dvs1, program, populationSourceId_2);
        expectedPopulation = dvs1.getId() * populationSourceId_2 * 100;
        assertThat(returnedPopulation, is(expectedPopulation));
    }

    @Test
    public void shouldUseFacilityCatchmentPopulationForServiceDeliveryPointWhenPopulationSourceIsNull()
    {
        Long returnedPopulation = stockRequirementsService.getPopulation(sdp1, program, null);
        Long expectedPopulation = sdp1.getId() * 900;
        assertThat(returnedPopulation, is(expectedPopulation));
    }

    @Test
    public void shouldUseFacilityCatchmentPopulationForDistrictVaccineStoreWhenPopulationSourceIsNull()
    {
        Long returnedPopulation = stockRequirementsService.getPopulation(dvs1, program, null);
        Long expectedPopulation = dvs1.getId() * 900;
        assertThat(returnedPopulation, is(expectedPopulation));
    }


    @Test
    public void shouldDerivePopulationForCentralVaccineStoreFromChildFacilityPopulations()
    {
        Long returnedPopulation = stockRequirementsService.getPopulation(cvs, program, populationSourceId_1);

        Long sdp1Population = sdp1.getId() * populationSourceId_1 * 100;
        Long sdp2Population = sdp2.getId() * populationSourceId_1 * 100;
        Long sdp3Population = sdp3.getId() * populationSourceId_1 * 100;
        Long sdp4Population = sdp4.getId() * populationSourceId_1 * 100;
        Long sdp5Population = sdp5.getId() * populationSourceId_1 * 100;

        Long dvs1Population = dvs1.getId() * populationSourceId_1 * 100;
        Long dvs2Population = dvs2.getId() * populationSourceId_1 * 100;
        Long dvs3Population = dvs3.getId() * populationSourceId_1 * 100;

        Long rvs1Population = dvs1Population + dvs2Population;
        Long rvs2Population = dvs3Population;
        Long expectedPopulation = rvs1Population + rvs2Population;

        assertThat(returnedPopulation, is(expectedPopulation));
    }

    @Test
    public void shouldDerivePopulationForRegionalVaccineStoreFromChildFacilityPopulations()
    {
        Long returnedPopulation = stockRequirementsService.getPopulation(rvs1, program, populationSourceId_1);

        Long dvs1Population = dvs1.getId() * populationSourceId_1 * 100;
        Long dvs2Population = dvs2.getId() * populationSourceId_1 * 100;
        Long expectedPopulation = dvs1Population + dvs2Population;

        assertThat(returnedPopulation, is(expectedPopulation));
    }

}