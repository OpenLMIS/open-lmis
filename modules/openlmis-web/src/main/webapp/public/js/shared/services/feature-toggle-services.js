
services.factory('FeatureToggleService', function ($q, $timeout, $resource) {

    var getToggleValue = function (_key) {
        var deferred = $q.defer();
        $timeout(function () {
            $resource('/reference-data/toggle/:key.json', {key: '@key'}, {}).get({key: _key}, function (data) {
                deferred.resolve(data.key);
            }, {});
        }, 100);
        return deferred.promise;
    };

    return{
        getToggleValue: getToggleValue
    };
});

