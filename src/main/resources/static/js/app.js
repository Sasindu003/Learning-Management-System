// EduFlow LMS — App JavaScript
document.addEventListener('DOMContentLoaded', function () {

    // Auto-dismiss alerts after 5 seconds
    document.querySelectorAll('.alert').forEach(function (alert) {
        setTimeout(function () {
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(function () { alert.remove(); }, 300);
        }, 5000);
    });

    // Notification polling every 30 seconds
    setInterval(function () {
        fetch('/api/notifications/count')
            .then(function (r) { return r.json(); })
            .then(function (data) {
                var bell = document.getElementById('notif-count');
                if (bell) {
                    bell.textContent = data.notifications || '';
                    bell.style.display = data.notifications > 0 ? 'flex' : 'none';
                }
                var msgBadge = document.getElementById('msg-count');
                if (msgBadge) {
                    msgBadge.textContent = data.messages || '';
                    msgBadge.style.display = data.messages > 0 ? 'flex' : 'none';
                }
            }).catch(function () { });
    }, 30000);

    // Modal functionality
    window.openModal = function (id) {
        document.getElementById(id).classList.add('active');
    };
    window.closeModal = function (id) {
        document.getElementById(id).classList.remove('active');
    };

    // Click outside modal to close
    document.querySelectorAll('.modal-overlay').forEach(function (overlay) {
        overlay.addEventListener('click', function (e) {
            if (e.target === overlay) overlay.classList.remove('active');
        });
    });

    // Sidebar mobile toggle
    var menuToggle = document.getElementById('menu-toggle');
    if (menuToggle) {
        menuToggle.addEventListener('click', function () {
            document.querySelector('.sidebar').classList.toggle('open');
        });
    }

    // Quiz Timer
    var timerEl = document.getElementById('quiz-timer');
    if (timerEl) {
        var duration = parseInt(timerEl.dataset.duration) * 60; // minutes to seconds
        var interval = setInterval(function () {
            duration--;
            var mins = Math.floor(duration / 60);
            var secs = duration % 60;
            timerEl.textContent = String(mins).padStart(2, '0') + ':' + String(secs).padStart(2, '0');
            if (duration <= 60) {
                timerEl.style.color = '#ef4444';
                timerEl.style.animation = 'pulse 1s infinite';
            }
            if (duration <= 0) {
                clearInterval(interval);
                document.getElementById('quiz-form').submit();
            }
        }, 1000);
    }

    // Form validation
    document.querySelectorAll('form[data-validate]').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            var valid = true;
            form.querySelectorAll('[required]').forEach(function (input) {
                if (!input.value.trim()) {
                    valid = false;
                    input.style.borderColor = '#ef4444';
                    input.style.boxShadow = '0 0 0 3px rgba(239, 68, 68, 0.2)';
                } else {
                    input.style.borderColor = '';
                    input.style.boxShadow = '';
                }
            });
            if (!valid) { e.preventDefault(); }
        });
    });

    // Confirm delete
    document.querySelectorAll('[data-confirm]').forEach(function (el) {
        el.addEventListener('click', function (e) {
            if (!confirm(el.dataset.confirm || 'Are you sure?')) {
                e.preventDefault();
            }
        });
    });
});
