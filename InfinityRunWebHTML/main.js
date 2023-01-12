let map;

let markersArray = [];

let polyline = null;

function initMap() {
  map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: 48.22176299638565, lng: 16.445311903684694 },
    zoom: 17,
    disableDefaultUI: true,
  });

  map.addListener('click', function(e) {
    console.log(e);
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

    // Wenn der User klickt wird die Position in das Array getan
    markersArray.push(marker);
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

//window.initMap = initMap;

fetch('https://infinityrun.azurewebsites.net/api/UserData/')
  .then(response => response.json())
  .then(data => {
    // Access the data and update the HTML elements
    document.getElementById('example').innerHTML = data.example;
  });

var axios = require('axios');

axios.get('https://infinityrun.azurewebsites.net/api/UserData/')
  .then(function(response){
    console.log(response.data[0].heartRate);
  })

/*axios.post('https://infinityrun.azurewebsites.net/api/user', {
  username: 'matze',
  mail: 'matze@lol.com',
  password: 'testpw'
})*/

