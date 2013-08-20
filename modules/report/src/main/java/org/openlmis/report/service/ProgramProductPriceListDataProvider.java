/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.lookup.ProgramProductPriceListMapper;
import org.openlmis.report.model.dto.ProgramProductPriceList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mahmed
 * Date: 6/19/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates..
 */

@NoArgsConstructor
@Service
public class ProgramProductPriceListDataProvider {

    @Autowired
   private ProgramProductPriceListMapper programProductPriceListMapper;

    public List<ProgramProductPriceList> getByProductId(Long productId){
        return programProductPriceListMapper.getByProductId(productId);
    }

    public List<ProgramProductPriceList> getAllPrices(){
        return programProductPriceListMapper.getAllPrices();
    }

}
