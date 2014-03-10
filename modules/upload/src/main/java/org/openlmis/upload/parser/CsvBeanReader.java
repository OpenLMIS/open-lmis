/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.upload.parser;

import lombok.Getter;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.processor.CsvCellProcessors;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * This class has responsibility to instantiate a dozerBeanReader from given inputStream, and CsvPreferences.
 * Also is responsible for validating headers.
 */

public class CsvBeanReader {

  private ModelClass modelClass;
  private CsvDozerBeanReader dozerBeanReader;
  private CellProcessor[] processors;

  @Getter
  private String[] headers;

  public CsvBeanReader(ModelClass modelClass, InputStream inputStream) throws IOException {
    this.modelClass = modelClass;
    configureDozerBeanReader(inputStream);
    configureProcessors();
  }

  private void configureDozerBeanReader(InputStream inputStream) throws IOException {
    CsvPreference csvPreference = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE)
      .surroundingSpacesNeedQuotes(true).build();

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    dozerBeanReader = new CsvDozerBeanReader(bufferedReader, csvPreference);
    headers = readHeaders();
    String[] mappings = modelClass.getFieldNameMappings(headers);
    dozerBeanReader.configureBeanMapping(modelClass.getClazz(), mappings);
  }

  private String[] readHeaders() throws IOException {
    String[] headers = dozerBeanReader.getHeader(true);
    return headers == null ? new String[0] : headers;
  }

  public Importable read() throws IOException {
    return dozerBeanReader.read(modelClass.getClazz());
  }

  public Importable readWithCellProcessors() throws IOException {
    return dozerBeanReader.read(modelClass.getClazz(), processors);
  }

  public int getRowNumber() {
    return dozerBeanReader.getRowNumber();
  }

  public int length() {
    return dozerBeanReader.length();
  }

  private void configureProcessors() {
    List<CellProcessor> cellProcessors = CsvCellProcessors.getProcessors(modelClass, asList(headers));
    processors = cellProcessors.toArray(new CellProcessor[cellProcessors.size()]);
  }

  public void validateHeaders() {
    modelClass.validateHeaders(asList(headers));
  }
}
