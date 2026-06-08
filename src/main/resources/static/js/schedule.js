// ── CONFIG ────────────────────────────────────────────────────────────────

/** @type {HTMLElement} Hidden element containing server-side configuration */
const config = document.getElementById('schedule-config');

/** @type {Array<Object>} List of all events loaded from the server */
const allEvents = JSON.parse(config.dataset.events);

/** @type {string} Role of the logged-in user ('MANAGER', 'INTERPRETER', 'BENEFICIARY') */
const userRole = config.dataset.role;

/** @type {number} ID of the logged-in user */
const userId = parseInt(config.dataset.userId);

/** @type {boolean} True if the screen width is less than 768px */
const isMobile = window.innerWidth < 768;


// ── STATE ─────────────────────────────────────────────────────────────────

/**
 * @typedef {Object} ActiveFilters
 * @property {string|null} status      - Active filter by status (e.g. "Acceptée", "En attente")
 * @property {string|null} interpreter - Active filter by interpreter name
 */

/** @type {ActiveFilters} */
const activeFilters = {
    status: null,
    interpreter: null
};

/** @type {number|null} ID of the currently selected mission */
let currentMissionId = null;

/** @type {boolean} Whether this is the first calendar load */
let premierChargement = true;


// ── UTILS ─────────────────────────────────────────────────────────────────

/**
 * Displays a notification toast in the bottom-right corner of the screen,
 * which disappears after 3 seconds.
 *
 * @param {string} message                             - Text to display in the toast
 * @param {'success'|'error'|'info'} [type='success'] - Visual type of the toast
 * @returns {void}
 */
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast-custom toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.classList.add('toast-hide'), 3000);
    setTimeout(() => toast.remove(), 3300);
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
 * Sets up a toggle filter on a group of elements.
 * Clicking an item activates or deactivates the filter,
 * updates the bold style, refreshes the calendar and closes the dropdown.
 *
 * @param {string} selector  - CSS selector targeting the filter items
 * @param {string} filterKey - Key in activeFilters to update ('status' or 'interpreter')
 * @returns {void}
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


// ── CALENDAR BUTTONS ──────────────────────────────────────────────────────

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
            window.location.href = '/interpretes/profil/' + userId;
        }
    };
}

if (userRole === 'MANAGER') {
    customButtons.newUnavailability = {
        text: isMobile ? '+ Indispo' : '+ Nouvelle indisponibilité',
        click: function() {
            window.location.href = '/interpretes/profil/' + userId;
        }
    };
    customButtons.newMission = {
        text: isMobile ? '+ Mission' : '+ Nouvelle mission',
        click: function () {
            const newMissionModal = bootstrap.Modal.getOrCreateInstance(
                document.getElementById('newMissionModal')
            );
            newMissionModal.show();
        }
    };
}


// ── CALENDAR INIT ─────────────────────────────────────────────────────────

/**
 * Fetches calendar events from the REST API based on the current date range
 * and active filters (status, interpreter).
 *
 * @param {Object}   fetchInfo          - Object provided by FullCalendar containing view date range
 * @param {string}   fetchInfo.startStr - View start date in ISO 8601 format
 * @param {Function} successCallback    - Callback to call with the fetched events
 * @param {Function} failureCallback    - Callback to call on network error
 * @returns {Promise<void>}
 */
async function fetchEvents(fetchInfo, successCallback, failureCallback) {
    const params = new URLSearchParams();
    params.append('weekDate', fetchInfo.startStr.substring(0, 10));

    if (activeFilters.status !== null){
        params.append('status', activeFilters.status);
    }
    if (activeFilters.interpreter !== null){
        params.append('interpreter', activeFilters.interpreter);
    }

    try {
        const r = await fetch('/horaire/evenements?' + params.toString());
        const data = await r.json();
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
    const calendar = new FullCalendar.Calendar(document.getElementById('calendar'), {
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
                successCallback(allEvents);
                return;
            }
            fetchEvents(fetchInfo, successCallback, failureCallback);
        },
        customButtons: customButtons,
        headerToolbar: {
            start: isMobile ? 'timeGridDay filterBtn' : 'timeGridWeek,timeGridDay filterBtn',
            center: 'prev title next',
            end: userRole === 'MANAGER' ? 'newUnavailability newMission today'
                : userRole === 'INTERPRETER' ? 'newUnavailability today'
                    : 'newRequest today'
        },
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
            const start = event.start;
            const end = event.end;
            const status = (props.status || '').toLowerCase();
            const now = new Date();

            const beforeStart = start && now < start;
            const isManager = userRole === 'MANAGER';
            const isPending = status.includes('en attente');
            const isAccepted = status.includes('accept');
            const interpreterSelect = document.getElementById('managerPendingInterpreter');
            interpreterSelect.disabled = !(isPending && beforeStart);

            if (isAccepted) {
                interpreterSelect.style.display = 'none';

                let nameSpan = document.getElementById('managerPendingInterpreterName');
                if (!nameSpan) {
                    nameSpan = document.createElement('span');
                    nameSpan.id = 'managerPendingInterpreterName';
                    nameSpan.className = 'fst-italic text-muted';
                    interpreterSelect.parentNode.appendChild(nameSpan);
                }

                nameSpan.style.display = '';
                const interpreterNames = (props.interpreter || '')
                    .split(',')
                    .map(name => name.trim())
                    .filter(name => name !== '');
                if (interpreterNames.length > 0) {
                    nameSpan.textContent = interpreterNames.join(' • ');
                } else {
                    nameSpan.textContent = 'Aucun interprète';
                }
            } else {
                interpreterSelect.style.display = '';
                const nameSpan = document.getElementById('managerPendingInterpreterName');
                if (nameSpan) {
                    nameSpan.style.display = 'none';
                }
                interpreterSelect.value = '';
            }

            const timeFormatter = new Intl.DateTimeFormat('fr-BE', { hour: '2-digit', minute: '2-digit' });
            let timeText = '';
            if (start) {
                timeText = timeFormatter.format(start);
                if (end) timeText += ' - ' + timeFormatter.format(end);
            }

            if (isManager && (isPending || isAccepted)) {
                const importance = parseInt(props.importance || '0', 10);
                document.getElementById('managerPendingImportance').innerHTML = buildStars(importance, 5);
                document.getElementById('managerPendingTitle').innerText = event.title || '';
                document.getElementById('managerPendingDate').innerText = start ? start.toLocaleDateString('fr-BE') : '';
                document.getElementById('managerPendingTime').innerText = timeText || '';
                document.getElementById('managerPendingLocation').innerText = props.address || props.room || '';
                document.getElementById('managerPendingBeneficiary').innerText = props.beneficiary || 'Aucun bénéficiaire';
                document.getElementById('managerPendingType').innerText = props.type ? 'Type : ' + props.type : '';
                document.getElementById('managerPendingComment').innerText = props.comment || '';
                document.getElementById('managerPendingStatus').innerText = props.status || '';


                const footer = document.querySelector('#managerPendingModal .modal-footer');
                footer.innerHTML = '';

                if (isPending && beforeStart) {
                    footer.innerHTML = `
                        <button type="button" class="btn btn-danger" id="btnRefuseMission">Refuser</button>
                        <button type="button" class="btn btn-success" id="btnAcceptMission">Accepter</button>
                    `;
                } else {
                    footer.innerHTML = `
                        <button type="button" class="btn btn-danger" id="btnCancelAcceptedMission">Annuler la mission</button>
                    `;
                }

                const acceptBtn = document.getElementById('btnAcceptMission');
                const refuseBtn = document.getElementById('btnRefuseMission');
                const cancelAcceptedBtn = document.getElementById('btnCancelAcceptedMission');

                if (acceptBtn) {
                    /**
                     * Sends a POST request to accept the selected mission with the chosen interpreter.
                     * Displays a success toast and refreshes the calendar events.
                     *
                     * @listens click
                     * @returns {Promise<void>}
                     */
                    acceptBtn.addEventListener('click', async function () {
                        const selectedInterpreter = document.getElementById('managerPendingInterpreter').value;
                        if (!selectedInterpreter) {
                            showToast("Veuillez sélectionner un interprète.", 'error');
                            return;
                        }
                        try {
                            const res = await fetch('/horaire/missions/' + event.id + '/accepter', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ interpreterId: selectedInterpreter })
                            });
                            if (!res.ok) throw new Error(await res.text());
                            bootstrap.Modal.getOrCreateInstance(document.getElementById('managerPendingModal')).hide();
                            calendar.refetchEvents();
                            showToast("Mission acceptée.", 'success');
                        } catch (err) {
                            showToast("Erreur : " + err.message, 'error');
                        }
                    }, { once: true });
                }

                if (refuseBtn) {
                    /**
                     * Sends a POST request to refuse the selected mission.
                     * Displays an info toast and refreshes the calendar events.
                     *
                     * @listens click
                     * @returns {Promise<void>}
                     */
                    refuseBtn.addEventListener('click', async function () {
                        try {
                            const res = await fetch('/horaire/missions/' + event.id + '/refuser', {
                                method: 'POST'
                            });
                            if (!res.ok) throw new Error(await res.text());
                            bootstrap.Modal.getOrCreateInstance(document.getElementById('managerPendingModal')).hide();
                            calendar.refetchEvents();
                            showToast("Mission refusée.", 'info');
                        } catch (err) {
                            showToast("Erreur : " + err.message, 'error');
                        }
                    }, { once: true });
                }

                if (cancelAcceptedBtn) {
                    /**
                     * Closes the manager modal and opens the cancellation confirmation modal.
                     *
                     * @listens click
                     */
                    cancelAcceptedBtn.addEventListener('click', function() {
                        bootstrap.Modal.getOrCreateInstance(document.getElementById('managerPendingModal')).hide();
                        bootstrap.Modal.getOrCreateInstance(document.getElementById('confirmCancelModal')).show();
                    });
                }

                bootstrap.Modal.getOrCreateInstance(document.getElementById('managerPendingModal')).show();
                return;
            }

            document.getElementById('modalTitle').innerText = event.title || '';
            document.getElementById('modalTime').innerText = timeText || '';
            document.getElementById('modalDate').innerText = event.start ? event.start.toLocaleDateString('fr-BE') : '';
            document.getElementById('modalType').innerText = props.type || '';
            document.getElementById('modalLocation').innerText = props.address || '';
            document.getElementById('modalBeneficiary').innerText = props.beneficiary || '';
            document.getElementById('modalComment').innerText = props.comment || '';
            document.getElementById('modalStatus').innerText = props.status || '';
            const actions = document.getElementById('modalActions');
            actions.innerHTML = '';

            const isSameDay = now.getFullYear() === start.getFullYear() && now.getMonth() === start.getMonth() && now.getDate() === start.getDate();
            const isBeforeEnd = now < end;

            if (isPending) {
                actions.innerHTML = `
                    <button type="button" class="btn btn-danger" id="btnCancelRequest">Annuler la demande</button>
                    <button type="button" class="btn btn-primary">Modifier la demande</button>
                `;
            } else if (isAccepted && isSameDay && isBeforeEnd) {
                actions.innerHTML = `
                    <button type="button" class="btn btn-warning text-white" id="btnDelayReport">Signaler un retard</button>
                `;
            }

            const cancelBtn = document.getElementById('btnCancelRequest');
            const delayBtn = document.getElementById('btnDelayReport');

            if (delayBtn) {
                /**
                 * Closes the event modal and opens the delay report modal.
                 *
                 * @listens click
                 */
                delayBtn.addEventListener('click', function() {
                    bootstrap.Modal.getOrCreateInstance(document.getElementById('eventModal')).hide();
                    bootstrap.Modal.getOrCreateInstance(document.getElementById('delayModal')).show();
                });
            }

            if (cancelBtn) {
                /**
                 * Closes the event modal and opens the cancellation confirmation modal.
                 *
                 * @listens click
                 */
                cancelBtn.addEventListener('click', function() {
                    bootstrap.Modal.getOrCreateInstance(document.getElementById('eventModal')).hide();
                    bootstrap.Modal.getOrCreateInstance(document.getElementById('confirmCancelModal')).show();
                });
            }

            document.getElementById('modalInterpreter').innerText = props.interpreter || 'Aucun interprète';
            bootstrap.Modal.getOrCreateInstance(document.getElementById('eventModal')).show();
        }
    });

    document.addEventListener('click', (e) => {
        if (!e.target.closest('.fc-filterBtn-button') && !e.target.closest('#dropdown-filtre')) {
            document.getElementById('dropdown-filtre').classList.remove('show');
            document.querySelector('.fc-filterBtn-button').classList.remove('active');
        }
    });

    document.getElementById('search-interpreter')?.addEventListener('input', function() {
        const query = this.value.toLowerCase();
        document.querySelectorAll('#interpreter-list .filter-interpreter').forEach(item => {
            const name = item.dataset.value.toLowerCase();
            item.style.display = name.includes(query) ? '' : 'none';
        });
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
            if (!res.ok) throw new Error(await res.text());
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
        clearFormErrors(['missionTitle', 'missionDate', 'missionLocationDesignation', 'missionCity', 'missionStreet', 'missionPostalCode', 'missionInterpreter']);
        const rules = [
            { id: 'missionTitle',               errorId: 'missionTitleError',               msg: 'Le titre est requis.' },
            { id: 'missionDate',                errorId: 'missionDateError',                msg: 'La date est requise.' },
            { id: 'missionLocationDesignation', errorId: 'missionLocationDesignationError', msg: 'Le lieu est requis.' },
            { id: 'missionCity',                errorId: 'missionCityError',                msg: 'La ville est requise.' },
            { id: 'missionStreet',     errorId: 'missionStreetError',     msg: 'La rue est requise.' },
        ];
        if (!validateFields(rules)) return;

        const checkedInterpreters = Array.from(document.querySelectorAll('.mission-interpreter-check:checked'));
        if (checkedInterpreters.length === 0) {
            document.getElementById('missionInterpreterError').textContent = 'Veuillez sélectionner au moins un interprète.';
            document.getElementById('missionInterpreterError').style.display = 'block';
            return;
        }

        const startTime = document.getElementById('missionStartTime').value;
        const endTime   = document.getElementById('missionEndTime').value;
        if (startTime >= endTime) {
            showToast("L'heure de fin doit être après l'heure de début.", 'error');
            return;
        }
        const payload = {
            type:                document.querySelector('input[name="missionType"]:checked').value,
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
            interpreterIds:      checkedInterpreters.map(cb => cb.value),
            beneficiaryId:      document.getElementById('missionBeneficiary').value,
            comment:             document.getElementById('missionComment').value,
            room:                document.getElementById('missionRoom').value,
            academicSkillId:     document.getElementById('missionAcademicSkill').value,
        };
        try {
            const res = await fetch('/horaire/missions', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!res.ok) throw new Error(await res.text());
            bootstrap.Modal.getOrCreateInstance(document.getElementById('newMissionModal')).hide();
            calendar.refetchEvents();
            showToast("Mission créée avec succès.", 'success');
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
        clearFormErrors(['requestTitle', 'requestDate', 'requestLocationDesignation', 'requestCity', 'requestStreet', 'requestPostalCode']);
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
            type:                document.querySelector('input[name="requestType"]:checked').value,
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
            const res = await fetch('/horaire/requetes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!res.ok) throw new Error(await res.text());
            bootstrap.Modal.getOrCreateInstance(document.getElementById('newRequestModal')).hide();
            calendar.refetchEvents();
            showToast("Demande envoyée avec succès.", 'success');
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
    setupFilter('.filter-status', 'status');
    setupFilter('.filter-interpreter', 'interpreter');
    calendar.render();
});