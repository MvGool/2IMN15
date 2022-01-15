export default function ParkingSpot(props) {
    let spot = props.spot
    let occupation = ""

    if (spot.includes("free")) {
        occupation = "free"
        spot = spot.substring(5)
    } else if (spot.includes("occupied")) {
        occupation = "occupied"
        spot = spot.substring(9)
    } else if (spot.includes("reserved")) {
        occupation = "reserved"
        spot = spot.substring(9)
    }

    return <div className={"parking-spot " + occupation}>{spot}</div>
}