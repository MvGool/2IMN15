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
        if (this.props.selected.state == "Free") {
            this.setState({
                error: ""
            });
            this.props.api.reservePlate(this.state.plate, this.props.selected.x, this.props.selected.y);
            setTimeout(this.props.update, 500);
        } else {
            this.setState({
                error: "This state is not free"
            });
        }
        
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
                {this.props.selected!=null?<p>Selected spot: {this.props.selected.id?this.props.selected.id + " at ":null}{"("+this.props.selected.x+", "+this.props.selected.y+")"}</p>:null}
                {this.props.selected!=null && this.props.selected.licensePlate?<p>Plate on this spot: {this.props.selected.licensePlate}</p>:null}
                <br/>
                License plate: 
                <input type="text" name="plate" value={this.state.plate} onChange={this.handlePlateChange} onKeyPress={(event) => {if (event.key == 'Enter') {this.submitPlate()}}} required />
                <input type="submit" value="Reserve" onClick={this.submitPlate} />
                <p>{this.state.error}</p>
            </div>
        </div>
    };
}

export default SideBar;