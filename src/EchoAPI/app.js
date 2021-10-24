const cluster = require('cluster');

if (cluster.isMaster) {

    // Parallelism can be configured through env params
    var cpuCount = process.env.PARALLELISM || 2;

    // Create a worker for each CPU
    for (var i = 0; i < cpuCount; i += 1) {
        cluster.fork();
    }
} else {
    const express = require("express");
    const app = express();
    const port = process.env.ECHO_PORT || 9595

    var server = app.listen(port, () => {
        console.log("Server running on port " + port);
    });

    server.on('connection', function(socket) {
        //Below line has been added to check how many new connections are being made. This should be very few in a keep alive env
        console.log("New connection was made by client");
        socket.setTimeout(180 * 1000); 
    });

    app.get("/echo/:msg/after/:time", (req, res, next) => {
        let time = req.params.time;
        let msg = req.params.msg;
        setTimeout(function() {
            res.status(200).send(msg);
        },time);
    });

}