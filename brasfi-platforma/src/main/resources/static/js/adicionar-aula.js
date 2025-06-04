// A função closeAddAulaModal() deve referenciar 'modalRegistrarTrilha'
function closeAddAulaModal() {
    document.getElementById('modalRegistrarTrilha').classList.add('hidden');
    resetForm();
}

// A função showAddAulaModal() deve referenciar 'modalRegistrarTrilha'
function showAddAulaModal() {
    document.getElementById('modalRegistrarTrilha').classList.remove('hidden');
}

function resetForm() {
    const form = document.querySelector('#modalRegistrarTrilha form');
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

