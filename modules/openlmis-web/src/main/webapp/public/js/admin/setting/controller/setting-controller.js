/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function ListSettingController($scope, $location, Settings, SettingUpdator) {

  $scope.current = '';
    $scope.saveDisabled=true;
  $scope.CreateHeader = function(setting) {
    showHeader = (setting.toUpperCase() != $scope.current.toUpperCase());
    $scope.current = setting;
    return showHeader;
  };

  $scope.changeTab = function(tab){
    $scope.visibleTab = tab;
      $scope.saveDisabled=false;

  };

  Settings.get(function (data){
     $scope.settings = data.settings;
    $scope.grouped_settings = _.groupBy($scope.settings.list,'groupName');

  });

  $scope.saveSettings = function(){
      SettingUpdator.post({}, $scope.settings, function (data){
          $location.path('');
          $scope.$parent.message = "The configuration changes were successfully updated.";
          $scope.saveDisabled=true;

      });
  };
}
