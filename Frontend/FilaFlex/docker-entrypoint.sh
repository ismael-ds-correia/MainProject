#!/bin/sh

# Caminho para o arquivo env.js
ENV_FILE=/usr/share/nginx/html/assets/env.js

# Garantir que o diretório existe
mkdir -p /usr/share/nginx/html/assets

# Criar o conteúdo do arquivo env.js com as variáveis de ambiente
echo "(function(window) {
    window.__env = window.__env || {};
    
    //API URL
    window.__env.apiUrl = '${API_URL:-http://localhost:8080}';
    
    //Ambiente (true para produção, false para desenvolvimento)
    window.__env.production = ${PRODUCTION:-true};
    
    window.__env.appVersion = '${APP_VERSION:-1.0.0}';
    
})(this);" > $ENV_FILE

echo "Ambiente configurado:"
echo "- API URL: ${API_URL:-http://localhost:8080}"
echo "- Ambiente: ${PRODUCTION:-true}"
echo "- Versão: ${APP_VERSION:-1.0.0}"

# Iniciar o Nginx
exec nginx -g 'daemon off;'