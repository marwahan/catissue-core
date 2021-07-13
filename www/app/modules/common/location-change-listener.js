angular.module('openspecimen')
  .factory('LocationChangeListener', function($window, $injector, $translate, $timeout) {
    var isLocationChangeAllowed = true;

    function init() {
      $window.onbeforeunload = function() {
        if (!isLocationChangeAllowed) {
          return $translate.instant('common.confirm_navigation');
        }
      }
    }

    function allowChange() {
      isLocationChangeAllowed = true;
    }

    function preventChange() {
      isLocationChangeAllowed = false;
    }

    function onChange(event) {
      if (!isLocationChangeAllowed && !confirm($translate.instant('common.confirm_navigation'))) {
        event.preventDefault();
      } else {
        allowChange();
      }
    }

    function currentState() {
      var st  = $injector.get('$state');
      var stp = $injector.get('$stateParams');

      var sname = (st.current || {}).name;
      var params = '';
      angular.forEach(stp,
        function(value, key) {
          if (value) {
            params += key + '=' + value;
          }
        }
      );

      return sname + '#' + params;
    }

    function back() {
      var currentSt = currentState();
      allowChange();
      $window.history.back();

      $timeout(
        function() {
          var newSt = currentState();
          if (currentSt == newSt) {
            $window.history.back();
          }
        },
        250
      );
    }

    init();

    return {
      allowChange: allowChange,

      preventChange: preventChange,

      onChange: onChange,

      back: back
    };
  })
