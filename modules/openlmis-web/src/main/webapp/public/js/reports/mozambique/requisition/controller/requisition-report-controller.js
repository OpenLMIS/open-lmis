function RequisitionReportController($scope, $filter, RequisitionReportService, messageService, DateFormatService, $window) {

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadRequisitions();
    });
    $scope.selectedItems = [];

    $scope.loadRequisitions = function () {
        var requisitionQueryParameters = {
            startTime: '2015-09-26 00:00:00',
            endTime: $filter('date')(new Date(), 'yyyy-MM-dd HH:mm:ss')
        };

        RequisitionReportService.get(requisitionQueryParameters, function (data) {
            $scope.requisitions = data.rnr_list;
            setInventoryDateAndStubmittedStatus();
        }, function () {
        });
    };

    var setInventoryDateAndStubmittedStatus = function () {
        _.each($scope.requisitions, function (rnr) {
            if (rnr.actualPeriodEnd === null) {
                rnr.actualPeriodEnd = rnr.schedulePeriodEnd;
            }

            if (rnr.clientSubmittedTime !== null) {
                var FIVE_DAYS = 5 * 24 * 60 * 60 * 1000;
                if (rnr.clientSubmittedTime <= rnr.schedulePeriodEnd + FIVE_DAYS) {
                    rnr.submittedStatus = messageService.get("rnr.report.submitted.status.ontime");
                } else {
                    rnr.submittedStatus = messageService.get("rnr.report.submitted.status.late");
                }
            }

            rnr.inventoryDate = formatDate(rnr.actualPeriodEnd);
        });
    };

    var formatDate = function (date) {
        return DateFormatService.formatDateWithLocale(date);
    };
    
    $scope.submitStatusClass = function(status) {
        var cellTemplateClass = "customCell";
        var lateStatusClass = "submitStatusLate";
        return messageService.get("rnr.report.submitted.status.late") === status ? lateStatusClass : cellTemplateClass;
    };

    function redirectPage() {
        var url = "";
        var urlMapping = {
            "VIA": "/public/pages/logistics/rnr/index.html#/view-requisition-via/",
            "ESS_MEDS": "/public/pages/logistics/rnr/index.html#/view-requisition-via/",
            "MMIA": "/public/pages/logistics/rnr/index.html#/view-requisition-mmia/"
        };

        var selectedItem = $scope.selectedItems[0];
        if (selectedItem.programName) {
            url = urlMapping[selectedItem.programName];
        }
        url += selectedItem.id + "?supplyType=fullSupply&page=1";
        $window.location.href = url;
    }

    $scope.rnrListGrid = {
        data: 'requisitions',
        displayFooter: false,
        multiSelect: false,
        selectedItems: $scope.selectedItems,
        afterSelectionChange: redirectPage,
        displaySelectionCheckbox: false,
        enableColumnResize: true,
        showColumnMenu: false,
        showFilter: false,
        enableSorting: true,
        plugins: [new ngGridFlexibleHeightPlugin()],
        sortInfo: {fields: ['webSubmittedTimeString'], directions: ['desc']},
        columnDefs: [
            {displayName: 'number', cellTemplate: '<div class="customCell">{{$parent.$index + 1}}</div>', sortable: false },
            {field: 'programName', displayName: messageService.get("program.header")},
            {field: 'type', displayName: 'Type'},
            {field: 'facilityName', displayName: messageService.get("option.value.facility.name"),width:200},
            {field: 'submittedUser', displayName: 'Submitted User'},
            {field: 'inventoryDate', displayName: 'Inventory Date'},
            {field: 'submittedStatus', displayName: 'Submitted Status',cellTemplate:'<div ng-class="submitStatusClass(\'{{row.getProperty(col.field)}}\')">{{row.getProperty(col.field)}}</div>'},
            {field: 'clientSubmittedTimeString', displayName: 'Submitted Time'},
            {field: 'webSubmittedTimeString', displayName: 'Sync Time'}
        ]
    };
}