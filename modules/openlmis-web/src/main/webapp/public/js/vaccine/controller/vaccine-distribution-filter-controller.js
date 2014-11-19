/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function VaccineDistributionFilterController($scope,FilterDistributionBatches,GetFacilityCompleteList,Manufacturers,DistributionTypes,Products){

    $scope.distributionBatch = {};
    $scope.formFilter = {};

    $scope.origins = [{id:0,name:'France'},{id:1,name:'USA'}];

    DistributionTypes.get({}, function(data){
        $scope.distributionTypes = data.distributionTypes;
    });
    Manufacturers.get({}, function (data) {
        $scope.manufacturers = data.manufacturers;
    });

    Products.get({}, function(data){
        $scope.products = data.productList;
    });

    GetFacilityCompleteList.get({},function(data){
        $scope.allFacilities = data.allFacilities;
        $scope.allFacilities.unshift({name:'-- Select Facility --'});
    });


    $scope.convertStringToCorrectDateFormat = function(stringDate) {
        if (stringDate) {
            return stringDate.split("-").reverse().join("-");
        }
        return null;
    };


    $scope.searchDistributionBatch = function () {

        alert('searching for '+JSON.stringify($scope.formFilter));

    };



}
