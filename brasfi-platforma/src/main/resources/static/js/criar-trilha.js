
// Variáveis globais para o modal e o botão de abrir
const newTrilhaOpenBtn = document.getElementById('openNewTrilhaModalBtn');
let newTrilhaModal = null; // Variável para armazenar a referência ao modal

// Função para carregar o conteúdo do modal via AJAX
async function loadNewTrilhaModal() {
    if (!newTrilhaModal) { // Carrega o modal apenas uma vez
        try {
            const response = await fetch('/trilhas/nova')
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const htmlContent = await response.text();
            document.getElementById('newTrilhaModalContainer').innerHTML = htmlContent;

            newTrilhaModal = document.getElementById('modalRegistrarTrilha');
            const closeNewTrilhaBtn = newTrilhaModal.querySelector('#closeModal');

            if (closeNewTrilhaBtn) {
                closeNewTrilhaBtn.addEventListener('click', closeNewTrilhaModal);
            }
            if (newTrilhaModal) {
                newTrilhaModal.addEventListener('click', (e) => {
                    if (e.target === newTrilhaModal) {
                        closeNewTrilhaModal();
                    }
                });
            }

            // *** AS FUNÇÕES DE INICIALIZAÇÃO AGORA SÃO CHAMADAS AQUI ***
            // Isso garante que o DOM do modal foi carregado.
            // As funções initializeEixoPicker e initializeTimePicker devem estar definidas globalmente
            // ou serem carregadas junto com o modal se forem específicas do modal.
            if (typeof initializeEixoPicker === 'function') {
                 initializeEixoPicker();
            }
            if (typeof initializeTimePicker === 'function') {
                 initializeTimePicker();
            }

            // *** CHAME A FUNÇÃO DE INICIALIZAÇÃO DAS AULAS AQUI TAMBÉM ***
            initializeAulaMaterialLogic();

        } catch (error) {
            console.error("Erro ao carregar o modal de Nova Trilha:", error);
        }
    }
}

// Função para abrir o modal
function openNewTrilhaModal() {
    if (!newTrilhaModal) { // Se o modal ainda não foi carregado
        loadNewTrilhaModal().then(() => {
            if (newTrilhaModal) {
                newTrilhaModal.classList.remove('hidden');
                newTrilhaModal.classList.add('flex');
            }
        });
    } else { // Se o modal já foi carregado
        newTrilhaModal.classList.remove('hidden');
        newTrilhaModal.classList.add('flex');
        // Re-inicializa os pickers e a lógica de aulas/materiais a cada abertura
        if (typeof initializeEixoPicker === 'function') {
             initializeEixoPicker();
        }
        if (typeof initializeTimePicker === 'function') {
             initializeTimePicker();
        }
        // *** RE-CHAME A FUNÇÃO DE INICIALIZAÇÃO DAS AULAS AQUI TAMBÉM ***
        // Isso garante que os listeners e índices são re-aplicados se o modal for reaberto.
        initializeAulaMaterialLogic();
    }
}

// Função para fechar o modal
function closeNewTrilhaModal() {
    if (newTrilhaModal) {
        newTrilhaModal.classList.remove('flex');
        newTrilhaModal.classList.add('hidden');
    }
}

// Adiciona o event listener ao botão de abrir o modal de Nova Trilha
document.addEventListener('DOMContentLoaded', function() {
    if (newTrilhaOpenBtn) {
        newTrilhaOpenBtn.addEventListener('click', openNewTrilhaModal);
    }
});


// --- Funções de Lógica de Aulas e Materiais ---
function initializeAulaMaterialLogic() {
    const aulasContainer = document.getElementById('aulasContainer');
    if (!aulasContainer) {
        console.warn("Container de aulas não encontrado. A lógica de aulas/materiais não será inicializada.");
        return;
    }

    // Remove e adiciona listeners para evitar duplicação (importante ao reabrir o modal)
    const addAulaBtn = document.getElementById('addAulaBtn');
    if (addAulaBtn) {
        addAulaBtn.removeEventListener('click', addAulaHandler);
        addAulaBtn.addEventListener('click', addAulaHandler);
    }

    // Event delegation para botões dentro do container de aulas
    aulasContainer.removeEventListener('click', handleContainerClick); // Remove o handler genérico
    aulasContainer.addEventListener('click', handleContainerClick); // Adiciona o handler genérico

    function handleContainerClick(event) {
        if (event.target.closest('.remove-aula-btn')) {
            removeAulaHandler(event);
        } else if (event.target.closest('.add-material-btn')) {
            addMaterialHandler(event);
        } else if (event.target.closest('.remove-material-btn')) {
            removeMaterialHandler(event);
        }
    }

    // Função para gerar o HTML para um novo bloco de material
    function getNewMaterialBlockHtml(aulaIndex, materialIndex) {
        // IDs e names são gerados dinamicamente com os índices corretos.
        // `id`s devem ser únicos, `name`s devem seguir a convenção do Spring.
        // Note o uso direto de `aulas[${aulaIndex}].materiais[${materialIndex}].propriedade`
        return `
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 material-bloco" data-material-index="${materialIndex}">
                <div>
                    <label for="material-linkApoio-${aulaIndex}-${materialIndex}" class="block text-gray-600 text-sm font-semibold mb-1">Links Externos</label>
                    <input type="url" id="material-linkApoio-${aulaIndex}-${materialIndex}" name="aulas[${aulaIndex}].materiais[${materialIndex}].linkApoio" placeholder="https://..."
                           class="w-full p-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-1 focus:ring-blue-400">
                </div>
                <div>
                    <label for="material-documento-${aulaIndex}-${materialIndex}" class="block text-gray-600 text-sm font-semibold mb-1">Documentos</label>
                    <label class="flex items-center gap-2 text-sm text-blue-600 cursor-pointer hover:text-blue-800">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="#437312">
                            <path fill-rule="evenodd" d="M3 17a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm3.293-7.707a1 1 0 011.414 0L9 10.586V3a1 1 0 112 0v7.586l1.293-1.293a1 1 0 111.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" clip-rule="evenodd"/>
                        </svg>
                        <span>Upload de Arquivos</span>
                        <input type="file" id="material-documento-${aulaIndex}-${materialIndex}" name="aulas[${aulaIndex}].materiais[${materialIndex}].documento" class="hidden">
                    </label>
                </div>
                <button type="button" class="remove-material-btn col-span-2 text-red-500 hover:text-red-700">Remover Material</button>
            </div>
        `;
    }

    // Função para atualizar os nomes e IDs dos inputs dentro de um bloco de material
    function updateMaterialFields(aulaBlock, aulaIndex) {
        const materialBlocks = aulaBlock.querySelectorAll('.material-bloco');
        materialBlocks.forEach((materialBlock, materialIndex) => {
            materialBlock.setAttribute('data-material-index', materialIndex); // Atualiza o índice do material

            materialBlock.querySelectorAll('input, label').forEach(element => {
                let currentId = element.getAttribute('id');
                let currentFor = element.getAttribute('for');
                let currentName = element.getAttribute('name');

                // Atualiza o atributo 'name'
                if (currentName && currentName.startsWith('aulas[')) { // Garante que é um campo de aula/material
                    // Extrai a parte final do nome da propriedade (ex: "linkApoio", "documento")
                    const propertyName = currentName.substring(currentName.lastIndexOf('.') + 1);
                    element.setAttribute('name', `aulas[${aulaIndex}].materiais[${materialIndex}].${propertyName}`);
                }

                // Atualiza os atributos 'id' e 'for'
                if (currentId && (currentId.startsWith('material-linkApoio-') || currentId.startsWith('material-documento-'))) {
                    // Ex: material-linkApoio-0-0 -> material-linkApoio
                    const idPrefix = currentId.split('-').slice(0, 2).join('-'); // Pega 'material-linkApoio'
                    element.setAttribute('id', `${idPrefix}-${aulaIndex}-${materialIndex}`);
                }
                if (currentFor && (currentFor.startsWith('material-linkApoio-') || currentFor.startsWith('material-documento-'))) {
                    const forPrefix = currentFor.split('-').slice(0, 2).join('-');
                    element.setAttribute('for', `${forPrefix}-${aulaIndex}-${materialIndex}`);
                }
            });

            // Mostra/oculta o botão de remover material
            const removeMaterialBtn = materialBlock.querySelector('.remove-material-btn');
            if (removeMaterialBtn) {
                if (materialBlocks.length === 1) { // Se houver apenas um material, esconde o botão de remover
                    removeMaterialBtn.classList.add('hidden');
                } else {
                    removeMaterialBtn.classList.remove('hidden');
                }
            }
        });
    }

    // Função principal para atualizar todos os blocos de aulas
    function updateAulaBlocks() {
        const currentAulaBlocks = aulasContainer.querySelectorAll('.aula-bloco');
        currentAulaBlocks.forEach((aulaBlock, aulaIndex) => {
            const aulaNumSpan = aulaBlock.querySelector('.aula-numero');
            if (aulaNumSpan) {
                aulaNumSpan.textContent = `#${aulaIndex + 1}`;
            }
            aulaBlock.setAttribute('data-aula-index', aulaIndex); // Atualiza o índice da aula

            // Atualiza nomes e IDs dos inputs de aula
            aulaBlock.querySelectorAll('input, textarea, label, div[id^="videoPreviewContainer-"]').forEach(element => {
                let currentId = element.getAttribute('id');
                let currentFor = element.getAttribute('for');
                let currentName = element.getAttribute('name');

                if (currentName && currentName.startsWith('aulas[')) { // Garante que é um campo de aula
                    const propertyName = currentName.substring(currentName.lastIndexOf('.') + 1);
                    element.setAttribute('name', `aulas[${aulaIndex}].${propertyName}`);
                }
                if (currentId && (currentId.startsWith('linkAula-') || currentId.startsWith('descricaoAula-') || currentId.startsWith('videoPreviewContainer-'))) {
                    const idPrefix = currentId.split('-')[0]; // Pega 'linkAula' ou 'descricaoAula'
                    element.setAttribute('id', `${idPrefix}-${aulaIndex}`);
                }
                if (currentFor && (currentFor.startsWith('linkAula-') || currentFor.startsWith('descricaoAula-'))) {
                    const forPrefix = currentFor.split('-')[0];
                    element.setAttribute('for', `${forPrefix}-${aulaIndex}`);
                }
            });

            // Mostra ou esconde o botão de remover aula
            const removeAulaBtn = aulaBlock.querySelector('.remove-aula-btn');
            if (removeAulaBtn) {
                if (currentAulaBlocks.length === 1) {
                    removeAulaBtn.classList.add('hidden');
                } else {
                    removeAulaBtn.classList.remove('hidden');
                }
            }

            // Atualiza os campos de material para esta aula específica
            updateMaterialFields(aulaBlock, aulaIndex);
        });
    }

    // Handlers para os event listeners (funções nomeadas para permitir removeEventListener)
    function addAulaHandler() {
        const currentAulaCount = aulasContainer.querySelectorAll('.aula-bloco').length;
        const newAulaIndex = currentAulaCount; // O novo índice será o próximo disponível

        // Clona o primeiro bloco de aula como base
        const originalBlock = aulasContainer.querySelector('.aula-bloco');
        const newAulaBlock = originalBlock.cloneNode(true); // Clona profundamente (com filhos)

        // Limpa os valores dos inputs clonados
        newAulaBlock.querySelectorAll('input, textarea').forEach(input => {
            if (input.type !== 'file') {
                input.value = '';
            } else {
                // Para inputs de tipo file, é melhor criar um novo input por questões de segurança.
                const oldInput = input;
                const newInput = document.createElement('input');
                newInput.type = 'file';
                newInput.name = `aulas[${newAulaIndex}].materiais[0].documento`; // Nome inicial
                newInput.id = `material-documento-${newAulaIndex}-0`; // ID inicial
                newInput.classList = oldInput.classList;
                const correspondingLabel = oldInput.closest('label');
                if (correspondingLabel) {
                    correspondingLabel.replaceChild(newInput, oldInput);
                    correspondingLabel.setAttribute('for', newInput.id);
                }
            }
        });

        // Reseta a pré-visualização de vídeo
        const videoPreview = newAulaBlock.querySelector('[id^="videoPreviewContainer-"]');
        if (videoPreview) {
            videoPreview.innerHTML = '<span class="text-gray-500">Pré-visualização</span>';
        }

        // Limpa os materiais clonados e adiciona um novo material inicial
        const materiaisAulaContainer = newAulaBlock.querySelector('.materiais-aula-container');
        if (materiaisAulaContainer) {
            materiaisAulaContainer.innerHTML = ''; // Limpa todos os materiais clonados
            materiaisAulaContainer.insertAdjacentHTML('beforeend', getNewMaterialBlockHtml(newAulaIndex, 0));
        }

        aulasContainer.appendChild(newAulaBlock);
        updateAulaBlocks(); // Re-indexa todas as aulas e seus materiais após a adição
    }

    function removeAulaHandler(event) {
        const blockToRemove = event.target.closest('.aula-bloco');
        if (blockToRemove) {
            blockToRemove.remove();
            updateAulaBlocks(); // Re-indexa todas as aulas e seus materiais após a remoção
        }
    }

    function addMaterialHandler(event) {
        const aulaBlock = event.target.closest('.aula-bloco');
        const aulaIndex = parseInt(aulaBlock.getAttribute('data-aula-index'));
        const currentMaterialCount = aulaBlock.querySelectorAll('.material-bloco').length;
        aulaBlock.querySelector('.materiais-aula-container').insertAdjacentHTML('beforeend', getNewMaterialBlockHtml(aulaIndex, currentMaterialCount));
        updateMaterialFields(aulaBlock, aulaIndex); // Atualiza os nomes e IDs dos materiais da aula específica
    }

    function removeMaterialHandler(event) {
        const materialBlockToRemove = event.target.closest('.material-bloco');
        if (materialBlockToRemove) {
            const aulaBlock = materialBlockToRemove.closest('.aula-bloco');
            materialBlockToRemove.remove();
            // Passa o materialBlock original para updateMaterialFields para re-indexar
            updateMaterialFields(aulaBlock, parseInt(aulaBlock.getAttribute('data-aula-index')));
        }
    }

    // Inicializa os blocos de aulas e materiais quando a função é chamada (para o bloco inicial)
    updateAulaBlocks();
}