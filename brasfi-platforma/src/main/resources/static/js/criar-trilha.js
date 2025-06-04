const newTrilhaOpenBtn = document.getElementById('openNewTrilhaModalBtn');
    let newTrilhaModal = null; // Variável para armazenar a referência ao modal

    async function loadNewTrilhaModal() {
        if (!newTrilhaModal) { // Carrega o modal apenas uma vez
            try {
                const response = await fetch('/trilhas/criar-modal');
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
                // Isso garante que o DOM do modal foi carregado e os scripts que definem
                // initializeEixoPicker e initializeTimePicker já foram executados.
                initializeEixoPicker();
                initializeTimePicker();

            } catch (error) {
                console.error("Erro ao carregar o modal de Nova Trilha:", error);
            }
        }
    }

    function openNewTrilhaModal() {
        // A lógica de carregamento do modal deve estar no 'then'
        // para garantir que o modal foi carregado ANTES de tentar abri-lo e inicializar os pickers.
        if (!newTrilhaModal) { // Se o modal ainda não foi carregado
            loadNewTrilhaModal().then(() => {
                // Após o carregamento bem-sucedido e inicialização dos pickers
                if (newTrilhaModal) {
                    newTrilhaModal.classList.remove('hidden');
                    newTrilhaModal.classList.add('flex');
                }
            });
        } else { // Se o modal já foi carregado
            newTrilhaModal.classList.remove('hidden');
            newTrilhaModal.classList.add('flex');
            // Re-inicializa os pickers aqui também caso o modal seja aberto novamente sem recarregar a página
            // Isso pode ser útil se o estado dos pickers precisar ser redefinido a cada abertura
            initializeEixoPicker();
            initializeTimePicker();
        }
    }

    function closeNewTrilhaModal() {
        if (newTrilhaModal) {
            newTrilhaModal.classList.remove('flex');
            newTrilhaModal.classList.add('hidden');
        }
    }

    // Adiciona o event listener ao botão de abrir o modal de Nova Trilha
    if (newTrilhaOpenBtn) {
        newTrilhaOpenBtn.addEventListener('click', openNewTrilhaModal);
    }