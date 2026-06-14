const API_URL = 'http://localhost:8080/api/patients';
const REGISTER_API = 'http://localhost:8080/api/patients/register';

let editId = null; 

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('registerForm').addEventListener('submit', onSubmitForm);
    loadPatients();
});

async function onSubmitForm(e) {
    e.preventDefault();
    
    const payload = {
        name: document.getElementById('regName').value.trim(),
        email: document.getElementById('regEmail').value.trim(),
        password: document.getElementById('regPassword').value,
        phone: document.getElementById('regPhone').value.trim() || null,
        age: parseInt(document.getElementById('regAge').value) || null
    };

    try {
        let res;
        if (editId === null) {
            res = await fetch(REGISTER_API, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!res.ok) throw new Error(await res.text());
            alert('Patient registered successfully!');
        } else {
            res = await fetch(`${API_URL}/${editId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!res.ok) throw new Error('Update failed');
            alert('Patient updated successfully!');
            
            editId = null;
            document.querySelector('#registerForm button[type="submit"]').innerText = 'Register';
            document.getElementById('regPassword').required = true; // Password එක ආයෙත් අනිවාර්ය කරනවා
        }

        e.target.reset();
        loadPatients();
    } catch (err) {
        alert('Operation failed: ' + err.message);
    }
}

async function loadPatients() {
    try {
        const res = await fetch(API_URL);
        if (!res.ok) throw new Error('Failed to load');
        const patients = await res.json();
        const tbody = document.getElementById('patientTable');
        tbody.innerHTML = '';
        
        patients.forEach(p => {
            const tr = document.createElement('tr');
            
            tr.innerHTML = `
                <td>${p.id}</td>
                <td>${escapeHtml(p.name)}</td>
                <td>${escapeHtml(p.email)}</td>
                <td>${escapeHtml(p.phone || '')}</td>
                <td>${p.age || ''}</td>
                <td>
                    <button class="btn btn-sm btn-primary" style="background-color: #007bff; border: none; margin-right: 5px;" onclick="startEdit(${p.id}, '${escapeHtml(p.name)}', '${escapeHtml(p.email)}', '${escapeHtml(p.phone || '')}', ${p.age || 'null'})">Edit</button>
                    <button class="btn btn-sm btn-danger" onclick="deletePatient(${p.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error(err);
    }
}

function startEdit(id, name, email, phone, age) {
    editId = id; 
    
    document.getElementById('regName').value = name;
    document.getElementById('regEmail').value = email;
    document.getElementById('regPhone').value = phone === 'null' ? '' : phone;
    document.getElementById('regAge').value = age === null ? '' : age;
    
    document.getElementById('regPassword').required = false; 
    document.getElementById('regPassword').value = ''; 

    document.querySelector('#registerForm button[type="submit"]').innerText = 'Update Patient';
}

async function deletePatient(id) {
    if (!confirm('Delete patient?')) return;
    try {
        const res = await fetch(API_URL + '/' + id, { method: 'DELETE' });
        if (!res.ok) throw new Error('Delete failed');
        loadPatients();
    } catch (err) {
        alert(err.message);
    }
}

function escapeHtml(str) {
    return String(str).replace(/[&<>"']/g, s => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[s]));
}