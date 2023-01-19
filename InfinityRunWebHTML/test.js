var xhr = new XMLHttpRequest();
xhr.open('GET', 'https://infinityrun.azurewebsites.net/api/UserData/', true);
xhr.send();
xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 && xhr.status === 200) {
        var data = JSON.parse(xhr.responseText);
        document.getElementById('example').innerHTML = data[0].heartRate;
    }
}
xhr.onerror = function() {
    console.log("Error", xhr.statusText);
}
/*
import axios from 'axios';


axios.get('https://infinityrun.azurewebsites.net/api/UserData/')
  .then(response => {
    // Access the data and update the HTML elements
    document.getElementById('example').innerHTML = response.data;
  })
  .catch(error => {
    console.log(error);
  });*/
