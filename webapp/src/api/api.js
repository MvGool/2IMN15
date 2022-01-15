export default class api {

    instance = null

    // parkingLot = []
    parkingLot = ["free", "free", "occupied", "free", "reserved"]

    static getInstance() {
        if (this.instance == null) {
            this.instance = new api()
        }

        return this.instance
    }

    update() {
        // TODO get updated values from server
        this.parkingLot.push("free")
    }
    
    getParkingLot() {
        return this.parkingLot
    }
    
    getFree() {
        return this.parkingLot.filter(x => x === "free").length
    }
    
    getOccupied() {
        return this.parkingLot.filter(x => x === "occupied").length
    }
    
    getReservations() {
        return this.parkingLot.filter(x => x === "reserved").length
    }

    reservePlate(plate) {
        console.log(plate)
        // TODO post plate to server
    }
}