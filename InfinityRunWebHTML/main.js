let map;

let markersArray = [];

var route = [];

let polyline = null;

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
    sendRoute();
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
xhr.onerror = function() {
    console.log("Error", xhr.statusText);
}

/*let marker = new google.maps.Marker({
  map: map,            
  position: new google.maps.LatLng(1, 1),
  draggable: true,
  icon: 'images/test.jpg'
});*/
//Daten werden alle 2 Sekunden aktualisiert
let marker;
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
        /*var marker = new google.maps.Marker({
          map : map
        });
        marker.setPosition(o);*/
      }
  }
  xhr.onerror = function() {
      console.log("Error", xhr.statusText);
  }
}, 1000);


setInterval(() =>{
  marker.setMap(null);
}, 1000);


//window.initMap = initMap;