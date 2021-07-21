
import http from '@/common/services/HttpClient.js';

class Site {

  getSites(filterOpts, pageOpts) {
    let params = Object.assign({}, filterOpts || {});
    params = Object.assign(params, pageOpts || {});
    return http.get('sites', params);
  }

}

export default new Site();
