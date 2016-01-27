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
function HelpFileContentController($scope, $route, $location, $dialog, messageService, SettingsByKey, HelpTopicDetail) {
    ////alert('to view help content');

    $scope.startHelpContentView = function (helpContentId) {
        ////alert('loading help content for view');
        $scope.$parent.editProductMode = true;
        $scope.title = 'Help Content';
        $scope.AddEditMode = true;


        // now get a fresh copy of the product object from the server
        HelpTopicDetail.get({id: helpContentId}, function (data) {
            $scope.helpContentView = data.helpTopic;
//            if($scope.editHelpTopic.active === false){
//                $scope.disableAllFields();
//            }
        });
    };

     $scope.startHelpContentView($route.current.params.id);
}
