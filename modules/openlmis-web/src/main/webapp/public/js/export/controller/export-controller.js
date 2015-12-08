/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function ExportController($scope, $http) {
    var oneDay = 1000 * 60 * 60 * 24;
    //urls = {'facilities':'http://localhost:5555/cube/facilities/facts?format=csv',
    //    products :'http://localhost:5555/cube/requisition_line_items/members/products?format=csv',
    //    stock :'http://localhost:5555/cube/stock_cards/members/stock?cut=stock:expirationdates&format=csv',
    //    movement :'http://localhost:5555/cube/stock_cards/members/movement?cut=movement:signature&format=csv',
    //    requisition_line_items :'http://localhost:5555/cube/requisition_line_items/facts?cut=products:ESS_MEDS',
    //    requisition_line_items1 :'http://localhost:5555/cube/requisition_line_items/facts?cut=products:ESS_MEDS'};

     urls = {index :'index.html', index1 :'index.html'};

    $scope.setEndDateOffset = function () {
        if ($scope.endDate < $scope.startDate) {
            $scope.endDate = undefined;
        }
        $scope.endDateOffset = Math.ceil((new Date($scope.startDate.split('-')).getTime() + oneDay - Date.now()) / oneDay);
    };

    $scope.requestAndExportReports = function () {
        $scope.inProgress = true;

        angular.forEach(urls, function(url, reportName) {
            $http({method: 'GET', url: foramtUrlToRequestReport(url)}).
                success(function(data) {
                    exportReport(reportName, data);
                }).
                error(function() {
                    console.log("oops");
                });
        });

        $scope.inProgress = false;
    };

    function exportReport(reportName, file) {
        var anchor = angular.element('<a/>');
        anchor.css({display: 'none'});
        angular.element(document.body).append(anchor);

        anchor.attr({
            href: 'data:attachment/csv' + encodeURI(file),
            target: '_self',
            download: reportName
        })[0].click();

        anchor.remove();
    }

    function foramtUrlToRequestReport(url) {
        //url += "startDate=" + $scope.startDate  + "endDate=" + $scope.endDate;
        return url;
    }
}
