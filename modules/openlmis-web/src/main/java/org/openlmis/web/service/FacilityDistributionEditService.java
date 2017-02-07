package org.openlmis.web.service;

import org.apache.commons.beanutils.PropertyUtils;
import org.openlmis.distribution.domain.AdultCoverageLineItem;
import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.EpiInventoryLineItem;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.domain.Facilitator;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.MotorbikeProblems;
import org.openlmis.distribution.domain.OpenedVialLineItem;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.domain.VaccinationFullCoverage;
import org.openlmis.distribution.service.DistributionRefrigeratorsService;
import org.openlmis.distribution.service.EpiInventoryService;
import org.openlmis.distribution.service.EpiUseService;
import org.openlmis.distribution.service.FacilityVisitService;
import org.openlmis.distribution.service.VaccinationCoverageService;
import org.openlmis.web.util.FacilityDistributionEditDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacilityDistributionEditService {

  @Autowired
  private FacilityVisitService facilityVisitService;

  @Autowired
  private EpiInventoryService epiInventoryService;

  @Autowired
  private DistributionRefrigeratorsService distributionRefrigeratorsService;

  @Autowired
  private EpiUseService epiUseService;

  @Autowired
  private VaccinationCoverageService vaccinationCoverageService;

  public void save(FacilityDistributionEditDetail detail) {
    Object db = getObject(detail);

    try {
      PropertyUtils.setProperty(db, detail.getEditedItem(), detail.getNewValue());
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }

    saveObject(detail, db);
  }

  private Object getObject(FacilityDistributionEditDetail detail) {
    String dataScreen = detail.getDataScreen();
    Long dataScreenId = detail.getDataScreenId();
    Object db;

    switch (dataScreen) {
      case "FacilityVisit":
        db = facilityVisitService.getById(dataScreenId);
        break;
      case "Facilitator":
      case "MotorbikeProblems":
        db = facilityVisitService.getById(detail.getParentDataScreenId());
        if (db != null) {
          FacilityVisit visit = (FacilityVisit) db;
          boolean facilitator = false;
          boolean motorbikeProblems = false;

          switch (detail.getParentProperty()) {
            case "confirmedBy":
              db = visit.getConfirmedBy();
              facilitator = true;
              break;
            case "verifiedBy":
              db = visit.getVerifiedBy();
              facilitator = true;
              break;
            case "motorbikeProblems":
              db = visit.getMotorbikeProblems();
              motorbikeProblems = true;
              break;
            default:
              throw new IllegalStateException("Unknown property in FacilityVisit: " + detail.getParentProperty());
          }

          if (null == db && facilitator) {
            db = new Facilitator();
          }

          if (null == db && motorbikeProblems) {
            db = new MotorbikeProblems();
          }
        } else {
          throw new IllegalStateException("Can't find facility visit with id: " + detail.getParentDataScreenId());
        }
        break;
      case "EpiInventoryLineItem":
        db = epiInventoryService.getById(dataScreenId);
        break;
      case "RefrigeratorReading":
        db = distributionRefrigeratorsService.getReading(dataScreenId);
        break;
      case "RefrigeratorProblem":
        db = distributionRefrigeratorsService.getProblem(dataScreenId);
        break;
      case "EpiUseLineItem":
        db = epiUseService.getLineById(dataScreenId);
        break;
      case "VaccinationFullCoverage":
        db = vaccinationCoverageService.getFullCoverageById(dataScreenId);
        break;
      case "ChildCoverageLineItem":
        db = vaccinationCoverageService.getChildCoverageLineItem(dataScreenId);
        break;
      case "OpenedVialLineItem":
        switch (detail.getParentDataScreen()) {
          case "VaccinationAdultCoverage":
            db = vaccinationCoverageService.getAdultCoverageOpenedVialLineItem(dataScreenId);
            break;
          case "VaccinationChildCoverage":
            db = vaccinationCoverageService.getChildCoverageOpenedVialLineItem(dataScreenId);
            break;
          default:
            throw new IllegalStateException("Unknown data screen: " + dataScreen);
        }
        break;
      case "AdultCoverageLineItem":
        db = vaccinationCoverageService.getAdultCoverageLineItem(dataScreenId);
        break;
      default:
        throw new IllegalStateException("Unknown data screen: " + dataScreen);
    }

    return db;
  }

  private void saveObject(FacilityDistributionEditDetail detail, Object db) {
    if (db instanceof FacilityVisit) {
      facilityVisitService.save((FacilityVisit) db);
    } else if (db instanceof Facilitator) {
      Facilitator facilitator = (Facilitator) db;
      FacilityVisit visit = facilityVisitService.getById(detail.getParentDataScreenId());

      if (visit != null) {
        switch (detail.getParentProperty()) {
          case "confirmedBy":
            visit.setConfirmedBy(facilitator);
            break;
          case "verifiedBy":
            visit.setVerifiedBy(facilitator);
            break;
          default:
            throw new IllegalStateException("Unknown property in FacilityVisit: " + detail.getParentProperty());
        }

        facilityVisitService.save(visit);
      } else {
        throw new IllegalStateException("Can't find facility visit with id: " + detail.getParentDataScreenId());
      }
    } else if (db instanceof MotorbikeProblems) {
      MotorbikeProblems motorbikeProblems = (MotorbikeProblems) db;
      FacilityVisit visit = facilityVisitService.getById(detail.getParentDataScreenId());

      if (visit != null) {
        visit.setMotorbikeProblems(motorbikeProblems);
        facilityVisitService.save(visit);
      } else {
        throw new IllegalStateException("Can't find facility visit with id: " + detail.getParentDataScreenId());
      }
    } else if (db instanceof EpiInventoryLineItem) {
      epiInventoryService.save((EpiInventoryLineItem) db);
    } else if (db instanceof RefrigeratorReading) {
      distributionRefrigeratorsService.saveReading((RefrigeratorReading) db);
    } else if (db instanceof RefrigeratorProblem) {
      distributionRefrigeratorsService.saveProblem((RefrigeratorProblem) db);
    } else if (db instanceof EpiUseLineItem) {
      epiUseService.saveLine((EpiUseLineItem) db);
    } else if (db instanceof VaccinationFullCoverage) {
      vaccinationCoverageService.updateFullCoverage((VaccinationFullCoverage) db);
    } else if (db instanceof ChildCoverageLineItem) {
      vaccinationCoverageService.updateChildCoverageLineItem((ChildCoverageLineItem) db);
    } else if (db instanceof AdultCoverageLineItem) {
      vaccinationCoverageService.updateAdultCoverageLineItem((AdultCoverageLineItem) db);
    } else if (db instanceof OpenedVialLineItem) {
      if (detail.getParentDataScreen().equals("VaccinationAdultCoverage")) {
        vaccinationCoverageService.updateAdultCoverageOpenedVialLineItem((OpenedVialLineItem) db);
      } else if (detail.getParentDataScreen().equals("VaccinationChildCoverage")) {
        vaccinationCoverageService.updateChildCoverageOpenedVialLineItem((OpenedVialLineItem) db);
      }
    } else {
      throw new IllegalStateException("Unknown data screen: " + detail.getDataScreen());
    }
  }

}
