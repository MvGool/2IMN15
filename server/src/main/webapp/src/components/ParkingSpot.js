export default function ParkingSpot(props) {
    let spot = props.spot

    return <div className={props.selected?"parking-spot " + spot.state + "selected":"parking-spot " + spot.state} onClick={() => props.spotSelected(spot)}><p>{spot.id}<br/>{spot.x}, {spot.y}</p></div>
}