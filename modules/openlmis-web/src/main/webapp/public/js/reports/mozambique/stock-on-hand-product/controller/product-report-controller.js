function ProductReportController(type) {
    return function ($scope, $filter, ProductReportService,GeographicZoneService,FacilityService, $dialog) {

        $scope.provinces = [];
        $scope.districts = [];
        $scope.facilities = [];
        $scope.fullGeoZoneList = [];
        $scope.multiProducts = [];
        $scope.reportParams = {};

        $scope.todayDateString = $filter('date')(new Date(), "yyyy-MM-dd");

        $scope.multiSelectionSettings = {
            displayProp: "primaryName",
            idProp: "code",
            externalIdProp: "code",
            enableSearch: true,
            scrollable : true,
            scrollableHeight: "300px"
        };
        $scope.multiSelectionModifyTexts = {
            dynamicButtonTextSuffix: "drugs selected",
            buttonDefaultText: "Select Drugs"
        };

        var currentDate = new Date();
        var timePeriods = {
            "month": new Date().setMonth(currentDate.getMonth() - 1),
            "3month": new Date().setMonth(currentDate.getMonth() - 3),
            "year": new Date().setFullYear(currentDate.getFullYear() - 1)
        };
        $scope.timeTags = Object.keys(timePeriods);

        $scope.changePeriod = function(timeTag) {
            $scope.timeTagSelected = timeTag;
            $scope.reportParams.startTime = $filter('date')(timePeriods[timeTag], "yyyy-MM-dd");
            $scope.reportParams.endTime = $scope.todayDateString;
        };

        $scope.$on('$viewContentLoaded', function () {
            if (type == "singleProduct") {
                $scope.loadProducts();
            } else if (type == "singleFacility") {
                $scope.loadHealthFacilities();
            }else{
                $scope.loadProducts();
                $scope.loadHealthFacilities();
            }
            loadGeographicZones();
            $scope.reportParams.endTime = $scope.todayDateString;
        });

        $scope.loadProducts = function () {
            ProductReportService.loadAllProducts().get({}, function (data) {
                $scope.products = data.products;
            });
        };

        $scope.loadHealthFacilities = function () {
            FacilityService.facilityTypes().get({},function(data){
               var facilityTypes = data["facility-types"];

               $scope.loadFacilities(facilityTypes);
            });
        };

        $scope.loadFacilities = function (facilityTypes) {
            FacilityService.allFacilities().get({}, function (data) {
                var facilities = data.facilities;
                var healthFacilities = [];

                _.forEach(facilities,function(facility){
                    _.forEach(facilityTypes,function(type){
                       if(type.id == facility.typeId){
                           if(type.code != "DDM" && type.code != "DPM"){
                               healthFacilities.push(facility);
                           }
                       }
                    });
                });
                $scope.facilities = healthFacilities;
            });
        };

        function loadGeographicZones() {
            GeographicZoneService.loadGeographicLevel().get({}, function (data) {

                var geographicZoneLevel = data["geographic-levels"];
                GeographicZoneService.loadGeographicZone().get({}, function (data) {
                    $scope.getProvincesAndDistricts(geographicZoneLevel,data);
                });
            });
        }

        function findGeographicZoneLevelByCode(geographicZoneLevels, code){
            if (!geographicZoneLevels) {
                return null;
            }
            return geographicZoneLevels.find(function (level) {
                return level.code == code;
            });
        }

        $scope.getProvincesAndDistricts = function(geographicZoneLevels, data) {
            var provinceLevel = findGeographicZoneLevelByCode(geographicZoneLevels, "province");
            var districtLevel = findGeographicZoneLevelByCode(geographicZoneLevels, "district");
            _.forEach(data["geographic-zones"], function(zone){
                if (zone.levelId == provinceLevel.id) {
                    $scope.provinces.push(zone);
                } else if (zone.levelId == districtLevel.id) {
                    $scope.districts.push(zone);
                }
            });
            $scope.fullGeoZoneList = _.union($scope.fullGeoZoneList, $scope.provinces, $scope.districts);
        };

        $scope.fillProvince = function () {
            var parent = $scope.getParent($scope.reportParams.districtId);
            $scope.reportParams.provinceId = !parent ? undefined : parent.id;
        };

        $scope.getParent = function (geoZoneId) {
            return geoZoneId && $scope.fullGeoZoneList.find(function (zone, index, array) {
                return $scope.getGeoZone(geoZoneId).parentId == zone.id;
            });
        };

        $scope.getGeoZone = function (id) {
            return id && $scope.fullGeoZoneList.find(function (zone, index, array) {
                return id == zone.id;
            });
        };

        $scope.loadReport = function () {
            var params = {};

            params.endTime = $filter('date')($scope.reportParams.endTime, "yyyy-MM-dd HH:mm:ss");
            if (type == "singleProduct") {
                params.productId = $scope.reportParams.productId;
                params.geographicZoneId = $scope.reportParams.districtId ? $scope.reportParams.districtId : $scope.reportParams.provinceId;

                if (!validateProduct()) {
                    return;
                }

                ProductReportService.loadProductReport().get(params, function (data) {
                    $scope.reportData = data.products;
                });
            } else if (type == "singleFacility"){
                params.facilityId = $scope.reportParams.facilityId;
                if (!validateFacility()) {
                    return;
                }
                ProductReportService.loadFacilityReport().get(params, function (data) {
                    $scope.reportData = data.products;
                });
            }else {
                params.startTime = $filter('date')($scope.reportParams.startTime, "yyyy,MM,dd");
                params.endTime = $filter('date')($scope.reportParams.endTime, "yyyy,MM,dd");
                params.provinceName = ($scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId)).name;
                params.districtName = ($scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId)).name;
                params.selectedProducts = $scope.multiProducts.map(function(product){
                    return product.code;
                });
                params.facilityName = ($scope.facilities.find(function(facility){
                    return facility.id = $scope.reportParams.facilityId;
                })).name;
                $scope.reportParams.reportTitle = params.provinceName + ","+ params.districtName+","+ params.facilityName;

                $scope.reportData = [
                    {"code":"f1","name":"p1","avg":100,"occurrences":3,"total":300},
                    {"code":"f2","name":"p2","avg":200,"occurrences":3,"total":600},
                    {"code":"f3","name":"p3","avg":100,"occurrences":3,"total":300},
                    {"code":"f4","name":"p4","avg":100,"occurrences":3,"total":300}];
            }
        };

        $scope.getGeographicZoneById = function(zones, zoneId){
            return zones.find(function(zone){
                return zone.id == zoneId;
            });
        };

        $scope.checkDate = function(){
            $scope.timeTagSelected = "";
            if(new Date() < new Date($scope.reportParams.endTime)){
                $scope.reportParams.endTime = $filter('date')(new Date(), "yyyy-MM-dd");
                var options = {
                    id: "chooseDateAlertDialog",
                    header:"title.alert",
                    body: "dialog.body.date"
                };
                MozambiqueDialog.newDialog(options, function(){}, $dialog);
            }
        };

        function validateFacility() {
            $scope.invalid = !$scope.reportParams.facilityId;
            return !$scope.invalid;
        }

        function validateProduct() {
            $scope.invalid = !$scope.reportParams.productId;
            return !$scope.invalid;
        }

        $scope.checkLastSyncDate = function (time) {
            var syncInterval = (new Date() - time) / 1000 / 3600;
            return syncInterval <= 24 && {'background-color': 'green'} ||
                syncInterval > 24 * 3 && {'background-color': 'red'} ||
                {'background-color': 'orange'};
        };
    };
}
