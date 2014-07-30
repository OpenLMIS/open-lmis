/**
 * Created by issa on 4/24/14.
 */
function SendNotificationController($scope,$timeout,$filter,SendNotification,dashboardFiltersHistoryService,UserGeographicZoneTree,programsList,messageService, NotificationAlerts, GetPeriod, formInputValue,FacilitiesForNotifications,userPreferredFilterValues,ReportSchedules, ReportPeriods, OperationYears, ReportPeriodsByScheduleAndYear, ngTableParams) {
    $scope.filterObject = {};

    $scope.formFilter = {};

    $scope.formPanel = {openPanel:true};

    $scope.alertsPanel = {openPanel:true};

    $scope.selectedNotification = { selectAllFacilities: true};

    initialize();

    function initialize() {
        $scope.$parent.currentTab = 'NOTIFICATION';
        $scope.showProductsFilter = false;
        $scope.hideScheduleFilter = true;
        $scope.hideYearFilter = true;
        $scope.hidePeriodFilter = true;
        $scope.showStockStatusFilter = false;
        $scope.showFacilitiesFilter = false;
        $scope.maxSmsText = 160;
    }

    $scope.notificationMethodsChange = function(notification){
        $scope.selectedNotification = notification;
        processNotificationChange($scope.selectedNotification);
    };

    $scope.loadGeoZones = function(){
        UserGeographicZoneTree.get({programId:$scope.formFilter.programId}, function(data){
            $scope.zones = data.zone;
        });
    };

    $scope.programs = programsList;
    $scope.programs.unshift({'name': formInputValue.programOptionSelect});

    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift(formInputValue.yearOptionAll);
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name': formInputValue.scheduleOptionSelect});
    });

    $scope.loadFacilities = function(){
        FacilitiesForNotifications.get({zoneId: $scope.filterObject.zoneId}, function(data) {
            $scope.facilities = data.facilities;
            setupTableOption();
            $scope.selectAllFacilities();

        });

    };

    $scope.sendNotifications = function () {
        $scope.errorMessage = '';
        if($scope.validateSendNotification()){
            var receivers = _.map($scope.facilityForNotifications, function(facility){
                return {id: facility.userid,
                         cellPhone: facility.phonenumber,
                         email: facility.email,
                         primaryNotificationMethod: facility.primarynotificationmethod};
            });

            var notification = {
                emailMessage:  $scope.emailTemplate,
                smsMessage : $scope.smsTemplate,
                receivers : receivers,
                notificationMethods :$scope.notificationMethods
            };

            SendNotification.save({},notification, function(data){
                $scope.error = "";
                $scope.successMessage = messageService.get(data.success);
            });
            return;
        }
    };
    $scope.$watch('smsTemplate', function(){
        if($scope.smsTemplate.length > $scope.maxSmsText){
            $scope.smsTemplate = $scope.smsTemplate.substring(0,$scope.maxSmsText);
        }

    });


    var processNotificationChange = function(selectedNotification){
        if(!isUndefined(selectedNotification)){

            if(selectedNotification.emailMethod){
                $scope.emailTemplate = selectedNotification.emailMessageTemplate;
            }else{
                $scope.emailTemplate = null;
            }

            if(selectedNotification.smsMethod){
                $scope.smsTemplate = selectedNotification.smsMessageTemplate;
            }else{
                $scope.smsTemplate = null;
            }

        }
    };

    $scope.$watch("formFilter.selectedAlert", function(newVal, oldVal){

        if(!isUndefined(oldVal)){
            oldVal.emailMethod = null;
            oldVal.smsMethod = null;
        }
        $scope.selectedNotification = $scope.formFilter.selectedAlert;
        processNotificationChange($scope.selectedNotification);

    });

    $scope.$watch("errorMessage", function (errorMsg) {
        $timeout(function () {
            if (errorMsg) {
                document.getElementById('sendSuccessMsgDiv').scrollIntoView();
            }
        });
    });

    $scope.facilitySelectChange = function(selected){
        if(!selected && $scope.formFilter.selectAll){
            $scope.formFilter.selectAll = false;
        }
    };

    $scope.validateSendNotification = function () {
        $scope.notificationMethods = [];

        if(isUndefined($scope.selectedNotification) || isUndefined($scope.selectedNotification.alertType)){
            $scope.errorMessage = messageService.get('errorMessage.send.notification.select.notification.type');
            return false;
        }
        if(isUndefined($scope.selectedNotification.emailMethod) && isUndefined($scope.selectedNotification.smsMethod)){
            $scope.errorMessage = messageService.get('errorMessage.send.notification.select.notification.method');
            return false;
        }
        if(!isUndefined($scope.selectedNotification.emailMethod) && $scope.selectedNotification.emailMethod === true){
            $scope.notificationMethods.push("email");

        }
        if(!isUndefined($scope.selectedNotification.smsMethod) && $scope.selectedNotification.smsMethod === true){
            $scope.notificationMethods.push("sms");
        }
            $scope.facilityForNotifications = [];
        angular.forEach($scope.facilities, function(itm,idx){
            if(itm.selected){
                $scope.facilityForNotifications.push(itm);
            }
        });

        if(isUndefined($scope.facilityForNotifications) || $scope.facilityForNotifications.length === 0){
            $scope.errorMessage = messageService.get('errorMessage.send.notification.select.facilities');
            return false;
        }

        if($scope.selectedNotification.emailMethod && isUndefined($scope.emailTemplate)){
            $scope.errorMessage = messageService.get('errorMessage.send.notification.empty.email.message');
            return false;
        }
        if($scope.selectedNotification.smsMethod && isUndefined($scope.smsTemplate)){
            $scope.errorMessage = messageService.get('errorMessage.send.notification.empty.sms.message');
            return false;
        }

        return true;
    };

    $scope.$watch('formFilter.facilityId', function (selection) {
        $scope.filterObject.facilityId = $scope.formFilter.facilityId;
    });

    NotificationAlerts.get({},function(data){
        $scope.notifications = data.notifications;
    });

    $scope.filterProductsByProgram = function (){
        $scope.loadGeoZones();
        if(isUndefined($scope.formFilter.programId)){
            return;
        }
        $scope.filterObject.programId = $scope.formFilter.programId;

    };

    $scope.processPeriodFilter = function (){
        if ( $scope.formFilter.periodId == "All") {
            $scope.filterObject.periodId = -1;
        } else if ($scope.formFilter.periodId !== undefined || $scope.formFilter.periodId === "") {
            $scope.filterObject.periodId = $scope.formFilter.periodId;
            $.each($scope.periods, function (item, idx) {
                if (idx.id == $scope.formFilter.periodId) {
                    $scope.filterObject.period = idx.name;
                }
            });
        } else {
            $scope.filterObject.periodId = 0;
        }
    };

    $scope.changeSchedule = function(){

        if (!isUndefined($scope.formFilter.scheduleId)) {
            $scope.filterObject.scheduleId = $scope.formFilter.scheduleId;
        }

        if(!isUndefined($scope.filterObject.scheduleId) ){
            if(!isUndefined($scope.filterObject.year) ){
                ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filterObject.scheduleId, year: $scope.filterObject.year}, function(data){
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name':formInputValue.periodOptionSelect});
                });
            }else{
                ReportPeriods.get({ scheduleId : $scope.filterObject.scheduleId },function(data) {
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name': formInputValue.periodOptionSelect});

                });
            }
        }

        $scope.loadFacilities();

    };

    $scope.changeScheduleByYear = function (){

        if (!isUndefined($scope.formFilter.year)) {
            $scope.filterObject.year = $scope.formFilter.year;

        }
        $scope.changeSchedule();

    };
    $scope.processZoneFilter = function(){
        $scope.filterObject.zoneId = $scope.formFilter.zoneId;
        $scope.loadFacilities();
    };

    $scope.selectAllFacilities = function(){
        if($scope.formFilter.selectAll === true){
            if(!isUndefined($scope.facilities)){
                var markAllFacilities = _.map($scope.facilities,function(facility){
                    facility.selected = (facility.email || facility.phonenumber) ? true : false;
                    return facility;
                });
                $scope.facilities = markAllFacilities;
            }
        }else{
            if(!isUndefined($scope.facilities)){
                var unmarkAllFacilities = _.map($scope.facilities,function(facility){
                    facility.selected = false;
                    return facility;
                });
                $scope.facilities = unmarkAllFacilities;
            }
        }
    };


    $scope.$on('$viewContentLoaded', function () {
        var filterHistory = dashboardFiltersHistoryService.get($scope.$parent.currentTab);

        if(isUndefined(filterHistory)){
            if(!_.isEmpty(userPreferredFilterValues)){
                var date = new Date();

                $scope.filterObject.programId = isItemWithIdExists(userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PROGRAM], $scope.programs) ?
                    userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PROGRAM] : $scope.filterObject.programId;

                $scope.filterObject.periodId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PERIOD];
                if(!isUndefined($scope.filterObject.periodId)){

                    GetPeriod.get({id:$scope.filterObject.periodId}, function(period){
                        if(!isUndefined(period.year)){
                            $scope.filterObject.year = period.year;
                        }else{
                            $scope.filterObject.year = date.getFullYear() - 1;
                        }
                    });
                }
                $scope.filterObject.scheduleId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_SCHEDULE];

                $scope.filterObject.zoneId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_GEOGRAPHIC_ZONE];

                $scope.registerWatches();

                $scope.formFilter = $scope.filterObject;

            }
        }else{

            $scope.registerWatches();
            $scope.formFilter = $scope.filterObject = filterHistory;

        }

    });
    $scope.registerWatches = function(){

        $scope.$watch('formFilter.programId',function(){
            $scope.filterProductsByProgram();

        });

        $scope.$watch('formFilter.scheduleId', function(){
            $scope.changeSchedule();

        });

    };

    $scope.$on('$routeChangeStart', function(){
        var data = {};
        angular.extend(data,$scope.filterObject);
        dashboardFiltersHistoryService.add($scope.$parent.currentTab, data);
    });


    function setupTableOption(){
        // the grid options
        $scope.tableParams = new ngTableParams({
            page: 1,            // show first page
            total: 0,           // length of data
            count: 25           // count per page
        });

        $scope.paramsChanged = function (params) {

            // slice array data on pages
            if ($scope.facilities === undefined) {
                $scope.datarows = [];
            } else {
                var data = $scope.facilities;
                var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
                orderedData = params.sorting ? $filter('orderBy')(orderedData, params.orderBy()) : data;

                params.total = orderedData.length;
                $scope.datarows = orderedData.slice((params.page - 1) * params.count, params.page * params.count);

            }
        };

        // watch for changes of parameters
        $scope.$watch('tableParams', $scope.paramsChanged, true);



    }



}
