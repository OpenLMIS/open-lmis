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


function StockOnHandController($scope,$window,programs,$location,homeFacility, localStorageService,StockCardsByCategory,Forecast) {

    $scope.selectedProgramId=null;
    $scope.homeFacilityId=homeFacility.id;
    $scope.userPrograms=programs;
    $scope.selectedFacilityId=homeFacility.id;
    $scope.selectedType="0";//My facility selected by default;
    $scope.facilityDisplayName=homeFacility.name;

    $scope.data={"stockcards": null};//Set default chart stock cards data to null;
    $scope.panel = {alerts:false};//Close Alert Accordion by default

    var loadStockCards=function(programId, facilityId){
        StockCardsByCategory.get(programId ,facilityId).then(function(data){
               $scope.stockCardsByCategory=data;
               if( $scope.stockCardsByCategory[0] !== undefined){
                    $scope.data = {"stockcards": $scope.stockCardsByCategory[0].stockCards};
                    $scope.showGraph=true;
               }

        });
    };

    if($scope.userPrograms.length > 1)
    {
            $scope.showPrograms=true;
            //TODO: load stock cards on program change
            $scope.selectedProgramId=$scope.userPrograms[0].id;
    }
    else{
            $scope.showPrograms=false;
            $scope.selectedProgramId=$scope.userPrograms[0].id;
    }

    //Clear and Hide Chart and table when Radio switch from my facility to supervised facility and show vise versa
    $scope.changeFacilityType  = function () {
        //If Select My facility reload data with home facility Id
        if ($scope.selectedType === "0") {
            $scope.showGraph=false;
            $scope.filter.facilityId=null;
            $scope.selectedFacilityId= $scope.homeFacilityId;
            loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.selectedFacilityId,10));

        }
        else if($scope.selectedType === "1")
        {
            //Clear Chart data
            $scope.showGraph=false;
            $location.url($location.path());
            $scope.data={"stockcards": null};
            $scope.filter={};
        }
    };
     //When the filter change reload Data
    $scope.OnFilterChanged = function () {
               //Reload data with Supervised facility ID
            $scope.showGraph=false;
            $scope.data={"stockcards": null};
            if($scope.selectedType === "1")
            {
               $scope.selectedFacilityId = $scope.filter.facilityId;
               console.log($scope.selectedFacilityId);
            }
            if($scope.selectedFacilityId !== "0" && $scope.selectedFacilityId !== undefined)
             {
                loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.selectedFacilityId,10));
             }
     };

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

    $scope.Adjustment=function(){
        $location.path('/stock-adjustment');
    };

     $scope.Requisition=function(){
            $window.location='/public/pages/vaccine/order-requisition/index.html#/order-requisition';
     };
}
StockOnHandController.resolve = {

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
         }
};