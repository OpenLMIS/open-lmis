/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function VaccineReportPOCReportController($scope, DiseaseSurveillanceReport, ColdChainReport, AdverseEffectReport){
    $scope.vaccineData = [
        {name:'Vaccine A'},{name:'Vaccine B'},{name: 'Vaccine C'}
    ];

    $scope.colorify = function(value){
        if(!isUndefined(value)){
            if(value >= 95) return 'green';
            if(value < 0) return 'blue';
            if(value <= 50) return 'red';
        }

    };

    DiseaseSurveillanceReport.get({}, function(data){
        $scope.diseaseSurveillance = data.diseaseSurveillance;
    });

    ColdChainReport.get({}, function(data){
       $scope.coldChain = data.coldChain;
    });

    AdverseEffectReport.get({}, function(data){
        $scope.adverseEffect = data.adverseEffect;
    });

    $scope.OnFilterChanged = function() {
        // clear old data if there was any
        $scope.data = $scope.datarows = [];
       // $scope.filter.max = 10000;
        
    };


}
