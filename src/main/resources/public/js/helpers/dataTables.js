function DataTableHelper(element, options) {
    var self = this;

    self.element = element;
    self.dataTable = element.DataTable(options);

    // Select row handler
    element.on('click', 'tr', function() {
        self.element.find('tr.selected').removeClass('selected');
        var id = self.dataTable.row(this).id();
        if (id !== self.selectedRowId) {
            $(this).addClass('selected');
            self.selectedRowId = self.dataTable.row( this ).id();
        } else {
            self.selectedRowId = undefined;
        }

        $('button.controls').prop('disabled', self.selectedRowId === undefined);
    });
}

_.extend(DataTableHelper.prototype, {
    getElement() {
        return this.element;
    },
    getDataTable() {
        return this.dataTable;
    },
    getSelectedRowId() {
        return this.selectedRowId;
    },
    getSelectedRowData() {
        if (!this.selectedRowId) {
            return;
        }

        return this.dataTable.row('#' + this.selectedRowId).data();
    }
});
