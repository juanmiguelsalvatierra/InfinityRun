let map;

let markersArray = [];

var route = [];

let polyline = null;

var coachname;

var groupArray = [];
let selectedGroup;
let currentrunners = [];
var polylines = [];
var uid = "63970f9bd83230a9af442016";
var userHashMap = {};

function logout(){
  localStorage.setItem("userid", null);
  localStorage.setItem("username", null);
  localStorage.setItem("group1ID", null);
  localStorage.setItem("group2ID", null);
  localStorage.setItem("group3ID", null);
  localStorage.setItem("selectedgroup", null);
  localStorage.setItem("currentgroup", null);
  window.location.href = "login.html";
}

function loadlogin(){
  console.log(localStorage.getItem("userid"));
  console.log(localStorage.getItem("username"));
  console.log(localStorage.getItem("group1ID"));
  console.log(localStorage.getItem("group2ID"));
  console.log(localStorage.getItem("group3ID"));
  console.log(localStorage.getItem("selectedgroup"));
}



function initMap() {
  map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: 48.22176299638565, lng: 16.445311903684694 },
    zoom: 17,
    disableDefaultUI: true,
    zoomControl: true,
  });

  map.addListener('click', function(e) {
    //console.log(e);
    addMarker(e.latLng);
    drawPolyline();
  });

}
  //map.setMapTypeId(google.maps.MapTypeId.ROADMAP);

  let lastmarker;
  function addMarker(latLng) {
    let marker = new google.maps.Marker({
        map: map,
        position: latLng,
        draggable: false
    });


    var lat = latLng.lat();
    var lng = latLng.lng();
    // Wenn der User klickt wird die Position in das Array getan
    markersArray.push(marker);
    
    //Koordinaten werden in ein Array getan, Das Array wird dann zur DB geschickt
    route.push([lat, lng]);
    //sendRoute();
    lastmarker = marker;
    //console.log(markersArray);
    //console.log(route);
  }

  function drawPolyline() {
    let markersPositionArray = [];
    // Koordinaten von dem Array in das neue Array hinzufügen
    markersArray.forEach(function(e) {
      markersPositionArray.push(e.getPosition());
    });
    //console.log(markersPositionArray);
    // Polyline wird gezeichnet
    polyline = new google.maps.Polyline({
      map: map,
      path: markersPositionArray,
      strokeOpacity: 1
    });

    polylines.push(polyline);
  }


function deleteLastMarker() {
  let markersPositionArray = [];
  // get last marker in array
  var lastMarker = markersArray.pop();
  route.pop();

  // remove marker from map
  lastMarker.setMap(null);

  // update polyline
  updatePolyline();
  //console.log(route);

}

function updatePolyline() {
  // get array of marker positions
  var path = markersArray.map(function(marker) { return marker.getPosition() });
  console.log(path);

  // update polyline with new path
  polylines.forEach(function(polyline) {
    polyline.setPath(path);
  });
}


var xhr = new XMLHttpRequest();

//Route wird geschickt
function sendRoute(){
  let ridAr = localStorage.getItem("ridarray");
  let parsedArray = JSON.parse(ridAr);
  console.log(parsedArray);

  var id = localStorage.getItem("userid");

  var test = {
    "userId" : /*"639158b08b3660204207cacb"*/ id,
    "name" : "MatthiasMorgenspaziergang",
    "routePoints" : route,
    "runners": parsedArray
  }

  var jsonString = JSON.stringify(test);
  console.log(jsonString);
  xhr.open('POST', 'https://infinityrun.azurewebsites.net/api/Route/', true);
  xhr.setRequestHeader('Content-Type', 'application/json');
  xhr.send(jsonString);
  alert("Route wurde geschickt");
}

//xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/User/63970f9bd83230a9af442016', true);






/*xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/User/63da36d9d33d8be0b81cafe4', true);
xhr.onreadystatechange = function() {
  console.log(xhr.responseText);
    if (xhr.readyState === 4 && xhr.status === 200) {
        var data = JSON.parse(xhr.responseText);
        document.getElementById('username').innerHTML = data.username;
    }
}
xhr.send();

xhr.onerror = function() {
    console.log("Error", xhr.statusText);
}*/






/*let marker = new google.maps.Marker({
  map: map,            
  position: new google.maps.LatLng(1, 1),
  draggable: true,
  icon: 'images/test.jpg'
});*/
let marker;
function go(){
  
  
  var selectedgroup = localStorage.getItem("selectedGroup");
  var userid = localStorage.getItem("userid");

  var username = localStorage.getItem("username");
  document.getElementById("dropdown-btn").innerHTML = username;

  var rida = localStorage.getItem("ridarray");
  let parsedArray = JSON.parse(rida);
  console.log(parsedArray.length);
  console.log(parsedArray);
  
  for (var i = 0; i < parsedArray.length; i++) {
    var table = document.getElementById("usertable");
    var row = table.insertRow(-1);
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    var cell3 = row.insertCell(2);
    cell1.innerHTML = "";
    cell2.innerHTML = "LOADING...";
    cell3.innerHTML = "";
    cell2.setAttribute("id", "username"+i);

    var row = table.insertRow(-1);
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    var cell3 = row.insertCell(2);
    cell1.innerHTML = "SPEED";
    cell2.innerHTML = "";
    cell3.innerHTML = "HEARTRATE";

    var row = table.insertRow(-1);
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    var cell3 = row.insertCell(2);
    cell1.innerHTML = "LOADING...";
    cell2.innerHTML = "";
    cell3.innerHTML = "LOADING...";
    cell1.setAttribute("id", "speed"+i);
    cell3.setAttribute("id", "hr"+i);
  }
  for (var i = 0; i < parsedArray.length; i++) {
    //console.log(i);
    //console.log(parsedArray[i]);
    getUserData(parsedArray[i], i);
}

for (const key in parsedArray) {
  showUserData(parsedArray[key], key);
}
  //Daten werden alle 2 Sekunden aktualisiert
  /*setInterval(() => {
    //marker.remove();
    //xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/UserData/63970f9bd83230a9af442016', true);
    xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/UserData/63da36d9d33d8be0b81cafe4', false);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
          console.log(xhr.status);
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
  }, 1000);*/




//window.initMap = initMap;

}


function getUserData(id, index) {
  xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/User/'+id, false);
  xhr.onreadystatechange = function() { 
    if (xhr.readyState === 4 && xhr.status === 200) {
      var data = JSON.parse(xhr.responseText);
      var username = data.username;
      userHashMap[id] = username;
      document.getElementById('username'+index).innerHTML = data.username;
    }
  }
  xhr.send();
}

function showUserData(user, i){
  var marker = null;
  setInterval(() => {
    //marker.remove();
    //xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/UserData/63970f9bd83230a9af442016', true);
    xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/UserData/'+user, false);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
          var data = JSON.parse(xhr.responseText);
          document.getElementById('hr'+i).innerHTML = data.heartRate;
          document.getElementById('speed'+i).innerHTML = data.speed;
          var o = new google.maps.LatLng(data.location[0], data.location[1]);
          if (!marker) {
            // create new marker if it doesn't exist yet
            marker = new google.maps.Marker({
              map: map,            
              position: o,
              draggable: true,
              icon: 'images/runner.png'
            });
            const infowindow = new google.maps.InfoWindow({
              content: userHashMap[user]
            });
            // add an event listener to the marker to open the info window when clicked
            marker.addListener('click', function() {
              infowindow.open(map, marker);
            });
            infowindow.open(map, marker);
          } else {
            // update marker position if it already exists
            marker.setPosition(o);
          }
          
          /*
          marker = new google.maps.Marker({
            map: map,            
            position: o,
            draggable: true,
            icon: 'images/runner.png'
          });
          var infowindow = new google.maps.InfoWindow({
            content: userHashMap[user]
          });
        
          // add an event listener to the marker to open the info window when clicked
          marker.addListener('click', function() {
            infowindow.open(map, marker);
          });*/
        }
    }
    xhr.send();
    //marker.setMap(null);
  }, 2000);

  /*setInterval(() =>{
    marker.setMap(null);
  }, 1000);*/
}





async function signup() {
  var username = document.getElementById("username").value;
  var email = document.getElementById("email").value;
  var password = await hashString(document.getElementById("password").value) + "";
  var xhr = new XMLHttpRequest();
  xhr.open("POST", "https://infinityrun.azurewebsites.net/api/User/", true);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onreadystatechange = function() {
    //alert(xhr.responseText);
    /*if (xhr.readyState === XMLHttpRequest.DONE) {
      console.log(xhr.status);
      if (xhr.status === 201) { 
        window.location.href = "login.html";
      } else {
        console.error("Sign Up failed");
      }
    }*/

    if (xhr.readyState === XMLHttpRequest.DONE) {
      alert(xhr.responseText);
      window.location.href = "login.html";

    }
  };
  xhr.send(JSON.stringify({ username: username, mail: email, password: password }));
}

async function login() {
  var username = document.getElementById("username").value;
  var password = await hashString(document.getElementById("password").value);
  localStorage.setItem("username", username);
  var xhr = new XMLHttpRequest();
  xhr.open("GET", "https://infinityrun.azurewebsites.net/api/User/"+username+"&"+password, true);
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      if (xhr.status === 200) {
        console.log("Data received");
        var data = JSON.parse(xhr.responseText);
        coachname = data.username;  
        console.log(coachname);
        uid = data._id;
        localStorage.setItem("userid", uid);
        console.log(uid);
        window.location.href = "allgroups.html";
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
  /*const encoder = new TextEncoder();
  const dataArray = encoder.encode(data);
  const hashBuffer = await window.crypto.subtle.digest('SHA-256', dataArray);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
  console.log(hashHex);
  return hashHex;*/

  try {
    const encoder = new TextEncoder();
    const dataArray = encoder.encode(data);
    const hashBuffer = await window.crypto.subtle.digest('SHA-256', dataArray);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
    console.log(hashHex);
    return hashHex;
  } catch (error) {
    console.error(error);
    return null; // or throw a custom error message
  }
} 

function getGroup(){
  console.log(localStorage.getItem("userid"));
  console.log(localStorage.getItem("username"));
  console.log(localStorage.getItem("group1ID"));
  console.log(localStorage.getItem("group2ID"));
  console.log(localStorage.getItem("group3ID"));
  console.log(localStorage.getItem("selectedgroup"));
  var username = localStorage.getItem("username");
  document.getElementById("dropdown-btn").innerHTML = username;
  var userid = localStorage.getItem("userid");
  var xhr = new XMLHttpRequest();
  xhr.open("GET", "https://infinityrun.azurewebsites.net/api/UserGroup/"+userid+"&1", true);
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      if (xhr.status === 200) {
        console.log("Data received");
        var data = JSON.parse(xhr.responseText);
        console.log(data);
        document.getElementById('group1').innerHTML = data[0].name;
        localStorage.setItem("group1ID", data[0]._id);

        document.getElementById('group2').innerHTML = data[1].name;
        localStorage.setItem("group2ID", data[1]._id);

        document.getElementById('group3').innerHTML = data[2].name;
        localStorage.setItem("group3ID", data[2]._id);

        //location.reload();
        groupArray[data[0]._id, data[1]._id, data[2]._id];
      } else {
        console.error("Error retrieving data");
        document.getElementById("error-message").style.display = "block";
      }
    }
  };
  xhr.send();
}

function sendGroup(checksum){
  /*switch (checksum){
    case 1:
      var groupname = document.getElementById("groupname1").value;
      break;
    case 2:
      var groupname = document.getElementById("groupname2").value;
      break;
    case 3:
      var groupname = document.getElementById("groupname3").value;
      break;
  }*/
  var groupname = document.getElementById("groupname").value;
  var userid = localStorage.getItem("userid");
  console.log(userid);
  console.log(groupname);
  let gdata = {"userid" : userid, "name": groupname};
  xhr.open("POST", "https://infinityrun.azurewebsites.net/api/UserGroup/", false);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      console.log(xhr.status);
      alert(xhr.responseText);
      location.reload();
      /*if (xhr.status === 201) { 
        //window.location.href = "login.html";
        console.log("Group added"); 
      } else {
        console.error("Sign Up failed");
      }*/
    }
  };
  xhr.send(JSON.stringify({ userId: userid, name: groupname, a: 1}));

  xhr.open("GET", "https://infinityrun.azurewebsites.net/api/UserGroup/"+userid+"&1", false);
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      if (xhr.status === 200) {
        console.log("Data received");
        var data = JSON.parse(xhr.responseText);
        //console.log(data);
        document.getElementById('group1').innerHTML = data[0].name;
        document.getElementById('group2').innerHTML = data[1].name;
        document.getElementById('group3').innerHTML = data[2].name;
        groupArray[data[0]._id, data[1]._id, data[2]._id];
        location.reload();
        //console.log(groupArray);
      } else {
        console.error("Error retrieving data");
        document.getElementById("error-message").style.display = "block";
      }
    }
  };
  xhr.send();
}

/*function addRunner(checksum){
  let currentgroup;
  switch (checksum){
    case 1:
      var runner = document.getElementById("runnername1").value;
      currentgroup = localStorage.getItem("group1ID");
      console.log("test");
      break;
    case 2:
      var runner = document.getElementById("runnername2").value;
      currentgroup = localStorage.getItem("group2ID");
      console.log("test");
      break;
    case 3:
      var runner = document.getElementById("runnername3").value;
      currentgroup = localStorage.getItem("group3ID");
      console.log("test");
      break;
  }
  var xhr = new XMLHttpRequest();
  //var runner = document.getElementById("runnername3").value;
  //var userid = localStorage.getItem("userid");
  //var gdata = {"id" : "63ec8fc65847f1a8553b0ce5", "username": runner, "a" : 1};
  xhr.open("PUT", "https://infinityrun.azurewebsites.net/api/UserGroup/"+currentgroup+"&"+runner+"&1", true);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      console.log(xhr.status);
      if (xhr.status === 204) { 
        //window.location.href = "login.html";
        console.log("Runner added");
      } else {
        console.error("Add failed");
      }
    }
  };
  xhr.send(JSON.stringify({}));
}*/

function addRunner(){
  let currentgroup = localStorage.getItem("currentgroup");
  var runner = document.getElementById("runnernew").value;
  var xhr = new XMLHttpRequest();
  xhr.open("PUT", "https://infinityrun.azurewebsites.net/api/UserGroup/"+currentgroup+"&"+runner+"&1", true);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      console.log(xhr.status);
      alert(xhr.responseText);
      location.reload();
      /*if (xhr.status === 200) { 
        //window.location.href = "login.html";
        console.log("Runner added");
        alert(xhr.responseText);
        //location.reload();
      } else {
        console.error("Add failed");
      }*/
    }
  };
  xhr.send(JSON.stringify({}));
}

function groupSelect(checksum){
  switch (checksum){
    case 1:
      selectedgroup = localStorage.getItem("group1ID");
      localStorage.setItem("currentgroup", selectedgroup);
      localStorage.setItem("selectedGroup", 0);
      console.log(localStorage.getItem("selectedGroup"));
      break;
    case 2:
      selectedgroup = localStorage.getItem("group2ID");
      localStorage.setItem("currentgroup", selectedgroup);
      localStorage.setItem("selectedGroup", 1);
      console.log(localStorage.getItem("selectedGroup"));
      break;
    case 3:
      selectedgroup = localStorage.getItem("group3ID");
      localStorage.setItem("currentgroup", selectedgroup);
      localStorage.setItem("selectedGroup", 2);
      console.log(localStorage.getItem("selectedGroup"));
      break;
  }

  console.log(localStorage.getItem("selectedGroup"));
  window.location.href = "showgroup.html";
}

function getSelectedGroup(){
  var username = localStorage.getItem("username");
  document.getElementById("dropdown-btn").innerHTML = username;
  var selectedgroup = localStorage.getItem("selectedGroup");
  var userid = localStorage.getItem("userid");
  var nowrunners;
  var runnersnames;
  xhr.open("GET", "https://infinityrun.azurewebsites.net/api/UserGroup/"+userid+"&1", true);
  xhr.send();
  xhr.onreadystatechange = async function() {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      if (xhr.status === 200) {
        console.log("Data received");
        var data = JSON.parse(xhr.responseText);
        //console.log(data);
        document.getElementById('selectedgroupname').innerHTML = data[selectedgroup].name;
        nowrunners = data[selectedgroup].runners;
        let ridArray = JSON.stringify(nowrunners);
        localStorage.setItem("ridarray", ridArray);
        //console.log(nowrunners);
        //getRunnersNames(nowrunners);
        for (const key in data[selectedgroup].runners) {
          //console.log(data[selectedgroup].runners[key]);
          getRunnersNames(data[selectedgroup].runners[key]);
        }
        //console.log(runnerun);
        document.getElementById('showrunner').innerHTML = runnerun;
        /*const jsonRunner = JSON.stringify(runnerun);
        localStorage.setItem("runnerArray", jsonRunner);*/
      } else {
        console.error("Error retrieving data");
        document.getElementById("error-message").style.display = "block";
      }
    }
  };
  //getRunnersNames(nowrunners);
  //console.log(nowrunners);
}

function goMain(){
  window.location.href = "main.html";
}

let runnerun = [];

function getRunnersNames(runnerid){
  /*var runnernames = [];
  //console.log(nowrunners);
  for (const key in nowrunners.value) {
    runnernames.push("https://infinityrun.azurewebsites.net/api/User/" + runnerid[key]);
  }*/

  var runnername;
  xhr.open("GET", "https://infinityrun.azurewebsites.net/api/User/"+runnerid, false);
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (xhr.status === 200) {
          console.log("Data received");
          var data = JSON.parse(xhr.responseText);
          //console.log(data);
          console.log(data);
          /*runnernames.push(data.username);
          console.log(runnernames);*/
          runnername = data.username;
          console.log(data.username);
          runnerun.push(runnername);
          return runnername;
          //console.log(runnername);
        } else {
          console.error("Error retrieving data");
          document.getElementById("error-message").style.display = "block";
        }
      }
    };
    xhr.send();
}

function deleteGroup(checksum){
  var group;
  switch (checksum){
    case 1:
      group = localStorage.getItem("group1ID");
      localStorage.setItem("group1ID", null);
      break;
    case 2:
      group = localStorage.getItem("group2ID");
      localStorage.setItem("group2ID", null);
      break;
    case 3:
      group = localStorage.getItem("group3ID");
      localStorage.setItem("group3ID", null);
      break;
  }
  xhr.open("DELETE", "https://infinityrun.azurewebsites.net/api/UserGroup/"+group, true);
      xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
          console.log(xhr.status);
          alert(xhr.responseText);
          location.reload();
          /*if (xhr.status === 204) {
            //alert('Data successfully deleted.');
            console.log("Data deleted");
            location.reload();
          } else {
            alert('Error deleting data.');
          }*/
        }
      };
  xhr.send();
}

function endRun(){
  var uid = localStorage.getItem("userid");
  xhr.open("DELETE", "https://infinityrun.azurewebsites.net/api/Route/"+uid, true);
      xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
          console.log(xhr.status);
          //alert(xhr.responseText);
          ///location.reload();
          /*if (xhr.status === 204) {
            //alert('Data successfully deleted.');
            console.log("Data deleted");
            location.reload();
          } else {
            alert('Error deleting data.');
          }*/
        }
      };
  xhr.send();
}