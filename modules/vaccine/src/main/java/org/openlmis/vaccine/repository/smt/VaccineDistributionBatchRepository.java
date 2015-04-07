/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository.smt;

import org.openlmis.core.exception.DataException;
import org.openlmis.vaccine.domain.smt.InventoryBatch;
import org.openlmis.vaccine.domain.smt.InventoryTransaction;
import org.openlmis.vaccine.domain.smt.OnHand;
import org.openlmis.vaccine.repository.mapper.smt.VaccineDistributionBatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Deprecated
public class VaccineDistributionBatchRepository {

    @Autowired
    private VaccineDistributionBatchMapper distributionBatchMapper;

    public List<InventoryTransaction> getReceivedVaccinesForFacility(Long facilityId){
        return distributionBatchMapper.getInventoryTransactionsByReceivingFacility(facilityId);
    }
    public InventoryTransaction getReceivedVaccineById(Long id){
        return distributionBatchMapper.getInventoryTransactionsById(id);
    }


    public List<InventoryBatch> getUsableBatches(Long productId){
        return distributionBatchMapper.getUsableBatches(productId);
    }

    @Transactional
    public void updateInventoryTransaction(InventoryTransaction inventoryTransaction, boolean isReceived){
        try {
            if (inventoryTransaction.getId() == null) {
                distributionBatchMapper.insertInventoryTransaction(inventoryTransaction);
                if(inventoryTransaction.getId() != null && inventoryTransaction.getInventoryBatches() != null ){
                    //insert list of batches
                    for(InventoryBatch inventoryBatch : inventoryTransaction.getInventoryBatches()){

                        InventoryTransaction transaction = new InventoryTransaction();
                        transaction.setId(inventoryTransaction.getId());
                        inventoryBatch.setInventoryTransaction(transaction);
                        distributionBatchMapper.insertInventoryBatch(inventoryBatch);


                        //insert onHand
                        OnHand onHand = new OnHand();
                        onHand.setInventoryTransaction(inventoryTransaction);
                        onHand.setTransactionType(inventoryTransaction.getTransactionType());
                        onHand.setInventoryBatch(inventoryBatch);
                        onHand.setProduct(inventoryTransaction.getProduct());
                        if(isReceived){
                            onHand.setQuantity(inventoryBatch.getQuantity());
                        }else {
                            onHand.setQuantity(-1*inventoryBatch.getQuantity());
                        }
                        onHand.setFacility(inventoryTransaction.getToFacility());

                        distributionBatchMapper.insertOnHand(onHand);
                    }
                }

            } else {
                distributionBatchMapper.updateInventoryTransaction(inventoryTransaction);
                //Update list of batches

                for(InventoryBatch inventoryBatch : inventoryTransaction.getInventoryBatches()){
                    if(inventoryBatch.getId() != null){
                        distributionBatchMapper.updateInventoryBatch(inventoryBatch);
                        distributionBatchMapper.deleteOnHandForBatchId(inventoryBatch.getId());//first delete all on hands for existing batch and recreate
                    }else{
                        InventoryTransaction transaction = new InventoryTransaction();
                        transaction.setId(inventoryTransaction.getId());
                        inventoryBatch.setInventoryTransaction(transaction);
                        distributionBatchMapper.insertInventoryBatch(inventoryBatch);
                    }

                    //insert onHand
                    OnHand onHand = new OnHand();
                    onHand.setInventoryTransaction(inventoryTransaction);
                    onHand.setTransactionType(inventoryTransaction.getTransactionType());
                    onHand.setInventoryBatch(inventoryBatch);
                    onHand.setProduct(inventoryTransaction.getProduct());
                    if(isReceived){
                        onHand.setQuantity(inventoryBatch.getQuantity());
                    }else {
                        onHand.setQuantity(-1*inventoryBatch.getQuantity());
                    }
                    onHand.setFacility(inventoryTransaction.getToFacility());

                    distributionBatchMapper.insertOnHand(onHand);
                }
            }
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new DataException("error.duplicate");
        } catch (DataIntegrityViolationException integrityViolationException) {
            String errorMessage = integrityViolationException.getMessage().toLowerCase();
            if (errorMessage.contains("foreign key") || errorMessage.contains("not-null constraint")) {
                throw new DataException("error.reference.data.missing");
            }
            throw new DataException("error.incorrect.length");
        }catch (Exception e){
            throw e;
        }
    }

}
