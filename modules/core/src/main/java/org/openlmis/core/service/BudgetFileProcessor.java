package org.openlmis.core.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.transformer.budget.BudgetLineItemTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@Service
public class BudgetFileProcessor {

  @Autowired
  BudgetFileTemplateService budgetFileTemplateService;

  @Autowired
  BudgetLineItemTransformer budgetLineItemTransformer;

  public void process(Message message) throws IOException, ParseException {

    File file = (File) message.getPayload();

    EDIFileTemplate budgetFileTemplate = budgetFileTemplateService.get();

    ICsvListReader listReader = new CsvListReader(new FileReader(file), STANDARD_PREFERENCE);

    if (budgetFileTemplate.getConfiguration().isHeaderInFile()) listReader.getHeader(true);

    List<String> csvRow;
    while ((csvRow = listReader.read()) != null) {
      Collection<EDIFileColumn> includedColumns = budgetFileTemplate.filterIncludedColumns();

      BudgetLineItemDTO budgetLineItemDTO = BudgetLineItemDTO.populate(csvRow, includedColumns);
      try {
        budgetLineItemDTO.checkMandatoryFields();
      } catch (Exception e) {
        //logger
        continue;
      }

      EDIFileColumn periodDateColumn = (EDIFileColumn) CollectionUtils.find(includedColumns, new Predicate() {
        @Override
        public boolean evaluate(Object o) {
          EDIFileColumn periodDateColumn = (EDIFileColumn) o;
          return periodDateColumn.getName().equals("periodStartDate") && periodDateColumn.getInclude();
        }
      });
      String datePattern = periodDateColumn == null ? null : periodDateColumn.getDatePattern();
      BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern);


    }


  }
}
