services.factory('FeatureToggleService', function ($q, $timeout, $resource) {
    return $resource('/reference-data/toggle/:key.json', {key: '@key'});
});