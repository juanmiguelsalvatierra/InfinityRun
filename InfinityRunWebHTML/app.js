var coachname;

function signup() {
  var username = document.getElementById("username").value;
  var email = document.getElementById("email").value;
  var password = document.getElementById("password").value;
  var xhr = new XMLHttpRequest();
  xhr.open("POST", "https://infinityrun.azurewebsites.net/api/User/", true);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
      console.log(xhr.responseText);
    }
  };
  xhr.send(JSON.stringify({ username: username, mail: email, password: password }));
  window.location.href = "login.html";
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
        if(data != null){
          window.location.href = "index.html";
        }else {
          var msg = "error";
          document.getElementById('errormsg').innerHTML = "error";
        }
        document.getElementById("data").innerHTML = JSON.stringify(data);
      } else {
        console.error("Error retrieving data");
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