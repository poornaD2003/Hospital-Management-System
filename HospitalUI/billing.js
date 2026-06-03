document.addEventListener("DOMContentLoaded", () => {
    const billingForm = document.getElementById("billingForm");
    const billingGrid = document.getElementById("billingGrid");
    const billingCount = document.getElementById("billingCount");
    const formTitle = document.getElementById("formTitle");
    const cancelBtn = document.getElementById("cancelBtn");
    const submitBtn = document.getElementById("submitBtn");

    const apiBaseUrl = "http://localhost:8080/api/billing";

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
                                <span class="status-badge ${statusClass}">${billing.paymentStatus}</span>
                            </div>
                            <div class="card-body">
                                <h4>${billing.patientName}</h4>
                                <div class="service-name">${billing.serviceName}</div>
                                
                                <div class="card-meta-row">
                                    <span class="method-tag">💳 ${billing.paymentMethod}</span>
                                    <span class="amount-display">Rs.${parseFloat(billing.amount).toFixed(2)}</span>
                                </div>
                            </div>
                        </div>
                        <div class="card-actions">
                            <button class="btn-sm btn-edit" onclick="editBilling(${billing.id})">Edit</button>
                            <button class="btn-sm btn-delete" onclick="deleteBilling(${billing.id})">Delete</button>
                        </div>
                    `;
                    billingGrid.appendChild(card);
                });
            })
            .catch(err => {
                console.error(err);
                billingCount.textContent = "Error communicating with server ledger.";
            });
    }

    billingForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const id = document.getElementById("billingId").value;
        const patientName = document.getElementById("patientName").value;
        const serviceName = document.getElementById("serviceName").value;
        const amount = document.getElementById("amount").value;
        const paymentMethod = document.getElementById("paymentMethod").value;
        const paymentStatus = document.getElementById("paymentStatus").value;

        const method = id ? "PUT" : "POST";
        const url = id ? `${apiBaseUrl}/${id}` : apiBaseUrl;

        fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                patientName,
                serviceName,
                amount,
                paymentMethod,
                paymentStatus
            })
        })
        .then(response => response.json())
        .then(() => {
            resetForm();
            fetchBilling();
        });
    });

    window.editBilling = function(id) {
        fetch(`${apiBaseUrl}/${id}`)
            .then(response => response.json())
            .then((billing) => {
                document.getElementById("billingId").value = billing.id;
                document.getElementById("patientName").value = billing.patientName;
                document.getElementById("serviceName").value = billing.serviceName;
                document.getElementById("amount").value = billing.amount;
                document.getElementById("paymentMethod").value = billing.paymentMethod;
                document.getElementById("paymentStatus").value = billing.paymentStatus;
                
                // Switch styling seamlessly for edits
                formTitle.textContent = "Update Invoice";
                submitBtn.textContent = "Apply Changes";
                cancelBtn.style.display = "block";
            });
    }

    window.deleteBilling = function(id) {
        if (confirm("Are you certain you want to erase this transaction record?")) {
            fetch(`${apiBaseUrl}/${id}`, {
                method: "DELETE"
            }).then(() => fetchBilling());
        }
    }

    cancelBtn.addEventListener("click", resetForm);

    function resetForm() {
        billingForm.reset();
        document.getElementById("billingId").value = "";
        formTitle.textContent = "Create Invoice";
        submitBtn.textContent = "Save Billing";
        cancelBtn.style.display = "none";
    }

    fetchBilling();
});