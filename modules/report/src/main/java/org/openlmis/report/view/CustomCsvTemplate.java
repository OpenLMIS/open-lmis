/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openlmis.core.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class CustomCsvTemplate extends AbstractView {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomCsvTemplate.class);

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map queryModel = (Map)model.get("queryModel");
    List reportContent = (List) model.get("report");
    response.setHeader("Content-Disposition", "attachment; filename=" + queryModel.get("name") + ".csv");

    try (BufferedWriter writer = new BufferedWriter(response.getWriter())) {
      writeHeader(queryModel, writer);
      writeReportData(queryModel, reportContent, writer);
      writer.flush();
    } catch (IOException e) {
      throw new DataException(e.getMessage());
    }
  }

  private void writeHeader(Map queryModel, BufferedWriter writer) throws IOException {
    JsonNode columns = getColumnDefinitions(queryModel);
    int index = 0;
    for(JsonNode n: columns){
      String displayName = n.get("displayName").asText();
      writer.write(displayName.toString());
      if(index < columns.size() - 1){
        writer.write(",");
      }
    }
    writer.write("\n");
  }

  private JsonNode getColumnDefinitions(Map queryModel) {
    String columnModel = queryModel.get("columnoptions").toString();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actualObj = null;
    try {
      actualObj = mapper.readValue(columnModel, JsonNode.class);
    }catch (Exception exp){
      LOGGER.warn("Column Definition was not populated correctly due to .... ", exp);
    }
    return actualObj;
  }

  private void writeReportData(Map queryModel, List reportContent, BufferedWriter writer) throws IOException {
    JsonNode columns = getColumnDefinitions(queryModel);

    for(Object o: reportContent){
      Map m = (Map)o;
      int index = 0;

      for(JsonNode col: columns ){
        if( m.containsKey(col.get("name").asText()) && m.get( col.get("name").asText() ) != null ) {
          String colValue = m.get(col.get("name").asText()).toString();
          writer.write( colValue.toString() );
        }
        if(index < m.values().size() - 1){
          writer.write(",");
        }
        index++ ;
      }
      writer.write("\n");
    }
  }
}
