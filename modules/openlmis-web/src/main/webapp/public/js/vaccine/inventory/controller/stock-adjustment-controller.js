/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function StockAdjustmentController($scope, $timeout,$window,$routeParams,programs,StockCardsByCategory,productsConfiguration,StockEvent,localStorageService,homeFacility,adjustmentTypes,UserFacilityList) {

    //Get Home Facility
    $scope.currentStockLot = undefined;
    $scope.adjustmentReasonsDialogModal = false;
    $scope.userPrograms=programs;
    $scope.adjustmentTypes=adjustmentTypes;
    $scope.adjustmentReason={};
    $scope.vvmStatuses=[{"value":1,"name":" 1 "},{"value":2,"name":" 2 "}];
    $scope.productsConfiguration=productsConfiguration;
    var AdjustmentReasons=[];

    var loadStockCards=function(programId, facilityId){
            StockCardsByCategory.get(programId,facilityId).then(function(data){
                $scope.stockCardsToDisplay=data;
            });
        };
    if(homeFacility){
            $scope.homeFacility = homeFacility;
            $scope.homeFacilityId=homeFacility.id;
            $scope.selectedFacilityId=homeFacility.id;
            $scope.facilityDisplayName=homeFacility.name;
            }
    if($scope.userPrograms.length > 1)
    {
                $scope.showPrograms=true;
                //TODO: load stock cards on program change
                $scope.selectedProgramId=$scope.userPrograms[0].id;
                loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.selectedFacilityId,10));
     }
    else if($scope.userPrograms.length === 1){
                $scope.showPrograms=false;
                $scope.selectedProgramId=$scope.userPrograms[0].id;
                loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.selectedFacilityId,10));
    }

    $scope.date = new Date();
    $scope.apply=function(){
        $scope.$apply();
    };

    $scope.showAdjustmentReason=function(lot)
    {

       $scope.oldAdjustmentReason = angular.copy(lot.AdjustmentReasons);
       $scope.currentStockLot = lot;
       $scope.currentStockLot.adjustmentReasons=((lot.adjustmentReasons === undefined)?[]:lot.adjustmentReasons);
       //Remove reason already exist from drop down
       reEvaluateTotalAdjustmentReasons();
       updateAdjustmentReasonForLot(lot.adjustmentReasons);
       $scope.adjustmentReasonsDialogModal = true;

    };
    $scope.removeAdjustmentReason=function(adjustment)
    {
        console.log(adjustment);
        $scope.currentStockLot.adjustmentReasons = $.grep($scope.currentStockLot.adjustmentReasons, function (reasonObj) {
              return (adjustment !== reasonObj);
            });
        updateAdjustmentReasonForLot($scope.currentStockLot.adjustmentReasons);
        reEvaluateTotalAdjustmentReasons();
    };

    $scope.closeModal=function(){
        $scope.currentStockLot.adjustmentReasons = $scope.oldAdjustmentReason;
        reEvaluateTotalAdjustmentReasons();
        $scope.adjustmentReasonsDialogModal=false;
    };
    //Save Adjustment
     $scope.saveAdjustmentReasons = function () {
        $scope.modalError = '';
        $scope.clearAndCloseAdjustmentModal();
      };
     $scope.clearAndCloseAdjustmentModal = function () {
         reEvaluateTotalAdjustmentReasons();
         $scope.adjustmentReason = undefined;
         $scope.adjustmentReasonsDialogModal=false;

       };

     $scope.addAdjustmentReason=function(newAdjustmentReason)
     {
         var adjustmentReason={};
         adjustmentReason.type = newAdjustmentReason.type;
         adjustmentReason.name = newAdjustmentReason.type.name;
         adjustmentReason.quantity= newAdjustmentReason.quantity;

         $scope.currentStockLot.adjustmentReasons.push(adjustmentReason);
         updateAdjustmentReasonForLot($scope.currentStockLot.adjustmentReasons);
         reEvaluateTotalAdjustmentReasons();
         newAdjustmentReason.type = undefined;
         newAdjustmentReason.quantity = undefined;

     };
     $scope.updateStock=function(){
            var events=[];
            $scope.stockCardsToDisplay.forEach(function(st){
                st.stockCards.forEach(function(s){
                 console.log(JSON.stringify(s));
                    if(s.lotsOnHand !==undefined && s.lotsOnHand.length>0){
                        s.lotsOnHand.forEach(function(l){
                            if(l.quantity !== undefined)
                            {
                                    l.adjustmentReasons.forEach(function(reason){
                                        var event={};
                                        event.type= "ADJUSTMENT";
                                        event.productCode=s.product.code;
                                        event.quantity=reason.quantity;
                                        event.lotId=l.lot.id;
                                        event.reasonName=reason.name;
                                        events.push(event);
                                    });
                            }
                        });
                    }
                    else{
                     if(s.quantity !==undefined && s.quantity >0)
                     {
                        s.adjustmentReasons.forEach(function(reason){
                            var event={};
                            event.type= "ADJUSTMENT";
                            event.productCode=s.product.code;
                            event.quantity=reason.quantity;
                            event.reasonName=reason.name;
                            events.push(event);
                        });
                     }
                    }
                });
            });
            console.log(JSON.stringify(events));
           StockEvent.save({facilityId:homeFacility.id},events, function (data) {
               if(data.success !==null)
               {
                     $scope.message=data.success;
                     $timeout(function(){
                       $window.location='/public/pages/vaccine/inventory/dashboard/index.html#/stock-on-hand';
                     },900);
               }
            });
     };
     $scope.cancel=function(){
        $window.location='/public/pages/vaccine/inventory/dashboard/index.html#/stock-on-hand';
     };


      function reEvaluateTotalAdjustmentReasons()
      {
             var totalAdjustments = 0;
             $($scope.currentStockLot.adjustmentReasons).each(function (index, adjustmentObject) {
               if(adjustmentObject.type.additive)
               {
                    totalAdjustments = totalAdjustments + parseInt(adjustmentObject.quantity,10);
               }else{
                    totalAdjustments = totalAdjustments - parseInt(adjustmentObject.quantity,10);
               }

             });
             $scope.currentStockLot.totalAdjustments=totalAdjustments;
      }
      $scope.reEvaluateTotalAdjustmentReasons= function() {reEvaluateTotalAdjustmentReasons();};

     function updateAdjustmentReasonForLot(adjustmentReasons)
     {

         var adjustmentReasonsForLot = _.pluck(_.pluck(adjustmentReasons, 'type'), 'name');
         $scope.adjustmentReasonsToDisplay = $.grep($scope.adjustmentTypes, function (adjustmentTypeObject) {
              return $.inArray(adjustmentTypeObject.name, adjustmentReasonsForLot) == -1;
          });
     }

     //Load Right to check if user level can Send Requisition ond do stock adjustment
          $scope.loadRights = function () {
                 $scope.rights = localStorageService.get(localStorageKeys.RIGHT);
               }();

          $scope.hasPermission = function (permission) {
                 if ($scope.rights !== undefined && $scope.rights !== null) {
                   var rights = JSON.parse($scope.rights);
                   var rightNames = _.pluck(rights, 'name');
                   return rightNames.indexOf(permission) > -1;
                 }
                 return false;
           };
          $scope.vvmTracked=function(c)
          {
             var config=_.filter(productsConfiguration, function(obj) {
                   return obj.product.id===c.product.id;
             });

             if(config.length >0)
             {
                return config[0].vvmTracked;
             }
             else{
                return false;
             }
          };

}
StockAdjustmentController.resolve = {

        homeFacility: function ($q, $timeout,UserFacilityList) {
            var deferred = $q.defer();
            var homeFacility={};

            $timeout(function () {
                   //Home Facility
                   UserFacilityList.get({}, function (data) {
                           homeFacility = data.facilityList[0];
                           deferred.resolve(homeFacility);
                   });

            }, 100);
            return deferred.promise;
         },
         adjustmentTypes: function ($q, $timeout,VaccineAdjustmentReasons ) {
                     var deferred = $q.defer();

                     $timeout(function () {
                              //Load Adjustment reasons
                              VaccineAdjustmentReasons.get({},function(data){
                                     deferred.resolve(data.adjustmentReasons);
                              });
                     }, 100);
                     return deferred.promise;
        },
        programs:function ($q, $timeout, VaccineInventoryPrograms) {
                    var deferred = $q.defer();
                    var programs={};

                    $timeout(function () {
                             VaccineInventoryPrograms.get({},function(data){
                               programs=data.programs;
                                deferred.resolve(programs);
                             });
                    }, 100);
                    return deferred.promise;
        },

        productsConfiguration:function($q, $timeout, VaccineInventoryPrograms,VaccineInventoryConfigurations) {
                     var deferred = $q.defer();
                     var configurations={};
                     $timeout(function () {
                        VaccineInventoryConfigurations.get(function(data)
                        {
                              configurations=data.Configurations;
                              deferred.resolve(configurations);
                        });
                     }, 100);
                     return deferred.promise;
                }


};