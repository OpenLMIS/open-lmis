/* Inicialización en español para la extensión 'UI date picker' para jQuery. */
/* Traducido por Vester (xvester@gmail.com). */
( function( factory ) {
	if ( typeof define === "function" && define.amd ) {

		// AMD. Register as an anonymous module.
		define( [ "../widgets/datepicker" ], factory );
	} else {

		// Browser globals
		factory( jQuery.datepicker );
	}
}( function( datepicker ) {

datepicker.regional.es = {
	closeText: "Cerrar",
	prevText: "&#x3C;Ant",
	nextText: "Sig&#x3E;",
	currentText: "Hoy",
	monthNames: [ "Enero","Febrero","Marzo","Abril","Mayo","Junio",
	"Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre" ],
	monthNamesShort: [ "Ene","Feb","Mar","Abr","May","Jun",
	"Jul","Ago","Sep","Oct","Nov","Dic" ],
	dayNames: [ "Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado" ],
	dayNamesShort: [ "Dom","Lun","Mar","Mié","Jue","Vie","Sáb" ],
	dayNamesMin: [ "Dom","Lun","Mar","Mié","Jue","Vie","Sáb" ],
	weekHeader: "Sm",
	firstDay: 1,
	isRTL: false,
	showMonthAfterYear: false,
	yearSuffix: "" };

return datepicker.regional.es;

} ) );
