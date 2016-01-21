var VaccineOrderRequisition = function (orderRequisition) {
    $.extend(this, orderRequisition);

    VaccineOrderRequisition.prototype.init = function () {

        function getLineItems(collection) {
            var lineItems = [];
            angular.forEach(collection, function (lineItem, r) {
                lineItems.push(new OrderRequisitionLineItem(lineItem, r));
            });
            lineItems=_.sortBy(lineItems,'displayOrder');
            return lineItems;
        }

        this.lineItems = getLineItems(this.lineItems, this);
        this.LineItemViews = _.groupBy(this.lineItems, function(s){
            return s.productCategory.name;
        });


    };

    this.init();
};