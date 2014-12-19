
ResolveDashboardFormData = {
    programsList : function($q, $timeout, $rootScope, UserSupervisedActivePrograms){
        var deferred = $q.defer();
        $timeout(function () {

            UserSupervisedActivePrograms.get(function(data){
                deferred.resolve(data.programs);

            });

        },100);

        return deferred.promise;

    },
    formInputValue : function(messageService){
               return {
                yearOptionAll : messageService.get('input.year.option.all'),
                programOptionSelect : messageService.get('input.program.option.select'),
                scheduleOptionSelect : messageService.get('input.schedule.option.select'),
                geographicZoneNational : messageService.get('input.geographic.zone.national'),
                facilityOptionSelect : messageService.get('input.facility.option.select'),
                periodOptionSelect : messageService.get('input.period.option.select')
            };
    },
    userPreferredFilters : function(GetPeriod,localStorageService){
        var date = new Date();
        var filterObject = {};
        var preferredFilterValues = {};
        for(var prefKey in localStorageKeys.PREFERENCE){
            preferredFilterValues[localStorageKeys.PREFERENCE[prefKey]] =  localStorageService.get(localStorageKeys.PREFERENCE[prefKey]);
        }

        filterObject.programId = preferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PROGRAM];
        filterObject.periodId = preferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PERIOD];

        if(!isUndefined(filterObject.periodId)){

            GetPeriod.get({id:filterObject.periodId}, function(period){
                if(!isUndefined(period.year)){
                    filterObject.year = period.year;
                }else{
                    filterObject.year = date.getFullYear() - 1;
                }
            });
        }
        filterObject.scheduleId = preferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_SCHEDULE];

        filterObject.zoneId = preferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_GEOGRAPHIC_ZONE];

        filterObject.productIdList = !isUndefined(preferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PRODUCTS]) ? preferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PRODUCTS].split(',') : null;

        filterObject.facilityId = preferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_FACILITY];

        filterObject.facilityId = preferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_FACILITY];


        return filterObject;
    }
};