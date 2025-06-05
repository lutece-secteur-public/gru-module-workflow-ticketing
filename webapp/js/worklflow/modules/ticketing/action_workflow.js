$( function() {	
	$('#next_button').prop('disabled', false);

	$('#wf_action_form').submit(function( ){
		$('#next_button').prop('disabled', true);
	});	
});
