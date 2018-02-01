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
           { "data": "fuelCapacity" },
           { "data": "fuelLeft"},
           { "mData": function date(data, type, dataToSet) {
                try{
                    return data.location.city;
                } catch(err){
                    return "In the air";
                }
             }
           }
        ]
    });

   $('#create').on('click', function(event) {
        $('#airplaneModal .modal-title').html('Creating an airplane');
        $('#airplaneModal').modal('show');

        ajaxJsonCall('GET', '/api/airports', null, function(airports) {
            getAirports(function(result) {
                result.forEach(function(airport) {
                    $('#location').append('<option value=' + airport.id + '>' + airport.city + '</option>');
                });
            }), null});
    });
    $('#edit').on('click', function(event) {
        edit = true;
        var airplane = tableHelper.getSelectedRowData();
        setFormData(airplane);
        $('#airplaneModal .modal-title').html('Editing ' + airplane.airplaneNumber);
        $('#airplaneModal').modal('show');
    });

    $('#remove').on('click', function(event) {
        var airplane = tableHelper.getSelectedRowData();
        bootboxConfirm("Are you sure you want to delete this airplane?", function(result){
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
        location: data.location
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
        fuelLeft: $("#fuelCapacity").val()
    };
}

function setFormData(airplane) {
    if(_.has(airplane, 'location')){
        $('#location option:eq(' + airplane.location.id + ')').prop('selected', true);
    } else {
        $('#location option:eq(0)').prop('selected', true);
    }
    $('#fuelCapacity').val(airplane.fuelCapacity);
    $("#airplaneNr").val(airplane.airplaneNumber);
}

function updateTable(id) {
    console.log("Updating table..");

    ajaxJsonCall('GET', 'api/airplanes', null, function(airplanes) {
      tableHelper.dataTable.clear();
      tableHelper.dataTable.rows.add(airplanes);
      tableHelper.dataTable.columns.adjust().draw();}, null)
}