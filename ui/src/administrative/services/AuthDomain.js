
import http from '@/common/services/HttpClient.js';

class AuthDomain {

  getDomains() {
    return http.get('auth-domains', {});
  }

}

export default new AuthDomain();
