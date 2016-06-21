package org.openlmis.web.service;

import org.apache.commons.beanutils.PropertyUtils;
import org.openlmis.distribution.domain.AdultCoverageLineItem;
import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.EpiInventoryLineItem;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.OpenedVialLineItem;
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

import java.lang.reflect.InvocationTargetException;

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
    Object db = getObject(detail.getParentDataScreen(), detail.getDataScreenId(), detail.getDataScreen());

    try {
      PropertyUtils.setProperty(db, detail.getEditedItem(), detail.getNewValue());
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }

    saveObject(detail.getParentDataScreen(), detail.getDataScreen(), db);
  }

  private Object getObject(String parentDataScreen, Long dataScreenId, String dataScreen) {
    Object db;

    switch (dataScreen) {
      case "FacilityVisit":
        db = facilityVisitService.getById(dataScreenId);
        break;
      case "EpiInventoryLineItem":
        db = epiInventoryService.getById(dataScreenId);
        break;
      case "RefrigeratorReading":
        db = distributionRefrigeratorsService.getReading(dataScreenId);
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
        switch (parentDataScreen) {
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

  private void saveObject(String parentDataScreen, String dataScreen, Object db) {
    if (db instanceof FacilityVisit) {
      facilityVisitService.save((FacilityVisit) db);
    } else if (db instanceof EpiInventoryLineItem) {
      epiInventoryService.save((EpiInventoryLineItem) db);
    } else if (db instanceof RefrigeratorReading) {
      distributionRefrigeratorsService.saveReading((RefrigeratorReading) db);
    } else if (db instanceof EpiUseLineItem) {
      epiUseService.saveLine((EpiUseLineItem) db);
    } else if (db instanceof VaccinationFullCoverage) {
      vaccinationCoverageService.updateFullCoverage((VaccinationFullCoverage) db);
    } else if (db instanceof ChildCoverageLineItem) {
      vaccinationCoverageService.updateChildCoverageLineItem((ChildCoverageLineItem) db);
    } else if (db instanceof AdultCoverageLineItem) {
      vaccinationCoverageService.updateAdultCoverageLineItem((AdultCoverageLineItem) db);
    } else if (db instanceof OpenedVialLineItem) {
      if (parentDataScreen.equals("VaccinationAdultCoverage")) {
        vaccinationCoverageService.updateAdultCoverageOpenedVialLineItem((OpenedVialLineItem) db);
      } else if (parentDataScreen.equals("VaccinationChildCoverage")) {
        vaccinationCoverageService.updateChildCoverageOpenedVialLineItem((OpenedVialLineItem) db);
      }
    } else {
      throw new IllegalStateException("Unknown data screen: " + dataScreen);
    }
  }

}
