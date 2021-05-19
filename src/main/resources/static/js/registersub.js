$(document).ready(function () {
	$("#select_subject").select2({
		theme: "classic"
	});
});

function loginFail() {
	alert("Username or Password is wrong!");
}