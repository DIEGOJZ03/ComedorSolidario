(() => {
    const selector = document.getElementById('modalidad');
    if (selector) {
        function mostrar() {
            document.querySelectorAll('[data-modalidad]').forEach(panel => {
                const activo = panel.dataset.modalidad === selector.value;
                panel.hidden = !activo;
                panel.querySelectorAll('input,select').forEach(input => {
                    input.disabled = !activo;
                    input.required = activo;
                });
            });
        }
        selector.addEventListener('change', mostrar);
        mostrar();
    }

    const contenedor = document.getElementById('contenedor-donaciones');
    const btnAgregar = document.getElementById('btnAgregarDonacion');
    const btnSubmit = document.getElementById('btnSubmitDonacion');
    if (!contenedor || !btnAgregar) return;

    const MAX_DONACIONES = 5;

    function actualizarNumeracion() {
        const items = contenedor.querySelectorAll('[data-donacion-item]');
        const multiple = items.length > 1;
        items.forEach((item, idx) => {
            const num = idx + 1;
            const header = item.querySelector('.donacion-bloque-header');
            const titulo = item.querySelector('[data-donacion-titulo]');
            const btnQuitar = item.querySelector('[data-quitar-donacion]');
            if (header) header.hidden = !multiple;
            if (titulo) titulo.textContent = 'Donación #' + num;
            if (btnQuitar) btnQuitar.hidden = idx === 0;
            item.classList.toggle('donacion-bloque--multiple', multiple);
        });
        btnAgregar.disabled = items.length >= MAX_DONACIONES;
        if (btnSubmit) {
            btnSubmit.textContent = multiple
                ? 'Registrar ' + items.length + ' donaciones'
                : 'Registrar donación';
        }
    }

    btnAgregar.addEventListener('click', () => {
        const items = contenedor.querySelectorAll('[data-donacion-item]');
        if (items.length >= MAX_DONACIONES) return;
        const base = items[0];
        const clon = base.cloneNode(true);
        const sufijo = '-' + Date.now() + '-' + (items.length + 1);

        clon.querySelectorAll('[id]').forEach(el => {
            const oldId = el.id;
            const newId = oldId + sufijo;
            el.id = newId;
            const label = clon.querySelector('label[for="' + oldId + '"]');
            if (label) label.setAttribute('for', newId);
        });

        const selectTipo = clon.querySelector('[data-tipo-cantidad]');
        const inputProducto = clon.querySelector('[data-producto-input]');
        const hiddenProducto = clon.querySelector('[data-producto-id-hidden]');
        const datalist = clon.querySelector('datalist');
        const inputOtro = clon.querySelector('[data-input-otro-producto]');
        const selectUnidad = clon.querySelector('[data-unidad-cantidad]');
        const inputCantidad = clon.querySelector('[data-input-cantidad]');
        const inputDescripcion = clon.querySelector('textarea[name="descripcion"]');

        if (datalist && inputProducto) {
            const nuevoIdList = 'listaProductosCatalogo' + sufijo;
            datalist.id = nuevoIdList;
            inputProducto.setAttribute('list', nuevoIdList);
        }

        if (selectTipo) selectTipo.value = 'ALIMENTOS';
        if (inputProducto) inputProducto.value = '';
        if (hiddenProducto) hiddenProducto.value = '';
        if (inputOtro) {
            inputOtro.value = '';
            inputOtro.setCustomValidity('');
        }
        if (selectUnidad) selectUnidad.value = 'kg';
        if (inputCantidad) {
            inputCantidad.value = '';
            inputCantidad.setCustomValidity('');
        }
        if (inputDescripcion) inputDescripcion.value = '';

        const btnQuitar = clon.querySelector('[data-quitar-donacion]');
        if (btnQuitar) {
            btnQuitar.addEventListener('click', () => {
                clon.remove();
                actualizarNumeracion();
            });
        }

        contenedor.appendChild(clon);
        if (window.configurarValidacionBloqueCantidad) {
            window.configurarValidacionBloqueCantidad(clon);
        }
        actualizarNumeracion();
    });

    actualizarNumeracion();
})();
