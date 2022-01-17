export default class api {

    instance = null

    width = 0
    height = 0
    parkingLot = []

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

        return "success"
    }
    
    async reservePlate(plate, x, y) {
        try {
            let response = await fetch('/api/plate', {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: plate + "\n" + x + "," + y
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
        return this.parkingLot.filter(x => x.state === "Free").length
    }
    
    getOccupied() {
        return this.parkingLot.filter(x => x.state === "Occupied").length
    }
    
    getReservations() {
        return this.parkingLot.filter(x => x.state === "Reserved").length
    }
}