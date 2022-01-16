export default class api {

    instance = null

    width = 0
    height = 0
    parkingLot = ["free", "free", "occupied", "free", "reserved"]

    static getInstance() {
        if (this.instance == null) {
            this.instance = new api()
        }

        return this.instance
    }

    async update() {
        let updatedLot = null
        try {
            let response = await fetch('/api/lot');
            let responseJson = await response.json();
            console.log(responseJson)
            updatedLot = responseJson;
        } catch(error) {
            console.error(error);
        }
        this.width = updatedLot.width
        this.height = updatedLot.height
        this.parkingLot = updatedLot.parkingLot
    }
    
    getParkingLot() {
        return this.parkingLot
    }

    getWidth() {
        return this.width
    }

    getHeight() {
        return this.height
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