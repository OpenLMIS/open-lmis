package org.openlmis.core.upload;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.FacilityProgramProductISADTO;
import org.openlmis.core.repository.*;
import org.openlmis.db.categories.UnitTests;

import java.util.Arrays;
import java.util.Collections;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityProgramProductISAHandlerTest {

    private static Logger logger = Logger.getLogger(FacilityProgramProductISAHandlerTest.class);

    @Mock
    FacilityProgramProductRepository repository;

    @Mock
    FacilityRepository facilityRepository;

    @Mock
    ProgramProductRepository programProductRepository;

    @Mock
    ProgramRepository programRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    SupervisoryNodeRepository supervisoryNodeRepository;

    @Mock
    RequisitionGroupRepository requisitionGroupRepository;

    @Mock
    RequisitionGroupMemberRepository requisitionGroupMemberRepository;

    @InjectMocks
    FacilityProgramProductISAHandler handler;

    Facility facility;
    Program program;
    Product product;
    ProgramProduct programProduct;
    FacilityProgramProductISADTO fppISA;
    Double whoRatio;
    Integer dosesPerYear;
    Double wastageFactor;
    Double bufferPercentage;
    Integer adjustmentValue;

    Facility sdp1;
    Facility dvs1;
    Facility rvs1;
    Facility cvs;

    @Before
    public void setup() {
        facility = make(a(FacilityBuilder.defaultFacility));
        program = make(a(ProgramBuilder.defaultProgram));
        product = make(a(ProductBuilder.defaultProduct));
        programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));

        whoRatio = 0.0;
        dosesPerYear = 0;
        wastageFactor = 7.0;
        bufferPercentage = 0.0;
        adjustmentValue = 0;

        fppISA = new FacilityProgramProductISADTO();
        fppISA.setFacility(facility);
        fppISA.setProgram(program);
        fppISA.setProduct(product);
        fppISA.setWhoRatio(whoRatio);
        fppISA.setDosesPerYear(dosesPerYear);
        fppISA.setWastageFactor(wastageFactor);
        fppISA.setBufferPercentage(bufferPercentage);
        fppISA.setAdjustmentValue(adjustmentValue);
    }

    private void setupFacilityHierarchy() {
        cvs = createFacility("cvs", false, 1L);
        SupervisoryNode cvsNode = createSupervisoryNode(cvs, 1L);

        rvs1 = createFacility("rvs", false, 2L);
        Facility rvs2 = createFacility("rvs", false, 3L);
        RequisitionGroup cvsGroup = createRequisitionGroup(12L);
        RequisitionGroupMember rvs1Member = new RequisitionGroupMember(cvsGroup, rvs1);
        RequisitionGroupMember rvs2Member = new RequisitionGroupMember(cvsGroup, rvs2);
        SupervisoryNode rvs1Node = createSupervisoryNode(rvs1, 2L);
        SupervisoryNode rvs2Node = createSupervisoryNode(rvs2, 3L);

        dvs1 = createFacility("dvs", false, 4L);
        Facility dvs2 = createFacility("dvs", false, 5L);
        RequisitionGroup rvs1Group = createRequisitionGroup(13L);
        RequisitionGroupMember dvs1Member = new RequisitionGroupMember(rvs1Group, dvs1);
        RequisitionGroupMember dvs2Member = new RequisitionGroupMember(rvs1Group, dvs2);
        SupervisoryNode dvs1Node = createSupervisoryNode(dvs1, 4L);
        SupervisoryNode dvs2Node = createSupervisoryNode(dvs2, 5L);

        Facility dvs3 = createFacility("dvs", false, 6L);
        RequisitionGroup rvs2Group = createRequisitionGroup(14L);
        RequisitionGroupMember dvs3Member = new RequisitionGroupMember(rvs2Group, dvs3);
        SupervisoryNode dvs3Node = createSupervisoryNode(dvs3, 6L);

        sdp1 = createFacility("sdp", true, 7L);
        Facility sdp2 = createFacility("sdp", true, 8L);
        RequisitionGroup dvs1Group = createRequisitionGroup(15L);
        RequisitionGroupMember sdp1Member = new RequisitionGroupMember(dvs1Group, sdp1);
        RequisitionGroupMember sdp2Member = new RequisitionGroupMember(dvs1Group, sdp2);

        Facility sdp3 = createFacility("sdp", true, 9L);
        RequisitionGroup dvs2Group = createRequisitionGroup(16L);
        RequisitionGroupMember sdp3Member = new RequisitionGroupMember(dvs2Group, sdp3);

        Facility sdp4 = createFacility("sdp", true, 10L);
        Facility sdp5 = createFacility("sdp", true, 11L);
        RequisitionGroup dvs3Group = createRequisitionGroup(17L);
        RequisitionGroupMember sdp4Member = new RequisitionGroupMember(dvs3Group, sdp4);
        RequisitionGroupMember sdp5Member = new RequisitionGroupMember(dvs3Group, sdp5);

        when(supervisoryNodeRepository.getFor(sdp1, program)).thenReturn(dvs1Node);
        when(supervisoryNodeRepository.getAllParentSupervisoryNodesInHierarchy(dvs1Node)).thenReturn(Arrays.asList(dvs1Node, rvs1Node, cvsNode));
        when(supervisoryNodeRepository.getAllChildSupervisoryNodesInHierarchy(dvs1Node)).thenReturn(Collections.singletonList(dvs1Node));
        when(supervisoryNodeRepository.getAllChildSupervisoryNodesInHierarchy(rvs1Node)).thenReturn(Arrays.asList(rvs1Node, dvs1Node, dvs2Node));
        when(supervisoryNodeRepository.getAllChildSupervisoryNodesInHierarchy(cvsNode)).thenReturn(Arrays.asList(cvsNode, rvs1Node, rvs2Node, dvs1Node, dvs2Node, dvs3Node));
        when(requisitionGroupRepository.getRequisitionGroups(Collections.singletonList(dvs1Node))).thenReturn(Collections.singletonList(dvs1Group));
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
        when(repository.getOverriddenIsa(programProduct.getId(), sdp1.getId())).thenReturn(new ISA(0.0, 0, 7.0, 0.0, 0, 0, 0));
        when(repository.getOverriddenIsa(programProduct.getId(), sdp2.getId())).thenReturn(new ISA(0.0, 0, 9.0, 0.0, 0, 0, 0));
        when(repository.getOverriddenIsa(programProduct.getId(), sdp3.getId())).thenReturn(new ISA(0.0, 0, 9.5, 0.0, 0, 0, 0));
        when(repository.getOverriddenIsa(programProduct.getId(), sdp4.getId())).thenReturn(new ISA(0.0, 0, 8.0, 0.0, 0, 0, 0));
        when(repository.getOverriddenIsa(programProduct.getId(), sdp5.getId())).thenReturn(new ISA(0.0, 0, 10.0, 0.0, 0, 0, 0));
    }

    private Facility createFacility(String facilityTypeCode, boolean isSdp, Long id) {
        FacilityType type = new FacilityType(facilityTypeCode);
        Facility facility = new Facility();
        facility.setFacilityType(type);
        facility.setSdp(isSdp);
        if (id != null) {
            facility.setId(id);
        }
        return facility;
    }

    private SupervisoryNode createSupervisoryNode(Facility facility, Long id) {
        SupervisoryNode supervisoryNode = new SupervisoryNode();
        supervisoryNode.setFacility(facility);
        supervisoryNode.setId(id);
        return supervisoryNode;
    }

    private RequisitionGroup createRequisitionGroup(Long id) {
        RequisitionGroup requisitionGroup = new RequisitionGroup();
        requisitionGroup.setId(id);
        return requisitionGroup;
    }

    @Test
    public void shouldGetFacilityProgramProductISA() {
        FacilityProgramProduct expectedFpp = new FacilityProgramProduct();

        when(repository.getByCodes(any(String.class), any(String.class), any(String.class))).thenReturn(expectedFpp);

        BaseModel returnedFpp = handler.getExisting(fppISA);

        assertThat((FacilityProgramProduct)returnedFpp, is(expectedFpp));
    }

    @Test
    public void shouldSaveFacilityProgramProductISA() {
        when(facilityRepository.getByCode(any(String.class))).thenReturn(facility);
        when(programProductRepository.getByProgramAndProductCode(any(ProgramProduct.class))).thenReturn(programProduct);
        when(programRepository.getByCode(any(String.class))).thenReturn(program);
        when(supervisoryNodeRepository.getFor(any(Facility.class), any(Program.class))).thenReturn(null);

        ISA isa = new ISA();
        isa.setWhoRatio(whoRatio);
        isa.setDosesPerYear(dosesPerYear);
        isa.setWastageFactor(wastageFactor);
        isa.setBufferPercentage(bufferPercentage);
        isa.setAdjustmentValue(adjustmentValue);

        ProgramProductISA ppISA = new ProgramProductISA(programProduct.getId(), isa);

        FacilityProgramProduct fpp = new FacilityProgramProduct();
        fpp.setFacilityId(facility.getId());
        fpp.setId(programProduct.getId());
        fpp.setProgramProductIsa(ppISA);
        fpp.setOverriddenIsa(isa);

        handler.save(fppISA);

        verify(repository).save(fpp);
    }

    @Test
    public void shouldSaveFacilityProgramProductISAAndCalculateParents() {
        setupFacilityHierarchy();

        when(facilityRepository.getByCode(any(String.class))).thenReturn(sdp1);
        when(programProductRepository.getByProgramAndProductCode(any(ProgramProduct.class))).thenReturn(programProduct);
        when(programRepository.getByCode(any(String.class))).thenReturn(program);

        ISA isa = new ISA();
        isa.setWhoRatio(whoRatio);
        isa.setDosesPerYear(dosesPerYear);
        isa.setWastageFactor(wastageFactor);
        isa.setBufferPercentage(bufferPercentage);
        isa.setAdjustmentValue(adjustmentValue);

        ProgramProductISA ppISA = new ProgramProductISA(programProduct.getId(), isa);

        FacilityProgramProduct fpp = new FacilityProgramProduct();
        fpp.setFacilityId(sdp1.getId());
        fpp.setId(programProduct.getId());
        fpp.setProgramProductIsa(ppISA);
        fpp.setOverriddenIsa(isa);

        handler.save(fppISA);

        verify(repository).save(fpp);

        isa.setWastageFactor(8.0);
        fpp.setFacilityId(dvs1.getId());
        verify(repository).save(fpp);

        isa.setWastageFactor(8.5);
        fpp.setFacilityId(rvs1.getId());
        verify(repository).save(fpp);

        isa.setWastageFactor(8.7);
        fpp.setFacilityId(cvs.getId());
        verify(repository).save(fpp);
    }
}
