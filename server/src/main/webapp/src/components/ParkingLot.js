import ParkingSpot from "./ParkingSpot";

export default function ParkingLot(props) {

    // Get from server
    let length = props.api.getHeight()
    let width = props.api.getWidth()
    let lot = props.api.getParkingLot()

    let parkingLot = []
    let parkingRow = []

    for (let i = 0; i < length; i++) {
        parkingRow = []
        for (let j = 0; j < width; j++) {
            parkingRow.push(lot[width*i + j])
        }
        parkingLot.push(parkingRow)
    }

    let lotComponent = 
        <div className="parking-lot">
            {parkingLot.map(row => 
                <div className="parking-row">
                    {row.map(spot => 
                        <ParkingSpot spot={spot} />
                    )}
                </div>
            )}
        </div>

    return lotComponent
}