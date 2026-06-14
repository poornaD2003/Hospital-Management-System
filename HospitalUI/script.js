document.addEventListener("DOMContentLoaded", () => {
    const appointmentForm = document.getElementById("appointmentForm");
    const appointmentGrid = document.getElementById("appointmentGrid");
    const appointmentCount = document.getElementById("appointmentCount");
    const formTitle = document.getElementById("formTitle");
    const cancelBtn = document.getElementById("cancelBtn");
    const submitBtn = document.getElementById("submitBtn");
    const specializationEl = document.getElementById("specialization");

    const apiBaseUrl = "http://localhost:8080/api/appointments";

    function fetchAppointments() {
        fetch(apiBaseUrl)
            .then(response => response.json())
            .then(data => {
                appointmentGrid.innerHTML = "";
                appointmentCount.textContent = `${data.length} scheduled sessions`;

                if(data.length === 0) {
                    appointmentGrid.innerHTML = `<p style="color: var(--text-muted); grid-column: 1/-1;">No appointments scheduled yet.</p>`;
                    return;
                }

                data.forEach((appointment) => {
                    const displayName = appointment.patientName ? appointment.patientName : `Patient #${appointment.patientId}`;
                    const initials = appointment.patientName ? appointment.patientName.split(' ').map(n => n[0]).join('').toUpperCase().substring(0,2) : "PT";
                    
                    const card = document.createElement("div");
                    card.className = "appointment-card";
                    card.innerHTML = `
                        <div>
                            <div class="card-header">
                                <div class="patient-avatar">${initials}</div>
                                <span class="appointment-id">#${appointment.id}</span>
                            </div>
                            <div class="card-body">
                                <h4>${displayName}</h4> <div class="doc-name">
                                    with ${appointment.doctorName} 
                                    <span style="font-size: 0.75rem; background: var(--secondary); padding: 2px 6px; border-radius: 4px; margin-left: 4px;">
                                        ${appointment.specialization || 'General'}
                                    </span>
                                </div>
                                <div class="time-badge">
                                    <span>📅 ${appointment.appointmentDate}</span>
                                    <span>🕒 ${appointment.appointmentTime}</span>
                                </div>
                            </div>
                        </div>
                        <div class="card-actions">
                            <button class="btn-sm btn-edit" onclick="editAppointment(${appointment.id})">Edit</button>
                            <button class="btn-sm btn-delete" onclick="deleteAppointment(${appointment.id})">Delete</button>
                        </div>
                    `;
                    appointmentGrid.appendChild(card);
                });
            })
            .catch(err => {
                console.error(err);
                appointmentCount.textContent = "Error loading schedule connection.";
            });
    }

    appointmentForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const id = document.getElementById("appointmentId").value;
        const patientId = document.getElementById("patientId").value;
        const doctorName = document.getElementById("doctorName").value;
        const specialization = document.getElementById("specialization").value; 
        const appointmentDate = document.getElementById("appointmentDate").value;
        const appointmentTime = document.getElementById("appointmentTime").value;

        const method = id ? "PUT" : "POST";
        const url = id ? `${apiBaseUrl}/${id}` : apiBaseUrl;

        fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                patientId: parseInt(patientId), 
                doctorName,
                specialization,
                appointmentDate,
                appointmentTime
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Validation failed or Patient not found");
            }
            return response.json();
        })
        .then(() => {
            resetForm();
            fetchAppointments();
        })
        .catch(err => {
            alert("Error: Patient not found.");
            console.error(err);
        });
    });

    window.editAppointment = function(id) {
        fetch(`${apiBaseUrl}/${id}`)
            .then(response => response.json())
            .then((appointment) => {
                document.getElementById("appointmentId").value = appointment.id;
                document.getElementById("patientId").value = appointment.patientId; 
                document.getElementById("doctorName").value = appointment.doctorName;
                document.getElementById("specialization").value = appointment.specialization || "";
                document.getElementById("appointmentDate").value = appointment.appointmentDate;
                document.getElementById("appointmentTime").value = appointment.appointmentTime;
                
                formTitle.textContent = "Update Appointment";
                submitBtn.textContent = "Apply Changes";
                cancelBtn.style.display = "block";
            });
    }

    window.deleteAppointment = function(id) {
        if(confirm("Are you sure you want to remove this appointment?")) {
            fetch(`${apiBaseUrl}/${id}`, {
                method: "DELETE"
            }).then(() => fetchAppointments());
        }
    }

    cancelBtn.addEventListener("click", resetForm);

    function resetForm() {
        appointmentForm.reset();
        document.getElementById("appointmentId").value = "";
        formTitle.textContent = "Create Appointment";
        submitBtn.textContent = "Save Appointment";
        cancelBtn.style.display = "none";
    }

    fetchAppointments();
});