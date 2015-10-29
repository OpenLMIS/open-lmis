/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.service.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.vaccine.domain.inventory.*;
import org.openlmis.vaccine.dto.VaccineInventoryTransactionDTO;
import org.openlmis.vaccine.repository.inventory.StockMovementLineItemExtRepository;
import org.openlmis.vaccine.repository.inventory.StockMovementLineItemRepository;
import org.openlmis.vaccine.repository.inventory.StockMovementRepository;
import org.openlmis.vaccine.repository.inventory.VaccineInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Exposes the services for handling stock cards.
 */

@Service
@NoArgsConstructor
public class VaccineInventoryService {

    @Autowired
    VaccineInventoryRepository repository;



    @Autowired
    FacilityService facilityService;

    @Autowired
    StockMovementRepository stockMovementRepository;
    @Autowired
    StockMovementLineItemRepository lineItemRepository;

    @Autowired
    StockMovementLineItemExtRepository lineItemExtRepository;

//  public List<Lot> getLots(Long productId) {
//    return getTestLots(productId);
//  }

    public StockCard getStockCard(Long facilityId, Long productId) {
        return repository.getStockCard(facilityId, productId);
    }

    public StockCard getStockCardById(Long stockCardId) {
        return repository.getStockCardById(stockCardId);
    }

    public List<StockCard> getStockCards(Long facilityId) {
        return repository.getStockCards(facilityId);
    }

    public Integer updateStockCard(StockCard stockCard) {
        return repository.updateStockCard(stockCard);
    }

    public Integer insertStockCardEntry(StockCardEntry stockCardEntry) {
        return repository.insertStockCardEntry(stockCardEntry);
    }

    public Lot getLotByCode(String lotCode) {
        return repository.getLotByCode(lotCode);
    }

    public LotOnHand getLotOnHand(Long stockCardId, Long lotId) {
        return repository.getLotOnHand(stockCardId, lotId);
    }

    public Integer insertLot(Lot lot) {
        return repository.insertLot(lot);
    }

    public Integer insertStockCard(StockCard stockCard) {
        return repository.insertStockCard(stockCard);
    }

    public Integer insertLotsOnHand(LotOnHand lotOnHand) {
        return repository.insertLotsOnHand(lotOnHand);
    }


    public void saveTransaction(VaccineInventoryTransactionDTO dto, StockCardEntryType type, Long userId) {
        StockMovement stockMovement = new StockMovement();
        Facility homeFacility = facilityService.getHomeFacility(userId);
        Long facilityId = homeFacility.getId();

        if (type == StockCardEntryType.DEBIT) {

            stockMovement.setType(StockMovementType.Order);
            stockMovement.setFromFacilityId(facilityId);
            stockMovement.setToFacilityId(dto.getTransactionList().get(0).getToFacilityId());
            stockMovement.setInitiatedDate(dto.getTransactionList().get(0).getInitiatedDate());
            stockMovement.setCreatedBy(userId);
            stockMovement.setModifiedBy(userId);
            stockMovementRepository.insert(stockMovement);
        }

        for (InventoryTransaction transaction : dto.getTransactionList()) {
            //Check if stock card exist if not create;
            Long productId = (transaction.getProduct() != null) ? transaction.getProduct().getId() : transaction.getProductId();

           StockCard existStockCard = repository.getStockCard(facilityId, productId);

            // stockMovement.set

            //No stock card exist hence create it and associated lots and entry (ONLY CREDIT)
            //CREDIT
            if (existStockCard == null) {
                StockCard newStockCard = new StockCard();
                newStockCard.setFacilityId(facilityId);
                newStockCard.setProductId(productId);
                Long quantity = (transaction.getQuantity() != null) ? transaction.getQuantity() : 0L;
                newStockCard.setTotalQuantityOnHand(quantity);
                repository.insertStockCard(newStockCard);

                Long lotSum = 0L;
                if (transaction.getLots() != null && transaction.getLots().size() > 0) {
                    for (LotOnHandTransaction lot : transaction.getLots()) {
                        LotOnHand newLotOnHand = new LotOnHand();
                        StockCardEntry newEntry = new StockCardEntry();
                        Long lotId = (lot.getLot() != null) ? lot.getLot().getId() : lot.getLotId();

                        newLotOnHand.setStockCardId(newStockCard.getId());
                        newLotOnHand.setLotId(lotId);
                        newLotOnHand.setQuantityOnHand(lot.getQuantity());
                        repository.insertLotsOnHand(newLotOnHand);
//TODO:     Insert VVM status
                        if (lot.getVvmStatus() != null) {
                            repository.insertVVM(newLotOnHand.getId(), lot.getVvmStatus());
                        }

                        newEntry.setStockCardId(newStockCard.getId());
                        newEntry.setLotOnHandId(newLotOnHand.getId());
                        newEntry.setType(StockCardEntryType.CREDIT);
                        newEntry.setQuantity(lot.getQuantity());
                        repository.insertStockCardEntry(newEntry);

                        lotSum = lotSum + lot.getQuantity();
                    }
                    newStockCard.setTotalQuantityOnHand(lotSum);
                    repository.updateStockCard(newStockCard);
                }

            }
//    CREDIT /DEBIT /ADJUSTMENT
            else {
//          1. Stock card exist check if lot exist
                Long newTotalQuantity = existStockCard.getTotalQuantityOnHand();

                if (transaction.getLots() != null && transaction.getLots().size() > 0) {

                    for (LotOnHandTransaction lot : transaction.getLots()) {
                        Long lotId = (lot.getLot() != null) ? lot.getLot().getId() : lot.getLotId();
                        Long stockCardId = existStockCard.getId();
                        LotOnHand existingLotOnHand = repository.getLotOnHand(stockCardId, lotId);

                        //RECEIVING NEW BATCH: CREDIT- Create new Lot On Hand
                        if (existingLotOnHand == null) {
                            LotOnHand newLotOnHand = new LotOnHand();
                            StockCardEntry newEntry = new StockCardEntry();

                            newLotOnHand.setStockCardId(stockCardId);
                            newLotOnHand.setLotId(lotId);
                            newLotOnHand.setQuantityOnHand(lot.getQuantity());
                            repository.insertLotsOnHand(newLotOnHand);
//TODO Save LOT VVM status
                            if (lot.getVvmStatus() != null) {
                                repository.insertVVM(newLotOnHand.getId(), lot.getVvmStatus());
                            }
                            newEntry.setStockCardId(existStockCard.getId());
                            newEntry.setLotOnHandId(newLotOnHand.getId());
                            newEntry.setType(StockCardEntryType.CREDIT);
                            newEntry.setQuantity(lot.getQuantity());
                            repository.insertStockCardEntry(newEntry);
                            newTotalQuantity = newTotalQuantity + lot.getQuantity();
                        } else {
                            //CREDIT DEBIT ADJUST
                            Long currentQuantityOnHand = existingLotOnHand.getQuantityOnHand();
//ADJUSTMENT
                            if (type == StockCardEntryType.ADJUSTMENT) {
                                Long physicalCount = (lot.getQuantity() != null) ? lot.getQuantity() : currentQuantityOnHand;
                                Long difference = physicalCount - currentQuantityOnHand;
                                StockCardEntry newEntry = new StockCardEntry();

                                existingLotOnHand.setQuantityOnHand(physicalCount);
                                repository.updateLotOnHand(existingLotOnHand);
//TODO : Update LOT VVM status
                                if (lot.getVvmStatus() != null) {
                                    if (repository.updateVVM(existingLotOnHand.getId(), lot.getVvmStatus()) != 1) {
                                        repository.insertVVM(existingLotOnHand.getId(), lot.getVvmStatus());
                                    }
                                }
                                newEntry.setStockCardId(stockCardId);
                                newEntry.setLotOnHandId(existingLotOnHand.getId());
                                newEntry.setType(StockCardEntryType.ADJUSTMENT);
                                newEntry.setQuantity(difference);
                                repository.insertStockCardEntry(newEntry);
// TODO: Save adjustment reasons
                                if (lot.getAdjustmentReasons() != null) {
                                    for (AdjustmentReason reason : lot.getAdjustmentReasons()) {
                                        reason.setLotOnHandId(existingLotOnHand.getId());
                                        repository.insertAdjustmentReason(reason);
                                    }
                                }
                                newTotalQuantity = newTotalQuantity + difference;
                            }
// RECEIVE EXISTING BATCH
                            if (type == StockCardEntryType.CREDIT) {
                                StockCardEntry newEntry = new StockCardEntry();

                                existingLotOnHand.setQuantityOnHand(currentQuantityOnHand + lot.getQuantity());
                                repository.updateLotOnHand(existingLotOnHand);
//TODO: Initiate stock movement
                                newEntry.setStockCardId(stockCardId);
                                newEntry.setLotOnHandId(existingLotOnHand.getId());
                                newEntry.setType(StockCardEntryType.CREDIT);
                                newEntry.setQuantity(lot.getQuantity());
                                repository.insertStockCardEntry(newEntry);

                                newTotalQuantity = newTotalQuantity + lot.getQuantity();
                            }
// ISSUE FROM EXISTING BATCH
                            if (type == StockCardEntryType.DEBIT) {
                                StockCardEntry newEntry = new StockCardEntry();
                                existingLotOnHand.setQuantityOnHand(currentQuantityOnHand - lot.getQuantity());
                                repository.updateLotOnHand(existingLotOnHand);

                                newEntry.setStockCardId(stockCardId);
                                newEntry.setLotOnHandId(existingLotOnHand.getId());
                                newEntry.setType(StockCardEntryType.CREDIT);
                                newEntry.setQuantity(lot.getQuantity());
                                repository.insertStockCardEntry(newEntry);

                                newTotalQuantity = newTotalQuantity - lot.getQuantity();

                                StockMovementLineItem lineItem = new StockMovementLineItem();
                                lineItem.setStockMovementId(stockMovement.getId());
                                lineItem.setLotId(lotId);
                                lineItem.setQuantity(lot.getQuantity());
                                lineItem.setCreatedBy(userId);
                                lineItem.setModifiedBy(userId);
                                lineItemRepository.insert(lineItem);

                                if(lineItem.getId() != null) {

                                    StockMovementLineItemExt lineItemExt = new StockMovementLineItemExt();
                                    lineItemExt.setStockMovementLineItemId(lineItem.getId());
                                    lineItemExt.setIssueVoucher(transaction.getIssueVoucher());
                                    lineItemExt.setIssueDate(transaction.getIssueDate());
                                    lineItemExt.setToFacilityName(transaction.getToFacilityName());
                                    lineItemExt.setProductId(transaction.getProductId());
                                    lineItemExt.setDosesRequested(transaction.getDosesRequested());
                                    lineItemExt.setGap(transaction.getGap());
                                    lineItemExt.setProductCategoryId(transaction.getProductCategoryId());
                                    lineItemExt.setQuantityOnHand(lot.getQuantityOnHand());
                                    lineItemExt.setCreatedBy(userId);
                                    lineItemExtRepository.insert(lineItemExt);
                                }
                            }
                        }

                    }
                    existStockCard.setTotalQuantityOnHand(newTotalQuantity);
                } else {
                    if (type == StockCardEntryType.ADJUSTMENT) {
                        existStockCard.setTotalQuantityOnHand(transaction.getQuantity());
//                        TODO Insert Adjustment resons
                    } else if (type == StockCardEntryType.CREDIT) {
                        existStockCard.setTotalQuantityOnHand(transaction.getQuantity() + newTotalQuantity);
                    } else if (type == StockCardEntryType.DEBIT) {

                        existStockCard.setTotalQuantityOnHand(newTotalQuantity - transaction.getQuantity());
                    }
                }
                repository.updateStockCard(existStockCard);

            }
        }
    }

    public List<Lot> getLotsByProductId(Long productId) {
        return repository.getLotsByProductId(productId);
    }


    public List<StockCard> getStockCards(Long facilityId, Long programId) {
        return repository.getStockCards(facilityId, programId);
    }

    public StockMovement getLastStockMovement(){
        return stockMovementRepository.getLastStock();
    }
}
