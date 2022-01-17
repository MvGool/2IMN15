import React from 'react';
import './App.css';
import ParkingLot from './components/ParkingLot';
import SideBar from './components/SideBar';
import api from './api/api';

class App extends React.Component {

  constructor(propt) {
    super(propt)

    this.state = {
      apiInstance: api.getInstance(),
      lot: [],
      selectedSpot: null
    }

    this.update = this.update.bind(this)
    this.resetSelected = this.resetSelected.bind(this)
    this.spotSelected = this.spotSelected.bind(this);
  }

  componentDidMount() {
    this.update();
    this.interval = setInterval(this.update, 10000);
  }

  componentWillUnmount() {
    clearInterval(this.interval);
  }

  async update() {
    await this.state.apiInstance.update();
    this.setState({
      lot: this.state.apiInstance.getParkingLot()
    });
  }

  resetSelected() {
    this.setState({
      selectedSpot: null
    });
  }

  spotSelected(spot) {
    if (this.state.selectedSpot == spot) {
      spot = null
    }
    this.setState({
      selectedSpot: spot
    })
  }

  render() {
    return (
      <div className="App">
        <div className='sidebar'>
          <SideBar api={this.state.apiInstance} update={this.update} selected={this.state.selectedSpot} resetSelected={this.resetSelected} />
        </div>
        <div className='main'>
          <ParkingLot api={this.state.apiInstance} selected={this.state.selectedSpot} spotSelected={this.spotSelected} />
        </div>
      </div>
    );
  }
}

export default App;
