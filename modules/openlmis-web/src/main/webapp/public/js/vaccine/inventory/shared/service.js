/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

services.factory('StockCardsByCategory', function($resource,StockCards,$q, $timeout, VaccineProgramProducts){

 var programId;
 var facilityId;
 var programProducts=[];

 function get(pId,fId) {
    var deferred =$q.defer();
                $timeout(function(){
                    if(!isNaN(pId)){
                        VaccineProgramProducts.get({programId:pId},function(data){
                                 programProducts=data.programProductList;

                                 StockCards.get({facilityId:fId},function(data){
                                        var stockCards=data.stockCards;

                                        stockCards.forEach(function(s){
                                              var product= _.filter(programProducts, function(obj) {
                                                  return obj.product.primaryName === s.product.primaryName;
                                              });

                                                    s.productCategory=product[0].productCategory;
                                              });

                                              var byCategory=_.groupBy(stockCards,function(s){
                                                   return s.productCategory.name;
                                              });

                                              var stockCardsToDisplay = $.map(byCategory, function(value, index) {
                                                  return [{"productCategory":index,"stockCards":value}];
                                              });

                                              deferred.resolve(stockCardsToDisplay);
                                 });
                        });
                    }
                    else{
                            var stockCardsToDisplay=[];
                            StockCards.get({facilityId:fId},function(data){
                                var stockCards=data.stockCards;
                                if(stockCards.length > 0){
                                     stockCardsToDisplay=[{"productCategory":"no-category","stockCards":stockCards}];
                                }
                                deferred.resolve(stockCardsToDisplay);
                            });

                    }


                },100);
     return deferred.promise;

  }
return {
  get: get,
 };
});




services.factory('StockCardsByCategoryAndRequisition', function($resource,StockCards,$q, $timeout, VaccineOrderRequisitionProgramProduct,RequisitionForFacility){

    var programId;
    var facilityId;
    var programProducts=[];
    var quantityRequested = [];

    function get(pId,fId,periodId,toFacilityId) {
        var deferred =$q.defer();
        $timeout(function(){
            if(!isNaN(pId)){
                VaccineOrderRequisitionProgramProduct.get({programId:pId},function(data){
                    programProducts=data.programProductList;

                    RequisitionForFacility.get({programId:pId, periodId:periodId,facilityId:toFacilityId}, function(data){
                        quantityRequested = data.requisitionList;

                        StockCards.get({facilityId:fId},function(data){
                            var stockCards = data.stockCards;

                            stockCards.forEach(function(s){
                                var product= _.filter(programProducts, function(obj) {
                                    return obj.product.primaryName === s.product.primaryName;
                                });

                                var quantityToRequest= _.filter(quantityRequested, function(obj) {
                                    return obj.productId === s.product.id;
                                });


                                s.productCategory=product[0].productCategory;


                                s.quantityRequested = quantityToRequest[0].quantityRequested;

                            });
                            var byCategory=_.groupBy(stockCards,function(s){
                                return s.productCategory.name;
                            });

                            var stockCardsToDisplay = $.map(byCategory, function(value, index) {
                                return [{"productCategory":index,"stockCards":value}];
                            });

                            deferred.resolve(stockCardsToDisplay);
                        });

                    });

                });
            }
            else{
                var stockCardsToDisplay=[];
                StockCards.get({facilityId:fId},function(data){
                    var stockCards=data.stockCards;
                    if(stockCards.length > 0){
                        stockCardsToDisplay=[{"productCategory":"no-category","stockCards":stockCards}];
                    }
                    deferred.resolve(stockCardsToDisplay);
                });

            }


        },100);
        return deferred.promise;

    }
    return {
        get: get,
    };
});


services.factory('VaccineOrderRequisitionReport', function($resource){
    return $resource('/vaccine/inventory/facilities/:facilityId/programs/:programId/stockCards.json', {facilityId: '@facilityId',programId:'@programId'}, {});
});
services.factory('VaccineOrderRequisitionProgramProduct', function($resource){
    return $resource('/vaccine/orderRequisition/:programId.json', {programId:'@programId'}, {});
});


services.factory('StockCardsForProgramByCategory', function($resource,VaccineOrderRequisitionReport,RequisitionForFacility,$q, $timeout, VaccineOrderRequisitionProgramProduct){

    var programProducts=[];
    var programId;
    var facilityId;
    var quantityRequested = [];

    function get(pId,fId,periodId,toFacilityId) {
        var deferred = $q.defer();
        $timeout(function(){
            if(!isNaN(pId)){
                VaccineOrderRequisitionProgramProduct.get({programId:pId},function(data){
                    programProducts=data.programProductList;

                    RequisitionForFacility.get({programId:pId, periodId:periodId,facilityId:toFacilityId}, function(data) {

                        quantityRequested = data.requisitionList;

                        VaccineOrderRequisitionReport.get({facilityId: fId, programId: pId}, function (data) {
                            var stockCards = data.stockCards;

                            stockCards.forEach(function (s) {
                                var product = _.filter(programProducts, function (obj) {
                                    if(s.product.primaryName !== undefined) {
                                        return obj.product.primaryName === s.product.primaryName;
                                    }
                                });

                                var quantityToRequest= _.filter(quantityRequested, function(obj) {
                                    if(obj.productId !== undefined) {
                                        return obj.productId === s.product.id;
                                    }
                                });

                                s.productCategory = product[0].productCategory;

                                if(quantityToRequest[0].quantityRequested !==undefined)
                                s.quantityRequested = quantityToRequest[0].quantityRequested;
                            });

                            var byCategory = _.groupBy(stockCards, function (s) {
                                return s.productCategory.name;
                            });

                            var stockCardsToDisplay = $.map(byCategory, function (value, index) {
                                return [{"productCategory": index, "stockCards": value}];
                            });

                            deferred.resolve(stockCardsToDisplay);
                        });

                    });


                });
            }
            else{
                var stockCardsToDisplay=[];
                VaccineOrderRequisitionReport.get({facilityId:fId, programId:pId},function(data){
                    var stockCards=data.stockCards;
                    if(stockCards.length > 0){
                        stockCardsToDisplay=[{"productCategory":"no-category","stockCards":stockCards}];
                    }
                    deferred.resolve(stockCardsToDisplay);
                });

            }


        },100);
        return deferred.promise;

    }
    return {
        get: get,
    };
});







services.factory('RequisitionForFacility', function ($resource) {
    return $resource('/vaccine/orderRequisition/getAllBy/:programId/:periodId/:facilityId.json', {facilityId: '@facilityId', programId: '@programId'}, {});
});

services.factory('FacilitiesWithProducts', function($resource,$timeout,$q,VaccineReportFacilities,StockCards,DistributedFacilities){
     var programId;
     var facilityId;
     var allSupervisedFacilities;
     var stockCards;
     var distributions;

     function get(pId,fId) {
         var deferred =$q.defer();
                     $timeout(function(){
                         if(!isNaN(pId)){
                            VaccineReportFacilities.get({programId:pId},function(data){
                                allSupervisedFacilities=data.facilities;
                                StockCards.get({facilityId:fId},function(data){
                                    stockCards=data.stockCards;
                                    DistributedFacilities.get(function(data){
                                        distributions=data.Distributions;
                                        allSupervisedFacilities.forEach(function(facility){

                                             facility.productsToIssue=[];
                                             var facilityDistribution=_.findWhere(distributions,{toFacilityId:facility.id});
                                             if(facilityDistribution !=undefined)
                                             {
                                                facility.status=facilityDistribution.status;
                                             }
                                             stockCards.forEach(function(stockCard){
                                                 var product={};
                                                 var distributedProduct
                                                 if(facilityDistribution !=undefined)
                                                 {
                                                     distributedProduct=_.findWhere(facilityDistribution.lineItems,{productId:stockCard.product.id});
                                                     console.log(JSON.stringify(distributedProduct));
                                                 }
                                                 product.name=stockCard.product.primaryName;
                                                 product.productId=stockCard.product.id;
                                                 product.totalQuantityOnHand=stockCard.totalQuantityOnHand;
                                                 product.quantity=(distributedProduct===undefined)?0:distributedProduct.quantity;
                                                 if(stockCard.lotsOnHand !== undefined && stockCard.lotsOnHand.length >0)
                                                 {
                                                      product.lots=[];
                                                      stockCard.lotsOnHand.forEach(function(lot){
                                                          var lotOnHand={};
                                                          var distributedLot;
                                                          if(distributedProduct !== undefined)
                                                          {
                                                             distributedLot=_.findWhere(distributedProduct.lots,{lotId:lot.lot.id});
//                                                             console.log(distributedLot);
                                                          }
                                                          lotOnHand.lotId=lot.lot.id;
                                                          lotOnHand.lotCode=lot.lot.lotCode;
                                                          lotOnHand.quantityOnHand=lot.quantityOnHand;
                                                          lotOnHand.quantity=(distributedLot === undefined)?0:distributedLot.quantity;
                                                          lotOnHand.vvmStatus=lot.vvmStatus;
                                                          lotOnHand.expirationDate=lot.lot.expirationDate;
                                                          product.lots.push(lotOnHand);
                                                      });
                                                 }
                                                 facility.productsToIssue.push(product);
                                             });
                                        });

                                    });

                                    deferred.resolve(allSupervisedFacilities);
                                });
                             });
                         }

                     },100);
          return deferred.promise;

       }
     return {
       get: get,
      };


});
