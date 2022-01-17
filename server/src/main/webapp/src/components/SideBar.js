import React from 'react';
import api from '../api/api';

class SideBar extends React.Component {

    constructor(props) {
        super(props)

        this.state={
            plate: "",
            error: ""
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
        if (this.props.api.getReservedPlates().length >= this.props.api.getFree()) {
            this.setState({
                error: "No more spots available to reserve"
            });
        } else if (this.props.selected == null) {
            this.setState({
                plate: "",
                error: ""
            });
            this.props.api.reservePlate(this.state.plate);
            setTimeout(this.props.update, 500);
        } else if (this.props.selected.state != "Free") {
            this.setState({
                error: "This spot is not free"
            });
        } else {
            this.setState({
                plate: "",
                error: ""
            });
            this.props.api.reservePlate(this.state.plate, this.props.selected.x, this.props.selected.y);
            setTimeout(this.props.update, 500);
        }

        this.props.resetSelected()
    }

    render() {
        let reservedSpots = this.props.api.getReservedSpots().length > 0?<ul>{this.props.api.getReservedSpots().map((spot) => <li>{spot}</li>)}</ul>:"No specifics spots reserved"

        let reservedPlates = this.props.api.getReservedPlates().length > 0?<ul>{this.props.api.getReservedPlates().map((spot) => <li>{spot}</li>)}</ul>:"No arbitrary spots reserved"

        return <div>
            <div>
                <h3>Current parkinglot status</h3>
                <p>
                    Free spaces: {this.props.api.getFree()}<br/>
                    Occupied spaces: {this.props.api.getOccupied()}<br/>
                    Reserved spaces: {this.props.api.getReservations()}<br/>
                    Reserved spots: {reservedSpots}<br/>
                    Reserved plates: {reservedPlates}
                </p>
                <input type="button" value="Refresh" onClick={() => {this.props.update()}} />
            </div>
            <div>
                <h3>Make a reservation</h3>
                {this.props.selected!=null?<p>Selected spot: {this.props.selected.id?this.props.selected.id + " at ":null}{"("+this.props.selected.x+", "+this.props.selected.y+")"}</p>:null}
                {this.props.selected!=null && this.props.selected.licensePlate?<p>Plate on this spot: {this.props.selected.licensePlate}</p>:null}
                <br/>
                License plate: <br/>
                <input type="text" name="plate" value={this.state.plate} onChange={this.handlePlateChange} onKeyPress={(event) => {if (event.key == 'Enter') {this.submitPlate()}}} required />
                <input type="button" value={this.props.selected?"Reserve specific spot":"Reserve arbitrary spot"} onClick={this.submitPlate} />
                <p>{this.state.error}</p>
            </div>
        </div>
    };
}

export default SideBar;