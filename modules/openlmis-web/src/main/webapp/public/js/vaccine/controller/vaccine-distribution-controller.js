/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
function VaccineDistributionConroller($scope,VaccineDistributionBatches){
   /* VaccineDistributionBatches.get({},function(data){
        $scope.distributionBatches  = data.distributionBatches;
       // alert('distributions = '+JSON.stringify($scope.distributionBatches));

    });*/
    $scope.distribution = {batchId:2}

    VaccineDistributionBatches.save({},$scope.distribution,function(successMessage){
        alert('save successful');

    }, function(errorMessage){
        alert('save error');
    });

}
