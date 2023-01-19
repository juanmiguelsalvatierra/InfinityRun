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


//Daten werden alle 2 Sekunden aktualisiert
setInterval(() => {
  xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/UserData/639158b08b3660204207cacb', true);
  xhr.send();
  xhr.onreadystatechange = function() {
      if (xhr.readyState === 4 && xhr.status === 200) {
          var data = JSON.parse(xhr.responseText);
          document.getElementById('hr').innerHTML = data.heartRate;
          document.getElementById('speed').innerHTML = data.speed;
          var o = new google.maps.LatLng(data.location[0], data.location[1]);
          let marker = new google.maps.Marker({
            map: map,
            position: o,
            draggable: true,
            icon: 'images/test.jpg'
        });
      }
  }
  xhr.onerror = function() {
      console.log("Error", xhr.statusText);
  }
}, 2000);



//window.initMap = initMap;