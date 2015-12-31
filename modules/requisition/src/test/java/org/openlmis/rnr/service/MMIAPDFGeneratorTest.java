package org.openlmis.rnr.service;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Signature;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.PatientQuantificationLineItem;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.natpryce.makeiteasy.MakeItEasy.*;

public class MMIAPDFGeneratorTest {

    @Test
    public void shouldGenerateMMIAPdf() throws IOException {
        MMIAPDFGenerator mmiapdfGenerator = new MMIAPDFGenerator();
        mmiapdfGenerator.cachePath = "/app/tomcat/openlmis/emailattachment/cache";

        mmiapdfGenerator.generateMMIAPdf(createRnr(), "mmia.pdf");
    }

    public Rnr createRnr() {
        ProcessingPeriod period = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, new LocalDate("2015-11-12").toDate()))
                .but(with(ProcessingPeriodBuilder.endDate, new LocalDate("2015-12-12").toDate())));


        GeographicZone geographicZone = new GeographicZone();
        geographicZone.setName("guanggu");
        GeographicZone parent = new GeographicZone();
        parent.setName("HuBei Province");
        geographicZone.setParent(parent);



        Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.name, "HF2"))
                .but(with(FacilityBuilder.geographicZone, geographicZone)));

        Rnr requisition = make(a(RequisitionBuilder.defaultRequisition, with(RequisitionBuilder.period, period))
                .but(with(RequisitionBuilder.facility, facility)));

        requisition.setFullSupplyLineItems(Arrays.asList(
                generateRnrLineItem("08S42", "Zidovudina/Lamivudina/Nevirapi; 300mg+150mg+200mg 60Comp; Embalagem", "300mg+150mg+200mg Comp", 10, 0,"26/12/2015"),
                generateRnrLineItem("08S18Y", "Tenofovir/Lamivudina/Efavirenz; 300mg + 300mg + 600mg 30Comp; Embalagem", "300mg+150mg+200mg Comp", 20, 100, "1/12/2015"),
                generateRnrLineItem("08S40", "Zidovudina/Lamivudina; 300mg+150mg 60Comp; Embalagem", "300mg+150mg+200mg Comp", 30, 100, "26/12/2015"),
                generateRnrLineItem("08S36", "Estavudina/Lamivudina/Nevirapi; 200mg+150mg+30mg 60Comp; Embalagem", "300mg+150mg+200mg Comp", 40, 100, "2/12/2015"),
                generateRnrLineItem("08S32", "Estavudina/Lamivudina; 30mg +150mg 60Comp; Embalagem", "300mg+150mg+200mg Comp", 50, 100, "3/12/2015"),
                generateRnrLineItem("08S18Z", "Tenofovir/Lamivudina; 300mg+300mg 30Comp; Embalagem", "300mg+150mg+200mg Comp", 60, 100, "4/12/2015"),
                generateRnrLineItem("08S39Z", "Lopinavir/Ritonavir; 200mg+50mg 120Comp; Embalagem", "300mg+150mg+200mg Comp", 70, 100, "5/12/2015"),
                generateRnrLineItem("08S21", "Efavirenz (EFV); 600mg 30Comp; Embalagem", "300mg+150mg+200mg Comp", 80, 100, "26/2/2015"),
                generateRnrLineItem("08S01", "Abacavir sulfato (ABC) 300mg,60comp Embalagem", "300mg+150mg+200mg Comp", 90, 100, "26/3/2015"),
                generateRnrLineItem("08S22", "Nevirapina (NVP); 200mg 60Comp; Embalagem", "300mg+150mg+200mg Comp", 100, 100, "21/4/2015"),
                generateRnrLineItem("08S13", "Lamivudina(3TC); 150mg 60Comp; Embalagem", "300mg+150mg+200mg Comp", 110, 100, "1/12/2015"),
                generateRnrLineItem("08S15", "Zidovudina (AZT); 300mg 60Comp; Embalagem", "300mg+150mg+200mg Comp", 120, 100, "1/12/2015"),

                generateRnrLineItem("08S34B", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 10, 100, "11/11/2015"),
                generateRnrLineItem("08S32Z", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 20, 100, "22/12/2015"),
                generateRnrLineItem("08S42B", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 30, 100, "21/12/2015"),
                generateRnrLineItem("08S40Z", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 40, 100, "12/12/2015"),
                generateRnrLineItem("08S39B", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 50, 100, "12/12/2015"),
                generateRnrLineItem("08S39Y", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 60, 100, "21/12/2015"),
                generateRnrLineItem("08S01ZZ", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 70, 100, "26/12/2015"),
                generateRnrLineItem("08S20", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 80, 100, "26/12/2015"),
                generateRnrLineItem("08S19", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 90, 100, "21/1/2015"),
                generateRnrLineItem("08S01B", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 100, 100, "26/12/2015"),

                generateRnrLineItem("08S23", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 10, 100, "11/12/2015"),
                generateRnrLineItem("08S17", "Zidovudina/Lamivudina/Nevirapi", "300mg+150mg+200mg Comp", 20, 100, "21/12/2015")
        ));

        requisition.setRegimenLineItems(Arrays.asList(
                generateRegimenLineItem("01","AZT+3TC+NVP",1),
                generateRegimenLineItem("02","AZT+3TC+NVP",2),
                generateRegimenLineItem("03","AZT+3TC+NVP",3),
                generateRegimenLineItem("04","AZT+3TC+NVP",4),
                generateRegimenLineItem("05","ABC+3TC+LPV/r",5),
                generateRegimenLineItem("06","ABC+3TC+LPV/r",6),
                generateRegimenLineItem("07","ABC+3TC+LPV/r",7),
                generateRegimenLineItem("08","ABC+3TC+LPV/r",8),

                generateRegimenLineItem("09","d4T+3TC+NVP(3DFC Baby)",9),
                generateRegimenLineItem("10","d4T+3TC+NVP(3DFC Baby)",10),
                generateRegimenLineItem("11","d4T+3TC+NVP(3DFC Baby)",11),
                generateRegimenLineItem("12","d4T+3TC+NVP(3DFC Baby)",12),
                generateRegimenLineItem("13","d4T+3TC+NVP(3DFC Baby)",13),
                generateRegimenLineItem("14","d4T+3TC+NVP(3DFC Baby)",14),
                generateRegimenLineItem("15","d4T+3TC+NVP(3DFC Baby)",15),
                generateRegimenLineItem("16","d4T+3TC+NVP(3DFC Baby)",16),
                generateRegimenLineItem("17","d4T+3TC+NVP(3DFC Baby)",17),
                generateRegimenLineItem("18","d4T+3TC+NVP(3DFC Baby)",18)
        ));

        requisition.setPatientQuantifications(Arrays.asList(
                generatePatientLineItem("New",10),
                generatePatientLineItem("Maintenance",20),
                generatePatientLineItem("Alteration",30),
                generatePatientLineItem("PTV",40),
                generatePatientLineItem("PPE",50),
                generatePatientLineItem("Total Dispensed",60),
                generatePatientLineItem("Total Patients",210)
        ));

        requisition.setClientSubmittedNotes("This is client submitted notes,balabala! I am the long long long text!!!!!!!");
        ArrayList<Signature> rnrSignatures = new ArrayList<>();
        rnrSignatures.add(new Signature(Signature.Type.SUBMITTER,"Mr.submitter"));
        rnrSignatures.add(new Signature(Signature.Type.APPROVER,"Mr.approver"));
        requisition.setRnrSignatures(rnrSignatures);

        return requisition;
    }

    private PatientQuantificationLineItem generatePatientLineItem(String category, int total) {
        PatientQuantificationLineItem patientQuantificationLineItem = new PatientQuantificationLineItem();
        patientQuantificationLineItem.setCategory(category);
        patientQuantificationLineItem.setTotal(total);
        return patientQuantificationLineItem;
    }


    private RnrLineItem generateRnrLineItem(String productCode, String primaryName, String strength, int beginningBalance, int stockOnHand, String expirationDate) {
        RnrLineItem rnrLineItem = new RnrLineItem();
        rnrLineItem.setProductCode(productCode);
        rnrLineItem.setProductPrimaryName(primaryName);
        rnrLineItem.setProductStrength(strength);
        rnrLineItem.setBeginningBalance(beginningBalance);
        rnrLineItem.setQuantityReceived(0);
        rnrLineItem.setQuantityDispensed(0);
        rnrLineItem.setTotalLossesAndAdjustments(0);
        rnrLineItem.setStockInHand(stockOnHand);
        rnrLineItem.setExpirationDate(expirationDate);
        return rnrLineItem;
    }

    private RegimenLineItem generateRegimenLineItem(String code, String primaryName, int total) {
        RegimenLineItem item = new RegimenLineItem();
        item.setCode(code);
        item.setName(primaryName);
        item.setPatientsOnTreatment(total);
        return item;
    }
}