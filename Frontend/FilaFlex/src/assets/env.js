(function(window) {
    window.__env = window.__env || {};
    
    //API URL
    window.__env.apiUrl = 'http://localhost:8080';
    
    //Ambiente (true para produção, false para desenvolvimento)
    window.__env.production = false;
    
    window.__env.appVersion = '1.0.0';
    
  })(this);