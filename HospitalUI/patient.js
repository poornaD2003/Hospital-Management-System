let currentPatient = null;

function loadCurrentPatient() {
    const stored = localStorage.getItem('patient');
    if (!stored) {
        // Not logged in — redirect to auth page
        window.location.href = 'auth.html';
        return;
    }
    try {
        currentPatient = JSON.parse(stored);
    } catch (e) {
        localStorage.removeItem('patient');
        window.location.href = 'auth.html';
        return;
    }
}


const appointmentApiUrl = 'http://localhost:8080/api/appointments';
const billingApiUrl = 'http://localhost:8080/api/billing';

const medicalHistory = [
    {
        id: 1,
        date: '2026-05-10',
        type: 'Consultation',
        doctor: 'Dr. Sarah Smith',
        diagnosis: 'Hypertension',
        treatment: 'Lisinopril',
        notes: 'Blood pressure elevated, lifestyle changes recommended'
    },
    {
        id: 2,
        date: '2026-04-15',
        type: 'Lab Test',
        doctor: 'Lab Technician',
        diagnosis: 'Blood Work',
        treatment: 'Normal results',
        notes: 'All blood parameters within normal range'
    },
    {
        id: 3,
        date: '2026-03-20',
        type: 'Checkup',
        doctor: 'Dr. Michael Johnson',
        diagnosis: 'General Wellness',
        treatment: 'Multivitamins',
        notes: 'Overall health good, continue exercise and diet'
    }
];

const notifications = [
    {
        id: 1,
        title: 'Appointment Reminder',
        message: 'Your appointment with Dr. Sarah Smith is on 2026-06-15 at 10:00 AM.',
        date: '2026-06-14',
        type: 'reminder',
        read: false
    },
    {
        id: 2,
        title: 'Lab Results Ready',
        message: 'Your blood test results are ready for review.',
        date: '2026-06-10',
        type: 'info',
        read: false
    },
    {
        id: 3,
        title: 'Invoice Due',
        message: 'Invoice #INV-2026-002 is due. Please pay by 2026-06-18.',
        date: '2026-06-12',
        type: 'warning',
        read: true
    }
];

let patientAppointments = [];
let patientBillings = [];

function initializePatientPage() {
    loadCurrentPatient();
    loadPatientProfile();
    showSection('appointments');
    fetchAppointments();
    fetchBilling();
    loadMedicalHistory();
    loadNotifications();
}

function loadPatientProfile() {
    if (!currentPatient) return;
    document.getElementById('displayName').textContent = currentPatient.name || '';
    document.getElementById('displayPatientId').textContent = currentPatient.id || '';
    document.getElementById('displayAge').textContent = (currentPatient.age ? currentPatient.age + ' years' : '');
    document.getElementById('displayPhone').textContent = currentPatient.phone || '';
    document.getElementById('displayEmail').textContent = currentPatient.email || '';
}

function fetchAppointments() {
    fetch(appointmentApiUrl)
        .then(response => response.json())
        .then(data => {
            patientAppointments = data.filter(appointment => appointment.patientName === currentPatient.name);
            renderAppointments();
        })
        .catch(() => {
            const grid = document.getElementById('appointmentGrid');
            grid.innerHTML = '<p class="no-data">Unable to load appointments from API.</p>';
        });
}

function renderAppointments() {
    const grid = document.getElementById('appointmentGrid');
    grid.innerHTML = '';

    if (!patientAppointments.length) {
        grid.innerHTML = '<p class="no-data">No appointments found for this patient.</p>';
        return;
    }

    patientAppointments.forEach(appointment => {
        const card = document.createElement('div');
        card.className = 'card appointment-card';
        card.innerHTML = `
            <div class="card-header">
                <h4>${appointment.doctorName}</h4>
                <span class="status-badge status-${appointment.status ? appointment.status.toLowerCase() : 'confirmed'}">${appointment.status || 'Confirmed'}</span>
            </div>
            <div class="card-body">
                <p><strong>Specialization:</strong> ${appointment.specialization || 'General Medicine'}</p>
                <p><strong>Date & Time:</strong> ${appointment.appointmentDate} at ${appointment.appointmentTime}</p>
            </div>
            <div class="card-footer">
                <button class="btn btn-sm btn-primary" onclick="openEditAppointment(${appointment.id})">Edit</button>
                <button class="btn btn-sm btn-danger" onclick="cancelAppointment(${appointment.id})">Cancel</button>
            </div>
        `;
        grid.appendChild(card);
    });
}

function fetchBilling() {
    fetch(billingApiUrl)
        .then(response => response.json())
        .then(data => {
            patientBillings = data.filter(billing => billing.patientName === currentPatient.name);
            renderBilling();
        })
        .catch(() => {
            const grid = document.getElementById('billingGrid');
            grid.innerHTML = '<p class="no-data">Unable to load billing records from API.</p>';
        });
}

function renderBilling() {
    const grid = document.getElementById('billingGrid');
    grid.innerHTML = '';

    if (!patientBillings.length) {
        grid.innerHTML = '<p class="no-data">No billing records found for this patient.</p>';
        return;
    }

    patientBillings.forEach(bill => {
        const statusClass = bill.paymentStatus === 'Paid' ? 'status-paid' : 'status-pending';
        const card = document.createElement('div');
        card.className = 'card billing-card';
        card.innerHTML = `
            <div class="card-header">
                <h4>Invoice #${bill.id}</h4>
                <span class="status-badge ${statusClass}">${bill.paymentStatus}</span>
            </div>
            <div class="card-body">
                <p><strong>Service:</strong> ${bill.serviceName}</p>
                <p><strong>Amount:</strong> Rs ${bill.amount.toFixed(2)}</p>
                <p><strong>Method:</strong> ${bill.paymentMethod}</p>
            </div>
            <div class="card-footer">
                <button class="btn btn-sm btn-primary" onclick="downloadBill(${bill.id})">Download</button>
                ${bill.paymentStatus === 'Pending' ? `<button class="btn btn-sm btn-success" onclick="payBill(${bill.id})">Pay Now</button>` : ''}
            </div>
        `;
        grid.appendChild(card);
    });
}

function loadMedicalHistory() {
    const grid = document.getElementById('medicalHistoryGrid');
    grid.innerHTML = '';

    if (!medicalHistory.length) {
        grid.innerHTML = '<p class="no-data">No medical history records found.</p>';
        return;
    }

    medicalHistory.forEach(record => {
        const card = document.createElement('div');
        card.className = 'card history-card';
        card.innerHTML = `
            <div class="card-header">
                <h4>${record.type}</h4>
                <span class="date-badge">${record.date}</span>
            </div>
            <div class="card-body">
                <p><strong>Doctor:</strong> ${record.doctor}</p>
                <p><strong>Diagnosis:</strong> ${record.diagnosis}</p>
                <p><strong>Treatment:</strong> ${record.treatment}</p>
                <p><strong>Notes:</strong> ${record.notes}</p>
            </div>
            <div class="card-footer">
                <button class="btn btn-sm btn-primary" onclick="downloadRecord(${record.id})">Download</button>
            </div>
        `;
        grid.appendChild(card);
    });
}

function loadNotifications() {
    const grid = document.getElementById('notificationsGrid');
    grid.innerHTML = '';

    if (!notifications.length) {
        grid.innerHTML = '<p class="no-data">No notifications</p>';
        return;
    }

    notifications.forEach(notif => {
        const card = document.createElement('div');
        card.className = `card notification-card notification-${notif.type} ${notif.read ? 'read' : 'unread'}`;
        card.innerHTML = `
            <div class="card-header">
                <h4>${notif.title}</h4>
                <span class="notification-date">${notif.date}</span>
            </div>
            <div class="card-body">
                <p>${notif.message}</p>
            </div>
            <div class="card-footer">
                <button class="btn btn-sm btn-secondary" onclick="markNotificationRead(${notif.id})">
                    ${notif.read ? 'Marked Read' : 'Mark as Read'}
                </button>
            </div>
        `;
        grid.appendChild(card);
    });
}

document.addEventListener('DOMContentLoaded', initializePatientPage);

function showSection(section) {
    document.querySelectorAll('.content-section').forEach(el => {
        el.style.display = 'none';
    });

    const titles = {
        'appointments': { title: 'My Appointments', subtitle: 'View and manage your appointments' },
        'medical-history': { title: 'Medical History', subtitle: 'View your medical records and history' },
        'notifications': { title: 'Notifications', subtitle: 'Stay updated with important messages' },
        'billing': { title: 'Billing & Invoices', subtitle: 'View and manage your bills' }
    };

    document.getElementById('sectionTitle').textContent = titles[section].title;
    document.getElementById('sectionSubtitle').textContent = titles[section].subtitle;
    document.getElementById(`${section}Section`).style.display = 'block';
}

function viewAppointmentForm() {
    document.getElementById('appointmentModal').style.display = 'flex';
    document.getElementById('bookAppointmentForm').reset();
}

function closeModal() {
    document.getElementById('appointmentModal').style.display = 'none';
    document.getElementById('bookAppointmentForm').reset();
}

function openEditAppointment(id) {
    fetch(`${appointmentApiUrl}/${id}`)
        .then(response => response.json())
        .then(appointment => {
            document.getElementById('doctorName').value = appointment.doctorName;
            document.getElementById('specialization').value = appointment.specialization;
            document.getElementById('appointmentDate').value = appointment.appointmentDate;
            document.getElementById('appointmentTime').value = appointment.appointmentTime;
            document.getElementById('bookAppointmentForm').dataset.editingId = appointment.id;
            viewAppointmentForm();
        });
}

function cancelAppointment(id) {
    if (!confirm('Are you sure you want to cancel this appointment?')) return;
    fetch(`${appointmentApiUrl}/${id}`, { method: 'DELETE' })
        .then(() => fetchAppointments());
}

function downloadRecord(id) {
    alert('Downloading medical record ' + id);
}

function markNotificationRead(id) {
    const notif = notifications.find(n => n.id === id);
    if (notif) {
        notif.read = true;
        loadNotifications();
    }
}

function clearAllNotifications() {
    if (!confirm('Clear all notifications?')) return;
    notifications.length = 0;
    loadNotifications();
}

function downloadBill(id) {
    alert('Downloading bill ' + id);
}

function payBill(id) {
    alert('Payment flow for bill ' + id + ' is not implemented yet.');
}

document.getElementById('bookAppointmentForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const appointmentId = this.dataset.editingId;
    const doctorName = document.getElementById('doctorName').value;
    const specialization = document.getElementById('specialization').value;
    const appointmentDate = document.getElementById('appointmentDate').value;
    const appointmentTime = document.getElementById('appointmentTime').value;

    const payload = {
        patientName: currentPatient ? currentPatient.name : 'Unknown',
        doctorName,
        specialization,
        appointmentDate,
        appointmentTime
    };

    const method = appointmentId ? 'PUT' : 'POST';
    const url = appointmentId ? `${appointmentApiUrl}/${appointmentId}` : appointmentApiUrl;

    fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(response => response.json())
        .then(() => {
            delete this.dataset.editingId;
            closeModal();
            fetchAppointments();
        });
});

window.onclick = function(event) {
    const modal = document.getElementById('appointmentModal');
    if (event.target === modal) {
        closeModal();
    }
};