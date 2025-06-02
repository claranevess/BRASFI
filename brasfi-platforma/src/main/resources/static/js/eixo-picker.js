// Dados do eixo (pode alterar conforme necessário)
const eixosTematicos = [
  "Finanças",
  "Empreendedorismo",
  "Liderança"
];

const eixoMapping = {
  "Finanças": "FINANCAS",
  "Empreendedorismo": "EMPREENDEDORISMO",
  "Liderança": "LIDERANCA"
};

function fillEixoColumn() {
    const eixoCol = document.getElementById('eixo-col');

    eixosTematicos.forEach((eixo) => {
        const item = document.createElement('div');
        item.className = 'eixo-item';
        item.innerText = eixo;  // nome aparece aqui
        item.dataset.value = eixo;  // para pegar depois
        eixoCol.appendChild(item);
    });

    const spacerTop = document.createElement('div');
    spacerTop.className = 'eixo-spacer';
    const spacerBottom = document.createElement('div');
    spacerBottom.className = 'eixo-spacer';
    eixoCol.insertBefore(spacerTop, eixoCol.firstChild);
    eixoCol.appendChild(spacerBottom);
}

function getSelectedEixo() {
    const eixoCol = document.getElementById('eixo-col');
    const eixoIndex = Math.round(eixoCol.scrollTop / 50);
    const eixoEl = eixoCol.children[eixoIndex + 1]; // +1 por causa do spacer
    return eixoEl?.dataset?.value ?? "";
}

function updateActiveEixo(index) {
    const items = document.querySelectorAll('#eixo-col .eixo-item');
    items.forEach((item, i) => {
        if (i === index) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });
}

document.getElementById('togglePickerEixo').addEventListener('click', function (e) {
    e.stopPropagation();
    document.getElementById('eixopicker').classList.toggle('hidden');
});

document.addEventListener('click', function () {
    const picker = document.getElementById('eixopicker');
    if (!picker.classList.contains('hidden')) {
        picker.classList.add('hidden');
    }
});

document.getElementById('eixopicker').addEventListener('click', function (e) {
    e.stopPropagation();
});

document.getElementById('eixo-col').addEventListener('scroll', () => {
    clearTimeout(window._eixoScrollTimeout);
    window._eixoScrollTimeout = setTimeout(() => {
        const selected = getSelectedEixo(); // ex: "Empreendedorismo"
        document.getElementById('buttonTextEixo').innerText = selected;

        // converte para o formato do enum
        const enumValue = eixoMapping[selected] || "";
        document.getElementById('eixoInput').value = enumValue;

        const eixoCol = document.getElementById('eixo-col');
        const eixoIndex = Math.round(eixoCol.scrollTop / 50);
        updateActiveEixo(eixoIndex);
    }, 100);
});


fillEixoColumn();
