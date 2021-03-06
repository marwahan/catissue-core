import { createApp } from 'vue'
import PrimeVue from 'primevue/config';

// import 'bootstrap/dist/css/bootstrap.min.css';
// import 'primevue/resources/themes/saga-blue/theme.css';
import 'primevue/resources/themes/bootstrap4-light-blue/theme.css';
import 'primevue/resources/primevue.css';
import 'primeflex/primeflex.css';
import 'primeicons/primeicons.css';

import { library } from '@fortawesome/fontawesome-svg-core'
import { fas } from '@fortawesome/free-solid-svg-icons'

import ToastService from 'primevue/toastservice';

import router from './router'
import ui from './global.js';
import App from './App.vue'
import http from '@/common/services/HttpClient.js';
import alerts from '@/common/services/Alerts.js';

import showIfAllowed from '@/common/directives/ShowIfAllowed.js';

const app = createApp(App)
  .use(router)
  .use(PrimeVue)
  .use(ToastService);

app.directive('show-if-allowed', showIfAllowed);

alerts.toastSvc = app.config.globalProperties.$toast;
library.add(fas);

window.parent.postMessage({op: 'getGlobalProps', requestor: 'vueapp'}, '*');
window.parent.postMessage({op: 'getAuthToken', requestor: 'vueapp'}, '*');
window.parent.postMessage({op: 'getUserDetails', requestor: 'vueapp'}, '*');
window.parent.postMessage({op: 'getAppMenuItems', requestor: 'vueapp'}, '*');
let count = 3;
window.addEventListener('message', function(event) {
  if (event.data.op == 'getGlobalProps') {
    ui.os = event.data.resp.os || {};

    let server = ui.os.server || {};
    http.protocol = server.secure ? 'https' : 'http';
    http.host = server.hostname;
    http.port = server.port;
    http.path = server.app || '..';
    if (http.path) {
      http.path += '/';
    }

    http.path += 'rest/ng'

    --count;
  } else if (event.data.op == 'getAuthToken') {
    let resp = event.data.resp;
    ui.token = resp.token;

    http.headers['X-OS-API-TOKEN'] = ui.token; // localStorage.getItem('osAuthToken');
    if (resp.impUserToken) {
      http.headers['X-OS-IMPERSONATE-USER'] = resp.impUserToken;
    } else {
      delete http.headers['X-OS-IMPERSONATE-USER'];
    }

    --count;
  } else if (event.data.op == 'getUserDetails') {
    Object.assign(ui, event.data.resp);
    --count;
  } else if (event.data.op == 'getAppMenuItems') {
    ui.menuItems = event.data.resp;
  }

  if (count == 0) {
    app.mount('#app')
    app.provide('ui', ui);
    count = -1;
  }
});
