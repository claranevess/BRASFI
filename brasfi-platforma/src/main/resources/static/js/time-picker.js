

// Função para preencher colunas de hora e minuto
function fillTimeColumns() {
    const hourCol = document.getElementById('hour-col');
    const minuteCol = document.getElementById('minute-col');

    for (let i = 0; i <= 23; i++) {
        const item = document.createElement('div');
        item.className = 'item';
        item.innerText = `${i} h`; // Exibe como "1 hr", "2 hr", etc.
        item.dataset.value = i; // Valor real (útil se precisar enviar ao backend)
        hourCol.appendChild(item);
    }

    for (let i = 0; i <= 55; i += 5) {
        const item = document.createElement('div');
        item.className = 'item';
        item.innerText = `${i} min`; // Exibe como "5 min", "10 min", etc.
        item.dataset.value = i; // Valor real
        minuteCol.appendChild(item);
    }

    ['hour-col', 'minute-col'].forEach(id => {
        const col = document.getElementById(id);
        const spacerTop = document.createElement('div');
        spacerTop.className = 'spacer';
        const spacerBottom = document.createElement('div');
        spacerBottom.className = 'spacer';
        col.insertBefore(spacerTop, col.firstChild);
        col.appendChild(spacerBottom);
    });
}

function getSelectedTime() {
    const hourCol = document.getElementById('hour-col');
    const minuteCol = document.getElementById('minute-col');

    const hourIndex = Math.round(hourCol.scrollTop / 50);
    const minuteIndex = Math.round(minuteCol.scrollTop / 50);

    const hourEl = hourCol.children[hourIndex + 1]; // +1 por causa do spacer
    const minuteEl = minuteCol.children[minuteIndex + 1];

    const hour = String(hourEl?.dataset?.value ?? 0).padStart(2, '0');
    const minute = String(minuteEl?.dataset?.value ?? 0).padStart(2, '0');

    return `${hour}:${minute}`;

}


function updateActiveItem(col, index) {
    const items = col.querySelectorAll('.item');
    items.forEach((item, i) => {
        if (i === index) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });
}



// Mostra/esconde o seletor
document.getElementById('togglePicker').addEventListener('click', function (e) {
    e.stopPropagation();
    document.getElementById('timepicker').classList.toggle('hidden');
});

// Fecha ao clicar fora
document.addEventListener('click', function () {
    const picker = document.getElementById('timepicker');
    if (!picker.classList.contains('hidden')) {
        picker.classList.add('hidden');
    }
});

// Impede propagação ao clicar dentro
document.getElementById('timepicker').addEventListener('click', function (e) {
    e.stopPropagation();
});

// Atualiza o valor ao parar de rolar
['hour-col', 'minute-col'].forEach(id => {
    document.getElementById(id).addEventListener('scroll', () => {
        clearTimeout(window._scrollTimeout);
        window._scrollTimeout = setTimeout(() => {
            const selectedTime = getSelectedTime();
            document.getElementById('buttonText').innerText = selectedTime;
            document.getElementById('duracaoInput').value = selectedTime;

            // marca item central como ativo
            const hourCol = document.getElementById('hour-col');
            const minuteCol = document.getElementById('minute-col');
            const hourIndex = Math.round(hourCol.scrollTop / 50);
            const minuteIndex = Math.round(minuteCol.scrollTop / 50);

            updateActiveItem(hourCol, hourIndex);
            updateActiveItem(minuteCol, minuteIndex);
        }, 100);
    });
});


// Inicialização
fillTimeColumns();
