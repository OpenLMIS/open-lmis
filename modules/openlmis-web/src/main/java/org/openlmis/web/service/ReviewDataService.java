package org.openlmis.web.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.openlmis.authentication.web.PermissionEvaluator;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.DistributionEdit;
import org.openlmis.distribution.domain.DistributionsEditHistory;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.dto.DistributionDTO;
import org.openlmis.distribution.dto.FacilityDistributionDTO;
import org.openlmis.distribution.service.DistributionService;
import org.openlmis.distribution.service.FacilityDistributionService;
import org.openlmis.web.model.ReviewDataFilter;
import org.openlmis.web.model.ReviewDataFilters;
import org.openlmis.web.model.SynchronizedDistribution;
import org.openlmis.web.util.FacilityDistributionEditDetail;
import org.openlmis.web.util.FacilityDistributionEditHandler;
import org.openlmis.web.util.FacilityDistributionEditResults;
import org.openlmis.web.util.SynchronizedDistributionComparators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.openlmis.core.domain.RightName.EDIT_SYNCHRONIZED_DATA;
import static org.openlmis.core.domain.RightName.VIEW_SYNCHRONIZED_DATA;

@Service
public class ReviewDataService {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
  private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
  private static final String[] HEADER = {
      "label.distribution.history.header.district", "label.distribution.history.header.facility",
      "label.distribution.history.header.data.screen", "label.distribution.history.header.edited.item",
      "label.distribution.history.header.old.value", "label.distribution.history.header.new.value",
      "label.distribution.history.header.edited.date", "label.distribution.history.header.edited.by"
  };

  @Autowired
  private ProgramService programService;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private DeliveryZoneService deliveryZoneService;

  @Autowired
  private DistributionService distributionService;

  @Autowired
  private UserService userService;

  @Autowired
  private FacilityDistributionService facilityDistributionService;

  @Autowired
  private PermissionEvaluator permissionEvaluator;

  @Autowired
  private FacilityDistributionEditService facilityDistributionEditService;

  @Autowired
  private MessageService messageService;

  @Value("${eligibility.edit}")
  private Long eligibilityEdit;

  @Value("${distribution.edit.in.progress}")
  private Long distributionEditInProgress;

  public ReviewDataFilters getFilters() {
    List<Program> programs = programService.getAll();
    List<GeographicZone> geographicZones = facilityService.getAllZones();
    List<DeliveryZone> deliveryZones = deliveryZoneService.getAll();

    List<Distribution> distributions = distributionService.getFullSyncedDistributions();
    List<ProcessingPeriod> periods = new ArrayList<>(distributions.size());

    for (Distribution distribution : distributions) {
      periods.add(distribution.getPeriod());
    }

    return new ReviewDataFilters(programs, geographicZones, deliveryZones, periods);
  }

  public List<SynchronizedDistribution> get(ReviewDataFilter filter, Long userId) {
    List<Distribution> distributions = distributionService.getFullSyncedDistributions(filter.getProgram(), filter.getDeliveryZone(), filter.getPeriod());
    List<SynchronizedDistribution> list = new ArrayList<>();

    for (Distribution distribution : distributions) {
      Map<Long, FacilityDistribution> facilityDistributionMap = facilityDistributionService.getData(distribution);
      Iterator<Map.Entry<Long, FacilityDistribution>> iterator = facilityDistributionMap.entrySet().iterator();

      if (!iterator.hasNext()) {
        continue;
      }

      FacilityDistribution value = iterator.next().getValue();
      String geographicZone = value.getGeographicZone();

      if (!filter.isProvinceSelected() || geographicZone.equalsIgnoreCase(filter.getProvince().getName())) {
        list.add(create(userId, distribution, geographicZone));
      }
    }

    Collections.sort(list, SynchronizedDistributionComparators.get(filter.getOrder()));
    return list;
  }

  public DistributionEdit checkInProgress(Distribution arg, Long userId) {
    Distribution distribution = distributionService.get(arg);

    List<DistributionEdit> inProgress = distributionService.getEditInProgress(distribution.getId(), userId, TimeUnit.MINUTES.toSeconds(distributionEditInProgress));
    Collections.sort(inProgress);

    DistributionEdit last = inProgress.isEmpty() ? null : inProgress.get(0);

    if (null != last) {
      last.setDistribution(null);
      last.setUser(userService.getById(last.getUser().getId()));
    }

    return last;
  }

  public void deleteDistributionEdit(Long distributionId, Long userId) {
    distributionService.deleteDistributionEdit(distributionId, userId);
  }

  public DistributionDTO getDistribution(Distribution arg, Long userId) {
    Distribution distribution = distributionService.getFullSyncedDistribution(arg);
    distributionService.insertEditInProgress(userId, distribution.getId());

    Map<Long, FacilityDistribution> facilityDistributionMap = facilityDistributionService.getData(distribution);

    distribution.setFacilityDistributions(facilityDistributionMap);

    return distribution.transform();
  }

  public FacilityDistributionEditResults update(Long distributionId, FacilityDistributionDTO replacement, Long userId) {
    deleteDistributionEdit(distributionId, userId);

    replacement.setModifiedBy(userId);

    Distribution distribution = distributionService.getBy(distributionId);
    Map<Long, FacilityDistribution> facilityDistributions = facilityDistributionService.getData(distribution);
    FacilityDistribution original = facilityDistributions.get(replacement.getFacilityId());

    FacilityDistributionEditHandler handler = new FacilityDistributionEditHandler();
    FacilityDistributionEditResults results = handler.check(original, replacement);

    Iterator<FacilityDistributionEditDetail> iterator = results.getDetails().iterator();

    while (iterator.hasNext()) {
      FacilityDistributionEditDetail detail = iterator.next();

      if (!detail.isConflict()) {
        facilityDistributionEditService.save(detail);
        createHistory(userId, distribution, original, detail);

        iterator.remove();
      }
    }

    return results;
  }

  public File getHistoryAsCSV(Long distributionId) throws IOException {
    String fileName = "history_" + distributionId;
    File tmp = File.createTempFile(fileName, "csv");

    try (ICsvBeanWriter writer = new CsvBeanWriter(new FileWriter(tmp), CsvPreference.STANDARD_PREFERENCE)) {
      String[] header = getHeader();
      writer.writeHeader(header);

      List<DistributionsEditHistory> history = distributionService.getHistory(distributionId);
      for (DistributionsEditHistory item : history) {
        writer.write(item, header);
      }
    }

    return tmp;
  }

  public File getHistoryAsPDF(Long distributionId) throws IOException {
    String fileName = "history_" + distributionId;
    File tmp = File.createTempFile(fileName, "pdf");

    Document document = new Document(PageSize.A4);

    try {
      @SuppressWarnings("unused")
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tmp));
      document.open();

      Paragraph title = new Paragraph(messageService.message("label.distribution.history.header"));
      title.setAlignment(Element.ALIGN_CENTER);

      Paragraph currentDate = new Paragraph(DATE_FORMAT.format(new Date()));
      currentDate.setAlignment(Element.ALIGN_RIGHT);

      Distribution distribution = distributionService.getBy(distributionId);
      distribution = distributionService.getFullSyncedDistribution(distribution);

      Map<Long, FacilityDistribution> facilityDistributions = facilityDistributionService.getData(distribution);
      Iterator<Map.Entry<Long, FacilityDistribution>> iterator = facilityDistributions.entrySet().iterator();

      if (!iterator.hasNext()) {
        throw new IllegalStateException("Missing facility distribution");
      }

      FacilityDistribution value = iterator.next().getValue();
      String geographicZone = value.getGeographicZone();

      Paragraph info = new Paragraph();
      info.add(new Paragraph(messageService.message("label.distribution.history.header.program", distribution.getProgram().getName())));
      info.add(new Paragraph(messageService.message("label.distribution.history.header.province", geographicZone)));
      info.add(new Paragraph(messageService.message("label.distribution.history.header.delivery.zone", distribution.getDeliveryZone().getName())));
      info.add(new Paragraph(messageService.message("label.distribution.history.header.period", distribution.getPeriod().getName())));


      PdfPTable dataTable = new PdfPTable(HEADER.length);

      for (String header : getHeader()) {
        dataTable.addCell(header);
      }

      List<DistributionsEditHistory> history = distributionService.getHistory(distributionId);
      for (DistributionsEditHistory item : history) {
        dataTable.addCell(item.getDistrict());
        dataTable.addCell(item.getFacility().getName());
        dataTable.addCell(item.getDataScreen());
        dataTable.addCell(item.getEditedItem());
        dataTable.addCell(item.getOriginalValue());
        dataTable.addCell(item.getNewValue());
        dataTable.addCell(DATE_TIME_FORMAT.format(item.getEditedDatetime()));
        dataTable.addCell(item.getEditedBy().getUserName());
      }

      document.add(title);
      document.add(currentDate);
      document.add(info);
      document.add(dataTable);
    } catch (DocumentException e) {
      throw new IOException(e);
    } finally {
      document.close();
    }

    return tmp;
  }

  private String[] getHeader() {
    String[] header = new String[HEADER.length];

    for (int i = 0; i < HEADER.length; ++i) {
      header[i] = messageService.message(HEADER[i]);
    }

    return header;
  }

  private void createHistory(Long userId, Distribution distribution, FacilityDistribution facilityDistribution,
                 FacilityDistributionEditDetail detail) {
    Facility facility = facilityService.getById(facilityDistribution.getFacilityId());

    DistributionsEditHistory history = new DistributionsEditHistory();
    history.setDistribution(distribution);

    history.setDistrict(facility.getGeographicZone().getName());
    history.setFacility(facility);

    history.setDataScreen(detail.getDataScreen());
    history.setEditedItem(detail.getEditedItem());

    history.setOriginalValue(detail.getOriginalValue().toString());
    history.setNewValue(detail.getNewValue().toString());

    history.setEditedBy(userService.getById(userId));

    distributionService.insertHistory(history);
  }

  private SynchronizedDistribution create(Long userId, Distribution distribution, String geographicZone) {
    DistributionsEditHistory history = distributionService.getLastHistory(distribution.getId());

    SynchronizedDistribution item = new SynchronizedDistribution();

    item.setDistributionId(distribution.getId());

    item.setProvince(geographicZone);
    item.setDeliveryZone(distribution.getDeliveryZone());
    item.setPeriod(distribution.getPeriod());

    item.setInitiated(distribution.getCreatedDate());
    item.setSync(distribution.getSyncDate());

    item.setView(isViewable(distribution.getSyncDate(), userId));
    item.setEdit(isEditable(distribution.getSyncDate(), userId));

    item.setLastViewed(distribution.getLastViewed());

    item.setLastEdited(history.getEditedDatetime());
    item.setEditedBy(history.getEditedBy().getUserName());

    return item;
  }

  private boolean isViewable(Date syncDate, Long userId) {
    Months months = Months.monthsBetween(new DateTime(syncDate), DateTime.now());
    boolean hasViewRight = permissionEvaluator.hasPermission(userId, VIEW_SYNCHRONIZED_DATA);
    boolean hasEditRight = permissionEvaluator.hasPermission(userId, EDIT_SYNCHRONIZED_DATA);
    return months.getMonths() <= 12 && ((hasViewRight && !hasEditRight) || (hasEditRight && !isEligibility(syncDate)));
  }

  private boolean isEditable(Date syncDate, Long userId) {
    Months months = Months.monthsBetween(new DateTime(syncDate), DateTime.now());
    boolean hasEditRight = permissionEvaluator.hasPermission(userId, EDIT_SYNCHRONIZED_DATA);
    return months.getMonths() <= 12 && hasEditRight && isEligibility(syncDate);
  }

  private boolean isEligibility(Date syncDate) {
    Days days = Days.daysBetween(new DateTime(syncDate), DateTime.now());
    return days.getDays() <= eligibilityEdit;
  }

}
