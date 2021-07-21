
import http from '@/common/services/HttpClient.js'

class TimeZone {

  getTimeZones() {
    return http.get('time-zones');
  }
}

export default new TimeZone();
