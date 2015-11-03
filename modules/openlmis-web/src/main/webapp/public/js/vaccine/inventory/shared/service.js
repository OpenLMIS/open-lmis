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
                                              s.displayOrder=s.product.form.displayOrder;
                                              var product= _.filter(programProducts, function(obj) {
                                                  return obj.product.primaryName === s.product.primaryName;
                                              });
                                                s.productCategory=product[0].productCategory;
                                        });
                                        stockCards=_.sortBy(stockCards,'displayOrder');

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

services.factory('FacilitiesWithProducts', function($resource,$timeout,$q,OneLevelSupervisedFacilities,StockCards,DistributedFacilities){
     var programId;
     var facilityId;
     var allSupervisedFacilities;
     var stockCards;
     var distributions;
     var facilities={};
     facilities.routine=[];
     facilities.emergence=[];

     function get(pId,fId) {
         var deferred =$q.defer();
                     $timeout(function(){
                         if(!isNaN(pId)){
                            OneLevelSupervisedFacilities.get(function(f){

                                StockCards.get({facilityId:fId},function(s){
                                    DistributedFacilities.get(function(d){
                                        allSupervisedFacilities=f.facilities;
                                        stockCards=s.stockCards;
                                        distributions=d.Distributions;
                                        allSupervisedFacilities.forEach(function(facility){

                                             facility.productsToIssue=[];
                                             var facilityDistribution=_.findWhere(distributions,{toFacilityId:facility.id});
                                             facility.status=(facilityDistribution !== undefined)?facilityDistribution.status:undefined;
                                             facility.voucherNumber=(facilityDistribution !== undefined)?facilityDistribution.voucherNumber:undefined;
                                             facility.distributionDate=(facilityDistribution !== undefined)?facilityDistribution.distributionDate:undefined;
                                             facility.distributionId=(facilityDistribution !== undefined)?facilityDistribution.id:undefined;
                                             stockCards.forEach(function(stockCard){
                                                 var product={};
                                                 var distributedProduct;
                                                 if(facilityDistribution !== undefined)
                                                 {
                                                     distributedProduct=_.findWhere(facilityDistribution.lineItems,{productId:stockCard.product.id});
                                                 }
                                                 product.name=stockCard.product.primaryName;
                                                 product.productId=stockCard.product.id;
                                                 product.productCode=stockCard.product.code;
                                                 product.displayOrder=stockCard.product.id;
                                                 product.totalQuantityOnHand=stockCard.totalQuantityOnHand;
                                                 product.quantity=(distributedProduct===undefined || facility.status==="RECEIVED")?0:distributedProduct.quantity;
                                                 product.emergenceQuantity=0;
                                                 product.lineItemId=(distributedProduct===undefined)?null:distributedProduct.id;
                                                 product.initialQuantity=(distributedProduct === undefined)?null:distributedProduct.quantity;
                                                 if(stockCard.lotsOnHand !== undefined && stockCard.lotsOnHand.length >0)
                                                 {
                                                      product.lots=[];
                                                      stockCard.lotsOnHand.forEach(function(lot){
                                                          var lotOnHand={};
                                                          var distributedLot;
                                                          if(distributedProduct !== undefined)
                                                          {
                                                             distributedLot=_.findWhere(distributedProduct.lots,{lotId:lot.lot.id});
                                                          }
                                                          lotOnHand.lotId=lot.lot.id;
                                                          lotOnHand.lotCode=lot.lot.lotCode;
                                                          lotOnHand.quantityOnHand=lot.quantityOnHand;
                                                          lotOnHand.quantity=(distributedLot === undefined || facility.status==="RECEIVED" )?0:distributedLot.quantity;
                                                          lotOnHand.lineItemLotId=(distributedLot === undefined)?null:distributedLot.id;
                                                          lotOnHand.initialQuantity=(distributedLot === undefined)?null:distributedLot.quantity;
                                                          lotOnHand.vvmStatus=lot.vvmStatus;
                                                          lotOnHand.expirationDate=lot.lot.expirationDate;
                                                          product.lots.push(lotOnHand);
                                                      });
                                                 }

                                                 facility.productsToIssue.push(product);
                                                 facility.productsToIssue=_.sortBy(facility.productsToIssue,'displayOrder');
                                             });

                                        });

                                         facilities.routine = $.grep(allSupervisedFacilities, function (facility) {
                                                   return facility.status !=='RECEIVED';
                                         });
                                         facilities.emergency = $.grep(allSupervisedFacilities, function (facility) {
                                                   return facility.status ==="RECEIVED";
                                         });
                                         deferred.resolve(facilities);
                                    });

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


services.factory('VaccineOrderRequisitionByCategory', function ($resource, VaccineOrderRequisitionGetReport, $q, $timeout, VaccineProgramProducts) {

    var programId;
    var facilityId;
    var programProducts = [];

    function get(OrderId,programId) {
        var deferred = $q.defer();
        $timeout(function () {
            if (!isNaN(OrderId)) {
                VaccineProgramProducts.get({programId: parseInt(programId,10)}, function (data) {
                    programProducts = data.programProductList;

                    VaccineOrderRequisitionGetReport.get({id: parseInt(OrderId,10)}, function (data) {

                        var overallData = data.report;

                        var lineItems = data.report.lineItems;

                        lineItems.forEach(function(s){
                            s.displayOrder=s.product.form.displayOrder;
                            var product= _.filter(programProducts, function(obj) {
                                return obj.product.primaryName === s.product.primaryName;
                            });
                            s.productCategory=product[0].productCategory;
                        });
                        lineItems=_.sortBy(lineItems,'displayOrder');

                        var byCategory=_.groupBy(lineItems,function(s){
                            return s.productCategory.name;
                        });

                        var stockCardsToDisplay = $.map(byCategory, function(value, index) {
                            return {"productCategory":index,"lineItem":value};
                        });

                        var object = angular.extend({},overallData,stockCardsToDisplay);

                        deferred.resolve(object);
                    });

                });
            }
            else {
                var stockCardsToDisplay = [];
                VaccineOrderRequisitionGetReport.get({id:parseInt(OrderId,10)}, function (data) {
                    var stockCards = data.report.lineItems;
                    if (stockCards.length > 0) {
                        stockCardsToDisplay = [{"productCategory": "no-category", "stockCards": stockCards}];
                    }
                    deferred.resolve(stockCardsToDisplay);
                });

            }


        }, 100);
        return deferred.promise;

    }

    return {
        get: get,
    };
});



services.factory('StockRequirementsData', function ($resource, StockRequirements, $q, $timeout, VaccineOrderRequisitionProgramProduct) {

    var programProducts = [];
    var pageLineItems = [];

    function getData(program, homeFacility) {

        var deferred = $q.defer();
        $timeout(function () {
            if (!isNaN(program) && !isUndefined(homeFacility)) {

                VaccineOrderRequisitionProgramProduct.get({programId: program}, function (data) {
                    programProducts = data.programProductList;


                    StockRequirements.get({facilityId: homeFacility, programId: program}, function (data) {

                        pageLineItems = data;
                        pageLineItems.forEach(function (s) {

                            if (s.isaValue !== null && s.eop !== null && s.maxMonthsOfStock !== null) {

                                var product = _.filter(programProducts, function (obj) {
                                    if (s.productName !== undefined) {
                                        return obj.product.primaryName === s.productName;
                                    }
                                });
                                s.productCategory = product[0].productCategory;
                            }


                        });

                        var byCategory = _.groupBy(pageLineItems, function (s) {
                            return s.productCategory.name;

                        });
                        var stockCardsToDisplay = $.map(byCategory, function (value, index) {
                            if (index !== 'undefined') {
                                return [{"productCategory": index, "stockRequirements": value}];
                            }
                        });

                        deferred.resolve(stockCardsToDisplay);

                    });


                });


            } else {
                var stockCardsToDisplay = [];
                StockRequirements.get({facilityId: homeFacility, programId: program}, function (data) {
                    var stockRequirements = data;
                    if (stockRequirements.length > 0) {
                        stockCardsToDisplay = [{
                            "productCategory": "no-category",
                            "stockRequirements": stockRequirements
                        }];
                    }
                    deferred.resolve(stockCardsToDisplay);
                });

            }
        }, 100);
        return deferred.promise;

    }

    return {
        get: getData,
    };


});





services.factory('StockCardsByCategoryAndRequisition', function ($resource, StockCards, $q, $timeout, VaccineOrderRequisitionProgramProduct, RequisitionForFacility) {

    var programId;
    var facilityId;
    var programProducts = [];
    var quantityRequested = [];

    function get(pId, fId, periodId, toFacilityId) {
        var deferred = $q.defer();
        $timeout(function () {
            if (!isNaN(pId)) {
                VaccineOrderRequisitionProgramProduct.get({programId: pId}, function (data) {
                    programProducts = data.programProductList;

                    RequisitionForFacility.get({
                        programId: pId,
                        periodId: periodId,
                        facilityId: toFacilityId
                    }, function (data) {
                        quantityRequested = data.requisitionList;

                        StockCards.get({facilityId: fId}, function (data) {
                            var stockCards = data.stockCards;

                            stockCards.forEach(function (s) {
                                var product = _.filter(programProducts, function (obj) {
                                    return obj.product.primaryName === s.product.primaryName;
                                });

                                var quantityToRequest = _.filter(quantityRequested, function (obj) {
                                    return obj.productId === s.product.id;
                                });


                                s.productCategory = product[0].productCategory;


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
            else {
                var stockCardsToDisplay = [];
                StockCards.get({facilityId: fId}, function (data) {
                    var stockCards = data.stockCards;
                    if (stockCards.length > 0) {
                        stockCardsToDisplay = [{"productCategory": "no-category", "stockCards": stockCards}];
                    }
                    deferred.resolve(stockCardsToDisplay);
                });

            }


        }, 100);
        return deferred.promise;

    }

    return {
        get: get,
    };
});

services.factory('StockCardsForProgramByCategory', function ($resource, VaccineOrderRequisitionReport, RequisitionForFacility, $q, $timeout, VaccineOrderRequisitionProgramProduct) {

    var programProducts = [];
    var programId;
    var facilityId;
    var quantityRequested = [];

    function get(pId, fId, periodId, toFacilityId) {
        var deferred = $q.defer();
        $timeout(function () {
            if (!isNaN(pId)) {
                VaccineOrderRequisitionProgramProduct.get({programId: pId}, function (data) {
                    programProducts = data.programProductList;

                    RequisitionForFacility.get({
                        programId: pId,
                        periodId: periodId,
                        facilityId: toFacilityId
                    }, function (data) {
                        if(!isUndefined(data.requisitionList) || data.requisitionList !== null){

                            quantityRequested = data.requisitionList;

                            VaccineOrderRequisitionReport.get({facilityId: fId, programId: pId}, function (data) {
                                var stockCards = data.stockCards;
                                //console.log(stockCards);


                                stockCards.forEach(function (s) {
                                    if (s.product.id !== undefined && quantityRequested !==undefined) {

                                        var product = _.filter(programProducts, function (obj) {
                                            if (s.product.primaryName !== undefined) {
                                                return obj.product.primaryName === s.product.primaryName;
                                            }
                                        });

                                        var quantityToRequest = _.filter(quantityRequested, function (obj) {
                                            if (obj.productId !== undefined) {
                                                return obj.productId === s.product.id;
                                            }
                                        });

                                        //console.log(product[0]);

                                        if (quantityToRequest.length > 0 && product[0].productCategory !==undefined) {
                                            s.productCategory = product[0].productCategory;
                                            console.log(product[0]);

                                            s.quantityRequested = quantityToRequest[0].quantityRequested;

                                        }
                                    }
                                });

                                var byCategory = _.groupBy(stockCards, function (s) {
                                    if(!isUndefined(s.productCategory))
                                        return s.productCategory.name;
                                });

                                var stockCardsToDisplay = $.map(byCategory, function (value, index) {
                                    if (index !== 'undefined')
                                        return [{"productCategory": index, "stockCards": value}];
                                });
                                deferred.resolve(stockCardsToDisplay);
                            });

                        } });


                });
            }
            else {
                var stockCardsToDisplay = [];
                VaccineOrderRequisitionReport.get({facilityId: fId, programId: pId}, function (data) {
                    var stockCards = data.stockCards;
                    if (stockCards.length > 0) {
                        stockCardsToDisplay = [{"productCategory": "no-category", "stockCards": stockCards}];
                    }
                    deferred.resolve(stockCardsToDisplay);
                });

            }


        }, 100);
        return deferred.promise;

    }

    return {
        get: get,
    };
});



services.factory('RequisitionForFacility', function ($resource) {
    return $resource('/vaccine/orderRequisition/getAllBy/:programId/:periodId/:facilityId.json', {
        facilityId: '@facilityId',
        programId: '@programId'
    }, {});
});



services.factory('VaccineOrderRequisitionGetReport', function ($resource) {
    return $resource('/vaccine/orderRequisition/get/:id.json', {id: '@id'}, {});
});



services.factory('VaccineOrderRequisitionReport', function ($resource) {
    return $resource('/vaccine/orderRequisition/facilities/:facilityId/programs/:programId/stockCards.json', {
        facilityId: '@facilityId',
        programId: '@programId'
    }, {});
});


services.factory('VaccineOrderRequisitionProgramProduct', function ($resource) {
    return $resource('/vaccine/orderRequisition/:programId.json', {programId: '@programId'}, {});
});



services.factory('StockRequirements', function ($resource) {

    return $resource(
        '/rest-api/facility/:facilityId/program/:programId/stockRequirements.json',
        {facilityId: '@facilityId', programId: '@programId'},
        {
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    return angular.fromJson(data);
                },
                isArray: true //since list property is an array
            }
        }
    );
});