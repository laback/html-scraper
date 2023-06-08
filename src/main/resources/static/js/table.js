function sortTable() {
    var table = document.getElementById('eventsTable');
    newTbody = document.createElement('tbody');
    oldTbody = table.tBodies[0];
    rows = oldTbody.rows;
    i = rows.length - 1;

    while (i >= 0) {
        newTbody.appendChild(rows[i]);
        i -= 1;
    }
    oldTbody.parentNode.replaceChild(newTbody, oldTbody);
}