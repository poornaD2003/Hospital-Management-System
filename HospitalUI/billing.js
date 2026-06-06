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
            serviceFee: parseFloat(feeInput.value)
        });

        nameSelect.value = "";
        feeInput.value = "";
        renderItemsLists();
    });

    // Add Medicine to local list
    document.getElementById("addMedicineBtn").addEventListener("click", () => {
        const nameInput = document.getElementById("medicineNameInput");
        const priceInput = document.getElementById("medPriceInput");
        const qtyInput = document.getElementById("medQtyInput");

        if (!nameInput.value || !priceInput.value || !qtyInput.value) {
            alert("Please completely provide medicine name, unit price, and quantity.");
            return;
        }

        addedMedicines.push({
            medicineName: nameInput.value,
            unitPrice: parseFloat(priceInput.value),
            quantity: parseInt(qtyInput.value)
        });

        nameInput.value = "";
        priceInput.value = "";
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
            medicinesContainer.innerHTML += `
                <div class="item-tag" style="background:#fef08a; color:#854d0e;">
                    <span>${med.medicineName} (Rs.${med.unitPrice} × ${med.quantity})</span>
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
                            <h4 style="margin: 5px 0;">${billing.patientName}</h4>
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
            });
    }

    // Handle Form Submission Save Operation (POST or PUT)
    billingForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const id = document.getElementById("billingId").value;
        const patientName = document.getElementById("patientName").value;
        const paymentMethod = document.getElementById("paymentMethod").value;
        const paymentStatus = document.getElementById("paymentStatus").value;

        // Structured JSON body layout to sync up to modern backend model requirements
        const payload = {
            patientName: patientName,
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
                document.getElementById("patientName").value = billing.patientName;
                document.getElementById("paymentMethod").value = billing.paymentMethod;
                document.getElementById("paymentStatus").value = billing.paymentStatus;
                
                // Load items collections from backend database to local edit arrays
                addedServices = billing.serviceItems || [];
                addedMedicines = billing.medicineItems || [];
                renderItemsLists();

                formTitle.textContent = "Update Invoice";
                submitBtn.textContent = "Apply Changes";
                cancelBtn.style.display = "block";
            });
    };

    window.deleteBilling = function(id) {
        if (confirm("Are you certain you want to erase this transaction record?")) {
            fetch(`${apiBaseUrl}/${id}`, { method: "DELETE" })
            .then(() => fetchBilling());
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