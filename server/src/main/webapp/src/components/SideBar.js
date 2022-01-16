import React from 'react';
import api from '../api/api';

class SideBar extends React.Component {

    constructor(props) {
        super(props)

        this.state={
            plate: "",
        }

        this.handlePlateChange = this.handlePlateChange.bind(this);
        this.submitPlate = this.submitPlate.bind(this);
    }

    handlePlateChange(event) {
        let value = event.target.value;
        console.log(value);

        this.setState({
            plate: value
        });
    }

    submitPlate() {
        this.props.api.reservePlate(this.state.plate);
    }

    render() {
        return <div>
            <div>
                <h3>Current parkinglot status</h3>
                <p>
                    Free spaces: {this.props.api.getFree()}<br/>
                    Occupied spaces: {this.props.api.getOccupied()}<br/>
                    Reserved spaces: {this.props.api.getReservations()}
                </p>
                <input type="button" value="Refresh" onClick={() => {this.props.update()}} />
            </div>
            <div>
                <h3>Make a reservation</h3>
                <form name="form">
                    License plate: <input type="text" name="plate" value={this.state.plate} onChange={this.handlePlateChange} required />
                    <input type="button" value="Reserve" onClick={this.submitPlate} />
                </form>
            </div>
        </div>
    };
}

export default SideBar;