const API_URL = 'http://localhost:8080/api/patients';
const REGISTER_API = 'http://localhost:8080/api/patients/register';

// 💡 දැනට Edit කරමින් පවතින Patient ගේ ID එක තියාගන්න Variable එකක්
let editId = null; 

document.addEventListener('DOMContentLoaded', () => {
    // ෆෝම් එක සබ්මිට් වෙද්දී ක්‍රියාත්මක වන Event එක
    document.getElementById('registerForm').addEventListener('submit', onSubmitForm);
    loadPatients();
});

// 💡 Register සහ Update දෙකම හැන්ඩ්ල් කරන්න මෙතඩ් එක වෙනස් කරා
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
            // 💡 1. සාමාන්‍ය රෙජිස්ටර් කිරීම (POST)
            res = await fetch(REGISTER_API, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!res.ok) throw new Error(await res.text());
            alert('Patient registered successfully!');
        } else {
            // 💡 2. දත්ත වෙනස් කිරීම (PUT)
            res = await fetch(`${API_URL}/${editId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!res.ok) throw new Error('Update failed');
            alert('Patient updated successfully!');
            
            // Edit වැඩේ ඉවර නිසා ආපහු Reset කරනවා
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

// 💡 දත්ත ටේබල් එකට ලෝඩ් කරන කොටස
async function loadPatients() {
    try {
        const res = await fetch(API_URL);
        if (!res.ok) throw new Error('Failed to load');
        const patients = await res.json();
        const tbody = document.getElementById('patientTable');
        tbody.innerHTML = '';
        
        patients.forEach(p => {
            const tr = document.createElement('tr');
            
            // 💡 මෙතනට Edit බටන් එකක් අලුතින් එකතු කරා මචං
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

// 💡 Edit බටන් එක එබුවාම ටේබල් එකේ තියෙන දත්ත ටික ෆෝම් එකට පුරවන මෙතඩ් එක
function startEdit(id, name, email, phone, age) {
    editId = id; // Edit කරන කෙනාගේ ID එක මතක තියාගන්නවා
    
    // දත්ත ටික input fields වලට දානවා
    document.getElementById('regName').value = name;
    document.getElementById('regEmail').value = email;
    document.getElementById('regPhone').value = phone === 'null' ? '' : phone;
    document.getElementById('regAge').value = age === null ? '' : age;
    
    // Update කරද්දී Password එක අනිවාර්ය වෙන්න ඕන නැති නිසා required එක අයින් කරනවා
    document.getElementById('regPassword').required = false; 
    document.getElementById('regPassword').value = ''; 

    // බටන් එකේ නම "Update" කියලා වෙනස් කරනවා
    document.querySelector('#registerForm button[type="submit"]').innerText = 'Update Patient';
}

// 💡 Delete කරන කොටස
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