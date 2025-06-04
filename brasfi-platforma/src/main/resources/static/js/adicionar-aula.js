let addAulaModal = null; // Variável para armazenar a referência ao modal

async function loadaddAulaModal() {
    if (!addAulaModal) { // Carrega o modal apenas uma vez
        try {
            // Requisição para carregar o conteúdo HTML do modal de edição
            const response = await fetch(`/trilhas/adicionar-aula`); // Endpoint a ser criado no backend
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const htmlContent = await response.text();
            document.getElementById('addAulaModalContainer').innerHTML = htmlContent;

            addAulaModal = document.getElementById('modalAdicionarAula'); // ID do modal de adicionar
            const closeAddAulaBtn = addAulaModal.querySelector('#closeModal');

            if (closeAddAulaBtn) {
                closeAddAulaBtn.addEventListener('click', closeaddAulaModal);
            }
            if (addAulaModal) {
                addAulaModal.addEventListener('click', (e) => {
                    if (e.target === addAulaModal) {
                        closeaddAulaModal();
                    }
                });
            }


        } catch (error) {
            console.error("Erro ao carregar o modal de Adição de Aula:", error);
        }
    }
}

function closeAddAulaModal() {
    if (addAulaModal) {
        addAulaModal.classList.add('hidden');
        resetForm();
    } else {
        console.warn("closeAddAulaModal chamado antes do modal ser carregado.");
    }
}

async function showAddAulaModal() {
    await loadaddAulaModal(); // Garante que o modal seja carregado
    if (addAulaModal) {
        addAulaModal.classList.remove('hidden');
        // Lidar com a pré-visualização de vídeo do YouTube
        document.getElementById('link').addEventListener('input', function() {
            const url = this.value;
            const videoPreviewContainer = document.getElementById('videoPreviewContainer');
            videoPreviewContainer.innerHTML = ''; // Limpar conteúdo anterior
            videoPreviewContainer.classList.remove('has-video');

            let videoId = '';
            if (url.includes('youtube.com/watch?v=')) {
                videoId = url.split('v=')[1].split('&')[0];
            } else if (url.includes('youtu.be/')) {
                videoId = url.split('youtu.be/')[1].split('?')[0];
            } else if (url.includes('youtube.com/embed/')) { // Para links já incorporados
                videoId = url.split('youtube.com/embed/')[1].split('?')[0];
            } else if (url.includes('youtube.com/live/')) { // Para transmissões ao vivo
                videoId = url.split('youtube.com/live/')[1].split('?')[0];
            }

            if (videoId) {
                const iframe = document.createElement('iframe');
                iframe.setAttribute('width', '100%');
                iframe.setAttribute('height', '100%');
                // Usando a URL de incorporação padrão do YouTube
                iframe.setAttribute('src', `https://www.youtube.com/embed/${videoId}`);
                iframe.setAttribute('frameborder', '0');
                iframe.setAttribute('allow', 'accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture');
                iframe.setAttribute('allowfullscreen', '');
                videoPreviewContainer.appendChild(iframe);
                videoPreviewContainer.classList.add('has-video');
            }
        });

        // Atualizar o visual do input de arquivo na mudança
        document.getElementById('documentos').addEventListener('change', function() {
            if (this.files && this.files.length > 0) {
                this.classList.add('file-chosen');
            } else {
                this.classList.remove('file-chosen');
            }
        });
    } else {
        console.warn("showAddAulaModal chamado antes do modal ser carregado.");
    }
}

function resetForm() {
    const form = document.querySelector('#modalRegistrarAula form');
    if (form) {
        form.reset();
        // Limpar pré-visualização de vídeo
        const videoPreviewContainer = document.getElementById('videoPreviewContainer');
        videoPreviewContainer.innerHTML = '';
        videoPreviewContainer.classList.remove('has-video');
        // Redefinir campo de link para apenas um input sem o botão de adicionar
        const linkApoioContainer = document.getElementById('linkApoioContainer');
        const initialLinkField = `
            <div class="flex">
                <input type="url" name="linkApoio" placeholder="Copie e cole aqui..."
                       class="w-full p-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-1 focus:ring-blue-400">
            </div>
        `;
        linkApoioContainer.innerHTML = initialLinkField;
        // Redefinir exibição do input de arquivo
        document.getElementById('documentos').classList.remove('file-chosen');
    }
}