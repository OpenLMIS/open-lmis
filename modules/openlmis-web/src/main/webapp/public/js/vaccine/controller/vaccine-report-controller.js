/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


function VaccineReportPOCReportController($scope, VaccineMonthlyReport) {


    $scope.OnFilterChanged = function() {
        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;

        if($scope.filter.period !== null && $scope.filter.period !== 0 &&
            $scope.filter.facility !== null && $scope.filter.facility !== 0
        ){
            VaccineMonthlyReport.get($scope.filter, function(data){
                $scope.data = data.vaccineData;

                if($scope.data !== null){
                    $scope.diseaseSurveillance = $scope.data.diseaseSurveillance;
                    $scope.coldChain = $scope.data.coldChain;
                    $scope.adverseEffect = $scope.data.adverseEffect;
                    $scope.vaccineCoverage = $scope.data.vaccineCoverage;
                    $scope.immunizationSession = $scope.data.immunizationSession;
                    $scope.vaccination = $scope.data.vaccination;
                    $scope.syringes = $scope.data.syringes;
                    $scope.vitamins = $scope.data.vitamins;
                }
            });

          /*  ColdChainReport.get($scope.filter, function(data){
                $scope.coldChain = data.coldChain;
            });

            AdverseEffectReport.get($scope.filter, function(data){
                $scope.adverseEffect = data.adverseEffect;
            });

            VaccineCoverageReport.get($scope.filter, function(data){
                $scope.vaccineCoverage = data.vaccineCoverage;
            });
            $scope.immunizationSession = null;
            ImmunizationSessionReport.get({}, function(data){
               $scope.immunizationSession = data.immunizationSession;
            });*/
        }

    };

}
