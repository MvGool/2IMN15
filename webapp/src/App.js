import React from 'react';
import './App.css';
import ParkingLot from './components/ParkingLot';
import SideBar from './components/SideBar';

class App extends React.Component {
  render() {
    return (
      <div className="App">
        <div className='sidebar'>
          <SideBar update={() => this.forceUpdate().bind(this)} />
        </div>
        <div className='main'>
          <ParkingLot />
        </div>
      </div>
    );
  }
}

export default App;
