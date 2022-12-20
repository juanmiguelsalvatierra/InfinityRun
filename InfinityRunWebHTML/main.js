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

var axios = require('axios');

axios.get('https://infinityrun.azurewebsites.net/api/UserData/')
  .then(function(response){
    let test = response.data;
    for (let i = 0; i< test.length; i++) {
      console.log(test[i].heartRate);
    }
    let temp = test.username;
      //console.log(response.data)
    //console.log(response);
  })

axios.post('https://infinityrun.azurewebsites.net/api/user', {
  username: 'matze',
  mail: 'matze@lol.com',
  password: 'hs'
})
