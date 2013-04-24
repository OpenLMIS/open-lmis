/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.upload.parser;

import lombok.Getter;
import org.openlmis.upload.Importable;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.ModelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private static Logger logger = LoggerFactory.getLogger(CsvBeanReader.class);

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
    headers = dozerBeanReader.getHeader(true);
    String[] mappings = modelClass.getFieldNameMappings(headers);
    dozerBeanReader.configureBeanMapping(modelClass.getClazz(), mappings);

  }

  public Importable read() throws IOException {
    return dozerBeanReader.read(modelClass.getClazz());
  }

  public void validateHeaders() throws UploadException {
    for (int i = 0; i < headers.length; i++) {
      if (headers[i] == null) {
        throw new UploadException("Header for column " + (i + 1) + " is missing.");
      }
      headers[i] = headers[i].trim();
    }
    List<String> headerList = asList(headers);
    modelClass.validateHeaders(headerList);
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
}
