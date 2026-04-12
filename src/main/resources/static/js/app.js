// ===================================================================
// EduFlow LMS — Main JavaScript
// ===================================================================
// This file handles:
//   1. Auto-dismissing alert messages after 5 seconds
//   2. Polling for new notification / message counts every 30 seconds
//   3. Opening and closing modal dialogs
//   4. The mobile sidebar hamburger menu toggle
//   5. The quiz countdown timer (only on quiz pages)
//   6. Client-side form validation for required fields
//   7. Confirmation dialogs before deleting items

document.addEventListener('DOMContentLoaded', function () {

    // ------------------------------------------------------------------
    // 1. Auto-dismiss alert messages
    // ------------------------------------------------------------------
    // Success/error banners fade out automatically after 5 seconds.
    document.querySelectorAll('.alert').forEach(function (alert) {
        setTimeout(function () {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.3s, transform 0.3s';
            alert.style.transform = 'translateY(-10px)';
            // Wait for the CSS transition to finish, then remove the element
            setTimeout(function () { alert.remove(); }, 300);
        }, 5000);
    });


    // ------------------------------------------------------------------
    // 2. Notification & message count polling
    // ------------------------------------------------------------------
    // Check if the user is on an authenticated page (sidebar exists).
    // This prevents 401 errors on the login page which has no sidebar.
    var isAuthenticatedPage = document.querySelector('.sidebar') !== null;

    if (isAuthenticatedPage) {
        setInterval(function () {
            fetch('/api/notifications/count')
                .then(function (r) {
                    // Only process the response if the request succeeded
                    if (!r.ok) throw new Error('Not authenticated');
                    return r.json();
                })
                .then(function (data) {
                    // Update the notification bell count badge
                    var bell = document.getElementById('notif-count');
                    if (bell) {
                        bell.textContent = data.notifications || '';
                        bell.style.display = data.notifications > 0 ? 'flex' : 'none';
                    }
                    // Update the messages count badge
                    var msgBadge = document.getElementById('msg-count');
                    if (msgBadge) {
                        msgBadge.textContent = data.messages || '';
                        msgBadge.style.display = data.messages > 0 ? 'flex' : 'none';
                    }
                })
                .catch(function () {
                    // Silent fail — network errors or auth issues shouldn't break the page
                });
        }, 30000); // Poll every 30 seconds
    }


    // ------------------------------------------------------------------
    // 3. Modal open / close helpers
    // ------------------------------------------------------------------
    // Usage: onclick="openModal('my-modal-id')"  and  onclick="closeModal('my-modal-id')"
    window.openModal = function (id) {
        var overlay = document.getElementById(id);
        if (!overlay) return;
        overlay.classList.add('active');
        overlay.setAttribute('aria-hidden', 'false');
        // Move focus inside the modal so screen readers announce it
        var firstInput = overlay.querySelector('input, select, textarea, button');
        if (firstInput) firstInput.focus();
    };

    window.closeModal = function (id) {
        var overlay = document.getElementById(id);
        if (!overlay) return;
        overlay.classList.remove('active');
        overlay.setAttribute('aria-hidden', 'true');
    };

    // Click outside modal to close
    document.querySelectorAll('.modal-overlay').forEach(function (overlay) {
        overlay.addEventListener('click', function (e) {
            // Only close if the user clicked the dark background, not the white modal box
            if (e.target === overlay) {
                overlay.classList.remove('active');
                overlay.setAttribute('aria-hidden', 'true');
            }
        });
        // Close modal with the Escape key (accessibility requirement)
        overlay.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                overlay.classList.remove('active');
                overlay.setAttribute('aria-hidden', 'true');
            }
        });
    });


    // ------------------------------------------------------------------
    // 4. Mobile sidebar hamburger toggle
    // ------------------------------------------------------------------
    var menuToggle = document.getElementById('menu-toggle');
    var sidebar    = document.querySelector('.sidebar');
    var overlay    = document.getElementById('sidebar-overlay');

    if (menuToggle && sidebar) {
        // Open sidebar when hamburger is clicked
        menuToggle.addEventListener('click', function () {
            var isOpen = sidebar.classList.toggle('open');
            // Update aria-expanded so screen readers know the state
            menuToggle.setAttribute('aria-expanded', String(isOpen));
            menuToggle.setAttribute('aria-label', isOpen ? 'Close navigation menu' : 'Open navigation menu');
            // Show / hide the dark overlay behind the sidebar
            if (overlay) overlay.classList.toggle('active', isOpen);
        });

        // Close sidebar when clicking the overlay
        if (overlay) {
            overlay.addEventListener('click', function () {
                sidebar.classList.remove('open');
                overlay.classList.remove('active');
                menuToggle.setAttribute('aria-expanded', 'false');
                menuToggle.setAttribute('aria-label', 'Open navigation menu');
            });
        }

        // Close sidebar when pressing Escape
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' && sidebar.classList.contains('open')) {
                sidebar.classList.remove('open');
                if (overlay) overlay.classList.remove('active');
                menuToggle.setAttribute('aria-expanded', 'false');
                menuToggle.focus(); // Return focus to the button
            }
        });
    }


    // ------------------------------------------------------------------
    // 5. Quiz countdown timer
    // ------------------------------------------------------------------
    // Looks for an element with id="quiz-timer" and a data-duration attribute
    // (in minutes). Counts down and auto-submits the quiz form when time is up.
    var timerEl = document.getElementById('quiz-timer');

    if (timerEl) {
        var duration = parseInt(timerEl.dataset.duration) * 60; // convert minutes → seconds

        var interval = setInterval(function () {
            duration--;

            // Format as MM:SS
            var mins = Math.floor(duration / 60);
            var secs = duration % 60;
            timerEl.textContent = String(mins).padStart(2, '0') + ':' + String(secs).padStart(2, '0');

            // Turn red and pulse when less than 1 minute remains
            if (duration <= 60) {
                timerEl.style.color = '#ef4444';
                timerEl.style.animation = 'pulse 1s infinite';
            }

            // Auto-submit when time runs out
            if (duration <= 0) {
                clearInterval(interval);
                // Guard: make sure the form exists before trying to submit
                var quizForm = document.getElementById('quiz-form');
                if (quizForm) {
                    quizForm.submit();
                }
            }
        }, 1000);
    }


    // ------------------------------------------------------------------
    // 6. Client-side form validation
    // ------------------------------------------------------------------
    // Any <form data-validate> will have its required fields checked
    // before submission. A red border is shown on empty required fields.
    document.querySelectorAll('form[data-validate]').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            var valid = true;

            form.querySelectorAll('[required]').forEach(function (input) {
                if (!input.value.trim()) {
                    valid = false;
                    input.style.borderColor = '#ef4444';
                    input.style.boxShadow = '0 0 0 3px rgba(239, 68, 68, 0.2)';
                    // Announce error to screen readers
                    input.setAttribute('aria-invalid', 'true');
                } else {
                    // Clear the error styling once the field is filled
                    input.style.borderColor = '';
                    input.style.boxShadow = '';
                    input.removeAttribute('aria-invalid');
                }
            });

            if (!valid) {
                e.preventDefault(); // Stop form submission if validation fails
                // Focus the first invalid field so the user knows where the problem is
                var firstInvalid = form.querySelector('[aria-invalid="true"]');
                if (firstInvalid) firstInvalid.focus();
            }
        });
    });


    // ------------------------------------------------------------------
    // 7. Confirm before delete
    // ------------------------------------------------------------------
    // Any button or link with data-confirm="Your message here" will show
    // a confirmation dialog. If the user clicks Cancel, the action is stopped.
    document.querySelectorAll('[data-confirm]').forEach(function (el) {
        el.addEventListener('click', function (e) {
            if (!confirm(el.dataset.confirm || 'Are you sure? This cannot be undone.')) {
                e.preventDefault();
            }
        });
    });

});
