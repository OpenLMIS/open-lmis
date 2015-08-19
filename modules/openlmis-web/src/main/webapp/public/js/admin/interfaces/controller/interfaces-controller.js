/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *   Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function InterfacesController($scope, elmisInterfaceData, $timeout, $location, ELMISInterfaceSave, elmisInterfaceList, $route){

    $scope.newDataSet = {};
    $scope.interface = elmisInterfaceData;
    $scope.interfaceList = elmisInterfaceList;


    $scope.formMessage = 'header.interfaces.add.new';
    if($route.current.params.id)
        $scope.formMessage = 'header.interfaces.add.edit';


    $scope.addInterfaceDatasets = function(){
        if ($scope.interfaceDatasetForm.$error.required) {
            $scope.showError = true;
            return;
        }
        if(!$scope.interface.dataSets)
            $scope.interface.dataSets = [];
        $scope.interface.dataSets.push($scope.newDataSet);
        $scope.newDataSet = {};
        $scope.showError = false;
    };

    $scope.deleteInterfaceDatasets = function(index){
        console.log($scope.interface.dataSets[index]);
        $scope.interface.dataSets.splice(index, 1);
    };

    $scope.save = function(){
        if ($scope.interfaceForm.$error.required) {
            console.log($scope);
            $scope.showParentError = true;
            $scope.error = 'form.error';
            return;
        }

        ELMISInterfaceSave.save($scope.interface,  function (data) {
            $scope.message = data.success;
            $timeout(function(){ $scope.message = ""; }, 4000);

        }, function () {});

        $scope.error = false;
    };

    $scope.saveInterfaceDatasets = function(index){
        $scope.interface.dataSets[index].underEdit = false;

    };

    $scope.editInterfaceDatasets = function(index){
        $scope.interface.dataSets[index].previousDataset = angular.copy($scope.interface.dataSets[index]);
        $scope.interface.dataSets[index].underEdit = true;
    };

    $scope.cancleEditInterfaceDatasets = function(index){
        $scope.interface.dataSets[index] = $scope.interface.dataSets[index].previousDataset;
        $scope.interface.dataSets[index].previousDataset = undefined;
        $scope.interface.dataSets[index].underEdit = false;
    };

    $scope.cancel = function(){
        $location.path('');
    };
}

InterfacesController.resolve = {

    elmisInterfaceData : function ($q, $timeout, ELMISInterface, $route) {
        var deferred = $q.defer();
        if($route.current.params.id) {
            $timeout(function () {
                ELMISInterface.getInterface().get({id: $route.current.params.id}, function (data) {
                    deferred.resolve(data.interface);
                }, {});
            }, 100);
            return deferred.promise;
        }
        return deferred.reject("");
    },

    elmisInterfaceList : function ($q, $timeout, ELMISInterface) {
        var deferred = $q.defer();
        $timeout(function () {
            ELMISInterface.getAllinterfaces().get({}, function(data){
                deferred.resolve(data.interfaces);
            }, {});
        }, 100);
        return deferred.promise;
    }
};