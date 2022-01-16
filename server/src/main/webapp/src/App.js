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
      lot: []
    }

    this.update = this.update.bind(this)
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

  render() {
    return (
      <div className="App">
        <div className='sidebar'>
          <SideBar api={this.state.apiInstance} update={this.update} />
        </div>
        <div className='main'>
          <ParkingLot api={this.state.apiInstance} />
        </div>
      </div>
    );
  }
}

export default App;
