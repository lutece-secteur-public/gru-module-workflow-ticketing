$( function() {	
	
	var form = $('#wf_action_form');
	var boutonSubmit = $('#next_button');
	
	reactiverBouton();

	$('#wf_action_form').submit(function( ){
		desactiverBouton();
	});	
	
	// Réactivation à chaque ouverture de pop-in Bootstrap
	$('#ticketing-modal-workflow-action-form').on('show.bs.modal', function () {
	  console.log('ouverture boo');
	  reactiverBouton();
	});
	
	function reactiverBouton(){
		boutonSubmit.prop('disabled', false);
		form.removeClass('js_erreur_wkf');
	}

	function desactiverBouton(){
		boutonSubmit.prop('disabled', true);
	}
});
