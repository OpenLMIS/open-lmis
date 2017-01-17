package org.openlmis.web.service;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
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
import org.openlmis.distribution.util.EditedItemUI;
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
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
      ProcessingPeriod period = distribution.getPeriod();
      boolean exist = FluentIterable.from(periods).anyMatch(new PeriodPredicate(period.getId()));

      if (!exist) {
        periods.add(period);
      }
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

      Map.Entry<Long, FacilityDistribution> entry = iterator.next();
      FacilityDistribution value = entry.getValue();
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

  @Transactional
  public FacilityDistributionEditResults update(Long distributionId, FacilityDistributionDTO replacement, Long userId) {
    FacilityDistributionEditHandler handler = new FacilityDistributionEditHandler();
    FacilityDistributionEditResults results;

    deleteDistributionEdit(distributionId, userId);

    Distribution distribution = distributionService.getBy(distributionId);
    distribution = distributionService.getFullSyncedDistribution(distribution);
    Map<Long, FacilityDistribution> facilityDistributions = facilityDistributionService.getData(distribution);
    distribution.setFacilityDistributions(facilityDistributions);

    if (handler.modified(replacement)) {
      replacement.setModifiedBy(userId);
      FacilityDistribution original = facilityDistributions.get(replacement.getFacilityId());

      results = handler.check(original, replacement);

      Iterator<FacilityDistributionEditDetail> iterator = results.getDetails().iterator();

      while (iterator.hasNext()) {
        FacilityDistributionEditDetail detail = iterator.next();

        if (!detail.isConflict()) {
          facilityDistributionEditService.save(detail);
          createHistory(userId, distribution, detail, original.getFacilityId());

          iterator.remove();
        }
      }
    } else {
      results = new FacilityDistributionEditResults(replacement.getFacilityId());
    }

    distribution = distributionService.getFullSyncedDistribution(distribution);
    facilityDistributions = facilityDistributionService.getData(distribution);
    distribution.setFacilityDistributions(facilityDistributions);

    results.setDistribution(distribution.transform());

    return results;
  }

  public DistributionDTO update(Long distributionId, Long facilityId, FacilityDistributionEditDetail detail, Long userId) {
    Distribution distribution = distributionService.getBy(distributionId);
    distribution = distributionService.getFullSyncedDistribution(distribution);
    Map<Long, FacilityDistribution> facilityDistributions = facilityDistributionService.getData(distribution);
    distribution.setFacilityDistributions(facilityDistributions);

    facilityDistributionEditService.save(detail);
    createHistory(userId, distribution, detail, facilityId);

    distribution = distributionService.getFullSyncedDistribution(distribution);
    facilityDistributions = facilityDistributionService.getData(distribution);
    distribution.setFacilityDistributions(facilityDistributions);

    return distribution.transform();
  }

  public File getHistoryAsCSV(Long distributionId) throws IOException {
    String fileName = "history_" + distributionId;
    File tmp = File.createTempFile(fileName, ".csv");

    try (ICsvMapWriter writer = new CsvMapWriter(new FileWriter(tmp), CsvPreference.STANDARD_PREFERENCE)) {
      String[] header = getHeader();
      writer.writeHeader(header);

      List<DistributionsEditHistory> history = distributionService.getHistory(distributionId);

      for (DistributionsEditHistory item : history) {
        Map<String, Object> record = new HashMap<>();
        record.put(header[0], item.getDistrict());
        record.put(header[1], item.getFacility().getName());
        record.put(header[2], item.getDataScreen());
        record.put(header[3], item.getEditedItem());
        record.put(header[4], item.getOriginalValue());
        record.put(header[5], item.getNewValue());
        record.put(header[6], item.getEditedDatetime());
        record.put(header[7], item.getEditedBy().getUserName());

        writer.write(record, header);
      }
    }

    return tmp;
  }

  public File getHistoryAsPDF(Long distributionId) throws IOException {
    String fileName = "history_" + distributionId;
    File tmp = File.createTempFile(fileName, ".pdf");

    Document document = new Document(PageSize.A4);

    try {
      Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
      Font normalFont = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
      Font boldFont = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);

      Paragraph title = new Paragraph(messageService.message("label.distribution.history.header"), headerFont);
      title.setAlignment(Element.ALIGN_CENTER);

      Paragraph currentDate = new Paragraph(DATE_FORMAT.format(new Date()), normalFont);
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

      Paragraph program = new Paragraph();
      program.add(new Phrase(messageService.message("label.distribution.history.header.program"), normalFont));
      program.add(new Phrase(" "));
      program.add(new Phrase(distribution.getProgram().getName(), boldFont));

      Paragraph province = new Paragraph();
      province.add(new Phrase(messageService.message("label.distribution.history.header.province"), normalFont));
      province.add(new Phrase(" "));
      province.add(new Phrase(geographicZone, boldFont));

      Paragraph deliveryZone = new Paragraph();
      deliveryZone.add(new Phrase(messageService.message("label.distribution.history.header.delivery.zone"), normalFont));
      deliveryZone.add(new Phrase(" "));
      deliveryZone.add(new Phrase(distribution.getDeliveryZone().getName(), boldFont));

      Paragraph period = new Paragraph();
      period.add(new Phrase(messageService.message("label.distribution.history.header.period"), normalFont));
      period.add(new Phrase(" "));
      period.add(new Phrase(distribution.getPeriod().getName(), boldFont));

      PdfPTable infoTable = new PdfPTable(4);
      infoTable.setWidthPercentage(100);
      infoTable.setSpacingBefore(0f);
      infoTable.setSpacingAfter(0f);

      infoTable.addCell(createCenterCell(program));
      infoTable.addCell(createCenterCell(province));
      infoTable.addCell(createCenterCell(deliveryZone));
      infoTable.addCell(createCenterCell(period));

      PdfPTable dataTable = new PdfPTable(HEADER.length);
      dataTable.setWidthPercentage(100);
      dataTable.setSpacingBefore(0f);
      dataTable.setSpacingAfter(0f);

      for (String header : getHeader()) {
        dataTable.addCell(createCenterCell(new Paragraph(header, boldFont)));
      }

      List<DistributionsEditHistory> history = distributionService.getHistory(distributionId);

      for (DistributionsEditHistory item : history) {
        dataTable.addCell(createLeftCell(new Paragraph(item.getDistrict(), normalFont)));
        dataTable.addCell(createLeftCell(new Paragraph(item.getFacility().getName(), normalFont)));
        dataTable.addCell(createLeftCell(new Paragraph(item.getDataScreen(), normalFont)));
        dataTable.addCell(createLeftCell(new Paragraph(item.getEditedItem(), normalFont)));
        dataTable.addCell(createLeftCell(new Paragraph(item.getOriginalValue(), normalFont)));
        dataTable.addCell(createLeftCell(new Paragraph(item.getNewValue(), normalFont)));
        dataTable.addCell(createLeftCell(new Paragraph(DATE_TIME_FORMAT.format(item.getEditedDatetime()), normalFont)));
        dataTable.addCell(createLeftCell(new Paragraph(item.getEditedBy().getUserName(), normalFont)));
      }

      PdfWriter.getInstance(document, new FileOutputStream(tmp));
      document.open();

      document.add(title);
      document.add(currentDate);
      document.add(infoTable);
      document.add(Chunk.NEWLINE);
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

  private PdfPCell createLeftCell(Element element) {
    PdfPCell cell = new PdfPCell();
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
    cell.addElement(element);

    return cell;
  }

  private PdfPCell createCenterCell(Element element) {
    PdfPCell cell = new PdfPCell();
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.addElement(element);

    return cell;
  }

  private void createHistory(Long userId, Distribution distribution,
                             FacilityDistributionEditDetail detail, Long facilityId) {
    Facility facility = facilityService.getById(facilityId);

    DistributionsEditHistory history = new DistributionsEditHistory();
    history.setDistribution(distribution);

    history.setDistrict(facility.getGeographicZone().getName());
    history.setFacility(facility);

    history.setDataScreen(messageService.message(detail.getDataScreenUI()));
    history.setEditedItem(createEditedItem(detail.getEditedItemUI()));

    history.setOriginalValue(null == detail.getOriginalValue() ? "" : detail.getOriginalValue().toString());
    history.setNewValue(null == detail.getNewValue() ? "": detail.getNewValue().toString());

    history.setEditedBy(userService.getById(userId));

    distributionService.insertHistory(history);
  }

  private String createEditedItem(EditedItemUI ui) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < ui.getTranslate().size(); ++i) {
      String format = i == 0 ? "%s" : " (%s)";
      String message = messageService.message(ui.getTranslate().get(i));

      sb.append(String.format(format, message));
    }

    for (String value : ui.getNoTranslate()) {
      sb.append(String.format(" (%s)", value));
    }

    return sb.toString();
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

    if (null != history) {
      item.setLastEdited(history.getEditedDatetime());
      item.setEditedBy(history.getEditedBy().getUserName());
    }

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

  private static final class PeriodPredicate implements Predicate<ProcessingPeriod> {
    private Long id;

    public PeriodPredicate(Long id) {
      this.id = id;
    }

    @Override
    public boolean apply(@Nullable ProcessingPeriod input) {
      return null != input && input.getId().equals(id);
    }

  }

}
