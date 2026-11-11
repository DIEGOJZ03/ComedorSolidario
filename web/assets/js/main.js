// La aplicación funciona por formularios GET/POST. JavaScript solo mejora la confirmación.
document.addEventListener('DOMContentLoaded', () => {
    const modalElement = document.getElementById('confirmarModal');
    if (!modalElement || !window.bootstrap) return;
    const modal = new bootstrap.Modal(modalElement);
    let pendingForm = null;
    let pendingSubmitter = null;
    document.querySelectorAll('form[data-confirm]').forEach(form => {
        form.addEventListener('submit', event => {
            if (form.dataset.confirmed === 'true') return;
            event.preventDefault();
            pendingForm = form;
            pendingSubmitter = event.submitter;
            document.getElementById('confirmarTitulo').textContent = form.dataset.confirmTitle || 'Confirmar cambio';
            document.getElementById('confirmarMensaje').textContent = form.dataset.confirmMessage || '¿Deseas cambiar el estado de este registro? Su historial se conservará.';
            document.getElementById('confirmarAccion').textContent = form.dataset.confirmLabel || 'Confirmar';
            modal.show();
        });
    });
    document.getElementById('confirmarAccion').addEventListener('click', () => {
        if (!pendingForm) return;
        pendingForm.dataset.confirmed = 'true';
        pendingForm.requestSubmit(pendingSubmitter || undefined);
        pendingForm = null;
        pendingSubmitter = null;
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

// Botón de ojo para mostrar/ocultar contraseña en contenedores .password-wrapper.
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-toggle-password]').forEach(btn => {
        btn.addEventListener('click', () => {
            const wrapper = btn.closest('.password-wrapper');
            const input = wrapper ? wrapper.querySelector('input') : null;
            if (!input) return;
            const showing = input.type === 'text';
            input.type = showing ? 'password' : 'text';
            btn.classList.toggle('is-visible', !showing);
            const label = showing ? 'Mostrar contraseña' : 'Ocultar contraseña';
            btn.setAttribute('aria-label', label);
            btn.setAttribute('title', label);
        });
    });
});

// Validación de teléfonos celulares en Perú (9 dígitos que empiezan con 9) y límites de cantidad.
window.configurarValidacionBloqueCantidad = (scope) => {
    const tipo = scope.querySelector('[data-tipo-cantidad]');
    const unidad = scope.querySelector('[data-unidad-cantidad]');
    const cantidad = scope.querySelector('[data-input-cantidad]');
    const labelCantidad = scope.querySelector('[data-label-cantidad]');
    if (!cantidad) return;

    const actualizarMax = (origen) => {
        if (origen === 'tipo' && tipo && unidad && unidad.tagName === 'SELECT') {
            if (tipo.value === 'DINERO') unidad.value = 'soles';
            else if (unidad.value === 'soles') unidad.value = 'kg';
        } else if (origen === 'unidad' && tipo && unidad) {
            if (unidad.value.trim().toLowerCase() === 'soles') tipo.value = 'DINERO';
            else if (tipo.value === 'DINERO') tipo.value = 'ALIMENTOS';
        }
        const esDinero = (tipo && tipo.value === 'DINERO') || (unidad && unidad.value.trim().toLowerCase() === 'soles');
        const maxVal = esDinero ? 5000 : 500;
        cantidad.max = String(maxVal);
        if (labelCantidad) {
            labelCantidad.textContent = esDinero ? 'Cantidad (máx. 5,000 soles)' : 'Cantidad (máx. 500)';
        }
        const val = parseFloat(cantidad.value);
        if (!Number.isNaN(val) && val > maxVal) {
            cantidad.setCustomValidity(
                esDinero
                    ? 'Para dinero en soles, el monto máximo permitido es 5,000.'
                    : 'Para unidades físicas, la cantidad máxima permitida es 500.'
            );
        } else {
            cantidad.setCustomValidity('');
        }
    };

    if (tipo) tipo.addEventListener('change', () => actualizarMax('tipo'));
    if (unidad) {
        unidad.addEventListener('change', () => actualizarMax('unidad'));
        unidad.addEventListener('input', () => actualizarMax('unidad'));
    }
    cantidad.addEventListener('input', () => actualizarMax('cantidad'));
    actualizarMax();
};

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('input[name="telefono"]').forEach(input => {
        const validarTelefono = () => {
            const limpio = input.value.replace(/\D/g, '').slice(0, 9);
            if (input.value !== limpio) input.value = limpio;
            if (limpio.length > 0 && !/^9\d{8}$/.test(limpio)) {
                input.setCustomValidity('Ingresa un celular válido de 9 dígitos que empiece con 9 (ej. 987654321).');
            } else {
                input.setCustomValidity('');
            }
        };
        input.addEventListener('input', validarTelefono);
    });

    const bloques = document.querySelectorAll('[data-donacion-item]');
    if (bloques.length > 0) {
        bloques.forEach(b => window.configurarValidacionBloqueCantidad(b));
    } else {
        document.querySelectorAll('form').forEach(f => window.configurarValidacionBloqueCantidad(f));
    }
});
