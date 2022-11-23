let map;

function initMap() {
  map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: 48.22176299638565, lng: 16.445311903684694 },
    zoom: 17,
    disableDefaultUI: true,
  });

  map.setMapTypeId(google.maps.MapTypeId.ROADMAP);

  const uluru = { lat: 48.22176299638565, lng: 16.445311903684694 };


  const marker = new google.maps.Marker({
    position: uluru,
    map: map
  })
}

window.initMap = initMap;
