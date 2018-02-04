var tableHelper;
var tableElement;
var flightType = 0;
var airport_id;
var edit = false;

$(document).ready(function () {

    var url = new URL(window.location.href);
    airport_id = url.searchParams.get("airport");
    flightType = url.searchParams.get("type");

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
                    if(full.distanceLeft < full.distance){
                        return airport_id == full.origin.id ? "Plane has left" : "Plane has arrived";
                    }

                    var buttonID = "flight_countdown_" + full.flightNumber;
                    var script = '<script>$("#' + buttonID + '").countdown("' + full.liftOffTime.replace("T", " ") + '", function(event) { $(this).text( event.strftime("%D days %H:%M:%S"));});</script>';
                    return '<div id="' + buttonID + '"></div>' + script;
                }
            }
        ]
    });

    $('#takeOff').datetimepicker();

   $('#create').on('click', function(event) {
        $('#flightModal .modal-title').html('Creating a flight');
        $('#flightModal').modal('show');

        setModalDropdowns(null, false);

        console.log("opened create");
    });
    $('#edit').on('click', function(event) {
        edit = true;
        var flight = tableHelper.getSelectedRowData();
        setModalDropdowns(flight, true);

        $('#flightModal .modal-title').html('Editing ' + flight.flightNumber);
        $('#flightModal').modal('show');
    });

    $('#remove').on('click', function(event) {
        var flight = tableHelper.getSelectedRowData();
        console.log("about to delete");

        bootboxConfirm("Are you sure you want to delete flight " + flight.flightNumber + "?", function(result){
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

    switchFlightType();

    window.setInterval(function(){
        console.log("Updating");
        updateFlightPlans();
        updateTable();
    }, 3000);
});

function updateFlightPlans(successCallback, errorCallback){
    return ajaxJsonCall('POST', '/api/airports/' + airport_id + '/update_flightplans', null, successCallback, errorCallback);
}

function setModalDropdowns(flight, fill){
    $('#toAirport').html('');
    $('#airplaneNr').html('');

    ajaxJsonCall('GET', '/api/airports', null, function(airports) {
        getAirports(function(result) {
            result.forEach(function(airport) {
                if(airport.id != airport_id){
                    $('#toAirport').append('<option value=' + airport.id + '>' + airport.city + '</option>');
                }
            });
        }), null});

    ajaxJsonCall('GET', '/api/airplanes', null, function(airplanes) {
                getAirplanes(function(result) {
                    result.forEach(function(airplane) {
                        $('#airplaneNr').append('<option value=' + airplane.id + '>' + airplane.airplaneNumber + '</option>');
                    });
                }), null});

    if(fill){
        setFormData(flight);
    } else {
        $('#airplaneNr').val([]);
        $('#flightNr').val('');
        $('#toAirport').val('');
        $("#takeOff").data("DateTimePicker").date('');
    }
}
function getAirports(successCallback, errorCallback) {
    return ajaxJsonCall('GET', '/api/airports', null, successCallback, errorCallback);
}

function getAirplanes(successCallback, errorCallback) {
    return ajaxJsonCall('GET', '/api/airports/' + airport_id + '/available_planes', null, successCallback, errorCallback);
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
    $('#flightTypeShowing').text(flightType ? 'Departues' : 'Arrivals');
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
    $('#toAirport option:eq(' + flight.destination.id + ')').prop('selected', true);
    $("#takeOff").data("DateTimePicker").date(flight.liftOffTime);
}

function updateTable(id) {
    console.log("Updating table..");

    if(!airport_id){
        var url = new URL(window.location.href);
        airport_id = url.searchParams.get("airport");
    }

    url = '/api/airports/' + airport_id + (flightType ? '/departures' : '/arrivals');

    ajaxJsonCall('GET', url, null, function(flights) {
      tableHelper.dataTable.clear();
      tableHelper.dataTable.rows.add(flights);
      tableHelper.dataTable.columns.adjust().draw();}, null)
}