var axios = require('axios');

var app = express()

app.post('/sign_up', function(req,res){
    var name = req.body.username;
    var mail = req.body.mail;
    var password = req.body.password;
})

var name = document.getElementById("username").value;
var mail = document.

axios.post('https://infinityrun.azurewebsites.net/api/user', {
  username: name,
  mail: 'matze@lol.com',
  password: 'hs'
})