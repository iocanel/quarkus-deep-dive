import React, { useEffect, useState } from "react";
import GoogleMapReact from 'google-map-react';
import {Marker} from './Marker';

export default function SimpleMap(){
  const defaultLat = 45.617653;
  const defaultLng =  9.281157;
  const defaultProps = {
    center: {
      lat: defaultLat,
      lng: defaultLng
    },
    zoom: 15
  };

  const [latitude, setLatitude] = useState(defaultLat);
  const [longtitude, setLongtitude] = useState(defaultLng);

  useEffect(() => {
    const sse = new EventSource('http://localhost:8080/gps/track');
    const getPostion = (data) => {
      let lat = data.substring(0, data.indexOf(' '))
      let lng = data.substring(data.indexOf(' ') + 1)
      console.log("lat:"+lat + " lng:"+lng);
      setLatitude(lat);
      setLongtitude(lng);
    };
    sse.onmessage = e => getPostion(e.data);
    sse.onerror = () => {
      sse.close();
    }
    return () => {
      sse.close();
    };
  },[]);

  return (
    <div style={{ height: '100vh', width: '100%' }}>
      <GoogleMapReact
        bootstrapURLKeys={{ key: "" }}
        defaultCenter={defaultProps.center}
        defaultZoom={defaultProps.zoom} >
        <Marker
          lat={latitude}
          lng={longtitude}
          text="Vehicle"
        />
      </GoogleMapReact>
    </div>
  );
}
