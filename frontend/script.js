const BASE_URL = 'http://localhost:8080';

document.addEventListener('DOMContentLoaded', () => {
    // Screen sections
    const homeScreen = document.getElementById('homeScreen');
    const studentLoginScreen = document.getElementById('studentLoginScreen');
    const adminLoginScreen = document.getElementById('adminLoginScreen');
    const adminControlPanel = document.getElementById('adminControlPanel');
    const portalScreen = document.getElementById('portalScreen');

    // Authentication buttons & inputs
    const studentButton = document.getElementById('studentButton');
    const adminButton = document.getElementById('adminButton');
    const studentLoginForm = document.getElementById('studentLoginForm');
    const adminLoginForm = document.getElementById('adminLoginForm');
    const studentErrorMessage = document.getElementById('studentErrorMessage');
    const adminErrorMessage = document.getElementById('adminErrorMessage');
    
    // Back navigation buttons
    const backToHomeFromStudentLogin = document.getElementById('backToHomeFromStudentLogin');
    const backToHomeFromAdminLogin = document.getElementById('backToHomeFromAdminLogin');
    const backToHomeFromPortal = document.getElementById('backToHomeFromPortal');

    // Logout buttons
    const logoutAdminButton = document.getElementById('logoutAdminButton');
    const logoutStudentButton = document.getElementById('logoutStudentButton');

    // Student Search Panel elements
    const searchMaterialForm = document.getElementById('searchMaterialForm');
    const searchSubjectNameInput = document.getElementById('searchSubjectName');
    const searchSemesterSelect = document.getElementById('searchSemester');
    const searchLoading = document.getElementById('searchLoading');
    const searchError = document.getElementById('searchError');
    const searchResultCard = document.getElementById('searchResultCard');
    const resultSubjectName = document.getElementById('resultSubjectName');
    const resultSemester = document.getElementById('resultSemester');
    const resultDepartment = document.getElementById('resultDepartment');
    const resultYear = document.getElementById('resultYear');
    const openDriveLinkBtn = document.getElementById('openDriveLinkBtn');

    // Faculty Dashboard Form & Table elements
    const materialForm = document.getElementById('materialForm');
    const formTitle = document.getElementById('formTitle');
    const materialIdInput = document.getElementById('materialId');
    const materialSubjectNameInput = document.getElementById('materialSubjectName');
    const materialDepartmentSelect = document.getElementById('materialDepartment');
    const materialCourseYearSelect = document.getElementById('materialCourseYear');
    const materialSem1Input = document.getElementById('materialSem1');
    const materialSem2Input = document.getElementById('materialSem2');
    const materialActiveStatusCheckbox = document.getElementById('materialActiveStatus');
    const saveMaterialBtn = document.getElementById('saveMaterialBtn');
    const clearFormBtn = document.getElementById('clearFormBtn');
    const facultyFormMessage = document.getElementById('facultyFormMessage');
    const materialsTableBody = document.getElementById('materialsTableBody');
    const tableSearchInput = document.getElementById('tableSearch');

    // Global variable to hold fetched materials for edit mapping
    let materialsList = [];

    // --- Screen Router ---
    function showScreen(screenToShow) {
        [homeScreen, studentLoginScreen, adminLoginScreen, adminControlPanel, portalScreen].forEach(screen => {
            screen.classList.add('hidden');
        });
        screenToShow.classList.remove('hidden');
    }

    // --- Session Restoration ---
    function checkSession() {
        const token = localStorage.getItem('token');
        const role = localStorage.getItem('role');
        const loginTime = parseInt(localStorage.getItem('loginTime'), 10);
        const now = Date.now();
        const THIRTY_MINUTES = 30 * 60 * 1000;

        if (token && role && loginTime && (now - loginTime < THIRTY_MINUTES)) {
            // Keep token valid, update login time
            localStorage.setItem('loginTime', now.toString());
            if (role === 'STUDENT') {
                showScreen(portalScreen);
            } else if (role === 'FACULTY') {
                showScreen(adminControlPanel);
                fetchMaterials();
            }
        } else {
            logout();
        }
    }

    // --- Logout Function ---
    function logout() {
        localStorage.clear();
        showScreen(homeScreen);
    }

    // Check session on load
    checkSession();

    // --- Form Login Handler ---
    async function loginUser(username, password, roleType, errorElement) {
        errorElement.classList.add('hidden');
        try {
            const response = await fetch(`${BASE_URL}/api/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            const result = await response.json();
            if (response.ok && result.success) {
                localStorage.setItem('token', result.data.token);
                localStorage.setItem('role', result.data.role);
                localStorage.setItem('username', result.data.username);
                localStorage.setItem('loginTime', Date.now().toString());

                if (result.data.role === 'STUDENT') {
                    showScreen(portalScreen);
                } else if (result.data.role === 'FACULTY') {
                    showScreen(adminControlPanel);
                    fetchMaterials();
                }
            } else {
                errorElement.textContent = result.message || 'Invalid username or password.';
                errorElement.classList.remove('hidden');
            }
        } catch (error) {
            errorElement.textContent = 'Server connection failed. Make sure the backend is running.';
            errorElement.classList.remove('hidden');
        }
    }

    // --- Login Events ---
    studentLoginForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const username = document.getElementById('studentUsername').value.trim();
        const password = document.getElementById('studentPassword').value.trim();
        loginUser(username, password, 'STUDENT', studentErrorMessage);
    });

    adminLoginForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const username = document.getElementById('adminUsername').value.trim();
        const password = document.getElementById('adminPassword').value.trim();
        loginUser(username, password, 'FACULTY', adminErrorMessage);
    });

    // Navigation triggers
    studentButton.addEventListener('click', () => {
        showScreen(studentLoginScreen);
        studentErrorMessage.classList.add('hidden');
        studentLoginForm.reset();
    });

    adminButton.addEventListener('click', () => {
        showScreen(adminLoginScreen);
        adminErrorMessage.classList.add('hidden');
        adminLoginForm.reset();
    });

    backToHomeFromStudentLogin.addEventListener('click', () => showScreen(homeScreen));
    backToHomeFromAdminLogin.addEventListener('click', () => showScreen(homeScreen));
    backToHomeFromPortal.addEventListener('click', logout);

    logoutAdminButton.addEventListener('click', () => {
        if (confirm("Are you sure you want to log out?")) {
            logout();
        }
    });

    logoutStudentButton.addEventListener('click', () => {
        if (confirm("Are you sure you want to log out?")) {
            logout();
        }
    });

    // --- Student Search Operations ---
    window.searchMaterial = async function() {
        const subjectName = searchSubjectNameInput.value.trim();
        const semester = searchSemesterSelect.value;
        const token = localStorage.getItem('token');

        if (!subjectName || !semester) {
            showSearchError("Subject name and semester are required.");
            return;
        }

        if (!token) {
            showSearchError("Session expired. Please log in again.");
            logout();
            return;
        }

        showSearchLoading();

        try {
            const url = `${BASE_URL}/api/materials/search?subjectName=${encodeURIComponent(subjectName)}&semester=${semester}`;
            const response = await fetch(url, {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                }
            });

            const result = await response.json();
            hideSearchLoading();

            if (response.ok && result.success) {
                showSearchResult(result.data);
            } else {
                showSearchError(result.message || "Material not found for the given subject.");
            }
        } catch (error) {
            hideSearchLoading();
            showSearchError("Unable to connect to the server. Please try again later.");
        }
    };

    searchMaterialForm.addEventListener('submit', (e) => {
        e.preventDefault();
        window.searchMaterial();
    });

    function showSearchLoading() {
        searchLoading.classList.remove('hidden');
        searchError.classList.add('hidden');
        searchResultCard.classList.add('hidden');
    }

    function hideSearchLoading() {
        searchLoading.classList.add('hidden');
    }

    function showSearchError(message) {
        searchError.textContent = message;
        searchError.classList.remove('hidden');
        searchResultCard.classList.add('hidden');
    }

    function showSearchResult(data) {
        searchError.classList.add('hidden');
        resultSubjectName.textContent = data.subjectName;
        resultSemester.textContent = data.semester;
        resultDepartment.textContent = data.department;
        resultYear.textContent = data.courseYear;
        
        // Remove previous listeners
        const newBtn = openDriveLinkBtn.cloneNode(true);
        openDriveLinkBtn.parentNode.replaceChild(newBtn, openDriveLinkBtn);
        
        newBtn.addEventListener('click', () => {
            window.open(data.driveLink, '_blank');
        });

        searchResultCard.classList.remove('hidden');
    }

    // --- Faculty Dashboard CRUD Operations ---
    async function fetchMaterials() {
        const token = localStorage.getItem('token');
        if (!token) return;

        try {
            const response = await fetch(`${BASE_URL}/api/faculty/materials`, {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                }
            });
            const result = await response.json();
            if (response.ok && result.success) {
                materialsList = result.data;
                populateMaterialsTable(materialsList);
            }
        } catch (error) {
            console.error("Error fetching materials", error);
        }
    }

    function populateMaterialsTable(materials) {
        materialsTableBody.innerHTML = '';
        if (materials.length === 0) {
            materialsTableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="px-6 py-4 text-center text-gray-500 font-semibold">No study materials available. Add one above!</td>
                </tr>
            `;
            return;
        }

        materials.forEach(material => {
            const sem1Available = material.sem1 && material.sem1.trim() !== '' ? 'Yes' : 'No';
            const sem2Available = material.sem2 && material.sem2.trim() !== '' ? 'Yes' : 'No';
            const statusText = material.activeStatus ? 'Active' : 'Inactive';
            const statusColor = material.activeStatus ? 'text-green-600 font-bold' : 'text-red-500 font-bold';

            const tr = document.createElement('tr');
            tr.className = 'hover:bg-gray-50 transition-colors border-b';
            tr.innerHTML = `
                <td class="px-6 py-4 font-semibold text-gray-900">${escapeHtml(material.subjectName)}</td>
                <td class="px-6 py-4">${escapeHtml(material.department)}</td>
                <td class="px-6 py-4 font-medium">${escapeHtml(material.courseYear)}</td>
                <td class="px-6 py-4 text-center">${sem1Available}</td>
                <td class="px-6 py-4 text-center">${sem2Available}</td>
                <td class="px-6 py-4 text-center ${statusColor}">${statusText}</td>
                <td class="px-6 py-4 text-center">
                    <button onclick="editMaterial(${material.id})" class="text-blue-600 hover:text-blue-900 mr-4 font-semibold">
                        <i class="fas fa-edit mr-1"></i> Edit
                    </button>
                    <button onclick="deleteMaterial(${material.id})" class="text-red-600 hover:text-red-900 font-semibold">
                        <i class="fas fa-trash-alt mr-1"></i> Delete
                    </button>
                </td>
            `;
            materialsTableBody.appendChild(tr);
        });
    }

    // Form submit for Add / Edit
    window.saveMaterial = async function() {
        const token = localStorage.getItem('token');
        const id = materialIdInput.value;
        const subjectName = materialSubjectNameInput.value.trim();
        const department = materialDepartmentSelect.value;
        const courseYear = materialCourseYearSelect.value;
        const sem1 = materialSem1Input.value.trim();
        const sem2 = materialSem2Input.value.trim();
        const activeStatus = materialActiveStatusCheckbox.checked;

        if (!subjectName || !department || !courseYear) {
            showFacultyMessage("Subject name, department, and course year are required.", false);
            return;
        }

        const payload = { subjectName, department, courseYear, sem1, sem2, activeStatus };
        const url = id ? `${BASE_URL}/api/faculty/materials/${id}` : `${BASE_URL}/api/faculty/materials`;
        const method = id ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            const result = await response.json();
            if (response.ok && result.success) {
                showFacultyMessage(id ? "Material updated successfully!" : "Material added successfully!", true);
                clearMaterialForm();
                fetchMaterials();
            } else {
                showFacultyMessage(result.message || "Failed to save material.", false);
            }
        } catch (error) {
            showFacultyMessage("Connection error while saving material.", false);
        }
    };

    materialForm.addEventListener('submit', (e) => {
        e.preventDefault();
        window.saveMaterial();
    });

    window.editMaterial = function(id) {
        const material = materialsList.find(m => m.id === id);
        if (!material) return;

        formTitle.textContent = "Edit Study Material";
        materialIdInput.value = material.id;
        materialSubjectNameInput.value = material.subjectName;
        materialDepartmentSelect.value = material.department;
        materialCourseYearSelect.value = material.courseYear;
        materialSem1Input.value = material.sem1 || '';
        materialSem2Input.value = material.sem2 || '';
        materialActiveStatusCheckbox.checked = material.activeStatus;
        
        saveMaterialBtn.textContent = "Update Material";
        facultyFormMessage.classList.add('hidden');
        materialSubjectNameInput.focus();
        // Smooth scroll to form
        document.getElementById('formTitle').scrollIntoView({ behavior: 'smooth' });
    };

    window.deleteMaterial = async function(id) {
        if (!confirm("Are you sure you want to delete this study material record?")) return;

        const token = localStorage.getItem('token');
        try {
            const response = await fetch(`${BASE_URL}/api/faculty/materials/${id}`, {
                method: "DELETE",
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                }
            });
            const result = await response.json();
            if (response.ok && result.success) {
                showFacultyMessage("Material deleted successfully!", true);
                fetchMaterials();
                if (materialIdInput.value == id) {
                    clearMaterialForm();
                }
            } else {
                showFacultyMessage(result.message || "Failed to delete material.", false);
            }
        } catch (error) {
            showFacultyMessage("Connection error while deleting material.", false);
        }
    };

    window.clearMaterialForm = function() {
        materialForm.reset();
        materialIdInput.value = '';
        formTitle.textContent = "Add New Study Material";
        saveMaterialBtn.textContent = "Save Material";
        facultyFormMessage.classList.add('hidden');
    };

    clearFormBtn.addEventListener('click', window.clearMaterialForm);

    function showFacultyMessage(message, isSuccess) {
        facultyFormMessage.textContent = message;
        facultyFormMessage.className = `p-2 rounded text-center text-sm font-semibold mt-2 ${
            isSuccess ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
        }`;
        facultyFormMessage.classList.remove('hidden');
    }

    // --- Table Filtering ---
    window.filterMaterialsTable = function() {
        const query = tableSearchInput.value.toLowerCase();
        const rows = materialsTableBody.getElementsByTagName('tr');
        
        for (let i = 0; i < rows.length; i++) {
            const cells = rows[i].getElementsByTagName('td');
            if (cells.length > 0) {
                const subject = cells[0].textContent || cells[0].innerText;
                if (subject.toLowerCase().indexOf(query) > -1) {
                    rows[i].style.display = "";
                } else {
                    rows[i].style.display = "none";
                }
            }
        }
    };

    tableSearchInput.addEventListener('keyup', window.filterMaterialsTable);

    // --- Accordion Animation Handling ---
    const accordionButtons = document.querySelectorAll('.accordion-button');
    accordionButtons.forEach(button => {
        button.addEventListener('click', () => {
            const targetId = button.dataset.target;
            const targetContent = document.querySelector(targetId);
            if (!targetContent) return;

            const parentAccordionId = button.dataset.parentAccordion;
            if (parentAccordionId) {
                const parentAccordion = document.querySelector(parentAccordionId);
                if (parentAccordion) {
                    const allDepartmentContents = parentAccordion.querySelectorAll(':scope > .accordion-item > .accordion-body-content');
                    allDepartmentContents.forEach(content => {
                        if (content !== targetContent && content.classList.contains('show')) {
                            content.classList.remove('show');
                            const associatedButton = content.previousElementSibling.querySelector('.accordion-button');
                            if (associatedButton) associatedButton.classList.remove('expanded');
                        }
                    });
                }
            }

            targetContent.classList.toggle('show');
            button.classList.toggle('expanded');
            const isExpanded = targetContent.classList.contains('show');
            button.setAttribute('aria-expanded', isExpanded);
        });
    });

    // Helper function to escape HTML to prevent XSS
    function escapeHtml(text) {
        if (!text) return '';
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, function(m) { return map[m]; });
    }
});
