var tableHelper;
var tableElement;
var edit = false;

$(document).ready(function () {

    tableElement = $('#airportsTable');
    tableHelper =  new DataTableHelper(tableElement, {
        bLengthChange: false,
        rowId: 'id',
        columns: [
           { "data": "country" },
           { "data": "city" },
           { "data": "numberRunways" }
        ]
    });

   $('#create').on('click', function(event) {
        $('#airportModal .modal-title').html('Creating an airport');
        $('#airportModal').modal('show');
    });
    $('#edit').on('click', function(event) {
        edit = true;
        var airport = tableHelper.getSelectedRowData();
        setFormData(airport);
        $('#airportModal .modal-title').html('Editing ' + airport.city);
        $('#airportModal').modal('show');
    });

    $('#remove').on('click', function(event) {
        var airport = tableHelper.getSelectedRowData();
        bootboxConfirm("Are you sure you want to delete airport " + airport.city + "?", function(result){
            if (result == true){
                removeAirport(airport, function() {
                    toastr.success('Removed "' + airport.city + '" from Airports!');
                    updateTable();
                },
                handleError);
            }
            else{
                $('#modal').modal('toggle');
            }
        });
    });
    $('#airportForm').submit(function(event) {
        event.preventDefault();
        if (edit) {
            handleEditFormSubmit();
        } else {
            handleCreateFormSubmit();
        }
    });

    updateTable();
});

function handleCreateFormSubmit() {
    var data = getFormData();

    console.log(data);
    createAirport(data, function(result) {
        toastr.success('Added "' + data.city + '" to Airports!');
        $('#airportForm').get(0).reset();
        updateTable();
        setAirportOptions();
        $('#airportModal').modal('hide');
    }, handleError);
}

function handleEditFormSubmit() {
    var airport = tableHelper.getSelectedRowData();
    var data = getFormData();
    _.extend(airport, data);
    editAirport(airport, function(result) {
        toastr.success('Edited airport "' + data.city);
        $('#airportForm').get(0).reset();
        updateTable();
        $('#airportModal').modal('hide');
        edit = false;
    }, handleError);
}

function handleError(error) {
    toastr.error(JSON.parse(error.responseText).message);
    console.log(error);
};

function createAirport(airport, successCallback, errorCallback) {
    console.log("Creating airport..")
    console.log(airport);

    ajaxJsonCall('POST', '/api/airports/create', airport, successCallback, errorCallback);
}

function editAirport(data, successCallback, errorCallback) {

    console.log("Editing airport..")
    var editedAirport = {
        city : data.city,
        country : data.country,
        numberRunways : data.numberRunways
    };

    console.log(editedAirport);
    var airport = tableHelper.getSelectedRowData();
    _.extend(airport, editedAirport);
    console.log(airport);
    ajaxJsonCall('POST', '/api/airports/edit', airport, successCallback, errorCallback);
}

function removeAirport(airport, successCallback, errorCallback) {
    console.log("Removing airport..")
    ajaxJsonCall('DELETE', '/api/airports/delete/' + airport.id, null, successCallback, errorCallback);
}

function getFormData() {
    return {
        country: $("#country").val(),
        city: $("#city").val(),
        numberRunways: $("#numberRunways").val()
    };
}

function setFormData(airport) {
    $("#country").val(airport.country);
    $('#city').val(airport.city);
    $('#numberRunways').val(airport.numberRunways);
}

function updateTable(id) {
    console.log("Updating table..");

    ajaxJsonCall('GET', 'api/airports', null, function(airports) {
      tableHelper.dataTable.clear();
      tableHelper.dataTable.rows.add(airports);
      tableHelper.dataTable.columns.adjust().draw();}, null)
}