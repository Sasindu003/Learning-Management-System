/**
 * EduFlow LMS — Main Application Script
 * Handles theme toggling, mobile navigation, and UI interactions.
 */

document.addEventListener('DOMContentLoaded', () => {
    // === Theme Management ===
    const themeToggle = document.getElementById('theme-toggle');
    const currentTheme = localStorage.getItem('theme') || 'light';

    // Apply saved theme on load
    document.documentElement.setAttribute('data-theme', currentTheme);
    updateThemeIcon(currentTheme);

    if (themeToggle) {
        themeToggle.addEventListener('click', () => {
            let theme = document.documentElement.getAttribute('data-theme');
            let newTheme = theme === 'light' ? 'dark' : 'light';
            
            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            updateThemeIcon(newTheme);
        });
    }

    function updateThemeIcon(theme) {
        if (!themeToggle) return;
        
        const iconEl = themeToggle.querySelector('.theme-icon');
        const textEl = themeToggle.querySelector('.theme-text');
        
        if (iconEl && textEl) {
            // Trigger animation
            themeToggle.classList.add('switching');
            setTimeout(() => themeToggle.classList.remove('switching'), 600);

            iconEl.textContent = theme === 'light' ? '🌓' : '☀️';
            textEl.textContent = theme === 'light' ? 'Light' : 'Dark';
        }
        
        themeToggle.title = theme === 'light' ? 'Switch to Dark Mode' : 'Switch to Light Mode';
    }

    // === Mobile Sidebar Toggle ===
    const menuToggle = document.getElementById('menu-toggle');
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.getElementById('sidebar-overlay');

    if (menuToggle && sidebar && overlay) {
        const toggleSidebar = () => {
            sidebar.classList.toggle('open');
            overlay.classList.toggle('active');
            const isOpen = sidebar.classList.contains('open');
            menuToggle.setAttribute('aria-expanded', isOpen);
            menuToggle.textContent = isOpen ? '✕' : '☰';
        };

        menuToggle.addEventListener('click', toggleSidebar);
        overlay.addEventListener('click', toggleSidebar);

        // Close sidebar on window resize if it's open and we're back on desktop
        window.addEventListener('resize', () => {
            if (window.innerWidth > 768 && sidebar.classList.contains('open')) {
                toggleSidebar();
            }
        });
    }

    // === Alert Auto-Fade ===
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease-out';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // === Top Bar Popups (Notifications, Discussions, Messages) ===
    initTopBarPopups();

    function initTopBarPopups() {
        const popups = [
            { icon: 'notif-icon', popup: 'notif-popup', list: 'notif-list', api: '/api/notifications/recent', type: 'notif' },
            { icon: 'discussion-icon', popup: 'discussion-popup', list: 'discussion-list', api: '/api/discussions/recent', type: 'disc' },
            { icon: 'msg-icon', popup: 'msg-popup', list: 'msg-list', api: '/messages/api/recent', type: 'msg' }
        ];

        popups.forEach(p => {
            const iconEl = document.getElementById(p.icon);
            const popupEl = document.getElementById(p.popup);
            const listEl = document.getElementById(p.list);

            if (iconEl && popupEl) {
                iconEl.addEventListener('click', (e) => {
                    e.stopPropagation();
                    const isActive = popupEl.classList.contains('active');
                    
                    // Close all popups first
                    document.querySelectorAll('.dropdown-popup').forEach(el => el.classList.remove('active'));
                    
                    if (!isActive) {
                        popupEl.classList.add('active');
                        fetchPopupData(p.api, listEl, p.type);
                    }
                });
            }
        });

        document.addEventListener('click', (e) => {
            document.querySelectorAll('.dropdown-popup').forEach(popup => {
                if (popup.classList.contains('active') && !popup.contains(e.target)) {
                    popup.classList.remove('active');
                }
            });
        });
    }

    async function fetchPopupData(apiUrl, container, type) {
        container.innerHTML = '<div class="loading-state">Loading...</div>';
        try {
            const response = await fetch(apiUrl);
            if (!response.ok) throw new Error('Network response was not ok');
            const data = await response.json();
            renderPopupList(data, container, type);
        } catch (error) {
            console.error('Error fetching popup data:', error);
            container.innerHTML = '<div class="empty-state">Failed to load content</div>';
        }
    }

    function renderPopupList(data, container, type) {
        if (!data || data.length === 0) {
            container.innerHTML = `<div class="empty-state">No recent ${type === 'notif' ? 'notifications' : type === 'msg' ? 'messages' : 'activity'}</div>`;
            return;
        }

        container.innerHTML = data.map(item => {
            if (type === 'notif') {
                return `
                    <a href="${item.link || '#'}" class="dropdown-item ${item.read ? '' : 'unread'}">
                        <span class="msg-snippet">${item.message}</span>
                        <span class="msg-meta">${new Date(item.createdAt).toLocaleString()}</span>
                    </a>`;
            } else if (type === 'msg') {
                return `
                    <a href="/messages/${item.id}" class="dropdown-item ${item.read ? '' : 'unread'}">
                        <span class="course-name">${item.sender}</span>
                        <span class="msg-snippet">${item.subject}</span>
                        <span class="msg-meta">${new Date(item.sentAt).toLocaleString()}</span>
                    </a>`;
            } else if (type === 'disc') {
                return `
                    <a href="/courses/${item.courseId}" class="dropdown-item">
                        <span class="course-name">${item.courseName}</span>
                        <span class="msg-snippet"><strong>${item.senderName}:</strong> ${item.lastMessage}</span>
                        <span class="msg-meta">${new Date(item.sentAt).toLocaleString()}</span>
                    </a>`;
            }
            return '';
        }).join('');
    }
});
