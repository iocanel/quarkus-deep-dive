import './Marker.css';
require('./quarkus.png');

export const Marker = ({ text }) => (
  <div className="container">
    <img className="relative" src={require('./quarkus.png')}  />
    {/* <div className="small red circle relative"> */}
    {/*   <div className="tiny white circle relative"> */}
    {/*   </div> */}
    {/* </div> */}
    <div className="caption relative">
      {text}
    </div>
  </div>
);
