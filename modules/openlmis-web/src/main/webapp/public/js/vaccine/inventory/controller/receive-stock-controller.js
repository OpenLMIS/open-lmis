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


function ReceiveStockController($scope,programs,$timeout,homeFacility,VaccineProgramProducts,productsConfiguration, ProductLots,SaveVaccineInventoryReceived,localStorageService,$location, $anchorScroll) {

    $scope.userPrograms=programs;
    $scope.facilityDisplayName=homeFacility.name;
    $scope.selectedProgramId=null;
    $scope.receivedProducts=[];
    $scope.productToAdd={};
    $scope.productToAdd.lots=[];
    $scope.lotToAdd={};
    $scope.vvmStatuses=[{"value":1,"name":" 1 "},{"value":2,"name":" 2 "}];
    $scope.productsConfiguration=productsConfiguration;
    $scope.loadProducts=function(programId){
        VaccineProgramProducts.get({programId:programId},function(data){
            $scope.allProducts=data.programProductList;
            $scope.productsToDisplay=$scope.allProducts;
        });
    };
    $scope.loadProductLots=function(product)
    {
         $scope.lotsToDisplay=null;


         if(product !==null)
         {
             var id=product.id;
             config=_.filter(productsConfiguration, function(obj) {
                   return obj.product.id===id;
             });
             if(config.length > 0)
             {
               $scope.productToAdd.batchTracked=config[0].batchTracked;
               $scope.productToAdd.vvmTracked=config[0].vvmTracked;
             }
             else if(config.length ===0){
                $scope.productToAdd.batchTracked=true;
                $scope.productToAdd.vvmTracked=false;
             }
             if($scope.productToAdd.batchTracked)
             {
                ProductLots.get({productId:product.id},function(data){
                     $scope.allLots=data.lots;
                     $scope.lotsToDisplay=$scope.allLots;
                });
             }

         }
    };
    $scope.submit=function()
    {
        var transaction={};
        transaction.transactionList=[];
        $scope.receivedProducts.forEach(function(s){
            var list={};
            list.productId=s.product.id;
            list.quantity=s.quantity;
            if(s.lots !==undefined && s.lots.length)
            {
                list.lots=[];
                s.lots.forEach(function(l){
                    var lot={};
                    lot.lotId=l.lot.id;
                    lot.quantity=l.quantity;
                    lot.vvmStatus=l.vvmStatus;
                 //   lot.adjustmentReasons=l.adjustmentReasons;
                    list.lots.push(lot);
                });
            }
            transaction.transactionList.push(list);
    });

    SaveVaccineInventoryReceived.update(transaction,function(data)
        {
             if(data.success !==null)
             {
                  $scope.message=data.success;
                  $timeout(function(){
                     $location.path('/stock-on-hand');
                  },100);
              }

        });
    };
    $scope.cancel=function(){
       $location.path('/stock-on-hand');
    };
    if($scope.userPrograms.length > 1)
    {
           $scope.showPrograms=true;
           //TODO: load stock cards on program change
           $scope.loadProducts($scope.userPrograms[0].id);
    }
     else if($scope.userPrograms.length === 1){
          $scope.showPrograms=false;
          $scope.loadProducts($scope.userPrograms[0].id);
     }

    $scope.removeProduct=function(product)
    {
         var index = $scope.receivedProducts.indexOf(product);
         $scope.receivedProducts.splice(index, 1);
         updateProductToDisplay($scope.receivedProducts);
    };

    $scope.addProduct=function(productToAdd){
        $scope.receivedProducts.push(productToAdd);
        $scope.productToAdd={};
        $scope.productToAdd.lots=[];
        updateProductToDisplay($scope.receivedProducts);
        $location.hash('scroll-to-lot');
        $anchorScroll();
    };
    $scope.addLot=function(lotToAdd){
            $scope.productToAdd.lots.push(lotToAdd);
            $scope.lotToAdd={};
            updateLotsToDisplay($scope.productToAdd.lots);
            $location.hash('scroll-to-lot');
            $anchorScroll();
    };

    $scope.removeProductLot=function(lot){
            var index = $scope.productToAdd.lots.indexOf(lot);
            $scope.productToAdd.lots.splice(index, 1);
            updateLotsToDisplay($scope.productToAdd.lots);
    };


    $scope.removeReceivedLot=function(product,lot)
    {
            if(product.lots.length ===1)
            {
                $scope.removeProduct(product);
            }
            else{
                 var productIndex = $scope.receivedProducts.indexOf(product);
                 var lotIndex = $scope.receivedProducts[productIndex].lots.indexOf(lot);
                 $scope.receivedProducts[productIndex].lots.splice(lotIndex, 1);
            }
     };


    function updateProductToDisplay(receivedProducts)
    {
             var toExclude = _.pluck(_.pluck(receivedProducts, 'product'), 'primaryName');
             $scope.productsToDisplay = $.grep($scope.allProducts, function (productObject) {
                  return $.inArray(productObject.product.primaryName, toExclude) == -1;
              });
    }

    function updateLotsToDisplay(lotsToAdd)
    {
             var toExclude = _.pluck(_.pluck(lotsToAdd, 'lot'), 'lotCode');
             $scope.lotsToDisplay = $.grep($scope.allLots, function (lotObject) {
                   return $.inArray(lotObject.lotCode, toExclude) == -1;
             });
    }



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

     $scope.sumLots = function(lots) {
            var total=0;
            angular.forEach(lots , function(lot){
              total+= parseInt(lot.quantity,10);
            });
            return total;
     };

}
ReceiveStockController.resolve = {

        homeFacility: function ($q, $timeout,UserFacilityList) {
            var deferred = $q.defer();
            var homeFacility={};

            $timeout(function () {
                   UserFacilityList.get({}, function (data) {
                           homeFacility = data.facilityList[0];
                           deferred.resolve(homeFacility);
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