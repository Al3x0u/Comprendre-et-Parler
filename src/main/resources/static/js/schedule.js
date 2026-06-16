//CONFIG

/** @type {HTMLElement} Hidden element containing server-side configuration */
const config = document.getElementById('schedule-config');

/** @type {Array<Object>} List of all events loaded from the server */
const allEvents = JSON.parse(config.dataset.events);
/** @type {string} Role of the logged-in user ('MANAGER', 'INTERPRETER', 'BENEFICIARY') */
const userRole = config.dataset.role;

/** @type {number} ID of the logged-in user */
const userId = parseInt(config.dataset.userId);

/**
 * Full name of the logged-in user (manager or interpreter), used to pre-filter on load
 * Empty string for BENEFICIARY.
 * @type {string}
 */
const userFullName = config.dataset.userName || '';

/** @type {boolean} True if the screen width is less than 768px */
const isMobile = window.innerWidth < 768;

/** @type {ActiveFilters} */
const activeFilters = {
    status: null,
    userId: null
};

if (userRole === 'MANAGER' || userRole === "INTERPRETER") {
    activeFilters.userId = userId;
}
/** @type {number|null} ID of the currently selected mission */
let currentMissionId = null;

/** @type {boolean} Whether this is the first calendar load */
let premierChargement = true;

/**
 * Hidden all events of the week-end
 * @type {Array<Object>}
 */
let cachedEvents = allEvents.slice();

/** The calendar */
let calendar;

//UTILS

/**
 * Displays a notification toast in the bottom-right corner of the screen,
 * which disappears after a delay proportional to the message length.
 *
 * @param {string} message -text to display in the toast
 * @param {'success'|'error'|'info'} [type='success'] - Visual type of the toast
 * @returns {void}
 */
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast-custom toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    const duration = message.length > 60 ? 10000 : 5000;
    setTimeout(() => toast.classList.add('toast-hide'), duration);
    setTimeout(() => toast.remove(), duration + 300);
}

/**
 * @typedef {Object} ValidationRule
 * @property {string} id      - ID of the form field to validate
 * @property {string} errorId - ID of the element displaying the error message
 * @property {string} msg     - Error message to display if the field is empty
 */

/**
 * Validates a list of form fields. Marks each empty field as invalid
 * and displays the associated error message.
 *
 * @param {ValidationRule[]} rules - Array of validation rules
 * @returns {boolean} `true` if all fields are valid, `false` otherwise
 */
function validateFields(rules) {
    let isValid = true;
    rules.forEach(({ id, errorId, msg }) => {
        const field = document.getElementById(id);
        const errorEl = document.getElementById(errorId);
        if (!field || !field.value.trim()) {
            if (field) field.classList.add('is-invalid');
            if (errorEl) errorEl.textContent = msg;
            isValid = false;
        }
    });
    return isValid;
}

/**
 * Clears validation errors from a list of form fields.
 * Removes the `is-invalid` class and empties the associated error text.
 *
 * @param {string[]} fieldIds - Array of field IDs to clean up
 * @returns {void}
 */
function clearFormErrors(fieldIds) {
    fieldIds.forEach(id => {
        const field = document.getElementById(id);
        if (field) field.classList.remove('is-invalid');
        const errorEl = document.getElementById(id + 'Error');
        if (errorEl) errorEl.textContent = '';
    });
}

/**
 * Sets up the user filter (Manager only)
 * Clicking a user item makes it the active filter (exclusive —always one selected)
 */
function setupUserFilter() {
    document.querySelectorAll('.filter-user-item').forEach(item => {
        item.addEventListener('click', e => {
            e.preventDefault();
            const newUserId = parseInt(item.dataset.id);
            if (activeFilters.userId === newUserId) return;
            activeFilters.userId = newUserId;
            highlightActiveUser();
            calendar.refetchEvents();
            document.getElementById('dropdown-filtre').classList.remove('show');
            document.querySelector('.fc-filterBtn-button')?.classList.remove('active');
        });
    });
}

/**
 * Highlights the currently active user item in the filter list
 * Removes .fw-bold and .active from all items, then applies them to the matching one
 */
function highlightActiveUser() {
    document.querySelectorAll('.filter-user-item').forEach(i => {
        i.classList.remove('fw-bold', 'active');
    });
    if (activeFilters.userId != null) {
        const active = Array.from(document.querySelectorAll('.filter-user-item'))
            .find(i => parseInt(i.dataset.id) === activeFilters.userId);
        if (active) active.classList.add('fw-bold', 'active');
    }
}

/**
 * Sets up a toggle filter on a group of elements (for status filter).
 * Clicking an item toggles the filter on/off.
 *
 * @param {string} selector  - CSS selector targeting the filter items
 * @param {string} filterKey - Key in activeFilters to update
 */
function setupFilter(selector, filterKey) {
    document.querySelectorAll(selector).forEach(item => {
        item.addEventListener('click', e => {
            e.preventDefault();
            activeFilters[filterKey] = activeFilters[filterKey] === item.dataset.value ? null : item.dataset.value;
            document.querySelectorAll(selector).forEach(i => i.classList.remove('fw-bold'));
            if (activeFilters[filterKey]) item.classList.add('fw-bold');
            calendar.refetchEvents();
            document.getElementById('dropdown-filtre').classList.remove('show');
        });
    });
}

/**
 * Generates a string of Bootstrap star icons for a given importance level.
 *
 * @param {number} importance - Number of stars to display
 * @param {number} [max=3]    - Maximum number of stars to display
 * @returns {string} HTML string of star icons
 */
function buildStars(importance, max = 3) {
    let stars = '';
    for (let i = 0; i < importance && i < max; i++) {
        stars += `<i class="bi bi-star-fill me-1"></i>`;
    }
    return stars;
}

/** @type {Array<{id: string, name: string}>} Interpreters currently selected for the mission form */
let selectedMissionInterpreters = [];

/**
 * Renders the selected-interpreters badges in the mission form,
 * each with a remove button that updates the selection, and hides
 * the corresponding options in the interpreter select to prevent duplicates.
 * @returns {void}
 */
function renderInterpreterBadges() {
    const container = document.getElementById('missionInterpreterBadges');
    container.innerHTML = '';
    selectedMissionInterpreters.forEach(interp => {
        const badge = document.createElement('span');
        badge.className = 'interpreter-badge';
        badge.innerHTML = `${interp.name} <i class="bi bi-x" data-id="${interp.id}"></i>`;
        badge.querySelector('i').addEventListener('click', () => {
            selectedMissionInterpreters = selectedMissionInterpreters.filter(x => x.id !== interp.id);
            renderInterpreterBadges();
        });
        container.appendChild(badge);
    });

    const select = document.getElementById('missionInterpreterSelect');
    Array.from(select.options).forEach(opt => {
        if (!opt.value) return;
        opt.hidden = selectedMissionInterpreters.some(i => i.id === opt.value);
    });
}

/**
 * Maps a mission status label to the corresponding badge CSS class.
 * @param {string} status - the status label (e.g. "Acceptée", "En attente")
 * @returns {string} the CSS class to apply to the status badge
 */
function getStatusBadgeClass(status) {
    const s = (status || '').toLowerCase();
    if (s.includes('accept')) return 'status-badge--accepted';
    if (s.includes('attente')) return 'status-badge--pending';
    if (s.includes('refus') || s.includes('annul')) return 'status-badge--refused';
    return 'status-badge--base';
}

/**
 * Fills a creation modal (mission or request) with existing data and switches it to edit mode.
 * Changes the submit button to send a PUT instead of POST.
 *
 * @param {'mission'|'request'} type - Which modal to fill
 * @param {Object} props             - extendedProps of the FullCalendar event
 * @param {Object} event             - The FullCalendar event object
 * @param {number} missionId         - The ID of the mission to edit
 */
function fillAndOpenEditModal(type, props, event, missionId) {
    let prefix;
    let modalId;
    let btnId;
    if (type === 'mission') {
        prefix = 'mission';
        modalId = 'newMissionModal';
        btnId = 'sendMissionBtn';
    } else {
        prefix = 'request';
        modalId = 'newRequestModal';
        btnId = 'sendRequestBtn';
    }

    const titleEl = document.getElementById(modalId + 'Title');

    if (type === 'mission') {
        titleEl.innerText = 'Modifier la mission';
    } else {
        titleEl.innerText = 'Modifier la demande';
    }

    if (event.title) {
        document.getElementById(prefix + 'Title').value = event.title;
    } else {
        document.getElementById(prefix + 'Title').value = '';
    }
    if (event.start) {
        document.getElementById(prefix + 'Date').value =
            event.start.toISOString().substring(0, 10);
    } else {
        document.getElementById(prefix + 'Date').value = '';
    }

    document.getElementById(prefix + 'LocationDesignation').value = props.locationDesignation || '';
    document.getElementById(prefix + 'Street').value = props.street || '';
    document.getElementById(prefix + 'StreetNumber').value = props.streetNumber || '';
    document.getElementById(prefix + 'Box').value = props.box || '';
    document.getElementById(prefix + 'Room').value = props.room || '';
    document.getElementById(prefix + 'Comment').value = props.comment || '';

    let startTime = '';
    if (event.start) {
        startTime = event.start.toTimeString().substring(0, 5);
    }
    let endTime = '';

    if (event.end) {
        endTime = event.end.toTimeString().substring(0, 5);
    }

    document.getElementById(prefix + 'StartTime').value = startTime;
    document.getElementById(prefix + 'EndTime').value = endTime;
    const citySelect = document.getElementById(prefix + 'City');

    if (props.postalCode) {
        citySelect.value = props.postalCode;
    }

    document.getElementById(prefix + 'CityName').value = props.city || '';
    const academicSelect = document.getElementById(prefix + 'AcademicSkill');

    if (props.academicSkillId) {
        academicSelect.value = props.academicSkillId;
    }
    let radioName;

    if (type === 'mission') {
        radioName = 'missionType';
    } else {
        radioName = 'requestType';
    }

    document.querySelectorAll('input[name="' + radioName + '"]').forEach(function (radio) {
        radio.checked = (radio.value === String(props.jobSkillId || ''));
    });

    if (type === 'request') {
        if (props.importance !== undefined) {

            const importanceRadio = document.querySelector(
                'input[name="requestImportance"][value="' + props.importance + '"]'
            );

            if (importanceRadio) {
                importanceRadio.checked = true;
            }
        }
    }

    if (type === 'mission') {

        selectedMissionInterpreters = [];
        if (props.interpreterIds) {
            const ids = props.interpreterIds.split(',').map(s => s.trim()).filter(s => s !== '');
            const names = (props.interpreter || '').split(',').map(s => s.trim()).filter(s => s !== '');
            ids.forEach((id, i) => {
                selectedMissionInterpreters.push({ id, name: names[i] || id });
            });
        }
        renderInterpreterBadges();

        const beneficiarySelect = document.getElementById('missionBeneficiary');
        if (props.beneficiaryId) {
            beneficiarySelect.value = props.beneficiaryId;
        }
    }

    const btn = document.getElementById(btnId);

    btn.dataset.editMode = 'true';
    btn.dataset.missionId = missionId;
    btn.innerText = 'Enregistrer';

    bootstrap.Modal
        .getOrCreateInstance(document.getElementById(modalId))
        .show();
}

/**
 * Fetches the list of available interpreters for a mission and populates
 * the given select element with the results.
 *
 * @param {number} missionId - The ID of the mission to fetch available interpreters for
 * @param {HTMLSelectElement} interpreterSelect - The select element to populate
 * @returns {void}
 */
function loadAvailableInterpreters(missionId, interpreterSelect) {
    interpreterSelect.innerHTML = '<option value="">Chargement...</option>';
    fetch('/horaire/missions/' + missionId + '/interpretes-disponibles')
        .then(res => res.json())
        .then(interpreters => {
            interpreterSelect.innerHTML = '<option value="">Sélectionner</option>';
            if (interpreters.length === 0) {
                interpreterSelect.innerHTML = '<option value="">Aucun interprète disponible</option>';
            } else {
                interpreters.forEach(i => {
                    const opt = document.createElement('option');
                    opt.value = i.id;
                    opt.textContent = i.name;
                    interpreterSelect.appendChild(opt);
                });
            }
        })
        .catch(() => {
            interpreterSelect.innerHTML = '<option value="">Erreur de chargement</option>';
        });
}

/**
 * Attaches the click handler to the "Accepter" button in the manager modal.
 * Checks the selected interpreter's quota before accepting; if the quota
 * is exceeded, shows a confirmation modal allowing the manager to override it.
 *
 * @param {HTMLButtonElement|null} acceptBtn - The accept button element, or null if not present
 * @param {number} currentMissionId - The ID of the mission being accepted
 * @listens click
 * @returns {void}
 */
function setupAcceptButton(acceptBtn, currentMissionId) {
    if (!acceptBtn) return;
    acceptBtn.addEventListener('click', async function () {
        const selectedInterpreter = document.getElementById('managerPendingInterpreter').value;
        if (!selectedInterpreter) {
            showToast("Veuillez sélectionner un interprète.", 'error');
            return;
        }

        const doAccept = async () => {
            try {
                await fetch('/horaire/missions/' + currentMissionId + '/accepter', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ interpreterId: selectedInterpreter })
                });
                bootstrap.Modal.getOrCreateInstance(document.getElementById('quotaWarningModal')).hide();
                bootstrap.Modal.getOrCreateInstance(document.getElementById('managerPendingModal')).hide();
                calendar.refetchEvents();
                showToast("Mission acceptée.", 'success');
            } catch (err) {
                showToast("Erreur : " + err.message, 'error');
            }
        };

        try {
            const checkRes = await fetch('/horaire/missions/' + currentMissionId + '/verifier-quota', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ interpreterId: selectedInterpreter })
            });

            const warning = await checkRes.text();

            if (warning && warning.trim() !== '') {
                document.getElementById('quotaWarningMessage').innerText = warning;
                const quotaModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('quotaWarningModal'));
                const managerModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('managerPendingModal'));

                document.getElementById('quotaWarningConfirm').onclick = async () => { await doAccept(); };
                document.getElementById('quotaWarningCancel').onclick = () => {
                    quotaModal.hide();
                    managerModal.show();
                };

                document.getElementById('managerPendingModal').addEventListener('hidden.bs.modal', () => {
                    quotaModal.show();
                }, { once: true });

                managerModal.hide();
            } else {
                await doAccept();
            }
        } catch (err) {
            showToast("Erreur : " + err.message, 'error');
        }
    }, { once: true });
}

/**
 * Populates and displays the manager modal for a pending or accepted mission.
 * Shows different content and footer actions depending on the mission status
 * (pending missions can be edited, refused or accepted; accepted missions
 * can be edited or cancelled).
 *
 * @param {Object} event - The FullCalendar event object
 * @param {Object} props - extendedProps of the FullCalendar event
 * @param {number} currentMissionId - The ID of the mission being displayed
 * @returns {void}
 */
function openManagerModal(event, props, currentMissionId) {
    const start = event.start;
    const end = event.end;
    const status = (props.status || '').toLowerCase();
    const now = new Date();

    const beforeStart = start && now < start;
    const isPending = status.includes('en attente');
    const isAccepted = status.includes('accept');
    const interpreterSelect = document.getElementById('managerPendingInterpreter');
    const nameSpan = document.getElementById('managerPendingInterpreterName');

    if (isAccepted) {
        interpreterSelect.classList.add('d-none');
        nameSpan.classList.remove('d-none');

        const interpreterNames = (props.interpreter || '')
            .split(',')
            .map(name => name.trim())
            .filter(name => name !== '');
        nameSpan.textContent = interpreterNames.length > 0 ? interpreterNames.join(' • ') : 'Aucun interprète';
    } else {
        nameSpan.classList.add('d-none');
        interpreterSelect.classList.remove('d-none');
        interpreterSelect.disabled = !(isPending && beforeStart);
        interpreterSelect.value = '';
        loadAvailableInterpreters(currentMissionId, interpreterSelect);
    }

    const timeFormatter = new Intl.DateTimeFormat('fr-BE', { hour: '2-digit', minute: '2-digit' });
    let timeText = '';
    if (start) {
        timeText = timeFormatter.format(start);
        if (end) timeText += ' - ' + timeFormatter.format(end);
    }

    const importance = parseInt(props.importance || '0', 10);
    document.getElementById('managerPendingImportance').innerHTML = buildStars(importance, 5);
    document.getElementById('managerPendingTitle').innerText = event.title || '';
    document.getElementById('managerPendingDate').innerText = start ? start.toLocaleDateString('fr-BE') : '';
    document.getElementById('managerPendingTime').innerText = timeText || '';
    document.getElementById('managerPendingLocation').innerText = props.address || props.room || '';
    document.getElementById('managerPendingBeneficiary').innerText = props.beneficiary || 'Aucun bénéficiaire';
    const typeBadge = document.getElementById('managerPendingType');
    if (props.type) {
        typeBadge.innerText = props.type;
        typeBadge.classList.remove('d-none');
    } else {
        typeBadge.classList.add('d-none');
    }

    const academicBadge = document.getElementById('managerPendingAcademicSkill');
    if (props.academicSkill) {
        academicBadge.innerText = props.academicSkill;
        academicBadge.classList.remove('d-none');
    } else {
        academicBadge.classList.add('d-none');
    }

    const commentSection = document.getElementById('managerPendingCommentSection');
    if (props.comment) {
        document.getElementById('managerPendingComment').innerText = props.comment;
        commentSection.classList.remove('d-none');
    } else {
        commentSection.classList.add('d-none');
    }

    const statusBadge = document.getElementById('managerPendingStatus');
    statusBadge.innerText = props.status || '';
    statusBadge.className = 'status-badge ms-2 ' + getStatusBadgeClass(props.status);

    const footer = document.querySelector('#managerPendingModal .modal-footer');
    footer.innerHTML = '';

    if (isPending && beforeStart) {
        footer.innerHTML = `
            <button type="button" class="btn btn-secondary" id="btnEditMission">Modifier</button>
            <button type="button" class="btn btn-danger"    id="btnRefuseMission">Refuser</button>
            <button type="button" class="btn btn-success"   id="btnAcceptMission">Accepter</button>
        `;
    } else if (isAccepted) {
        footer.innerHTML = `
            <button type="button" class="btn btn-secondary" id="btnEditMission">Modifier</button>
            <button type="button" class="btn btn-danger"    id="btnCancelAcceptedMission">Annuler la mission</button>
        `;
    }

    const btnEdit           = document.getElementById('btnEditMission');
    const acceptBtn         = document.getElementById('btnAcceptMission');
    const refuseBtn         = document.getElementById('btnRefuseMission');
    const cancelAcceptedBtn = document.getElementById('btnCancelAcceptedMission');

    if (btnEdit) {
        btnEdit.addEventListener('click', function() {
            const managerModalEl = document.getElementById('managerPendingModal');
            managerModalEl.addEventListener('hidden.bs.modal', function() {
                const editType = isPending ? 'request' : 'mission';
                fillAndOpenEditModal(editType, props, event, currentMissionId);
            }, { once: true });
            bootstrap.Modal.getOrCreateInstance(managerModalEl).hide();
        }, { once: true });
    }

    setupAcceptButton(acceptBtn, currentMissionId);

    if (refuseBtn) {
        refuseBtn.addEventListener('click', async function () {
            try {
                const res = await fetch('/horaire/missions/' + event.id + '/refuser', { method: 'POST' });
                if (!res.ok) {
                    showToast("Erreur lors du refus.", 'error');
                    return;
                }
                bootstrap.Modal.getOrCreateInstance(document.getElementById('managerPendingModal')).hide();
                calendar.refetchEvents();
                showToast("Mission refusée.", 'info');
            } catch (err) {
                showToast("Erreur : " + err.message, 'error');
            }
        }, { once: true });
    }

    if (cancelAcceptedBtn) {
        cancelAcceptedBtn.addEventListener('click', function() {
            bootstrap.Modal.getOrCreateInstance(document.getElementById('managerPendingModal')).hide();
            bootstrap.Modal.getOrCreateInstance(document.getElementById('confirmCancelModal')).show();
        });
    }

    bootstrap.Modal.getOrCreateInstance(document.getElementById('managerPendingModal')).show();
}

/**
 * Populates and displays the standard event modal for non-manager users,
 * or for refused/cancelled missions viewed by a manager.
 * Shows action buttons depending on the mission status and timing
 * (pending requests can be cancelled or edited; accepted missions occurring
 * today and not yet ended allow reporting a delay).
 *
 * @param {Object} event - The FullCalendar event object
 * @param {Object} props - extendedProps of the FullCalendar event
 * @param {number} currentMissionId - The ID of the mission being displayed
 * @returns {void}
 */
function openEventModal(event, props, currentMissionId) {
    const start = event.start;
    const end = event.end;
    const status = (props.status || '').toLowerCase();
    const now = new Date();
    const isPending = status.includes('en attente');
    const isAccepted = status.includes('accept');

    const timeFormatter = new Intl.DateTimeFormat('fr-BE', { hour: '2-digit', minute: '2-digit' });
    let timeText = '';
    if (start) {
        timeText = timeFormatter.format(start);
        if (end) timeText += ' - ' + timeFormatter.format(end);
    }

    document.getElementById('modalTitle').innerText = event.title || '';
    document.getElementById('modalTime').innerText = timeText || '';
    document.getElementById('modalDate').innerText = start ? start.toLocaleDateString('fr-BE') : '';
    document.getElementById('modalLocation').innerText = props.address || '';
    document.getElementById('modalBeneficiary').innerText = props.beneficiary || 'Aucun bénéficiaire';
    document.getElementById('modalInterpreter').innerText = props.interpreter || 'Aucun interprète';

    const statusBadge = document.getElementById('modalStatus');
    statusBadge.innerText = props.status || '';
    statusBadge.className = 'status-badge ms-2 ' + getStatusBadgeClass(props.status);

    const typeBadge = document.getElementById('modalType');
    if (props.type) {
        typeBadge.innerText = props.type;
        typeBadge.classList.remove('d-none');
    } else {
        typeBadge.classList.add('d-none');
    }

    const academicBadge = document.getElementById('modalAcademicSkill');
    if (props.academicSkill) {
        academicBadge.innerText = props.academicSkill;
        academicBadge.classList.remove('d-none');
    } else {
        academicBadge.classList.add('d-none');
    }

    const commentSection = document.getElementById('modalCommentSection');
    if (props.comment) {
        document.getElementById('modalComment').innerText = props.comment;
        commentSection.classList.remove('d-none');
    } else {
        commentSection.classList.add('d-none');
    }

    const actions = document.getElementById('modalActions');
    actions.innerHTML = '';

    const isSameDay = start && now.getFullYear() === start.getFullYear() && now.getMonth() === start.getMonth() && now.getDate() === start.getDate();
    const isBeforeEnd = end && now < end;

    if (isPending) {
        actions.innerHTML = `
            <button type="button" class="btn btn-danger" id="btnCancelRequest">Annuler la demande</button>
            <button type="button" class="btn btn-primary" id="btnEditRequest">Modifier la demande</button>
        `;
    } else if (isAccepted && isSameDay && isBeforeEnd) {
        actions.innerHTML = `
            <button type="button" class="btn btn-warning text-white" id="btnDelayReport">Signaler un retard</button>
        `;
    }

    const cancelBtn = document.getElementById('btnCancelRequest');
    const delayBtn = document.getElementById('btnDelayReport');
    const editRequestBtn = document.getElementById('btnEditRequest');

    if (delayBtn) {
        delayBtn.addEventListener('click', function() {
            bootstrap.Modal.getOrCreateInstance(document.getElementById('eventModal')).hide();
            bootstrap.Modal.getOrCreateInstance(document.getElementById('delayModal')).show();
        });
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            bootstrap.Modal.getOrCreateInstance(document.getElementById('eventModal')).hide();
            bootstrap.Modal.getOrCreateInstance(document.getElementById('confirmCancelModal')).show();
        });
    }

    if (editRequestBtn) {
        editRequestBtn.addEventListener('click', function() {
            const eventModalEl = document.getElementById('eventModal');
            eventModalEl.addEventListener('hidden.bs.modal', function() {
                fillAndOpenEditModal('request', props, event, currentMissionId);
            }, { once: true });
            bootstrap.Modal.getOrCreateInstance(eventModalEl).hide();
        }, { once: true });
    }

    bootstrap.Modal.getOrCreateInstance(document.getElementById('eventModal')).show();
}

/**
 * Resets the mission creation/edition form to its default empty state,
 * clearing all fields, selected interpreters, validation errors,
 * and exiting edit mode.
 * @returns {void}
 */
function resetMissionForm() {
    document.getElementById('missionTitle').value = '';
    document.getElementById('missionDate').value = '';
    document.getElementById('missionStartTime').selectedIndex = 0;
    document.getElementById('missionEndTime').selectedIndex = 0;
    document.getElementById('missionLocationDesignation').value = '';
    document.getElementById('missionStreet').value = '';
    document.getElementById('missionStreetNumber').value = '';
    document.getElementById('missionBox').value = '';
    document.getElementById('missionRoom').value = '';
    document.getElementById('missionComment').value = '';
    document.getElementById('missionCity').value = '';
    document.getElementById('missionCityName').value = '';
    document.getElementById('missionAcademicSkill').value = '';
    document.getElementById('missionBeneficiary').value = '';

    const typeRadios = document.querySelectorAll('input[name="missionType"]');
    typeRadios.forEach((radio, index) => radio.checked = index === 0);

    selectedMissionInterpreters = [];
    renderInterpreterBadges();

    clearFormErrors(['missionTitle', 'missionDate', 'missionLocationDesignation', 'missionCity', 'missionStreet']);
    document.getElementById('missionInterpreterError').textContent = '';
    document.getElementById('missionInterpreterError').style.display = '';

    const btn = document.getElementById('sendMissionBtn');
    btn.dataset.editMode = 'false';
    btn.dataset.missionId = '';
    btn.innerText = 'Envoyer';
    document.getElementById('newMissionModalTitle').innerText = 'Nouvelle mission';
}

/**
 * Resets the request creation/edition form to its default empty state,
 * clearing all fields, validation errors, and exiting edit mode.
 * @returns {void}
 */
function resetRequestForm() {
    document.getElementById('requestTitle').value = '';
    document.getElementById('requestDate').value = '';
    document.getElementById('requestStartTime').selectedIndex = 0;
    document.getElementById('requestEndTime').selectedIndex = 0;
    document.getElementById('requestLocationDesignation').value = '';
    document.getElementById('requestStreet').value = '';
    document.getElementById('requestStreetNumber').value = '';
    document.getElementById('requestBox').value = '';
    document.getElementById('requestRoom').value = '';
    document.getElementById('requestComment').value = '';
    document.getElementById('requestCity').value = '';
    document.getElementById('requestCityName').value = '';
    document.getElementById('requestAcademicSkill').value = '';

    const typeRadios = document.querySelectorAll('input[name="requestType"]');
    typeRadios.forEach((radio, index) => radio.checked = index === 0);

    const importance0 = document.getElementById('importance0');
    if (importance0) importance0.checked = true;

    clearFormErrors(['requestTitle', 'requestDate', 'requestLocationDesignation', 'requestCity', 'requestStreet']);

    const btn = document.getElementById('sendRequestBtn');
    btn.dataset.editMode = 'false';
    btn.dataset.missionId = '';
    btn.innerText = 'Envoyer';
    document.getElementById('newRequestModalTitle').innerText = 'Nouvelle demande';
}


//CALENDAR BUTTONS

const customButtons = {
    filterBtn: {
        text: 'Filtre ▾',
        click: function() {
            const btn = document.querySelector('.fc-filterBtn-button');
            const rect = btn.getBoundingClientRect();
            const dropdown = document.getElementById('dropdown-filtre');

            dropdown.style.cssText = ` position: fixed !important; top: ${rect.top + rect.height}px !important; left: ${rect.left}px !important; z-index: 9999 !important;`;

            dropdown.classList.toggle('show');
            btn.classList.toggle('active');
        }
    }
};

if (userRole === 'BENEFICIARY') {
    customButtons.newRequest = {
        text: '+ Nouvelle demande',
        click: function() {
            const newRequestModal = bootstrap.Modal.getOrCreateInstance(
                document.getElementById('newRequestModal')
            );
            newRequestModal.show();
        }
    };
}

if (userRole === 'INTERPRETER') {
    customButtons.newUnavailability = {
        text: '↗ Nouvelle indisponibilité',
        click: function() {
            window.location.href = '/profil';
        }
    };
}

if (userRole === 'MANAGER') {
    customButtons.newUnavailability = {
        text: isMobile ? '↗ Indispo' : '↗ Nouvelle indisponibilité',
        click: function() {
            window.location.href = '/profil';
        }
    };
    customButtons.newMission = {
        text: isMobile ? '+ Mission' : '+ Nouvelle mission',
        click: function () {
            selectedMissionInterpreters = [];
            renderInterpreterBadges();
            const newMissionModal = bootstrap.Modal.getOrCreateInstance(
                document.getElementById('newMissionModal')
            );
            newMissionModal.show();
        }
    };
    customButtons.viewRequests = {
        text: '↗ Voir demandes',
        click: function() {
            window.location.href = '/demandes';
        }
    };
}


//CALENDAR INIT

/**
 * Fetches missions for a given week from the server and updates the calendar.
 * If a user filter is active, scopes the query to that user's missions only.
 * Sends status and role filters to the server so all filtering happens in the DB.
 *
 * @param {Object}   fetchInfo          - Date range info provided by FullCalendar
 * @param {string}   fetchInfo.startStr - ISO date string of the week start
 * @param {Function} successCallback    - FullCalendar callback, called with the event array
 * @param {Function} failureCallback    - FullCalendar callback, called with the error on fetch failure
 * @returns {Promise<void>}
 */
async function fetchEvents(fetchInfo, successCallback, failureCallback) {
    const params = new URLSearchParams();
    params.append('weekDate', fetchInfo.startStr.substring(0, 10));

    if (activeFilters.userId != null && !isNaN(activeFilters.userId)) {
        params.append('userId', activeFilters.userId);
        const activeItem = document.querySelector('.filter-user-item.active');
        if (activeItem) params.append('role', activeItem.dataset.role);
    }

    if (activeFilters.status != null) {
        params.append('status', activeFilters.status);
    }

    try {
        const r = await fetch('/horaire/evenements?' + params.toString());
        const data = await r.json();
        cachedEvents = data;
        successCallback(data);
    } catch (err) {
        failureCallback(err);
    }
}
document.addEventListener('input', function (e) {
    if (e.target.classList.contains('is-invalid')) {
        e.target.classList.remove('is-invalid');
        const errorEl = document.getElementById(e.target.id + 'Error');
        if (errorEl) errorEl.textContent = '';
    }
});

document.addEventListener('DOMContentLoaded', function() {
    calendar = new FullCalendar.Calendar(document.getElementById('calendar'), {
        initialView: isMobile ? 'timeGridDay' : 'timeGridWeek',
        height: isMobile ? '75vh' : '83vh',
        locale: 'fr',
        firstDay: 1,
        weekends: true,
        allDaySlot: false,
        businessHours: {
            daysOfWeek: [ 1, 2, 3, 4, 5 ],
            startTime: '00:00',
            endTime: '23:59',
        },
        slotDuration: '00:30:00',
        slotMinTime: '08:00:00',
        slotMaxTime: '22:00:00',
        expandRows: true,

        /**
         * Event loading function for FullCalendar.
         * Uses pre-loaded data on the first render, then queries
         * the API on each subsequent navigation.
         *
         * @param {Object}   fetchInfo       - Information about the requested date range
         * @param {Function} successCallback - Callback called with the events
         * @param {Function} failureCallback - Callback called on error
         */
        events: function(fetchInfo, successCallback, failureCallback) {
            if (premierChargement) {
                premierChargement = false;
                successCallback(cachedEvents);
                return;
            }
            fetchEvents(fetchInfo, successCallback, failureCallback);
        },
        customButtons: customButtons,
        headerToolbar: isMobile ? {
            start: 'today',
            center: 'prev,title,next',
            end: ''
        } : {
            start: 'timeGridWeek,timeGridDay filterBtn viewRequests',
            center: 'prev title next',
            end: userRole === 'MANAGER' ? 'newUnavailability newMission today'
                : userRole === 'INTERPRETER' ? 'newUnavailability today'
                    : 'newRequest today'
        },
        footerToolbar: isMobile ? {
            start: 'filterBtn',
            center: '',
            end: userRole === 'MANAGER' ? 'newMission newUnavailability viewRequests'
                : userRole === 'INTERPRETER' ? 'newUnavailability'
                    : 'newRequest'
        } : false,
        buttonText: {
            week: 'Semaine',
            day: 'Jour',
            today: isMobile ? 'Auj.' : "Aujourd'hui"
        },

        /**
         * Customizes the HTML rendering of a calendar event.
         * Displays importance stars, title, time, type and room.
         *
         * @param {Object} arg          - Argument provided by FullCalendar
         * @param {Object} arg.event    - FullCalendar event object
         * @param {string} arg.timeText - Time text formatted by FullCalendar
         * @returns {{ html: string }}  - Object containing the custom HTML to inject
         */
        eventContent: function(arg) {
            const props = arg.event.extendedProps;
            const importance = parseInt(props.importance);
            const stars = buildStars(importance);
            return {
                html: `
                    <div class="fc-event-content-inner p-1">
                        <div class="position-absolute top-0 end-0 pe-1 text-white fw-bold">
                            ${stars}
                        </div>
                        <div class="fw-bold">${arg.event.title}</div>
                        <div>${arg.timeText}</div>
                        <div>${props.type || ''}</div>
                        <div><i class="bi bi-pin-map-fill"></i> ${props.room || ''}</div>
                    </div>
                `
            };
        },

        /**
         * Handles a click on an empty calendar slot.
         * Opens the mission creation modal (MANAGER) or request creation modal (BENEFICIARY),
         * pre-filling the date and start/end times based on the clicked slot.
         *
         * @param {Object} info      - Information about the clicked slot
         * @param {Date}   info.date - Date and time of the clicked slot
         * @returns {void}
         */
        dateClick: function(info) {
            const clickedDate = new Date(info.date);
            const endHour = new Date(clickedDate);
            endHour.setMinutes(endHour.getMinutes() + 60);

            const formatDate = (date) => date.toLocaleDateString('en-CA');
            const formatTime = (date) => date.toLocaleTimeString('fr-BE', {
                hour: '2-digit',
                minute: '2-digit',
            });
            const dateValue = formatDate(clickedDate);
            const startHourValue = formatTime(clickedDate);
            const endHourValue = formatTime(endHour);

            if (userRole === 'MANAGER') {
                selectedMissionInterpreters = [];
                renderInterpreterBadges();
                document.getElementById('missionDate').value = dateValue;
                document.getElementById('missionStartTime').value = startHourValue;
                document.getElementById('missionEndTime').value = endHourValue;
                bootstrap.Modal.getOrCreateInstance(document.getElementById('newMissionModal')).show();
            } else if (userRole === 'BENEFICIARY') {
                document.getElementById('requestDate').value = dateValue;
                document.getElementById('requestStartTime').value = startHourValue;
                document.getElementById('requestEndTime').value = endHourValue;
                bootstrap.Modal.getOrCreateInstance(document.getElementById('newRequestModal')).show();
            }
        },

        /**
         * Handles a click on a calendar event.
         * Displays the appropriate detail modal based on the user's role and the event's
         * status (manager modal for pending/accepted missions, standard modal otherwise).
         *
         * @param {Object} info       - Information about the clicked event
         * @param {Object} info.event - FullCalendar event object
         * @returns {void}
         */
        eventClick: function(info) {
            const event = info.event;
            currentMissionId = event.id;
            const props = event.extendedProps;
            const status = (props.status || '').toLowerCase();
            const isManager = userRole === 'MANAGER';
            const isPending = status.includes('en attente');
            const isAccepted = status.includes('accept');

            if (isManager && (isPending || isAccepted)) {
                openManagerModal(event, props, currentMissionId);
                return;
            }

            openEventModal(event, props, currentMissionId);
        }
    });

    document.addEventListener('click', (e) => {
        if (!e.target.closest('.fc-filterBtn-button') && !e.target.closest('#dropdown-filtre')) {
            document.getElementById('dropdown-filtre').classList.remove('show');
            document.querySelector('.fc-filterBtn-button').classList.remove('active');
        }
    });

    document.getElementById('missionInterpreterSelect').addEventListener('change', function() {
        const id = this.value;
        if (!id) return;
        if (!selectedMissionInterpreters.some(x => x.id === id)) {
            const name = this.options[this.selectedIndex].dataset.name;
            selectedMissionInterpreters.push({ id, name });
            renderInterpreterBadges();
        }
        this.value = '';
    });

    document.getElementById('search-interpreter')?.addEventListener('input', function() {
        const query = this.value.trim().toLowerCase();
        let totalVisible = 0;
        ['section-manager', 'section-interpreter', 'section-beneficiary'].forEach(sectionId => {
            const section = document.getElementById(sectionId);
            if (!section) return;
            const items = section.querySelectorAll('.filter-user-item');
            let visibleInSection = 0;
            items.forEach(item => {
                const name = item.dataset.value.toLowerCase();
                const matches = !query || name.includes(query);
                item.style.display = matches ? '' : 'none';
                if (matches) visibleInSection++;
            });
            section.style.display = visibleInSection > 0 ? '' : 'none';
            totalVisible += visibleInSection;
        });
        const noResult = document.getElementById('no-user-result');
        if (noResult) noResult.style.display = totalVisible === 0 ? '' : 'none';
    });

    document.getElementById('search-interpreter')?.addEventListener('click', function(e) {
        e.stopPropagation();
    });

    /**
     * Handles a click on the "No" button in the cancellation confirmation modal.
     * Closes the confirmation modal and re-opens the previous modal
     * based on the user's role.
     *
     * @listens click
     */
    document.getElementById('cancelConfirmBackBtn').addEventListener('click', function() {
        bootstrap.Modal.getOrCreateInstance(document.getElementById('confirmCancelModal')).hide();
        if (userRole === 'MANAGER') {
            bootstrap.Modal.getOrCreateInstance(document.getElementById('managerPendingModal')).show();
        } else {
            bootstrap.Modal.getOrCreateInstance(document.getElementById('eventModal')).show();
        }
    });

    /**
     * Handles a click on the "Yes, cancel" button in the cancellation confirmation modal.
     * Sends a POST request to cancel the currently selected mission.
     * Closes the confirmation modal, refreshes the calendar and displays a toast on success.
     *
     * @listens click
     * @returns {Promise<void>}
     */
    document.getElementById('confirmCancelBtn').addEventListener('click', async function () {
        if (!currentMissionId) return;
        try {
            const res = await fetch('/horaire/missions/' + currentMissionId + '/annuler', {
                method: 'POST'
            });
            if (!res.ok) {
                showToast("Erreur lors de l'annulation.", 'error');
                return;
            }
            bootstrap.Modal.getOrCreateInstance(document.getElementById('confirmCancelModal')).hide();
            calendar.refetchEvents();
            showToast("Mission annulée.", 'info');
        } catch (err) {
            showToast("Erreur : " + err.message, 'error');
        }
    });

    /**
     * Handles a click on "Cancel" in the delay report modal.
     * Closes the delay modal and re-opens the event modal.
     *
     * @listens click
     */
    document.getElementById('cancelDelayModalBtn').addEventListener('click', function() {
        bootstrap.Modal.getOrCreateInstance(document.getElementById('delayModal')).hide();
        bootstrap.Modal.getOrCreateInstance(document.getElementById('eventModal')).show();
    });

    /**
     * Sends a POST request to report a delay for the selected mission.
     * Includes the number of delay minutes and the absence indicator.
     * Displays a confirmation or error toast.
     *
     * @listens click
     * @returns {Promise<void>}
     */
    document.getElementById('sendDelayBtn').addEventListener('click', async function () {
        if (!currentMissionId) {
            alert("Aucune mission sélectionnée.");
            return;
        }
        const minutes = document.getElementById('delayMinutes').value;
        const absent = document.getElementById('delayAbsent').checked;
        try {
            const res = await fetch('/horaire/missions/' + currentMissionId + '/retard', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ minutes, absent })
            });
            if (!res.ok) {
                showToast("Erreur lors du signalement.", 'error');
                return;
            }
            bootstrap.Modal.getOrCreateInstance(document.getElementById('delayModal')).hide();
            showToast("Retard signalé avec succès.", 'success');
        } catch (err) {
            showToast("Erreur : " + err.message, 'error');
        }
    });

    /**
     * Validates and submits the new mission creation form (MANAGER role).
     * Checks required fields and time consistency, then sends
     * the data via POST to `/horaire/missions`. Refreshes the calendar on success.
     *
     * @listens click
     * @returns {Promise<void>}
     */
    document.getElementById('sendMissionBtn').addEventListener('click', async function () {
        clearFormErrors(['missionTitle', 'missionDate', 'missionLocationDesignation', 'missionCity', 'missionStreet', 'missionInterpreter']);
        const rules = [
            { id: 'missionTitle',               errorId: 'missionTitleError',               msg: 'Le titre est requis.' },
            { id: 'missionDate',                errorId: 'missionDateError',                msg: 'La date est requise.' },
            { id: 'missionLocationDesignation', errorId: 'missionLocationDesignationError', msg: 'Le lieu est requis.' },
            { id: 'missionCity',                errorId: 'missionCityError',                msg: 'La ville est requise.' },
            { id: 'missionStreet',     errorId: 'missionStreetError',     msg: 'La rue est requise.' },
        ];
        if (!validateFields(rules)) return;

        if (selectedMissionInterpreters.length === 0) {
            document.getElementById('missionInterpreterError').textContent = 'Veuillez sélectionner au moins un interprète.';
            document.getElementById('missionInterpreterError').style.display = 'block';
            return;
        }

        const startTime = document.getElementById('missionStartTime').value;
        const endTime   = document.getElementById('missionEndTime').value;
        if (startTime >= endTime){
            showToast("L'heure de fin doit être après l'heure de début.", 'error');
            return;
        }
        const payload = {
            jobSkillId:          document.querySelector('input[name="missionType"]:checked')?.value || '',
            title:               document.getElementById('missionTitle').value,
            date:                document.getElementById('missionDate').value,
            startTime,
            endTime,
            locationDesignation: document.getElementById('missionLocationDesignation').value,
            city:     document.getElementById('missionCityName').value,
            postalCode: document.getElementById('missionCity').value,
            street:              document.getElementById('missionStreet').value,
            streetNumber:        document.getElementById('missionStreetNumber').value,
            box:                 document.getElementById('missionBox').value,
            interpreterIds:      selectedMissionInterpreters.map(x => x.id),
            beneficiaryId:       document.getElementById('missionBeneficiary').value,
            comment:             document.getElementById('missionComment').value,
            room:                document.getElementById('missionRoom').value,
            academicSkillId:     document.getElementById('missionAcademicSkill').value,
        };
        try {
            const btn = document.getElementById('sendMissionBtn');
            const isEditMode = btn.dataset.editMode === 'true';
            const missionIdToEdit = btn.dataset.missionId;

            let url;
            if (isEditMode) {
                url = '/horaire/missions/' + missionIdToEdit + '/modifier';
            } else {
                url = '/horaire/missions';
            }
            const res = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!res.ok) {
                const errorMsg = await res.text();
                if (res.status === 409 && errorMsg) {
                    showToast(errorMsg, 'error');
                } else {
                    showToast("Erreur lors de l'enregistrement de la mission.", 'error');
                }
                return;
            }
            btn.dataset.editMode = 'false';
            btn.innerText = 'Envoyer';
            document.getElementById('newMissionModalTitle').innerText = 'Nouvelle mission';
            bootstrap.Modal.getOrCreateInstance(document.getElementById('newMissionModal')).hide();
            calendar.refetchEvents();
            if (isEditMode) {
                showToast("Mission modifiée avec succès.", 'success');
            } else {
                showToast("Mission créée avec succès.", 'success');
            }
        } catch (err) {
            showToast("Erreur : " + err.message, 'error');
        }
    });

    /**
     * Validates and submits the new request creation form (BENEFICIARY role).
     * Checks required fields and time consistency, then sends
     * the data via POST to `/horaire/requetes`. Refreshes the calendar on success.
     *
     * @listens click
     * @returns {Promise<void>}
     */
    document.getElementById('sendRequestBtn').addEventListener('click', async function () {
        clearFormErrors(['requestTitle', 'requestDate', 'requestLocationDesignation', 'requestCity', 'requestStreet']);
        const rules = [
            { id: 'requestTitle',               errorId: 'requestTitleError',               msg: 'Le titre est requis.' },
            { id: 'requestDate',                errorId: 'requestDateError',                msg: 'La date est requise.' },
            { id: 'requestLocationDesignation', errorId: 'requestLocationDesignationError', msg: 'Le lieu est requis.' },
            { id: 'requestCity',                errorId: 'requestCityError',                msg: 'La ville est requise.' },
            { id: 'requestStreet',     errorId: 'requestStreetError',     msg: 'La rue est requise.' },
        ];
        if (!validateFields(rules)) return;
        const startTime = document.getElementById('requestStartTime').value;
        const endTime   = document.getElementById('requestEndTime').value;
        if (startTime >= endTime) {
            showToast("L'heure de fin doit être après l'heure de début.", 'error');
            return;
        }
        const payload = {
            jobSkillId:          document.querySelector('input[name="requestType"]:checked').value,
            title:               document.getElementById('requestTitle').value,
            date:                document.getElementById('requestDate').value,
            startTime,
            endTime,
            locationDesignation: document.getElementById('requestLocationDesignation').value,
            city:           document.getElementById('requestCityName').value,
            postalCode:         document.getElementById('requestCity').value,
            room:             document.getElementById('requestRoom').value,
            academicSkillId:  document.getElementById('requestAcademicSkill').value,
            street:              document.getElementById('requestStreet').value,
            streetNumber:        document.getElementById('requestStreetNumber').value,
            box:                 document.getElementById('requestBox').value,
            comment:             document.getElementById('requestComment').value,
            importance:          document.querySelector('input[name="requestImportance"]:checked').value
        };
        try {
            const btn = document.getElementById('sendRequestBtn');
            const isEditMode = btn.dataset.editMode === 'true';
            const requestIdToEdit = btn.dataset.missionId;

            let url;
            if (isEditMode) {
                url = '/horaire/requetes/' + requestIdToEdit + '/modifier';
            } else {
                url = '/horaire/requetes';
            }

            const res = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const errorMsg = await res.text();
                if (res.status === 409 && errorMsg) {
                    showToast(errorMsg, 'error');
                } else {
                    showToast("Erreur lors de l'enregistrement de la mission.", 'error');
                }
                return;
            }

            btn.dataset.editMode = 'false';
            btn.innerText = 'Envoyer';
            document.getElementById('newRequestModalTitle').innerText = 'Nouvelle demande';
            bootstrap.Modal.getOrCreateInstance(document.getElementById('newRequestModal')).hide();
            calendar.refetchEvents();

            if (isEditMode) {
                showToast("Demande modifiée avec succès.", 'success');
            } else {
                showToast("Demande envoyée avec succès.", 'success');
            }
        } catch (err) {
            showToast("Erreur : " + err.message, 'error');
        }
    });

    document.getElementById('requestCity').addEventListener('change', function() {
        const opt = this.options[this.selectedIndex];
        document.getElementById('requestCityName').value = opt.dataset.name || '';
    });
    document.getElementById('missionCity').addEventListener('change', function() {
        const opt = this.options[this.selectedIndex];
        document.getElementById('missionCityName').value = opt.dataset.name || '';
    });
    document.getElementById('newMissionModal').addEventListener('hidden.bs.modal', resetMissionForm);
    document.getElementById('newRequestModal').addEventListener('hidden.bs.modal', resetRequestForm);
    setupFilter('.filter-status', 'status');
    if (userRole === 'MANAGER' || userRole === 'INTERPRETER') {
        setupUserFilter();
        highlightActiveUser();
    }
    calendar.render();
});