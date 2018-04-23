let loginURL = 'http://10.38.49.136:4567/login';
let registerURL = 'http://10.38.49.136:4567/register'	

/**
 * 
 */
function register(username, email, password, species, nickname, success, fail) {
	
	error = false;
	
	if (username == undefined) {
		error = 'Please input a user name.';
	} else if (password == undefined) {
		error = 'Please input a password.';
	} else if (email == undefined) {
		error = 'Please input an email.';
	} else if (species == undefined) {
		error = 'Please select a pokemon.';
	} else if (nickname == undefined) {
		error = 'Please input a nickname.';
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
			password: password,
			species: species,
			nickname: nickname
		},
		success: function(data) {
			
			if (data.success == true) {
				
				success(data);
				return;
				
			} else {
				
				error = (data.message != undefined) ? data.message : "Unknown error occurred.";
				
				fail({
					success: false,
					error: error
				});
				
				return;
			}
		},
		error: function(xhr, status, err) {
			fail({
				success: false,
				error: "A network error occured!"
			});
			
			return;
		},
		dataType: 'json'
	});
}

function login(username, password, success, fail) {
	error = false;
	
	if (username == undefined) {
		error = 'Please input a user name.';
	} else if (password == undefined) {
		error = 'Please input a password.';
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
		url: loginURL,
		data: {
			username: username,
			password: password
		},
		success: function(data) {
			
			if (data.success == true) {
				
				success(data);
				return;
				
			} else {
				
				error = (data.message != undefined) ? data.message : "Unknown error occurred.";
				
				fail({
					success: false,
					error: error
				});
				
				return;
			}
		},
		error: function(xhr, status, err) {
			fail({
				success: false,
				error: "A network error occured!"
			});
			
			return;
		},
		dataType: 'json'
	});
}

function simpleLogin(username, password) {
	login(username, password, function(resp) {
		net.connect(resp.id, resp.token);
	}, function(resp) {
		console.log('Login failure: ' + resp.error);
	});
}

function simpleRegister(username, nickname, email, password) {
	
	register(username, email, password, 'pikachu', nickname, function(resp) {
		net.connect(resp.id, resp.token);
	}, function(resp) {
		console.log('Registration failure: ' + resp.error);
	});
}
