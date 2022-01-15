import ParkingSpot from "./ParkingSpot"

export default function ParkingLot() {
    // Get from server
    let length = 10
    let width = 18
    let lots = []
    let choices = ["free", "free", "occupied", "occupied", "reserved"]
    for (let i = 0; i < length*width ; i++){
        var index = Math.floor(Math.random() * choices.length);
        let occupation = choices[index];
        lots.push(occupation + " " + i)
    }

    let parkingLot = []
    let parkingRow = []

    for (let i = 0; i < length; i++) {
        parkingRow = []
        for (let j = 0; j < width; j++) {
            parkingRow.push(lots[width*i + j])
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