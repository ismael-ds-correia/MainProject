//Importa módulos necessários
const fs = require('fs');
const path = require('path');

//Leitura das variáveis de ambiente
//Se uma variável não estiver definida, usa o valor padrão
const apiUrl = process.env.API_URL || 'https://filaflex-backend.onrender.com';
const production = process.env.PRODUCTION === 'true';
const appVersion = process.env.APP_VERSION || '1.0.0';

//Log para verificar os valores lidos
console.log('Configurando ambiente:');
console.log(`- API URL: ${apiUrl}`);
console.log(`- Ambiente: ${production ? 'Produção' : 'Desenvolvimento'}`);
console.log(`- Versão: ${appVersion}`);

//Define o caminho para o arquivo env.js na pasta de distribuição
const targetPath = path.resolve(__dirname, './dist/fila-flex/assets/env.js');
console.log(`Arquivo será gerado em: ${targetPath}`);

//Garante que o diretório existe
const targetDir = path.dirname(targetPath);
if (!fs.existsSync(targetDir)) {
  console.log(`Criando diretório: ${targetDir}`);
  fs.mkdirSync(targetDir, { recursive: true });
}

//Cria o conteúdo do arquivo env.js
const envContent = `(function(window) {
    window.__env = window.__env || {};
    
    //API URL
    window.__env.apiUrl = '${apiUrl}';
    
    //Ambiente (true para produção, false para desenvolvimento)
    window.__env.production = ${production};
    
    window.__env.appVersion = '${appVersion}';
    
  })(this);
`;

console.log('Conteúdo do arquivo env.js gerado com sucesso');

//Escreve o conteúdo no arquivo
try {
    fs.writeFileSync(targetPath, envContent);
    console.log(`Arquivo env.js gerado com sucesso: ${targetPath}`);
  } catch (error) {
    console.error(`Erro ao escrever o arquivo: ${error}`);
    process.exit(1);
  }
  
  console.log('Script concluído com sucesso!');