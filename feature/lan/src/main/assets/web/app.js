document.addEventListener('DOMContentLoaded', () => {
    // UI Elements
    const navItems = document.querySelectorAll('.nav-item');
    const fileGrid = document.getElementById('file-grid');
    const breadcrumb = document.getElementById('breadcrumb');
    const loader = document.getElementById('loader');
    const errorContainer = document.getElementById('error-message');
    const errorText = document.getElementById('error-text');
    const refreshBtn = document.getElementById('refreshBtn');
    const searchContainer = document.getElementById('search-container');
    const searchInput = document.getElementById('search-input');

    // State
    let currentPath = '/';
    let currentSpecial = null;

    // Init
    loadPath(currentPath);

    // Event Listeners
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const path = item.getAttribute('data-path');
            const special = item.getAttribute('data-special');

            // Update active state
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');

            if (special === 'search') {
                currentSpecial = special;
                searchContainer.classList.remove('hidden');
                breadcrumb.classList.add('hidden');
                searchInput.focus();
                // Clear grid while waiting for input
                fileGrid.innerHTML = '';
                loader.classList.add('hidden');
            } else if (special === 'recent' || special === 'pictures' || special === 'videos' || special === 'musics' || special === 'documents') {
                currentSpecial = special;
                searchContainer.classList.add('hidden');
                breadcrumb.classList.remove('hidden');
                loadSpecial(special);
            } else {
                currentSpecial = null;
                searchContainer.classList.add('hidden');
                breadcrumb.classList.remove('hidden');
                loadPath(path);
            }
        });
    });

    let searchTimeout;
    searchInput.addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        const query = e.target.value.trim();
        if (query.length > 0) {
            searchTimeout = setTimeout(() => {
                loadSpecial(`search?q=${encodeURIComponent(query)}`);
            }, 300);
        } else {
            fileGrid.innerHTML = ''; // clear on empty query
        }
    });

    refreshBtn.addEventListener('click', () => {
        if (currentSpecial) {
            if (currentSpecial === 'search') {
                const query = searchInput.value.trim();
                if (query) loadSpecial(`search?q=${encodeURIComponent(query)}`);
            } else {
                loadSpecial(currentSpecial);
            }
        } else {
            loadPath(currentPath);
        }
    });

    // Core Functions
    async function loadSpecial(specialPath) {
        // Show loader, hide content
        loader.classList.remove('hidden');
        fileGrid.classList.add('hidden');
        errorContainer.classList.add('hidden');
        fileGrid.innerHTML = '';

        if (specialPath === 'recent') {
            breadcrumb.innerHTML = '<span class="breadcrumb-segment current">Recent Files</span>';
        } else if (specialPath === 'pictures') {
            breadcrumb.innerHTML = '<span class="breadcrumb-segment current">Pictures</span>';
        } else if (specialPath === 'videos') {
            breadcrumb.innerHTML = '<span class="breadcrumb-segment current">Videos</span>';
        } else if (specialPath === 'musics') {
            breadcrumb.innerHTML = '<span class="breadcrumb-segment current">Musics</span>';
        } else if (specialPath === 'documents') {
            breadcrumb.innerHTML = '<span class="breadcrumb-segment current">Documents</span>';
        } else if (specialPath.startsWith('search')) {
            // keep search bar visible instead of breadcrumbs
        }

        try {
            const response = await fetch(`/api/${specialPath}`);
            if (!response.ok) {
                const errJson = await response.json();
                throw new Error(errJson.error || `HTTP error! status: ${response.status}`);
            }

            const items = await response.json();
            renderItems(items, true); // true indicates we are in a special view (no "Up" folder)

            loader.classList.add('hidden');
            fileGrid.classList.remove('hidden');
        } catch (error) {
            console.error('Failed to load special path:', error);
            loader.classList.add('hidden');
            errorContainer.classList.remove('hidden');
            errorText.textContent = `Error: ${error.message}`;
        }
    }

    async function loadPath(path) {
        currentPath = path;
        updateBreadcrumb();

        // Show loader, hide content
        loader.classList.remove('hidden');
        fileGrid.classList.add('hidden');
        errorContainer.classList.add('hidden');
        fileGrid.innerHTML = '';

        try {
            const response = await fetch(`/api/files?path=${encodeURIComponent(path)}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const items = await response.json();

            renderItems(items, false);

            // Show content, hide loader
            loader.classList.add('hidden');
            fileGrid.classList.remove('hidden');
        } catch (error) {
            console.error('Failed to load files:', error);
            loader.classList.add('hidden');
            errorContainer.classList.remove('hidden');
            errorText.textContent = `Failed to load directory: ${error.message}`;
        }
    }

    function renderItems(items, isSpecialView) {
        // Sort: Folders first, then alphabetically
        items.sort((a, b) => {
            if (a.isDirectory && !b.isDirectory) return -1;
            if (!a.isDirectory && b.isDirectory) return 1;
            return a.name.toLowerCase().localeCompare(b.name.toLowerCase());
        });

        // Add "Up" directory if not root and not in special view
        if (!isSpecialView && currentPath !== '/' && currentPath !== '') {
            const parentPath = currentPath.substring(0, currentPath.lastIndexOf('/')) || '/';
            const upCard = document.createElement('div');
            upCard.className = 'file-card parent-dir';
            upCard.innerHTML = `
                <div class="card-icon"><span class="material-symbols-outlined folder-icon">folder_up</span></div>
                <div class="card-info">
                    <span class="card-title">... (Up a level)</span>
                </div>
            `;
            upCard.addEventListener('click', () => loadPath(parentPath));
            fileGrid.appendChild(upCard);
        }

        if (items.length === 0) {
            const emptyState = document.createElement('div');
            emptyState.style.gridColumn = '1 / -1';
            emptyState.style.textAlign = 'center';
            emptyState.style.padding = '40px';
            emptyState.style.color = 'var(--text-secondary)';
            emptyState.innerHTML = '<span class="material-symbols-outlined" style="font-size: 48px; margin-bottom: 16px;">folder_open</span><p>This folder is empty</p>';
            fileGrid.appendChild(emptyState);
            return;
        }

        items.forEach(item => {
            const card = document.createElement(item.isDirectory ? 'div' : 'a');
            card.className = 'file-card';

            if (item.isDirectory) {
                const navPath = currentPath === '/' ? `/${item.name}` : `${currentPath}/${item.name}`;
                card.addEventListener('click', () => loadPath(navPath));
            } else {
                // Determine file path
                let filePath;
                if (item.absolutePath) {
                    filePath = item.absolutePath;
                } else {
                    filePath = currentPath === '/' ? `/${item.name}` : `${currentPath}/${item.name}`;
                }
                const fileUrl = `/api/file?path=${encodeURIComponent(filePath)}`;
                card.href = fileUrl;
                card.target = "_blank";

                // Previewable files open inline in browser; others force download
                const ext = item.name.split('.').pop().toLowerCase();
                const previewExts = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp', 'svg',
                    'mp4', 'mkv', 'avi', 'mov', 'webm', '3gp',
                    'mp3', 'wav', 'ogg', 'flac', 'aac', 'm4a',
                    'pdf', 'txt', 'html', 'css', 'js', 'json', 'xml', 'csv'];
                if (!previewExts.includes(ext)) {
                    card.download = item.name; // Force download for non-previewable types
                }
            }

            const iconClass = getIconClass(item);
            const sizeStr = item.isDirectory ? `${item.itemCount || 0} items` : formatSize(item.size);

            card.innerHTML = `
                <div class="card-icon">
                    <span class="material-symbols-outlined ${iconClass.class}">${iconClass.icon}</span>
                </div>
                <div class="card-info">
                    <span class="card-title"></span>
                    <span class="card-meta"></span>
                </div>
            `;

            const titleSpan = card.querySelector('.card-title');
            titleSpan.title = item.name;
            titleSpan.textContent = item.name;

            card.querySelector('.card-meta').textContent = sizeStr;

            fileGrid.appendChild(card);
        });
    }

    function updateBreadcrumb() {
        breadcrumb.innerHTML = '';
        const parts = currentPath.split('/').filter(p => p);

        // Root Home Icon
        const homeSpan = document.createElement('span');
        homeSpan.className = 'breadcrumb-segment' + (parts.length === 0 ? ' current' : '');
        homeSpan.innerHTML = '<span class="material-symbols-outlined" style="font-size: 20px;">home</span>';
        if (parts.length > 0) {
            homeSpan.addEventListener('click', () => loadPath('/'));
        }
        breadcrumb.appendChild(homeSpan);

        let buildPath = '';
        parts.forEach((part, index) => {
            const separator = document.createElement('span');
            separator.className = 'breadcrumb-separator material-symbols-outlined';
            separator.textContent = 'chevron_right';
            breadcrumb.appendChild(separator);

            buildPath += '/' + part;
            const segment = document.createElement('span');
            segment.className = 'breadcrumb-segment' + (index === parts.length - 1 ? ' current' : '');
            segment.textContent = part;

            if (index < parts.length - 1) {
                // IIFE to capture current buildPath in closure
                (function (path) {
                    segment.addEventListener('click', () => loadPath(path));
                })(buildPath);
            }

            breadcrumb.appendChild(segment);
        });
    }

    function getIconClass(item) {
        if (item.isDirectory) return { icon: 'folder', class: 'folder-icon' };

        const ext = item.name.split('.').pop().toLowerCase();

        switch (ext) {
            case 'jpg': case 'jpeg': case 'png': case 'gif': case 'webp':
                return { icon: 'image', class: 'image-icon' };
            case 'mp4': case 'mkv': case 'avi': case 'mov':
                return { icon: 'movie', class: 'video-icon' };
            case 'mp3': case 'wav': case 'ogg': case 'flac':
                return { icon: 'audio_file', class: 'audio-icon' };
            case 'pdf':
                return { icon: 'picture_as_pdf', class: 'pdf-icon' };
            case 'doc': case 'docx': case 'txt':
                return { icon: 'description', class: 'doc-icon' };
            case 'zip': case 'rar': case '7z': case 'tar': case 'gz':
                return { icon: 'folder_zip', class: 'archive-icon' };
            case 'apk':
                return { icon: 'android', class: 'apk-icon' };
            default:
                return { icon: 'draft', class: 'file-icon' };
        }
    }

    function formatSize(bytes) {
        if (bytes === 0) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
    }
});
