    /*
     * This program is part of the OpenLMIS logistics management information system platform software.
     * Copyright © 2013 VillageReach
     *
     * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
     *  
     * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
     * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
     */
    function ViewOrderRequisitionList($scope,programs,$window,$rootScope,facility,VaccineOrderRequisitionsForViewing,VaccineOrderRequisitionLastReport, facilities, RequisitionsForViewing, ProgramsToViewVaccineOrderRequisitions, $location, messageService, navigateBackService) {

        $scope.facilities = facilities;
        $scope.programs = programs;


        $scope.facilityLabel = (!$scope.facilities.length) ? messageService.get("label.none.assigned") : messageService.get("label.select.facility");
        $scope.programLabel = messageService.get("label.none.assigned");
        $scope.selectedItems = [];


        $scope.loadRequisitions = function () {
            if ($scope.viewRequisitionForm && $scope.viewRequisitionForm.$invalid) {
                $scope.errorShown = true;
                return;
            }
            var requisitionQueryParameters = {
                facilityId: $scope.selectedFacilityId,
                dateRangeStart: $scope.startDate,
                dateRangeEnd: $scope.endDate
            };

            if ($scope.selectedProgramId) requisitionQueryParameters.programId = $scope.selectedProgramId;

            VaccineOrderRequisitionsForViewing.get(requisitionQueryParameters, function (data) {
                 console.log(data.search);
                $scope.requisitions = $scope.filteredRequisitions = data.search;

                setRequisitionsFoundMessage();
            }, function () {
            });
        };

        $scope.selectedFacilityId = navigateBackService.facilityId;
        $scope.startDate = navigateBackService.dateRangeStart;
        $scope.endDate = navigateBackService.dateRangeEnd;
        $scope.programs = navigateBackService.programs;

        if (navigateBackService.programId) {
            $scope.selectedProgramId = navigateBackService.programId;
            $scope.program = _.findWhere($scope.programs, {id: utils.parseIntWithBaseTen($scope.selectedProgramId)});
            setOptions();
        }
        if ($scope.selectedFacilityId && $scope.startDate && $scope.endDate) {
            $scope.loadRequisitions();
        }

        var selectionFunc = function () {
            $scope.$parent.Status = $scope.selectedItems[0].status;
            $rootScope.viewOrder = true;
            console.log($scope.selectedItems[0].id);
            $scope.openRequisition();
        };


        $scope.rnrListGrid = { data: 'filteredRequisitions',
            displayFooter: false,
            multiSelect: false,
            selectedItems: $scope.selectedItems,
            afterSelectionChange: selectionFunc,
            displaySelectionCheckbox: false,
            enableColumnResize: true,
            showColumnMenu: false,
            showFilter: false,
            enableSorting: true,
            sortInfo: { fields: ['orderDate'], directions: ['asc'] },
            columnDefs: [
                {field: 'programName', displayName: messageService.get("program.header") },
                {field: 'facilityName', displayName: messageService.get("option.value.facility.name")},
                {field: 'periodStartDate', displayName: messageService.get("label.period.start.date"), cellFilter: 'date:\'dd-MM-yyyy\''},
                {field: 'periodEndDate', displayName: messageService.get("label.period.end.date"), cellFilter: 'date:\'dd-MM-yyyy\''},
                {field: 'orderDate', displayName: messageService.get("label.date.submitted"), cellFilter: 'date:\'dd-MM-yyyy\''},
                {field: 'status', displayName: messageService.get("label.status")},
                {field: 'emergency', displayName: messageService.get("requisition.type.emergency"),
                    cellTemplate: '<div id="emergency{{$parent.$index}}" class="ngCellText checked"><i ng-class="{\'icon-ok\': row.entity.emergency}"></i></div>',
                    width: 110 }
            ]
        };

        $scope.openRequisition = function () {
            var data = {
                facilityId: $scope.selectedFacilityId,
                dateRangeStart: $scope.startDate,
                dateRangeEnd: $scope.endDate,
                programs: $scope.programs
            };
            if ($scope.selectedProgramId) data.programId = $scope.selectedProgramId;
            navigateBackService.setData(data);

            $window.location = '/public/pages/vaccine/order-requisition/index.html#/create/'+parseInt($scope.selectedItems[0].id,10);
        };

        function setProgramsLabel() {
            $scope.selectedProgramId = undefined;
            $scope.programLabel = (!$scope.programs.length) ? messageService.get("label.none.assigned") : messageService.get("label.all");
        }

        function setOptions() {
            $scope.options = ($scope.programs.length) ? [
                {field: "All", name: "All"}
            ] : [];
        }

        $scope.loadProgramsForFacility = function () {
            ProgramsToViewVaccineOrderRequisitions.get({facilityId: $scope.selectedFacilityId},
                function (data) {
                    $scope.programs = data.programList;
                    setOptions();
                    setProgramsLabel();
                }, function () {
                    $scope.programs = [];
                    setProgramsLabel();
                });
        };

        function setRequisitionsFoundMessage() {
            $scope.requisitionFoundMessage = ($scope.requisitions.length) ? "" : messageService.get("msg.no.rnr.found");
        }

        $scope.filterRequisitions = function () {
            $scope.filteredRequisitions = [];
            var query = $scope.query || "";

            $scope.filteredRequisitions = $.grep($scope.requisitions, function (rnr) {
                return contains(rnr.requisitionStatus, query);
            });

        };

        function contains(string, query) {
            return string.toLowerCase().indexOf(query.toLowerCase()) != -1;
        }

        $scope.setEndDateOffset = function () {
            if ($scope.endDate < $scope.startDate) {
                $scope.endDate = undefined;
            }
            $scope.endDateOffset = Math.ceil((new Date($scope.startDate.split('-')).getTime() + oneDay - Date.now()) / oneDay);
        };
        
    }

    var oneDay = 1000 * 60 * 60 * 24;

ViewOrderRequisitionList.resolve = {

    facilities: function ($q, $timeout, UserFacilityWithViewVaccineOrderRequisition) {
        var deferred = $q.defer();
        $timeout(function () {
            UserFacilityWithViewVaccineOrderRequisition.get({}, function (data) {
                deferred.resolve(data.facilities);
            }, {});
        }, 100);
        return deferred.promise;
    },
    programs: function ($q, $timeout, VaccineHomeFacilityPrograms) {
        var deferred = $q.defer();

        $timeout(function () {
            VaccineHomeFacilityPrograms.get({}, function (data) {
                deferred.resolve(data.programs);
            });
        }, 100);

        return deferred.promise;
    },
    facility: function ($q, $timeout, UserHomeFacility) {
        var deferred = $q.defer();

        $timeout(function () {
            UserHomeFacility.get({}, function (data) {
                deferred.resolve(data.homeFacility);
            });
        }, 100);

        return deferred.promise;
    }
};