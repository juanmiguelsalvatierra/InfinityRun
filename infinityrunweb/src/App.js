/*import logo from './matthias-modified.png';
import './App.css';
import { GoogleMap, useLoadScript, Marker} from "@react-google-maps/api";

export default function Home() {
  let map;
  const {} = useLoadScript({
    googleMapsApiKey: "AIzaSyDYnFvTRbk98dxnRQPJSEhx1UU4x3f09v4",
  });

  
}

function initMap(){
  map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: 48.22176299638565, lng: 16.445311903684694 },
    zoom: 17,
  });

  const uluru = { lat: 48.22176299638565, lng: 16.445311903684694 };


  const marker = new google.maps.Marker({
    position: uluru,
    map: map
  })
}

window.initMap= initMap;*/

import React, { Component } from 'react'; import { Map, GoogleApiWrapper } from 'google-maps-react';
const mapStyles = {
  width: '100%',
  height: '100%'
};
export class MapContainer extends Component {
  render() {
    return (
      <Map
        google={this.props.google}
        zoom={14}
        style={mapStyles}
        initialCenter={
          {
            lat: -1.2884,
            lng: 36.8233
          }
        }
      />
    );
  }
}
export default GoogleApiWrapper({
  apiKey: 'AIzaSyDYnFvTRbk98dxnRQPJSEhx1UU4x3f09v4'
})(MapContainer);