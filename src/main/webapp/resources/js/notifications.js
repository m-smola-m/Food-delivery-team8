document.addEventListener('DOMContentLoaded', function () {
    const notificationBell = document.getElementById('notification-bell');
    const notificationDropdown = document.getElementById('notification-dropdown');
    const notificationList = document.getElementById('notification-list');
    const markReadBtn = document.getElementById('mark-read-btn');
    const notificationCount = document.getElementById('notification-count');

    function fetchNotifications() {
        fetch('/client/notifications-api')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(notifications => {
                notificationList.innerHTML = '';
                let unreadCount = 0;
                if (notifications.length === 0) {
                    notificationList.innerHTML = '<li>No notifications</li>';
                } else {
                    notifications.forEach(notification => {
                        const listItem = document.createElement('li');
                        listItem.textContent = notification.message;
                        if (!notification.read) {
                            listItem.classList.add('unread');
                            unreadCount++;
                        }
                        notificationList.appendChild(listItem);
                    });
                }

                if (unreadCount > 0) {
                    notificationCount.textContent = unreadCount;
                    notificationCount.style.display = 'flex';
                } else {
                    notificationCount.style.display = 'none';
                }
            })
            .catch(error => {
                console.error('Error fetching notifications:', error);
                notificationList.innerHTML = '<li>Error loading notifications.</li>';
            });
    }

    if (notificationBell) {
        notificationBell.addEventListener('click', (event) => {
            event.stopPropagation();
            const isVisible = notificationDropdown.style.display === 'block';
            notificationDropdown.style.display = isVisible ? 'none' : 'block';
            if (!isVisible) {
                fetchNotifications();
            }
        });
    }

    if (markReadBtn) {
        markReadBtn.addEventListener('click', () => {
            fetch('/client/notifications/readAll', { method: 'POST' })
                .then(response => response.json())
                .then(data => {
                    if (data.status === 'ok') {
                        fetchNotifications();
                    }
                })
                .catch(error => console.error('Error marking notifications as read:', error));
        });
    }

    document.addEventListener('click', (event) => {
        if (notificationDropdown && !notificationBell.contains(event.target) && !notificationDropdown.contains(event.target)) {
            notificationDropdown.style.display = 'none';
        }
    });

    // Initial fetch and periodic refresh
    fetchNotifications();
    setInterval(fetchNotifications, 60000); // Refresh every 60 seconds
});

