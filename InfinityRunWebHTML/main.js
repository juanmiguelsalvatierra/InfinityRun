let map;

let markersArray = [];

var route = [];

let polyline = null;

var coachname;
var uid = "63970f9bd83230a9af442016";


function initMap() {
  map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: 48.22176299638565, lng: 16.445311903684694 },
    zoom: 17,
    disableDefaultUI: true,
  });

  map.addListener('click', function(e) {
    //console.log(e);
    addMarker(e.latLng);
    drawPolyline();
  });


  //map.setMapTypeId(google.maps.MapTypeId.ROADMAP);

  function addMarker(latLng) {
    let marker = new google.maps.Marker({
        map: map,
        position: latLng,
        draggable: true
    });


    var lat = latLng.lat();
    var lng = latLng.lng();
    // Wenn der User klickt wird die Position in das Array getan
    markersArray.push(marker);
    
    //Koordinaten werden in ein Array getan, Das Array wird dann zur DB geschickt
    route.push([lat, lng]);
    //sendRoute();
  }

  function drawPolyline() {
    let markersPositionArray = [];
    // Koordinaten von dem Array in das neue Array hinzufügen
    markersArray.forEach(function(e) {
      markersPositionArray.push(e.getPosition());
    });

    // Polyline wird gezeichnet
    polyline = new google.maps.Polyline({
      map: map,
      path: markersPositionArray,
      strokeOpacity: 1
    });
  }
}


var xhr = new XMLHttpRequest();

//Route wird geschickt
function sendRoute(){
  var test = {
    "userId" : "639158b08b3660204207cacb",
    "name" : "Lauf, Matthias, lauf",
    "routePoints" : route
  }

  var jsonString = JSON.stringify(test);
  console.log(jsonString);
  xhr.open('POST', 'https://infinityrun.azurewebsites.net/api/Route/', true);
  xhr.setRequestHeader('Content-Type', 'application/json');
  xhr.send(jsonString);
}

xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/User/63970f9bd83230a9af442016', true);
xhr.send();
xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 && xhr.status === 200) {
        var data = JSON.parse(xhr.responseText);
        document.getElementById('username').innerHTML = data.username;
    }
}

/*xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/User/63970f9bd83230a9af442016', true);
xhr.send();
xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 && xhr.status === 200) {
        var data = JSON.parse(xhr.responseText);
        document.getElementById('test').innerHTML = data.username;
    }
}*/
xhr.onerror = function() {
    console.log("Error", xhr.statusText);
}

/*let marker = new google.maps.Marker({
  map: map,            
  position: new google.maps.LatLng(1, 1),
  draggable: true,
  icon: 'images/test.jpg'
});*/
let marker;
function go(){
  /*var xhr = new XMLHttpRequest();
  xhr.open("GET", "https://infinityrun.azurewebsites.net/api/User/"+uid, true);
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      if (xhr.status === 200) {
        console.log("Data received");
        var data = JSON.parse(xhr.responseText);
        document.getElementById("dropdown-btn").innerHTML = data.username;
      } else {
        console.error("Error retrieving data");
        document.getElementById("error-message").style.display = "block";
      }
    }
  };
  xhr.send();*/
  document.getElementById('dropdown-btn').innerHTML = coachname;
  //Daten werden alle 2 Sekunden aktualisiert
  setInterval(() => {
    //marker.remove();
    xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/UserData/63970f9bd83230a9af442016', true);
    xhr.send();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
          var data = JSON.parse(xhr.responseText);
          document.getElementById('hr').innerHTML = data.heartRate;
          document.getElementById('speed').innerHTML = data.speed;
          var o = new google.maps.LatLng(data.location[0], data.location[1]);
          marker = new google.maps.Marker({
            map: map,            
            position: o,
            draggable: true,
            icon: 'images/runner.png'
          });
        }
    }
    xhr.onerror = function() {
        console.log("Error", xhr.statusText);
    }
  }, 1000);

  setInterval(() =>{
  marker.setMap(null);
  }, 1000);

}


//window.initMap = initMap;








async function signup() {
  var username = document.getElementById("username").value;
  var email = document.getElementById("email").value;
  var password = await hashString(document.getElementById("password").value) + "";
  var xhr = new XMLHttpRequest();
  xhr.open("POST", "https://infinityrun.azurewebsites.net/api/User/", true);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      console.log(xhr.status);
      if (xhr.status === 201) { 
        window.location.href = "login.html";
      } else {
        console.error("Sign Up failed");
      }
    }
  };
  xhr.send(JSON.stringify({ username: username, mail: email, password: password }));
}

async function login() {
  var username = document.getElementById("username").value;
  var password = await hashString(document.getElementById("password").value);
  var xhr = new XMLHttpRequest();
  xhr.open("GET", "https://infinityrun.azurewebsites.net/api/User/"+username+"&"+password, true);
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      if (xhr.status === 200) {
        console.log("Data received");
        var data = JSON.parse(xhr.responseText);
        window.location.href = "index.html";
        coachname = data.username;  
        console.log(coachname);
        uid = data._id;
        //document.getElementById("data").innerHTML = JSON.stringify(data);
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
