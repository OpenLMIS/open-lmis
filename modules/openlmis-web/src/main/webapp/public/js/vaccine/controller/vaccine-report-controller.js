/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function VaccineReportPOCReportController($scope, GeoZoneFacilityTrees, DiseaseSurveillanceReport, ColdChainReport, AdverseEffectReport){
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

    GeoZoneFacilityTrees.get({}, function(data){
        $scope.facilities = data.geoZoneFacilities;
    });

    DiseaseSurveillanceReport.get({}, function(data){
        $scope.diseaseSurveillance = data.diseaseSurveillance;
    });

    ColdChainReport.get({}, function(data){
       $scope.coldChain = data.coldChain;
    });

    AdverseEffectReport.get({}, function(data){
        $scope.adverseEffect = data.adverseEffect;
    });

}

function expandCollapseToggle(element) {
    $(element).parents('.accordion-section').siblings('.accordion-section').each(function () {
        $(this).find('.accordion-body').slideUp();
        $(this).find('.accordion-heading b').text('+');
    });
    $(element).siblings('.accordion-body').stop().slideToggle(function () {
        if ($(element).siblings('.accordion-body').is(':visible')) {
            $(element).find('b').text('-');
        } else {
            $(element).find('b').text('+');
        }
    });
    var offset = $(element).offset();
    var offsetTop = offset ? offset.top : undefined;
    $('body, html').animate({
        scrollTop: utils.parseIntWithBaseTen(offsetTop) + 'px'
    });
}
