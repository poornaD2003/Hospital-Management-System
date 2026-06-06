document.addEventListener('DOMContentLoaded', () => {
    const pharmacyForm = document.getElementById("pharmacyForm");
    const issueForm = document.getElementById("issueForm");
    const pharmacyTable = document.getElementById("pharmacyTable").getElementsByTagName("tbody")[0];

    const apiBaseUrl = "http://localhost:8080/api/pharmacy";

    // FETCH AND DISPLAY CURRENT STOCK SHEET
    function fetchInventory() {
        fetch(apiBaseUrl)
            .then(response => response.json())
            .then(data => {
                pharmacyTable.innerHTML = "";
                data.forEach(medication => {
                    const row = pharmacyTable.insertRow();
                    row.innerHTML = `
                        <td><strong>${medication.id}</strong></td>
                        <td>${medication.medicineName}</td>
                        <td>${medication.stockQuantity}</td>
                        <td>LKR ${medication.pricePerUnit.toFixed(2)}</td>
                        <td>
                            <button class="edit-btn" onclick="editMedication(${medication.id})">Modify</button>
                            <button class="delete-btn" onclick="deleteMedication(${medication.id})">Purge</button>
                        </td>
                    `;
                });
            });
    }

    // HANDLER FOR INVENTORY STOCK SUBMISSIONS
    pharmacyForm.addEventListener("submit", (e) => {
        e.preventDefault();
        const id = document.getElementById("medicationId").value;
        const medicineName = document.getElementById("medicineName").value;
        const stockQuantity = parseInt(document.getElementById("stockQuantity").value);
        const pricePerUnit = parseFloat(document.getElementById("pricePerUnit").value);

        const method = id ? "PUT" : "POST";
        const url = id ? `${apiBaseUrl}/${id}` : apiBaseUrl;

        const payload = {
            medicineName: medicineName,
            stockQuantity: stockQuantity,
            pricePerUnit: pricePerUnit
        };
        if (id) {
        payload.id = parseInt(id);
    }

        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        }).then(() => {
            pharmacyForm.reset();
            document.getElementById("medicationId").value = "";
            fetchInventory();
        });
    });

    // NEW HANDLER FOR DISPENSING MEDICINE & PRINTING RECEIPTS
    issueForm.addEventListener("submit", (e) => {
        e.preventDefault();
        const patientId = document.getElementById("patientId").value;
        const medicineId = document.getElementById("issueMedicineId").value;
        const quantityIssued = document.getElementById("issueQuantity").value;

        fetch(`${apiBaseUrl}/issue`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ patientId, medicineId, quantityIssued })
        })
            .then(async response => {
                const isJson = response.headers.get('content-type')?.includes('application/json');
                const data = await response.json();
                if (!response.ok) {
                    // Flash stock validation error messages back from backend logic instantly
                    alert(data && data.message ? data.message : "An error occurred while dispensing medicine.");
                    document.getElementById("billInvoice").style.display = "none";
                } else {
                    // Populate invoice card elements and render output
                    document.getElementById("billPatientId").innerText = data.patientId;
                    document.getElementById("billMedName").innerText = data.medicineName;
                    document.getElementById("billQty").innerText = data.quantityIssued;
                    document.getElementById("billTotal").innerText = "LKR " + data.totalBill.toFixed(2);

                    document.getElementById("billInvoice").style.display = "block";
                    issueForm.reset();
                    fetchInventory(); // Reload left stock list view to verify live decrement deduction
                }
            });
    });

    window.editMedication = function (id) {
        fetch(`${apiBaseUrl}/${id}`)
            .then(response => response.json())
            .then(medication => {
                document.getElementById("medicationId").value = medication.id;
                document.getElementById("medicineName").value = medication.medicineName;
                document.getElementById("stockQuantity").value = medication.stockQuantity;
                document.getElementById("pricePerUnit").value = medication.pricePerUnit;
            });
    };

    window.deleteMedication = function (id) {
        if (confirm("Are you sure you want to remove this medication from stock entries?")) {
            fetch(`${apiBaseUrl}/${id}`, { method: "DELETE" }).then(() => fetchInventory());
        }
    };

    fetchInventory();
});