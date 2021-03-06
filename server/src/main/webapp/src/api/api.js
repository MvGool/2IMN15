export default class api {

    instance = null

    width = 0
    height = 0
    parkingLot = []
    reserved = []

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
            return "error"
        }
        this.width = updatedLot.width
        this.height = updatedLot.height
        this.parkingLot = updatedLot.parkingLot
        this.reserved = updatedLot.reserved

        return "success"
    }
    
    async reservePlate(plate, x=null, y=null) {
        let msg = x!=null?plate + "\n" + x + "," + y:plate
        try {
            let response = await fetch('/api/plate', {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: msg
            });
            return response.json();
        } catch(error) {
            console.error(error);
            return "error"
        }
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
        return this.parkingLot.filter(x => x.state === "Free").length - this.reserved.length
    }
    
    getOccupied() {
        return this.parkingLot.filter(x => x.state === "Occupied").length
    }
    
    getReservations() {
        return this.parkingLot.filter(x => x.state === "Reserved").length + this.reserved.length
    }

    getReservedSpots() {
        let out = []
        this.parkingLot.filter(x => x.state === "Reserved").forEach((s) => {
            out.push(s.licensePlate + " for spot (" + s.x + "," + s.y + ")")
        })
        return out
    }

    getReservedPlates() {
        return this.reserved
    }
}