var VaccineForecastLineItem = function(forecast,report) {

    $.extend(this, forecast);

    VaccineForecastLineItem.prototype.maxMonthsOfStock = function () {
        var quantity = (this.getMaximumStock() - Number(parseInt(this.stockOnHand, 10)) );
        this.quantityRequested = quantity;
        return quantity;

    };
};