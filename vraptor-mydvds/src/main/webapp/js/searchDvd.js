$(document).ready(function() {
		var url = 'dvd.searchCombo.logic';
		$("#dvdTitle").ajaxError(
			function(request, settings){
			   //alert(settings.getAllResponseHeaders());
			   //i18n ????
			   erroText = settings.getResponseHeader("SessionTimeOut");
			   if(erroText != null) {
				   	alert(erroText);
			   } else {
			   		alert("Could not complete ajax request");
			   }
		 	}
		).autocomplete(url, "dvd.title", { minChars:2 });
});