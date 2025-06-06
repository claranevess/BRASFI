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
            document.getElementById('newTrilhaModalContainer').innerHTML = htmlContent; // Assumindo que você tem um container para este modal

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

            // *** AS FUNÇÕES DE INICIALIZAÇÃO SÃO CHAMADAS AQUI ***
            // Isso garante que o DOM do modal foi carregado antes de inicializar os scripts.
            if (typeof initializeEixoPicker === 'function') {
                 initializeEixoPicker();
            }
            if (typeof initializeTimePicker === 'function') {
                 initializeTimePicker();
            }

            // Inicializa a lógica de aulas/materiais e pré-visualização para as aulas existentes (e futuras)
            initializeAulaMaterialLogic(); // Esta função agora lida com a pré-visualização também

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
                resetNewTrilhaForm(); // Reseta o formulário ao abrir (se for um modal de criação)
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
        // Re-inicializa a lógica de aulas/materiais. Isso é importante para re-aplicar listeners se o conteúdo for dinâmico.
        initializeAulaMaterialLogic();
        resetNewTrilhaForm(); // Reseta o formulário ao abrir (se for um modal de criação)
    }
}

// Função para fechar o modal
function closeNewTrilhaModal() {
    if (newTrilhaModal) {
        newTrilhaModal.classList.remove('flex');
        newTrilhaModal.classList.add('hidden');
        resetNewTrilhaForm(); // Reseta o formulário ao fechar
    }
}

// Adiciona o event listener ao botão de abrir o modal de Nova Trilha
document.addEventListener('DOMContentLoaded', function() {
    if (newTrilhaOpenBtn) {
        newTrilhaOpenBtn.addEventListener('click', openNewTrilhaModal);
    }
});


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
    aulasContainer.removeEventListener('click', handleContainerClick); // Remove o handler genérico anterior
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

    // Função para gerar o HTML para um novo bloco de aula
    function getNewAulaBlockHtml(aulaIndex) {
        // Corrigido o src do iframe para a URL de incorporação padrão do YouTube
        // e o preenchimento inicial da pré-visualização.
        return `
            <div class="bg-white rounded-2xl p-6 aula-bloco" data-aula-index="${aulaIndex}">
                <h3 class="text-xl font-semibold mb-4 flex items-center gap-2">
                    <svg xmlns="[http://www.w3.org/2000/svg](http://www.w3.org/2000/svg)" class="h-5 w-5" viewBox="0 0 20 20" fill="#437312">
                        <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3zM3.31 9.397L5 10.12v4.102a8.969 8.969 0 00-1.05-.174 1 1 0 01-.89-.89 11.115 11.115 0 01.25-3.762zM9.3 16.573A9.026 9.026 0 007 14.935v-3.957l1.818.78a3 3 0 002.364 0l5.508-2.361a11.026 11.026 0 01.25 3.762 1 1 0 01-.89.89 8.968 8.968 0 00-5.35 2.524 1 1 0 01-1.4 0zM6 18a1 1 0 001-1v-2.065a8.935 8.935 0 00-2-.712V17a1 1 0 001 1z"/>
                    </svg>
                    Aula <span class="aula-numero">#${aulaIndex + 1}</span>
                    <button type="button" class="remove-aula-btn ml-auto text-red-500 hover:text-red-700 hidden">
                        <svg xmlns="[http://www.w3.org/2000/svg](http://www.w3.org/2000/svg)" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                    </button>
                </h3>

                <div class="mb-4">
                    <input placeholder="Digite o nome da aula..." type="text" name="aulas[${aulaIndex}].titulo"
                           class="w-full border-b-2 border-gray-300 p-2 focus:ring-0 outline-none font-semibold text-lg" required/>
                </div>
                <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">

                    <div class="border border-green-600 p-4 rounded-lg flex flex-col">
                        <label for="linkAula-${aulaIndex}" class="flex text-lg items-center text-gray-700 font-medium mb-2">
                            <svg xmlns="[http://www.w3.org/2000/svg](http://www.w3.org/2000/svg)" class="h-4 w-5 mr-1" viewBox="0 0 20 21" fill="none">
                                <path d="M10.0811 19.2147C15.0091 19.2147 19.004 15.2198 19.004 10.2918C19.004 5.36379 15.0091 1.36887 10.0811 1.36887C5.15312 1.36887 1.1582 5.36379 1.1582 10.2918C1.1582 15.2198 5.15312 19.2147 10.0811 19.2147Z" stroke="#437312" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M8.29688 6.72261L13.6506 10.2918L8.29688 13.8609V6.72261Z" stroke="#437312" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            Link da Aula
                        </label>
                        <input type="url" id="linkAula-${aulaIndex}" name="aulas[${aulaIndex}].link" placeholder="Cole o link do vídeo..."
                               class="w-full p-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-1 focus:ring-blue-400 mb-4" required>
                        <label class="text-gray-700 font-medium mb-2">Pré-visualização</label>
                        <div id="videoPreviewContainer-${aulaIndex}" class="w-full bg-gray-200 rounded-md flex items-center justify-center relative video-preview-placeholder" style="min-height: 180px;">
                            <span class="text-gray-500">Pré-visualização</span>
                        </div>
                    </div>

                    <div class="border border-green-600 p-4 rounded-lg flex flex-col">
                        <label for="descricaoAula-${aulaIndex}" class="flex text-lg items-center text-gray-700 font-medium mb-2">
                            <svg xmlns="[http://www.w3.org/2000/svg](http://www.w3.org/2000/svg)" class="h-4 w-4 mr-2 text-gray-600" viewBox="0 0 19 15" fill="none">
                                <path d="M14.0885 13.0421H1.13379" stroke="#437312" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M17.7899 9.34073H1.13379" stroke="#437312" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M14.0885 5.6394H1.13379" stroke="#437312" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M17.7899 1.93802H1.13379" stroke="#437312" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            Descrição da Aula
                        </label>
                        <textarea id="descricaoAula-${aulaIndex}" name="aulas[${aulaIndex}].descricao" rows="5" placeholder="Descreva o conteúdo da aula..."
                                  class="w-full p-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-1 focus:ring-blue-400 flex-grow"></textarea>
                    </div>
                </div>

                <div class="mt-6 border border-green-600 p-4 rounded-lg">
                    <div class="text-gray-700 text-lg font-medium mb-4 flex justify-between items-center">
                        <div class="flex items-baseline mb-1">
                            <svg xmlns="[http://www.w3.org/2000/svg](http://www.w3.org/2000/svg)" class="h-4 w-5 mr-2" viewBox="0 0 20 21" fill="none">
                                <path d="M18.1688 10.023L10.2942 17.8976C9.32947 18.8623 8.02105 19.4042 6.65676 19.4042C5.29247 19.4042 3.98406 18.8623 3.01936 17.8976C2.05466 16.9329 1.5127 15.6245 1.5127 14.2602C1.5127 12.8959 2.05466 11.5875 3.01936 10.6228L10.894 2.74815C11.5371 2.10502 12.4094 1.74371 13.3189 1.74371C14.2284 1.74371 15.1007 2.10502 15.7438 2.74815C16.387 3.39129 16.7483 4.26356 16.7483 5.17309C16.7483 6.08262 16.387 6.95489 15.7438 7.59803L7.86066 15.4726C7.53909 15.7942 7.10296 15.9749 6.64819 15.9749C6.19343 15.9749 5.75729 15.7942 5.43572 15.4726C5.11416 15.1511 4.9335 14.7149 4.9335 14.2602C4.9335 13.8054 5.11416 13.3693 5.43572 13.0477L12.7105 5.78147" stroke="#437312" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            <span>Materiais de Apoio</span>
                        </div>
                        <button type="button" class="add-material-btn px-3 py-1 bg-blue-500 text-white rounded-md font-semibold hover:bg-blue-600 text-sm">
                            + Material
                        </button>
                    </div>
                    <p class="text-sm text-gray-500 font-normal">Anexe materiais complementares para esta aula</p>

                    <div class="materiais-aula-container space-y-4 mt-4">
                        ${getNewMaterialBlockHtml(aulaIndex, 0)}
                    </div>
                </div>
            </div>
        `;
    }

    // Função para gerar o HTML para um novo bloco de material
    function getNewMaterialBlockHtml(aulaIndex, materialIndex) {
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
                        <svg xmlns="[http://www.w3.org/2000/svg](http://www.w3.org/2000/svg)" class="h-4 w-4" viewBox="0 0 20 20" fill="#437312">
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

            // *** AQUI É ONDE CHAMAMOS A FUNÇÃO DE PRÉ-VISUALIZAÇÃO PARA CADA AULA ***
            const linkInput = aulaBlock.querySelector(`#linkAula-${aulaIndex}`);
            const videoPreviewContainer = aulaBlock.querySelector(`#videoPreviewContainer-${aulaIndex}`);
            setupVideoPreviewForElement(linkInput, videoPreviewContainer);
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
                // Substitua o input de arquivo antigo por um novo
                const oldInput = input;
                const newInput = document.createElement('input');
                newInput.type = 'file';
                // O name e id serão atualizados pelo updateMaterialFields
                newInput.classList = oldInput.classList;
                const correspondingLabel = oldInput.closest('label'); // Pega o label pai
                if (correspondingLabel) {
                    correspondingLabel.replaceChild(newInput, oldInput); // Substitui o input antigo
                    // O 'for' será atualizado em updateMaterialFields
                }
            }
        });

        // Reseta a pré-visualização de vídeo para o estado inicial
        const videoPreview = newAulaBlock.querySelector('[id^="videoPreviewContainer-"]');
        if (videoPreview) {
            videoPreview.innerHTML = '<span class="text-gray-500">Pré-visualização</span>';
            videoPreview.classList.remove('has-video');
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

    // *** Nova função para configurar a pré-visualização de vídeo para um elemento específico ***
    function setupVideoPreviewForElement(linkInput, videoPreviewContainer) {
        if (!linkInput || !videoPreviewContainer) return;

        // Remover qualquer listener anterior para evitar duplicação (especialmente importante para aulas clonadas)
        // Usamos uma propriedade personalizada no elemento para armazenar a referência do listener
        if (linkInput.videoListenerRef) {
            linkInput.removeEventListener('input', linkInput.videoListenerRef);
        }

        const newListener = function() {
            const url = this.value;
            videoPreviewContainer.innerHTML = '<span class="text-gray-500">Pré-visualização</span>';
            videoPreviewContainer.classList.remove('has-video');

            let videoId = '';
            // Expressão regular mais robusta para extrair o ID do vídeo do YouTube
            const youtubeRegex = /(?:https?:\/\/)?(?:www\.)?(?:m\.)?(?:youtube\.com|youtu\.be)\/(?:watch\?v=|embed\/|v\/|)([\w-]{11})(?:\S+)?/g;
            const match = youtubeRegex.exec(url);

            if (match && match[1]) {
                videoId = match[1];
            }

            if (videoId) {
                videoPreviewContainer.innerHTML = '';
                const iframe = document.createElement('iframe');
                iframe.setAttribute('width', '100%');
                iframe.setAttribute('height', '100%');
                // URL de incorporação padrão do YouTube
                iframe.setAttribute('src', `https://www.youtube.com/embed/${videoId}`); // Corrigido aqui
                iframe.setAttribute('frameborder', '0');
                iframe.setAttribute('allow', 'accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture');
                iframe.setAttribute('allowfullscreen', '');
                videoPreviewContainer.appendChild(iframe);
                videoPreviewContainer.classList.add('has-video');
            }
        };

        linkInput.addEventListener('input', newListener);
        linkInput.videoListenerRef = newListener; // Armazena a referência para poder remover depois
    }

    // Inicializa os blocos de aulas e materiais quando a função é chamada (para o bloco inicial e subsequentes)
    updateAulaBlocks();
}

// Função para resetar o formulário da nova trilha e suas aulas/materiais
function resetNewTrilhaForm() {
    const form = document.getElementById('novaTrilhaForm');
    if (form) {
        form.reset();

        // Limpar a imagem de capa (se houver lógica para isso)
        const dropzoneFile = document.getElementById('dropzone-file');
        if (dropzoneFile) {
            dropzoneFile.value = ''; // Limpa o input de arquivo
            // Se houver uma pré-visualização da imagem de capa, você precisaria limpá-la também
        }

        // Resetar o Eixo Temático e Duração para o estado inicial
        const buttonTextEixo = document.getElementById('buttonTextEixo');
        if (buttonTextEixo) buttonTextEixo.textContent = 'Selecionar...';
        const eixoInput = document.getElementById('eixoInput');
        if (eixoInput) eixoInput.value = '';

        const buttonTextTime = document.getElementById('buttonText');
        if (buttonTextTime) buttonTextTime.textContent = 'Selecionar...';
        const duracaoInput = document.getElementById('duracaoInput');
        if (duracaoInput) duracaoInput.value = '';

        // Remover todas as aulas adicionadas dinamicamente, exceto a primeira
        const aulasContainer = document.getElementById('aulasContainer');
        if (aulasContainer) {
            let aulaBlocks = aulasContainer.querySelectorAll('.aula-bloco');
            // Remove todas as aulas exceto a primeira
            for (let i = aulaBlocks.length - 1; i > 0; i--) {
                aulaBlocks[i].remove();
            }

            // Reseta a primeira aula
            const firstAulaBlock = aulasContainer.querySelector('.aula-bloco');
            if (firstAulaBlock) {
                // Limpa campos da primeira aula
                firstAulaBlock.querySelector('input[name="aulas[0].titulo"]').value = '';
                firstAulaBlock.querySelector('input[name="aulas[0].link"]').value = '';
                firstAulaBlock.querySelector('textarea[name="aulas[0].descricao"]').value = '';

                // Limpa a pré-visualização de vídeo da primeira aula
                const videoPreviewContainer = firstAulaBlock.querySelector('#videoPreviewContainer-0');
                if (videoPreviewContainer) {
                    videoPreviewContainer.innerHTML = '<span class="text-gray-500">Pré-visualização</span>';
                    videoPreviewContainer.classList.remove('has-video');
                }

                // Remove todos os materiais de apoio da primeira aula, exceto o primeiro
                const materiaisContainer = firstAulaBlock.querySelector('.materiais-aula-container');
                if (materiaisContainer) {
                    let materialBlocks = materiaisContainer.querySelectorAll('.material-bloco');
                    for (let i = materialBlocks.length - 1; i > 0; i--) {
                        materialBlocks[i].remove();
                    }
                    // Limpa os campos do primeiro material
                    const firstMaterialBlock = materiaisContainer.querySelector('.material-bloco');
                    if (firstMaterialBlock) {
                        firstMaterialBlock.querySelector('input[name="aulas[0].materiais[0].linkApoio"]').value = '';
                        // Para o input de tipo "file", recriamos para limpar completamente
                        const fileInput = firstMaterialBlock.querySelector('input[name="aulas[0].materiais[0].documento"]');
                        if (fileInput) {
                            const oldInput = fileInput;
                            const newInput = document.createElement('input');
                            newInput.type = 'file';
                            newInput.name = oldInput.name;
                            newInput.id = oldInput.id;
                            newInput.classList = oldInput.classList;
                            const correspondingLabel = oldInput.closest('label');
                            if (correspondingLabel) {
                                correspondingLabel.replaceChild(newInput, oldInput);
                            }
                        }
                    }
                }
            }
        }
        // Garante que os índices e botões de remoção estejam corretos após o reset
        initializeAulaMaterialLogic(); // Re-inicializa a lógica após o reset
    }
}