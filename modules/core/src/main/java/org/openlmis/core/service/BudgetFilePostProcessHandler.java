/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.BudgetFileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.stereotype.Component;

import java.io.File;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.springframework.integration.support.MessageBuilder.withPayload;

@Component
@NoArgsConstructor
public class BudgetFilePostProcessHandler {

  @Autowired
  private BudgetFileService budgetFileService;

  @Autowired
  private MessageChannel budgetFtpErrorChannel;

  @Autowired
  private MessageChannel budgetFtpArchiveOutputChannel;

  private static final Logger logger = Logger.getLogger(BudgetFilePostProcessHandler.class);


  public void process(BudgetFileInfo budgetFileInfo, File budgetFile) {
    budgetFileService.save(budgetFileInfo);

    Message<File> message = withPayload(budgetFile).build();

    if (!budgetFileInfo.getProcessingError()) {
      budgetFtpArchiveOutputChannel.send(message);
      logger.debug("Budget file " + budgetFile.getName() + " archived");
    } else {
      budgetFtpErrorChannel.send(message);
      logger.warn("Budget file " + budgetFile.getName() + " copied to error folder");
    }

    if (!deleteQuietly(budgetFile)) {
      logger.error("Unable to delete temporary Budget file " + budgetFile.getName());
    }
  }


}
