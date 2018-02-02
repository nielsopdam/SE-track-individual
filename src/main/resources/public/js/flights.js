var tableHelper;
var tableElement;
var flightType = 0;
var airport_id;
var edit = false;

$(document).ready(function () {

    tableElement = $('#flightsTable');
    tableHelper =  new DataTableHelper(tableElement, {
        bLengthChange: false,
        rowId: 'id',
        columns: [
           { "data": "flightNumber" },
           { "data": "airplane.airplaneNumber" },
           { "data": "origin.city"},
           { "data": "destination.city" },
           { "mData": function date(data, type, dataToSet) {
                  return data.liftOffTime.replace("T", " ");
              }
           },
           { "mData": function date(data, type, dataToSet) {
                              return data.landingTime.replace("T", " ");
              }
           },
           {"data": "duration"},
           {"data": "distance"},
           {"data": "distanceLeft"},
           {"render": function ( data, type, full, meta ) {
                    console.log(full);
                    var buttonID = "flight_countdown_" + full.flightNumber;
                    var script = '<script>$("#' + buttonID + '").countdown("' + full.liftOffTime.replace("T", " ") + '", function(event) { $(this).text( event.strftime("%D days %H:%M:%S"));});</script>';
                    console.log(script);
                    return '<div id="' + buttonID + '"></div>' + script;
                }
            }
        ]
    });

    $('#takeOff').datetimepicker();
    $('#landingTime').datetimepicker();

   $('#create').on('click', function(event) {
        $('#flightModal .modal-title').html('Creating a flight');
        $('#flightModal').modal('show');

        ajaxJsonCall('GET', '/api/airports', null, function(airports) {
            getAirports(function(result) {
                result.forEach(function(airport) {
                    $('#toAirport').append('<option value=' + airport.id + '>' + airport.city + '</option>');
                });
            }), null});

        ajaxJsonCall('GET', '/api/airplanes', null, function(airplanes) {
                    getAirplanes(function(result) {
                        result.forEach(function(airplane) {
                            $('#airplaneNr').append('<option value=' + airplane.id + '>' + airplane.airplaneNumber + '</option>');
                        });
                    }), null});

        console.log("opened create");
    });
    $('#edit').on('click', function(event) {
        edit = true;
        var flight = tableHelper.getSelectedRowData();
        setFormData(flight);
        $('#flightModal .modal-title').html('Editing ' + flight.flightNumber);
        $('#flightModal').modal('show');
    });

    $('#remove').on('click', function(event) {
        var flight = tableHelper.getSelectedRowData();
        console.log("about to delete");
        console.log(flight);
        bootboxConfirm("Are you sure you want to delete this flight?", function(result){
            if (result == true){
                removeFlight(flight, function() {
                    toastr.success('Removed "' + flight.flightNumber + '" from Flights!');
                    updateTable();
                },
                handleError);
            }
            else{
                $('#modal').modal('toggle');
            }
        });
    });
    $('#flightForm').submit(function(event) {
        event.preventDefault();
        if (edit) {
            handleEditFormSubmit();
        } else {
            handleCreateFormSubmit();
        }
    });

    var url = new URL(window.location.href);
    airport_id = url.searchParams.get("airport");
    flightType = url.searchParams.get("type");

    switchFlightType();
});

function getAirports(successCallback, errorCallback) {
    return ajaxJsonCall('GET', '/api/airports', null, successCallback, errorCallback);
}

function getAirplanes(successCallback, errorCallback) {
    return ajaxJsonCall('GET', '/api/airplanes', null, successCallback, errorCallback);
}

function handleCreateFormSubmit() {
    var data = getFormData();

    console.log(data);
    createFlight(data, function(result) {
        toastr.success('Added "' + data.airplane.id + '" to Flights!');
        $('#flightForm').get(0).reset();
        updateTable();
        $('#flightModal').modal('hide');
    }, handleError);
}

function handleEditFormSubmit() {
    var flight = tableHelper.getSelectedRowData();
    var data = getFormData();
    _.extend(flight, data);
    editFlight(flight, function(result) {
        console.log("editing");
        console.log(flight);
        toastr.success('Edited flight "' + data.flightNumber);
        $('#flightForm').get(0).reset();
        updateTable();
        $('#flightModal').modal('hide');
        edit = false;
    }, handleError);
}

function handleError(error) {
    toastr.error(JSON.parse(error.responseText).message);
    console.log(error);
};

function createFlight(flight, successCallback, errorCallback) {
    console.log("Creating flight..")

    flight.origin = {id: airport_id};
    flight.fuel = 1;
    flight.distance = 1;

    console.log(flight);

    ajaxJsonCall('POST', '/api/flights/create', flight, successCallback, errorCallback);
}

function editFlight(data, successCallback, errorCallback) {

    console.log("Editing flight..")
    var editedFlight = {
        airplaneNr : data.airplaneNr,
        fromAirport : data.fromAirport,
        toAirport : data.toAirport,
        takeOff : data.takeOff,
        landingTime : data.landingTime,
        duration : data.duration
    };

    var flight = tableHelper.getSelectedRowData();
    _.extend(flight, editedFlight);

    ajaxJsonCall('POST', '/api/flights/edit', flight, successCallback, errorCallback);
}

function removeFlight(flight, successCallback, errorCallback) {
    console.log("Removing flight..")
    ajaxJsonCall('DELETE', '/api/flights/delete/' + flight.id, null, successCallback, errorCallback);
}

function switchFlightType(){
    flightType = 1 - flightType;
    console.log(flightType);
    $("#typeFlightButton").html(flightType ? 'Arrivals' : 'Departures');
    updateTable(airport_id);
}

function getFormData() {
    return {
        flightNumber: $("#flightNr").val(),
        airplane : {
            id: $("#airplaneNr").val(),
        },
        destination : {
            id: $("#toAirport").val()
        },
        liftOffTime : $("#takeOff").data("DateTimePicker").date().format("YYYY-MM-DDTHH:mm:ss")
    };
}

function formatDate(date){
    return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() +
     "T" + date.getHours + ":" + date.getMinutes() + ":" + date.getSeconds();
}
function setFormData(flight) {
    $('#airplaneNr option:eq(' + flight.airplane.id + ')').prop('selected', true)
    $('#flightNr').val(flight.flightNumber);
    $('#toAirport option:eq(' + flight.destination.id + ')').prop('selected', true)
    $('#takeOff').val(flight.liftOffTime);
    $("#landingTime").val(flight.landingTime);
}

function updateTable(id) {
    console.log("Updating table..");

    if(!airport_id){
        var url = new URL(window.location.href);
        airport_id = url.searchParams.get("airport");
    }

    url = '/api/airports/' + id + (flightType ? '/departures' : '/arrivals');

    ajaxJsonCall('GET', url, null, function(flights) {
      tableHelper.dataTable.clear();
      tableHelper.dataTable.rows.add(flights);
      tableHelper.dataTable.columns.adjust().draw();}, null)
}