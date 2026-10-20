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
