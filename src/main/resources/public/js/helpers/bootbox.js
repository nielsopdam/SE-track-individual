function bootboxConfirm(message, callback) {
    bootbox.confirm({
        message: message,
        buttons: {
            confirm: {
                label: 'Yes',
                className: 'btn-danger'
            },
            cancel: {
                label: 'No',
                className: 'btn-primary'
            }
        },
        callback: callback
    });
}
