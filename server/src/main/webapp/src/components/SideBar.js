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
                {this.props.selected!=null?<p>Selected spot: {this.props.selected.id?this.props.selected.id + " at ":null}{"("+this.props.selected.x+", "+this.props.selected.y+")"}</p>:null}
                <form name="form">
                    License plate: <input type="text" name="plate" value={this.state.plate} onChange={this.handlePlateChange} required />
                    <input type="button" value="Reserve" onClick={() => {
                        if (this.props.selected.state == "Free") {
                            this.setState({
                                error: ""
                            });
                            this.submitPlate();
                        } else {
                            this.setState({
                                error: "This state is not free"
                            });
                        }
                    }} />
                    {this.state.error}
                </form>
            </div>
        </div>
    };
}

export default SideBar;