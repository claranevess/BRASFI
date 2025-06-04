// --- Script para o modal de EDITAR TRILHA ---
let editTrilhaModal = null; // Variável para armazenar a referência ao modal

async function loadEditTrilhaModal(trilhaId) {
    if (!editTrilhaModal) { // Carrega o modal apenas uma vez
        try {
            // Requisição para carregar o conteúdo HTML do modal de edição
            const response = await fetch(`/trilhas/editar-modal/${trilhaId}`); // Endpoint a ser criado no backend
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const htmlContent = await response.text();
            document.getElementById('editTrilhaModalContainer').innerHTML = htmlContent;

            editTrilhaModal = document.getElementById('modalEditarTrilha'); // ID do modal de edição
            const closeEditTrilhaBtn = editTrilhaModal.querySelector('#closeModal');

            if (closeEditTrilhaBtn) {
                closeEditTrilhaBtn.addEventListener('click', closeEditTrilhaModal);
            }
            if (editTrilhaModal) {
                editTrilhaModal.addEventListener('click', (e) => {
                    if (e.target === editTrilhaModal) {
                        closeEditTrilhaModal();
                    }
                });
            }

            // Inicializa os pickers após o DOM do modal ser carregado
            initializeEixoPicker();
            initializeTimePicker();

        } catch (error) {
            console.error("Erro ao carregar o modal de Edição de Trilha:", error);
        }
    }
}

function setSelectedEixo(valorEnum) {
    const eixoCol = document.getElementById('eixo-col');
    const eixo = Object.keys(eixoMapping).find(k => eixoMapping[k] === valorEnum);

    if (!eixo) return;

    const index = eixosTematicos.indexOf(eixo);
    eixoCol.scrollTop = index * 50;

    document.getElementById('buttonTextEixo').innerText = eixo;
    document.getElementById('eixoInput').value = valorEnum;
    updateActiveEixo(index);
}

function setSelectedTime(horas, minutos) {
    const hourCol = document.getElementById('hour-col');
    const minuteCol = document.getElementById('minute-col');

    hourCol.scrollTop = horas * 50;
    minuteCol.scrollTop = (minutos / 5) * 50;

    const horaStr = String(horas).padStart(2, '0');
    const minutoStr = String(minutos).padStart(2, '0');
    const tempo = `${horaStr}:${minutoStr}`;

    document.getElementById('buttonText').innerText = tempo;
    document.getElementById('duracaoInput').value = tempo;

    updateActiveItem(hourCol, horas);
    updateActiveItem(minuteCol, minutos / 5);
}

// Preencher Eixo Temático
setSelectedEixo(atividade.eixo);

// Preencher Duração
const [horas, minutos] = atividade.duracao.split(':').map(Number);
setSelectedTime(horas, minutos);

function openEditTrilhaModal(element) {
    const trilhaData = {
        id: element.dataset.trilhaId,
        titulo: element.dataset.trilhaTitulo,
        descricao: element.dataset.trilhaDescricao,
        eixoTematico: element.dataset.trilhaEixo,
        duracao: element.dataset.trilhaDuracao,
        topicosDeAprendizado: element.dataset.trilhaTopicos
    };

    // Your existing logic to open and populate the modal with trilhaData
    if (!editTrilhaModal) {
        loadEditTrilhaModal(trilhaData.id).then(() => {
            if (editTrilhaModal) {
                fillEditTrilhaModal(trilhaData);
                editTrilhaModal.classList.remove('hidden');
                editTrilhaModal.classList.add('flex');
            }
        });
    } else {
        fillEditTrilhaModal(trilhaData);
        editTrilhaModal.classList.remove('hidden');
        editTrilhaModal.classList.add('flex');
        initializeEixoPicker();
        initializeTimePicker();
    }
}

function closeEditTrilhaModal() {
    if (editTrilhaModal) {
        editTrilhaModal.classList.remove('flex');
        editTrilhaModal.classList.add('hidden');
    }
}

function fillEditTrilhaModal(trilhaData) {
    // Preenche os campos do formulário com os dados da trilha
    if (document.getElementById('tituloTrilha')) {
        document.getElementById('tituloTrilha').value = trilhaData.titulo;
    }
    if (document.getElementById('descricao')) {
        document.getElementById('descricao').value = trilhaData.descricao;
    }
    if (document.getElementById('topicosDeAprendizado')) {
        document.getElementById('topicosDeAprendizado').value = trilhaData.topicosDeAprendizado;
    }

    // Preencher Eixo Temático
    if (trilhaData.eixoTematico) {
        setSelectedEixo(trilhaData.eixoTematico);
    }

    // Preencher Duração
    if (trilhaData.duracao) {
        const [horas, minutos] = trilhaData.duracao.split(':').map(Number);
        setSelectedTime(horas, minutos);
    }
    // Note: O upload da imagem de capa (dropzone-file) não pode ser preenchido
    // diretamente por segurança, exigindo que o usuário faça o upload novamente.
}
