var tableHelper;
var tableElement;
var edit = false;

$(document).ready(function () {

    tableElement = $('#airplanesTable');
    tableHelper =  new DataTableHelper(tableElement, {
        bLengthChange: false,
        rowId: 'id',
        columns: [
           { "data": "airplaneNumber" },
           { "data": "speed" },
           { "data": "fuelCapacity" },
           { "data": "fuelLeft"},
           { "data": "mileage"},
           { "mData": function date(data, type, dataToSet) {
                try{
                    return data.location.city;
                } catch(err){
                    return "In the air";
                }
             }
           },
           {"render": function ( data, type, full, meta ) {
                if(full.location){
                    return '<button type="button" class="btn btn-primary" onclick="gasPlane(\'' + full.id + '\')">Gas</button>';
               }
               return " ";
           }}
        ]
    });

   $('#create').on('click', function(event) {
        $('#airplaneModal .modal-title').html('Creating an airplane');
        $('#airplaneModal').modal('show');

        setAirports();
    });
    $('#edit').on('click', function(event) {
        edit = true;
        var airplane = tableHelper.getSelectedRowData();
        setAirports();
        setFormData(airplane);
        $('#airplaneModal .modal-title').html('Editing ' + airplane.airplaneNumber);
        $('#airplaneModal').modal('show');
    });

    $('#remove').on('click', function(event) {
        var airplane = tableHelper.getSelectedRowData();
        bootboxConfirm("Are you sure you want to delete airplane " + airplane.airplaneNumber + "?", function(result){
            if (result == true){
                removeAirplane(airplane, function() {
                    toastr.success('Removed "' + airplane.airplaneNumber + '" from Airplanes!');
                    updateTable();
                },
                handleError);
            }
            else{
                $('#modal').modal('toggle');
            }
        });
    });
    $('#airplaneForm').submit(function(event) {
        event.preventDefault();
        if (edit) {
            handleEditFormSubmit();
        } else {
            handleCreateFormSubmit();
        }
    });

    updateTable();
});

function getAirports(successCallback, errorCallback) {
    return ajaxJsonCall('GET', '/api/airports', null, successCallback, errorCallback);
}

function gasPlane(id, successCallback, errorCallback) {
    return ajaxJsonCall('POST', '/api/airplanes/' + id + '/gas', null, function(){
        updateTable();
    }, errorCallback);
}

function handleCreateFormSubmit() {
    var data = getFormData();

    console.log(data);
    createAirplane(data, function(result) {
        toastr.success('Added "' + data.id + '" to Airplanes!');
        $('#airplaneForm').get(0).reset();
        updateTable();
        $('#airplaneModal').modal('hide');
    }, handleError);
}

function handleEditFormSubmit() {
    var airplane = tableHelper.getSelectedRowData();
    var data = getFormData();
    _.extend(airplane, data);
    editAirplane(airplane, function(result) {
        toastr.success('Edited airplane "' + data.airplaneNumber);
        $('#airplaneForm').get(0).reset();
        updateTable();
        $('#airplaneModal').modal('hide');
        edit = false;
    }, handleError);
}

function handleError(error) {
    toastr.error(JSON.parse(error.responseText).message);
    console.log(error);
};

function setAirports(successCallback, errorCallback){
   ajaxJsonCall('GET', '/api/airports', null, function(airports) {
        getAirports(function(result) {
            result.forEach(function(airport) {
                $('#location').append('<option value=' + airport.id + '>' + airport.city + '</option>');
            });
        }), null});
}

function createAirplane(airplane, successCallback, errorCallback) {
    console.log("Creating airplane..")

    if(airplane.location.id == "0") {
        console.log("Plane is in the air!");
        delete airplane['location'];
    }
    console.log(airplane);

    ajaxJsonCall('POST', '/api/airplanes/create', airplane, successCallback, errorCallback);
}

function editAirplane(data, successCallback, errorCallback) {

    console.log("Editing airplane..")
    var editedAirplane = {
        airplaneNumber : data.airplaneNumber,
        fuelCapacity : data.fuelCapacity,
        fuelLeft : data.fuelLeft,
        location: data.location,
        speed: data.speed,
        mileage: data.mileage
    };

    console.log(editedAirplane);
    var airplane = tableHelper.getSelectedRowData();
    _.extend(airplane, editedAirplane);
    console.log(airplane);
    ajaxJsonCall('POST', '/api/airplanes/edit', airplane, successCallback, errorCallback);
}

function removeAirplane(airplane, successCallback, errorCallback) {
    console.log("Removing airplane..")
    ajaxJsonCall('DELETE', '/api/airplanes/delete/' + airplane.id, null, successCallback, errorCallback);
}

function getFormData() {
    return {
        airplaneNumber: $("#airplaneNr").val(),
        fuelCapacity: $("#fuelCapacity").val(),
        location: {
            id: $("#location").val()
        },
        fuelLeft: $("#fuelCapacity").val(),
        mileage: $("#mileage").val(),
        speed: $("#speed").val()
    };
}

function setFormData(airplane) {
    if(airplane.location){
        $('#location option:eq(' + airplane.location.id + ')').prop('selected', true);
    } else {
        $('#location option:eq(0)').prop('selected', true);
    }
    $('#fuelCapacity').val(airplane.fuelCapacity);
    $("#airplaneNr").val(airplane.airplaneNumber);
    $('#speed').val(airplane.speed);
    $('#mileage').val(airplane.mileage);
}

function updateTable(id) {
    console.log("Updating table..");

    ajaxJsonCall('GET', 'api/airplanes', null, function(airplanes) {
      tableHelper.dataTable.clear();
      tableHelper.dataTable.rows.add(airplanes);
      tableHelper.dataTable.columns.adjust().draw();}, null)
}