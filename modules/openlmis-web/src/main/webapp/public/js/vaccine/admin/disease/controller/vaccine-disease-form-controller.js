/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
function VaccineDiseaseFormController($scope, disease, SaveVaccineDisease,$location) {

  $scope.disease = disease;

  $scope.save = function(form){
    if(form.$valid){
      SaveVaccineDisease.update($scope.disease, function(){
        $location.path('/disease');
      });
    }
  };

}

VaccineDiseaseFormController.resolve = {
  disease : function($q, $timeout, VaccineDisease, $route){
    if(!$route.current.params.id){
      return {};
    }
    var deferred = $q.defer();
    $timeout(function(){
      VaccineDisease.get({id:$route.current.params.id}, function(data){
        deferred.resolve(data.disease);
      },{});
    }, 100);
    return deferred.promise;
  }
};
