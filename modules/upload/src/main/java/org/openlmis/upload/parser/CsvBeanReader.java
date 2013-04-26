/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.upload.parser;

import lombok.Getter;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.ModelClass;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.Arrays.asList;

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
