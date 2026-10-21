// La aplicación funciona por formularios GET/POST. JavaScript solo mejora la confirmación.
document.addEventListener('DOMContentLoaded', () => {
    const modalElement = document.getElementById('confirmarModal');
    if (!modalElement || !window.bootstrap) return;
    const modal = new bootstrap.Modal(modalElement);
    let pendingForm = null;
    document.querySelectorAll('form[data-confirm]').forEach(form => {
        form.addEventListener('submit', event => {
            if (form.dataset.confirmed === 'true') return;
            event.preventDefault();
            pendingForm = form;
            document.getElementById('confirmarTitulo').textContent = form.dataset.confirmTitle || 'Confirmar cambio';
            document.getElementById('confirmarMensaje').textContent = form.dataset.confirmMessage || '¿Deseas cambiar el estado de este registro? Su historial se conservará.';
            document.getElementById('confirmarAccion').textContent = form.dataset.confirmLabel || 'Confirmar';
            modal.show();
        });
    });
    document.getElementById('confirmarAccion').addEventListener('click', () => {
        if (!pendingForm) return;
        pendingForm.dataset.confirmed = 'true';
        pendingForm.requestSubmit();
        pendingForm = null;
        modal.hide();
    });
});

// Mejoras visuales independientes de la confirmación de formularios.
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('img[data-image-fallback]').forEach(img => {
        const useFallback = () => {
            const fallback = img.dataset.imageFallback;
            if (!fallback) return;
            if (img.src === new URL(fallback, window.location.href).href) return;
            img.alt = 'Ilustración de un comedor comunitario; fotografía pendiente';
            img.src = fallback;
        };
        img.addEventListener('error', useFallback, { once: true });
        if (img.complete && img.naturalWidth === 0) useFallback();
    });
    document.querySelectorAll('.admin-nav a, .navbar .nav-link').forEach(link => {
        const path = new URL(link.href, window.location.href).pathname;
        if (!link.hash && (path === window.location.pathname ||
            (link.closest('.admin-nav') && window.location.pathname.startsWith(path + '/')))) {
            link.setAttribute('aria-current', 'page');
        }
    });
});

// Vista previa local: la foto solo se envía al guardar el formulario.
document.addEventListener('DOMContentLoaded', () => {
    const editor = document.querySelector('[data-photo-editor]');
    if (!editor) return;
    const input = editor.querySelector('#foto');
    const predeterminada = editor.querySelector('#fotoPredeterminada');
    const preview = editor.querySelector('img');
    const message = editor.querySelector('#foto-error');
    const original = preview.getAttribute('src');
    const fallback = preview.dataset.imageFallback;
    let blobUrl = null;
    const release = () => { if (blobUrl) URL.revokeObjectURL(blobUrl); blobUrl = null; };
    input.addEventListener('change', () => {
        release();
        message.textContent = '';
        input.setCustomValidity('');
        const file = input.files[0];
        if (!file) { preview.src = predeterminada.checked ? fallback : original; return; }
        if (file.size > 5 * 1024 * 1024 || !['image/jpeg', 'image/png'].includes(file.type)) {
            message.textContent = 'Selecciona una imagen JPG o PNG de hasta 5 MB.';
            input.setCustomValidity(message.textContent);
            preview.src = original;
            return;
        }
        predeterminada.checked = false;
        blobUrl = URL.createObjectURL(file);
        preview.src = blobUrl;
    });
    predeterminada.addEventListener('change', () => {
        release();
        input.value = '';
        input.setCustomValidity('');
        message.textContent = '';
        preview.src = predeterminada.checked ? fallback : original;
    });
    window.addEventListener('pagehide', release);
});

// Las opciones provienen del catálogo del servidor; al cambiar provincia se limpia el distrito.
document.addEventListener('DOMContentLoaded', () => {
    const provincia = document.querySelector('[data-provincia-comedor]');
    const distrito = document.querySelector('[data-distrito-comedor]');
    if (!provincia || !distrito) return;
    const opciones = new Map(Array.from(distrito.querySelectorAll('optgroup'), group => [
        group.dataset.provincia,
        Array.from(group.querySelectorAll('option'), option => ({ value: option.value, text: option.textContent }))
    ]));
    const actualizar = (seleccion = '') => {
        const disponibles = opciones.get(provincia.value) || [];
        distrito.replaceChildren(new Option(disponibles.length ? (distrito.dataset.placeholder || 'Selecciona un distrito') : 'Selecciona primero una provincia', ''));
        disponibles.forEach(option => distrito.add(new Option(option.text, option.value)));
        distrito.disabled = disponibles.length === 0;
        distrito.value = disponibles.some(option => option.value === seleccion) ? seleccion : '';
    };
    actualizar(distrito.value);
    provincia.addEventListener('change', () => actualizar());
});
