import ParkingSpot from "./ParkingSpot"

export default function ParkingLot() {
    let length = 10
    let width = 15

    let parkingLot = []
    let parkingRow = []

    for (let i = 0; i < length; i++) {
        parkingRow = []
        for (let j = 0; j < width; j++) {
            console.log(i, j)
            parkingRow.push(i+10*j)
        }
        parkingLot.push(parkingRow)
    }

    let lot = 
        <div className="parking-lot">
            {parkingLot.map(row => 
                <div className="parking-row">
                    {row.map(spot => 
                        <ParkingSpot spot={spot} />
                    )}
                </div>
            )}
        </div>

    return lot
}