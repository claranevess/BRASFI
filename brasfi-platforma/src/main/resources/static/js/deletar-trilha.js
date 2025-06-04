// --- Script para o modal de DELETAR TRILHA ---
let deleteTrilhaModal = null; // Variável para armazenar a referência ao modal de deletar

// Function to dynamically load the delete modal HTML
async function loadDeleteTrilhaModal(trilhaId) {
    if (!deleteTrilhaModal) { // Load the modal HTML only once
        try {
            // Request to load the HTML content of the delete modal
            // We use the trilhaId here to ensure the backend can prepare specific data if needed,
            // though for simple deletion, the ID is primarily used for the POST action.
            const response = await fetch(`/trilhas/deletar-modal/${trilhaId}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const htmlContent = await response.text();
            document.getElementById('deleteTrilhaModalContainer').innerHTML = htmlContent;

            deleteTrilhaModal = document.getElementById('modalDeletar'); // ID of the delete modal
            const closeDeleteModalBtn = deleteTrilhaModal.querySelector('.custom-btn-outline'); // Assuming "Cancelar" button

            if (closeDeleteModalBtn) {
                closeDeleteModalBtn.addEventListener('click', closeDeleteTrilhaModal);
            }
            if (deleteTrilhaModal) {
                // Add event listener to close modal when clicking outside content
                deleteTrilhaModal.addEventListener('click', (e) => {
                    if (e.target === deleteTrilhaModal) {
                        closeDeleteTrilhaModal();
                    }
                });
            }

        } catch (error) {
            console.error("Erro ao carregar o modal de Deletar Trilha:", error);
        }
    }
}

// Function to open and populate the delete confirmation modal
// This will be called from your button's onclick
function abrirDeletarModal(trilhaId, trilhaTitulo) {
    // If the modal hasn't been loaded yet, load it first
    if (!deleteTrilhaModal) {
        loadDeleteTrilhaModal(trilhaId).then(() => {
            // After loading, populate and show the modal
            if (deleteTrilhaModal) {
                fillDeleteTrilhaModal(trilhaId, trilhaTitulo);
                deleteTrilhaModal.classList.remove('hidden');
            }
        });
    } else {
        // If modal is already loaded, just populate and show it
        fillDeleteTrilhaModal(trilhaId, trilhaTitulo);
        deleteTrilhaModal.classList.remove('hidden');
    }
}

// Function to populate the modal fields with the specific trilha data
function fillDeleteTrilhaModal(trilhaId, trilhaTitulo) {
    const trilhaIdInput = deleteTrilhaModal.querySelector('#trilhaIdInput');
    const trilhaTituloModal = deleteTrilhaModal.querySelector('#trilhaTituloModal');

    if (trilhaIdInput) {
        trilhaIdInput.value = trilhaId;
    }
    if (trilhaTituloModal) {
        trilhaTituloModal.textContent = trilhaTitulo; // Display the trail title
    }
}

// Function to close the delete confirmation modal
function closeDeleteTrilhaModal() {
    if (deleteTrilhaModal) {
        deleteTrilhaModal.classList.add('hidden'); // Hide the modal overlay
    }
}
