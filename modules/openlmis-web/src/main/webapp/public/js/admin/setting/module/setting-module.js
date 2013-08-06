
'use strict';
angular.module('setting', ['openlmis', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).config(['$routeProvider', function ($routeProvider) {
  $routeProvider.
    when('/list', {controller:ListSettingController, templateUrl:'partials/list.html'}).
    otherwise({redirectTo:'/list'});
}]).run(function($rootScope, AuthorizationService) {
    $rootScope.roleSelected = "selected";
    AuthorizationService.preAuthorize('MANAGE_SETTING');
  });


app.directive('setting', function ($compile, $http, $templateCache) {

    var noteTemplate = '<div class="entry-note"><div class="entry-text"><div class="entry-title">{{name}}</div><div class="entry-copy">{{content.data}}</div></div></div>';



    var getTemplate = function(contentType) {
        var templateLoader,
            baseUrl = '/public/pages/admin/setting/templates/',
            templateMap = {
                TEXT: 'text.html',
                TEXT_AREA: 'text-area.html',
                NUBMER: 'number.html',
                OPTIONS: 'options.html',
                BOOLEAN: 'boolean.html',
                EMAIL: 'email.html'
            };

        var templateUrl = baseUrl + templateMap[contentType];
        templateLoader = $http.get(templateUrl, {cache: $templateCache});

        return templateLoader;

    }

    var linker = function(scope, element, attrs) {
        var loader = getTemplate(scope.content.valueType);
        var promise = loader.success(function(html) {
                            element.html(html);
                        }).then(function (response) {
                            element.replaceWith($compile(element.html())(scope));
                        });
    }

    return {
        restrict: "E",
        rep1ace: true,
        link: linker,
        scope: {
            content:'=ngModel'
        }
    };
});