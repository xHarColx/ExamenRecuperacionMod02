$(document).ready(function () {
    const tabla = $('#example').DataTable({
        ajax: {
            url: 'medioscrud',
            dataSrc: ''
        },
        columns: [
            { data: 'codiMedi' },
            { data: 'ndniMedi' },
            { data: 'appaMedi' },
            { data: 'apmaMedi' },
            { data: 'nombMedi' },
            { data: 'fechNaciMedi' },
            { data: 'logiMedi' },
            {
                data: null,
                render: function (data, type, row) {
                    return `
                        <button class="btn btn-sm btn-primary btn-editar" data-id="${row.codiMedi}">Editar</button>
                        <button class="btn btn-sm btn-danger btn-eliminar" data-id="${row.codiMedi}">Eliminar</button>
                    `;
                }
            }
        ]
    });

    // Guardar (Crear/Actualizar)
    $('#btnGuardar').click(function () {
        const medio = {
            codiMedi: $('#codiMedi').val() || null,
            ndniMedi: $('#ndniMedi').val(),
            appaMedi: $('#appaMedi').val(),
            apmaMedi: $('#apmaMedi').val(),
            nombMedi: $('#nombMedi').val(),
            fechNaciMedi: $('#fechNaciMedi').val(),
            logiMedi: $('#logiMedi').val(),
            passMedi: $('#passMedi').val()
        };

        const metodo = medio.codiMedi ? 'PUT' : 'POST';
        const url = medio.codiMedi ? 'medioscrud' : 'medioscrud';

        fetch(url, {
            method: metodo,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(medio)
        })
        .then(res => {
            if (!res.ok) {
                return res.json().then(err => { throw err; });
            }
            return res.json();
        })
        .then(data => {
            $('#modalMedio').modal('hide');
            tabla.ajax.reload();
            mostrarAlerta('success', medio.codiMedi ? 'Médico actualizado correctamente' : 'Médico creado correctamente');
        })
        .catch(error => {
            mostrarAlerta('danger', error.error || 'Error al guardar el médico');
        });
    });

    // Editar
    $('#example tbody').on('click', '.btn-editar', function () {
        const data = tabla.row($(this).parents('tr')).data();
        $('#codiMedi').val(data.codiMedi);
        $('#ndniMedi').val(data.ndniMedi);
        $('#appaMedi').val(data.appaMedi);
        $('#apmaMedi').val(data.apmaMedi);
        $('#nombMedi').val(data.nombMedi);
        $('#fechNaciMedi').val(data.fechNaciMedi);
        $('#logiMedi').val(data.logiMedi);
        $('#passMedi').val(''); // Por seguridad, no mostramos la contraseña actual
        $('#modalMedioLabel').text('Editar Médico');
        $('#modalMedio').modal('show');
    });

    // Eliminar - Mostrar confirmación
    $('#example tbody').on('click', '.btn-eliminar', function () {
        const id = $(this).data('id');
        $('#idMedicoEliminar').val(id);
        $('#modalConfirmarEliminar').modal('show');
    });

    // Confirmar eliminación
    $('#btnConfirmarEliminar').click(function () {
        const id = $('#idMedicoEliminar').val();
        
        fetch('medioscrud?codiMedi=' + id, {
            method: 'DELETE'
        })
        .then(res => {
            if (!res.ok) {
                return res.json().then(err => { throw err; });
            }
            return res.json();
        })
        .then(data => {
            $('#modalConfirmarEliminar').modal('hide');
            tabla.ajax.reload();
            mostrarAlerta('success', 'Médico eliminado correctamente');
        })
        .catch(error => {
            mostrarAlerta('danger', error.error || 'Error al eliminar el médico');
        });
    });

    // Nuevo médico
    $('#btnNuevo').click(function () {
        $('#formMedio')[0].reset();
        $('#codiMedi').val('');
        $('#modalMedioLabel').text('Nuevo Médico');
        $('#modalMedio').modal('show');
    });

    // Función para mostrar alertas
    function mostrarAlerta(tipo, mensaje) {
        const alerta = $(`
            <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
                ${mensaje}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        `);
        
        $('#alertContainer').html(alerta);
        setTimeout(() => alerta.alert('close'), 5000);
    }
});