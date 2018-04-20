let loginURL = null;
let registerURL = 'http://localhost/register'

/**
 * 
 */
function register(username, email, password, success, fail) {
	
	error = null;
	
	if (username == undefined) {
		error = 'Please input a user name.';
	} else if (password == undefined) {
		error = 'Please input a password.';
	} else if (email == undefined) {
		error = 'Please input an email.';
	}
	
	if (error != false) {
		 fail({
			success: false,
			error: error
		 });
		 
		 return;
	}

	$.ajax({
		type: "POST",
		url: registerURL,
		data: {
			username: username,
			email: email,
			password: password
		},
		success: function(data) {
			
			if (data.success == true) {
				
				success(data);
				return;
				
			} else {
				
				error = (data.message != undefined) ? data.message : "Unknown error occurred.");
				
				fail({
					success: false,
					error: error
				});
				
				return;
			}
		},
		error: function(err) {
			fail({
				success: false,
				error: 'Malformed response from server.'
			});
			
			return;
		}
		dataType: 'json'
	});
}

function simpleRegister(username, email, password) {
	
	register(username, email, password, function(resp) {
		console.log('Registration succesful!');
		console.log(resp);
	}, function(resp) {
		console.log('Registration failure: ' + resp.error);
	});
}

/**
 * 
 */
function login(args, success, fail) {
	
}