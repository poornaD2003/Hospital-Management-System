document.addEventListener("DOMContentLoaded", () => {
    const billingForm = document.getElementById("billingForm");
    const billingGrid = document.getElementById("billingGrid");
    const billingCount = document.getElementById("billingCount");
    const formTitle = document.getElementById("formTitle");
    const cancelBtn = document.getElementById("cancelBtn");
    const submitBtn = document.getElementById("submitBtn");

    // Arrays to temporarily store items added one-by-one in the UI form
    let addedServices = [];
    let addedMedicines = [];

    // 💡 Port updated to 8082 and path changed to /api/billings for Billing Microservice
    const apiBaseUrl = "http://localhost:8080/api/billing";

    // --- Dynamic Item Addition Operations ---

    // Add Service to local list
    document.getElementById("addServiceBtn").addEventListener("click", () => {
        const nameSelect = document.getElementById("serviceSelect");
        const feeInput = document.getElementById("serviceFeeInput");

        if (!nameSelect.value || !feeInput.value) {
            alert("Please provide both Service Name and Fee.");
            return;
        }

        addedServices.push({
            serviceName: nameSelect.value,
            serviceFee: parseFloat(feeInput.value) // 💡 Backend model එකේ නම serviceCost නිසා වෙනස් කළා
        });

        nameSelect.value = "";
        feeInput.value = "";
        renderItemsLists();
    });

    // Add Medicine to local list
    document.getElementById("addMedicineBtn").addEventListener("click", () => {
        const idInput = document.getElementById("medicineIdInput") || document.getElementById("medicineNameInput"); // HTML එකේ ID එක
        const qtyInput = document.getElementById("medQtyInput");

        if (!idInput.value || !qtyInput.value) {
            alert("Please provide both Medicine ID and Quantity.");
            return;
        }

        // 💡 Microservice සංකල්පයට අනුව යවන්නේ medicineId විතරයි. Name/Price Backend එකෙන් බලාගනී.
        addedMedicines.push({
            medicineId: parseInt(idInput.value),
            quantity: parseInt(qtyInput.value)
        });

        idInput.value = "";
        qtyInput.value = "1";
        renderItemsLists();
    });

    // Display temporary items added in the UI container
    function renderItemsLists() {
        const servicesContainer = document.getElementById("servicesList");
        const medicinesContainer = document.getElementById("medicinesList");

        servicesContainer.innerHTML = addedServices.length === 0 ? '<span style="font-size:12px; color:#9ca3af;">No services added yet.</span>' : '';
        addedServices.forEach((service, index) => {
            servicesContainer.innerHTML += `
                <div class="item-tag">
                    <span>${service.serviceName} - Rs.${service.serviceFee.toFixed(2)}</span>
                    <span class="remove-btn" onclick="removeServiceItem(${index})">×</span>
                </div>`;
        });

        medicinesContainer.innerHTML = addedMedicines.length === 0 ? '<span style="font-size:12px; color:#9ca3af;">No medicines added yet.</span>' : '';
        addedMedicines.forEach((med, index) => {
            // Edit කරද්දී backend එකෙන් නම එවුවොත් පෙන්වන්න, නැත්නම් ID එක පෙන්වන්න
            const displayName = med.medicineName ? med.medicineName : `Medicine ID: ${med.medicineId}`;
            const displayPrice = med.unitPrice ? ` (Rs.${med.unitPrice} × ${med.quantity})` : ` (Qty: ${med.quantity})`;
            
            medicinesContainer.innerHTML += `
                <div class="item-tag" style="background:#fef08a; color:#854d0e;">
                    <span>${displayName}${displayPrice}</span>
                    <span class="remove-btn" onclick="removeMedicineItem(${index})">×</span>
                </div>`;
        });
    }

    // Global handles to remove entries from array lists via the 'x' tag buttons
    window.removeServiceItem = function(index) {
        addedServices.splice(index, 1);
        renderItemsLists();
    };

    window.removeMedicineItem = function(index) {
        addedMedicines.splice(index, 1);
        renderItemsLists();
    };

    // --- API Master Request Handlers ---

    function fetchBilling() {
        fetch(apiBaseUrl)
            .then(response => response.json())
            .then(data => {
                billingGrid.innerHTML = "";
                billingCount.textContent = `${data.length} invoices registered`;

                if (data.length === 0) {
                    billingGrid.innerHTML = `<p style="color: var(--text-muted); grid-column: 1/-1;">No ledger records found.</p>`;
                    return;
                }

                data.forEach((billing) => {
                    const statusClass = billing.paymentStatus === "Paid" ? "status-paid" : "status-pending";
                    
                    const card = document.createElement("div");
                    card.className = "billing-card";
                    card.innerHTML = `
                        <div>
                            <div class="card-header">
                                <span class="billing-id">Invoice #${billing.id}</span>
                                <span class="badge ${statusClass}">${billing.paymentStatus}</span>
                            </div>
                            <h4 style="margin: 5px 0;">${billing.patientName} <small style="font-size:10px; color:gray;">(ID: ${billing.patientId})</small></h4>
                            <p style="font-size: 12px; color:gray; margin: 4px 0;">Paid via: <b>${billing.paymentMethod}</b></p>
                            
                            <div style="font-size: 13px; background: #f3f4f6; padding: 6px; border-radius:4px; margin-top:8px;">
                                <strong>Grand Total Cost:</strong> 
                                <span style="color:#059669; font-weight:bold; float:right;">Rs. ${billing.amount.toFixed(2)}</span>
                            </div>
                        </div>
                        <div class="card-actions" style="margin-top:10px;">
                            <button class="btn btn-secondary" onclick="editBilling(${billing.id})">Edit</button>
                            <button class="btn btn-danger" onclick="deleteBilling(${billing.id})">Delete</button>
                        </div>
                    `;
                    billingGrid.appendChild(card);
                });
            })
            .catch(err => console.error("Error fetching billings:", err));
    }

    // Handle Form Submission Save Operation (POST or PUT)
    billingForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const id = document.getElementById("billingId").value;
        const patientIdInput = document.getElementById("patientId") || document.getElementById("patientName"); // HTML එකේ තියෙන input එක
        const paymentMethod = document.getElementById("paymentMethod").value;
        const paymentStatus = document.getElementById("paymentStatus").value;

        if (!patientIdInput.value) {
            alert("Please enter a valid Patient ID");
            return;
        }

        // 💡 Payload structure updated to match Backend entity
        const payload = {
            patientId: parseInt(patientIdInput.value), // 💡 patientName වෙනුවට patientId යවයි
            paymentMethod: paymentMethod,
            paymentStatus: paymentStatus,
            serviceItems: addedServices,
            medicineItems: addedMedicines
        };

        const url = id ? `${apiBaseUrl}/${id}` : apiBaseUrl;
        const method = id ? "PUT" : "POST";

        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (!response.ok) throw new Error("API Execution Failure");
            return response.json();
        })
        .then(() => {
            resetForm();
            fetchBilling();
        })
        .catch(error => console.error("Error processing record:", error));
    });

    // Populate data back to inputs on Editing mode triggered
    window.editBilling = function(id) {
        fetch(`${apiBaseUrl}/${id}`)
            .then(response => response.json())
            .then((billing) => {
                document.getElementById("billingId").value = billing.id;
                
                const patientIdInput = document.getElementById("patientId") || document.getElementById("patientName");
                patientIdInput.value = billing.patientId; // ID එක පිරවීම
                
                document.getElementById("paymentMethod").value = billing.paymentMethod;
                document.getElementById("paymentStatus").value = billing.paymentStatus;
                
                // Load items collections from backend database to local edit arrays
                addedServices = billing.serviceItems || [];
                addedMedicines = billing.medicineItems || [];
                renderItemsLists();

                formTitle.textContent = "Update Invoice";
                submitBtn.textContent = "Apply Changes";
                cancelBtn.style.display = "block";
            })
            .catch(err => console.error("Error fetching bill detail:", err));
    };

    window.deleteBilling = function(id) {
        if (confirm("Are you certain you want to erase this transaction record?")) {
            fetch(`${apiBaseUrl}/${id}`, { method: "DELETE" })
            .then(() => fetchBilling())
            .catch(err => console.error("Error deleting bill:", err));
        }
    };

    cancelBtn.addEventListener("click", resetForm);

    function resetForm() {
        billingForm.reset();
        document.getElementById("billingId").value = "";
        addedServices = [];
        addedMedicines = [];
        renderItemsLists();
        formTitle.textContent = "Create Invoice";
        submitBtn.textContent = "Save Billing";
        cancelBtn.style.display = "none";
    }

    fetchBilling();
});