async function sendCode() {
        const email = document.getElementById('emailInput').value;
        const messageElement = document.getElementById('message');
        const errorMessageElement = document.getElementById('error-message');

        messageElement.textContent = '';
        errorMessageElement.textContent = '';
        document.getElementById('validationMessage').textContent = '';
        document.getElementById('validationErrorMessage').textContent = '';

        if (!email) {
            errorMessageElement.textContent = 'Por favor, digite um email válido.';
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/api/verification/send-code?email=' + encodeURIComponent(email), {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const data = await response.text();

           if (response.ok) {
               messageElement.textContent = data;
           } else {
               errorMessageElement.textContent = 'Erro: ' + data;
           }
       } catch (error) {
           console.error('Erro ao enviar a requisição de código:', error);
           errorMessageElement.textContent = 'Ocorreu um erro de rede ou de conexão. Verifique o console para mais detalhes.';
       }
   }
