/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

services.factory('StockCardsByCategory', function($resource,StockCards,$q, $timeout, ProgramProducts){

 var programId;
 var facilityId;
 var programProducts=[];

 function get(pId,fId) {
    var deferred =$q.defer();
                $timeout(function(){
                    if(!isNaN(pId)){
                        ProgramProducts.get({programId:pId},function(data){
                                 programProducts=data.programProductList;

                                 StockCards.get({facilityId:fId},function(data){
                                        var stockCards=data.stockCards;

                                        stockCards.forEach(function(s){
                                              var product= _.filter(programProducts, function(obj) {
                                                  return obj.product.primaryName === s.product.primaryName;
                                              });

                                                    s.productCategory=product[0].productCategory;
                                              });

                                              var byCategory=_.groupBy(stockCards,function(s){
                                                   return s.productCategory.name;
                                              });

                                              var stockCardsToDisplay = $.map(byCategory, function(value, index) {
                                                  return [{"productCategory":index,"stockCards":value}];
                                              });

                                              deferred.resolve(stockCardsToDisplay);
                                 });
                        });
                    }
                    else{
                            var stockCardsToDisplay=[];
                            StockCards.get({facilityId:fId},function(data){
                                var stockCards=data.stockCards;
                                if(stockCards.length > 0){
                                     stockCardsToDisplay=[{"productCategory":"no-category","stockCards":stockCards}];
                                }
                                deferred.resolve(stockCardsToDisplay);
                            });

                    }


                },100);
     return deferred.promise;

  }
return {
  get: get,
 };
});