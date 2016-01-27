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


function VaccineInventoryConfigurationController($scope,programs,DemographicEstimateCategories,VaccineInventoryConfigurations,VaccineProgramProducts,configurations,SaveVaccineInventoryConfigurations,localStorageService,$location) {

    $scope.userPrograms=programs;
    $scope.configToAdd={};
    $scope.configToAdd.batchTracked=false;
    $scope.configToAdd.vvmTracked=false;
    $scope.configToAdd.survivingInfants = false;

    DemographicEstimateCategories.get({}, function (data) {
        $scope.demographicCategories = data.estimate_categories;
    });

    $scope.loadProducts=function(programId){
        VaccineProgramProducts.get({programId:programId},function(data){
           $scope.allProducts=data.programProductList;
           $scope.loadConfigurations();
        });
    };
    $scope.loadConfigurations=function(){
        VaccineInventoryConfigurations.get(function(data)
        {
           $scope.configurations=data.Configurations;
           updateProductToDisplay($scope.configurations);
        });
    };
    $scope.addConfiguration=function(configToAdd)
    {
        configToAdd.type='PRODUCT';
        console.log(JSON.stringify($scope.configurations[0]));
        console.log(JSON.stringify(configToAdd));

        $scope.configurations.push(configToAdd);
        updateProductToDisplay($scope.configurations);
        $scope.configToAdd={};
        $scope.configToAdd.batchTracked=false;
        $scope.configToAdd.vvmTracked=false;
        $scope.configToAdd.survivingInfants = false;


    };
    $scope.$watch('configurations',function(){
        if($scope.configurations !==undefined)
        {

        }
    });
    $scope.visibleTab = 'PRODUCT';
    $scope.changeTab=function(key){
        $scope.visibleTab=key;
    };

    $scope.saveConfigurations=function()
    {
        SaveVaccineInventoryConfigurations.update($scope.configurations,function(data){
            $scope.configurations=data.Configurations;
            updateProductToDisplay($scope.configurations);
        });
    };

    function updateProductToDisplay(configurationProducts)
    {
         var toExclude = _.pluck(_.pluck(configurationProducts, 'product'), 'primaryName');
         $scope.productsToDisplay = $.grep($scope.allProducts, function (productObject) {
                 return $.inArray(productObject.product.primaryName, toExclude) == -1;
         });
    }

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

}
VaccineInventoryConfigurationController.resolve = {

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

        configurations:function ($q, $timeout, VaccineInventoryConfigurations) {
             var deferred = $q.defer();
             var configurations=[];

            $timeout(function () {
                VaccineInventoryConfigurations.get({},function(data){
                     configurations=data;
                     deferred.resolve(configurations);
                });
            }, 100);
            return deferred.promise;
        }

};