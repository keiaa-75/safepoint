// Time Slot Management
function editTimeSlot(id) {
    fetch(`/admin/timeslot/${id}`)
        .then(response => response.json())
        .then(timeSlot => {
            document.getElementById('editDayOfWeek').value = timeSlot.dayOfWeek;
            document.getElementById('editStartTime').value = timeSlot.startTime;
            document.getElementById('editEndTime').value = timeSlot.endTime;
            document.getElementById('editActive').checked = timeSlot.active;
            
            const form = document.getElementById('editTimeSlotForm');
            form.action = `/admin/timeslot/update/${id}`;
            
            const editModal = new bootstrap.Modal(document.getElementById('editTimeSlotModal'));
            editModal.show();
        });
}

function deleteTimeSlot(id) {
    if (confirm('Are you sure you want to delete this time slot?')) {
        fetch(`/admin/timeslot/delete/${id}`, {
            method: 'POST',
        }).then(() => {
            window.location.reload();
        });
    }
}

// Time validation
document.getElementById('timeSlotForm').addEventListener('submit', function(e) {
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;
    
    if (startTime >= endTime) {
        e.preventDefault();
        alert('End time must be after start time');
    }
});

document.getElementById('editTimeSlotForm').addEventListener('submit', function(e) {
    const startTime = document.getElementById('editStartTime').value;
    const endTime = document.getElementById('editEndTime').value;
    
    if (startTime >= endTime) {
        e.preventDefault();
        alert('End time must be after start time');
    }
});