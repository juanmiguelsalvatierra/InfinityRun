var coachname;
var uid;

function signup() {
  var username = document.getElementById("username").value;
  var email = document.getElementById("email").value;
  var password = document.getElementById("password").value;
  var xhr = new XMLHttpRequest();
  xhr.open("POST", "https://infinityrun.azurewebsites.net/api/User/", true);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      console.log(xhr.status);
      if (xhr.status === 201) {
        window.location.href = "login.html";
      } else {
        console.error("Login failed");
      }
    }
  };
  xhr.send(JSON.stringify({ username: username, mail: email, password: password }));
}

function login() {
  var username = document.getElementById("username").value;
  var password = document.getElementById("password").value;
  var xhr = new XMLHttpRequest();
  xhr.open("GET", "https://infinityrun.azurewebsites.net/api/User/"+username+"&"+password, true);
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      if (xhr.status === 200) {
        console.log("Data received");
        var data = JSON.parse(xhr.responseText);
        window.location.href = "index.html";
        coachname = data.username;  
        uid = data._id;
        document.getElementById("data").innerHTML = JSON.stringify(data);
      } else {
        console.error("Error retrieving data");
        document.getElementById("error-message").style.display = "block";
      }
    }
  };
  xhr.send();
}


async function hashString(data) {
  const encoder = new TextEncoder();
  const dataArray = encoder.encode(data);
  const hashBuffer = await window.crypto.subtle.digest('SHA-256', dataArray);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
  console.log(hashHex);
  return hashHex;
}






const xhr = new XMLHttpRequest();

xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/User/');

xhr.onload = function() {
  if (xhr.status === 200) {
    const response = JSON.parse(xhr.responseText);
    console.log(response);
  } else {
    console.log(xhr.status);
  }
};

xhr.send();








axios.get('https://infinityrun.azurewebsites.net/api/User/')
  .then(function (response) {
    console.log(response.data);
  })
  .catch(function (error) {
    console.log(error);
  });
